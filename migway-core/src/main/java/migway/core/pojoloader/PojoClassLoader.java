package migway.core.pojoloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class loader specific to the Migway POJO helper.
 * POJO class file can be stored outside the java classpath. The path where POJO
 * are searched is store in the field pojoLocation
 * TODO create methods to configure this fields
 * 
 * @author Sébastien Tissier
 *
 */
public class PojoClassLoader extends ClassLoader {
    private static Logger LOG = LoggerFactory.getLogger(PojoClassLoader.class);

    private List<File> lookupPaths = new ArrayList<File>();

    /**
     * Create a new ClassLoader that will lookup for class into current folder
     */
    public PojoClassLoader() {
        addPath(new File("."));
        LOG.debug("New POJO Class loader created. Location list initialized to current path only");
    }

    /**
     * Create a new ClassLoader that will load class from current folder and
     * from the path in argument
     * 
     * @param path
     */
    public PojoClassLoader(File path) {
        this();
        addPath(path);
    }

    /**
     * Add a path to load classes from
     * 
     * @param path
     */
    public void addPath(File path) {
        LOG.debug("Adding " + path + " to location list");
        if (!path.exists())
            LOG.trace(path + " doesn't exist");
        if (lookupPaths.contains(path)) {
            LOG.debug(path + " was already in location list");
            return;
        }
        LOG.trace(path + " added to location list");
        lookupPaths.add(path);
    }

    /**
     * find a class with its name.
     * 
     * @param name
     *            can be either a java binary name, either simply the class
     *            name.
     *            In both cases, class file are loaded only from pojoLocation
     *            path
     *            If it's java binary name, only name starting
     *            pojoRootBinaryName are allowed
     *            TODO allow using relative path name (with name starting with
     *            .)
     * 
     * @return the loaded Class
     */
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // the guidelines found in javase/tutorial/ext/basics/load.html (1.
        // lookup in cache, 2. try parent, 3. load) are not relevant to
        // findClass method, but only to loadClass method.
        LOG.debug("Trying to find " + name);
        // This method is called only if parent didn't find the class.

        String fileClassName = name.replace('.', '/') + ".class";
        LOG.trace("looking for " + fileClassName + " file");
        FileSystem fs = FileSystems.getDefault();

        // First try: loading direct class file
        for (File pojoLocation : lookupPaths) {
            Path path = fs.getPath(pojoLocation.getAbsolutePath(), fileClassName).normalize();
            LOG.trace("Checking that " + path + " exists");
            if (path.toFile().exists()) {
                LOG.trace(path + " exists");
                if (!path.toFile().canRead()) {
                    LOG.warn(path + " file exists but can't be read");
                    throw new ClassNotFoundException("Unable to read class file " + path);
                }
                // File found. Load class data
                try {
                    byte[] b = loadClassData(path.toFile());
                    // and return the defined class
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException("Unable to load class file", e);
                }
            }
        }

        // Not found, try to find from a Jar
        // Iterate all locations
        LOG.trace(name + " class is not found as .class file. Looking into jar");
        for (File pojoLocation : lookupPaths) {
            LOG.trace("searching jar files into " + pojoLocation);
            // Get all jar of this folder
            String[] jars = pojoLocation.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            // No jar is found in this location. Iterate next location
            if (jars == null) {
                LOG.trace("No jar found here");
                continue;
            }

            LOG.trace(jars.length + " jar files found");
            // iterate all jars in the folder
            for (String jar : jars) {
                JarFile jarFile = null;
                // the path of the jar
                String jarFilePath = fs.getPath(pojoLocation.getAbsolutePath(), jar).normalize().toString();
                LOG.trace("Looking for " + fileClassName + " file into " + jarFilePath);
                try {
                    jarFile = new JarFile(jarFilePath);
                    if (jarFile.getEntry(fileClassName) != null) {
                        LOG.trace(name + " class exists in " + jarFilePath);
                        byte[] b = loadClassData(jarFile, fileClassName);
                        return defineClass(name, b, 0, b.length);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // Close the jar file in any case (if it's open!)
                    try {
                        if (jarFile != null)
                            jarFile.close();
                    } catch (IOException e) {
                    }
                }
            }

        }

        LOG.warn("The class " + name + " cannot be found");
        // Default behaviour: useless, because super.findClass will never find
        // anythind: parent.loadClass has been called before the current method
        // to be called

        return super.findClass(name);
    }

    protected byte[] loadClassData(JarFile jar, String entry) throws IOException {
        LOG.trace("Loading class data from Jar file: " + jar + " -- " + entry);
        // find the class entry in the jar file
        JarEntry e = jar.getJarEntry(entry);
        // get the stream
        InputStream stream = jar.getInputStream(e);
        try {
            return loadInputStream(stream, e.getSize());
        } finally {
            // close the stream whatever happens
            stream.close();
        }
    }

    protected byte[] loadClassData(File file) throws IOException {
        LOG.trace("Loading class data from class file: " + file);
        FileInputStream finput;
        // Open the file
        finput = new FileInputStream(file);
        try {
            // Get the size of the file
            long size = file.length();

            return loadInputStream(finput, size);
        } finally {
            // close the stream whatever could happen
            finput.close();
        }
    }

    /**
     * Doesn't close the stream
     * 
     * @param finput
     * @param size
     * @return
     * @throws IOException
     */
    private byte[] loadInputStream(InputStream finput, long size) throws IOException {
        // TODO implement file > 2^31-1 bytes length
        if (size > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("File with length > 2^31-1 are not yet implemented");
        try {
            // Prepare storage area
            byte[] buf = new byte[(int) size];
            // store the content into byte array
            finput.read(buf);
            // Send data back
            return buf;
        } catch (IOException e) {
            // Do nothing to let the finput close
            throw e;
        }
    }
}
