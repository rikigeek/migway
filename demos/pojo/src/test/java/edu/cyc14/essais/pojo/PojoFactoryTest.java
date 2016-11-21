package edu.cyc14.essais.pojo;

import static org.junit.Assert.*;

import org.junit.Test;

public class PojoFactoryTest {

    @Test
    public void testDebugPlatform() {
        PlatformCapability_T  p = PojoFactory.createPlatform();
        assertNotNull(p);
        
        PojoFactory.debugPlatform(p);
    }

}
