package migway.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Contains Key fields for a POJO
 * 
 * @author Sébastien Tissier
 *
 */
public class KeyModel {
    private enum KeyStorage {
        POJO_ELEMENT, EXCHANGE_PROPERTIES, MESSAGE_HEADER
    }

    @XmlAttribute(name = "type")
    private KeyStorage keyStorage;

    /** POJO element names that contain the Key values */
    @XmlElement(name = "element")
    private List<String> keyElementList = new ArrayList<String>();

    public KeyModel() {
        keyStorage = KeyStorage.POJO_ELEMENT;
    }

    /**
     * Return true if the key value is stored inside POJO (in one or more of its
     * fields)
     */
    public boolean isInPojo() {
        return keyStorage == KeyStorage.POJO_ELEMENT;
    }

    /** Return true if the key value is stored in a header of the Camel Message */
    public boolean isInMessageHeader() {
        return keyStorage == KeyStorage.MESSAGE_HEADER;
    }

    /**
     * Return true if the key value is stored in a property of the Camel
     * Exchange
     */
    public boolean isInExchangeProperties() {
        return keyStorage == KeyStorage.EXCHANGE_PROPERTIES;
    }

    void addKeyName(String keyName) {
        keyElementList.add(keyName);
    }

    /**
     * Get the name of the key #i
     * 
     * @param i
     * @return
     */
    public String getKeyName(int i) {
        return keyElementList.get(i);
    }

    /**
     * get the number of keys for this Structure
     * 
     * @return
     */
    public int getKeyNumber() {
        return keyElementList.size();

    }

    /**
     * get all key names
     * 
     * @return a 'safe' array, that can be modified if necessary
     */
    public String[] getKeyNames() {
        return keyElementList.toArray(new String[0]);
    }

    /**
     * Set the key for this POJO as contained in a Properties field of the Camel
     * Exchange
     * 
     * @param propertyName
     *            name of properties where the key value is stored
     */
    void setExchangeProperty(String propertyName) {
        keyStorage = KeyStorage.EXCHANGE_PROPERTIES;
        keyElementList.clear();
        keyElementList.add(propertyName);
    }

    /**
     * Set the key for this POJO as contained in a Camel Message header
     * 
     * @param headerName
     */
    void setMessageHeader(String headerName) {
        keyStorage = KeyStorage.MESSAGE_HEADER;
        keyElementList.clear();
        keyElementList.add(headerName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyModel))
            return false;
        KeyModel k = (KeyModel) obj;
        if (!Equals.test(this.keyElementList, k.keyElementList))
            return false;
        if (!Equals.test(this.keyStorage, k.keyStorage))
            return false;
        return true;
    }

}
