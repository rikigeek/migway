package migway.core.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.TypeConversionException;

/**
 * Class to store POJO instances, stored by instance id (or key)
 * 
 * To prevent duplicate object, this class is a singleton instance
 * 
 * @author Sébastien Tissier
 *
 */
public class InstanceKeeperImpl implements InstanceKeeper {
    private static InstanceKeeperImpl _instance = null;
    private Map<KeyHolder, List<Object>> keymap;

    /**
     * Set the Camel Context of the singleton. Context is used to access to the
     * Type converter system
     * 
     * @param context
     */
    public static void setContext(CamelContext context) {
    }

    /**
     * Get singleton
     * 
     * @return
     */
    public static InstanceKeeperImpl getSingleton() {
        if (_instance == null)
            _instance = new InstanceKeeperImpl();
        return _instance;
    }

    private InstanceKeeperImpl() {
        keymap = new HashMap<KeyHolder, List<Object>>();
    }

    @Override
    public <T> T findValue(KeyHolder key, Class<T> typeClass) throws TypeConversionException, NoTypeConversionAvailableException {
        // Key is null means there is no need to store this value
        if (key == null)
            return null;

        List<Object> values = keymap.get(key);
        if (values == null)
            return null;
        T instance = findType(values, typeClass);
        return instance;
    }

    @Override
    public boolean addValue(KeyHolder key, Object instance) {
        // Key is null means there is no need to store this value
        if (key == null || instance == null)
            return false;

        List<Object> values = keymap.get(key);
        if (values == null) {
            values = new ArrayList<Object>();
            values.add(instance);
            keymap.put(key, values);
            return true;
        }
        if (containsType(values, instance.getClass()))
            return false; // Already exist an object of this type
        values.add(instance);
        return true;
    }

    @Override
    public boolean addKey(KeyHolder key, KeyHolder otherkey) {
        // Key is null means there is no need to store this value
        if (key == null || otherkey == null)
            return false;

        List<Object> values = keymap.get(key);
        if (values == null) {
            values = new ArrayList<Object>();
            keymap.put(key, values);
        }
        keymap.put(otherkey, values);
        return true;
    };

    @Override
    public boolean addKey(Object instance, KeyHolder key) {
        // Key is null means there is no need to store this value
        if (key == null || instance == null)
            return false;

        // Locate where instance is stored
        KeyHolder existingKey = findKey(instance);
        List<Object> instances = keymap.get(existingKey);

        if (existingKey != null) {
            // found
            if (keymap.containsKey(key)) {
                return false; // The new key is already assigned to a different
                              // instance collection, and the instance is
                              // already in its own instance collection
            }
            // key is not already in the storage
            keymap.put(key, instances);
            return true;
        } else {
            return false; // instance not found
        }
    };

    @Override
    public boolean addValue(Object instance, Object newinstance) {
        // Key is null means there is no need to store this value
        if (newinstance == null || instance == null)
            return false;

        KeyHolder existingKey = findKey(instance);

        if (existingKey != null) {
            // found
            return addValue(existingKey, newinstance);
        } else {
            return false; // instance not found
        }
    }

    /**
     * Check if the list contains an object of type {@link typeClass}
     * 
     * @param values
     * @param typeClass
     * @return
     */
    private <T> boolean containsType(List<Object> values, Class<T> typeClass) {
        T found = findType(values, typeClass);
        if (found == null)
            return false;
        else
            return true;
    }

    /**
     * Find an object of type {@link typeClass}.
     * 
     * @param values
     *            List of object to look into
     * @param typeClass
     *            the type of the object to find
     * @return null if no object is found
     */
    @SuppressWarnings("unchecked")
    private <T> T findType(List<Object> values, Class<T> typeClass) {
        for (Object instance : values) {
            if (typeClass.isInstance(instance))
                return (T) instance;
        }
        return null;
    }

    @Override
    public boolean setValue(KeyHolder key, Object instance) {
        // Key is null means there is no need to store this value
        if (key == null || instance == null)
            return false;

        // FIXME not the same as add value. You must replace the existing value
        // return addValue(key, value);
        List<Object> values = keymap.get(key);
        // first instance for this key
        if (values == null) {
            values = new ArrayList<Object>();
            values.add(instance);
            keymap.put(key, values);
            return true;
        }
        // Check if same type already stored
        if (containsType(values, instance.getClass())) {
            // remove previous instance
            Object oldInstance = findType(values, instance.getClass());
            values.remove(oldInstance);
        }
        // Add the actual instance
        values.add(instance);
        return true;
    }

    @Override
    public boolean containsKey(KeyHolder key) {
        return keymap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return (findKey(value) != null);
    }

    @Override
    public KeyHolder findKey(Object instance, Class<?> objectType) {
        if (objectType == null)
            throw new IllegalArgumentException("ObjectType cannot be null");
        KeyHolder existingKey = null;
        // Locate where instance is stored
        for (Map.Entry<KeyHolder, List<Object>> entry : keymap.entrySet()) {
            if (entry.getValue().contains(instance)) {
                // instance is found
                existingKey = entry.getKey();
                // but objectType must be ok
                if (objectType.equals(existingKey.getInstanceType()))
                    return existingKey;
            }
        }
        return null;
    }

    @Override
    public KeyHolder findKey(Object instance) {
        if (instance == null)
            return null;
        //
        for (Map.Entry<KeyHolder, List<Object>> entry : keymap.entrySet()) {
            if (entry.getValue().contains(instance)) {
                // instance is found, return the key
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public KeyHolder findKey(KeyHolder key, Class<?> objectType) {
        if (objectType == null)
            throw new IllegalArgumentException("ObjectType cannot be null");
        // first get the list attached to this key.
        List<Object> value = keymap.get(key);
        if (value == null)
            return null;
        // then lookup which key are also mapped to this list
        for (Map.Entry<KeyHolder, List<Object>> entry : keymap.entrySet()) {
            if (value.equals(entry.getValue())) { // the current key is mapped
                                                  // to the same value
                KeyHolder keyHolder = entry.getKey();
                if (keyHolder != null)
                    // check instanceType of the key
                    if (objectType.equals(keyHolder.getInstanceType()))
                        return entry.getKey();
            }
        }
        return null;
    }

}
