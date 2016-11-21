package migway.plugins.hla;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAopaqueData;
import hla.rti1516e.exceptions.RTIinternalError;

import org.ieee.standards.ieee1516_2010.BasicDataRepresentationsType.BasicData;
import org.ieee.standards.ieee1516_2010.BasicDataType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType;

public class ExtendedHlaDataReader {
    // TODO remove this suppress warning
    @SuppressWarnings("unused")
    private FomParser fom;
    private EncoderFactory encoderFactory;

    public ExtendedHlaDataReader(FomParser fom) throws RTIinternalError {
        this.fom = fom;
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

    }

    /**
     * Check if the selected value of an enumeration can have another meaning
     * 
     * @param selectedValue
     * @param fomDataType
     * @return null if selectedValue has no specific meaning
     */
    public Object getValue(String selectedValue, EnumeratedDataType fomDataType) {
        if ("RPRboolean".equals(fomDataType.getName().getValue())) {
            if ("True".equals(selectedValue))
                return true;
            if ("False".equals(selectedValue))
                return false;
        }
        return null;
    }

    public boolean setValue(DataElement hlaDataObject, EnumeratedDataType fomDataType, Object value) {
//        if ("RPRboolean".equals(fomDataType.getName().getValue()) {
//            if ("True".equals(selectedValue)) 
//            {
//                setValue()
//            }
//        }
        return false;
    }
    /**
     * TODO doesn't work, remove int sign before shifting. Byte is always
     * signed.
     * 
     * @param hlaObject
     * @param fomDataType
     * @return
     */
    public Object getValue(DataElement hlaObject, BasicDataType fomDataType) {
        if (hlaObject instanceof HLAopaqueData) {
            byte[] hlaData = ((HLAopaqueData) hlaObject).getValue();
            if ("RPRunsignedInteger8BE".equals(fomDataType.getName().getValue())) {
                // Size in bits = 8
                if (hlaData.length != 1)
                    throw new ClassCastException("Cannot cast HLAopaqueData to RPRunsignedInteger8BE");
                return new Short(hlaData[0]);
            }
            if ("RPRunsignedInteger16BE".equals(fomDataType.getName().getValue())) {
                // Size in bits = 8
                if (hlaData.length != 2)
                    throw new ClassCastException("Cannot cast HLAopaqueData to RPRunsignedInteger16BE");
                return new Integer(((short) hlaData[0] << 8) + (short) hlaData[1]);
            }
            if ("RPRunsignedInteger32BE".equals(fomDataType.getName().getValue())) {
                // Size in bits = 8
                if (hlaData.length != 4)
                    throw new ClassCastException("Cannot cast HLAopaqueData to RPRunsignedInteger32BE");
                return new Long(hlaData[0] << 24 + hlaData[1] << 16 + hlaData[2] << 8 + hlaData[3]);
            }
            if ("RPRunsignedInteger64BE".equals(fomDataType.getName().getValue())) {
                // Size in bits = 8
                if (hlaData.length != 4)
                    throw new ClassCastException("Cannot cast HLAopaqueData to RPRunsignedInteger64BE");
                if (hlaData[0] < 0)
                    throw new java.lang.ArithmeticException("HLAopaqueData is too big value.. cannot keep it as unsigned long");
                return new Long(
                        hlaData[0] << 56 + hlaData[1] << 48 + hlaData[2] << 40 + hlaData[3] << 32 + hlaData[4] << 24 + hlaData[5] << 16 + hlaData[6] << 8 + hlaData[7]);
            }

        }
        return null;
    }

    /**
     * Extension mechanism to set a value to HLA non standard object (BasicData specified in FOM, not in MIM)
     * @param hlaObject the HLA object to update
     * @param fomDataType the data type from the FOM
     * @param value the value to assign
     * @return false if nothing has been updated (BasicDataType not recognized)
     */
    public boolean setValue(DataElement hlaObject, BasicDataType fomDataType, Object value) {
        // TODO Could send ClassCastException
        HLAopaqueData hlaOpaqueData = (HLAopaqueData) hlaObject;
        byte[] pojoValue = (byte[]) value;

        // TODO check pojoValue size
        switch (fomDataType.getName().getValue()) {
        case "RPRunsignedInteger8BE":
        case "RPRunsignedInteger16BE":
        case "RPRunsignedInteger32BE":
        case "RPRunsignedInteger64BE":
            hlaOpaqueData.setValue(pojoValue);
            return true;
        }
        // no matching type name. Return false to indicate we failed in setting the value 
        return false;
    }

    public DataElement buildHlaBasicData(BasicData dt) {
        switch (dt.getName().getValue()) {
        case "RPRunsignedInteger8BE":
            return encoderFactory.createHLAopaqueData(new byte[1]);
        case "RPRunsignedInteger16BE":
            return encoderFactory.createHLAopaqueData(new byte[2]);
        case "RPRunsignedInteger32BE":
            return encoderFactory.createHLAopaqueData(new byte[4]);
        case "RPRunsignedInteger64BE":
            return encoderFactory.createHLAopaqueData(new byte[8]);
        }
        return encoderFactory.createHLAopaqueData();
    }

}
