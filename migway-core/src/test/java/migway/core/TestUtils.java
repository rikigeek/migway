package migway.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.junit.Test;

import migway.core.pojoloader.PojoClassLoader;

/**
 *  Class to help building/retrieving POJO/ByteBuffer...
 * 
 * @author Sébastien Tissier
 *
 */
public class TestUtils {
    public static final int MAX_BUFFER_SIZE = 150;
    public static final String IN_HLA = "direct:simhla";
    public static final String IN_DDS = "direct:simdds";
    public static final String OUT_HLA = "mock:resultHla";
    public static final String OUT_DDS = "mock:resultDds";
    public static final float ddsPrecision = 0.95f;
    public static final String ddsDescription = "Answer";
    public static final boolean ddsAcknowledged = false;
    public static final int ddsNumber = 42;
    public static final boolean hlaOk = true;
    public static final int hlaValue = 66;
    public static final String hlaName = "Command";
    public static final String HLA_POJO_CLASS = "edu.cyc14.essais.pojo.MyPojo";
    public static final String DDS_POJO_CLASS = "edu.cyc14.essais.pojo.DdsPojo";
    
    public static ByteBuffer fillHlaBuffer(ByteBuffer buf) {
        // Actually, format of SUPPORTED_CLASS topic is:
        // a boolean (ok)
        // an int (value)
        // a String (name)
        String name = hlaName;
        // Check size of message. Must be less than the MAX_BUFFER_SIZE
        int messageLength = 1 + 4 + 4 + 2 * name.length();
        if (messageLength > MAX_BUFFER_SIZE) {
            int extraMessageLength = messageLength - MAX_BUFFER_SIZE;
            // Remove extra length / 2 char from the name variable
            name = name.substring(0, name.length() - (extraMessageLength / 2));
        }

        // Rewind the buffer
        buf.rewind();

        // Put every value into the buffer
        // A boolean
        buf.put((byte) (hlaOk ? 1 : 0));
        // An integer
        buf.putInt(hlaValue);
        // A String. 1/ size of the string, 2/ array of char
        buf.putInt(name.length());
        for (int i = 0; i < name.length(); i++) {
            buf.putChar(name.charAt(i));
        }

        buf.rewind(); //Don't forget to rewind the buffer. The interface processor won't do it...

        return buf;
    }

    public static ByteBuffer fillDdsBuffer(ByteBuffer buf) {
        // Actually, format of SUPPORTED_CLASS topic is:
        // an int (number)
        // a boolean (acknowledeged)
        // a String (description)
        // a float (precision)

        // rewind the buffer
        buf.rewind();

        // put a number
        buf.putInt(ddsNumber);
        buf.put((byte) (ddsAcknowledged ? 1 : 0));
        buf.putInt(ddsDescription.length());
        for (int i = 0; i < ddsDescription.length(); i++) {
            buf.putChar(ddsDescription.charAt(i));
        }
        buf.putFloat(ddsPrecision);

        buf.rewind(); //Don't forget to rewind the buffer. The interface processor won't do it...
        return buf;
    }
    
    public static Object createDdsPojo() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Class<?> ddsPojoClass;
        
        ddsPojoClass = new PojoClassLoader().loadClass(DDS_POJO_CLASS);
        Object ddsPojo = ddsPojoClass.newInstance();
        
        Field numberField = ddsPojoClass.getField("number");
        Field acknowledgedField = ddsPojoClass.getField("acknowledged");
        Field descriptionField = ddsPojoClass.getField("description");
        Field precisionField = ddsPojoClass.getField("precision");
        
        numberField.set(ddsPojo, ddsNumber);
        acknowledgedField.set(ddsPojo, ddsAcknowledged);
        descriptionField.set(ddsPojo, ddsDescription);
        precisionField.set(ddsPojo, ddsPrecision);
        
        return ddsPojo;
    }
    
    public static Object createHlaPojo() {
        Class<?> hlaPojoClass;
        try {
            hlaPojoClass = new PojoClassLoader().loadClass(HLA_POJO_CLASS);
            Object hlaPojo = hlaPojoClass.newInstance();
            
            Field okField = hlaPojoClass.getField("ok");
            Field nameField = hlaPojoClass.getField("name");
            Field valueField = hlaPojoClass.getField("value");
            
            okField.set(hlaPojo, hlaOk);
            nameField.set(hlaPojo, hlaName);
            valueField.set(hlaPojo, hlaValue);
            
            return hlaPojo;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testValidate() {
        assertTrue(true);
    }
}
