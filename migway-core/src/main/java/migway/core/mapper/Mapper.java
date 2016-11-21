package migway.core.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

import migway.core.config.ConfigHelper;
import migway.core.config.ElementMapping;
import migway.core.config.KeyModel;
import migway.core.config.Mapping;
import migway.core.helper.PojoLoaderHelper;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor is a first try of a Mapper.
 * 
 * A POJO is received.
 * It's translated, following configuration in ConfigHelper' Mapping section
 * A POJO is send.
 * 
 * Instance of elements are saved during the process. So that they can be
 * retrieved during another message processing.
 * To keep those instances, key have to be identified (also found in
 * ConfigHelper).
 * If a structure doesn't have keys, then its instances are not saved.
 * 
 * 
 * This processor is not completed. Particularly, some elements are not
 * supported yet:
 * <ul>
 * <li>POJO access must be made through the field only (it doesn't support
 * setter and getter)
 * <li>This doesn't really use the Migway Meta-model that describes POJO (this
 * MM is stored in ConfigHelper object)
 * <li>Arrays are not supported (can't actually map an element into an array,
 * nor an array into an element)
 * </ul>
 * 
 * 
 * @author Sébastien Tissier
 *
 */
public class Mapper implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(Mapper.class);

    private PojoLoaderHelper helper = PojoLoaderHelper.INSTANCE;
    private ConfigHelper config;
    /**
     * Where we store each instance according to its key values and key values
     * of related instance
     */
    private InstanceKeeper instanceKeeper = InstanceKeeperImpl.getSingleton();

    private Exchange exchange;

    public Mapper(ConfigHelper config) {
        if (config == null)
            throw new IllegalArgumentException("config argument can't be null");
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // MappingSampleFactory.loadTankLab(config);
        // Map input with output.

        if (!validateExchange(exchange)) {
            stopExchange();
            return;
        }
        this.exchange = exchange;

        // 1. get the input instance
        Object inputPojo = getPojoInstance(exchange);
        // find the source POJO type (detect from input class)
        Class<?> inputPojoClass = inputPojo.getClass();
        String inputPojoType = inputPojoClass.getName();
        LOG.debug("Receiving " + inputPojoType);

        // 2. Where can I find its key(s)
        Mapping mapping = config.getMappingForSource(inputPojoType);
        LOG.debug("CONFIG Mapping found = {}", (mapping == null ? "null" : mapping.toString()));
        if (mapping == null) {
            LOG.debug("No mapping found for object {}", inputPojoType);
            stopExchange();
            return;
        }

        // find the destination POJO type
        String outputPojoType = mapping.getDestination();
        Class<?> outputPojoClass = helper.getPojoClass(outputPojoType);
        LOG.debug("CONFIG Destination mapping is {}", outputPojoType);

        // Get the source key
        KeyModel inputKeyMap = mapping.getSourceKey(inputPojoType);

        // 3. What is its keys values
        KeyHolder inputKeyValues = getKeyValues(inputPojo, inputKeyMap);

        // 4. Is there already a output instance for this key? (NB: we need the
        // output type)
        Object outputPojo = instanceKeeper.findValue(inputKeyValues, outputPojoClass);

        // 4.1 If yes, get this output instance
        if (outputPojo != null) {
            LOG.debug("Output POJO instance is found {}", outputPojo.toString());
        }

        // 4.2 If no, create a new output instance, and store it
        else {
            LOG.debug("No output POJO instance found. Create a new one");
            // Build the destination POJO type
            outputPojo = helper.loadPojo(outputPojoType);
            instanceKeeper.addValue(inputKeyValues, outputPojo);
        }
        // 5. In output object, where is the instance key? Check the value, and
        // eventually update it
        LOG.debug("Find instance key");
        KeyModel outputKeyMap = mapping.getDestinationKey();
        KeyHolder outputKeyValues = instanceKeeper.findKey(inputKeyValues, outputPojoClass);
        if (outputKeyValues != null && !outputKeyValues.isNull()) {
            // Save this keyvalue into the message (either in POJO, in header or
            // property). So that it is available for next processor
            LOG.debug(String.format("Save instance key %s %s %s", outputPojo.toString(), Arrays.toString(outputKeyMap.getKeyNames()),
                    outputKeyValues.toString()));
            setKeyValues(outputPojo, outputKeyMap, outputKeyValues);
        }

        // 6. Update values in output from input values
        LOG.debug("Update values of outputPojo");
        outputPojo = mapValues(outputPojo, inputPojo, mapping);

        // 6b. Store input instance, so that we can use it in the other
        // direction
        instanceKeeper.setValue(inputKeyValues, inputPojo);

        // 6c. Store output instance in a Message Header
        exchange.getIn().setHeader(KeyProcessor.INSTANCE_HEADER, outputPojo);

        // 7. Is the output instance ready to be send? (data is coherent)
        boolean valid = config.validatePojo(mapping, outputPojo);

        // 7.1 If ready, send it
        if (valid) {
            LOG.debug("Send outputPojo {}", outputPojo.toString());
            sendObject(outputPojo);
        }

        // 7.2 Not ready, so stop the Exchange
        else {
            stopExchange();
            return;
        }

    }

    /**
     * Set key values for a POJO instance.
     * pojoInstance fields that contains key (as described in keyModel) are
     * updated with the values stored in keyValues
     * 
     * @param pojoInstance
     *            is the POJO to set the key
     * @param keyModel
     *            is the keys definition
     * @param keyValues
     *            contains the keyValues
     */
    private void setKeyValues(Object pojoInstance, KeyModel keyModel, KeyHolder keyValues) {
        Class<?> pojoClass = pojoInstance.getClass();
        String pojoClassName = pojoClass.getName();

        if (keyModel.isInPojo()) {
            // Shoud we really update key values?
            int keyValueIndex = 0;
            for (String keyname : keyModel.getKeyNames()) {
                // Set the keyValue from KeyHolder into the pojoInstance
                // KeyHolder keeps values in same order as in KeyModel
                try {
                    helper.getField(pojoClassName, keyname).set(pojoInstance, keyValues.get(keyValueIndex));
                } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
                        | SecurityException | IndexOutOfBoundsException e) {
                    LOG.warn("Can't save key value " + keyname + " on object " + pojoClassName, e);
                }
            }
        } else if (keyModel.isInMessageHeader()) {
            // Set in message header
            // This will store same values in each header (if more than one
            // header)

            if (keyValues.size() > 1) {
                // more that one value in key, so store it as an array
                Object values[] = new Object[keyValues.size()];
                for (int index = 0; index < keyValues.size(); index++) {
                    values[index] = keyValues.get(index);
                }
                for (String keyName : keyModel.getKeyNames())
                    exchange.getIn().setHeader(keyName, values);
            } else if (keyValues.size() == 1) {
                // only one value, store it directly in the header
                Object value = keyValues.get(0);
                for (String keyName : keyModel.getKeyNames())
                    exchange.getIn().setHeader(keyName, value);
            }
            // no values case is ignored
        } else if (keyModel.isInExchangeProperties()) {
            // Set in exchange property
            // This will store same values in each property (if more than one
            // property)

            if (keyValues.size() > 1) {
                // More than one value are store in property as an array
                Object values[] = new Object[keyValues.size()];
                for (int index = 0; index < keyValues.size(); index++) {
                    values[index] = keyValues.get(index);
                }
                for (String keyName : keyModel.getKeyNames())
                    exchange.setProperty(keyName, values);
            } else if (keyValues.size() == 1) {
                // Only one value is store as is in the property
                Object value = keyValues.get(0);
                for (String keyName : keyModel.getKeyNames())
                    exchange.setProperty(keyName, value);
            }
            // No values case is ignored
        }
    }

    /**
     * Get key value of this POJO instance
     * 
     * @param pojoInstance
     * @param keyModel
     * @return can be null
     */
    private KeyHolder getKeyValues(Object pojoInstance, KeyModel keyModel) {
        if (keyModel == null)
            return null;
        if (pojoInstance == null)
            return null;
        Class<?> pojoClass = pojoInstance.getClass();
        String pojoClassName = pojoClass.getName();
        // Build the key value for the POJO instance
        KeyHolder key = new KeyHolder(pojoClass);

        // Check the type of key
        if (keyModel.isInPojo()) {
            // get the key from the POJO
            for (String keyname : keyModel.getKeyNames()) {
                try {
                    key.add(helper.getField(pojoClassName, keyname).get(pojoInstance));
                } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
                        | SecurityException e) {
                    LOG.warn("Can't load key value " + keyname + " on object " + pojoClassName, e);
                }

            }
        } else if (keyModel.isInMessageHeader()) {
            // Get the key from Message header
            for (String keyname : keyModel.getKeyNames()) {
                key.add(exchange.getIn().getHeader(keyname));
            }
        } else if (keyModel.isInExchangeProperties()) {
            // get the key from the exchange property
            for (String keyname : keyModel.getKeyNames()) {
                key.add(exchange.getProperty(keyname));
            }
        }
        // Save the pojoInstance into the instanceKeeper
        // instanceKeeper.setValue(key, pojoInstance);

        return key;
    }

    /**
     * Send the POJO to next processor in the route
     * 
     * @param outputPojo
     */
    private void sendObject(Object outputPojo) {
        // Store destination POJO in Message
        exchange.getIn().setBody(outputPojo);

    }

    /**
     * Recursive method to map an element with another.
     * Mapping is made by copying value reference from <em>inputElementName</em>
     * of the POJO <em>inputPojo</em> into <em>outputElementName</em> of the
     * POJO <em>outputPojo</em>.
     * 
     * <em>elementName</em> is the name of the element (the field). It can also
     * be a
     * hierarchical name (i.e. {@code struct.value}), or a star ({@code "*"}). A
     * star means to use all the structure (the POJO).
     * 
     * @param outputPojo
     *            the output object to update. This reference can also be
     *            ignored (when using {@code "*"} as outputElementName)
     * 
     * @param inputPojo
     * @param outputElementName
     * @param inputElementName
     * @return the outputPojo updated with the values
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Object recursiveMapElement(Object outputPojo, Object inputPojo, String outputElementName, String inputElementName,
            ElementMapping mapping) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {

        LOG.debug("Recursive mapping of " + inputElementName + " into " + outputElementName + ". Values are " + inputPojo + ", "
                + outputPojo);

        String inputPojoType = inputPojo.getClass().getName();
        String outputPojoType = outputPojo.getClass().getName();

        // The field name we get/set values
        String inputFieldName, outputFieldName;
        // The recursive field (sub field part)
        String inputNextElementName, outputNextElementName;
        // flag set to true when one of element is hierarchical
        boolean inputRecursive = false, outputRecursive = false;

        // check if hierachical name of input element
        int dotIndex = inputElementName.indexOf(".");
        if (dotIndex == -1) {
            inputFieldName = inputElementName;
            inputNextElementName = inputElementName;
        } else {
            inputFieldName = inputElementName.substring(0, dotIndex);
            inputNextElementName = inputElementName.substring(dotIndex + 1, inputElementName.length());
            inputRecursive = true;
        }
        // check if hierarchical name of output element
        dotIndex = outputElementName.indexOf(".");
        if (dotIndex == -1) {
            outputFieldName = outputElementName;
            outputNextElementName = outputElementName;
        } else {
            outputFieldName = outputElementName.substring(0, dotIndex);
            outputNextElementName = outputElementName.substring(dotIndex + 1, outputElementName.length());
            outputRecursive = true;
        }

        // get field Object
        Field outputField = null;
        Field inputField = null;
        // Get field values
        Object inputFieldObject = null;
        Object outputFieldObject = null;
        if (!"*".equals(outputFieldName)) {
            outputField = helper.getField(outputPojoType, outputFieldName);
        }
        if (!"*".equals(inputFieldName)) {
            inputField = helper.getField(inputPojoType, inputFieldName);
            inputFieldObject = inputField.get(inputPojo);
        }

        if (inputRecursive || outputRecursive) {
            Object recursiveOutputPojo = outputPojo;
            Object recursiveInputPojo = inputPojo;

            if (outputRecursive) {
                outputFieldObject = outputField.get(outputPojo);
                // Is output value already instantiated?
                if (outputFieldObject == null) {
                    try {
                        outputFieldObject = outputField.getType().newInstance();
                    } catch (InstantiationException e) {
                        LOG.error("Failed to instantiate " + outputPojoType + " - " + e.getStackTrace().toString());
                    }
                }
                recursiveOutputPojo = outputFieldObject;
                // update the element value into the outputPojo
                if (outputFieldObject != null) {
                    outputField.setAccessible(true);
                    // Store result in the destination field, only if not NULL!
                    outputField.set(outputPojo, outputFieldObject);
                }
            }

            if (inputRecursive) {
                recursiveInputPojo = inputFieldObject;
            }

            // Get the new value of this object
            outputFieldObject = recursiveMapElement(recursiveOutputPojo, recursiveInputPojo, outputNextElementName, inputNextElementName,
                    mapping);
        } else {
            // Not recursive. We are at the last call of this recursive method
            if ("*".equals(inputNextElementName)) {
                // Source is the entire structure
                inputFieldObject = inputPojo;
            }

            if ("*".equals(outputNextElementName)) {
                // Destination is the entire structure
                outputPojo = copyValue(inputFieldObject, outputPojo.getClass());
            } else {

                Class<?> outputPojoClass = outputField.getType();
                if (outputField.getType().isArray()) {
                    LOG.debug("Destination is an array");
                    // Destination is an array
                    outputPojoClass = outputField.getType().getComponentType();
                }
                // Destination is one field of the structure
                outputFieldObject = copyValue(inputFieldObject, outputPojoClass);
                // update the element value into the outputPojo
                if (outputFieldObject != null) {
                    Object array;
                    outputField.setAccessible(true);
                    // Store result in the destination field, only if not NULL!
                    if (outputField.getType().isArray()) {
                        // How to identify which element to update?
                        KeyModel keyIn = mapping.getSourceKey();
                        KeyModel keyOut = mapping.getDestinationKey();

                        KeyHolder keyHolderOut = getKeyValues(outputFieldObject, keyOut);

                        array = outputField.get(outputPojo);
                        int newObjectIndex = 0;
                        if (array == null) {
                            LOG.debug("Create a new array of size 1");
                            array = Array.newInstance(outputPojoClass, 1);
                        } else {
                            int arrayLength = Array.getLength(array);
                            newObjectIndex = -1;
                            // Check key
                            if (keyOut != null && keyIn != null && keyHolderOut != null) {
                                // Locate the element
                                for(int i = 0; i < arrayLength; i++) {
                                    KeyHolder keyHolderIn = getKeyValues(Array.get(array, i), keyIn);
                                    if (keyHolderOut.equals(keyHolderIn))  {
                                        newObjectIndex = i;
                                        break;
                                    }
                                }
                                // Index to store the object
                            }
                            
                            if (newObjectIndex == -1) {
                                LOG.debug("Old array length is " + arrayLength);
                                Object arrayCopy = Array.newInstance(outputPojoClass, arrayLength + 1);
                                // Duplicate
                                for (int i = 0; i < arrayLength; i++) {
                                    Array.set(arrayCopy, i, Array.get(array, i));
                                }
                                newObjectIndex = arrayLength;
                                // use the new array
                                array = arrayCopy;
                            }
                        }
                        // add to Array
                        // check array size
                        // add element
                        LOG.debug("Insert value at position " + newObjectIndex);
                        Array.set(array, newObjectIndex, outputFieldObject);
                        outputFieldObject = array;
                    }
                    outputField.set(outputPojo, outputFieldObject);
                }
            }

        }
        return outputPojo;
    }

    private Object copyValue(Object inputPojo, Class<?> outputPojoType) {
        if (outputPojoType.isInstance(inputPojo)) {
            // Same type on both side
            LOG.debug("No type conversion needed");
            return outputPojoType.cast(inputPojo);
        } else {
            // Call the converter
            try {
                return exchange.getContext().getTypeConverter().mandatoryConvertTo(outputPojoType, inputPojo);
            } catch (NoTypeConversionAvailableException e) {
                LOG.error("No type converter available from " + inputPojo.getClass().getSimpleName() + " to "
                        + outputPojoType.getSimpleName());
                return null;
            } catch (TypeConversionException e) {
                LOG.error("Type conversion failed: " + e.getStackTrace().toString());
                return null;
            }
        }
    }

    /**
     * Do the mapping
     * 
     * @param outputPojo
     * @param inputPojo
     * @param mapping
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Object mapValues(Object outputPojo, Object inputPojo, Mapping mapping) throws ClassNotFoundException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        String inputPojoType = inputPojo.getClass().getName();
        // String outputPojoType = outputPojo.getClass().getName();

        for (ElementMapping elementMapping : mapping.getElementMapping()) {
            LOG.debug("Check elementMapping : " + elementMapping.getSource().structureName);
            // Don't map this element if it's not from the same inputType
            if (inputPojoType.equals(elementMapping.getSource().structureName)) {
                outputPojo = recursiveMapElement(outputPojo, inputPojo, elementMapping.getDestination().elementName,
                        elementMapping.getSource().elementName, elementMapping);
            }
        }
        return outputPojo;
    }

    /**
     * Stopping the Exchange.
     * To stop exchange, a property "SKIPMAPPING" is set to true. The route then
     * have to test this
     */
    private void stopExchange() {
        LOG.debug("Nothing more to do with this object: STOP the exchange");
        exchange.setProperty("SKIPMAPPING", true); // To be used later in
                                                   // the route
    }

    private Object getPojoInstance(Exchange exchange) {
        return exchange.getIn().getBody();
    }

    private boolean validateExchange(Exchange exchange) throws InvalidPayloadException {
        if (exchange.getIn() == null)
            throw new InvalidPayloadException(exchange, Message.class);

        if (exchange.getIn().getBody() == null)
            throw new InvalidPayloadException(exchange, Object.class);

        return true;
    }
}
