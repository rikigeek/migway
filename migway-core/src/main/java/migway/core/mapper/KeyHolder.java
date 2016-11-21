package migway.core.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains key values. This class can be compared and hashed.
 * Comparison is made only by considering values of keys.
 * Comparison is also made <b>with</b> considering sort order of the key values.
 * (i.e. <code>&lt;int 5, int 15&gt;</code> is <b>not</b> equivalent to <code>&lt;int 15, int 5&gt;</code>)
 * 
 * And type of value is considered in the comparison. i.e. <code>&lt;int 5, short 15&gt;</code> is
 * not the same as <code>&lt;int 15, short 5&gt;</code> !
 * 
 * InstanceType of the keyHolder (the Class of the object where the key values
 * are from) is <b>not</b> considered in the comparison
 * 
 * @author Sébastien Tissier
 *
 */
public class KeyHolder {
    private List<Object> values = new ArrayList<Object>();
    private Class<?> instanceType;

    /**
     * New KeyHolder that will contain key values for an object of class
     * {@link instanceType}
     *
     * @param instanceType
     */
    public KeyHolder(Class<?> instanceType) {
        if (instanceType == null)
            throw new IllegalArgumentException("InstanceType can't be null");
        this.instanceType = instanceType;
    }

    /**
     * Get the type of object that values are from.
     * 
     * @return
     */
    public Class<?> getInstanceType() {
        return instanceType;
    }

    @Override
    public int hashCode() {
        // Hashcode is average of values hashcode + instanceType hashcode
        if (values == null)
            return 0;
        if (values.size() == 0)
            return 100;
        int nbValues = values.size();
        int hashSum = 0;
        for (int i = 0; i < values.size(); i++) {
            hashSum += (values.get(i) != null ? values.get(i).hashCode() : -1);
        }
        return hashSum / nbValues;
    }

    @Override
    public boolean equals(Object obj) {
        // Objects are different if
        // - one is null
        if (obj == null)
            return false;
        // - not same class
        if (!(obj instanceof KeyHolder))
            return false;
        KeyHolder dest = (KeyHolder) obj;

        // - they don't identify the same type
//        if (!instanceType.equals(dest.instanceType))
//            return false;

        // - one instances values is null, but not the other;
        if (values == null && dest.values != null)
            return false;
        if (values != null && dest.values == null)
            return false;
        // But it's the same if both are null
        if (values == null && dest.values == null)
            return true;
        // - number of values are different
        if (values.size() != dest.values.size())
            return false;
        // - if one entry of value is different
        for (int i = 0; i < values.size(); i++) {
            // Value entry are different if
            // - one is null, and not the other
            if (values.get(i) == null) {
                if (dest.values.get(i) != null)
                    return false;
            }
            // - or values are not equals
            else {

                if (!values.get(i).equals(dest.values.get(i)))
                    return false;
            }
        }
        // if objects are not different, then they are equals!
        return true;
    }

    /**
     * Add a value to the list of keys
     * 
     * @param keyValue
     */
    public void add(Object keyValue) {
        values.add(keyValue);
    }

    /**
     * Retrieve key value #i
     * 
     * @param i
     * @return
     */
    public Object get(int i) {
        return values.get(i);
    }

    /**
     * Get the number of key values that are store in this holder
     * 
     * @return
     */
    public int size() {
        return values.size();
    }

    /**
     * Check if this key is null.
     * 
     * A keyHolder is null when there is no value in it, or when they are all
     * null
     * 
     * @return
     */
    public boolean isNull() {
        if (values == null)
            return true;
        if (values.isEmpty())
            return true;
        for (Object o : values)
            if (o != null)
                return false;
        return true;
    }
}
