package migway.plugins.hla;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat32LE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAfloat64LE;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger16LE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAinteger64LE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAoctetPairBE;
import hla.rti1516e.encoding.HLAoctetPairLE;
import hla.rti1516e.encoding.HLAopaqueData;
import hla.rti1516e.encoding.HLAunicodeChar;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.RTIinternalError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.ieee.standards.ieee1516_2010.ArrayDataTypesType.ArrayData;
import org.ieee.standards.ieee1516_2010.BasicDataRepresentationsType.BasicData;
import org.ieee.standards.ieee1516_2010.DataTypesType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType.Enumerator;
import org.ieee.standards.ieee1516_2010.EnumeratedDataTypesType.EnumeratedData;
import org.ieee.standards.ieee1516_2010.FixedRecordDataType.Field;
import org.ieee.standards.ieee1516_2010.FixedRecordDataTypesType.FixedRecordData;
import org.ieee.standards.ieee1516_2010.InteractionClass;
import org.ieee.standards.ieee1516_2010.ObjectClass;
import org.ieee.standards.ieee1516_2010.ObjectModelType;
import org.ieee.standards.ieee1516_2010.SimpleDataTypesType.SimpleData;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType.Alternative;
import org.ieee.standards.ieee1516_2010.VariantRecordDataTypesType.VariantRecordData;
import org.ieee.standards.ieee1516_2010.interfaces.IDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to access to the FOM
 * 
 * Also contains some methods to build HLA object ({@link DataElement})
 * according to the FOM structure.
 * 
 * Usage: create a FomParser, and then call parse method with the FOM file in
 * argument <code>
 * FomParser fom = new FomParser();
 * fom.parse(new File("RPR-D20_2010"));
 * 
 * DataElement hlaElement = fom.buildHlaDataElement("SpatialVariantStruct");
 * </code>
 * 
 * @author Sébastien Tissier
 *
 */
public class FomParser {
    private static Logger LOG = LoggerFactory.getLogger(FomParser.class);
    private EncoderFactory encoderFactory;

    private Unmarshaller unmarshaller;
    private ObjectModelType omt;
    private ObjectModelType mim;
    private ExtendedHlaDataReader extendedHlaDataReader;

    /**
     * Create the parser.
     * It depends on the encoderFactory of the HLA RTI.
     * It also loads ExtendedHlaDataReader (used to implement personal Basic
     * Types)
     * 
     * @throws JAXBException
     *             if parser creation failed
     * @throws RTIinternalError
     *             if HLA factory failed
     * @throws FileNotFoundException
     *             if MIM file not found
     */
    public FomParser() throws Exception {
        JAXBContext jc;
        jc = JAXBContext.newInstance("org.ieee.standards.ieee1516_2010");
        unmarshaller = jc.createUnmarshaller();

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        extendedHlaDataReader = new ExtendedHlaDataReader(this);
        // fomFactory = new ObjectFactory();
        parseMIM();
    }

    @SuppressWarnings("unchecked")
    public ObjectModelType parseMIM() throws FileNotFoundException, JAXBException {
        InputStream mim = this.getClass().getResourceAsStream("/HLAstandardMIM.xml");
        Object mimElement = unmarshaller.unmarshal(mim);
        ObjectModelType mimOmt = null;
        if (mimElement instanceof JAXBElement<?>) {
            JAXBElement<?> e = (JAXBElement<?>) mimElement;
            if (e.getDeclaredType().equals(ObjectModelType.class))
                // No way to remove this "unchecked" warning?
                mimOmt = ((JAXBElement<ObjectModelType>) mimElement).getValue();
        }
        this.mim = mimOmt;
        return mimOmt;
    }

    /**
     * Parse a FOM file. This FOM file must be from namespace
     * http://standards.ieee.org/IEEE1516-2010
     * 
     * @param file
     * @return null value if something else went wrong
     * @throws FileNotFoundException
     *             if FOM file can't be read
     * @throws JAXBException
     *             if FOM file structure is not correct
     */
    @SuppressWarnings("unchecked")
    public ObjectModelType parse(File file) throws FileNotFoundException, JAXBException {
        Object omtElement = unmarshaller.unmarshal(new FileInputStream(file));
        ObjectModelType omt = null;
        // Check returned value from unmarshaling
        if (omtElement instanceof JAXBElement<?>) {
            JAXBElement<?> e = (JAXBElement<?>) omtElement;
            if (e.getDeclaredType().equals(ObjectModelType.class))
                // No way to remove this "unchecked" warning?
                omt = ((JAXBElement<ObjectModelType>) omtElement).getValue();
        }
        // is null if something went wrong
        this.omt = omt;
        return omt;
    }

    /**
     * Find an ObjectClass with its name.
     * 
     * @param name
     * @return
     */
    public ObjectClass findObjectClass(String name) {
        return objectClassLookup(omt.getObjects().getObjectClass(), name);
    }

    public InteractionClass findInteractionClass(String name) {
        return interactionClassLookup(omt.getInteractions().getInteractionClass(), name);
    }

    /**
     * Find an enumerate value from the name of the enumerate and the name of
     * the enumerationDataType name
     * 
     * @param enumerationName
     *            name of the data type
     * @param enumerationValue
     *            name of the value
     * @return list of values linked to the name
     */
    public List<org.ieee.standards.ieee1516_2010.String> getEnumeratedDataValue(String enumerationName, String enumerationValue) {
        for (EnumeratedData d : omt.getDataTypes().getEnumeratedDataTypes().getEnumeratedData()) {
            if (d.getName().getValue().equals(enumerationName)) {
                // Found the right enumeration
                // Now lookup for the enumerator value
                for (Enumerator e : d.getEnumerator()) {
                    if (e.getName().getValue().equals(enumerationValue))
                        return e.getValue();
                }
            }
        }

        return null;

    }

    /***
     * Get the FOM DataType by its name.
     * It first looks into FOM. If nothing is found, it then looks into MIM.
     * Finally, if this DataType is not found, it returns null
     * 
     * @param name
     *            name of the DataType, as it appears in FOM/MIM. Example:
     *            "HLAoctet"
     * @return
     */
    public IDataType getDataTypes(String name) {
        if (name == null)
            return null; // Name must not be null
        DataTypesType dtt = omt.getDataTypes();
        for (BasicData d : dtt.getBasicDataRepresentations().getBasicData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (SimpleData d : dtt.getSimpleDataTypes().getSimpleData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (EnumeratedData d : dtt.getEnumeratedDataTypes().getEnumeratedData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (ArrayData d : dtt.getArrayDataTypes().getArrayData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (FixedRecordData d : dtt.getFixedRecordDataTypes().getFixedRecordData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (VariantRecordData d : dtt.getVariantRecordDataTypes().getVariantRecordData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        // Do this check from the MIM
        dtt = mim.getDataTypes();
        for (BasicData d : dtt.getBasicDataRepresentations().getBasicData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (SimpleData d : dtt.getSimpleDataTypes().getSimpleData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (EnumeratedData d : dtt.getEnumeratedDataTypes().getEnumeratedData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (ArrayData d : dtt.getArrayDataTypes().getArrayData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (FixedRecordData d : dtt.getFixedRecordDataTypes().getFixedRecordData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        for (VariantRecordData d : dtt.getVariantRecordDataTypes().getVariantRecordData()) {
            if (name.equals(d.getName().getValue()))
                return d;
        }
        return null;
    }

    private InteractionClass interactionClassLookup(InteractionClass obj, String name) {
        InteractionClass retVal = null;

        if (obj.getInteractionClass().isEmpty())
            return null; // not found
        if (name == null)
            return null; // Invalid argument
        if (name.equalsIgnoreCase(obj.getName().getValue())) {
            return obj;
        }
        for (InteractionClass cur : obj.getInteractionClass()) {
            if (cur != null) {
                if (name.equalsIgnoreCase(cur.getName().getValue()))
                    return cur;
                retVal = interactionClassLookup(cur, name);
                if (retVal != null)
                    return retVal;
            }
        }
        return retVal;
    }

    private ObjectClass objectClassLookup(ObjectClass obj, String name) {
        ObjectClass retVal = null;

        if (obj.getObjectClass().isEmpty())
            return null; // not found
        if (name == null)
            return null; // invalid argument
        if (name.equalsIgnoreCase(obj.getName().getValue())) {
            return obj;
        }
        for (ObjectClass cur : obj.getObjectClass()) {
            if (cur != null) {
                if (name.equalsIgnoreCase(cur.getName().getValue()))
                    return cur;
                retVal = objectClassLookup(cur, name);
                if (retVal != null)
                    return retVal;
            }
        }

        return retVal;

    }

    private DataElement buildHlaBasicData(BasicData dt) {
        switch (dt.getName().getValue()) {
        case "HLAfloat32BE":
            return encoderFactory.createHLAfloat32BE();
        case "HLAfloat32LE":
            return encoderFactory.createHLAfloat32LE();
        case "HLAfloat64BE":
            return encoderFactory.createHLAfloat64BE();
        case "HLAfloat64LE":
            return encoderFactory.createHLAfloat64LE();

        case "HLAinteger16BE":
            return encoderFactory.createHLAinteger16BE();
        case "HLAinteger16LE":
            return encoderFactory.createHLAinteger16LE();
        case "HLAinteger32BE":
            return encoderFactory.createHLAinteger32BE();
        case "HLAinteger32LE":
            return encoderFactory.createHLAinteger32LE();
        case "HLAinteger64BE":
            return encoderFactory.createHLAinteger64BE();
        case "HLAinteger64LE":
            return encoderFactory.createHLAinteger64LE();

        case "HLAoctet":
            return encoderFactory.createHLAoctet();
        case "HLAoctetPairBE":
            return encoderFactory.createHLAoctetPairBE();
        case "HLAoctetPairLE":
            return encoderFactory.createHLAoctetPairLE();
        }
        return extendedHlaDataReader.buildHlaBasicData(dt);
    }

    private DataElement buildHlaSimpleData(SimpleData dt) throws InconsistentFDD {
        switch (dt.getName().getValue()) {
        case "HLAASCIIchar":
            return encoderFactory.createHLAASCIIchar();
        case "HLAunicodeChar":
            return encoderFactory.createHLAunicodeChar();
        case "HLAbyte":
            return encoderFactory.createHLAunicodeChar();
        }
        return buildHlaDataElement(dt.getRepresentation().getValue());
    }

    private DataElement buildHlaEnumeratedData(EnumeratedData dt) throws InconsistentFDD {
        EnumeratedData ed = (EnumeratedData) dt;
        // create only its representation
        DataElement de = buildHlaDataElement(ed.getRepresentation().getValue());
        return de;

    }

    private DataElement buildHlaArrayData(ArrayData dt) throws InconsistentFDD {
        // dt.getEncoding() can be either HLAvariableArray, either
        // HLAfixedArray, either something else
        // ... (in example RPRlengthlessArray
        // used in RPR FOM)
        boolean isfixed = false;
        if ("HLAfixedArray".equals(dt.getEncoding().getValue()))
            isfixed = true;
        if (isfixed) {
            // IF FIXED Array
            // build an array with x DataElement
            DataElement[] elementList = new DataElement[10];
            for (int i = 0; i < 10; i++) {
                elementList[i] = buildHlaDataElement(dt.getDataType().getValue());
            }
            HLAfixedArray<DataElement> fixedArray = encoderFactory.createHLAfixedArray(elementList);
            return fixedArray;
        } else {
            HLAvariableArray<DataElement> variableArray = encoderFactory.createHLAvariableArray(new DataElementFactory<DataElement>() {

                @Override
                public DataElement createElement(int index) {
                    DataElement createdValue = null;
                    try {
                        createdValue = buildHlaDataElement(dt.getDataType().getValue());
                    } catch (InconsistentFDD e) {
                        LOG.error("HLAvariableArray - DataElementFactory. Unable to build this element: " + dt.getDataType().getValue());
                        createdValue = encoderFactory.createHLAoctet();
                    }
                    return createdValue;
                }
            });
            return variableArray;
        }
    }

    private DataElement buildHlaFixedRecordData(FixedRecordData dt) throws InconsistentFDD {
        FixedRecordData frd = (FixedRecordData) dt;
        // Fixed Record
        HLAfixedRecord hfr = encoderFactory.createHLAfixedRecord();
        for (Field field : frd.getField()) {
            hfr.add(buildHlaDataElement(getDataTypes(field.getDataType().getValue())));
        }
        return hfr;
    }

    private DataElement buildHlaVariantRecordData(VariantRecordData dt) throws InconsistentFDD {
        VariantRecordData vrd = (VariantRecordData) dt;
        // Variant
        // 1/ Get discriminant
        DataElement disc = buildHlaDataElement(getDataTypes(vrd.getDataType().getValue()));

        HLAvariantRecord<DataElement> hvr = encoderFactory.createHLAvariantRecord(disc);

        // For each alternative, build it
        //
        for (Alternative alt : vrd.getAlternative()) {
            DataElement altdt = buildHlaDataElement(getDataTypes(alt.getDataType().getValue()));
            // The data type is created.
            // First find the value of the enumerator
            List<org.ieee.standards.ieee1516_2010.String> values = getEnumeratedDataValue(vrd.getDataType().getValue(), alt.getEnumerator()
                    .getValue());
            DataElement discValue = buildHlaDataElement(getDataTypes(vrd.getDataType().getValue()));

            if (values.size() >= 1) {
                // Easy
                // FIXME don't ignore other values !!
                setElementValue(discValue, values.get(0).getValue());
            }
            if (values.size() == 0) {
                // Error, no values in FOM
                throw new InconsistentFDD("EnumeratedData " + vrd.getDataType().getValue() + " doesn't get any value for enumeration "
                        + alt.getEnumerator().getValue());
            }
            // And now add it as an alternative
            hvr.setVariant(discValue, altdt);

        }
        return hvr;
    }

    /**
     * Build a HLA DataElement object that follow the structure of the DataType
     * whose name is in argument.
     * DataType is found in the FOM (or the MIM)
     * 
     * @param dataType
     *            name of the DataType as it appears in FOM/MIM
     * @return a HLA object ready to be used with encode/decode method
     * @throws InconsistentFDD
     *             if
     */
    public DataElement buildHlaDataElement(String dataType) throws InconsistentFDD {
        IDataType fomDataType = getDataTypes(dataType);
        // If fomDataType is null, either it's a basic element, either it's not
        // in the
        // fom
        if (fomDataType == null) {
            LOG.error("" + dataType + " is not found in FOM");
            return null;
        } else {
            return buildHlaDataElement(fomDataType);
        }
    }

    /**
     * Build a HLA DataElement object that follows the structure of the DataType
     * in argument.
     * DataType was previously found in the FOM (or the MIM), as it's returned
     * by {@link getDataTypes}
     * 
     * @param dt
     * @return
     * @throws InconsistentFDD
     */
    public DataElement buildHlaDataElement(IDataType dt) throws InconsistentFDD {
        if (dt instanceof BasicData) {
            return buildHlaBasicData((BasicData) dt);
        }
        if (dt instanceof SimpleData) {
            return buildHlaSimpleData((SimpleData) dt);
        }
        // Create a enumeratedData
        if (dt instanceof EnumeratedData) {
            return buildHlaEnumeratedData((EnumeratedData) dt);
        }
        // Create an array data
        if (dt instanceof ArrayData) {
            return buildHlaArrayData((ArrayData) dt);
        }
        if (dt instanceof FixedRecordData) {
            return buildHlaFixedRecordData((FixedRecordData) dt);
        }
        if (dt instanceof VariantRecordData) {
            return buildHlaVariantRecordData((VariantRecordData) dt);
        }
        return encoderFactory.createHLAoctet(); // Default is to create an octet
    }

    /**
     * Affect a value to a HLA structure object. Redundant with HlaDataReader
     * object.
     * Is used by {@link buildHlaVariantRecord} method
     * 
     * @param element
     * @param value
     * @return
     * @throws InconsistentFDD
     */
    private DataElement setElementValue(DataElement element, String value) throws InconsistentFDD {
        if (element instanceof HLAinteger16BE) {
            ((HLAinteger16BE) element).setValue(Short.parseShort(value));
        }
        if (element instanceof HLAinteger16LE) {
            ((HLAinteger16LE) element).setValue(Short.parseShort(value));
        }
        if (element instanceof HLAinteger32BE) {
            ((HLAinteger32BE) element).setValue(Integer.parseInt(value));
        }
        if (element instanceof HLAinteger32LE) {
            ((HLAinteger32LE) element).setValue(Integer.parseInt(value));
        }
        if (element instanceof HLAinteger64BE) {
            ((HLAinteger64BE) element).setValue(Long.parseLong(value));
        }
        if (element instanceof HLAinteger64LE) {
            ((HLAinteger64LE) element).setValue(Long.parseLong(value));
        }
        if (element instanceof HLAfloat32BE) {
            ((HLAfloat32BE) element).setValue(Float.parseFloat(value));
        }
        if (element instanceof HLAfloat32LE) {
            ((HLAfloat32LE) element).setValue(Float.parseFloat(value));
        }
        if (element instanceof HLAfloat64BE) {
            ((HLAfloat64BE) element).setValue(Float.parseFloat(value));
        }
        if (element instanceof HLAfloat64LE) {
            ((HLAfloat64LE) element).setValue(Float.parseFloat(value));
        }
        if (element instanceof HLAoctet) {
            ((HLAoctet) element).setValue(Byte.parseByte(value));
        }
        if (element instanceof HLAbyte) {
            ((HLAbyte) element).setValue(Byte.parseByte(value));
        }
        if (element instanceof HLAoctetPairBE) {
            ((HLAoctetPairBE) element).setValue(Short.parseShort(value));
        }
        if (element instanceof HLAoctetPairLE) {
            ((HLAoctetPairLE) element).setValue(Short.parseShort(value));
        }
        if (element instanceof HLAASCIIchar) {
            try {
                byte[] bytes = value.getBytes("US-ASCII");
                if (bytes.length > 0) {
                    ((HLAASCIIchar) element).setValue(bytes[0]);
                } else {
                    // Error
                    throw new InconsistentFDD("Value " + value + " is not an ASCII char");
                }
            } catch (UnsupportedEncodingException e) {
                // throw another exception: input format is incorrect
                throw new InconsistentFDD("Value " + value + " is not encoded as an ASCII char", e);
            }
        }
        if (element instanceof HLAASCIIstring) {
            ((HLAASCIIstring) element).setValue(value);
        }
        if (element instanceof HLAboolean) {
            ((HLAboolean) element).setValue(Boolean.parseBoolean(value));
        }
        if (element instanceof HLAopaqueData) {
            ((HLAopaqueData) element).setValue(value.getBytes());
        }
        if (element instanceof HLAunicodeChar) {
            ((HLAunicodeChar) element).setValue((short) value.codePointAt(0));
        }
        if (element instanceof HLAunicodeString) {
            ((HLAunicodeString) element).setValue(value);
        }
        // Error
        if (element instanceof HLAfixedRecord) {
            throw new UnsupportedOperationException("Unable to assign " + value + " to a HLAfixedRecord");
        }
        if (element instanceof HLAfixedArray<?>) {
            throw new UnsupportedOperationException("Unable to assign " + value + " to a HLAfixedArray");
        }
        if (element instanceof HLAvariantRecord<?>) {
            throw new UnsupportedOperationException("Unable to assign " + value + " to a HLAvariantRecord");
        }
        if (element instanceof HLAvariableArray<?>) {
            throw new UnsupportedOperationException("Unable to assign " + value + " to a HLAvariableArray");
        }
        return element;
    }

    public IDataType getBasicDataRepresentation(IDataType disc) {
        // TODO Auto-generated method stub
        return disc;
    }

}
