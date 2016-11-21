package migway.plugins.hla;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ieee.standards.ieee1516_2010.ArrayDataTypesType.ArrayData;
import org.ieee.standards.ieee1516_2010.Attribute;
import org.ieee.standards.ieee1516_2010.BasicDataRepresentationsType.BasicData;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType.Enumerator;
import org.ieee.standards.ieee1516_2010.EnumeratedDataTypesType.EnumeratedData;
import org.ieee.standards.ieee1516_2010.FixedRecordDataType.Field;
import org.ieee.standards.ieee1516_2010.FixedRecordDataTypesType.FixedRecordData;
import org.ieee.standards.ieee1516_2010.InteractionClass;
import org.ieee.standards.ieee1516_2010.ObjectClass;
import org.ieee.standards.ieee1516_2010.ObjectModelType;
import org.ieee.standards.ieee1516_2010.Parameter;
import org.ieee.standards.ieee1516_2010.SimpleDataTypesType.SimpleData;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType.Alternative;
import org.ieee.standards.ieee1516_2010.VariantRecordDataTypesType.VariantRecordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HlaGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(HlaGenerator.class);
    private ObjectModelType omt;
    private Map<String, String> basicTypesMapping;
    private Map<String, String> simpleTypesMapping;
    private Map<String, String> arrayTypesMapping;
    private PojoGenerator pojoGenerator;
    private XmlGenerator confGenerator;
    
    public HlaGenerator(GeneratorConfiguration config) {
        this(config.getNamespace(), config.getFomFile(), config.getOutputConfigFile(), config.getOutputPojoLocation());
    }

    public HlaGenerator(String namespace, String fomPath, String configFile, String pojoLocation)  {
        parseFom(fomPath);

        basicTypesMapping = new HashMap<String, String>();
        simpleTypesMapping = new HashMap<String, String>();
        arrayTypesMapping = new HashMap<String, String>();

        pojoGenerator = new PojoGenerator(namespace, pojoLocation);
        confGenerator = new XmlGenerator(namespace, configFile);

    }

    private void parseFom(String fomPath) {
        File fom = new File(fomPath);
        LOG.debug("Loading {}", fomPath);
        try {
            LOG.error("ERROR");
            FomParser parser = new FomParser();
            omt = parser.parse(fom);
        } catch (Exception e) {
            LOG.error("Unable to parse " + fomPath, e);
            omt = null;
        }
    }

    /**
     * Get parsed FOM
     * 
     * @return
     */
    public ObjectModelType getOmt() {
        return omt;
    }

    /**
     * Convert data type to its simplest representation
     * 
     * @param dataType
     * @return
     */
    private String convertDataType(String dataType) {
        String dt = dataType;
        if (arrayTypesMapping.containsKey(dt)) {
            dt = arrayTypesMapping.get(dt);
            return dt; // No way of convert an array
        }
        if (simpleTypesMapping.containsKey(dt))
            dt = simpleTypesMapping.get(dt);
        if (basicTypesMapping.containsKey(dt))
            dt = basicTypesMapping.get(dt);
        return dt;
    }

    private void loadBasicDatatype(BasicData bd) {
        String name = bd.getName().getValue();
        BigInteger size = bd.getSize().getValue();
        int iSize = 0;
        try {
            iSize = size.intValueExact();
        } catch (ArithmeticException e) {
            LOG.warn(
                    String.format("%s Basic datatype is too big (%s bits). Cannot be converted to an integer value...", name,
                            size.toString()), e);
            return;
        }
        pojoGenerator.addNativeType(name, iSize);
        confGenerator.addNativeType(name, iSize);
    }

    public void generateAll() {

        if (omt == null) {
            LOG.error("No parsed FOM. Cancel generation");
            return;
        }

        confGenerator.writeHeader();
        pojoGenerator.writeHeader();

        LOG.info("Load basic data types");
        for (BasicData bd : omt.getDataTypes().getBasicDataRepresentations().getBasicData()) {
            loadBasicDatatype(bd);
        }

        LOG.info("Load all simple data types");
        for (SimpleData sd : omt.getDataTypes().getSimpleDataTypes().getSimpleData()) {
            simpleTypesMapping.put(sd.getName().getValue(), sd.getRepresentation().getValue());
        }

        LOG.info("Load all array data types");
        for (ArrayData ad : omt.getDataTypes().getArrayDataTypes().getArrayData()) {
            String value = convertDataType(ad.getDataType().getValue());
            value = value + "[]";
            arrayTypesMapping.put(ad.getName().getValue(), value);
        }

        LOG.info("Generate all enumerated Data types");
        for (EnumeratedData en : omt.getDataTypes().getEnumeratedDataTypes().getEnumeratedData()) {
            generateEnum(en);
            LOG.debug(en.getName().getValue() + " enum generated");
        }

        LOG.info("Generate all fixed record data types");
        for (FixedRecordData rd : omt.getDataTypes().getFixedRecordDataTypes().getFixedRecordData()) {
            generateClass(rd);
            LOG.debug(rd.getName().getValue() + " fixed record generated");
        }

        LOG.info("Generate all variant record data types");
        for (VariantRecordData vd : omt.getDataTypes().getVariantRecordDataTypes().getVariantRecordData()) {
            generateClass(vd);
            LOG.debug(vd.getName().getValue() + " variant record generated");
        }

        LOG.info("Generate all object class");
        ObjectClass rootoc = omt.getObjects().getObjectClass();
        // Recursive method to generate one object class and all its child
        generateClass(rootoc, null);
        LOG.debug(rootoc.getName().getValue() + " object class generated");

        LOG.info("Generate all interactions");
        // Recursive method to generate one interaction class and all its child
        InteractionClass rootin = omt.getInteractions().getInteractionClass();
        generateClass(rootin, null);
        LOG.debug(rootin.getName().getValue() + " interaction generated");

        confGenerator.writeFooter();
        pojoGenerator.writeFooter();
    }

    /**
     * Generate a class .java file for a HLA FixedRecord data type
     * 
     * @param rd
     * @throws Exception
     */
    private void generateClass(FixedRecordData rd) {
        String className = rd.getName().getValue();
        try {
            pojoGenerator.openClass(className, null, "Fixed record");
            confGenerator.openClass(className, className, "Fixed record");
            for (Field att : rd.getField()) {
                String attName = att.getName().getValue();
                String attType = convertDataType(att.getDataType().getValue());
                pojoGenerator.addField(className, attName, attType, null);
                confGenerator.addField(className, attName, attType, null);
            }
        } catch (Exception e) {
            LOG.error("Fatal while creating Fixed record" + className, e);

        } finally {
            pojoGenerator.closeClass(className);
            confGenerator.closeClass(className);
        }
    }

    /**
     * Generate a class .java file for a HLA variantRecord
     * 
     * @param vd
     * @throws Exception
     */
    private void generateClass(VariantRecordData vd) {
        String className = vd.getName().getValue();
        try {

            // CreateDiscriminant
            String discName = vd.getDiscriminant().getValue();
            String discType = convertDataType(vd.getDataType().getValue());
            pojoGenerator.openUnion(className, discName, discType, "Variant Record");
            confGenerator.openUnion(className, discName, discType, "Variant Record");

            // each alternative is a field
            for (Alternative alt : vd.getAlternative()) {
                String altName = alt.getName().getValue();
                String altType = convertDataType(alt.getDataType().getValue());
                pojoGenerator.addAlternative(className, altName, altType, alt.getEnumerator().getValue(), "Alternative "
                        + alt.getEnumerator().getValue());
                confGenerator.addAlternative(className, altName, altType, alt.getEnumerator().getValue(), "Alternative "
                        + alt.getEnumerator().getValue());
            }
            // Generic method to get value of the object, based on the
            // discriminant
            // value
            pojoGenerator.openVariantGetMethod();
            for (Alternative alt : vd.getAlternative()) {
                String altName = alt.getName().getValue();
                // FIXME normalizer.normalizeEnum is not always the good thing
                // to
                // do. If
                // discriminant is not an enum (a string for instance), value
                // could
                // then be wrong.
                String altEnum = alt.getEnumerator().getValue();
                pojoGenerator.addVariantGetAlternative(discName, discType, altEnum, altName);
            }
            pojoGenerator.closeVariantGetMethod();
        } catch (Exception e) {
            LOG.error("Fatal while creating Variant record " + className, e);
        } finally {
            pojoGenerator.closeUnion(className);
            confGenerator.closeUnion(className);
        }
    }

    /**
     * Generate a class .java file for HLA Object class
     * 
     * @param oc
     * @throws Exception
     */
    private void generateClass(ObjectClass oc, String parent) {
        String className = oc.getName().getValue();
        try {
            pojoGenerator.openClass(className, parent, "Object class");
            confGenerator.openClass(className, parent, "Object class");
            for (Attribute att : oc.getAttribute()) {
                String attName = att.getName().getValue();
                String attType = convertDataType(att.getDataType().getValue());
                pojoGenerator.addField(className, attName, attType, null);
                confGenerator.addField(className, attName, attType, null);
            }
        } catch (Exception e) {
            LOG.error("Fatal while creating Object Class " + className, e);

        } finally {
            pojoGenerator.closeClass(className);
            confGenerator.closeClass(className);
        }

        // Recursive generation of child object class
        for (ObjectClass ic : oc.getObjectClass()) {
            generateClass(ic, className);
            LOG.debug(ic.getName().getValue() + " object class generated");
        }
    }

    /**
     * Generate a class file
     * 
     * @param in
     * @param parent
     * @throws Exception
     */
    private void generateClass(InteractionClass in, String parent) {
        String className = in.getName().getValue();
        try {
            pojoGenerator.openClass(className, parent, "interaction");
            confGenerator.openClass(className, parent, "interaction");

            for (Parameter att : in.getParameter()) {
                String attName = att.getName().getValue();
                String attType = convertDataType(att.getDataType().getValue());
                pojoGenerator.addField(className, attName, attType, null);
                confGenerator.addField(className, attName, attType, null);
            }
        } catch (Exception e) {
            LOG.error("Fatal while creating Interaction " + className, e);
        } finally {
            pojoGenerator.closeClass(className);
            confGenerator.closeClass(className);
        }

        // Recursive generation of child object class
        for (InteractionClass ic : in.getInteractionClass()) {
            generateClass(ic, className);
            LOG.debug(ic.getName().getValue() + " interaction generated");
        }
    }

    /**
     * Generate a class .java file for a HLA enumerated data type
     * 
     * @param ed
     * @throws Exception
     */
    private void generateEnum(EnumeratedData ed) {
        String className = ed.getName().getValue();
        String enumType = convertDataType(ed.getRepresentation().getValue());
        try {
            pojoGenerator.openEnum(className, enumType, "Enum type");
            confGenerator.openEnum(className, enumType, "Enum type");

            for (Enumerator e : ed.getEnumerator()) {
                String value = e.getName().getValue();
                String id = Arrays.toString(e.getValue().toArray());
                pojoGenerator.addEnumValue(className, value, id, null);
                confGenerator.addEnumValue(className, value, id, null);
            }
        } catch (Exception e) {
            LOG.error("Fatal while creating " + className, e);
        } finally {
            pojoGenerator.closeEnum(className);
            confGenerator.closeEnum(className);
        }
    }
}
