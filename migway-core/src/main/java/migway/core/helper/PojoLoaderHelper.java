package migway.core.helper;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import migway.core.pojoloader.PojoClassLoader;

/**
 * A helper class to store Reflection data
 * 
 * @author Sébastien Tissier
 *
 */
public class PojoLoaderHelper {
    private PojoLoaderHelper() {
    }

    public final static PojoLoaderHelper INSTANCE = new PojoLoaderHelper();
    /**
     * Cache of the class loader
     */
    private PojoClassLoader classLoader = new PojoClassLoader();
    /**
     * Cache of POJO Class objects
     */
    private HashMap<String, Class<?>> pojoClassCache = new HashMap<String, Class<?>>();
    /**
     * Cache of the POJO Fields
     */
    private HashMap<String, HashMap<String, Field>> pojoFieldCache = new HashMap<String, HashMap<String, Field>>();

    /**
     * Get a field from a POJO Class. It searches into a cache before using
     * reflection code
     * 
     * @param pojoClassName
     * @param fieldName
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public Field getField(String pojoClassName, String fieldName) throws ClassNotFoundException, NoSuchFieldException, SecurityException {
        Field f;
        if (pojoFieldCache.containsKey(pojoClassName)) {
            HashMap<String, Field> cache = pojoFieldCache.get(pojoClassName);
            if (pojoFieldCache.get(pojoClassName).containsKey(fieldName))
                return pojoFieldCache.get(pojoClassName).get(fieldName);
            else {
                Class<?> pojoClass = getPojoClass(pojoClassName);
                f = pojoClass.getField(fieldName);
                cache.put(fieldName, f);
            }
        } else {
            Class<?> pojoClass = getPojoClass(pojoClassName);
            f = pojoClass.getField(fieldName);
            HashMap<String, Field> cache = new HashMap<String, Field>();
            pojoFieldCache.put(pojoClassName, cache);
            cache.put(fieldName, f);
        }
        return f;
    }

    /**
     * Getting the Class object of the requested POJO. Use a local cache for
     * better performance.
     * 
     * @param pojoClassName
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> getPojoClass(String pojoClassName) throws ClassNotFoundException {
        Class<?> pojoClass;
        if (pojoClassCache.containsKey(pojoClassName)) {
            pojoClass = pojoClassCache.get(pojoClassName);
        } else {
            pojoClass = classLoader.loadClass(pojoClassName);
            pojoClassCache.put(pojoClassName, pojoClass);
        }
        return pojoClass;
    }

    /**
     * Create an instance of the POJO
     * 
     * @param pojoClassName
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object loadPojo(String pojoClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object pojo;
        pojo = getPojoClass(pojoClassName).newInstance();

        return pojo;
    }

    public void addPojoLocation(String path) {
        classLoader.addPath(new File(path));
    }
}
