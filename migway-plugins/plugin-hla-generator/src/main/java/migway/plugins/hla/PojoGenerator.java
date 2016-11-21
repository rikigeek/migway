package migway.plugins.hla;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoGenerator implements IGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PojoGenerator.class);
    private String namespace;
    private Map<String, String> basicTypesMapping;
    private JavaNormalizer normalizer = new JavaNormalizer();
    private PrintWriter print;
    private boolean begin;
    private String pojoLocation;

    /**
     * Create a generator
     * 
     * @param namespace
     *            namespace to use for generated Java class
     * @param fomPath
     *            FOM file path
     * @throws Exception
     */
    public PojoGenerator(String namespace, String pojoLocation) {
        this.pojoLocation = pojoLocation;
        this.namespace = namespace;
        basicTypesMapping = new HashMap<String, String>();
        preloadNativeDataTypes();
    }

    /**
     * load all Java Native data types, and link them with related FOM type
     */
    private void preloadNativeDataTypes() {
        basicTypesMapping.put("HLAinteger16BE", "short");
        basicTypesMapping.put("HLAinteger16LE", "short");
        basicTypesMapping.put("HLAinteger32BE", "int");
        basicTypesMapping.put("HLAinteger32LE", "int");
        basicTypesMapping.put("HLAinteger64BE", "long");
        basicTypesMapping.put("HLAinteger64LE", "long");
        basicTypesMapping.put("HLAfloat32BE", "float");
        basicTypesMapping.put("HLAfloat32LE", "float");
        basicTypesMapping.put("HLAfloat64BE", "double");
        basicTypesMapping.put("HLAfloat64LE", "double");
        basicTypesMapping.put("HLAoctetPairBE", "short");
        basicTypesMapping.put("HLAoctetPairLE", "short");
        basicTypesMapping.put("HLAoctet", "byte");
        basicTypesMapping.put("HLAboolean", "boolean");
        basicTypesMapping.put("HLAASCIIstring", "string");
        basicTypesMapping.put("HLAunicodeString", "string");
        basicTypesMapping.put("HLAASCIIchar", "char");
        basicTypesMapping.put("HLAunicodeChar", "char");
        basicTypesMapping.put("HLAbyte", "byte");
    }

    /**
     * Convert data type to its Java representation
     * 
     * @param dataType
     * @return
     */
    private String convertDataType(String dataType) {
        String dt = dataType;
        if (basicTypesMapping.containsKey(dt))
            dt = basicTypesMapping.get(dt);
        return dt;
    }

    @Override
    public void addNativeType(String basicType, int bitSize) {
        String type = "";
        String name = normalizer.normalizeClass(basicType);
        if (bitSize <= 8) {
            type = "byte";
        } else if (bitSize <= 16) {
            type = "short";
        } else if (bitSize <= 32) {
            type = "int";
        } else if (bitSize <= 64) {
            type = "long";
        } else {
            LOG.warn("%s Basic datatype is too big (%d bits). Cannot be converted to an integer value...", name, bitSize);
            return;
        }
        basicTypesMapping.put(name, type);

        LOG.debug(String.format("Load BasicDataRepresentation: %s (%d bits) as %s", name, bitSize, type));

    }

    private PrintWriter openFile(String className) throws Exception {
        String javaBinaryName = namespace + "." + className;
        Path output = Paths.get(pojoLocation, javaBinaryName.replace(".", "/").concat(".java"));
        Path parent = output.getParent();
        if (!parent.toFile().exists()) {
            LOG.debug("mkdirs " + parent.toString());
            parent.toFile().mkdirs();
        }

        // File output = new File(javaBinaryName.replace(".",
        // "/").concat(".java"));
        // output = Paths.get("pojos", output.toString()).toFile();
        PrintWriter print = null;

        try {
            print = new PrintWriter(output.toFile());
            LOG.debug("Opened " + output.toString());
        } catch (FileNotFoundException e) {
            LOG.error("Unable to write to " + output.toString(), e);
            throw new Exception("Unable to write to " + output.toString(), e);
        }
        return print;

    }

    /**
     * Write header of the Java file (package name, and eventually some comments
     * and import
     * 
     * @param print
     *            a opened PrintWriter, link to the output file
     */
    private void writeHeader(PrintWriter print) {
        print.printf("package %s;\n", namespace);
        print.println();
    }

    @Override
    public void openEnum(String name, String type, String description) {
        String className = normalizer.normalizeClass(name);
        try {
            print = openFile(className);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        writeHeader(print);

        if (description != null)
            print.printf("// %s\n", description);
        print.printf("public enum %s {\n", className);
        begin = true;
    }

    @Override
    public void addEnumValue(String enumName, String name, String value, String description) {
        String normName = normalizer.normalizeEnum(name);
        if (!begin) {
            print.println(",");
        } else {
            begin = false;
        }
        if (description != null)
            print.printf("// %s\n", description);
        print.printf("  %s", normName);

    }

    @Override
    public void closeEnum(String name) {
        print.println("}");

        print.close();
    }

    @Override
    public void openClass(String name, String parentClassName, String description) {
        String className = normalizer.normalizeClass(name);
        String parent = parentClassName;

        // Get extended class
        String extension = "";
        if (parent != null)
            extension = String.format(" extends %s", normalizer.normalizeClass(parent));

        try {
            print = openFile(className);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        writeHeader(print);

        if (description != null)
            print.printf("// %s\n", description);
        print.printf("public class %s%s {\n", className, extension);
    }

    @Override
    public void addField(String className, String name, String type, String description) {
        String attName = normalizer.normalizeField(name);
        String arrayMarkup = "";
        String attType;
        int idxmarkup = type.indexOf("[]");
        if (idxmarkup >= 0) {
            arrayMarkup = type.substring(idxmarkup, type.length());
            attType = type.substring(0, idxmarkup);
        } else {
            attType = type;
        }
        attType = normalizer.normalizeClass(convertDataType(attType)).concat(arrayMarkup);
        if (description != null)
            print.printf("// %s\n", description);
        print.printf("  public %s %s;\n", attType, attName);
    }

    @Override
    public void closeClass(String className) {
        print.println("}");
        print.close();
    }

    @Override
    public void openUnion(String unionName, String discName, String discType, String description) {
        openClass(unionName, null, description);
        addField(unionName, discName, discType, "Discriminant");
    }
    
    @Override
    public void addAlternative(String unionName, String name, String type, String discValue, String description ) {
        addField(unionName, name, type, description);
    }
    
    @Override
    public void closeUnion(String unionName) {
        closeClass(unionName);
    }
    
    public void openVariantGetMethod() {
        print.printf("  public Object get() {\n");
    }

    public void addVariantGetAlternative(String dname, String dtype, String aenum, String aname) {
        String altName = normalizer.normalizeField(aname);
        // FIXME normalizer.normalizeEnum is not always the good thing to
        // do. If
        // discriminant is not an enum (a string for instance), value could
        // then be wrong.
        String altEnum = normalizer.normalizeEnum(aenum);
        String discName = normalizer.normalizeField(dname);
        String discType = normalizer.normalizeClass(dtype);
        print.printf("    if (%s.equals(%s.%s)) { return %s; }\n", discName, discType, altEnum, altName);

    }

    public void closeVariantGetMethod() {
        print.printf("    return null;\n");
        print.printf("  }\n");

    }

    @Override
    public void writeHeader() {
        LOG.info("Starting generation of POJO class into '" + pojoLocation + "' folder");
    }

    @Override
    public void writeFooter() {
        LOG.info("Generation of POJO class is done");
    }

}
