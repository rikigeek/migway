package migway.core.config;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigHelperEqualityTest {
    @Test
    public void testMappingEquality() {
        Mapping m1 = new Mapping();
        Mapping m2 = new Mapping();
        assertEquals(m1, m2);
        m1.setDestination("dest");
        assertNotEquals(m1, m2);
        m2.setDestination("dest");
        assertEquals(m1, m2);
        m1.addSource("source", null);
        assertNotEquals(m1, m2);
        m2.addSource("source", null);
        assertEquals(m1, m2);
                
        KeyModel k1 = new KeyModel();
        KeyModel k2 = new KeyModel();
        
        assertEquals(k1, k2);
        k1.addKeyName("x");
        assertNotEquals(k1, k2);
        k2.addKeyName("x");
        assertEquals(k1, k2);
        
        k1.setExchangeProperty("prop");
        assertNotEquals(k1, k2);
        k2.setExchangeProperty("prop2");
        assertNotEquals(k1, k2);
        k2.setExchangeProperty("prop");
        assertEquals(k1, k2);

        k1.setMessageHeader("head");
        assertNotEquals(k1, k2);
        k2.setMessageHeader("head2");
        assertNotEquals(k1, k2);
        k2.setMessageHeader("head");
        assertEquals(k1, k2);
        
        k1.addKeyName("toto");
        assertNotEquals(k1, k2);
        k2.addKeyName("toto");
        assertEquals(k1, k2);

        
        m1.setDestinationKey(k1);
        assertNotEquals(m1, m2);
        m2.setDestinationKey(k2);
        assertEquals(m1, m2);
        k1.addKeyName("p");
        assertNotEquals(m1, m2);
        k2.addKeyName("p");
        assertEquals(m1, m2);

        k1 = new KeyModel();
        k1.addKeyName("a");
        k2 = new KeyModel();

        m1.addSource("s2", k1);
        m2.addSource("s2", k2);
        assertNotEquals(m1, m2);
        k2.addKeyName("a");
        assertEquals(k1, k2);
        assertEquals(m1, m2);
    }
}
