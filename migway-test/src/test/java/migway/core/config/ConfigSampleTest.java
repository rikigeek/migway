package migway.core.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class ConfigSampleTest {

    @Test
    public void testLoadConfig() {
        ConfigHelper config;
        config = ConfigSample.loadConfig(new File("migway:sample"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-sample.xml")));
        
        
        config = ConfigSample.loadConfig(new File("migway:hla.tanklab"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-hla.tanklab.xml")));
        
        
        config = ConfigSample.loadConfig(new File("migway:dds-GVA.LDM"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-dds-GVA.LDM.xml")));
        
        
        config = ConfigSample.loadConfig(new File("migway:hla-RPRFOM.edu"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-hla-RPRFOM.edu.xml")));
        

        config = ConfigSample.loadConfig(new File("migway:testingconfig"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-testingconfig.xml")));

        
        config = ConfigSample.loadConfig(new File("migway:map-demo-tanklab"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-map-demo-tanklab.xml")));

        
        config = ConfigSample.loadConfig(new File("migway:key-demo-tanklab"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-key-demo-tanklab.xml")));


        config = ConfigSample.loadConfig(new File("migway:map-sample"));
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("mig-map-sample.xml")));

    }
    

}
