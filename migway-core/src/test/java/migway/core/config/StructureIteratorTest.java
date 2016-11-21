package migway.core.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import migway.core.helper.PojoLoaderHelper;

import org.junit.Before;
import org.junit.Test;

public class StructureIteratorTest {

    private final ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:dds-GVA.LDM"));
    private PojoLoaderHelper helper = PojoLoaderHelper.INSTANCE;
    
    @Before
    public void configure() {
        helper.addPojoLocation("../demos/pojo/target/classes");
        helper.addPojoLocation("../demos/pojo/target");
        helper.addPojoLocation("src/test/resources");
    }

    @Test
    public void testStructureIteration() {
        assertNotNull(conf);

        Structure struct;

        struct = conf.getPojoStructure("LDM.ModeCapability_T");
        assertNotNull(struct);
        assertEquals(5, struct.size());

        struct = conf.getPojoStructure("LDM.navigation.NavErrorDataType_T");
        assertNotNull(struct);
        assertEquals(6, struct.size());

        struct = conf.getPojoStructure("LDM.SoftwareVersionDescriptor_T");
        assertNotNull(struct);
        assertEquals(2, struct.size());

        struct = conf.getPojoStructure("LDM.CommonCapability_T");
        assertNotNull(struct);
        assertEquals(15, struct.size());

        Structure s = conf.getPojoStructure("LDM.platform.PlatformCapability_T");
        assertNotNull(s);
        assertEquals(21, s.size());

        int listIdx = 0;
        HierarchicalElement list[] = new HierarchicalElement[17];

        System.out.println("[testStructureIteration] you must see the recursive list of all fields of " + s.getName());
        StructureIterator it = s.iterator();
        while (it.hasNext()) {
            HierarchicalElement e = it.next();
            assertNotNull(e);
            System.out.println(e.path() + e.name());
            list[listIdx] = e;
            listIdx++;
        }
        System.out.println("[testStructureIteration] END OF TEST ****");
        assertEquals(17, listIdx);
        listIdx = 0;
        for (HierarchicalElement e : s) {
            assertNotNull(e);
            assertEquals(list[listIdx].fullName(), e.fullName());
            listIdx++;
        }

        assertEquals(17, listIdx);
    }

    @Test
    public void testHierarchicalElement() {
        Structure s = conf.getPojoStructure("LDM.platform.PlatformCapability_T");
        boolean fManufacturer = false;
        boolean fIsOffCapable = false;
        for (HierarchicalElement e : s) {
            assertNotNull(e);
            if (e.element().name().equals("manufacturer")) {
                fManufacturer = true;
                assertEquals(1, e.getElementPath().size());
                assertEquals(1, e.getStructurePath().size());
                // Parent Structure that contains leaf element
                // Java binary name
                assertEquals("LDM.platform.PlatformCapability_T", e.getStructure(0).getName());
                // field name
                assertEquals("coreCapability", e.getElement(0).name());

                // Structure (Java binary name) of the leaf class
                assertEquals("LDM.CommonCapability_T", e.element().getPojo().getName());
            }
            if (e.element().name().equals("isOffCapable")) {
                fIsOffCapable = true;

                assertEquals(2, e.getElementPath().size());
                assertEquals(2, e.getStructurePath().size());

                assertEquals("LDM.platform.PlatformCapability_T", e.getStructure(1).getName());
                assertEquals("LDM.platform.PlatformCapability_T", e.getElement(1).getPojo().getName());
                assertEquals("coreCapability", e.getElement(1).name());

                assertEquals("LDM.CommonCapability_T", e.getStructure(0).getName());
                assertEquals("LDM.CommonCapability_T", e.getElement(0).getPojo().getName());
                assertEquals("supportedModes", e.getElement(0).name());

                assertEquals("LDM.ModeCapability_T", e.element().getPojo().getName());

            }

        }

        assertTrue(fIsOffCapable);
        assertTrue(fManufacturer);
    }

    @Test
    public void testRecursivePojoLoading() throws ClassNotFoundException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, InstantiationException {
        Structure s = conf.getPojoStructure("LDM.platform.PlatformCapability_T");
        boolean fManufacturer = false;
        boolean fIsOffCapable = false;
        Object rootPojo = reflectiveLoadPlatform();
        for (HierarchicalElement hElement : s) {
            assertNotNull(hElement);
            if (hElement.element().name().equals("manufacturer")) {
                fManufacturer = true;
                assertEquals(1, hElement.getElementPath().size());
                assertEquals(1, hElement.getStructurePath().size());
                // Parent Structure that contains leaf element
                // Java binary name
                assertEquals("LDM.platform.PlatformCapability_T", hElement.getStructure(0).getName());
                // field name
                assertEquals("coreCapability", hElement.getElement(0).name());

                // Structure (Java binary name) of the leaf class
                assertEquals("LDM.CommonCapability_T", hElement.element().getPojo().getName());

                Object pojo = null;

                String pojoClassName;
                Field field = null;
                int pathSize = hElement.size();
                pojo = rootPojo;
                for (int i = pathSize; i > 0; i--) {
                    pojoClassName = hElement.getElement(i - 1).getPojo().getName();
                    String fieldName = hElement.getElement(i - 1).name();
                    field = helper.getField(pojoClassName, fieldName);
                    pojo = field.get(pojo);
                }

            }
            if (hElement.element().name().equals("isOffCapable")) {
                fIsOffCapable = true;

                assertEquals(2, hElement.getElementPath().size());
                assertEquals(2, hElement.getStructurePath().size());

                assertEquals("LDM.platform.PlatformCapability_T", hElement.getStructure(1).getName());
                assertEquals("LDM.platform.PlatformCapability_T", hElement.getElement(1).getPojo().getName());
                assertEquals("coreCapability", hElement.getElement(1).name());

                assertEquals("LDM.CommonCapability_T", hElement.getStructure(0).getName());
                assertEquals("LDM.CommonCapability_T", hElement.getElement(0).getPojo().getName());
                assertEquals("supportedModes", hElement.getElement(0).name());

                assertEquals("LDM.ModeCapability_T", hElement.element().getPojo().getName());

            }

        }

        assertTrue(fIsOffCapable);
        assertTrue(fManufacturer);
    }

    @Test
    public void testReflectLoadPlatform() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Object pojo = null;
        try {
            pojo = reflectiveLoadPlatform();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            fail(e.toString());
        }

        assertNotNull(pojo);
        assertEquals("LDM.platform.PlatformCapability_T", pojo.getClass().getName());

        Class<?> pojoFact = helper.getPojoClass("LDM.PojoFactory");
        Method m = pojoFact.getMethod("debugPlatform", helper.getPojoClass("LDM.platform.PlatformCapability_T"));
        System.out.println("[testReflectLoadPlatform] you must see the content of a " + pojo.getClass().getSimpleName()
                + " created by reflection, with string = 'toto', int = 42, and long = 42*42, and bool = true. Enum israndom");
        m.invoke(null, pojo);
        System.out.println("[testReflectLoadPlatform] END OF TEST****");

    }

    private Object reflectiveLoadPlatform() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object result = null;

        result = recursiveLoadClass("LDM.platform.PlatformCapability_T");
        return result;
    }

    private Object recursiveLoadClass(String classbinaryName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // System.out.println("recursive load " + classbinaryName);
        Object result = helper.loadPojo(classbinaryName);
        Field[] fields = result.getClass().getFields();
        int nbFields = fields.length;

        for (int i = 0; i < nbFields; i++) {
            Field f = fields[i];
            Class<?> fType = f.getType();
            // System.out.println(f.getName() + " = " + fType.toString());
            if (fType.toString().equals("byte")) {
                f.setByte(result, (byte) 42);
            }
            if (fType.toString().equals("short")) {
                f.setShort(result, (short) 42);
            }
            if (fType.toString().equals("int")) {
                f.setInt(result, 42);
            }
            if (fType.toString().equals("long")) {
                f.setLong(result, 42 * 42);
            }
            if (fType.toString().equals("boolean")) {
                f.setBoolean(result, true);
            }
            if (fType.toString().equals("double")) {
                f.setDouble(result, 42.4242);
            }
            if (fType.toString().equals("float")) {
                f.setFloat(result, 42.42f);
            }
            if (fType.toString().equals("char")) {
                f.setChar(result, '*');
            }
            if (fType.toString().startsWith("class ")) {
                String className = fType.toString().substring(6);
                className = fType.getName();

                if (className.equals("java.lang.String")) {
                    f.set(result, "toto");
                } else {
                    if (fType.isArray()) {
                        // Array
                        Class<?> c = fType.getComponentType();
                        Object o = Array.newInstance(c, 2);
                        f.set(result, o);
                        Array.set(o, 0, recursiveLoadClass(c.getName()));
                        // Attention, vérifier avant si ce n'est pas un type
                        // primitif ou un String
                    } else if (fType.isEnum()) {
                        // Enum
                        List<?> t = Arrays.asList(fType.getEnumConstants());
                        int idx = (int) Math.floor(Math.random() * t.size());
                        f.set(result, t.get(idx));
                        for (int j = 0; j < t.size(); j++) {
                            // System.out.println(t.get(j).toString() + " / " +
                            // t.get(j).getClass().toString());
                        }
                    } else {
                        // standard class
                        // loading another class
                        f.set(result, recursiveLoadClass(className));
                    }
                }

            }
        }

        return result;

    }

    // @Test
    // public void test() {
    // assertEquals(42, (int) '*');
    // long sec = LocalDate.of(1979, 9, 18).toEpochDay() * 24 * 3600;
    // long sec2 = LocalDate.of(1971, 1, 1).toEpochDay() * 24 * 3600;
    // // LocalDate dt = LocalDate.ofEpochDay(Long.MAX_VALUE / (24 * 3600));
    // System.out.println(sec + " seconds since ..");
    // System.out.println(sec2 + " seconds since epoch");
    // System.out.println(Long.MAX_VALUE / (24 * 3600) + " max seconds");
    // // System.out.println(dt.toString() + " max date");
    // }

    @Test
    public void testPojoFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<?> c = helper.getPojoClass("LDM.PojoFactory");
        assertNotNull(c);
        Method m = c.getDeclaredMethod("createPlatform");
        assertNotNull(m);
        Object pojo = m.invoke(null);
        assertNotNull(pojo);
        assertEquals("LDM.platform.PlatformCapability_T", pojo.getClass().getName());
    }

}
