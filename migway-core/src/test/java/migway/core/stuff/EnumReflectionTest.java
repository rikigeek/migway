package migway.core.stuff;

import static org.junit.Assert.*;
import static java.lang.System.out;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import org.junit.Test;

/**
 * Testing enum values reflection
 * @author Sébastien Tissier
 *
 */
public class EnumReflectionTest{

    enum SimpleEnum {
        UN,
        DEUX;
    }
    
    /** 
     * Instance of the class to reflect 
     */
    private SimpleClass toto = new SimpleClass();
    private SimpleClass tutu = new SimpleClass(false);
    
    class SimpleClass {
        public SimpleEnum e;
        public String s;
        public int i;
        public byte[] a;
        public byte b;
        
        public SimpleClass() {
            this(true);
        }
        
        public SimpleClass(boolean init) {
            if (init) {
                e = SimpleEnum.UN;
                s = "chaîne";
                i = 42;
                a = new byte[] {4,2};
                b = 77;
            }
            
        }
    }
    
    @Test
    public void testArrayGet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class<?> classToReflect = toto.getClass(); 
        
        Field fieldArray = classToReflect.getDeclaredField("a");
        assertNotNull(fieldArray);
        
        Class<?> classArray = fieldArray.getType();
        assertNotNull(classArray);
        assertTrue(classArray.isArray());
        // This array contains bytes
        out.println(classArray.getComponentType().isPrimitive());
        out.println(SimpleClass.class.isPrimitive());
        assertEquals("byte", classArray.getComponentType().toString());
        // byte is a primitive type
        assertTrue(classArray.getComponentType().isPrimitive());
        
        // Get the value
        Object arrayValue = fieldArray.get(toto);
        assertNotNull(arrayValue);
        assertNull(fieldArray.get(tutu));
        
        Class<?> arrayValueClass = arrayValue.getClass(); 
        out.println(arrayValue.getClass());
        // Class is the same as the class of the field
        assertEquals(arrayValueClass, classArray);
        
//        displayMethods(arrayValueClass);
        
        out.println("Array size = " + Array.getLength(arrayValue));
//        <?>[] objArray = (Object[])arrayValue;
//        assertNotNull(objArray);
        
        // Test to get a byte as an object
        Field fByte = classToReflect.getDeclaredField("b");
        Object byteValue = fByte.get(toto);
        Class<?> byteValueClass  = byteValue.getClass();
        out.println(byteValueClass);
        out.println(byteValue);
        assertTrue(!byteValueClass.isPrimitive());
        assertEquals("java.lang.Byte", byteValueClass.getName());
        
    }
    
    private void displayMethods(Class<?> classToDebug) {
        Field[] enumFields = classToDebug.getFields();
        for (int i = 0; i < enumFields.length; i ++) {
            System.out.println(enumFields[i].getName() + ":" + enumFields[i].getType().toGenericString() + " " + enumFields[i].isEnumConstant());
        }
        Method[] enumMethods = classToDebug.getMethods();
        for (int i = 0; i < enumMethods.length; i ++) {
            System.out.print(enumMethods[i].getName() + ":" + enumMethods[i].getReturnType().getName() + " " + enumMethods[i].getParameterCount() + " parameters [" + Modifier.toString(enumMethods[i].getModifiers()) + " / "+ enumMethods[i].getDeclaringClass().getName() + "]");
            if (enumMethods[i].getParameterCount() > 0) {
                System.out.print(" (");
                Parameter[] ps= enumMethods[i].getParameters();
                for (int p = 0; p < ps.length; p ++) {
                    System.out.print(ps[p].getName() + ":" + ps[p].getType().getName());
                    if(p < ps.length -1) 
                        System.out.print(", ");
                }
                System.out.print(")");
            }
            System.out.println("");
        }
        
    }

    @Test
    public void testEnumSet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> classToReflect = tutu.getClass();
        
        Field fEnum = classToReflect.getDeclaredField("e");
        
        Class<?> classEnum = fEnum.getType();
        assertNotNull(classEnum);
        assertTrue(classEnum.isEnum());
        
        //get the current value
        assertNull(fEnum.get(tutu));
        Method m = classEnum.getMethod("values");
        java.lang.Enum<?>[] list = (Enum<?>[]) m.invoke(null);
        assertEquals(2, list.length);
        
        assertNull(tutu.e);
        
        fEnum.set(tutu, list[0]);
        
        assertTrue(tutu.e == SimpleEnum.UN);
    }
    
    
    @Test
    public void testEnumGet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        
        Class<?> classToReflect = toto.getClass();
//        Field fString = classToReflect.getDeclaredField("s");
//        Field fInt = classToReflect.getDeclaredField("i");
        // Work with the enum field
        Field fEnum = classToReflect.getDeclaredField("e");
        
        // Check the class type of the enum field
        Class<?> classEnum = fEnum.getType();
        assertNotNull(classEnum);
        // must be a enum
        assertTrue(classEnum.isEnum());
        
        // get the value
        Object reflectedEnum = fEnum.get(toto);        
        assertNotNull(reflectedEnum);
        // the value type must be a enum too
        Class<?> classEnumValue = reflectedEnum.getClass();
        assertTrue(classEnumValue.isEnum());
        assertEquals("SimpleEnum", classEnumValue.getSimpleName());
        assertEquals(classEnumValue, classEnum);
        
        displayMethods(classEnumValue);
        
        Method ordinal = classEnumValue.getMethod("ordinal");
        assertEquals("int", ordinal.getReturnType().getName());
        assertEquals(0, (int)ordinal.invoke(reflectedEnum));
        
        System.out.println("ordinal() = " + (int)ordinal.invoke(reflectedEnum));
    }
}
