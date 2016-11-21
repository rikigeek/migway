package migway.plugins.hla;

import org.ieee.standards.ieee1516_2010.ArrayDataType;
import org.ieee.standards.ieee1516_2010.BasicDataType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType.Enumerator;
import org.ieee.standards.ieee1516_2010.FixedRecordDataType;
import org.ieee.standards.ieee1516_2010.SimpleDataType;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType;
import org.ieee.standards.ieee1516_2010.interfaces.IDataType;

import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedArray;
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
import hla.rti1516e.encoding.HLAunicodeChar;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Class used to decode data stored in HLA DataElement object.
 * The way it decodes / encode values depends on the FOM
 * 
 * This Class is running a 'correspondance' between FOM meta-model and the HLA
 * model
 * 
 * @author Sébastien Tissier
 *
 */
public class HlaDataReader {

    FomParser fom;
    ExtendedHlaDataReader extension;

    /**
     * Create a decoder, from a parser
     * The parser is needed to get the DataType of the HLA object
     * 
     * @param fom
     * @throws RTIinternalError
     */
    public HlaDataReader(FomParser fom) throws RTIinternalError {
        this.fom = fom;
        this.extension = new ExtendedHlaDataReader(fom);
    }

    /**
     * Try to convert the HLA stored data into a object.
     * This object will be the right type, as expected by the
     * "MM mapping - Basic Type 'correspondance'"
     * 
     * @param hlaObject
     * @param fomDataTypeName
     * @return
     */
    public Object getValue(DataElement hlaObject, String fomDataTypeName) {
        IDataType fomDataType = fom.getDataTypes(fomDataTypeName);
        if (fomDataType instanceof BasicDataType) {
            return getValue(hlaObject, (BasicDataType) fomDataType);
        }
        if (fomDataType instanceof SimpleDataType) {
            return getValue(hlaObject, (SimpleDataType) fomDataType);
        }
        if (fomDataType instanceof ArrayDataType) {
            return getValue(hlaObject, (ArrayDataType) fomDataType);
        }
        if (fomDataType instanceof EnumeratedDataType) {
            return getValue(hlaObject, (EnumeratedDataType) fomDataType);
        }
        if (fomDataType instanceof FixedRecordDataType) {
            return getValue(hlaObject, (FixedRecordDataType) fomDataType);
        }
        if (fomDataType instanceof VariantRecordDataType) {
            return getValue(hlaObject, (VariantRecordDataType) fomDataType);
        }
        throw new IllegalArgumentException("This object is not a known DataType: " + fomDataType.getClass().getName()
                + " type is not known");
    }

    /**
     * Assign a value to a HLAobject
     * 
     * @param hlaObject
     *            The HLA object to update
     * @param fomDataTypeName
     *            the FOM type of this HLA object
     * @param value
     *            the value to assign
     * @throws InconsistentFDD
     */
    public void setValue(DataElement hlaObject, String fomDataTypeName, Object value) throws InconsistentFDD {
        IDataType fomDataType = fom.getDataTypes(fomDataTypeName);
        if (fomDataType instanceof BasicDataType) {
            setValue(hlaObject, (BasicDataType) fomDataType, value);
            return;
        }
        if (fomDataType instanceof SimpleDataType) {
            setValue(hlaObject, (SimpleDataType) fomDataType, value);
            return;
        }
        if (fomDataType instanceof ArrayDataType) {
            setValue(hlaObject, (ArrayDataType) fomDataType, value);
            return;
        }
        if (fomDataType instanceof EnumeratedDataType) {
            setValue(hlaObject, (EnumeratedDataType) fomDataType, value);
            return;
        }
        if (fomDataType instanceof FixedRecordDataType) {
            setValue(hlaObject, (FixedRecordDataType) fomDataType, value);
            return;
        }
        if (fomDataType instanceof VariantRecordDataType) {
            setValue(hlaObject, (VariantRecordDataType) fomDataType, value);
            return;
        }
        throw new IllegalArgumentException("This object is not a known DataType: " + fomDataType.getClass().getName()
                + " type is not known");
    }

    /**
     * Set the value to a HLA Basic Data Type.
     * 
     * @param hlaObject
     * @param fomDataType
     * @param value
     */
    public void setValue(DataElement hlaObject, BasicDataType fomDataType, Object value) {
        switch (fomDataType.getName().getValue()) {
        case "HLAinteger16BE":
            ((HLAinteger16BE) hlaObject).setValue(((Short) value).shortValue());
            break;
        case "HLAinteger16LE":
            ((HLAinteger16LE) hlaObject).setValue(((Short) value).shortValue());
            break;
        case "HLAinteger32BE":
            ((HLAinteger32BE) hlaObject).setValue(((Integer) value).intValue());
            break;
        case "HLAinteger32LE":
            ((HLAinteger32LE) hlaObject).setValue(((Integer) value).intValue());
            break;
        case "HLAinteger64BE":
            ((HLAinteger64BE) hlaObject).setValue(((Long) value).longValue());
            break;
        case "HLAinteger64LE":
            ((HLAinteger64LE) hlaObject).setValue(((Long) value).longValue());
            break;
        case "HLAfloat32BE":
            ((HLAfloat32BE) hlaObject).setValue(((Float) value).floatValue());
            break;
        case "HLAfloat32LE":
            ((HLAfloat32LE) hlaObject).setValue(((Float) value).floatValue());
            break;
        case "HLAfloat64BE":
            ((HLAfloat64BE) hlaObject).setValue(((Double) value).doubleValue());
            break;
        case "HLAfloat64LE":
            ((HLAfloat64LE) hlaObject).setValue(((Double) value).doubleValue());
            break;
        case "HLAoctet":
//            ((HLAoctet) hlaObject).setValue(((Byte) value).byteValue());
            ((HLAoctet) hlaObject).setValue(Byte.decode(value.toString()));
            break;
        case "HLAoctetPairBE":
            ((HLAoctetPairBE) hlaObject).setValue(((Short) value).shortValue());
            break;
        case "HLAoctetPairLE":
            ((HLAoctetPairLE) hlaObject).setValue(((Short) value).shortValue());
            break;
        default:
            if (extension.setValue(hlaObject, fomDataType, value))
                return;
            throw new UnsupportedOperationException("This basic type (" + fomDataType.getName().getValue() + ") is not supported");
        }
    }

    /**
     * Set the value of the hlaObject with the supplied value
     * 
     * @param hlaObject
     * @param fomDataType
     * @param value
     */
    public void setValue(DataElement hlaObject, SimpleDataType fomDataType, Object value) {
        switch (fomDataType.getName().getValue()) {
        case "HLAASCIIchar":
            ((HLAASCIIchar) hlaObject).setValue((byte) ((Character) value).charValue());
            break;
        case "HLAbyte":
            ((HLAbyte) hlaObject).setValue(((Byte) value).byteValue());
            break;
        case "HLAunicodeChar":
            ((HLAunicodeChar) hlaObject).setValue((short) ((Character) value).charValue());
            break;
        }
        IDataType fomRepresentation = fom.getDataTypes(fomDataType.getRepresentation().getValue());
        if (fomRepresentation instanceof SimpleDataType)
            setValue(hlaObject, (SimpleDataType) fomRepresentation, value);
        else if (fomRepresentation instanceof BasicDataType)
            setValue(hlaObject, (BasicDataType) fomRepresentation, value);
        else throw new UnsupportedOperationException("Representation for Simple type other than Simple and Basic are not supported: "
                + fomRepresentation.getClass().getName() + " not supported as representation");
    }

    public void setValue(DataElement hlaObject, ArrayDataType fomDataType, Object value) throws InconsistentFDD {
        // Particular array: string
        if (fomDataType.getName().getValue().equals("HLAASCIIstring")) {
            ((HLAASCIIstring) hlaObject).setValue(value.toString());
            return;
        }
        if (fomDataType.getName().getValue().equals("HLAunicodeString")) {
            ((HLAunicodeString) hlaObject).setValue(value.toString());
            return;
        }
        // Standard array
        if (hlaObject instanceof HLAvariableArray) {
            // HLAvariableArray
            @SuppressWarnings("unchecked")
            HLAvariableArray<DataElement> hlaArray = (HLAvariableArray<DataElement>) hlaObject;
            Object[] arrayValue = (Object[]) value;
            for (int i = 0; i < arrayValue.length; i++) {
                // First create a new HLA Element
                DataElement hlaElementObject = fom.buildHlaDataElement(fomDataType.getDataType().getValue());
                // Set its value
                setValue(hlaElementObject, fomDataType.getDataType().getValue(), arrayValue[i]);
                // And store it into the HLA Variant object
                hlaArray.addElement(hlaElementObject);
            }
            return;
        }
        if (hlaObject instanceof HLAfixedArray) {
            // HLAfixedArray
            @SuppressWarnings("unchecked")
            HLAfixedArray<DataElement> hlaArray = (HLAfixedArray<DataElement>) hlaObject;
            Object[] arrayValue = (Object[]) value;
            for (int i = 0; i < arrayValue.length; i++) {
                // In fixed array, all HLA Elements are already created. Simple
                // get it and set its value
                setValue(hlaArray.get(i), fomDataType.getDataType().getValue(), arrayValue[i]);
            }
            return;

        }

        return;
    }

    //FIXME finish set Value on enumeratedDataType
    public void setValue(DataElement hlaObject, EnumeratedDataType fomDataType, Object value) throws InconsistentFDD {
        if (fomDataType.getName().getValue().equals("HLAboolean")) {
            ((HLAboolean) hlaObject).setValue((boolean)value);
            return;
        }
        // Conversion of enumerated value
        // 1. Get the representation value
//        String enumValue = getValue(hlaObject, fomDataType.getRepresentation().getValue()).toString();
        String selectedValue = null;
        for (Enumerator enumerator : fomDataType.getEnumerator()) {
            if (!enumerator.getValue().isEmpty()) {
                if (enumerator.getName().getValue().equals(value.toString())) {
                    // Name is found, get its value (the first one
                    selectedValue = enumerator.getValue().get(0).getValue();
                }
            }
        }
        // Now look in extension if this value can have another type
        Object extensionValue = extension.getValue(selectedValue, fomDataType);
        if (extensionValue != null)
            return;
        // Default value assignment, we use representation of the type of the selected value
        setValue(hlaObject, fomDataType.getRepresentation().getValue(), selectedValue);
    }

    /**
     * Assign a value object to a FixedRecord
     * <i>Is it really relevant?</i>
     * @param hlaObject
     * @param fomDataType
     * @param value
     */
    public void setValue(DataElement hlaObject, FixedRecordDataType fomDataType, Object value) {
    }
    
    /**
     * Assign a value object to a VariantRecord
     * <i>Is it really relevant?</i>
     * TODO assign the value to the discriminant
     * @param hlaObject
     * @param fomDataType
     * @param value
     */
    public void setValue(DataElement hlaObject, VariantRecordDataType fomDataType, Object value) {
    }

    /**
     * Convert Basic value to String
     * 
     * TODO HLAoctet is printed as a signed value. Maybe it must be considered
     * as unsigned value
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, BasicDataType fomDataType) {
        switch (fomDataType.getName().getValue()) {
        case "HLAinteger16BE":
            return new Short(((HLAinteger16BE) hlaObject).getValue());
        case "HLAinteger16LE":
            return new Short(((HLAinteger16LE) hlaObject).getValue());
        case "HLAinteger32BE":
            return new Integer(((HLAinteger32BE) hlaObject).getValue());
        case "HLAinteger32LE":
            return new Integer(((HLAinteger32LE) hlaObject).getValue());
        case "HLAinteger64BE":
            return new Long(((HLAinteger64BE) hlaObject).getValue());
        case "HLAinteger64LE":
            return new Long(((HLAinteger64LE) hlaObject).getValue());
        case "HLAfloat32BE":
            return new Float(((HLAfloat32BE) hlaObject).getValue());
        case "HLAfloat32LE":
            return new Float(((HLAfloat32LE) hlaObject).getValue());
        case "HLAfloat64BE":
            return new Double(((HLAfloat64BE) hlaObject).getValue());
        case "HLAfloat64LE":
            return new Double(((HLAfloat64LE) hlaObject).getValue());
        case "HLAoctet":
            return new Byte(((HLAoctet) hlaObject).getValue());
        case "HLAoctetPairBE":
            return new Short(((HLAoctetPairBE) hlaObject).getValue());
        case "HLAoctetPairLE":
            return new Short(((HLAoctetPairLE) hlaObject).getValue());
        default:
            Object ret = extension.getValue(hlaObject, fomDataType);
            if (ret != null)
                return ret;
            throw new UnsupportedOperationException("This basic type (" + fomDataType.getName().getValue() + ") is not supported");
        }
    }

    /**
     * Convert Simple value to String
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, SimpleDataType fomDataType) {
        switch (fomDataType.getName().getValue()) {
        case "HLAASCIIchar":
            return new Character((char) ((HLAASCIIchar) hlaObject).getValue());
        case "HLAbyte":
            return new Byte(((HLAbyte) hlaObject).getValue());
        case "HLAunicodeChar":
            return new Character((char) ((HLAunicodeChar) hlaObject).getValue());
        }
        IDataType fomRepresentation = fom.getDataTypes(fomDataType.getRepresentation().getValue());
        if (fomRepresentation instanceof SimpleDataType)
            return getValue(hlaObject, (SimpleDataType) fomRepresentation);
        if (fomRepresentation instanceof BasicDataType)
            return getValue(hlaObject, (BasicDataType) fomRepresentation);
        throw new UnsupportedOperationException("Representation for Simple type other than Simple and Basic are not supported: "
                + fomRepresentation.getClass().getName() + " not supported as representation");
    }

    /**
     * Convert Array value to String
     * Each array values are appended together, without separator
     * 
     * eg. {'A', 'B', 'C', 'D'} returns "ABCD"
     * eg. {1, 45, 1025} returns "1451025"
     * 
     * So this is not really useful
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public Object getValue(DataElement hlaObject, ArrayDataType fomDataType) {
        if (fomDataType.getName().getValue().equals("HLAASCIIstring"))
            return "" + ((HLAASCIIstring) hlaObject).getValue();
        if (fomDataType.getName().getValue().equals("HLAunicodeString"))
            return "" + ((HLAunicodeString) hlaObject).getValue();

        if (hlaObject instanceof HLAvariableArray) {
            // HLAvariableArray
            HLAvariableArray<DataElement> hlaArray = (HLAvariableArray<DataElement>) hlaObject;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hlaArray.size(); i++) {
                sb.append(getValue(hlaArray.get(i), fomDataType.getDataType().getValue()));
            }
            return sb.toString();
        }
        if (hlaObject instanceof HLAfixedArray) {
            // HLAfixedArray
            HLAfixedArray<DataElement> hlaArray = (HLAfixedArray<DataElement>) hlaObject;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hlaArray.size(); i++) {
                sb.append(getValue(hlaArray.get(i), fomDataType.getDataType().getValue()));
            }
            return sb.toString();

        }

        return "";
    }

    /**
     * Convert a Enumerated value into string
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, EnumeratedDataType fomDataType) {
        if (fomDataType.getName().getValue().equals("HLAboolean"))
            return ((HLAboolean) hlaObject).getValue();

        // Conversion of enumerated value
        // 1. Get the representation value
        String enumValue = getValue(hlaObject, fomDataType.getRepresentation().getValue()).toString();
        String selectedValue = null;
        for (Enumerator enumerator : fomDataType.getEnumerator()) {
            if (!enumerator.getValue().isEmpty()) {
                for (org.ieee.standards.ieee1516_2010.String value : enumerator.getValue()) {
                    if (value.getValue().equals(enumValue)) {
                        // return enumerator.getName().getValue(); // Value is
                        // found
                        selectedValue = enumerator.getName().getValue();
                    }
                }
            }
        }
        // Now look in extension if this value can have another type
        Object extensionValue = extension.getValue(selectedValue, fomDataType);
        if (extensionValue != null)
            return extensionValue;

        return selectedValue;
    }

    /**
     * Convert FixedRecord value to a value object
     * <i>Should it really return something?</i>
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, FixedRecordDataType fomDataType) {
        return null;
    }

    /**
     * Convert VariantRecord value to a value object
     * <i>Should it really return something?</i>
     * TODO return discriminant value
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, VariantRecordDataType fomDataType) {
        return null;
    }

}
