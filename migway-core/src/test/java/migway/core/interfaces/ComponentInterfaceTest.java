package migway.core.interfaces;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import migway.core.config.ConfigHelper;
import migway.core.config.HierarchicalElement;
import migway.core.config.Structure;
import migway.core.helper.PojoLoaderHelper;
import migway.core.interfaces.ComponentInterface;
import migway.core.interfaces.FakeDdsInterface;

import org.junit.Before;
import org.junit.Test;

public class ComponentInterfaceTest {
    byte[] _testBuffer = { 0, 0, 0, 42, 0, 0, 0, 15, 0, 0, 0, 4, 0, 83, 0, 69, 0, 84, 0, 73, 0, 0, 0, 12, 0, 77, 0, 105, 0, 103, 0, 119, 0,
            97, 0, 121, 0, 83, 0, 97, 0, 109, 0, 112, 0, 108, 0, 101, 0, 0, 0, 15, 0, 65, 0, 32, 0, 116, 0, 101, 0, 115, 0, 116, 0, 32, 0,
            112, 0, 108, 0, 97, 0, 116, 0, 102, 0, 111, 0, 114, 0, 109, 0, 0, 0, 2, 0, 52, 0, 50, 0, 0, 0, 8, 0, 110, 0, 111, 0, 32, 0,
            105, 0, 115, 0, 115, 0, 117, 0, 101, 0, 0, 0, 9, 0, 111, 0, 110, 0, 32, 0, 115, 0, 116, 0, 114, 0, 105, 0, 107, 0, 101, 0, 0,
            0, 9, 0, 70, 0, 111, 0, 114, 0, 116, 0, 121, 0, 32, 0, 84, 0, 119, 0, 111, 1, 1, 0, 0, 0, 0, 0, 0, 0, 18, 68, 56, -128, 0, 0,
            0, 0, 0, 0, 0, 123 };
    byte[] testBuffer = {0, 0, 0, 42, 0, 0, 0, 15, 0, 0, 0, 4, 0, 83, 0, 69, 0, 84, 0, 73, 0, 0, 0, 12, 0, 77, 0, 105, 0, 103, 0, 119, 0, 97, 0, 121, 0, 83, 0, 97, 0, 109, 0, 112, 0, 108, 0, 101, 0, 0, 0, 15, 0, 65, 0, 32, 0, 116, 0, 101, 0, 115, 0, 116, 0, 32, 0, 112, 0, 108, 0, 97, 0, 116, 0, 102, 0, 111, 0, 114, 0, 109, 0, 0, 0, 2, 0, 52, 0, 50, 0, 0, 0, 8, 0, 110, 0, 111, 0, 32, 0, 105, 0, 115, 0, 115, 0, 117, 0, 101, 0, 0, 0, 9, 0, 111, 0, 110, 0, 32, 0, 115, 0, 116, 0, 114, 0, 105, 0, 107, 0, 101, 0, 0, 0, 9, 0, 70, 0, 111, 0, 114, 0, 116, 0, 121, 0, 32, 0, 84, 0, 119, 0, 111, 0, 0, 0, 2, 0, 0, 0, 4, 0, 99, 0, 111, 0, 114, 0, 101, 0, 0, 0, 5, 0, 98, 0, 45, 0, 48, 0, 46, 0, 49, 0, 0, 0, 9, 0, 98, 0, 101, 0, 110, 0, 99, 0, 104, 0, 109, 0, 97, 0, 114, 0, 107, 0, 0, 0, 3, 0, 49, 0, 46, 0, 48, 1, 1, 0, 0, 0, 0, 0, 0, 0, 18, 68, 56, -128, 0, 0, 0, 0, 0, 0, 0, 123};

    // Create a new Fake DDS Interface: it's simply based on DefaultInterface,
    // changing only Interface and Header. Also load it with a sample configFile
    private ComponentInterface interfaceProcessor = new FakeDdsInterface(ConfigHelper.loadConfig(new File("migway:dds-GVA.LDM")));
    private PojoLoaderHelper helper = PojoLoaderHelper.INSTANCE;
    private Class<?> pojoFactory = null;
    private Object pojoPlatform = null;

    @Before
    public void configure() {
        helper.addPojoLocation("../demos/pojo/target/classes");
        helper.addPojoLocation("../demos/pojo/target");
        helper.addPojoLocation("src/test/resources");
    }

    @Before
    public void beforeTests() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        // create a Factory to create some testing POJO
        pojoFactory = helper.getPojoClass("LDM." + "PojoFactory");
        assertNotNull(pojoFactory);
        // We create one PlatformCapability_T
        Method m = pojoFactory.getMethod("createPlatform");
        assertNotNull(m);
        pojoPlatform = m.invoke(null);
        assertNotNull(pojoPlatform);
        // Check that the interface is correct
        assertEquals("DDS", interfaceProcessor.getInterfaceTypeName());

        // list of supported POJO is not empty
        List<String> supp = interfaceProcessor.configHelper.supportedPojos(interfaceProcessor.getInterfaceTypeName());
        assertNotNull(supp);
        assertFalse(supp.isEmpty());
        // Optional: display this list of supported POJO
        // for (String s: supp)
        // System.out.println(s);
    }

    @Test
    public void testConfigHelper() {
    }

    @Test
    public void testWriteBasicType() {

    }

    /**
     * Test encoding of a POJO
     * 
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    @Test
    public void testWritePojo() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
            SecurityException, InvocationTargetException, NoSuchFieldException {
        ByteBuffer buf = (ByteBuffer) interfaceProcessor.writePojo(pojoPlatform);
        assertEquals(1500, buf.capacity());
        assertEquals(237, buf.position());
        assertNotNull(buf);
        buf.limit(buf.position());

        // old size (without arrays) = 175
        byte[] dataBuf = new byte[237];
        assertEquals(237, buf.position());
        buf.rewind();
        buf.get(dataBuf);
        System.out.println(Arrays.toString(dataBuf));

        Class<?> pojoClass = pojoPlatform.getClass();
        Structure s = interfaceProcessor.configHelper.getPojoStructure(pojoClass.getName());

        buf.rewind();

        // resourceId : 42
        assertEquals(42, buf.getInt());
        // idtype = 15 (enum: 16th element of the enum)
        assertEquals(15, buf.getInt());
        // manufacturer: SETI. Lenght = 4
        assertEquals(4, buf.getInt());
        assertEquals('S', buf.getChar());
        assertEquals('E', buf.getChar());
        assertEquals('T', buf.getChar());
        assertEquals('I', buf.getChar());
        // ProductName: MigwaySample
        assertEquals(12, buf.getInt());
        assertEquals('M', buf.getChar());
        assertEquals('i', buf.getChar());
        assertEquals('g', buf.getChar());
        assertEquals('w', buf.getChar());
        assertEquals('a', buf.getChar());
        assertEquals('y', buf.getChar());
        assertEquals('S', buf.getChar());
        assertEquals('a', buf.getChar());
        assertEquals('m', buf.getChar());
        assertEquals('p', buf.getChar());
        assertEquals('l', buf.getChar());
        assertEquals('e', buf.getChar());
        // description: 'A test platform' (size 15)
        assertEquals(15, buf.getInt());
        // skip next 15 char
        for (int i = 0; i < 15; i++)
            buf.getChar();

        // serialNumber:'42' (size 2)
        assertEquals(2, buf.getInt());
        assertEquals('4', buf.getChar());
        assertEquals('2', buf.getChar());

        // issue: "no issue" (size = 8)
        assertEquals(8, buf.getInt());
        for (int i = 0; i < 8; i++)
            buf.getChar();

        // modStrike: "on strike" (size=9)
        assertEquals(9, buf.getInt());
        for (int i = 0; i < 9; i++)
            buf.getChar();

        // natoStockNumber: "Forty Two" (size = 9)
        assertEquals(9, buf.getInt());
        for (int i = 0; i < 9; i++)
            buf.getChar();

        // softwareVersions: array not implemented. Should be: 2 elements
        assertEquals(2, buf.getInt());
        // First element;
        // softwareModuleName = "core"
        assertEquals(4, buf.getInt());
        assertEquals('c', buf.getChar());
        assertEquals('o', buf.getChar());
        assertEquals('r', buf.getChar());
        assertEquals('e', buf.getChar());
        // versionNumber = "b-0.1"
        assertEquals(5, buf.getInt());
        assertEquals('b', buf.getChar());
        assertEquals('-', buf.getChar());
        assertEquals('0', buf.getChar());
        assertEquals('.', buf.getChar());
        assertEquals('1', buf.getChar());
        // Second element;
        // softwareModuleName = "benchmark"
        assertEquals(9, buf.getInt());
        assertEquals('b', buf.getChar());
        assertEquals('e', buf.getChar());
        assertEquals('n', buf.getChar());
        assertEquals('c', buf.getChar());
        assertEquals('h', buf.getChar());
        assertEquals('m', buf.getChar());
        assertEquals('a', buf.getChar());
        assertEquals('r', buf.getChar());
        assertEquals('k', buf.getChar());
        // versionNumber = "1.0"
        assertEquals(3, buf.getInt());
        assertEquals('1', buf.getChar());
        assertEquals('.', buf.getChar());
        assertEquals('0', buf.getChar());

        // isOffCapable: true
        assertEquals(1, buf.get());
        // followed by 4 bool. Last one is false
        assertEquals(1, buf.get());
        assertEquals(0, buf.get());
        assertEquals(0, buf.get());
        assertEquals(0, buf.get());
        // There should be some padding ?

        // timeOfDataGeneration.seconds: LocalDate.of(1979, 9, 18).toEpochDay()
        // * 3600 * 24
        assertEquals(LocalDate.of(1979, 9, 18).toEpochDay() * 3600 * 24, buf.getLong());
        // timeOfDataGeneration.nanoseconds: 123
        assertEquals(123, buf.getLong());
        assertEquals(237, buf.position());

        // Field f = pojoClass.getField("resourceId");
        // f = pojoClass.getField("resourceIdType");

        for (HierarchicalElement el : s) {
            System.out.println(el.fullName());
        }
    }

    @Test
    public void testReadBasicType() {
    }

    @Test
    public void testFillPojo() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
            ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ByteBuffer buf = ByteBuffer.wrap(testBuffer);
        assertNotNull(buf);
        String structureName = "LDM.platform." + "PlatformCapability_T";
        Object pojo = interfaceProcessor.readPojo(buf, structureName);
        assertNotNull(pojo);
        Class<?> c = pojo.getClass();
        Field f; // the field to access the pojo
        f = c.getField("resourceId");
        assertEquals(42, f.getInt(pojo));
        f = c.getField("coreCapability");
        Object coreCapability = f.get(pojo);
        assertNotNull(coreCapability);
        Class<?> c2 = coreCapability.getClass();
        f = c2.getField("supportedModes");
        Object supportedModes = f.get(coreCapability);
        assertNotNull(supportedModes);
        f = c2.getField("timeOfDataGeneration");
        Object timeOfDataGeneration = f.get(coreCapability);
        assertNotNull(timeOfDataGeneration);
        //
        f = c2.getField("manufacturer");
        assertEquals("SETI", (String) f.get(coreCapability));
        f = c2.getField("productName");
        assertEquals("MigwaySample", (String) f.get(coreCapability));
        f = c2.getField("description");
        assertEquals("A test platform", (String) f.get(coreCapability));
        f = c2.getField("serialNumber");
        assertEquals("42", (String) f.get(coreCapability));
        f = c2.getField("issue");
        assertEquals("no issue", (String) f.get(coreCapability));
        f = c2.getField("modStrike");
        assertEquals("on strike", (String) f.get(coreCapability));
        f = c2.getField("natoStockNumber");
        assertEquals("Forty Two", (String) f.get(coreCapability));
        //
        Class<?> c3 = supportedModes.getClass();
        f = c3.getField("isOffCapable");
        assertTrue(f.getBoolean(supportedModes));
        f = c3.getField("isOnCapable");
        assertTrue(f.getBoolean(supportedModes));
        f = c3.getField("isStandbyCapable");
        assertFalse(f.getBoolean(supportedModes));
        f = c3.getField("isMaintenanceCapable");
        assertFalse(f.getBoolean(supportedModes));
        f = c3.getField("isTrainingCapable");
        assertFalse(f.getBoolean(supportedModes));
        //
        Class<?> c4 = timeOfDataGeneration.getClass();
        f = c4.getField("seconds");
        assertEquals(LocalDate.of(1979, 9, 18).toEpochDay() * 3600 * 24, f.getLong(timeOfDataGeneration));
        f = c4.getField("nanoseconds");
        assertEquals(123, f.getLong(timeOfDataGeneration));
        // Array. Not done yet
        f = c2.getField("softwareVersions");
        Object softwareVersions = f.get(coreCapability);
        assertNotNull(softwareVersions);
        Class<?> c5 = softwareVersions.getClass();
        assertTrue(c5.isArray());
        assertEquals(2, Array.getLength(softwareVersions));
        // First element
        Object svElement = Array.get(softwareVersions, 0);
        assertNotNull(svElement);
        Class<?> c6 = svElement.getClass();
        f = c6.getField("softwareModuleName");
        assertEquals("core", f.get(svElement));
        f = c6.getField("versionNumber");
        assertEquals("b-0.1", f.get(svElement));
        // Second element
        svElement = Array.get(softwareVersions, 1);
        assertNotNull(svElement);
        f = c6.getField("softwareModuleName");
        assertEquals("benchmark", f.get(svElement));
        f = c6.getField("versionNumber");
        assertEquals("1.0", f.get(svElement));
    }

}
