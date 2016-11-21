package migway.core.samples;

import LDM.CommonCapability_T;
import LDM.ModeCapability_T;
import LDM.ResourceType_E;
import LDM.SoftwareVersionDescriptor_T;
import LDM.VsiTime_T;
import LDM.platform.PlatformCapability_T;
import edu.cyc14.essais.pojo.rprfom.BaseEntity;
import edu.cyc14.essais.pojo.rprfom.DeadReckoningAlgorithmEnum8;
import edu.cyc14.essais.pojo.rprfom.OrientationStruct;
import edu.cyc14.essais.pojo.rprfom.RPRboolean;
import edu.cyc14.essais.pojo.rprfom.SpatialFPStruct;
import edu.cyc14.essais.pojo.rprfom.SpatialVariantStruct;
import edu.cyc14.essais.pojo.rprfom.VelocityVectorStruct;
import edu.cyc14.essais.pojo.rprfom.WorldLocationStruct;

public class HlaSamples {
    private static byte[] convertToByteArray(short[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++)
            output[i] = (byte) input[i];
        return output;
    }

    /**
     * POJO version of getHlaBufferPlatformCapability()
     * Caution: ResourceId is not a structure in DDS POJO !! this should not
     * work for attribute 'resourceId'
     * 
     * @return a POJO
     */
    static public Object getHlaPojoPlatformCapability() {
        PlatformCapability_T platform = new PlatformCapability_T();
        platform.coreCapability = new CommonCapability_T();
        platform.coreCapability.manufacturer = "";
        platform.coreCapability.productName = "vehicule_2";
        platform.coreCapability.description = "1:1:78:1:2:0:0";
        platform.coreCapability.serialNumber = "";
        platform.coreCapability.issue = "1";
        platform.coreCapability.modStrike = "";
        platform.coreCapability.natoStockNumber = "";
        platform.coreCapability.softwareVersions = new SoftwareVersionDescriptor_T[20];
        for (int i = 0; i < platform.coreCapability.softwareVersions.length; i++) {
            platform.coreCapability.softwareVersions[i] = new SoftwareVersionDescriptor_T();
            platform.coreCapability.softwareVersions[i].softwareModuleName = "";
            platform.coreCapability.softwareVersions[i].versionNumber = "";
        }
        platform.coreCapability.supportedModes = new ModeCapability_T();
        platform.coreCapability.supportedModes.isOffCapable = false;
        platform.coreCapability.supportedModes.isOnCapable = false;
        platform.coreCapability.supportedModes.isStandbyCapable = false;
        platform.coreCapability.supportedModes.isMaintenanceCapable = false;
        platform.coreCapability.supportedModes.isTrainingCapable = false;
        platform.coreCapability.timeOfDataGeneration = new VsiTime_T();
        platform.coreCapability.timeOfDataGeneration.seconds = 0;
        platform.coreCapability.timeOfDataGeneration.nanoseconds = 0;

        platform.resourceId = 2;
        platform.resourceIdType = ResourceType_E.RESOURCE_TYPE__NAV_UNIT;

        return platform;
    }

    /**
     * Update: 'HLAobjectRoot.PlatformCapability22137' of class
     * 'PlatformCapability':
     * coreCapability = {, vehicule_2, 1:1:78:1:2:0:0, , 1, , , [{, }, {, }, {,
     * }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {,
     * }, {, }, {, }, {, }, {, }, {, }], {false, false, false, false, false},
     * {0, 0}}
     * resourceId = {2, RESOURCE_TYPE__NAV_UNIT}
     * Producing Federate: DDSGateway
     * 
     * Update: 'HLAobjectRoot.PlatformCapability22137' of class
     * 'PlatformCapability':
     * coreCapability = [00000000 0000000A 76656869
     * 63756C65 5F320000 0000000E
     * 313A313A 37383A31 3A323A30
     * 3A300000 00000000 00000001
     * 31000000 00000000
     * 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
     * 00000000]
     * resourceId = [00000002 000F]
     * Producing Federate: DDSGateway
     * 
     * @return the byte array that contains this data as it is encoded by HLA
     *         bus
     */
    static public byte[] getHlaBufferPlatformCapability() {
        short[] buf = new short[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x76, 0x65, 0x68, 0x69, 0x63, 0x75, 0x6C, 0x65, 0x5F,
                0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0E, 0x31, 0x3A, 0x31, 0x3A, 0x37, 0x38, 0x3A, 0x31, 0x3A, 0x32, 0x3A, 0x30, 0x3A,
                0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        return convertToByteArray(buf);
    }

    static public byte[] getHlaBufferSpatialVariantStruct() {
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

        return convertToByteArray(array);
    }

    static public Object getHlaBaseEntity() {
        BaseEntity pojo = new BaseEntity();
        pojo.spatial = (SpatialVariantStruct) getHlaPojoSpatialVariantStruct();
        return pojo;
    }

    static public SpatialVariantStruct getHlaPojoSpatialVariantStruct() {
        SpatialVariantStruct pojo = new SpatialVariantStruct();
        pojo.deadReckoningAlgorithm = DeadReckoningAlgorithmEnum8.DRM_FPW;
        pojo.spatialFPW = new SpatialFPStruct();
        pojo.spatialFPW.worldLocation = new WorldLocationStruct();
        pojo.spatialFPW.worldLocation.x = 4348595.623869689;
        pojo.spatialFPW.worldLocation.y = 185350.33281747854;
        pojo.spatialFPW.worldLocation.z = 4646777.811682586;
        pojo.spatialFPW.isFrozen = RPRboolean.False;
        pojo.spatialFPW.orientation = new OrientationStruct();
        pojo.spatialFPW.orientation.psi =  2.6655514F;
        pojo.spatialFPW.orientation.theta =  -0.66569006F;
        pojo.spatialFPW.orientation.phi = -2.7945876F;
        pojo.spatialFPW.velocityVector = new VelocityVectorStruct();
        pojo.spatialFPW.velocityVector.xVelocity = -0.0F;
        pojo.spatialFPW.velocityVector.yVelocity = 0.0F;
        pojo.spatialFPW.velocityVector.zVelocity = 0.0F;
        return pojo;

    }

    /**
     * Update: 'HLAobjectRoot.PlatformCapability22137' of class
     * 'PlatformCapability':
     * coreCapability = {, vehicule_2, 1:1:78:1:2:0:0, , 1, , , [{, }, {, }, {,
     * }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {, }, {,
     * }, {, }, {, }, {, }, {, }, {, }], {false, false, false, false, false},
     * {0, 0}}
     * resourceId = {2, RESOURCE_TYPE__NAV_UNIT}
     * Producing Federate: DDSGateway
     * 
     * @return
     */
    public static byte[] getHlaBufferCommonCapability_T() {
        String buffer = "00000000 0000000A 76656869 63756C65 5F320000 0000000E 313A313A 37383A31 3A323A30 3A300000 00000000 00000001 31000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000";
        return convertPitchRecorderStringToByteArray(buffer);
    }

    /**
     * Method to convert data from string into byte array
     * The string is as printed in Pitch Recorder, as raw data: String are words
     * of 4 bytes, in hexadecimal (so each word is 8 hexadecimal digits); with a
     * space between each word
     * example: "00000000 0000000A 76656869" will return a byte array with
     * length = 12; 4 first bytes are 0, last byte is 0x69, 8th byte is 10
     * (0x0A)
     * 
     * @param pitchRecorderString
     * @return
     */
    public static byte[] convertPitchRecorderStringToByteArray(String pitchRecorderString) {
        String[] array = pitchRecorderString.split(" ");
        byte[] outbuf = new byte[array.length * 4];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < 4; j++) {
                int posArray = 2 * j;
                int posOutbuf = i * 4 + j;
                String valueToParse = array[i].substring(posArray, posArray + 2);
                outbuf[posOutbuf] = (byte) Short.parseShort(valueToParse, 16);
            }
        }
        return outbuf;
    }
}
