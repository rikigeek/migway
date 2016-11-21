package migway.plugins.hla;

import static org.junit.Assert.*;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAvariableArray;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HlaDataReaderTest {
    static final Logger LOG = LoggerFactory.getLogger(HlaDataReaderTest.class);
    FomParser fom;
    EncoderFactory encoderFactory;
    HlaDataReader dr;
    DataElement hlaObject;

    @Before
    public void preTest() throws Exception {
        fom = new FomParser();
        fom.parse(new File("GVA_RPR2-D20_2010.xml"));
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        dr = new HlaDataReader(fom);
    }

    @Test
    public void testMIMBasicData() throws Exception {

        hlaObject = encoderFactory.createHLAfloat32BE((float) 3.14159);
        assertEquals("3.14159", dr.getValue(hlaObject, "HLAfloat32BE").toString());

        hlaObject = encoderFactory.createHLAfloat32LE((float) 3.1415927);
        assertEquals("3.1415927", dr.getValue(hlaObject, "HLAfloat32LE").toString());

        hlaObject = encoderFactory.createHLAfloat64BE(3.14159265358);
        assertEquals("3.14159265358", dr.getValue(hlaObject, "HLAfloat64BE").toString());

        hlaObject = encoderFactory.createHLAfloat64LE(3.14159265358979);
        assertEquals("3.14159265358979", dr.getValue(hlaObject, "HLAfloat64LE").toString());

        hlaObject = encoderFactory.createHLAinteger16BE((short) 42);
        assertEquals("42", dr.getValue(hlaObject, "HLAinteger16BE").toString());

        hlaObject = encoderFactory.createHLAinteger16LE((short) 43);
        assertEquals("43", dr.getValue(hlaObject, "HLAinteger16LE").toString());

        hlaObject = encoderFactory.createHLAinteger32BE(2015);
        assertEquals("2015", dr.getValue(hlaObject, "HLAinteger32BE").toString());

        hlaObject = encoderFactory.createHLAinteger32LE(2016);
        assertEquals("2016", dr.getValue(hlaObject, "HLAinteger32LE").toString());

        hlaObject = encoderFactory.createHLAinteger64BE(21102015);
        assertEquals("21102015", dr.getValue(hlaObject, "HLAinteger64BE").toString());

        hlaObject = encoderFactory.createHLAinteger64LE(8101955);
        assertEquals("8101955", dr.getValue(hlaObject, "HLAinteger64LE").toString());

        hlaObject = encoderFactory.createHLAoctet((byte) 66);
        assertEquals("66", dr.getValue(hlaObject, "HLAoctet").toString());

        hlaObject = encoderFactory.createHLAoctetPairBE((short) 1000);
        assertEquals("1000", dr.getValue(hlaObject, "HLAoctetPairBE").toString());

        hlaObject = encoderFactory.createHLAoctetPairLE((short) 2000);
        assertEquals("2000", dr.getValue(hlaObject, "HLAoctetPairLE").toString());

      boolean thrownedException = false;
        try {
            hlaObject = encoderFactory.createHLAoctetPairBE((short) 1);
            assertEquals("2000", dr.getValue(hlaObject, "HLAunicodeChar").toString());
        } catch (ClassCastException e) {
            thrownedException = true;
        }
        assertTrue("Exception not throwed", thrownedException);


    }

    @Test
    public void testMIMSimpleData() throws Exception {
        hlaObject = encoderFactory.createHLAASCIIchar((byte) 82);
        assertEquals('R', dr.getValue(hlaObject, "HLAASCIIchar"));

        hlaObject = encoderFactory.createHLAbyte((byte) 0x10);
        assertEquals("16", dr.getValue(hlaObject, "HLAbyte").toString());

        hlaObject = encoderFactory.createHLAinteger32BE(12345);
        assertEquals("12345", dr.getValue(hlaObject, "HLAcount").toString());

        hlaObject = encoderFactory.createHLAunicodeChar((short) 'ω');
        assertEquals('ω', dr.getValue(hlaObject, "HLAunicodeChar"));

    }

    @Test
    public void testMIMEnumData() {
        hlaObject = encoderFactory.createHLAboolean(true);
        assertEquals(true, dr.getValue(hlaObject, "HLAboolean"));

        hlaObject = encoderFactory.createHLAboolean(false);
        assertEquals(false, dr.getValue(hlaObject, "HLAboolean"));

        hlaObject = encoderFactory.createHLAinteger32BE(1);
        assertEquals("Enabled", dr.getValue(hlaObject, "HLAswitch"));

    }

    @Test
    public void testMIMArray() {

        hlaObject = encoderFactory.createHLAASCIIstring("Hello world!");
        assertEquals("Hello world!", dr.getValue(hlaObject, "HLAASCIIstring"));
        hlaObject = encoderFactory.createHLAunicodeString("&ιβ");
        assertEquals("&ιβ", dr.getValue(hlaObject, "HLAunicodeString"));
    }

    @Test
    public void testSimpleData() {
        hlaObject = encoderFactory.createHLAopaqueData(new byte[] {(byte) 2} );
        assertEquals("2", dr.getValue(hlaObject, "UnsignedInteger8").toString());
        hlaObject = encoderFactory.createHLAopaqueData(new byte[] {(byte) 0xFF} );
//        assertEquals((short)255, dr.getValue(hlaObject, "UnsignedInteger8"));
        hlaObject = encoderFactory.createHLAopaqueData(new byte[] {(byte) 0x2, (byte) 2} );
        assertEquals("514", dr.getValue(hlaObject, "UnsignedInteger16").toString());
        hlaObject = encoderFactory.createHLAopaqueData(new byte[] {(byte) 0x82, (byte) 2} );
//        assertEquals("33282", dr.getValue(hlaObject, "UnsignedInteger16").toString());
    }
    
    @Test
    public void testEnumData() {
        hlaObject = encoderFactory.createHLAoctet((byte) 2);
        assertEquals("DRM_FPW", dr.getValue(hlaObject, "DeadReckoningAlgorithmEnum8"));

    }

    @Test
    public void testFixedArray() throws Exception {
        HLAoctet[] octetArray = new HLAoctet[4];
        octetArray[0] = encoderFactory.createHLAoctet((byte) 3);
        octetArray[1] = encoderFactory.createHLAoctet((byte) 14);
        octetArray[2] = encoderFactory.createHLAoctet((byte) 15);
        octetArray[3] = encoderFactory.createHLAoctet((byte) 93);

        HLAfixedArray<HLAoctet> hlaFixedOctetArray4 = encoderFactory.createHLAfixedArray(octetArray);
        assertEquals("3141593", dr.getValue(hlaFixedOctetArray4, "OctetArray4"));
    }

    @Test
    public void testVariableArray() throws DecoderException {
        String value = "You rock!";
        HLAASCIIchar[] charArray = new HLAASCIIchar[value.length()];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = encoderFactory.createHLAASCIIchar((byte) value.charAt(i));
        }
        HLAvariableArray<HLAASCIIchar> hlaObject = encoderFactory.createHLAvariableArray(new DataElementFactory<HLAASCIIchar>() {

            @Override
            public HLAASCIIchar createElement(int index) {
                // Don't care about this method. Used only when decoding
                return null;
            }
        }, charArray);
        // hlaObject.
        assertEquals("You rock!", dr.getValue(hlaObject, "String20_T"));

        ByteWrapper bw = new ByteWrapper(new byte[9 + 4]);

        hlaObject.encode(bw);

        LOG.info("Encoded buffer (" + bw.array().length + ") = " + Arrays.toString(bw.array()));

        // Length is array size + 1 word (32bits) to store the length
        assertEquals(bw.array().length, 13);

        // Manually create buffer: first 32bit word is length of array
        // byte[] buf = new byte[value.length()+4];
        // buf[3] = 9;
        // for (int i = 0; i < value.length(); i ++)
        // {
        // buf[i+4] = (byte) value.charAt(i);
        // }
        hlaObject = encoderFactory.createHLAvariableArray(new DataElementFactory<HLAASCIIchar>() {

            @Override
            public HLAASCIIchar createElement(int index) {
                // Don't care about the value. It will be set at decode step
                return encoderFactory.createHLAASCIIchar();
            }

        });
        // use previously encoded Buffer
        hlaObject.decode(bw.array());
        assertEquals("You rock!", dr.getValue(hlaObject, "String20_T"));

    }
}