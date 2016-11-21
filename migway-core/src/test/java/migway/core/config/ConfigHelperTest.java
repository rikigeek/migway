package migway.core.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import migway.core.config.ArrayType;
import migway.core.config.ConfigHelper;
import migway.core.config.Structure;
import migway.core.config.StructureRef;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigHelperTest {
    private Logger LOG = LoggerFactory.getLogger(ConfigHelperTest.class);

    /**
     * Test structure of basic type.
     * Test basic access to POJO elements
     */
    @Test
    public void testSampleConfigHelper() {
        new ConfigHelper();
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:sample"));
        assertNotNull(conf);
        
        assertTrue(conf.supportedPojos("HLA").contains("edu.cyc14.essais.pojo.MyPojo"));
        assertFalse(conf.supportedPojos("DDS").contains("edu.cyc14.essais.pojo.MyPojo"));
        assertFalse(conf.supportedPojos("HLA").contains("edu.cyc14.essais.pojo.DdsPojo"));
        assertTrue(conf.supportedPojos("DDS").contains("edu.cyc14.essais.pojo.DdsPojo"));
        
        List<Structure> pojoList = conf.getPojos();
        boolean foundMyPojo = false;
        boolean foundDdsPojo = false;
        for (Structure p : pojoList) {
            if ("edu.cyc14.essais.pojo.MyPojo".equals(p.getName()))
                foundMyPojo = true;
            if ("edu.cyc14.essais.pojo.DdsPojo".equals(p.getName()))
                foundDdsPojo = true;
        }
        assertTrue(foundDdsPojo);
        assertTrue(foundMyPojo);
        
        Structure pojo = conf.getPojoStructure("edu.cyc14.essais.pojo.MyPojo");
        assertNotNull(pojo);
        assertEquals("edu.cyc14.essais.pojo.MyPojo", pojo.getName());
        assertTrue(pojo.isSupported("HLA"));
        assertTrue(pojo.isSupported("hla"));
        assertFalse(pojo.isSupported("dds"));

        assertEquals(3, pojo.getElementsSize());
        assertEquals("ok", pojo.getElement(0).name());
        assertEquals(BasicTypeEnum.BOOLEAN, ((BasicType)pojo.getElement(0).elementType()).getBasicTypeEnum());
        assertNull(pojo.getElement(0).setter());
        assertNull(pojo.getElement(0).getter());
        assertTrue(pojo.getElement(0).isField());
        
        assertEquals("value", pojo.getElement(1).name());
        assertEquals(BasicTypeEnum.INT, ((BasicType)pojo.getElement(1).elementType()).getBasicTypeEnum());
        assertNull(pojo.getElement(1).setter());
        assertNull(pojo.getElement(1).getter());
        assertTrue(pojo.getElement(1).isField());
        
        assertEquals("name", pojo.getElement(2).name());
        assertEquals(BasicTypeEnum.STRING, ((BasicType)pojo.getElement(2).elementType()).getBasicTypeEnum());
        assertNull(pojo.getElement(2).setter());
        assertNull(pojo.getElement(2).getter());
        assertTrue(pojo.getElement(2).isField());

        assertNull(pojo.getElement(3));
        
    }

    @Test
    public void testTankSample() {
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:dds-GVA.LDM"));
        assertNotNull(conf);
        
        assertTrue(conf.supportedPojos("HLA").isEmpty());
        
        assertTrue(conf.supportedPojos("DDS").contains("LDM.VsiTime_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.ResourceType_E"));
                                                                             
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavAttitudeState_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavAttitudeParameterName_E"));
                                                                             
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavPositionState_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavErrorDataType_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavErrorType_E"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.navigation.NavPositionParameterName_E"));
                                                                             
        assertTrue(conf.supportedPojos("DDS").contains("LDM.platform.PlatformCapability_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.CommonCapability_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.SoftwareVersionDescriptor_T"));
        assertTrue(conf.supportedPojos("DDS").contains("LDM.ModeCapability_T"));
        
        
        
        
        boolean platformCapFound = false;
        boolean navAttitudeFound = false;
        boolean navPositionFound = false;
        
        List<Structure> pojos = conf.getPojos();
        for(Structure pojo : pojos) {
            if ("LDM.platform.PlatformCapability_T".equals(pojo.getName())) platformCapFound = true;
            if ("LDM.navigation.NavAttitudeState_T".equals(pojo.getName())) navAttitudeFound = true;
            if ("LDM.navigation.NavPositionState_T".equals(pojo.getName())) navPositionFound = true;
        }
        
        assertTrue(platformCapFound);
        assertTrue(navAttitudeFound);
        assertTrue(navPositionFound);
        
        Structure pojo = conf.getPojoStructure("LDM.platform.PlatformCapability_T");
        assertNotNull(pojo);
        assertEquals(3, pojo.getElementsSize());
        assertEquals(BasicTypeEnum.INT,((BasicType)pojo.getElement(0).elementType()).getBasicTypeEnum());
        assertEquals("resourceId", pojo.getElement(0).name());
        // Test ENUM
        assertNotNull(pojo.getElement("resourceIdType"));
        assertEquals("resourceIdType", pojo.getElement("resourceIdType").name());
        assertEquals(StructureRef.class, pojo.getElement("resourceIdType").elementType().getClass());
        // Test ARRAY
        assertNotNull(pojo.getElement("coreCapability.softwareVersions"));
        assertEquals(ArrayType.class, pojo.getElement("coreCapability.softwareVersions").elementType().getClass());
        
        assertEquals("softwareVersions", pojo.getElement("coreCapability.softwareVersions").name());
        assertNotNull(pojo.getElement("coreCapability.softwareVersions.softwareModuleName"));
        assertEquals(BasicTypeEnum.STRING, ((BasicType)pojo.getElement("coreCapability.softwareVersions.softwareModuleName").elementType()).getBasicTypeEnum());
        
        //TODO complete with more tests
        assertNotNull(pojo.getElement("coreCapability.supportedModes.isOnCapable"));
        
    }

    @Test
    public void testMySimTankSample() {
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:hla-RPRFOM.edu"));
        assertNotNull(conf);
        List<String> hlaSupported = conf.supportedPojos("HLA"); 
        assertNotNull(hlaSupported);
        assertFalse(hlaSupported.isEmpty());
        LOG.debug("OK");
        for (String s : hlaSupported) {
            LOG.debug("HLA Supported: " + s);
        }
        assertTrue(hlaSupported.contains("edu.cyc14.essais.pojo.rprfom.BaseEntity"));
    }
}
