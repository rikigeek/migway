package migway.plugins.hla;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.RTIinternalError;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migway.core.config.ArrayType;
import migway.core.config.BasicType;
import migway.core.config.ClassStructure;
import migway.core.config.ConfigHelper;
import migway.core.config.Element;
import migway.core.config.ElementType;
import migway.core.config.EnumStructure;
import migway.core.config.Structure;
import migway.core.config.StructureRef;
import migway.core.config.UnionStructure;
import migway.core.helper.PojoLoaderHelper;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.Processor;
import org.ieee.standards.ieee1516_2010.AttributeType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType;
import org.ieee.standards.ieee1516_2010.FixedRecordDataType;
import org.ieee.standards.ieee1516_2010.InteractionClassType;
import org.ieee.standards.ieee1516_2010.ObjectClassType;
import org.ieee.standards.ieee1516_2010.ParameterType;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType.Alternative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Class used as Interface between Camel Component and Migway core
 * route.
 * To create an Interface, implement this Abstract class.
 * Note that DefaultInterface is an implementation of this abstract class. You
 * can extends DefaultInterface.
 * 
 * This interface uses an internal buffer (that must be implemented).
 * 
 * This interface uses an internal structure definition of the managed object.
 * The POJO structure {@link Structure} contains a sorted list of the elements
 * that must be written to the internal buffer.
 * This structure seems to be redundant with the Class<?> of the POJO. It is, so
 * that any POJO can be used. Even if the POJO implementation fields appear in a
 * different order than it must appear in the buffer. Moreover, buffer can
 * contains not all the fields value.
 * And finally, it can extract value from method (TODO not implemented yet)
 * 
 * 
 * @author Sébastien Tissier
 *
 */
public abstract class HlaBaseInterface implements Processor {
    protected static Logger LOG = LoggerFactory.getLogger(HlaBaseInterface.class);

    /**
     * The header in the message that is used to set the Object class (a java
     * binary name)
     */
    private String headerClassName;

    protected String getHeaderClassName() {
        return headerClassName;
    }

    protected Map<String, Object> loadedPojo = new HashMap<String, Object>();

    /**
     * The name of the interface type. Used mainly in Mapper with class specific
     * to a component
     */
    private String interfaceTypeName;

    protected String getInterfaceTypeName() {
        return interfaceTypeName;
    }

    /** The way to access the configuration */
    protected ConfigHelper configHelper;

    /** The way to access to Reflection mechanisms */
    protected PojoLoaderHelper helper = PojoLoaderHelper.INSTANCE;

    /** FOM parser = HLA Model parser */
    private FomParser parser;

    /** Encoding and decoding information from HLA objects */
    private HlaDataReader hladatareader;

    private File fomFile = null;
    public final static String DEFAULT_FOM_FILE = "GVA_RPR2-D20_2010.xml";

    /** Default constructor with the default configuration file */
    public HlaBaseInterface() throws RTIinternalError {
        this(new ConfigHelper(), new File(DEFAULT_FOM_FILE));
    }

    /**
     * Constructor that use a specific configuration
     * 
     * @param configHelper
     *            the configuration to use
     * @throws RTIinternalError
     *             if HLA RTI had troubles at initialization
     */
    public HlaBaseInterface(ConfigHelper configHelper, File fom)  {
        this.configHelper = configHelper;
        this.headerClassName = setHeaderClassName();
        this.interfaceTypeName = setInterfaceTypeName();

        if (fom == null)
            this.fomFile = new File(DEFAULT_FOM_FILE);
        else
            this.fomFile = fom;

        try {
            this.parser = new FomParser();
            this.parser.parse(this.fomFile);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LOG.info("FOM " + this.fomFile.getName() + " parsed");

        try {
            this.hladatareader = new HlaDataReader(parser);
        } catch (RTIinternalError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new InternalError("Error while loading FOM file", e);
        }
    }

    /**
     * Core of the Interface. Based on a Camel Processor.
     * This method is called when a new Message arrive on the Camel route.
     * 
     * First message is extracted and analyzed: is it a POJO or a Buffer?
     * Then, according to this answer, it respectively convert the POJO to a
     * buffer object (encoding or writing), or convert the Buffer object to a
     * POJO (decoding or reading).
     * This conversion follows rules stored in a {@link Structure} (object that
     * is created in a called method).
     * To find the correct Structure, association is made between the Structure
     * name, the POJO java class binary name, and an information stored in the
     * Camel Message (the header of the Message contains a field
     * {@link #getHeaderClassName()} with this information)
     * 
     * Finally, the converted object is stored in the Camel Message. Camel then
     * forward this Message to next Processor in the route definition.
     */
    @Override
    final public void process(Exchange exchange) throws Exception {
        // Check if exchange is ok
        LOG.trace("Validating Exchange");
        if (!validate(exchange)) {
            throw new InvalidPayloadException(exchange, null);
        }
        // Input message must be set
        Message in = exchange.getIn();
        LOG.trace("Checking if Input message is null");
        if (in == null) {
            // Should never happen
            throw new InvalidPayloadException(exchange, exchange.getClass());
        }

        // Body of the message must be a ByteBuffer, or a supported POJO
        LOG.trace("Validating body content");
        Object content = in.getBody();
        if (in.getBody() == null) {
            throw new InvalidPayloadException(exchange, Object.class);
        }
        // body is not null
        LOG.debug("Body of input message type:" + content.getClass().getName());
        // Try to convert it (using Camel TypeConverters) to the expected Buffer
        // class
        content = in.getBody(expectedBodyClass());
        // content is null if conversion failed
        if (content != null && isABuffer(content)) {
            // It's a Buffer. Header must exist
            Object h = in.getHeader(getHeaderClassName());
            if (h == null) {
                throw new NoSuchHeaderException(exchange, getHeaderClassName(), null);
            }
            // Convert header into class name (basically, h is a string
            // containing the className, but it can be different with
            // specific
            // Component)
            String pojoClass = getClassName(h);

            try {
                // Convert to the POJO and store it in the message
                Object pojo = readPojo(content, pojoClass);
                in.setBody(pojo);
            } catch (BufferUnderflowException e) {
                // Structure is not correct
                Message msg = exchange.getOut();
                msg.setFault(true);
                msg.setBody("Structure Error");
                LOG.error("BufferUnderflowException {} while decoding {} object", e.toString(), pojoClass);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unable to read header " + getHeaderClassName(), e);
            }
        } else {
            // Not a ByteBuffer. Must be a POJO
            // Get the not converted body
            content = in.getBody();

            Class<?> pojoClass = content.getClass();
            if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(pojoClass.getName())) {
                throw new InvalidPayloadException(exchange, pojoClass);
            }
            // Save the POJO in the output message
            in.setBody(writePojo(content));
            in.setHeader(getHeaderClassName(), pojoClass.getName());
        }
    }

    protected String getClassName(Object headerValue) {
        // Header TopicClass must return a String
        if (!(headerValue instanceof String)) {
            throw new IllegalArgumentException("header " + getHeaderClassName() + ": value is not a String ("
                    + headerValue.getClass().getName() + ")");
        }
        String pojoClass = (String) headerValue;
        return pojoClass;
    }

    /***********************************************************************
     * 
     * 
     * DECODING PART
     * 
     * 
     ***********************************************************************/

    /**
     * Read content of the HLA bus and store all values into a POJO.
     * 
     * The POJO is returned at the end of the process
     * 
     * To be able to read information and to unmarshal them into a POJO, the
     * system manipulates the following information:
     * <ul>
     * <li>The structure of the POJO (whose objects' name start with
     * <code>migXXX</code>). This come from the configuration file
     * <li>The structure of the HLA data (whose objects' name start with
     * <code>fomXXX</code>). This come from the FOM (Federation Object Model)
     * file
     * <li>The POJO (whose objects' name start with <code>pojoXXX</code>).
     * <li>The HLA object structure that represents the data on the bus (whose
     * objects' name start with <code>hlaXXX</code>)
     * </ul>
     * 
     * @param inputBuffer
     *            the data buffer that comes from HLA bus
     * @param pojoClassName
     *            the java binary class name of the POJO that will receive
     *            values
     * @return The instantiated POJO, with correct values
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws InconsistentFDD
     * @throws DecoderException
     */
    protected Object readPojo(Object inputBuffer, String remoteClassName) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, InconsistentFDD, DecoderException {

        // Convert HlaClassName into Java binary name
        String pojoClassName = configHelper.getPojoRemoteName(remoteClassName, getInterfaceTypeName());
        
        // First, check if this POJO is supported by the configuration
        if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(pojoClassName))
            throw new UnsupportedOperationException("Atm " + this.getClass().getName() + " doesn't support " + pojoClassName);

        // Check if buffer is valid, and initialize it before we can read from
        // it
        if (!initializeInputBuffer(inputBuffer)) {
            throw new IllegalArgumentException(String.format("Invalid input body"));
        }

        // Create the Object class / interaction class
        Object pojo = helper.loadPojo(pojoClassName);

        // Brutal cast, but it has been checked in initializeInputBuffer method
        @SuppressWarnings("unchecked")
        Map<String, byte[]> inputData = (Map<String, byte[]>) inputBuffer;
        // MIG structure
        Structure migStructure = configHelper.getPojoStructure(pojoClassName);
        // FOM structure
        // First try to find the object class
        ObjectClassType fomObjectType = parser.findObjectClass(migStructure.getRemoteName(getInterfaceTypeName()));
        // Try to find the InteractionClass
        InteractionClassType fomInteractionType = parser.findInteractionClass(migStructure.getRemoteName(getInterfaceTypeName()));

        // Decode each element (attribute) of the expected class
        for (int i = 0; i < migStructure.getElementsSize(); i++) {
            // The config of POJO element
            Element migElement = migStructure.getElement(i);
            // Reflection field informations
            String migFieldName = migElement.name();
            String migElementName = migElement.getRemoteName(getInterfaceTypeName());

            // **** FIX THIS **** Decode element, and not a structure!
            // TODO brutal cast. Deal with cast conversion error (don't do
            // anything but error reporting when element type is not a structure
            // ref)
            // String pojoElementClassName = ((StructureRef)
            // migElement.elementType()).getStructureName();
            if (inputData.containsKey(migElementName)) {
                // Find the FOM configuration for this element
                String fomDataTypeName = null;
                // Find the attribute in the FOM, either in ObjectClassType,
                // either in InteractionClassType
                if (fomObjectType != null) {
                    for (AttributeType cur : fomObjectType.getAttribute()) {
                        if (cur.getName().getValue().equals(migElementName)) {
                            // found it as an attribute of an object class
                            fomDataTypeName = cur.getDataType().getValue();
                            // fomDataTypeName = null;
                        }
                    }
                }
                if (fomInteractionType != null) {
                    for (ParameterType cur : fomInteractionType.getParameter()) {
                        if (cur.getName().getValue().equals(migElementName)) {
                            // found it as a parameter of an interaction
                            fomDataTypeName = cur.getDataType().getValue();
                        }
                    }
                }
                // Only if attribute has been found in Object Class (in FOM)
                if (fomDataTypeName != null) {
                    // The buffer to decode
                    byte[] attributeBuffer = inputData.get(migElementName);
                    // build the HLA object
                    DataElement hlaDataElement = parser.buildHlaDataElement(fomDataTypeName);
                    // decode the buffer object
                    hlaDataElement.decode(attributeBuffer);
                    // converte the HLA object into a POJO
                    // Object pojoAttribute =
                    // decodeStructure(pojoElementClassName, hlaDataElement,
                    // fomDataTypeName);
                    // And now, assign this POJO (that is an attribute of the
                    // final object) to the Object Class POJO
                    Field pojoAttributeField = helper.getField(pojoClassName, migFieldName);
                    // pojoAttributeField.set(pojo, pojoAttribute);
                    decodeElement(pojo, pojoAttributeField, migElement, hlaDataElement, fomDataTypeName);
                }
            }
        }
        // POJO can be empty if no attribute is send or if none of them are
        // configured in Migway
        return pojo;
    }

    /**
     * Decode HLA object and store values in a new POJO.
     * POJO is created and returned by this method
     * 
     * POJO are stored into loadedPojo HashMap
     * 
     * @param pojoClassName
     * @return The instantiated POJO with all its filled fields
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected Object decodeStructure(String pojoClassName, DataElement hlaObjectData, String fomDataTypeName)
            throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Get the POJO Structure
        Structure migStructure = configHelper.getPojoStructure(pojoClassName);
        // The object that will be returned
        Object pojo = null;
        // Three different cases: ClassStructure, UnionStructure (ignored at the
        // moment), and EnumStructure
        if (migStructure instanceof EnumStructure) {
            // LOGGER.error("Enum cannot be read yet. Structure " +
            // structureName + " can't be written to object " + pojo);
            // TODO call readEnumType in a correct way
            Class<?> enumType = helper.getPojoClass(pojoClassName);
            // pojo = readEnumType(enumType, (EnumStructure) migStructure,
            // hlaObjectData, fomDataType);

            // pojo = helper.loadPojo(pojoClassName);
            // Value returned as a string (contains name of the enum element)
            Object hlaValue = hladatareader.getValue(hlaObjectData, fomDataTypeName);
            // The method to get list of enum values
            Method m = enumType.getMethod("values");
            java.lang.Enum<?>[] list = (Enum<?>[]) m.invoke(null);
            // Find the enum entry that is referenced by hlaValue
            for (int i = 0; i < list.length; i++) {
                if (list[i].toString().equals(hlaValue.toString())) {
                    return list[i];
                }
            }
            LOG.error("Enum value doesn't exist " + hlaValue.toString() + " (" + migStructure.getName() + ")");

            return null;

        }
        // This must be done before Class, before UnionStructure is also a
        // ClassStructure !
        else if (migStructure instanceof UnionStructure) {
            /** Cast all structure and object **/
            // Union, must be considered as a variant. Get the discriminant
            UnionStructure migUnionStructure = (UnionStructure) migStructure;
            VariantRecordDataType fomVariantDataType = (VariantRecordDataType) parser.getDataTypes(fomDataTypeName);
            @SuppressWarnings("unchecked")
            HLAvariantRecord<DataElement> hlaVariantObject = (HLAvariantRecord<DataElement>) hlaObjectData;

            // instantiate the POJO
            pojo = helper.loadPojo(pojoClassName);
            // Name of the field that contains discriminant
            Element migDiscriminantElement = migUnionStructure.getDiscrimant();
            String pojoDiscriminantFieldName = migUnionStructure.getDiscrimant().name();
            Field pojoDiscriminantField = helper.getField(pojoClassName, pojoDiscriminantFieldName);

            DataElement hlaDiscriminantObject = hlaVariantObject.getDiscriminant();
            String fomDiscriminantDataTypeName = fomVariantDataType.getDataType().getValue();

            // Read the discriminant value and set it into POJO
            decodeElement(pojo, pojoDiscriminantField, migDiscriminantElement, hlaDiscriminantObject, fomDiscriminantDataTypeName);
            // Get the value of the discriminant from hla, and convert it as a
            // string.
            // We need a string, because alternative selection depends on string
            // value
            String hlaDiscriminantValue = hladatareader.getValue(hlaDiscriminantObject, fomDiscriminantDataTypeName).toString();
            // pojoDiscriminantField.set(pojo, hlaDiscriminantValue);

            // Obtain the field name to use for this alternative in POJO
            String migSelectedAlternativeName = migUnionStructure.getAlternative(hlaDiscriminantValue);
            Element migSelectedAlternativeElement = migUnionStructure.getElement(migSelectedAlternativeName);
            Field pojoSelectedAlternativeField = helper.getField(pojoClassName, migSelectedAlternativeName);

            // Find the selected alternative in FOM
            List<Alternative> fomAlternativeList = fomVariantDataType.getAlternative();
            Alternative fomSelectedAlternativeElement = null;
            for (Alternative fomAlternative : fomAlternativeList) {
                if (fomAlternative.getEnumerator().getValue().equals(hlaDiscriminantValue)) {
                    fomSelectedAlternativeElement = fomAlternative;
                }
            }
            String fomAlternativeName = fomSelectedAlternativeElement.getDataType().getValue();

            // Get the alternative value in HLA object
            DataElement hlaAlternativeValue = hlaVariantObject.getValue();

            decodeElement(pojo, pojoSelectedAlternativeField, migSelectedAlternativeElement, hlaAlternativeValue, fomAlternativeName);

            return pojo; // We don't want to execute the ClassStructure case
        } else if (migStructure instanceof ClassStructure) {
            // Instantiate the POJO
            pojo = helper.loadPojo(pojoClassName);
            FixedRecordDataType fomDataType = (FixedRecordDataType) parser.getDataTypes(fomDataTypeName);

            // Do the decoding of the class stored in field of the POJO
            // Iterate the structure
            int structureSize = migStructure.getElementsSize();
            for (int i = 0; i < structureSize; i++) {
                Element element = migStructure.getElement(i);
                if (element.isField()) {
                    Field elementField = helper.getField(pojoClassName, element.name());
                    // FIXME find the fomPartDataType by the remoteName of the
                    // MIG element, and not by its index
                    DataElement hlaPartObjectData = ((HLAfixedRecord) hlaObjectData).get(i);
                    String fomPartDataTypeName = fomDataType.getField().get(i).getDataType().getValue();
                    // String fomPartDataTypeName =
                    // Fill each element of this POJO
                    decodeElement(pojo, elementField, element, hlaPartObjectData, fomPartDataTypeName);
                } else {
                    throw new UnsupportedOperationException("PojoElement only support Fields atm");
                }
            }
        }
        return pojo;
    }

    /**
     * Read HLA value and store it in the field of the POJO
     * 
     * @param pojo
     *            the POJO object to set the field on
     * @param pojoElementField
     *            the Field that references in the POJO the value to read
     * @param migElement
     *            the MIG element that describe the field to update
     * @param hlaObjectData
     *            the HLA object that contains the value
     * @param fomDataTypeName
     *            Description of the HLA object, in a FOM syntax
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    protected void decodeElement(Object pojo, Field pojoElementField, Element migElement, DataElement hlaObjectData, String fomDataTypeName)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException,
            SecurityException, NoSuchMethodException, InvocationTargetException {
        ElementType migElementType = migElement.elementType();
        // Decode this element as a BasicType (bool, char, integer number,
        // floating point number, byte, string)
        if (migElementType instanceof BasicType) {
            LOG.debug("Decoding BasicType element {}", migElement.name());
            readBasicType(pojo, pojoElementField, (BasicType) migElementType, hlaObjectData, fomDataTypeName);
        }

        // Decode this element as an array (a sequence of elements)
        if (migElementType instanceof ArrayType) {
            LOG.debug("Decoding ArrayType element {}", migElement.name());
            readArrayType(pojo, pojoElementField, (ArrayType) migElementType, hlaObjectData, fomDataTypeName);
        }

        // Decode this element as another structure (another object)
        if (migElementType instanceof StructureRef) {
            LOG.debug("Decoding StructureRef element {}", migElement.name());
            String structureName = ((StructureRef) migElementType).getStructureName();
            // and finally call recursively to decodeStructure method
            Object pojoElementData = decodeStructure(structureName, hlaObjectData, fomDataTypeName);
            // And store this object into the pojo
            pojoElementField.set(pojo, pojoElementData);

        }
    }

    /**
     * Read a value from the buffer, and store it in the POJO.
     * Value is retrieved from the <i>field</i> of the POJO <i>pojo</i>
     * 
     * @param pojo
     * @param field
     * @param basicType
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected void readBasicType(Object pojo, Field field, BasicType basicType, DataElement hlaObjectData, String fomDataTypeName)
            throws IllegalArgumentException, IllegalAccessException {
        Object pojoValue = hladatareader.getValue(hlaObjectData, fomDataTypeName);

        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN: {
            boolean value = (Boolean) pojoValue;
            LOG.trace("boolean value = {}", value);
            field.setBoolean(pojo, value);
            break;
        }
        case BYTE: {
            byte value = (Byte) pojoValue;
            LOG.trace("byte value = {}", value);
            field.setByte(pojo, value);
            break;
        }
        case SHORT: {
            short value = (Short) pojoValue;
            LOG.trace("short value = {}", value);
            field.setShort(pojo, value);
            break;
        }
        case INT: {
            int value = (Integer) pojoValue;
            LOG.trace("int value = {}", value);
            field.setInt(pojo, value);
            break;
        }
        case LONG: {
            long value = (Long) pojoValue;
            LOG.trace("long value = {}", value);
            field.setLong(pojo, value);
            break;
        }
        case FLOAT: {
            float value = (Float) pojoValue;
            LOG.trace("float value = {}", value);
            field.setFloat(pojo, value);
            break;
        }
        case DOUBLE: {
            double value = (Double) pojoValue;
            LOG.trace("double value = {}", value);
            field.setDouble(pojo, value);
            break;
        }
        case CHAR: {
            char value = (Character) pojoValue;
            LOG.trace("char value = {}", value);
            field.setChar(pojo, value);
            break;
        }
        case STRING: {
            String value = (String) pojoValue;
            LOG.trace("string value = {}", value);
            field.set(pojo, value);
            break;
        }
        case UNSIGNED_BYTE: {
            short value = (Short) pojoValue;
            LOG.trace("unsigned byte value = {}", value);
            field.setShort(pojo, value);
            break;
        }
        case UNSIGNED_SHORT: {
            int value = (Integer) pojoValue;
            LOG.trace("unsigned short value = {}", value);
            field.setInt(pojo, value);
            break;
        }
        case UNSIGNED_INT: {
            long value = (Long) pojoValue;
            LOG.trace("unsigned int value = {}", value);
            field.setLong(pojo, value);
            break;
        }
        case UNSIGNED_FLOAT: {
            double value = (Double) pojoValue;
            LOG.trace("unsigned float value = {}", value);
            field.setDouble(pojo, value);
            break;
        }
        default: {
            Object value = pojoValue;
            LOG.trace("Object value = {}", value);
            field.set(pojo, value);
            LOG.warn(basicType + " decoding is not supported. No information is written in field " + field
                    + ". It could mess up the reading from the buffer!");
        }
        }
    }

    /**
     * Read an Array from the HLA object, and store each entry into the field
     * {@code field} of the POJO {@code pojo}
     * 
     * @param pojo
     * @param pojoElementField
     * @param migArrayType
     * @param hlaObjectData
     * @param fomDataTypeName
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected void readArrayType(Object pojo, Field pojoElementField, ArrayType migArrayType, DataElement hlaObjectData,
            String fomDataTypeName) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassNotFoundException,
            NoSuchFieldException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
            InstantiationException {
        Class<?> pojoElementFieldType = pojoElementField.getType();

        /** Check information about the object **/
        if (!pojoElementFieldType.isArray())
            throw new IllegalArgumentException("Field " + pojoElementField + " is not an array");

        // Read the array
        Object pojoArrayObject = getArrayObject(migArrayType, pojoElementFieldType, hlaObjectData, fomDataTypeName);

        // store it in the POJO
        pojoElementField.set(pojo, pojoArrayObject);
    }

    /**
     * Read an array from an HLA object. Create the POJO that will store data,
     * and return this POJO
     * 
     * @param migArrayType
     * @param pojoArrayClass
     * @param hlaObjectData
     * @param fomDataTypeName
     * @return
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected Object getArrayObject(ArrayType migArrayType, Class<?> pojoArrayClass, DataElement hlaObjectData, String fomDataTypeName)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassNotFoundException, NoSuchFieldException,
            SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Get the array content type
        ElementType migElementType = migArrayType.getElementType();

        // Get the array size, depends on the encoding type of the array (Fixed
        // or variable)
        int arraySize = 0;
        if (hlaObjectData instanceof HLAfixedArray) {
            /** Case HLAfixedArray **/
            @SuppressWarnings("unchecked")
            HLAfixedArray<DataElement> hlaArrayObjectData = (HLAfixedArray<DataElement>) hlaObjectData;
            arraySize = hlaArrayObjectData.size();
        } else {
            /** Case HLAvariableArray **/
            @SuppressWarnings("unchecked")
            HLAvariableArray<DataElement> hlaArrayObjectData = (HLAvariableArray<DataElement>) hlaObjectData;
            arraySize = hlaArrayObjectData.size();
        }

        // Create the POJO array
        Object arrayObject = Array.newInstance(pojoArrayClass, arraySize);

        if (migElementType instanceof BasicType) {
            LOG.debug("{} BasicType elements to store in the array", arraySize);
            // Basic types (primitives and string)
            for (int i = 0; i < arraySize; i++) {
                readArrayElementBasicType(arrayObject, i, (BasicType) migElementType, hlaObjectData, fomDataTypeName);
            }
        }
        if (migElementType instanceof ArrayType) {
            LOG.debug("{} ArrayType elements to store in the array", arraySize);
            // Nested array
            for (int i = 0; i < arraySize; i++) {
                Array.set(arrayObject, i,
                        getArrayObject((ArrayType) migElementType, pojoArrayClass.getComponentType(), hlaObjectData, fomDataTypeName));
            }
        }
        if (migElementType instanceof StructureRef) {
            LOG.debug("{} StructureRef elements to store in the array", arraySize);
            // Complex type = Object
            String structureName = ((StructureRef) migElementType).getStructureName();
            for (int i = 0; i < arraySize; i++) {
                Array.set(arrayObject, i, decodeStructure(structureName, hlaObjectData, fomDataTypeName));
            }

        }

        return arrayObject;
    }

    /**
     * Read an element from an HLA array {@code hlaObjectData}, and store it
     * into the POJO array {@code arrayObject}, located at index {@code index}.
     * This element is a Basic Type element.
     * 
     * @param arrayObject
     * @param index
     * @param basicType
     * @param hlaObjectData
     * @param fomDataTypeName
     */
    protected void readArrayElementBasicType(Object arrayObject, int index, BasicType basicType, DataElement hlaObjectData,
            String fomDataTypeName) {
        Object pojoValue = hladatareader.getValue(hlaObjectData, fomDataTypeName);
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN: {
            boolean value = (Boolean) pojoValue;
            Array.setBoolean(arrayObject, index, value);
            break;
        }
        case BYTE: {
            byte value = (Byte) pojoValue;
            Array.setByte(arrayObject, index, value);
            break;
        }
        case SHORT: {
            short value = (Short) pojoValue;
            Array.setShort(arrayObject, index, value);
            break;
        }
        case INT: {
            int value = (Integer) pojoValue;
            Array.setInt(arrayObject, index, value);
            break;
        }
        case LONG: {
            long value = (Long) pojoValue;
            Array.setLong(arrayObject, index, value);
            break;
        }
        case FLOAT: {
            float value = (Float) pojoValue;
            Array.setFloat(arrayObject, index, value);
            break;
        }
        case DOUBLE: {
            double value = (Double) pojoValue;
            Array.setDouble(arrayObject, index, value);
            break;
        }
        case CHAR: {
            char value = (Character) pojoValue;
            Array.setChar(arrayObject, index, value);
            break;
        }
        case STRING: {
            String value = (String) pojoValue;
            Array.set(arrayObject, index, value);
            break;
        }
        case UNSIGNED_BYTE: {
            short value = (Short) pojoValue;
            Array.setShort(arrayObject, index, value);
            break;
        }
        case UNSIGNED_SHORT: {
            int value = (Integer) pojoValue;
            Array.setInt(arrayObject, index, value);
            break;
        }
        case UNSIGNED_INT: {
            long value = (Long) pojoValue;
            Array.setLong(arrayObject, index, value);
            break;
        }
        case UNSIGNED_FLOAT: {
            double value = (Double) pojoValue;
            Array.setDouble(arrayObject, index, value);
            break;
        }
        default: {
            Object value = pojoValue;
            Array.set(arrayObject, index, value);
            LOG.warn(basicType + " decoding is not supported. No information is written in array " + arrayObject
                    + ". It could mess up the reading from the buffer!");
        }
        }
    }

    /***********************************************************************
     * 
     * 
     * ENCODING PART
     * 
     * 
     ***********************************************************************/

    /**
     * Write a POJO to a buffer. This buffer could be anything, and depends on
     * the implementation.
     * Default implementation use a ByteBuffer
     * 
     * @param pojo
     *            the Java object to serialize
     * @return a buffer that contains the serialized POJO
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InconsistentFDD
     */
    protected Object writePojo(final Object rootPojo) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException, InconsistentFDD {
        // Default is to create a ByteBuffer T
        initializeOutputBuffer(rootPojo);

        // Identifying the POJO
        Class<?> pojoClass = rootPojo.getClass();
        String structureName = pojoClass.getName();
        String pojoClassName = pojoClass.getName();

        if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(structureName))
            throw new UnsupportedOperationException("Atm " + this.getClass().getName() + " doesn't support " + structureName);

        // Encode each attribute of the Object class, one by one
        Map<String, byte[]> outputData = new HashMap<String, byte[]>();
        // MIG structure
        Structure migStructure = configHelper.getPojoStructure(pojoClassName);
        // FOM Structure
        // either object class
        ObjectClassType fomObjectType = parser.findObjectClass(migStructure.getRemoteName(getInterfaceTypeName()));
        // either interaction
        InteractionClassType fomInteractionType = parser.findInteractionClass(migStructure.getRemoteName(getInterfaceTypeName()));

        // Encode each element (attribute) of the class
        for (int i = 0; i < migStructure.getElementsSize(); i++) {
            Element migElement = migStructure.getElement(i);
            String migFieldName = migElement.name();
            String migElementName = migElement.getRemoteName(getInterfaceTypeName());
            //
            // String pojoElementClassName = ((StructureRef)
            // migElement.elementType()).getStructureName();
            String fomDataTypeName = null;
            Field pojoAttributeField = helper.getField(pojoClassName, migFieldName);
            Object pojoAttribute = pojoAttributeField.get(rootPojo);
            if (pojoAttribute != null) {
                // Find the attribute in the FOM
                if (fomObjectType != null) {
                    for (AttributeType cur : fomObjectType.getAttribute()) {
                        if (cur.getName().getValue().equals(migElementName)) {
                            // found it as an object attribute
                            fomDataTypeName = cur.getDataType().getValue();
                            // fomDataTypeName = null;
                        }
                    }
                }
                if (fomInteractionType != null) {
                    for(ParameterType cur:fomInteractionType.getParameter()) {
                        if (cur.getName().getValue().equals(migElementName)) {
                            // found it as an interaction parameter
                            fomDataTypeName = cur.getDataType().getValue();
                        }
                    }
                }
                if (fomDataTypeName != null) {
                    DataElement hlaDataElement = parser.buildHlaDataElement(fomDataTypeName);
                    encodeElement(rootPojo, pojoAttributeField, migElement, hlaDataElement, fomDataTypeName);
                    // encodeStructure(pojoAttribute, pojoElementClassName,
                    // hlaDataElement, fomDataTypeName);
                    ByteWrapper attributeBuffer = new ByteWrapper(new byte[hlaDataElement.getEncodedLength()]);
                    hlaDataElement.encode(attributeBuffer);
                    outputData.put(migElementName, attributeBuffer.array());

                }
            }
        }
        // Get the buffer with last update
        return outputData;
    }

    /**
     * Encode a POJO. The object to encode is stored in attribute field of the
     * object container.
     * If field is null, then container is the object to encode (the POJO)
     * Structure name is the structure element of the POJO we want to encode
     * 
     * 
     * @param container
     * @param pojoClassName
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws InconsistentFDD
     */
    protected void encodeStructure(Object pojo, String pojoClassName, DataElement hlaObjectData, String fomDataTypeName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException,
            ClassNotFoundException, NoSuchFieldException, InconsistentFDD {
        // Get the POJO structure
        Structure migStructure = configHelper.getPojoStructure(pojoClassName);

        // Three different cases: ClassStructure, UnionStructure,
        // EnumStructure
        if (migStructure instanceof EnumStructure) {
            LOG.debug("Encoding EnumStructure " + pojoClassName);
            // Get the FOM type
            EnumeratedDataType fomDataType = (EnumeratedDataType) parser.getDataTypes(fomDataTypeName);
            // Do the encoding of the enumeration stored in field of the
            // container
            Class<?> enumClass = pojo.getClass();

            if (enumClass.isEnum()) {
                // pojo is now the object to encode
                // pojo.toString() return the selected value
                hladatareader.setValue(hlaObjectData, fomDataType, pojo);
            } else {
                throw new IllegalArgumentException("This structure (" + migStructure
                        + ") should be an enum, but the related object is not an enum : " + enumClass);
            }
        } else if (migStructure instanceof UnionStructure) {
            LOG.debug("Encoding UnionStructure " + pojoClassName);
            // Get the FOM structure
            VariantRecordDataType fomVariantDataType = (VariantRecordDataType) parser.getDataTypes(fomDataTypeName);
            // Get the MIG structure
            UnionStructure migUnionStructure = (UnionStructure) migStructure;
            // Do the encoding of the class stored in pojo's field
            //
            @SuppressWarnings("unchecked")
            HLAvariantRecord<DataElement> hlaVariantObject = (HLAvariantRecord<DataElement>) hlaObjectData;
            // Discriminant
            Element migDiscriminantElement = migUnionStructure.getDiscrimant();
            String pojoDiscriminantFieldName = migUnionStructure.getDiscrimant().name();
            Field pojoDiscriminantField = helper.getField(pojoClassName, pojoDiscriminantFieldName);

            DataElement hlaDiscriminantObject = hlaVariantObject.getDiscriminant();
            String fomDiscriminantDataTypeName = fomVariantDataType.getDataType().getValue();

            encodeElement(pojo, pojoDiscriminantField, migDiscriminantElement, hlaDiscriminantObject, fomDiscriminantDataTypeName);
            // Get the value of the discriminant from hla, and convert it as a
            // string.
            // We need a string, because alternative selection depends on string
            // value
            String hlaDiscriminantValue = hladatareader.getValue(hlaDiscriminantObject, fomDiscriminantDataTypeName).toString();
            // pojoDiscriminantField.set(pojo, hlaDiscriminantValue);

            // Obtain the field name to use for this alternative in POJO
            String migSelectedAlternativeName = migUnionStructure.getAlternative(hlaDiscriminantValue);
            Element migSelectedAlternativeElement = migUnionStructure.getElement(migSelectedAlternativeName);
            Field pojoSelectedAlternativeField = helper.getField(pojoClassName, migSelectedAlternativeName);

            // Find the selected alternative in FOM
            List<Alternative> fomAlternativeList = fomVariantDataType.getAlternative();
            Alternative fomSelectedAlternativeElement = null;
            for (Alternative fomAlternative : fomAlternativeList) {
                if (fomAlternative.getEnumerator().getValue().equals(hlaDiscriminantValue)) {
                    fomSelectedAlternativeElement = fomAlternative;
                }
            }
            String fomAlternativeName = fomSelectedAlternativeElement.getDataType().getValue();

            // Get the alternative value in HLA object
            DataElement hlaAlternativeValue = hlaVariantObject.getValue();

            encodeElement(pojo, pojoSelectedAlternativeField, migSelectedAlternativeElement, hlaAlternativeValue, fomAlternativeName);
        } else if (migStructure instanceof ClassStructure) {
            LOG.debug("Encoding ClassStructure " + pojoClassName);
            // get the FOM Structure
            FixedRecordDataType fomDataType = (FixedRecordDataType) parser.getDataTypes(fomDataTypeName);

            // Do the encoding of the class stored in field of the pojo
            // Iterate the structure
            int structureSize = migStructure.getElementsSize();
            for (int idx = 0; idx < structureSize; idx++) {
                Element migElement = migStructure.getElement(idx);
                if (migElement.isField()) {
                    Field pojoElementField = helper.getField(pojoClassName, migElement.name());
                    String fomPartName = migElement.getRemoteName(getInterfaceTypeName());
                    String fomPartDataTypeName = null;
                    DataElement hlaPartObjectData = null;
                    // Find the FOM and HLA element part
                    for (int i = 0; i < fomDataType.getField().size(); i++) {
                        org.ieee.standards.ieee1516_2010.FixedRecordDataType.Field fomPart = fomDataType.getField().get(i);
                        if (fomPart.getName().getValue().equals(fomPartName)) {
                            // the fomPart is found
                            fomPartDataTypeName = fomPart.getDataType().getValue();
                            hlaPartObjectData = ((HLAfixedRecord) hlaObjectData).get(i);
                        }
                    }
                    if (fomPartDataTypeName != null && hlaPartObjectData != null) {
                        encodeElement(pojo, pojoElementField, migElement, hlaPartObjectData, fomPartDataTypeName);
                    } else {
                        throw new InconsistentFDD("Element " + fomPartName + " is not found in FOM on DataType " + fomDataTypeName);
                    }
                } else {
                    // Getter and Setter are not supported yet
                    throw new UnsupportedOperationException("PojoElement only support Fields atm");
                }

            }
        }
        // If unionStructure
        // FIXME EncodeStructure of VariantRecord data type
    }

    protected void encodeElement(Object pojo, Field pojoElementField, Element migElement, DataElement hlaObjectData, String fomDataTypeName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException,
            ClassNotFoundException, NoSuchFieldException, InconsistentFDD {
        ElementType migElementType = migElement.elementType();
        // encode this element as a basic type
        if (migElementType instanceof BasicType) {
            LOG.debug("Encoding BasicType element {}", migElement.name());
            writeBasicType(pojo, pojoElementField, (BasicType) migElementType, hlaObjectData, fomDataTypeName);
        }

        // encode this element as an array
        if (migElementType instanceof ArrayType) {
            LOG.debug("Encoding ArrayType element {}", migElement.name());
            writeArrayType(pojo, pojoElementField, (ArrayType) migElementType, hlaObjectData, fomDataTypeName);
        }

        // This element is a structure. Recursive call to encodeStructure (which
        // will call encodeElement if necessary)
        if (migElementType instanceof StructureRef) {
            LOG.debug("Encoding StructureRef element {}", migElement.name());
            Object pojoElementData = pojoElementField.get(pojo);
            String migStructureName = ((StructureRef) migElementType).getStructureName();
            encodeStructure(pojoElementData, migStructureName, hlaObjectData, fomDataTypeName);
        }
    }

    /**
     * Write a value from a field of the POJO. Value is first retrieved from the
     * <i>field</i> of the POJO <i>pojo</i>, and then written to the buffer
     * 
     * @param pojo
     * @param pojoElementField
     * @param basicType
     * @param hlaObjectData
     * @param fomDataTypeName
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InconsistentFDD
     */
    protected void writeBasicType(Object pojo, Field pojoElementField, BasicType basicType, DataElement hlaObjectData,
            String fomDataTypeName) throws IllegalArgumentException, IllegalAccessException, InconsistentFDD {
        Object pojoValue = null;
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN:
            pojoValue = pojoElementField.getBoolean(pojo);
            break;
        case BYTE:
            pojoValue = pojoElementField.getByte(pojo);
            break;
        case SHORT:
            pojoValue = pojoElementField.getShort(pojo);
            break;
        case INT:
            pojoValue = pojoElementField.getInt(pojo);
            break;
        case LONG:
            pojoValue = pojoElementField.getLong(pojo);
            break;
        case FLOAT:
            pojoValue = pojoElementField.getFloat(pojo);
            break;
        case DOUBLE:
            pojoValue = pojoElementField.getDouble(pojo);
            break;
        case CHAR:
            pojoValue = pojoElementField.getChar(pojo);
            break;
        case STRING:
            pojoValue = (String) pojoElementField.get(pojo);
            break;
        case UNSIGNED_BYTE:
            pojoValue = pojoElementField.getShort(pojo);
            break;
        case UNSIGNED_SHORT:
            pojoValue = pojoElementField.getInt(pojo);
            break;
        case UNSIGNED_INT:
            pojoValue = pojoElementField.getLong(pojo);
            break;
        case UNSIGNED_FLOAT:
            pojoValue = pojoElementField.getDouble(pojo);
            break;
        default:
            pojoValue = pojoElementField.get(pojo);
            // LOG.warn(basicType.toString() +
            // " is not supported. Information from field " + pojoElementField +
            // " is not encoded");
            break;
        }
        if (pojoValue != null) {
            hladatareader.setValue(hlaObjectData, fomDataTypeName, pojoValue);
        } else {
            LOG.warn(" Information from field" + pojoElementField + " is null. Data is not encoded");
        }
    }

    /**
     * Write an array to the buffer.
     * Array value is first extracted from the POJO. value is stored in the
     * field <i>field</i> of the POJO <i>pojo</i>
     * 
     * @param pojo
     * @param pojoElementField
     * @param migArrayType
     *            is the ElementType of this array
     * 
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ArrayIndexOutOfBoundsException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws InconsistentFDD
     */
    protected void writeArrayType(Object pojo, Field pojoElementField, ArrayType migArrayType, DataElement hlaObjectData,
            String fomDataTypeName) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException,
            NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException,
            InconsistentFDD {
        // Get information from the object
        Class<?> fieldType = pojoElementField.getType();

        /** Check information about the object **/
        if (!fieldType.isArray())
            throw new IllegalArgumentException("Field " + pojoElementField + " is not an array");

        /** Do the job **/
        // Get the array object
        Object arrayObject = pojoElementField.get(pojo);

        // Now that the array object value is extracted, write it to the buffer
        writeArrayType(arrayObject, migArrayType, hlaObjectData, fomDataTypeName);
    }

    /**
     * Write an array object. Value of the array object is already extracted
     * from the POJO (the "container" Object).
     * 
     * @param pojoArrayObject
     * @param arrayElementClassType
     * @param elementType
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws InconsistentFDD
     */
    protected void writeArrayType(Object pojoArrayObject, ArrayType migArrayType, DataElement hlaObjectData, String fomDataTypeName)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
            SecurityException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, InconsistentFDD {

        // Get information from the structure
        ElementType migElementType = migArrayType.getElementType();

        // First, write header of the array

        // Get size of the array
        int arraySize = Array.getLength(pojoArrayObject);

        if (migElementType instanceof BasicType) {
            LOG.debug("This array contains {} BasicType elements", arraySize);
            // Basic types (primitives, and string)
            for (int i = 0; i < arraySize; i++) {
                writeBasicType(pojoArrayObject, i, (BasicType) migElementType, hlaObjectData, fomDataTypeName);
            }
        }
        if (migElementType instanceof ArrayType) {
            LOG.debug("This array contains {} ArrayType elements", arraySize);
            // Nested Array

            /** Do the job **/
            for (int i = 0; i < arraySize; i++) {
                // Get the array object
                Object pojoArrayElement = Array.get(pojoArrayObject, i);
//                ArrayDataType fomArrayType = (ArrayDataType) parser.getDataTypes(fomDataTypeName);
                // if ("HLAfomArrayType.getCardinality().getValue())
//                @SuppressWarnings("unchecked")
//                DataElement hlaElementData = ((HLAfixedArray<DataElement>) hlaObjectData).get(i);
                // Now that the array object value is extracted, write it to the
                // buffer
                writeArrayType(pojoArrayElement, (ArrayType) migElementType, hlaObjectData, fomDataTypeName);
            }

        }
        if (migElementType instanceof StructureRef) {
            LOG.debug("This array contains {} StructureRef elements", arraySize);
            // Complex Type = Object
            String migStructureName = ((StructureRef) migElementType).getStructureName();
            for (int i = 0; i < arraySize; i++) {
                encodeStructure(Array.get(pojoArrayObject, i), migStructureName, hlaObjectData, fomDataTypeName);
            }
        }

        // Finally write the footer
    }

    /**
     * Write the basic type that is stored in the array <i>arrayObject</i>, at
     * position <i>index</i>
     * 
     * @param arrayObject
     *            The array
     * @param index
     *            index of the value in the array that must be written
     * @param basicType
     *            ElementType of the value stored in the array
     * @throws InconsistentFDD
     */
    protected void writeBasicType(Object arrayObject, int index, BasicType basicType, DataElement hlaObjectData, String fomDataTypeName)
            throws InconsistentFDD {
        Object value = null;
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN:
            value = Array.getBoolean(arrayObject, index);
            break;
        case BYTE:
            value = Array.getByte(arrayObject, index);
            break;
        case SHORT:
            value = Array.getShort(arrayObject, index);
            break;
        case INT:
            value = Array.getInt(arrayObject, index);
            break;
        case LONG:
            value = Array.getLong(arrayObject, index);
            break;
        case FLOAT:
            value = Array.getFloat(arrayObject, index);
            break;
        case DOUBLE:
            value = Array.getDouble(arrayObject, index);
            break;
        case CHAR:
            value = Array.getChar(arrayObject, index);
            break;
        case STRING:
            value = (String) Array.get(arrayObject, index);
            break;
        case UNSIGNED_BYTE:
            value = Array.getShort(arrayObject, index);
            break;
        case UNSIGNED_SHORT:
            value = Array.getInt(arrayObject, index);
            break;
        case UNSIGNED_INT:
            value = Array.getLong(arrayObject, index);
            break;
        case UNSIGNED_FLOAT:
            value = Array.getDouble(arrayObject, index);
            break;
        default:
            value = Array.get(arrayObject, index);
            // writeObject(field.get(pojo), pojoElement.name());
            LOG.warn(basicType.toString() + " is not supported. Information from array " + arrayObject + " is not encoded");
            break;
        }
        if (value != null) {
            hladatareader.setValue(hlaObjectData, fomDataTypeName, value);
        } else {
            LOG.warn(" Information from array " + arrayObject + " at position #" + index + " is null. Data is not encoded");
        }
    }

    /***********************************************************************
     * 
     * 
     * Abstract methods
     * 
     * 
     ***********************************************************************/

    /**
     * Override to return the header name that contain the POJO type
     * 
     * @return
     */
    protected abstract String setHeaderClassName();

    /**
     * Override to return the name of this interface
     * 
     * @return
     */
    protected abstract String setInterfaceTypeName();

    /**
     * Calculate buffer size for the POJO
     * 
     * @param pojo
     * @return
     */
    abstract protected int maxBufferSize(Object pojo);

    /**
     * This method returns true if the content is a buffer that contains a
     * serialized POJO.
     * Content can be a ByteBuffer, or something similar. It depends on the
     * implementation
     * 
     * @param content
     * @return
     */
    abstract protected boolean isABuffer(Object content);

    /**
     * Returns the expected class of the input message's body
     * Typically, this is used as
     * <code>exchange.getIn().getBody(expectedBodyClass());</code>
     * 
     * @return Class type of expected input Body
     */
    abstract protected Class<?> expectedBodyClass();

    /**
     * Initialized the input buffer. Implementing class can copy or keep an
     * instance reference of this buffer.
     * Implementation also validate that the buffer is rewinded
     * 
     * @param buffer
     *            the ByteBuffer read from the input message
     * @return true if buffer is a valid one.
     *         If false, the interface will stop and throw an exception
     */
    abstract protected boolean initializeInputBuffer(Object buffer);

    /**
     * Initialize the output buffer. After this, the buffer sent as parameter
     * will be filled with data written to it
     * 
     * @param rootPojo
     * 
     * @return true if the byte buffer can be sent as is. If false, the method
     *         getFinalizedOutputBuffer() will be used to get the buffer to copy
     *         to the message
     */
    abstract protected boolean initializeOutputBuffer(Object rootPojo);

    /**
     * Get the output buffer. It will then be written into the Camel Message
     * 
     * @return
     */
    abstract protected Object getFinalizedOutputBuffer();

    /**
     * Method called when exchange is received.
     * 
     * @param exchange
     * @return true if the exchange is correct
     */
    abstract protected boolean validate(Exchange exchange);

}
