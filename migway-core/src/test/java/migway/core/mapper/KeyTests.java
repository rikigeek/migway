package migway.core.mapper;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyTests {

    @Test
    public void testKeys() {
        // Hash code need to be equals only if instance are equals.
        // if instance are not equals, hash codes can be equals
        KeyHolder a, b;
        a = new KeyHolder(Object.class);
        b = new KeyHolder(Object.class);
        // Empty key holder are equals
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        
        // but empty is not null
        assertFalse(a.equals(null));
        
        // key with one null value is not equal to empty key 
        a.add(null);
        assertNotEquals(a, b);
        
        // both key with one null value are equals
        b.add(null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        
        
        a.add("Toto");
        assertNotEquals(a, b);

        // both key with same string are equals 
        b.add("Toto");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        
        b = new KeyHolder(Object.class);
        // invert the order of values. And keys are not equals
        b.add("Toto");
        assertNotEquals(a, b);
        b.add(null);
        assertNotEquals(a, b);
        
        // Key of different type must be equal if their content are the same
        b = new KeyHolder(KeyHolder.class);
        b.add(null);
        b.add("Toto");
        assertEquals(a,b);
        assertEquals(a.hashCode(), b.hashCode());
        
        
        
    }

}
