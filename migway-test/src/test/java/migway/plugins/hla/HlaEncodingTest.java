package migway.plugins.hla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.RTIinternalError;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import migway.core.samples.HlaSamples;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Construction manuelle du type SpatialVariantStruct de la RPR-FOM.
 * 
 * L'objet permet ensuite l'encodage et le décodage vers et depuis un byte[]
 * 
 * @author Sébastien Tissier
 *
 */
public class HlaEncodingTest {

    private Logger LOG = LoggerFactory.getLogger(HlaEncodingTest.class);
    EncoderFactory encoderFactory;
    FomParser parser;

    @Before
    public void initTest() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("RPR2-D20_2010.xml"));
        LOG.info("FOM parsed");
        LOG.debug("C'est parti");
    }

    @Test
    public void testHlaEncoderFactory() throws FileNotFoundException, JAXBException, RTIinternalError, DecoderException {
        HLAbyte b = encoderFactory.createHLAbyte((byte) 88);
        DataElement el = encoderFactory.createHLAASCIIchar((byte) 'C');
        LOG.debug("" + Arrays.toString(el.toByteArray()) + " / " + Arrays.toString(b.toByteArray()));

        ByteWrapper bw = new ByteWrapper(50);
        LOG.debug(Arrays.toString(bw.array()));
        el.encode(bw);
        b.encode(bw);
        el = encoderFactory.createHLAfixedArray(encoderFactory.createHLAbyte((byte) 70), encoderFactory.createHLAbyte((byte) 71));
        el.encode(bw);
        b.encode(bw);

        el = encoderFactory.createHLAvariableArray(new DataElementFactory<DataElement>() {

            @Override
            public DataElement createElement(int index) {
                return encoderFactory.createHLAASCIIchar((byte) 50);
            }
        }, encoderFactory.createHLAASCIIchar((byte) 01));
        el.encode(bw);

        b.encode(bw);

        DataElement[] list = new DataElement[10];
        for (int i = 0; i < 10; i++) {
            list[i] = encoderFactory.createHLAbyte((byte) (i + 50));
        }
        el = encoderFactory.createHLAvariableArray(new DataElementFactory<DataElement>() {

            @Override
            public DataElement createElement(int index) {
                return encoderFactory.createHLAASCIIchar((byte) 50);
            }
        }, list);
        el.encode(bw);

        encoderFactory.createHLAbyte((byte) 99).encode(bw);

        LOG.debug(Arrays.toString(bw.array()));
    }

    /**
     * Update:
     * 'HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle12117'
     * of class 'BaseEntity.PhysicalEntity.Platform.GroundVehicle':
     * Spatial = [02000000 00000000 415096AC E7ED7B22 4106A032 A99C35CF
     * 4151B9DE 73F29B84 00000000 402A9865 BF2A6AAA C032DA86 80000000
     * 00000000 00000000]
     * User-supplied Tag: [34383841 45363631]
     * Producing Federate: Converter
     * 
     * 
     * Update:
     * 'HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle12117'
     * of class 'BaseEntity.PhysicalEntity.Platform.GroundVehicle':
     * Spatial = (DRM_FPW: SpatialFPW = {{X:4348595.623869689,
     * Y:185350.33281747854, Z:4646777.811682586 (47.06459 N, 2.44064 E,
     * 162.29)}, False, {2.6655514, -0.66569006, -2.7945876}, {-0.0, 0.0,
     * 0.0}})
     * User-supplied Tag: 17:00.130,000
     * Producing Federate: Converter
     */
    @Test
    public void testEncodeVariant() throws EncoderException {
        HLAvariantRecord<HLAoctet> top = createSpatialVariantStruct();
        LOG.debug("Variant is " + top.toString());
        assertNull(top.getValue());
        HLAoctet disc = createDeadReckoningAlgorithmEnum8();
        top.getDiscriminant().setValue(setDeadReckoningAlgorithmEnum8(disc, "Static").getValue());
        assertEquals(1, top.getDiscriminant().getValue());
        LOG.debug("Variant is " + top.toString());
        assertTrue(top.getValue() instanceof HLAfixedRecord);
        assertEquals(3, ((HLAfixedRecord) top.getValue()).size());

    }

    @Test
    public void testDecodeVariant() throws DecoderException {
        /*
         * Update:
         * 'HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle12117'
         * of class 'BaseEntity.PhysicalEntity.Platform.GroundVehicle':
         * Spatial = [02000000 00000000 415096AC E7ED7B22 4106A032 A99C35CF
         * 4151B9DE 73F29B84 00000000 402A9865 BF2A6AAA C032DA86 80000000
         * 00000000 00000000]
         * User-supplied Tag: [34383841 45363631]
         * Producing Federate: Converter
         * 
         * 
         * Update:
         * 'HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle12117'
         * of class 'BaseEntity.PhysicalEntity.Platform.GroundVehicle':
         * Spatial = (DRM_FPW: SpatialFPW = {{X:4348595.623869689,
         * Y:185350.33281747854, Z:4646777.811682586 (47.06459 N, 2.44064 E,
         * 162.29)}, False, {2.6655514, -0.66569006, -2.7945876}, {-0.0, 0.0,
         * 0.0}})
         * User-supplied Tag: 17:00.130,000
         * Producing Federate: Converter
         */
        short[] array = new short[] { 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x41, 0x50, 0x96, 0xAC, 0xE7, 0xED, 0x7B, 0x22, 0x41,
                0x06, 0xA0, 0x32, 0xA9, 0x9C, 0x35, 0xCF, 0x41, 0x51, 0xB9, 0xDE, 0x73, 0xF2, 0x9B, 0x84, 0x00, 0x00, 0x00, 0x00, 0x40,
                0x2A, 0x98, 0x65, 0xBF, 0x2A, 0x6A, 0xAA, 0xC0, 0x32, 0xDA, 0x86, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00 };
        byte[] buf = new byte[array.length];

        buf = HlaSamples.getHlaBufferSpatialVariantStruct();

        for (int i = 0; i < array.length; i++) {
            buf[i] = (byte) array[i];
        }
        LOG.debug(Arrays.toString(buf));
        HLAfixedRecord cur;
        HLAvariantRecord<HLAoctet> top = createSpatialVariantStruct();
        top.decode(buf);
        LOG.debug(top.getValue().toString());
        LOG.debug(top.toString());
        assertTrue(top.getValue() instanceof HLAfixedRecord);
        assertEquals(2, top.getDiscriminant().getValue()); // Discrimant :
                                                           // DeadReckoningAlgorithm
        cur = (HLAfixedRecord) top.getValue();
        assertEquals(4, cur.size());
        assertTrue(cur.get(0) instanceof HLAfixedRecord);
        assertEquals(4348595.623869689, ((HLAfloat64BE) ((HLAfixedRecord) cur.get(0)).get(0)).getValue(), 0.0); // X
        assertEquals(185350.33281747854, ((HLAfloat64BE) ((HLAfixedRecord) cur.get(0)).get(1)).getValue(), 0.0); // Y
        assertEquals(4646777.811682586, ((HLAfloat64BE) ((HLAfixedRecord) cur.get(0)).get(2)).getValue(), 0.0); // Z
        assertTrue(cur.get(1) instanceof HLAoctet); // IsFrozen
        assertEquals("False", getRPRboolean((HLAoctet) cur.get(1)));
        /*
         * Orientation
         * 2.6655514, -0.66569006, -2.7945876
         */
        assertTrue(cur.get(2) instanceof HLAfixedRecord);
        assertEquals(2.6655514, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(2))).get(0))).getValue(), 0.0000001);
        assertEquals(-0.66569006, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(2))).get(1))).getValue(), 0.0000001);
        assertEquals(-2.7945876, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(2))).get(2))).getValue(), 0.0000001);
        /*
         * Velocity
         * -0.0, 0.0,
         * 0.0
         */
        assertTrue(cur.get(3) instanceof HLAfixedRecord);
        assertEquals(0.0, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(3))).get(0))).getValue(), 0.0000001);
        assertEquals(0.0, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(3))).get(1))).getValue(), 0.0000001);
        assertEquals(0.0, ((HLAfloat32BE) (((HLAfixedRecord) (cur.get(3))).get(2))).getValue(), 0.0000001);

    }

    HLAvariantRecord<HLAoctet> createSpatialVariantStruct() {
        HLAoctet disc;
        DataElement data;
        disc = createDeadReckoningAlgorithmEnum8();
        LOG.debug("Creation of SpatialVariantStruct with Static (" + disc.getValue() + ") as init discrimant");
        HLAvariantRecord<HLAoctet> top = encoderFactory.createHLAvariantRecord(disc);
        data = createSpatialStaticStruct();
        disc = createDeadReckoningAlgorithmEnum8("Static");
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_FPW");
        data = createSpatialFPStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_RPW");
        data = createSpatialRPStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_RVW");
        data = createSpatialRVStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_FVW");
        data = createSpatialFVStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_FPB");
        data = createSpatialFPStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_RPB");
        data = createSpatialRPStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_RVB");
        data = createSpatialRVStruct(); //
        LOG.debug("Algo = " + disc.getValue() + " - Struct = " + data);
        top.setVariant(disc, data); // SpatialStatic
        disc = createDeadReckoningAlgorithmEnum8("DRM_FVB");
        data = createSpatialFVStruct(); //

        return top;
    }

    HLAfixedRecord createSpatialFPStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createWorldLocationStruct());        // WorldLocation
        struct.add(createRPRboolean());                 // IsFrozen
        struct.add(createOrientationStruct());          // Orientation
        struct.add(createVelocityVectorStruct());       // VelocityVector
        return struct;
    }

    HLAfixedRecord createSpatialRPStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createWorldLocationStruct());        // WorldLocation
        struct.add(createRPRboolean());                 // IsFrozen
        struct.add(createOrientationStruct());          // Orientation
        struct.add(createVelocityVectorStruct());       // VelocityVector
        struct.add(createAngularVelocityVectorStruct());       // VelocityVector
        return struct;
    }

    HLAfixedRecord createSpatialFVStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createWorldLocationStruct());        // WorldLocation
        struct.add(createRPRboolean());                 // IsFrozen
        struct.add(createOrientationStruct());          // Orientation
        struct.add(createVelocityVectorStruct());       // VelocityVector
        struct.add(createAccelerationVectorStruct());       // VelocityVector
        return struct;
    }

    HLAfixedRecord createSpatialRVStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createWorldLocationStruct());        // WorldLocation
        struct.add(createRPRboolean());                 // IsFrozen
        struct.add(createOrientationStruct());          // Orientation
        struct.add(createVelocityVectorStruct());       // VelocityVector
        struct.add(createAccelerationVectorStruct());       // VelocityVector
        struct.add(createAngularVelocityVectorStruct());       // VelocityVector
        return struct;
    }

    HLAfixedRecord createSpatialStaticStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createWorldLocationStruct());        // WorldLocation
        struct.add(createRPRboolean());                 // IsFrozen
        struct.add(createOrientationStruct());          // Orientation
        return struct;
    }

    HLAfloat64BE createMeterFloat64(double value) {
        return encoderFactory.createHLAfloat64BE(value);
    }

    HLAfloat64BE createMeterFloat64() {
        return encoderFactory.createHLAfloat64BE();
    }

    HLAfloat32BE createAngleRadianFloat32(float value) {
        return encoderFactory.createHLAfloat32BE(value);
    }

    HLAfloat32BE createAngleRadianFloat32() {
        return encoderFactory.createHLAfloat32BE();
    }

    HLAfloat32BE createVelocityMeterPerSecondFloat32() {
        return encoderFactory.createHLAfloat32BE();
    }

    HLAfloat32BE createAccelerationMeterPerSecondSquaredFloat32() {
        return encoderFactory.createHLAfloat32BE();
    }

    HLAfloat32BE createAngularVelocityRadianPerSecondFloat32() {
        return encoderFactory.createHLAfloat32BE();
    }

    HLAoctet createRPRboolean() {
        return encoderFactory.createHLAoctet();
    }

    HLAoctet setRPRboolean(HLAoctet struct, String value) {
        if ("False".equals(value))
            struct.setValue((byte) 0);
        if ("True".equals(value))
            struct.setValue((byte) 1);
        return struct;
    }

    String getRPRboolean(HLAoctet struct) {
        if (struct.getValue() == 0)
            return "False";
        if (struct.getValue() == 1)
            return "False";
        return "";
    }

    HLAfixedRecord createOrientationStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createAngleRadianFloat32()); // Psi
        struct.add(createAngleRadianFloat32()); // Theta
        struct.add(createAngleRadianFloat32()); // Phi
        return struct;
    }

    HLAfixedRecord createWorldLocationStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createMeterFloat64()); // X
        struct.add(createMeterFloat64()); // Y
        struct.add(createMeterFloat64()); // Z
        return struct;
    }

    HLAfixedRecord createVelocityVectorStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createVelocityMeterPerSecondFloat32()); // XVelocity
        struct.add(createVelocityMeterPerSecondFloat32()); // YVelocity
        struct.add(createVelocityMeterPerSecondFloat32()); // ZVelocity
        return struct;
    }

    HLAfixedRecord createAccelerationVectorStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createAccelerationMeterPerSecondSquaredFloat32()); // XAcceleration
        struct.add(createAccelerationMeterPerSecondSquaredFloat32()); // YAcceleration
        struct.add(createAccelerationMeterPerSecondSquaredFloat32()); // ZAcceleration
        return struct;
    }

    HLAfixedRecord createAngularVelocityVectorStruct() {
        HLAfixedRecord struct = encoderFactory.createHLAfixedRecord();
        struct.add(createAngularVelocityRadianPerSecondFloat32()); // XAngularVelocity
        struct.add(createAngularVelocityRadianPerSecondFloat32()); // YAngularVelocity
        struct.add(createAngularVelocityRadianPerSecondFloat32()); // ZAngularVelocity
        return struct;
    }

    HLAoctet createDeadReckoningAlgorithmEnum8() {
        return encoderFactory.createHLAoctet();
    }

    HLAoctet createDeadReckoningAlgorithmEnum8(String value) {
        HLAoctet data = encoderFactory.createHLAoctet();
        return setDeadReckoningAlgorithmEnum8(data, value);
    }

    HLAoctet setDeadReckoningAlgorithmEnum8(HLAoctet struct, String value) {
        if ("Other".equals(value))
            struct.setValue((byte) 0);
        if ("Static".equals(value))
            struct.setValue((byte) 1);
        if ("DRM_FPW".equals(value))
            struct.setValue((byte) 2);
        if ("DRM_RPW".equals(value))
            struct.setValue((byte) 3);
        if ("DRM_RVW".equals(value))
            struct.setValue((byte) 4);
        if ("DRM_FVW".equals(value))
            struct.setValue((byte) 5);
        if ("DRM_FPB".equals(value))
            struct.setValue((byte) 6);
        if ("DRM_RPB".equals(value))
            struct.setValue((byte) 7);
        if ("DRM_RVB".equals(value))
            struct.setValue((byte) 8);
        if ("DRM_FVB".equals(value))
            struct.setValue((byte) 9);
        return struct;
    }

}
