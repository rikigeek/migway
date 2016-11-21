package LDM;

import java.time.LocalDate;
import java.util.Arrays;

import LDM.platform.PlatformCapability_T;

public class PojoFactory {

    public static PlatformCapability_T createPlatform() {
        PlatformCapability_T p = new PlatformCapability_T();
        p.coreCapability = new CommonCapability_T();
        p.coreCapability.description = "A test platform";
        p.coreCapability.issue = "no issue";
        p.coreCapability.manufacturer = "SETI";
        p.coreCapability.modStrike = "on strike";
        p.coreCapability.natoStockNumber = "Forty Two";
        p.coreCapability.productName = "MigwaySample";
        p.coreCapability.serialNumber = "42";
        p.coreCapability.softwareVersions = new SoftwareVersionDescriptor_T[2];
        p.coreCapability.softwareVersions[0] = new SoftwareVersionDescriptor_T();
        p.coreCapability.softwareVersions[1] = new SoftwareVersionDescriptor_T();
        p.coreCapability.softwareVersions[0].softwareModuleName = "core";
        p.coreCapability.softwareVersions[0].versionNumber = "b-0.1";
        p.coreCapability.softwareVersions[1].softwareModuleName = "benchmark";
        p.coreCapability.softwareVersions[1].versionNumber = "1.0";
        p.coreCapability.supportedModes = new ModeCapability_T();
        p.coreCapability.supportedModes.isMaintenanceCapable = false;
        p.coreCapability.supportedModes.isOffCapable = true;
        p.coreCapability.supportedModes.isOnCapable = true;
        p.coreCapability.supportedModes.isStandbyCapable = false;
        p.coreCapability.supportedModes.isTrainingCapable = false;
        p.coreCapability.timeOfDataGeneration = new VsiTime_T();
        p.coreCapability.timeOfDataGeneration.nanoseconds = 123;
        p.coreCapability.timeOfDataGeneration.seconds = LocalDate.of(1979, 9, 18).toEpochDay() * 3600 * 24;
        p.resourceId = 42;
        p.resourceIdType = ResourceType_E.RESOURCE_TYPE__NAV_UNIT;

        return p;
    }

    public static void debugPlatform(PlatformCapability_T p) {
        System.out.printf("ID %d - %s %n", p.resourceId, p.resourceIdType);
        CommonCapability_T c = p.coreCapability;
        if (c != null) {
            System.out.printf(" PN %s%n DS %s%n MF %s%n IS %s%n ST %s%n NS %s%n SN %s%n", c.productName, c.description, c.manufacturer,
                    c.issue, c.modStrike, c.natoStockNumber, c.serialNumber);
            if (c.softwareVersions != null) {
                for (SoftwareVersionDescriptor_T d : Arrays.asList(c.softwareVersions)) {
                    if (d != null)
                        System.out.printf("SW %s v%s%n", d.softwareModuleName, d.versionNumber);
                }
            }
            ModeCapability_T m = c.supportedModes;
            if (m != null) {
                System.out.printf("%s %s %s %s %s%n", m.isOnCapable, m.isOffCapable, m.isStandbyCapable, m.isMaintenanceCapable,
                        m.isTrainingCapable);
            }
            VsiTime_T t = c.timeOfDataGeneration;
            if (t != null) {
            LocalDate dt = LocalDate.ofEpochDay(t.seconds / 24 / 3600);
            long sec = t.seconds % (24 * 3600) + (long)t.nanoseconds / 1_000_000;

            System.out.printf("GEN %s %d.%06ds%n", dt.toString(), sec, t.nanoseconds % 1_000_000);
            }
        }
    }
}
