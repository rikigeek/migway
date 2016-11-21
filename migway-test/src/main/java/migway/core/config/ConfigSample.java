package migway.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class ConfigSample {
    /**
     * Load the configuration from hardcoded samples
     * Supports only the default samples:
     * <ul>
     * <li><code>'migway:sample'</code> - sample POJO (MyPojo & DdsPojo)
     * <li><code>'migway:hla.tanklab'</code> - HLA Tanklab object from Tanklab
     * FOM (tanklab.Tank & tanklab.Fire)
     * <li><code>'migway:dds-GVA.LDM'</code> - DDS subset of GVA IDL (LDM.*,
     * LDM.platform.*, LDM.navigation.*)
     * <li><code>'migway:hla-RPRFOM.edu'</code> - HLA subset of RPRFOM
     * (namespace is edu.cyc14.essais.pojo.rprfom). Define BaseEntity Class and
     * Spatial attribute types
     * <li><code>'migway:testingconfig'</code> - structure for unit test
     * (contains config that use all available type and element combination)
     * </ul>
     * 
     * @param f
     *            the file to load
     */
    static public ConfigHelper loadConfig(File f) {
        ConfigHelper config = new ConfigHelper();
        if (f.equals(new File("migway:sample"))) {
            ConfigSample.loadSample(config, "edu.cyc14.essais.pojo.");
        }

        else if (f.equals(new File("migway:testingconfig"))) {
            ConfigSample.loadTestingConfig(config);
        }

        else if (f.equals(new File("migway:hla.tanklab"))) {
            ConfigSample.loadTanklab(config, "tanklab.");
        }

        else if (f.equals(new File("migway:dds-GVA.LDM"))) {
            ConfigSample.loadDdsGvaLdm(config, "LDM.");
        }

        else if (f.equals(new File("migway:hla-RPRFOM.edu"))) {
            ConfigSample.loadHlaRprEdu(config, "edu.cyc14.essais.pojo.rprfom.");
        } 
        
        else if (f.equals(new File("migway:map-demo-tanklab"))) {
            ConfigHelper tempStructureConf = ConfigHelper.loadConfig("migway:hla.tanklab");
            tempStructureConf.appendConfig("migway:dds-GVA.LDM");
            MappingSampleFactory.loadMappingTankLab(config, tempStructureConf);
        } 
        
        else if (f.equals(new File("migway:key-demo-tanklab"))) {
            MappingSampleFactory.loadKeysTanklab(config);
        } 
        
        else if (f.equals(new File("migway:map-sample"))) {
            ConfigHelper tempStructureConf = ConfigHelper.loadConfig("migway:sample");
            MappingSampleFactory.loadMappingSample(config, tempStructureConf);
        } 
        
        else {
            return null;
        }
        config.loadFromMap();
        return config;
    }

    /**
     * Load the default sample (with simple POJO)
     */
    private static void loadSample(ConfigHelper config, String namespace) {
        // manually feed the pojoList
        ClassStructure pojo = new ClassStructure(namespace + "MyPojo", new String[] { "HLA" }, null);

        pojo.addBasic("ok", null, null, BasicTypeEnum.BOOLEAN);
        pojo.addBasic("value", null, null, BasicTypeEnum.INT);
        pojo.addBasic("name", null, null, BasicTypeEnum.STRING);
        config.addStructure(pojo);

        pojo = new ClassStructure(namespace + "DdsPojo", new String[] { "DDS" }, null);

        pojo.addBasic("number", null, null, BasicTypeEnum.INT);
        pojo.addBasic("acknowledged", null, null, BasicTypeEnum.BOOLEAN);
        pojo.addBasic("description", null, null, BasicTypeEnum.STRING);
        pojo.addBasic("precision", null, null, BasicTypeEnum.FLOAT);

        config.addStructure(pojo);
    }

    /**
     * Load samples for the Tank sample
     */

    private static void loadDdsGvaLdm(ConfigHelper config, String namespace) {
        Structure pojo;
        pojo = getResourceTypeEnum(namespace);
        config.addStructure(pojo);

        pojo = getVsiTimeStructure(namespace);
        config.addStructure(pojo);

        pojo = getNavAttitudeStateStructure(namespace);
        config.addStructure(pojo);

        pojo = getNavAttitudeParameterNameEnum(namespace);
        config.addStructure(pojo);

        pojo = getNavPositionStateStructure(namespace);
        config.addStructure(pojo);

        pojo = getNavErrorDataTypeStructure(namespace);
        config.addStructure(pojo);

        pojo = getNavErrorTypeEnum(namespace);
        config.addStructure(pojo);

        pojo = getNavPositionParameterNameEnum(namespace);
        config.addStructure(pojo);

        pojo = getPlatformCapabilityStructure(namespace);
        config.addStructure(pojo);

        pojo = getCommonCapabilityStructure(namespace);
        config.addStructure(pojo);

        pojo = getSoftwareVersionDescriptorStructure(namespace);
        config.addStructure(pojo);

        pojo = getModeCapabilityStructure(namespace);
        config.addStructure(pojo);
    }

    private static void loadHlaRprEdu(ConfigHelper config, String namespace) {
        Structure pojo;
        // Add RPR Structure (Spatial attribute)
        pojo = getBaseEntityStructure(namespace);
        config.addStructure(pojo);

        pojo = getRPRboolean(namespace);
        config.addStructure(pojo);

        pojo = getWorldLocationStruct(namespace);
        config.addStructure(pojo);

        pojo = getOrientationStruct(namespace);
        config.addStructure(pojo);

        pojo = getVelocityVectorStruct(namespace);
        config.addStructure(pojo);

        pojo = getAccelerationVectorStruct(namespace);
        config.addStructure(pojo);

        pojo = getAngularVelocityVectorStruct(namespace);
        config.addStructure(pojo);

        pojo = getSpatialFPStruct(namespace);
        config.addStructure(pojo);

        pojo = getSpatialFVStruct(namespace);
        config.addStructure(pojo);

        pojo = getSpatialRVStruct(namespace);
        config.addStructure(pojo);

        pojo = getSpatialRPStruct(namespace);
        config.addStructure(pojo);

        pojo = getSpatialStaticStruct(namespace);
        config.addStructure(pojo);

        pojo = getDeadReckoningAlgorithmEnum8(namespace);
        config.addStructure(pojo);

        pojo = getSpatialVariantStructure(namespace);
        config.addStructure(pojo);
    }

    private static Structure getDeadReckoningAlgorithmEnum8(String rprNamespace) {
        EnumStructure struct = new EnumStructure(rprNamespace + "DeadReckoningAlgorithmEnum8", new String[] { "HLA" },
                new String[] { "DeadReckoningAlgorithmEnum8" });
        struct.addEnumValue("Other");
        struct.addEnumValue("Static");
        struct.addEnumValue("DRM_FPW");
        struct.addEnumValue("DRM_RPW");
        struct.addEnumValue("DRM_RVW");
        struct.addEnumValue("DRM_FVW");
        struct.addEnumValue("DRM_FPB");
        struct.addEnumValue("DRM_RPB");
        struct.addEnumValue("DRM_RVB");
        struct.addEnumValue("DRM_FVB");
        return struct;
    }

    private static Structure getRPRboolean(String rprNamespace) {
        EnumStructure struct = new EnumStructure(rprNamespace + "RPRboolean", new String[] { "HLA" }, new String[] { "RPRboolean" });
        struct.addEnumValue("False");
        struct.addEnumValue("True");
        return struct;
    }

    private static Structure getOrientationStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "OrientationStruct", new String[] { "HLA" },
                new String[] { "OrientationStruct" });
        struct.addBasic("psi", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("theta", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("phi", null, null, BasicTypeEnum.FLOAT);
        struct.getElement("psi").setRemoteName("Psi");
        struct.getElement("theta").setRemoteName("Theta");
        struct.getElement("phi").setRemoteName("Phi");
        return struct;
    }

    private static Structure getAccelerationVectorStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "AccelerationVectorStruct", new String[] { "HLA" },
                new String[] { "AccelerationVectorStruct" });
        struct.addBasic("xAcceleration", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("yAcceleration", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("zAcceleration", null, null, BasicTypeEnum.FLOAT);
        struct.getElement("xAcceleration").setRemoteName("XAcceleration");
        struct.getElement("yAcceleration").setRemoteName("YAcceleration");
        struct.getElement("zAcceleration").setRemoteName("ZAcceleration");
        return struct;
    }

    private static Structure getAngularVelocityVectorStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "AngularVelocityVectorStruct", new String[] { "HLA" },
                new String[] { "AngularVelocityVectorStruct" });
        struct.addBasic("xAngularVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("yAngularVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("zAngularVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.getElement("xAngularVelocity").setRemoteName("XAngularVelocity");
        struct.getElement("yAngularVelocity").setRemoteName("YAngularVelocity");
        struct.getElement("zAngularVelocity").setRemoteName("ZAngularVelocity");
        return struct;
    }

    private static Structure getVelocityVectorStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "VelocityVectorStruct", new String[] { "HLA" },
                new String[] { "VelocityVectorStruct" });
        struct.addBasic("xVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("yVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.addBasic("zVelocity", null, null, BasicTypeEnum.FLOAT);
        struct.getElement("xVelocity").setRemoteName("XVelocity");
        struct.getElement("yVelocity").setRemoteName("YVelocity");
        struct.getElement("zVelocity").setRemoteName("ZVelocity");
        return struct;
    }

    private static Structure getWorldLocationStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "WorldLocationStruct", new String[] { "HLA" },
                new String[] { "WorldLocationStruct" });
        struct.addBasic("x", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("y", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("z", null, null, BasicTypeEnum.DOUBLE);
        struct.getElement("x").setRemoteName("X");
        struct.getElement("y").setRemoteName("Y");
        struct.getElement("z").setRemoteName("Z");
        return struct;
    }

    private static Structure getSpatialFPStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "SpatialFPStruct", new String[] { "HLA" },
                new String[] { "SpatialFPStruct" });
        struct.addClass("worldLocation", null, null, rprNamespace + "WorldLocationStruct");
        struct.addClass("isFrozen", null, null, rprNamespace + "RPRboolean");
        struct.addClass("orientation", null, null, rprNamespace + "OrientationStruct");
        struct.addClass("velocityVector", null, null, rprNamespace + "VelocityVectorStruct");
        struct.getElement("worldLocation").setRemoteName("WorldLocation");
        struct.getElement("isFrozen").setRemoteName("IsFrozen");
        struct.getElement("orientation").setRemoteName("Orientation");
        struct.getElement("velocityVector").setRemoteName("VelocityVector");
        return struct;
    }

    private static Structure getSpatialFVStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "SpatialFVStruct", new String[] { "HLA" },
                new String[] { "SpatialFVStruct" });
        struct.addClass("worldLocation", null, null, rprNamespace + "WorldLocationStruct");
        struct.addClass("isFrozen", null, null, rprNamespace + "RPRboolean");
        struct.addClass("orientation", null, null, rprNamespace + "OrientationStruct");
        struct.addClass("velocityVector", null, null, rprNamespace + "VelocityVectorStruct");
        struct.addClass("accelerationVector", null, null, rprNamespace + "AccelerationVectorStruct");
        struct.getElement("worldLocation").setRemoteName("WorldLocation");
        struct.getElement("isFrozen").setRemoteName("IsFrozen");
        struct.getElement("orientation").setRemoteName("Orientation");
        struct.getElement("velocityVector").setRemoteName("VelocityVector");
        struct.getElement("accelerationVector").setRemoteName("AccelerationVector");
        return struct;
    }

    private static Structure getSpatialRPStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "SpatialRPStruct", new String[] { "HLA" },
                new String[] { "SpatialRPStruct" });
        struct.addClass("worldLocation", null, null, rprNamespace + "WorldLocationStruct");
        struct.addClass("isFrozen", null, null, rprNamespace + "RPRboolean");
        struct.addClass("orientation", null, null, rprNamespace + "OrientationStruct");
        struct.addClass("velocityVector", null, null, rprNamespace + "VelocityVectorStruct");
        struct.addClass("angularVelocity", null, null, rprNamespace + "AngularVelocityVectorStruct");
        struct.getElement("worldLocation").setRemoteName("WorldLocation");
        struct.getElement("isFrozen").setRemoteName("IsFrozen");
        struct.getElement("orientation").setRemoteName("Orientation");
        struct.getElement("velocityVector").setRemoteName("VelocityVector");
        struct.getElement("angularVelocity").setRemoteName("AngularVelocity");
        return struct;
    }

    private static Structure getSpatialRVStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "SpatialRVStruct", new String[] { "HLA" },
                new String[] { "SpatialRVStruct" });
        struct.addClass("worldLocation", null, null, rprNamespace + "WorldLocationStruct");
        struct.addClass("isFrozen", null, null, rprNamespace + "RPRboolean");
        struct.addClass("orientation", null, null, rprNamespace + "OrientationStruct");
        struct.addClass("velocityVector", null, null, rprNamespace + "VelocityVectorStruct");
        struct.addClass("accelerationVector", null, null, rprNamespace + "AccelerationVectorStruct");
        struct.addClass("angularVelocity", null, null, rprNamespace + "AngularVelocityVectorStruct");
        struct.getElement("worldLocation").setRemoteName("WorldLocation");
        struct.getElement("isFrozen").setRemoteName("IsFrozen");
        struct.getElement("orientation").setRemoteName("Orientation");
        struct.getElement("velocityVector").setRemoteName("VelocityVector");
        struct.getElement("accelerationVector").setRemoteName("AccelerationVector");
        struct.getElement("angularVelocity").setRemoteName("AngularVelocity");
        return struct;
    }

    private static Structure getSpatialStaticStruct(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "SpatialStaticStruct", new String[] { "HLA" },
                new String[] { "SpatialStaticStruct" });
        struct.addClass("worldLocation", null, null, rprNamespace + "WorldLocationStruct");
        struct.addClass("isFrozen", null, null, rprNamespace + "RPRboolean");
        struct.addClass("orientation", null, null, rprNamespace + "OrientationStruct");
        struct.getElement("worldLocation").setRemoteName("WorldLocation");
        struct.getElement("isFrozen").setRemoteName("IsFrozen");
        struct.getElement("orientation").setRemoteName("Orientation");
        return struct;
    }

    private static Structure getSpatialVariantStructure(String rprNamespace) {
        UnionStructure struct = new UnionStructure(rprNamespace + "SpatialVariantStruct", new String[] { "HLA" },
                new String[] { "SpatialVariantStruct" });
        struct.addClass("deadReckoningAlgorithm", null, null, rprNamespace + "DeadReckoningAlgorithmEnum8");
        struct.addClass("spatialStatic", null, null, rprNamespace + "SpatialStaticStruct");
        struct.addClass("spatialFPB", null, null, rprNamespace + "SpatialFPStruct");
        struct.addClass("spatialFPW", null, null, rprNamespace + "SpatialFPStruct");
        struct.addClass("spatialFVB", null, null, rprNamespace + "SpatialRPStruct");
        struct.addClass("spatialFVW", null, null, rprNamespace + "SpatialRPStruct");
        struct.addClass("spatialRPB", null, null, rprNamespace + "SpatialFVStruct");
        struct.addClass("spatialRPW", null, null, rprNamespace + "SpatialFVStruct");
        struct.addClass("spatialRVB", null, null, rprNamespace + "SpatialRVStruct");
        struct.addClass("spatialRVW", null, null, rprNamespace + "SpatialRVStruct");
        struct.getElement("deadReckoningAlgorithm").setRemoteName("DeadReckoningAlgorithm");
        struct.getElement("spatialStatic").setRemoteName("SpatialStatic");
        struct.getElement("spatialFPB").setRemoteName("SpatialFPB");
        struct.getElement("spatialFPW").setRemoteName("SpatialFPW");
        struct.getElement("spatialFVB").setRemoteName("SpatialFVB");
        struct.getElement("spatialFVW").setRemoteName("SpatialFVW");
        struct.getElement("spatialRPB").setRemoteName("SpatialRPB");
        struct.getElement("spatialRPW").setRemoteName("SpatialRPW");
        struct.getElement("spatialRVB").setRemoteName("SpatialRVB");
        struct.getElement("spatialRVW").setRemoteName("SpatialRVW");
        struct.setDiscrimant("deadReckoningAlgorithm");
        struct.addAlternative("Static", "spatialStatic");
        struct.addAlternative("DRM_RVB", "spatialRVB");
        struct.addAlternative("DRM_FVB", "spatialFVB");
        struct.addAlternative("DRM_RPB", "spatialRPB");
        struct.addAlternative("DRM_FPB", "spatialFPB");
        struct.addAlternative("DRM_RPW", "spatialRPW");
        struct.addAlternative("DRM_RVW", "spatialRVW");
        struct.addAlternative("DRM_FVW", "spatialFVW");
        struct.addAlternative("DRM_FPW", "spatialFPW");

        return struct;
    }

    private static Structure getBaseEntityStructure(String rprNamespace) {
        ClassStructure struct = new ClassStructure(rprNamespace + "BaseEntity", new String[] { "HLA" }, new String[] { "BaseEntity" });
        struct.addClass("spatial", null, null, rprNamespace + "SpatialVariantStruct");
        struct.getElement("spatial").setRemoteName("Spatial", "HLA");
        struct.addClass("relativeSpatial", null, null, rprNamespace + "SpatialVariantStruct");
        struct.getElement("relativeSpatial").setRemoteName("RelativeSpatial", "HLA");

        return struct;
    }

    /**
     * Load a Pojo Structure for NavAttitude GVA topic
     * 
     * @return
     */
    private static Structure getNavAttitudeStateStructure(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        ClassStructure struct = new ClassStructure(navigationNamespace + "NavAttitudeState_T", new String[] { "DDS" }, null);

        struct.addBasic("pitch", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("roll", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("yaw", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("pitchRate", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("rollRate", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("yawRate", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("navElementId", null, null, BasicTypeEnum.INT);
        struct.addClass("navElementIdType", null, null, globalNamespace + "ResourceType_E");
        // This field is an array of enum
        struct.addArray("recentlyChanged", null, null, navigationNamespace + "NavAttitudeParameterName_E", 0, 10);
        struct.addClass("timeOfDataGeneration", null, null, globalNamespace + "VsiTime_T");

        return struct;
    }

    private static Structure getVsiTimeStructure(String globalNamespace) {
        ClassStructure struct = new ClassStructure(globalNamespace + "VsiTime_T", new String[] { "DDS" }, null);

        struct.addBasic("seconds", null, null, BasicTypeEnum.LONG);
        struct.addBasic("nanoseconds", null, null, BasicTypeEnum.UNSIGNED_INT);

        return struct;
    }

    /**
     * Load a POJO structure for the GVA Platform Capability topic
     * 
     * @return
     */
    private static Structure getPlatformCapabilityStructure(String globalNamespace) {
        String platformNamespace = globalNamespace + "platform.";
        ClassStructure struct = new ClassStructure(platformNamespace + "PlatformCapability_T", new String[] { "DDS" }, null);

        struct.addBasic("resourceId", null, null, BasicTypeEnum.INT);
        struct.addClass("resourceIdType", null, null, globalNamespace + "ResourceType_E");
        struct.addClass("coreCapability", null, null, globalNamespace + "CommonCapability_T");

        return struct;
    }

    private static Structure getCommonCapabilityStructure(String globalNamespace) {
        ClassStructure struct = new ClassStructure(globalNamespace + "CommonCapability_T", new String[] { "DDS" }, null);

        struct.addBasic("manufacturer", null, null, BasicTypeEnum.STRING);
        struct.addBasic("productName", null, null, BasicTypeEnum.STRING);
        struct.addBasic("description", null, null, BasicTypeEnum.STRING);
        struct.addBasic("serialNumber", null, null, BasicTypeEnum.STRING);
        struct.addBasic("issue", null, null, BasicTypeEnum.STRING);
        struct.addBasic("modStrike", null, null, BasicTypeEnum.STRING);
        struct.addBasic("natoStockNumber", null, null, BasicTypeEnum.STRING);
        struct.addArray("softwareVersions", null, null, globalNamespace + "SoftwareVersionDescriptor_T", 0, 20);
        struct.addClass("supportedModes", null, null, globalNamespace + "ModeCapability_T");
        struct.addClass("timeOfDataGeneration", null, null, globalNamespace + "VsiTime_T");

        return struct;
    }

    private static Structure getModeCapabilityStructure(String globalNamespace) {
        ClassStructure struct = new ClassStructure(globalNamespace + "ModeCapability_T", new String[] { "DDS" }, null);

        struct.addBasic("isOffCapable", null, null, BasicTypeEnum.BOOLEAN);
        struct.addBasic("isOnCapable", null, null, BasicTypeEnum.BOOLEAN);
        struct.addBasic("isStandbyCapable", null, null, BasicTypeEnum.BOOLEAN);
        struct.addBasic("isMaintenanceCapable", null, null, BasicTypeEnum.BOOLEAN);
        struct.addBasic("isTrainingCapable", null, null, BasicTypeEnum.BOOLEAN);

        return struct;
    }

    private static Structure getResourceTypeEnum(String globalNamespace) {
        EnumStructure struct = new EnumStructure(globalNamespace + "ResourceType_E", new String[] { "DDS" }, null);

        struct.addEnumValue("RESOURCE_TYPE__APPLICATION");
        struct.addEnumValue("RESOURCE_TYPE__BRAKE");
        struct.addEnumValue("RESOURCE_TYPE__CREWSTATION");
        struct.addEnumValue("RESOURCE_TYPE__ACOUSTIC");
        struct.addEnumValue("RESOURCE_TYPE__CAMERA");
        // TODO complete enum list...

        return struct;
    }

    private static Structure getNavPositionParameterNameEnum(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        EnumStructure struct = new EnumStructure(navigationNamespace + "NavPositionParameterName_E", new String[] { "DDS" }, null);

        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__LONGITUDE");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__LATITUDE");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__HEIGHT");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__POSITION_ERROR");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__HEIGHT_ERROR");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__UTC_TIME_OF_DAY");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__DATE");
        struct.addEnumValue("NAV_POSITION_PARAMETER_NAME__DISTANCE_TRAVELLED");

        return struct;
    }

    private static Structure getNavAttitudeParameterNameEnum(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        EnumStructure struct = new EnumStructure(navigationNamespace + "NavAttitudeParameterName_E", new String[] { "DDS" }, null);

        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__PITCH");
        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__ROLL");
        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__YAW");
        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__PITCH_RATE");
        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__ROLL_RATE");
        struct.addEnumValue("NAV_ATTITUDE_PARAMETER_NAME__YAW_RATE");

        return struct;
    }

    private static Structure getSoftwareVersionDescriptorStructure(String globalNamespace) {
        ClassStructure struct = new ClassStructure(globalNamespace + "SoftwareVersionDescriptor_T", new String[] { "DDS" }, null);

        struct.addBasic("softwareModuleName", null, null, BasicTypeEnum.STRING);
        struct.addBasic("versionNumber", null, null, BasicTypeEnum.STRING);

        return struct;
    }

    private static Structure getNavErrorDataTypeStructure(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        ClassStructure struct = new ClassStructure(navigationNamespace + "NavErrorDataType_T", new String[] { "DDS" }, null);

        struct.addBasic("Error", null, null, BasicTypeEnum.DOUBLE);
        struct.addClass("ErrorType", null, null, navigationNamespace + "NavErrorType_E");

        return struct;
    }

    private static Structure getNavErrorTypeEnum(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        EnumStructure struct = new EnumStructure(navigationNamespace + "NavErrorType_E", new String[] { "DDS" }, null);

        struct.addEnumValue("NAV_ERROR_TYPE__GDOP");
        struct.addEnumValue("NAV_ERROR_TYPE__FOM");
        struct.addEnumValue("NAV_ERROR_TYPE__CEP");
        struct.addEnumValue("NAV_ERROR_TYPE__ERROR_RATING");
        struct.addEnumValue("NAV_ERROR_TYPE__NONE_AVAILABLE");

        return struct;
    }

    private static Structure getNavPositionStateStructure(String globalNamespace) {
        String navigationNamespace = globalNamespace + "navigation.";
        ClassStructure struct = new ClassStructure(navigationNamespace + "NavPositionState_T", new String[] { "DDS" }, null);

        struct.addBasic("navElementId", null, null, BasicTypeEnum.INT);
        struct.addClass("navElementIdType", null, null, navigationNamespace + "ResourceType_E");
        struct.addBasic("longitude", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("latitude", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("height", null, null, BasicTypeEnum.DOUBLE);
        struct.addClass("positionError", null, null, navigationNamespace + "NavErrorDataType_T");
        struct.addClass("heightError", null, null, navigationNamespace + "NavErrorDataType_T");
        struct.addBasic("utcTimeOfDay", null, null, BasicTypeEnum.DOUBLE);
        struct.addBasic("date", null, null, BasicTypeEnum.STRING);
        struct.addBasic("distanceTravelled", null, null, BasicTypeEnum.DOUBLE);
        struct.addArray("recentlyChanged", null, null, navigationNamespace + "NavPositionParameterName_E", 0, 10);
        struct.addClass("timeOfDataGeneration", null, null, globalNamespace + "VsiTime_T");

        return struct;
    }

    /**
     * Load a configuration that use all possible structure/type/elements
     * This configuration is created for validation (JUnit test)
     * 
     * @param config
     */
    private static void loadTestingConfig(ConfigHelper config) {
        //
        ClassStructure classA = new ClassStructure("ns.classA");
        ClassStructure classB = new ClassStructure("ns.classB", new String[] { "HLA" }, new String[] { "class_b" });
        ClassStructure classC = new ClassStructure("ns.classC", new String[] { "HLA", "DDS" }, new String[] { "class_c", "classeC" });
        config.addStructure(classA);
        config.addStructure(classB);
        config.addStructure(classC);

        classA.addBasic("basicInt1", null, null, BasicTypeEnum.INT);
        classA.addClass("refClassB", null, null, "ns.classB");
        classA.addArray("arrayString1", null, null, BasicTypeEnum.STRING, 0, 0);
        classA.addArray("arrayClassC", null, null, "ns.classC", 0, 10);
        classA.addArray("arrayArray1", null, null, new ArrayType(BasicType.INT, 10, 10), 1, 6);
        classA.addClass("day", null, null, "ns.enumA");

        classB.addBasic("x", null, null, BasicTypeEnum.DOUBLE);
        classB.addBasic("y", null, null, BasicTypeEnum.DOUBLE);
        classB.addBasic("z", null, null, BasicTypeEnum.DOUBLE);

        classC.addArray("moves", null, null, "ns.classB", 0, 50);

        EnumStructure enumA = new EnumStructure("ns.enumA");
        config.addStructure(enumA);
        enumA.addEnumValue("MON");
        enumA.addEnumValue("TUE");
        enumA.addEnumValue("WED");
        enumA.addEnumValue("THU");
        enumA.addEnumValue("FRI");
        enumA.addEnumValue("SAT");
        enumA.addEnumValue("SUN");

        UnionStructure unionA = new UnionStructure("ns.unionA", new String[] { "DDS" }, new String[] { "union_A" });
        unionA.setGlobalRemoteName("unionA");
        config.addStructure(unionA);
        unionA.addBasic("choice", null, null, BasicTypeEnum.BOOLEAN);
        unionA.addClass("static", null, null, "ns.classB");
        unionA.addClass("mobile", null, null, "ns.classC");
        unionA.setDiscrimant("choice");
        unionA.addAlternative(Boolean.toString(true), "mobile");
        unionA.addAlternative(Boolean.toString(false), "static");
        
        // Add mapping
        List<Mapping> mappings = new ArrayList<Mapping>();
        Mapping mapping;
        ElementMapping elementMapping;
        KeyModel key;


        mapping = new Mapping();
        mappings.add(mapping);

        mapping.setDestination(classA.getName());
        key = new KeyModel();
        key.setMessageHeader("basicInt1");
        mapping.setDestinationKey(key);

        mapping.resetSources();
        // Attitude
        key = new KeyModel();
        key.addKeyName("x");
        mapping.addSource(classB.getName(), key);
        key = new KeyModel();
        key.addKeyName("moves");
        mapping.addSource(classC.getName(), key);



        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(classA.getElement("refClassB"));
        elementMapping.setSource(classB.getElement("y"));
        key = new KeyModel();
        key.addKeyName("*");
        elementMapping.setSourceKey(key);
        key = new KeyModel();
        key.addKeyName("z");
        elementMapping.setDestinationKey(key);

        mapping = new Mapping();
        mappings.add(mapping);
        
        mapping.setDestination(classB.getName());
        mapping.resetSources();
        mapping.addSource(classA.getName(), null);
        
        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(classB.getElement("z"));
        elementMapping.setSource(classA.getElement("basicInt1"));
        
        
        
        config.getMappings().addAll(mappings);
        
    }

    private static void loadTanklab(ConfigHelper config, String namespace) {
        ClassStructure tank = new ClassStructure(namespace + "Tank", new String[] { "HLA" }, new String[] { "Tank" });
        config.addStructure(tank);
        tank.addBasic("orientation", null, null, BasicTypeEnum.INT);
        tank.addBasic("damage", null, null, BasicTypeEnum.INT);
        tank.addBasic("x", null, null, BasicTypeEnum.INT);
        tank.addBasic("y", null, null, BasicTypeEnum.INT);

        ClassStructure fire = new ClassStructure(namespace + "Fire", new String[] { "HLA" }, new String[] { "Fire" });
        fire.addBasic("x", null, null, BasicTypeEnum.INT);
        fire.addBasic("y", null, null, BasicTypeEnum.INT);
    }

}
