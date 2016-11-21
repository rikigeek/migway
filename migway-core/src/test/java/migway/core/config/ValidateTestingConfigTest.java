package migway.core.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

/**
 * Test the structures configuration from migway:testingconfig embedded file
 * @author Sébastien Tissier
 *
 */
public class ValidateTestingConfigTest {

    @Test
    public void testConfig() {
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:testingconfig"));
        assertTestingConfig(conf);
    }
    public static void assertTestingConfig(ConfigHelper conf) {
        ArrayType at;

        assertNotNull(conf);
        assertEquals(5, conf.getPojos().size());
        assertNotNull(conf.supportedPojos("HLA"));
        assertNotNull(conf.supportedPojos("DDS"));
        assertNotNull(conf.supportedPojos("SOCKET"));

        // Check values of first Class
        ClassStructure csa = (ClassStructure) conf.getPojoStructure("ns.classA");
        assertTrue(csa.isSupported("HLA"));
        assertTrue(csa.isSupported("DDS"));
        assertTrue(csa.isSupported("SOCKET"));
        assertEquals(6, csa.getElementsSize());
        // check name of each element
        assertEquals("basicInt1", csa.getElement(0).name());
        assertEquals("refClassB", csa.getElement(1).name());
        assertEquals("arrayString1", csa.getElement(2).name());
        assertEquals("arrayClassC", csa.getElement(3).name());
        assertEquals("arrayArray1", csa.getElement(4).name());
        assertEquals("day", csa.getElement(5).name());

        // check type of each element
        assertTrue(csa.getElement(0).elementType() instanceof BasicType);
        assertTrue(csa.getElement(1).elementType() instanceof StructureRef);
        assertTrue(csa.getElement(2).elementType() instanceof ArrayType);
        assertTrue(csa.getElement(3).elementType() instanceof ArrayType);
        assertTrue(csa.getElement(4).elementType() instanceof ArrayType);
        assertTrue(csa.getElement(5).elementType() instanceof StructureRef);

        // Check each element
        // -- #0
        assertEquals(BasicTypeEnum.INT, ((BasicType) csa.getElement(0).elementType()).getBasicTypeEnum());

        // -- #1
        assertEquals("ns.classB", ((StructureRef) csa.getElement(1).elementType()).getStructureName());

        // -- #2
        at = (ArrayType) csa.getElement(2).elementType();
        assertEquals(BasicTypeEnum.STRING, ((BasicType) at.getElementType()).getBasicTypeEnum());
        assertEquals(0, at.getMin());
        assertEquals(0, at.getMax());

        // -- #3
        at = (ArrayType) csa.getElement(3).elementType();
        assertEquals("ns.classC", ((StructureRef) at.getElementType()).getStructureName());
        assertEquals(0, at.getMin());
        assertEquals(10, at.getMax());

        // -- #4
        at = (ArrayType) csa.getElement(4).elementType();
        assertEquals(ArrayType.class, at.getElementType().getClass());
        assertEquals(1, at.getMin());
        assertEquals(6, at.getMax());

        // -- #4 - the array inside it
        at = (ArrayType) at.getElementType();
        assertEquals(BasicTypeEnum.INT, ((BasicType) at.getElementType()).getBasicTypeEnum());
        assertEquals(10, at.getMin());
        assertEquals(10, at.getMax());

        // -- #5
        assertEquals("ns.enumA", ((StructureRef) csa.getElement(5).elementType()).getStructureName());

        // Check second class
        ClassStructure csb = (ClassStructure) conf.getPojoStructure("ns.classB");
        assertNotNull(csb);
        assertTrue(csb.isSupported("HLA"));
        assertFalse(csb.isSupported("DDS"));
        assertFalse(csb.isSupported("SOCKET"));
        assertEquals(3, csb.getElementsSize());
        assertEquals("x", csb.getElement(0).name());
        assertEquals("y", csb.getElement(1).name());
        assertEquals("z", csb.getElement(2).name());

        assertEquals(BasicTypeEnum.DOUBLE, ((BasicType) csb.getElement(0).elementType()).getBasicTypeEnum());
        assertEquals(BasicTypeEnum.DOUBLE, ((BasicType) csb.getElement(1).elementType()).getBasicTypeEnum());
        assertEquals(BasicTypeEnum.DOUBLE, ((BasicType) csb.getElement(2).elementType()).getBasicTypeEnum());

        // Namespace is missing, must return null
        Structure snull = conf.getPojoStructure("classC");
        assertNull(snull);
        
        // Check the last class
        ClassStructure csc = (ClassStructure)conf.getPojoStructure("ns.classC");
        assertTrue(csc.isSupported("HLA"));
        assertTrue(csc.isSupported("DDS"));
        assertFalse(csc.isSupported("SOCKET"));
        
        assertEquals(1, csc.getElementsSize());
        assertEquals("moves", csc.getElement(0).name());
        at = (ArrayType) csc.getElement(0).elementType();

        assertEquals("ns.classB", ((StructureRef)at.getElementType()).getStructureName());
        assertEquals(0, at.getMin());
        assertEquals(50, at.getMax());
        
        // Check Enum
        EnumStructure ea = (EnumStructure)conf.getPojoStructure("ns.enumA");
        assertTrue(ea.isSupported("HLA"));
        assertTrue(ea.isSupported("DDS"));
        assertTrue(ea.isSupported("SOCKET"));
        
        assertEquals(7, ea.size());
        assertEquals(0, ea.elementPosition("MON"));
        assertEquals(1, ea.elementPosition("TUE"));
        assertEquals(2, ea.elementPosition("WED"));
        assertEquals(3, ea.elementPosition("THU"));
        assertEquals(4, ea.elementPosition("FRI"));
        assertEquals(5, ea.elementPosition("SAT"));
        assertEquals(6, ea.elementPosition("SUN"));
        assertEquals(-1, ea.elementPosition("DIM"));
        
        // Check Union
        UnionStructure ua = (UnionStructure)conf.getPojoStructure("ns.unionA");
        assertTrue(ua.isSupported("HLA")); // should be ok, should return unionA (sharedRemoteName)
        assertTrue(ua.isSupported("DDS"));
        assertTrue(ua.isSupported("SOCKET"));
        
        assertEquals(3, ua.getElementsSize());
        assertEquals("choice", ua.getDiscrimant().name());
        assertTrue(ua.getDiscrimant().elementType() instanceof BasicType);
        assertEquals(BasicTypeEnum.BOOLEAN, ((BasicType)ua.getDiscrimant().elementType()).getBasicTypeEnum());
        assertEquals(2, ua.getAlternatives().size());
        assertNull(ua.getAlternative("oui"));
        assertNotNull(ua.getAlternative("true"));
        assertNotNull(ua.getAlternative(Boolean.toString(true)));
        assertNotNull(ua.getAlternative(Boolean.toString(false)));
        assertEquals("static", ua.getAlternative(Boolean.toString(false)));
        assertEquals("mobile", ua.getAlternative(Boolean.toString(true)));
        assertEquals("static", ua.getAlternative(Boolean.toString(false)));
        // Check content of Static alternative
        assertTrue(ua.getElement("static").elementType() instanceof StructureRef);
        StructureRef et = (StructureRef) ua.getElement("static").elementType();
        assertEquals("ns.classB", et.getStructureName());
        // Check content of Mobile alternative
        assertTrue(ua.getElement("mobile").elementType() instanceof StructureRef);
        et = (StructureRef) ua.getElement("mobile").elementType();
        assertEquals("ns.classC", et.getStructureName());
        // Check unknown element
        assertNull(ua.getElement("wheatfield"));
        assertNull(ua.getElement(80)); // Out of bound 
        
    }

}
