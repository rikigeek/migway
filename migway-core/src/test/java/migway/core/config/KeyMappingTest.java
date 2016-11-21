package migway.core.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class KeyMappingTest {
    
    @Test
    public void test() {
        ConfigHelper config = ConfigHelper.loadConfig(new File("migway:hla.tanklab"));
        config.appendConfig(new File("migway:hla-RPRFOM.edu"));
        config.appendConfig("migway:map-demo-tanklab");
//        MappingSampleFactory.loadTankLab(config);
        
        assertNotNull(config);
        
        assertNotNull(config.getMappingForDestination("tanklab.Tank").getDestinationKey());
        assertNotNull(config.getMappingForSource("LDM.navigation.NavAttitudeState_T").getSourceKey("LDM.navigation.NavAttitudeState_T"));
        assertNotNull(config.getMappingForSource("LDM.navigation.NavPositionState_T").getSourceKey("LDM.navigation.NavPositionState_T"));
    }

}
