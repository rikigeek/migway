package migway.core.config;

import static org.junit.Assert.*;
import migway.core.config.ClassStructure;

import org.junit.Test;

/** 
 * Test Structure access API
 * 
 * @author Sébastien Tissier
 *
 */
public class PojoClassStructureTest {

    /**
     * ClassStructure must always return null when getRemoteName a null or empty
     * value
     * ClassStructure must always return false when check supported interface whose
     * name is null or empty
     * Name is always test.toto
     * 
     * @param pojo
     */
    private void commonAssert(ClassStructure pojo) {
        assertNull(pojo.getRemoteName(null));
        assertNull(pojo.getRemoteName(""));
        assertFalse(pojo.isSupported(null));
        assertFalse(pojo.isSupported(""));
    }

    @Test
    public void testNullName() {
        // In any circumstances, throw an illegalArgumentException when name is
        // null

        // PojoClassStructure(String, String, String)
        //
        try {
            new ClassStructure(null, null, null);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            new ClassStructure(null, new String[] {"interface1"}, new String[]{"name"});
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            new ClassStructure(null, new String[] {"interface1"}, null);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            new ClassStructure(null, null, new String[]{"name"});
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        // PojoClassStructure(String)
        try {
            new ClassStructure(null);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }


    @Test
    public void testPojoClassStructureStringStringArrayStringArray() {
        ClassStructure pojo;

        //
        pojo = new ClassStructure("test.toto", (String[]) new String[] { "interface1" }, (String[]) new String[] { "toto" });

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertFalse(pojo.isSupported("interface2"));

        assertEquals("toto", pojo.getRemoteName("interface1"));
        assertNull(pojo.getRemoteName("interface2"));

        //
        pojo = new ClassStructure("test.toto", (String[]) new String[] { "interface1" }, (String[]) null);

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertFalse(pojo.isSupported("interface2"));

        assertEquals("test.toto", pojo.getRemoteName("interface1"));
        assertNull(pojo.getRemoteName("interface2"));

        //
        pojo = new ClassStructure("test.toto", (String[]) null, (String[]) new String[] { "toto" });

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertTrue(pojo.isSupported("interface2"));

        assertEquals("toto", pojo.getRemoteName("interface1"));
        assertEquals("toto", pojo.getRemoteName("interface2"));

        //
        pojo = new ClassStructure("test.toto", (String[]) null, (String[]) null);

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertTrue(pojo.isSupported("interface2"));

        assertEquals("test.toto", pojo.getRemoteName("interface1"));
        assertEquals("test.toto", pojo.getRemoteName("interface2"));

        // test different size interface list & remote name list
        pojo = new ClassStructure("test.toto", new String[] { "interface1", "interface2" }, new String[] { "toto" });

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertTrue(pojo.isSupported("interface2"));
        assertFalse(pojo.isSupported("interface3"));

        assertEquals("toto", pojo.getRemoteName("interface1"));
        assertEquals("toto", pojo.getRemoteName("interface2"));
        assertNull(pojo.getRemoteName("interface3"));

        // test different size interface list & remote name list
        pojo = new ClassStructure("test.toto", new String[] { "interface1", "interface2" }, new String[] { "toto", "tutu", "titi" });

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertTrue(pojo.isSupported("interface2"));
        assertFalse(pojo.isSupported("interface3"));

        assertEquals("toto", pojo.getRemoteName("interface1"));
        assertEquals("tutu", pojo.getRemoteName("interface2"));
        assertNull(pojo.getRemoteName("interface3"));

        // test different size interface list & remote name list
        pojo = new ClassStructure("test.toto", new String[] { "interface1", "interface2" }, new String[] {});

        commonAssert(pojo);
        assertTrue(pojo.isSupported("interface1"));
        assertTrue(pojo.isSupported("interface2"));
        assertFalse(pojo.isSupported("interface3"));

        assertEquals("test.toto", pojo.getRemoteName("interface1"));
        assertEquals("test.toto", pojo.getRemoteName("interface2"));
        assertNull(pojo.getRemoteName("interface3"));
    }


    /**
     * Test of full Constructor with invalid parameters (list with empty and/or
     * null values, blank values...)
     */
    @Test
    public void testIncorrectValues() {
        ClassStructure pojo;

        try {
            pojo = new ClassStructure("", new String[] {}, new String[] {});
            fail("IllegalArgumentException should has been thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            pojo = new ClassStructure(" ", new String[] { "i" }, null);
            fail("IllegalArgumentException should has been thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        pojo = new ClassStructure("test.toto", new String[] { "int1", " ", null, "interface4" }, null);
        assertTrue(pojo.isSupported("int1"));
        assertTrue(pojo.isSupported("interface4"));
        assertEquals("test.toto", pojo.getRemoteName("int1"));
        assertFalse(pojo.isSupported("int3"));
        assertNull(pojo.getRemoteName("int3"));

        pojo = new ClassStructure("test.toto", new String[] { "int1", " ",       null, "interface4",     "int5", null,   null, "myinterface",    "" }, 
                                              new String[] { " ",     "toto",    null, null,             "titi", "tutu", null, "tyty",           null, "chewi", "obi" });
        assertTrue(pojo.isSupported("int1"));
        assertTrue(pojo.isSupported("interface4"));
        assertTrue(pojo.isSupported("int5"));
        assertFalse(pojo.isSupported("int3"));
        assertFalse(pojo.isSupported("")); // empty or null are not supported
        // since remoteName list is not empty, values from this list are
        // supposed to be valid (except null values that are replaced by empty
        // string
        assertEquals("test.toto", pojo.getRemoteName("int1"));
        assertEquals("test.toto", pojo.getRemoteName("interface4"));
        assertEquals("titi", pojo.getRemoteName("int5"));
        assertEquals("tyty", pojo.getRemoteName("myinterface"));
        
        assertNull(pojo.getRemoteName(""));

    }

}
