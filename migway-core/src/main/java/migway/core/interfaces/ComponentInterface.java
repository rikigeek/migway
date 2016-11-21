package migway.core.interfaces;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
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
import migway.core.helper.PojoLoaderHelper;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.Processor;
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
public abstract class ComponentInterface implements Processor {
    protected Logger LOGGER = LoggerFactory.getLogger(ComponentInterface.class);

    /**
     * The header in the message that is used to set the Object class (a java
     * binary name)
     */
    private String headerClassName;

    protected String getHeaderClassName() {
        return headerClassName;
    }

    /**
     * The name of the interface type. Used mainly in Mapper with class specific
     * to a component
     */
    private String interfaceTypeName;

    protected String getInterfaceTypeName() {
        return interfaceTypeName;
    }

    protected ConfigHelper configHelper;

    protected PojoLoaderHelper helper = PojoLoaderHelper.INSTANCE;

    public ComponentInterface() {
        this(ConfigHelper.loadConfig(new File("migway:sample")));
    }

    public ComponentInterface(ConfigHelper configHelper) {
        this.configHelper = configHelper;
        this.headerClassName = setHeaderClassName();
        this.interfaceTypeName = setInterfaceTypeName();
        helper.addPojoLocation("src/test/resources");

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
        LOGGER.trace("Validating Exchange");
        if (!validate(exchange)) {
            throw new InvalidPayloadException(exchange, null);
        }
        // Input message must be set
        Message in = exchange.getIn();
        LOGGER.trace("Checking if Input message is null");
        if (in == null) {
            // Should never happen
            throw new InvalidPayloadException(exchange, exchange.getClass());
        }

        // Body of the message must be a ByteBuffer, or a supported POJO
        LOGGER.trace("Validating body content");
        Object content = in.getBody();
        if (in.getBody() == null) {
            throw new InvalidPayloadException(exchange, Object.class);
        }
        // body is not null
        LOGGER.debug("Body of input message type:" + content.getClass().getName());
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
                LOGGER.error("BufferUnderflowException {} while decoding {} object", e.toString(), pojoClass);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unable to read header " + getHeaderClassName(), e);
            }
        } else {
            // Not a ByteBuffer. Must be a POJO
            // Get the not converted body
            content = in.getBody();

            Class<?> pojoClass = content.getClass();
            if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(pojoClass.getName())) {
                LOGGER.error(getInterfaceTypeName() + " supported POJO doesn't contain " + pojoClass.getName()); 
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

    /**
     * Write a buffer into a POJO. the POJO pojo is already instantiated.
     * 
     * @param pojo
     * @param buffer
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected Object readPojo(Object buffer, String pojoClassName) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException {

        if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(pojoClassName))
            throw new UnsupportedOperationException("Atm " + this.getClass().getName() + " doesn't support " + pojoClassName);

        // Check if buffer is valid, and initialize it before we can read from
        // it
        if (!initializeInputBuffer(buffer)) {
            throw new IllegalArgumentException(String.format("Invalid input body"));
        }

        // The POJO will be instantiated into the decoding method
        return decodeStructure(pojoClassName);
        // Here we have a POJO object with good values that we can return
    }

    protected void readArrayType(Object pojo, Field field, ArrayType arrayType) throws ArrayIndexOutOfBoundsException,
            IllegalArgumentException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, InstantiationException {
        Class<?> fieldType = field.getType();

        /** Check information about the object **/
        if (!fieldType.isArray())
            throw new IllegalArgumentException("Field " + field + " is not an array");

        // Read the array
        Object arrayObject = readArrayType(arrayType, fieldType);

        // store it in the POJO
        field.set(pojo, arrayObject);
    }

    protected Object readArrayType(ArrayType arrayType, Class<?> arrayClass) throws ArrayIndexOutOfBoundsException,
            IllegalArgumentException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, InstantiationException {
        /** Do the job **/
        ElementType elementType = arrayType.getElementType();

        // The size must have been set in the readPreArray
        Object arrayObject = readPreArray(elementType, arrayClass.getComponentType());
        int arraySize = Array.getLength(arrayObject);

        if (elementType instanceof BasicType) {
            LOGGER.debug("{} BasicType elements to store in the array", arraySize);
            // Basic types (primitives and string)
            for (int i = 0; i < arraySize; i++) {
                readBasicType(arrayObject, i, (BasicType) elementType);
            }
        }
        if (elementType instanceof ArrayType) {
            LOGGER.debug("{} ArrayType elements to store in the array", arraySize);
            // Nested array
            for (int i = 0; i < arraySize; i++) {
                Array.set(arrayObject, i, readArrayType((ArrayType) elementType, arrayClass.getComponentType()));
            }
        }
        if (elementType instanceof StructureRef) {
            LOGGER.debug("{} StructureRef elements to store in the array", arraySize);
            // Complex type = Object
            String structureName = ((StructureRef) elementType).getStructureName();
            for (int i = 0; i < arraySize; i++) {
                Array.set(arrayObject, i, decodeStructure(structureName));
            }

        }

        // Finally read the footer
        readPostArray(arrayObject);

        return arrayObject;
    }

    /**
     * Read from the buffer information about the array structure. Typically,
     * the size of the array
     * Build then a new Array:
     * <code>Array.newInstance(Class<?> elementClass, int length);</code>
     * 
     * Where elementClass is the class of element contained in the Array. Get
     * it from the method parameters.
     * 
     * TODO clean this part. What information to send to this abstract method?
     * Is Class enough? ArrayType could be interesting, because it contains
     * minimal and maximal size of the array. Also, return value should be an
     * object with information about the Array (size... and whatever else)
     * 
     * 
     * @param arrayType
     *            type of the array as defined in the structure
     * @param elementClass
     *            class of component of the array. To be used in instantiation
     *            of the array
     * @return the new instance of the array
     */
    abstract protected Object readPreArray(ElementType elementType, Class<?> elementClass);

    abstract protected Object readPostArray(Object arrayObject);

    protected Object readEnumType(Class<?> enumType, EnumStructure structure) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object elementValue;
        // The method to get list of enum values
        Method m = enumType.getMethod("values");
        java.lang.Enum<?>[] list = (Enum<?>[]) m.invoke(null);
        // Read the value from the buffer.
        // TODO this should be in Default implementation class, not in Abstract
        // class, because it could also be String, for instance, or something
        // else (a long, a byte...)
        int i = readInt();
        if (i < list.length) {
            elementValue = list[i];
        } else {
            LOGGER.error("Enum read is out of range: " + i + " > " + list.length + " (" + structure.getName() + ")");
            elementValue = list[0];
        }

        return elementValue;
    }

    protected Map<String, Object> loadedPojo = new HashMap<String, Object>();

    protected void decodeElement(Object pojo, Field field, Element element) throws IllegalArgumentException, IllegalAccessException,
            ClassNotFoundException, InstantiationException, NoSuchFieldException, SecurityException, NoSuchMethodException,
            InvocationTargetException {
        ElementType type = element.elementType();
        // Decode this element as a BasicType (bool, char, integer number,
        // floating point number, byte, string)
        if (type instanceof BasicType) {
            LOGGER.debug("Decoding BasicType element {}", element.name());
            readBasicType(pojo, field, (BasicType) type);
        }

        // Decode this element as an array (a sequence of elements)
        if (type instanceof ArrayType) {
            LOGGER.debug("Decoding ArrayType element {}", element.name());
            readArrayType(pojo, field, (ArrayType) type);
        }

        // Decode this element as another structure (another object)
        if (type instanceof StructureRef) {
            LOGGER.debug("Decoding StructureRef element {}", element.name());
            String structureName = ((StructureRef) type).getStructureName();
            // and finally call recursively to decodeStructure method
            Object o = decodeStructure(structureName);
            // And store this object into the pojo
            field.set(pojo, o);

        }
    }

    protected void encodeElement(Object pojo, Field field, Element element) throws IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        ElementType type = element.elementType();
        // encode this element as a basic type
        if (type instanceof BasicType) {
            LOGGER.debug("Encoding BasicType element {}", element.name());
            writeBasicType(pojo, field, (BasicType) type);
        }

        // encode this element as an array
        if (type instanceof ArrayType) {
            LOGGER.debug("Encoding ArrayType element {}", element.name());
            writeArrayType(pojo, field, (ArrayType) type);
        }

        // This element is a structure. Recursive call to encodeStructure (which
        // will call encodeElement if necessary)
        if (type instanceof StructureRef) {
            LOGGER.debug("Encoding StructureRef element {}", element.name());
            Object o = field.get(pojo);
            String structureName = ((StructureRef) type).getStructureName();
            encodeStructure(o, structureName);
        }
    }

    /**
     * Decode buffer and store values in instantiated POJO.
     * POJO are stored into loadedPojo HashMap
     * 
     * @param structureName
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
    protected Object decodeStructure(String structureName) throws ClassNotFoundException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Get the POJO Structure
        Structure pojoStruct = configHelper.getPojoStructure(structureName);
        // The object that will be returned
        Object pojo = null;
        // Three different cases: ClassStructure, UnionStructure (ignored at the
        // moment), and EnumStructure
        if (pojoStruct instanceof EnumStructure) {
            // Class<?> enumClass = pojo.getClass(); // FIXME this could be null
            // if not instantiated in object constructor

            // LOGGER.error("Enum cannot be read yet. Structure " +
            // structureName + " can't be written to object " + pojo);
            // TODO call readEnumType in a correct way
            Class<?> enumType = helper.getPojoClass(structureName);
            pojo = readEnumType(enumType, (EnumStructure) pojoStruct);
        }
        if (pojoStruct instanceof ClassStructure) {
            // Instantiate the POJO
            pojo = helper.loadPojo(structureName);

            // Do the decoding of the class stored in field of the POJO
            // Iterate the structure
            int structureSize = pojoStruct.getElementsSize();
            for (int i = 0; i < structureSize; i++) {
                Element element = pojoStruct.getElement(i);
                if (element.isField()) {
                    Field elementField = helper.getField(structureName, element.name());
                    // Fill each element of this POJO
                    decodeElement(pojo, elementField, element);
                } else {
                    throw new UnsupportedOperationException("PojoElement only support Fields atm");
                }
            }
        }
        return pojo;
    }

    /**
     * Encode a POJO. The object to encode is stored in attribute field of the
     * object container.
     * If field is null, then container is the object to encode (the POJO)
     * Structure name is the structure element of the POJO we want to encode
     * 
     * 
     * @param container
     * @param structureName
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    protected void encodeStructure(Object pojo, String structureName) throws IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        // Get the POJO structure
        Structure pojoStruct = configHelper.getPojoStructure(structureName);

        // Three different cases: ClassStructure, UnionStructure (ignore atm),
        // EnumStructure
        if (pojoStruct instanceof EnumStructure) {
            LOGGER.debug("Encoding EnumStructure " + structureName);
            // Do the encoding of the enumeration stored in field of the
            // container
            Class<?> enumClass = pojo.getClass();

            if (enumClass.isEnum()) {
                // pojo is now the object to encode
                writeEnumType(pojo, enumClass, (EnumStructure) pojoStruct);
            } else {
                throw new IllegalArgumentException("This structure (" + pojoStruct
                        + ") should be an enum, but the related object is not an enum : " + enumClass);
            }
        }
        if (pojoStruct instanceof ClassStructure) {
            LOGGER.debug("Encoding ClassStructure " + structureName);
            // Do the encoding of the class stored in field of the pojo
            // Iterate the structure
            int structureSize = pojoStruct.getElementsSize();
            for (int idx = 0; idx < structureSize; idx++) {
                Element element = pojoStruct.getElement(idx);
                if (element.isField()) {
                    Field elementField = helper.getField(structureName, element.name());
                    encodeElement(pojo, elementField, element);
                } else {
                    // Getter and Setter are not supported yet
                    throw new UnsupportedOperationException("PojoElement only support Fields atm");
                }

            }
        }
        // If unionStructure
    }

    /**
     * Write an enum value to the buffer.
     * 
     * @param elementValue
     *            is the value extracted from a fiel of a POJO
     * @param enumType
     *            is the class of the elementValue (
     *            <code>elementValue.getClass()</code>)
     * @param structure
     *            is the EnumStructure
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     */
    protected void writeEnumType(Object elementValue, Class<?> enumType, EnumStructure structure) throws IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the method that will return the ordinal position of the value in
        // the enumeration
        Method mOrdinal = enumType.getMethod("ordinal");
        // And now, get the ordinal position of the value in the enum list
        int pos = (int) mOrdinal.invoke(elementValue);
        // And write this as an integer
        // TODO: extract this part in Default implementation
        writeInt(pos);
    }

    /**
     * Write an array to the buffer.
     * Array value is first extracted from the POJO. value is stored in the
     * field <i>field</i> of the POJO <i>pojo</i>
     * 
     * @param pojo
     * @param field
     * @param arrayType
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
     */
    protected void writeArrayType(Object pojo, Field field, ArrayType arrayType) throws IllegalArgumentException, IllegalAccessException,
            ArrayIndexOutOfBoundsException, NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException,
            NoSuchFieldException {
        // Get information from the object
        Class<?> fieldType = field.getType();

        /** Check information about the object **/
        if (!fieldType.isArray())
            throw new IllegalArgumentException("Field " + field + " is not an array");

        /** Do the job **/
        // Get the array object
        Object arrayObject = field.get(pojo);

        // Now that the array object value is extracted, write it to the buffer
        writeArrayType(arrayObject, arrayType);
    }

    /**
     * Write an array object. Value of the array object is already extracted
     * from the POJO (the "container" Object).
     * 
     * @param arrayObject
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
     */
    protected void writeArrayType(Object arrayObject, ArrayType arrayType) throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException,
            NoSuchFieldException {

        // Get information from the structure
        ElementType elementType = arrayType.getElementType();

        // First, write header of the array
        writePreArray(arrayObject, arrayObject.getClass());

        // Get size of the array
        int arraySize = Array.getLength(arrayObject);

        if (elementType instanceof BasicType) {
            LOGGER.debug("This array contains {} BasicType elements", arraySize);
            // Basic types (primitives, and string)
            for (int i = 0; i < arraySize; i++) {
                writeBasicType(arrayObject, i, (BasicType) elementType);
            }
        }
        if (elementType instanceof ArrayType) {
            LOGGER.debug("This array contains {} ArrayType elements", arraySize);
            // Nested Array

            /** Do the job **/
            for (int i = 0; i < arraySize; i++) {
                // Get the array object
                Object arrayElement = Array.get(arrayObject, i);

                // Now that the array object value is extracted, write it to the
                // buffer
                writeArrayType(arrayElement, (ArrayType) elementType);
            }

        }
        if (elementType instanceof StructureRef) {
            LOGGER.debug("This array contains {} StructureRef elements", arraySize);
            // Complex Type = Object
            String structureName = ((StructureRef) elementType).getStructureName();
            for (int i = 0; i < arraySize; i++) {
                encodeStructure(Array.get(arrayObject, i), structureName);
            }
        }

        // Finally write the footer
        writePostArray(arrayObject, arrayObject.getClass());
    }

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
     */
    protected Object writePojo(final Object rootPojo) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException {
        // Default is to create a ByteBuffer T
        initializeOutputBuffer(rootPojo);

        // Identifying the POJO
        Class<?> pojoClass = rootPojo.getClass();
        String structureName = pojoClass.getName();

        if (!configHelper.supportedPojos(getInterfaceTypeName()).contains(structureName))
            throw new UnsupportedOperationException("Atm " + this.getClass().getName() + " doesn't support " + structureName);

        encodeStructure(rootPojo, structureName);

        // Get the buffer with last update
        return getFinalizedOutputBuffer();
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
     */
    protected void writeBasicType(Object arrayObject, int index, BasicType basicType) {
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN:
            writeBoolean(Array.getBoolean(arrayObject, index));
            break;
        case BYTE:
            writeByte(Array.getByte(arrayObject, index));
            break;
        case SHORT:
            writeShort(Array.getShort(arrayObject, index));
            break;
        case INT:
            writeInt(Array.getInt(arrayObject, index));
            break;
        case LONG:
            writeLong(Array.getLong(arrayObject, index));
            break;
        case FLOAT:
            writeFloat(Array.getFloat(arrayObject, index));
            break;
        case DOUBLE:
            writeDouble(Array.getDouble(arrayObject, index));
            break;
        case CHAR:
            writeChar(Array.getChar(arrayObject, index));
            break;
        case STRING:
            writeString((String) Array.get(arrayObject, index));
            break;
        case UNSIGNED_BYTE:
            writeUnsignedByte(Array.getShort(arrayObject, index));
            break;
        case UNSIGNED_SHORT:
            writeUnsignedShort(Array.getInt(arrayObject, index));
            break;
        case UNSIGNED_INT:
            writeUnsignedInt(Array.getLong(arrayObject, index));
            break;
        case UNSIGNED_FLOAT:
            writeUnsignedFloat(Array.getDouble(arrayObject, index));
            break;
        default:
            // writeObject(field.get(pojo), pojoElement.name());
            LOGGER.warn(basicType.toString() + " is not supported. Information from array " + arrayObject + " is not encoded");
            break;
        }
    }

    /**
     * Write a value from a field of the POJO. Value is first retrieved from the
     * <i>field</i> of the POJO <i>pojo</i>, and then written to the buffer
     * 
     * @param pojo
     * @param field
     * @param basicType
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected void writeBasicType(Object pojo, Field field, BasicType basicType) throws IllegalArgumentException, IllegalAccessException {
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN:
            writeBoolean(field.getBoolean(pojo));
            break;
        case BYTE:
            writeByte(field.getByte(pojo));
            break;
        case SHORT:
            writeShort(field.getShort(pojo));
            break;
        case INT:
            writeInt(field.getInt(pojo));
            break;
        case LONG:
            writeLong(field.getLong(pojo));
            break;
        case FLOAT:
            writeFloat(field.getFloat(pojo));
            break;
        case DOUBLE:
            writeDouble(field.getDouble(pojo));
            break;
        case CHAR:
            writeChar(field.getChar(pojo));
            break;
        case STRING:
            writeString((String) field.get(pojo));
            break;
        case UNSIGNED_BYTE:
            writeUnsignedByte(field.getShort(pojo));
            break;
        case UNSIGNED_SHORT:
            writeUnsignedShort(field.getInt(pojo));
            break;
        case UNSIGNED_INT:
            writeUnsignedInt(field.getLong(pojo));
            break;
        case UNSIGNED_FLOAT:
            writeUnsignedFloat(field.getDouble(pojo));
            break;
        default:
            // writeObject(field.get(pojo), pojoElement.name());
            LOGGER.warn(basicType.toString() + " is not supported. Information from field " + field + " is not encoded");
            break;
        }
    }

    protected void readBasicType(Object arrayObject, int index, BasicType basicType) {
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN: {
            boolean value = readBoolean();
            Array.setBoolean(arrayObject, index, value);
            break;
        }
        case BYTE: {
            byte value = readByte();
            Array.setByte(arrayObject, index, value);
            break;
        }
        case SHORT: {
            short value = readShort();
            Array.setShort(arrayObject, index, value);
            break;
        }
        case INT: {
            int value = readInt();
            Array.setInt(arrayObject, index, value);
            break;
        }
        case LONG: {
            long value = readLong();
            Array.setLong(arrayObject, index, value);
            break;
        }
        case FLOAT: {
            float value = readFloat();
            Array.setFloat(arrayObject, index, value);
            break;
        }
        case DOUBLE: {
            double value = readDouble();
            Array.setDouble(arrayObject, index, value);
            break;
        }
        case CHAR: {
            char value = readChar();
            Array.setChar(arrayObject, index, value);
            break;
        }
        case STRING: {
            String value = readString();
            Array.set(arrayObject, index, value);
            break;
        }
        case UNSIGNED_BYTE: {
            short value = readUnsignedByte();
            Array.setShort(arrayObject, index, value);
            break;
        }
        case UNSIGNED_SHORT: {
            int value = readUnsignedShort();
            Array.setInt(arrayObject, index, value);
            break;
        }
        case UNSIGNED_INT: {
            long value = readUnsignedInt();
            Array.setLong(arrayObject, index, value);
            break;
        }
        case UNSIGNED_FLOAT: {
            double value = readUnsignedFloat();
            Array.setDouble(arrayObject, index, value);
            break;
        }
        default: {
            Object value = readObject("");
            Array.set(arrayObject, index, value);
            LOGGER.warn(basicType + " decoding is not supported. No information is written in array " + arrayObject
                    + ". It could mess up the reading from the buffer!");
        }
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
    protected void readBasicType(Object pojo, Field field, BasicType basicType) throws IllegalArgumentException, IllegalAccessException {
        switch (basicType.getBasicTypeEnum()) {
        case BOOLEAN: {
            boolean value = readBoolean();
            LOGGER.trace("boolean value = {}", value);
            field.setBoolean(pojo, value);
            break;
        }
        case BYTE: {
            byte value = readByte();
            LOGGER.trace("byte value = {}", value);
            field.setByte(pojo, value);
            break;
        }
        case SHORT: {
            short value = readShort();
            LOGGER.trace("short value = {}", value);
            field.setShort(pojo, value);
            break;
        }
        case INT: {
            int value = readInt();
            LOGGER.trace("int value = {}", value);
            field.setInt(pojo, value);
            break;
        }
        case LONG: {
            long value = readLong();
            LOGGER.trace("long value = {}", value);
            field.setLong(pojo, value);
            break;
        }
        case FLOAT: {
            float value = readFloat();
            LOGGER.trace("float value = {}", value);
            field.setFloat(pojo, value);
            break;
        }
        case DOUBLE: {
            double value = readDouble();
            LOGGER.trace("double value = {}", value);
            field.setDouble(pojo, value);
            break;
        }
        case CHAR: {
            char value = readChar();
            LOGGER.trace("char value = {}", value);
            field.setChar(pojo, value);
            break;
        }
        case STRING: {
            String value = readString();
            LOGGER.trace("string value = {}", value);
            field.set(pojo, value);
            break;
        }
        case UNSIGNED_BYTE: {
            short value = readUnsignedByte();
            LOGGER.trace("unsigned byte value = {}", value);
            field.setShort(pojo, value);
            break;
        }
        case UNSIGNED_SHORT: {
            int value = readUnsignedShort();
            LOGGER.trace("unsigned short value = {}", value);
            field.setInt(pojo, value);
            break;
        }
        case UNSIGNED_INT: {
            long value = readUnsignedInt();
            LOGGER.trace("unsigned int value = {}", value);
            field.setLong(pojo, value);
            break;
        }
        case UNSIGNED_FLOAT: {
            double value = readUnsignedFloat();
            LOGGER.trace("unsigned float value = {}", value);
            field.setDouble(pojo, value);
            break;
        }
        default: {
            Object value = readObject("");
            LOGGER.trace("Object value = {}", value);
            field.set(pojo, value);
            LOGGER.warn(basicType + " decoding is not supported. No information is written in field " + field
                    + ". It could mess up the reading from the buffer!");
        }
        }
    }

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

    // all methods to read primitive types to override
    abstract protected boolean readBoolean();

    abstract protected byte readByte();

    abstract protected short readShort();

    abstract protected int readInt();

    abstract protected long readLong();

    abstract protected float readFloat();

    abstract protected double readDouble();

    abstract protected char readChar();

    abstract protected String readString();

    // Unsigned
    abstract protected short readUnsignedByte();

    abstract protected int readUnsignedShort();

    abstract protected long readUnsignedInt();

    abstract protected double readUnsignedFloat();

    // Defaut type for unknown primitive
    abstract protected Object readObject(String objectType);

    /**
     * Write some heading information in the buffer. This information can help
     * decoding the buffer. Typically, it can be the size of the array.
     * Size of the array can be found with
     * <code>Array.getLength(arrayObject);</code>
     * 
     * @param arrayObject
     * @param fieldType
     */
    abstract protected void writePreArray(Object arrayObject, Class<?> fieldType);

    /**
     * Write some post information about the array
     * 
     * @param arrayObject
     * @param fieldType
     */
    abstract protected void writePostArray(Object arrayObject, Class<?> fieldType);

    abstract protected void writeBoolean(boolean value);

    abstract protected void writeShort(short value);

    abstract protected void writeByte(byte value);

    abstract protected void writeInt(int value);

    abstract protected void writeLong(long value);

    abstract protected void writeFloat(float value);

    abstract protected void writeDouble(double value);

    abstract protected void writeUnsignedByte(short value);

    abstract protected void writeUnsignedShort(int value);

    abstract protected void writeUnsignedInt(long value);

    abstract protected void writeUnsignedFloat(double value);

    abstract protected void writeString(String value);

    abstract protected void writeChar(char value);

    abstract protected void writeObject(Object value, String typeName);
}
