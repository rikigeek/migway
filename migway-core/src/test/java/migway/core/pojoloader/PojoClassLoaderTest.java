package migway.core.pojoloader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoClassLoaderTest {

    Logger LOG = LoggerFactory.getLogger(PojoClassLoaderTest.class);

    /** This class is on the class path */
    private static final String CLASSPATH_CLASS = "migway.core.pojoloader.ClassToLoad";
    /** This class can be found in jar of current folder */
    private static final String LOCAL_CLASS = "tanklab.Tank";
    /** Class that can be found only in jar in sub-folder */
    private static final String SUBLOCAL_CLASS = "LDM.PojoFactory";

    @Test
    public void testLoadNative() throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();
        Class<?> cl = loader.loadClass(CLASSPATH_CLASS);
        assertNotNull(cl);
        Object obj = cl.newInstance();
        assertNotNull(obj);

        try {
            cl = loader.loadClass(LOCAL_CLASS);
        } catch (ClassNotFoundException e) {
            cl = null;
        }
        assertNull(cl);

    }

    @Test
    public void testPojoLoader() throws Exception {
        // Default behavior is to lookup into current folder (and class path)
        PojoClassLoader loader = new PojoClassLoader();
        
        // This one is found on the class path
        Class<?> cl = loader.loadClass(CLASSPATH_CLASS);
        assertNotNull(cl);

        // This one is found in current path
        try {
            cl = loader.loadClass(LOCAL_CLASS);
        } catch (ClassNotFoundException e) {
            cl = null;
        }
        assertNotNull(cl);

        // This one cannot be found
        try {
            cl = loader.loadClass(SUBLOCAL_CLASS);
        } catch (ClassNotFoundException e) {
            cl = null;
        }
        assertNull(cl);
        
        loader.addPath(new File("src/test/resources"));
        // And now it can be found
        try {
            cl = loader.loadClass(SUBLOCAL_CLASS);
        } catch (ClassNotFoundException e) {
            cl = null;
        }
        assertNotNull(cl);
        
        loader.addPath(new File("unexistingfolder"));
        // And now it can be found
        try {
            cl = loader.loadClass("unknow.UnexistingClass");
        } catch (ClassNotFoundException e) {
            cl = null;
        }
        assertNull(cl);
        

    }

    @Test
    public void testJarLoader() throws Exception {
        // Lookup for jar file in current folder
        File current = new File(".");
        String[] jars = current.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        boolean found = false;
        for (String jarname : jars) {
            if ("partialPojoList.jar".equals(jarname))
                found = true;
        }
        assertTrue(found);

        // Lookup for pojo jar file
        JarFile jar = new JarFile("partialPojoList.jar");
        assertNotNull(jar);
        assertTrue(jar.entries().hasMoreElements());
        Enumeration<JarEntry> entries = jar.entries();
        // It must contain tanklab.Tank class
        assertNotNull(jar.getEntry("tanklab/Tank.class"));

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            LOG.debug(entry.getName() + " / " + entry.getComment() + " - " + entry.getSize());
        }
    }
}
