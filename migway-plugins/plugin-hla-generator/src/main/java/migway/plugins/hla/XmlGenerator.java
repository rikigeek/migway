package migway.plugins.hla;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migway.core.config.ClassStructure;
import migway.core.config.ConfigFactory;
import migway.core.config.ConfigHelper;
import migway.core.config.Element;
import migway.core.config.ElementType;
import migway.core.config.EnumStructure;
import migway.core.config.UnionStructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlGenerator implements IGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(XmlGenerator.class);
    ConfigHelper conf;
    ConfigFactory factory;
    ClassStructure cs;
    UnionStructure us;
    EnumStructure es;

    private final String[] INTERFACE_LIST = new String[] { "HLA" };
    private String namespace;
    private JavaNormalizer normalizer;
    private Map<String, String> basicTypesMapping;
    private List<String> basicTypeEnum;
    private String configFilePath;

    public XmlGenerator(String namespace, String configFilePath) {
        String ns = (namespace == null ? "" : namespace);
        if (ns.isEmpty())
            this.namespace = ns;
        else {
            if (ns.endsWith("."))
                this.namespace = ns;
            else
                this.namespace = ns.concat(".");

        }

        this.configFilePath = configFilePath;
        conf = new ConfigHelper();
        factory = new ConfigFactory();
        normalizer = new JavaNormalizer();

        preloadNativeDataTypes();

    }

    /**
     * load all Java Native data types, and link them with related FOM type
     */
    private void preloadNativeDataTypes() {
        basicTypesMapping = new HashMap<String, String>();
        basicTypesMapping.put("HLAinteger16BE", "SHORT");
        basicTypesMapping.put("HLAinteger16LE", "SHORT");
        basicTypesMapping.put("HLAinteger32BE", "INT");
        basicTypesMapping.put("HLAinteger32LE", "INT");
        basicTypesMapping.put("HLAinteger64BE", "LONG");
        basicTypesMapping.put("HLAinteger64LE", "LONG");
        basicTypesMapping.put("HLAfloat32BE", "FLOAT");
        basicTypesMapping.put("HLAfloat32LE", "FLOAT");
        basicTypesMapping.put("HLAfloat64BE", "DOUBLE");
        basicTypesMapping.put("HLAfloat64LE", "DOUBLE");
        basicTypesMapping.put("HLAoctetPairBE", "SHORT");
        basicTypesMapping.put("HLAoctetPairLE", "SHORT");
        basicTypesMapping.put("HLAoctet", "BYTE");
        basicTypesMapping.put("HLAboolean", "BOOLEAN");
        basicTypesMapping.put("HLAASCIIstring", "STRING");
        basicTypesMapping.put("HLAunicodeString", "STRING");
        basicTypesMapping.put("HLAASCIIchar", "CHAR");
        basicTypesMapping.put("HLAunicodeChar", "CHAR");
        basicTypesMapping.put("HLAbyte", "BYTE");
        basicTypeEnum = new ArrayList<String>();
        for (String c : "BOOLEAN BYTE CHAR DOUBLE FLOAT INT LONG SHORT STRING".split(" ")) {
            basicTypeEnum.add(c);
        }
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
    public void writeHeader() {
        LOG.info("Starting generation of Migway configuration file: '" + configFilePath + "'");
        Path path = Paths.get(configFilePath);
        File parent = path.getParent().toFile();
        
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                LOG.error("Unable to create folders: " + parent.toString());
            }
        }
    }

    @Override
    public void openClass(String name, String parent, String description) {
        String className = normalizer.normalizeClass(name);
        cs = factory.createClass(namespace.concat(className), INTERFACE_LIST, new String[] { name });
        conf.addStructure(cs);
    }

    @Override
    public void addField(String className, String name, String type, String description) {
        String attName = normalizer.normalizeField(name);
        // Array identification
        String arrayMarkup = "";
        String attType;
        int idxmarkup = type.indexOf("[]");
        if (idxmarkup >= 0) {
            arrayMarkup = type.substring(idxmarkup, type.length());
            attType = type.substring(0, idxmarkup);
        } else {
            attType = type;
        }
        attType = normalizer.normalizeClass(convertDataType(attType));

        ElementType elementType = getElementType(attType, arrayMarkup);
        Element element = factory.createElement(attName, elementType);
        cs.addElement(element);

    }

    private ElementType getElementType(String attType, String arrayMarkup) {
        if (!arrayMarkup.isEmpty()) {
            // Array
            arrayMarkup = arrayMarkup.substring(2);
            ElementType arrayElementType = getElementType(attType, arrayMarkup);
            ElementType arrayType = factory.createArrayType(arrayElementType, 0);
            return arrayType;
        } else if (basicTypeEnum.contains(attType)) {
            // Basic
            ElementType basic = factory.getBasicType(attType);
            return basic;
        } else {
            // Reference
            ElementType ref = factory.getReferenceType(attType);
            return ref;
        }
    }

    @Override
    public void closeClass(String name) {
        // NOP
    }

    @Override
    public void openEnum(String name, String type, String description) {
        String className = normalizer.normalizeClass(name);
        // *** Type different than INT is not possible (Java limit) ***
        // String attType;
        // // Array identification
        // String arrayMarkup = "";
        // int idxmarkup = type.indexOf("[]");
        // if (idxmarkup >= 0) {
        // arrayMarkup = type.substring(idxmarkup, type.length());
        // attType = type.substring(0, idxmarkup);
        // } else {
        // attType = type;
        // }
        //
        // attType = normalizer.normalizeClass(convertDataType(attType));

        es = factory.createEnum(namespace.concat(className), INTERFACE_LIST, new String[] { name });
        conf.addStructure(es);
    }

    @Override
    public void addEnumValue(String enumName, String enumValue, String enumId, String description) {
        String normValue = normalizer.normalizeEnum(enumValue);
        es.addEnumValue(normValue);
    }

    @Override
    public void closeEnum(String name) {
        // NOP
    }

    @Override
    public void openUnion(String name, String discName, String type, String description) {
        String className = normalizer.normalizeClass(name);
        String dName = normalizer.normalizeField(discName);

        String attType;
        // Array identification
        String arrayMarkup = "";
        int idxmarkup = type.indexOf("[]");
        if (idxmarkup >= 0) {
            arrayMarkup = type.substring(idxmarkup, type.length());
            attType = type.substring(0, idxmarkup);
        } else {
            attType = type;
        }

        attType = normalizer.normalizeClass(convertDataType(attType));

        us = factory.createUnion(namespace.concat(className), INTERFACE_LIST, new String[] { name });
        conf.addStructure(us);

        Element element = factory.createElement(dName, getElementType(attType, arrayMarkup));
        us.addElement(element);
        us.setDiscrimant(dName);
    }

    @Override
    public void addAlternative(String unionName, String name, String type, String discValue, String description) {
        String attName = normalizer.normalizeField(name);
        // Array identification
        String arrayMarkup = "";
        String attType;
        int idxmarkup = type.indexOf("[]");
        if (idxmarkup >= 0) {
            arrayMarkup = type.substring(idxmarkup, type.length());
            attType = type.substring(0, idxmarkup);
        } else {
            attType = type;
        }
        attType = normalizer.normalizeClass(convertDataType(attType));

        ElementType elementType = getElementType(attType, arrayMarkup);
        Element element = factory.createElement(attName, elementType);
        us.addElement(element);
    }

    @Override
    public void closeUnion(String name) {
        // NOP
    }

    @Override
    public void writeFooter() {
        LOG.info("Writing configuration file: '" + configFilePath + "'");
        conf.saveConfig(new File(configFilePath));
        LOG.info("Configuration file generation done");
    }

    @Override
    public void addNativeType(String basicType, int bitSize) {
        String type = "";
        String name = normalizer.normalizeClass(basicType);
        if (bitSize <= 8) {
            type = "BYTE";
        } else if (bitSize <= 16) {
            type = "SHORT";
        } else if (bitSize <= 32) {
            type = "INT";
        } else if (bitSize <= 64) {
            type = "LONG";
        } else {
            LOG.warn("%s Basic datatype is too big (%d bits). Cannot be converted to an integer value...", name, bitSize);
            return;
        }
        basicTypesMapping.put(name, type);

        LOG.debug(String.format("Load BasicDataRepresentation: %s (%d bits) as %s", name, bitSize, type));

    }
}
