package migway.plugins.hla;

import static org.junit.Assert.*;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAvariableArray;

import java.io.File;
import java.util.Arrays;

import migway.core.samples.HlaSamples;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HlaEncodingTest2 {
    static private Logger LOG = LoggerFactory.getLogger(HlaEncodingTest2.class);
    EncoderFactory encoderFactory;
    FomParser parser;

    @Before
    public void setUp() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("GVA_RPR2-D20_2010.xml"));
        LOG.info("FOM parsed");
        LOG.debug("C'est parti");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHlaArrayDecoding() throws DecoderException {
        byte[] buf = HlaSamples.getHlaBufferCommonCapability_T();
        HLAfixedRecord top = createCommonCapability_T();
        top.decode(buf);
        LOG.debug("" + top);
        assertTrue(top.get(0) instanceof HLAvariableArray<?>);
        HLAvariableArray<HLAASCIIchar> v = (HLAvariableArray<HLAASCIIchar>) top.get(0);
        LOG.debug("manufacturer (" + v.size() + ") = " + convertToString(v));
        assertEquals("", convertToString(v));
        v = (HLAvariableArray<HLAASCIIchar>) top.get(1);
        LOG.debug("productName  (" + v.size() + ") = " + convertToString(v));
        assertEquals("vehicule_2", convertToString(v));
        v = (HLAvariableArray<HLAASCIIchar>) top.get(2);
        LOG.debug("description (" + v.size() + ") = " + convertToString(v));
        assertEquals("1:1:78:1:2:0:0", convertToString(v));

        v = (HLAvariableArray<HLAASCIIchar>) top.get(3);
        LOG.debug("serialNumber (" + v.size() + ") = " + convertToString(v));
        assertEquals("", convertToString(v));

        v = (HLAvariableArray<HLAASCIIchar>) top.get(4);
        LOG.debug("issue (" + v.size() + ") = " + convertToString(v));
        assertEquals("1", convertToString(v));

        v = (HLAvariableArray<HLAASCIIchar>) top.get(5);
        LOG.debug("modStrike (" + v.size() + ") = " + convertToString(v));
        assertEquals("", convertToString(v));

        v = (HLAvariableArray<HLAASCIIchar>) top.get(6);
        LOG.debug("natoStockNumber (" + v.size() + ") = " + convertToString(v));
        assertEquals("", convertToString(v));

        HLAfixedArray<DataElement> f = (HLAfixedArray<DataElement>) top.get(7);
        LOG.debug("softwareVersions (" + f.size() + ") " + (f));
        assertEquals(20, f.size());

        for (int i = 0; i < 20; i++) {
            DataElement e = f.get(i);
            assertTrue(e instanceof HLAfixedRecord);
            assertEquals("", convertToString((HLAvariableArray<HLAASCIIchar>) ((HLAfixedRecord) e).get(0)));
            assertEquals("", convertToString((HLAvariableArray<HLAASCIIchar>) ((HLAfixedRecord) e).get(1)));
        }

        DataElement e = top.get(8);
        assertTrue(e instanceof HLAfixedRecord);
        LOG.debug("supportedModes (" + ((HLAfixedRecord) e).size() + ") " + (e));
        assertTrue(((HLAfixedRecord) e).get(0) instanceof HLAinteger32BE);
        assertEquals("false", getboolean((HLAinteger32BE) ((HLAfixedRecord) e).get(0)));
        // migway.utils.ReflectionUtils.debugReflect(top.get(1));
        // migway.utils.ReflectionUtils.debugReflect(top.get(1).getClass());

        assertTrue(true);
        LOG.debug(Arrays.toString(buf));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHlaArrayEncoding() throws EncoderException {
        ByteWrapper buf = new ByteWrapper(500);
        HLAfixedRecord top = createCommonCapability_T();
        // Assign value to productName
        convertToVariableArray((HLAvariableArray<DataElement>)top.get(1), "vehicule_2");
        top.encode(buf);
        LOG.debug(Arrays.toString(buf.array()));
        
    }

    private void convertToVariableArray(HLAvariableArray<DataElement> hlAvariableArray, String string) {
        for(byte b : string.getBytes()) {
            hlAvariableArray.addElement(encoderFactory.createHLAASCIIchar(b));
        }
        
    }

    private String convertToString(HLAvariableArray<HLAASCIIchar> var) {
        StringBuffer sb = new StringBuffer();
        for (HLAASCIIchar c : var) {
            sb.append((char) c.getValue());
        }
        return sb.toString();
    }




    /** Construction des objets GVA (CommonCapability_T) */
    private HLAfixedRecord createCommonCapability_T() {
        HLAfixedRecord data = encoderFactory.createHLAfixedRecord();
        data.add(createString50_T());   // manufacturer
        data.add(createString50_T());   // productName
        data.add(createString200_T());  // description
        data.add(createString50_T());   // serialNumber
        data.add(createString50_T());   // issue
        data.add(createString50_T());   // modStrike
        data.add(createString20_T());   // natoStockNumber
        data.add(createSequenceOfSoftwareVersionDescriptor_T()); // softwareVersions
        data.add(createModeCapability_T()); // supportedModes
        data.add(createVsiTime_T());        // timeOfDataGeneration
        return data;
    }

    private HLAvariableArray<HLAASCIIchar> createString50_T() {
        HLAvariableArray<HLAASCIIchar> data = encoderFactory.createHLAvariableArray(new DataElementFactory<HLAASCIIchar>() {

            @Override
            public HLAASCIIchar createElement(int index) {
                // TODO Auto-generated method stub
                return encoderFactory.createHLAASCIIchar();
            }
        });
        return data;
    }

    private HLAvariableArray<HLAASCIIchar> createString200_T() {
        return createString50_T();
    }

    private HLAvariableArray<HLAASCIIchar> createString20_T() {
        return createString50_T();
    }

    private HLAinteger32BE createboolean() {
        HLAinteger32BE data = encoderFactory.createHLAinteger32BE();
        return data;
    }

    @SuppressWarnings("unused")
    private HLAinteger32BE setboolean(HLAinteger32BE data, String enumerator) {
        if ("false".equals(enumerator))
            data.setValue(0);
        if ("true".equals(enumerator))
            data.setValue(1);
        return data;
    }

    private String getboolean(HLAinteger32BE data) {
        if (data.getValue() == 0)
            return "false";
        if (data.getValue() == 1)
            return "true";
        return "";
    }

    private HLAinteger64BE createlonglong() {
        return encoderFactory.createHLAinteger64BE();
    }

    private HLAinteger32BE createunsignedlong() {
        return encoderFactory.createHLAinteger32BE();
    }

    private HLAfixedArray<HLAfixedRecord> createSequenceOfSoftwareVersionDescriptor_T() {
        HLAfixedRecord[] elements = new HLAfixedRecord[20];
        for (int i = 0; i < 20; i++) {
            elements[i] = createSoftwareVersionDescriptor_T();
        }
        HLAfixedArray<HLAfixedRecord> data = encoderFactory.createHLAfixedArray(elements);
        return data;
    }

    private HLAfixedRecord createSoftwareVersionDescriptor_T() {
        HLAfixedRecord data = encoderFactory.createHLAfixedRecord();
        data.add(createString50_T()); // softwareModuleName
        data.add(createString50_T()); // versionNumber
        return data;
    }

    private HLAfixedRecord createModeCapability_T() {
        HLAfixedRecord data = encoderFactory.createHLAfixedRecord();
        data.add(createboolean()); // isOffCapable
        data.add(createboolean()); // isOnCapab
        data.add(createboolean()); // isStandbyCapable
        data.add(createboolean()); // isMaintenanceCapable
        data.add(createboolean()); // isTrainingCapable
        return data;
    }

    private HLAfixedRecord createVsiTime_T() {
        HLAfixedRecord data = encoderFactory.createHLAfixedRecord();
        data.add(createlonglong()); // seconds
        data.add(createunsignedlong()); // nanoseconds
        return data;
    }

}
