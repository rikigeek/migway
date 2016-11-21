package migway.plugins.hla;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.RTIinternalError;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
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
import migway.core.interfaces.ComponentInterface;

import org.apache.camel.Exchange;
import org.ieee.standards.ieee1516_2010.BasicDataRepresentationsType.BasicData;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType.Enumerator;
import org.ieee.standards.ieee1516_2010.EnumeratedDataTypesType.EnumeratedData;
import org.ieee.standards.ieee1516_2010.VariantRecordDataTypesType.VariantRecordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface with DDSI component.
 * TODO this implementation is not finished. All output are wrong
 * 
 * @author Sébastien Tissier
 *
 */
public class HlaProcessor extends ComponentInterface {
    private static final Logger LOG = LoggerFactory.getLogger(HlaProcessor.class);

    private Map<String, byte[]> message = new HashMap<String, byte[]>();
    private String[] keys;
    private int currentKeyId;
    private String currentKey;
    private byte[] currentBuffer;
    private int currentBufferPosition;

    private EncoderFactory encoderFactory;

    private FomParser fom;

    private void hlaInit() throws Exception {
        try {
            encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        } catch (RTIinternalError e) {
            e.printStackTrace();
        }
        fom = new FomParser();
        fom.parse(new File("GVA_RPR2-D20_2010.xml"));

    }

    public HlaProcessor() throws Exception {
        super();
        hlaInit();
    }

    public HlaProcessor(ConfigHelper helper) throws Exception {
        super(helper);
        hlaInit();
    }

    @Override
    protected String getClassName(Object headerValue) {
        if (!(headerValue instanceof String))
            throw new IllegalArgumentException("Header " + getHeaderClassName() + ": value is not a string ("
                    + headerValue.getClass().getName() + ")");
        // headerValue is a string
        String header = (String) headerValue;
        String className = "";
        className = "edu.cyc14.essais.pojo.rprfom." + header;

        return className;

    };

    /** First call done on Hla Object Class */
    @Override
    protected Object decodeStructure(String structureName) throws ClassNotFoundException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, NoSuchMethodException, java.lang.reflect.InvocationTargetException,
            InstantiationException {
        // Get the POJO Structure
        Structure migPojoStruct = configHelper.getPojoStructure(structureName);
        migPojoStruct.getRemoteName(getInterfaceTypeName());
        // Create the return object
        Object pojo = PojoLoaderHelper.INSTANCE.loadPojo(migPojoStruct.getName());

        // Cycle through the members of the HLA Object Class
        for (int i = 0; i < migPojoStruct.getElementsSize(); i++) {
            Element migElement = migPojoStruct.getElement(i);
            if (message.containsKey(migElement.name())) {
                currentKey = migElement.name();
                currentBuffer = message.get(currentKey);
                Arrays.sort(keys);
                currentKeyId = Arrays.binarySearch(keys, currentKey);
                // decode this attribute array

                Object pojoValue = decodeHlaElement(migElement);
                Field pojoField = helper.getField(migPojoStruct.getName(), migElement.name());
                pojoField.set(pojo, pojoValue);

            }
        }

        return pojo;
    };

    private Object decodeHlaElement(Element migElement) throws ClassNotFoundException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
        LOG.debug("Decoding " + migElement.name());
        ElementType migElementType = migElement.elementType();
        if (migElementType instanceof BasicType) {
            return decodeHlaBasicElement((BasicType) migElementType);
        }
        if (migElementType instanceof ArrayType) {
            return decodeHlaArrayElement((ArrayType) migElementType);
        }
        if (migElementType instanceof StructureRef) {
            return decodeHlaStructureElement((StructureRef) migElementType);
        }

        return null;
    }

    private Object decodeHlaBasicElement(BasicType migBasicType) {
        throw new UnsupportedOperationException("Decoding Basic element is not supported yet");
        // return null;
    }

    private Object decodeHlaArrayElement(ArrayType migArrayType) {
        throw new UnsupportedOperationException("Decoding Basic element is not supported yet");
    }

    private Object decodeHlaStructureElement(StructureRef migStructureType) throws ClassNotFoundException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
        Structure migStructure = configHelper.getPojoStructure(migStructureType.getStructureName());
        if (migStructure instanceof ClassStructure) {
            decodeHlaClassStructure((ClassStructure) migStructure);
        }
        if (migStructure instanceof EnumStructure) {
            decodeHlaEnumStructure((EnumStructure) migStructure);
        }
        if (migStructure instanceof UnionStructure) {
            decodeHlaUnionStructure((UnionStructure) migStructure);
        }
        return null;
    }

    private Object decodeHlaClassStructure(ClassStructure migClassStructure) {
        throw new UnsupportedOperationException("Decoding Basic element is not supported yet");
    }

    private Object decodeHlaEnumStructure(EnumStructure migEnumStructure) {
        throw new UnsupportedOperationException("Decoding Basic element is not supported yet");
    }

    private Object decodeHlaUnionStructure(UnionStructure migUnionStructure) throws ClassNotFoundException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
        try {
            String pojoClassName = migUnionStructure.getName();
            Object pojo = helper.loadPojo(pojoClassName);
            // Name of HLA structure DataType that will be decoded
            String remoteStructureName  = migUnionStructure.getRemoteName(getInterfaceTypeName()); // SpatialVariantStruct
            
            // The FOM Type of the element to decode
            VariantRecordData fomSpatialVariantStruct = (VariantRecordData) fom.getDataTypes(remoteStructureName);
            // The HLA Object that will decode the buffer and contains the values
            DataElement hlaVariant;
            hlaVariant = fom.buildHlaDataElement(remoteStructureName);

            // This element is a Variant
            // FIXME test this cast
            @SuppressWarnings("unchecked")
            HLAvariantRecord<DataElement> hlaVariantRecord = (HLAvariantRecord<DataElement>) hlaVariant;
            // decode the buffer content
            hlaVariantRecord.decode(currentBuffer);

            // From now, we have hlaVariantRecord that contains all correct values.
            // Next step is to copy these values to the POJO
            
            // Get the discriminant
            String fomDiscriminantDataType = fomSpatialVariantStruct.getDataType().getValue(); // DeadReckoningAlgorithmEnum8
            // FOM data type of the discriminant
            EnumeratedData fomEnumeratedData = (EnumeratedData) fom.getDataTypes(fomDiscriminantDataType);

            /** Convert the discriminant value from the encoded value into the value that must be loaded into POJO 
             * This conversion is needed only for EnumeratedValue
             * */
            // Get the value of the discriminant
            //FIXME Don't suppose it's HLAoctet !!!
            HLAoctet hlaDisc = (HLAoctet) hlaVariantRecord.getDiscriminant();
            int value = hlaDisc.getValue();
            
            // 1/ Find the index for this value
            int idx = 0, i = 0;
            for (Enumerator fomEnumerator : fomEnumeratedData.getEnumerator()) {
                LOG.trace("testing enumerator#" + i + " (" + fomEnumerator.getName().getValue() + ")");
                List<org.ieee.standards.ieee1516_2010.String> fomList = fomEnumerator.getValue();
                for (org.ieee.standards.ieee1516_2010.String fomString : fomList) {
                    LOG.trace("value = " + fomString.getValue());
                    if (new Integer(value).toString().equals(fomString.getValue())) {
                        LOG.debug("Found value " + value + " at enumerator#" + i);
                        idx = i;
                    }
                }
                i++;
            }
            // 2/ Find the name for the index
            String pojoDiscriminantValue = fomEnumeratedData.getEnumerator().get(idx).getName().getValue();
            LOG.debug(pojoDiscriminantValue); // DRM_FPW
            String alternativeElementName = migUnionStructure.getAlternative(pojoDiscriminantValue);
            LOG.debug(alternativeElementName); // SpatialFPW

            /** Now we're ready to store the values into the POJO */
            // 1/ Store discriminant
            String pojoDiscriminantFieldName = migUnionStructure.getDiscrimant().name();
            Field pojoDiscriminantField = helper.getField(pojoClassName, pojoDiscriminantFieldName);
            // FIXME should not work: it's an enum
            pojoDiscriminantField.set(pojo, pojoDiscriminantValue); 
            
            LOG.info("Object " + pojoClassName + " affect value "
                    + ((HLAoctet) hlaVariantRecord.getDiscriminant()).getValue() + " to field " + migUnionStructure.getDiscrimant().name());
            String alt = migUnionStructure.getAlternative(fomEnumeratedData.getEnumerator().get(idx).getName().getValue());
            LOG.info("Object " + migUnionStructure.getName() + " current alternative is "
                    + migUnionStructure.getAlternative(fomEnumeratedData.getEnumerator().get(idx).getName().getValue()));
            Element mgwAlternativeField = migUnionStructure.getElement(alt);
//            assertTrue(mgwAlternativeField.elementType() instanceof StructureRef);
            String structureName = ((StructureRef) mgwAlternativeField.elementType()).getStructureName();
            LOG.info("Create new Object " + structureName + " in field " + mgwAlternativeField.name());
        } catch (InconsistentFDD e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /*****************************************************************************/
    /*****************************************************************************/
    /******************** Initialization *********************************/
    /*****************************************************************************/
    /*****************************************************************************/

    @Override
    protected String setHeaderClassName() {
        return "HlaClass";
    }

    @Override
    protected String setInterfaceTypeName() {
        return "HLA";
    }

    @Override
    protected int maxBufferSize(Object pojo) {
        // TODO Auto-generated method stub
        return 5000;
    }

    @Override
    protected boolean isABuffer(Object content) {
        // To be a valid input buffer, content must be a Map<String, byte[]>
        return expectedBodyClass().isInstance(content);
    }

    @Override
    protected Class<?> expectedBodyClass() {
        // Expected type for input buffer is Map<String, byte[]>.
        // I don't know how to be more precise
        return Map.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initializeInputBuffer(Object buffer) {
        if (isABuffer(buffer)) {
            // I didn't find a way to cast without any warning.
            try {
                message = Map.class.cast(buffer);
            } catch (ClassCastException e) {
                return false;
            }

            keys = new String[message.size()];
            // Extract the key list
            keys = message.keySet().toArray(keys);
            //
            currentKeyId = 0;
            //
            currentKey = keys[currentKeyId];
            //
            currentBuffer = message.get(keys[currentKeyId]);
            //
            currentBufferPosition = 0;
        }
        return true;
    }

    @Override
    protected boolean initializeOutputBuffer(Object rootPojo) {
        message = new HashMap<String, byte[]>();
        return true;
    }

    @Override
    protected Object getFinalizedOutputBuffer() {
        return message;
    }

    @Override
    protected boolean validate(Exchange exchange) {
        return true;
    }

    /*******************************************************************************/
    /*******************************************************************************/
    /*******************************************************************************/
    /*******************************************************************************/
    /*******************************************************************************/
    /*******************************************************************************/

    @Override
    protected Object readPreArray(ElementType elementType, Class<?> elementClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object readPostArray(Object arrayObject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean readBoolean() {
        HLAboolean b = encoderFactory.createHLAboolean();
        try {
            b.decode(currentBuffer);
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        currentBufferPosition += b.getEncodedLength();
        if (currentBufferPosition >= currentBuffer.length) {
            // Go to next buffer
            currentBufferPosition = 0;
            currentKeyId++;
            if (currentKeyId > keys.length) {
                // Out of bound
                throw new IndexOutOfBoundsException("No more buffer to decode");
            }
        }
        return b.getValue();
    }

    @Override
    protected byte readByte() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected short readShort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int readInt() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected long readLong() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected float readFloat() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected double readDouble() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected char readChar() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected String readString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected short readUnsignedByte() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int readUnsignedShort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected long readUnsignedInt() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected double readUnsignedFloat() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected Object readObject(String objectType) {
        // TODO Auto-generated method stub
        Object dataType = fom.getDataTypes(objectType);
        if (dataType instanceof BasicData) {
            // Encode BasicData
            BasicData data = (BasicData) dataType;
            switch (data.getName().getValue()) {
            case "HLAfloat32BE":
            case "HLAfloat32LE":
            case "HLAfloat64BE":
            case "HLAfloat64LE":

            case "HLAinteger16BE":
                HLAinteger16BE d = encoderFactory.createHLAinteger16BE();
                try {
                    d.decode(new byte[] { 00, 00 });
                    d.getValue();
                } catch (DecoderException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case "HLAinteger16LE":
            case "HLAinteger32BE":
            case "HLAinteger32LE":
            case "HLAinteger64BE":
            case "HLAinteger64LE":

            case "HLAoctet":
            case "HLAoctetPairBE":
            case "HLAoctetPairLE":

            }
        }

        return null;
    }

    @Override
    protected void writePreArray(Object arrayObject, Class<?> fieldType) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writePostArray(Object arrayObject, Class<?> fieldType) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeBoolean(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeShort(short value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeByte(byte value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeInt(int value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeLong(long value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeFloat(float value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeDouble(double value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeUnsignedByte(short value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeUnsignedShort(int value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeUnsignedInt(long value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeUnsignedFloat(double value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeString(String value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeChar(char value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeObject(Object value, String typeName) {
        // TODO Auto-generated method stub

    }

}
