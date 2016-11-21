package migway.core.mapper;

import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.TypeConversionException;

/**
 * This interface is dealing with instance caching (and most important, with
 * instance life cycle support).
 * 
 * An instance, if is a keyed instance, must be available in other Exchange.
 * This interface link key/instance with related key/instance (the instance
 * created by the mapping process).
 * 
 * Let's consider 2 Object: A & B, with their respective instances a1 & b1. Each
 * instance has key values ka1 & kb1, respectively.
 * 
 * Because B is a transformation of A (and A a transformation of B), we consider
 * a1 <-> b1 and b1 <-> a1. We can assume it's the same for the keys: ka1 <->
 * kb1 and kb1 <-> ka1.
 * 
 * We can find a1 from its key ka1, and we can find b1 from its key kb1.
 * 
 * But because all instance are equivalent and all key values are equivalent, it
 * also means we can find a1 from kb1 and b1 from ka1.
 * 
 * However, to be sure to find the correct instance (the instance of the correct
 * Class), we specify in {@link findValue} method a Class<?> object of the
 * desired type.
 * 
 * 
 * From an implementation point of view, we use a list of instance
 * (List<Object>). This list is linked with each key in a Map<KeyHolder,
 * List<Object>>.
 * And because this map stores only reference to instance, it means our list of
 * instance can be shared between any number of KeyHolder entry.
 * 
 * <h2>Typical usage</h2> when a1/ka1 is received, we store it with
 * <code>setValue(ka1, a1);</code> or with <code>addValue(ka1, a1);</code>
 * Difference between add and set is very important: with set, if an instance of
 * A is already linked with the key ka1, then this instance is replaced by a1.
 * But using addValue won't replace the actual value with a1. Instead it does
 * nothing and return false.
 * 
 * Then we can lookup for the B instance with a {@code findValue(ka1, B.class)}.
 * 
 * You can then find the kb1 with {@code findKey(ka1, B.class)}. the key kb1
 * will be returned only if the instance already been created.
 * 
 * Some times, you can't know the key until the instance is sent to the
 * destination bus. In this case, you can link a key kb1 with the instance b1
 * that you previously sent with {@code addKey(b1, kb1)}. This method will first
 * find where is stored the instance b1, and link it with the key kb1.
 * 
 * @author Sébastien Tissier
 *
 */
public interface InstanceKeeper {
    /**
     * Set a key/value pair. If key doesn't exist, add it.
     * If the key already exists and an instance of the same type is already
     * linked, we replace it.
     * <p>
     * <i>
     * {@literal Comme addvalue, sauf qu'il remplace sur une instance du même type existe déjà}
     * </i>
     * 
     * @param key
     * @param value
     * @return
     *         false if the update cannot be made (internal error)
     */
    public boolean setValue(KeyHolder key, Object instance);

    /**
     * Add a key/value pair. If key already exists, and there is already a value
     * of the same type, nothing is changed, and false
     * is returned
     * 
     * <p>
     * <i>
     * {@literal ajoute l'instance instance dans la liste pointée par la clé key}
     * </i>
     * 
     * @param key
     * @param value
     * @return
     *         false when key is already linked with an instance of the same
     *         type as value.
     *         More generally, false is returned when update cannot be made
     */
    public boolean addValue(KeyHolder key, Object instance);

    /**
     * link a key with another key.
     * <p>
     * <i>
     * {@literal Lie la clé otherkey à la même liste que celle pointée par la clé key}
     * </i>
     * 
     * @param key
     *            is the key that already exist in the map.
     * @param otherkey
     *            is the key to link, to add.
     * @return
     *         true if the update has been made. False in other case (in example
     *         if an internal error occurs, but internal
     *         error should throw an Exception)
     */
    public boolean addKey(KeyHolder key, KeyHolder otherkey);

    /**
     * Link a key with an existing object instance.
     * 
     * <p>
     * <i> {@literal Lie la clé key à la liste contenant l'instance instance}
     * </i>
     * 
     * @param instance
     *            must already exist in one instance list
     * @param key
     *            will be linked with the instance list that contains instance
     * @return
     *         true if update has been made. False in other case: in example
     *         because instance is not found, or because key already exists.
     */
    public boolean addKey(Object instance, KeyHolder key);

    /**
     * Link the instance newinstance with the same keys as instance
     * 
     * <p>
     * <i>
     * {@literal Ajoute l'instance newinstance dans la liste contenant l'instance instance}
     * </i>
     * 
     * @param instance
     * @param newinstance
     * @return true if update is made. false in other case (ie when instance is
     *         not found, ...)
     */
    public boolean addValue(Object instance, Object newinstance);

    /**
     * Does the key exist in this storage
     * <p>
     * <i> {@literal Est-ce que la clé key est référencée?} </i>
     * 
     * @param key
     * @return false when key is not found. true when it's found
     */
    public boolean containsKey(KeyHolder key);

    /**
     * Is the instance linked with any key?
     * <p>
     * <i> {@literal Est-ce que l'instance instance est référencée?} </i>
     * 
     * @param instance
     * @return true if instance is found, false in other case
     */
    public boolean containsValue(Object instance);

    /**
     * Find the value associated with the key
     * <p>
     * <i>
     * {@literal Retrouve l'instance de type objectType située dans la liste pointée par la clé key}
     * </i>
     * 
     * @param key
     * @param objectType
     * @return
     *         instance of type objectType. null if no instance of this type has
     *         been found, or if the key doesn't exist.
     * @throws NoTypeConversionAvailableException
     * @throws TypeConversionException
     */
    public <T> T findValue(KeyHolder key, Class<T> objectType) throws TypeConversionException, NoTypeConversionAvailableException;

    /**
     * Find the <i>first key</i> linked with the instance. Because
     * {@link KeyHolder} also contain the Class type of the identified object,
     * you can specify which sort of key you want to find
     * <p>
     * <i>
     * {@literal Retrouve la clé d'un objet de type objectType pointant sur la liste contenant l'instance instance}
     * </i>
     * 
     * @param instance
     * @param objectType
     * @return
     *         null if no key has been found (or if the instance is not in the
     *         storage)
     */
    public KeyHolder findKey(Object instance, Class<?> objectType);

    /**
     * Find the <i>first</i> key linked with this key. Because {@link KeyHolder}
     * also contain the Class type of the identified object, you can specify
     * which sort of key you want to find
     * <p>
     * <i>
     * {@literal Retrouve la clé d'un objet de type objectType pointant sur la même liste que la clé key}
     * </i>
     * 
     * @param key
     * @param objectType
     * @return
     *         null if no key has been found (or if the supplied key is not in
     *         the storage)
     */
    public KeyHolder findKey(KeyHolder key, Class<?> objectType);

    /**
     * Find the <i>first key</i> linked the instance. The key we are looking up
     * is identifying an object of the same Class as instance.
     * <p>
     * <i>
     * {@literal Retrouve la clé pointant sur la liste qui contient l'instance instance}
     * </i>
     * 
     * @param instance
     * @return
     *         null if no key of same class as instance is found, or if instance
     *         is not listed.
     */
    public KeyHolder findKey(Object instance);

}
