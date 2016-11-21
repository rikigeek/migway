package migway.core.mapper;

import migway.core.config.ConfigHelper;
import migway.core.config.KeyModel;
import migway.core.helper.PojoLoaderHelper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Processor that update InstanceKeeper
 * 
 * @author Sébastien Tissier
 *
 */
public class KeyProcessor implements Processor {

    public static final String KEY_HEADER = "MIGObjectKey";
    public static final String INSTANCE_HEADER = "MIGObjectInstance";
    private InstanceKeeperImpl instanceKeeper;
    private PojoLoaderHelper pojoHelper = PojoLoaderHelper.INSTANCE;

    private ConfigHelper config;

    public KeyProcessor(ConfigHelper configHelper) {
        if (configHelper == null)
            throw new IllegalArgumentException("configHelper argument can't be null");
        this.config = configHelper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        instanceKeeper = InstanceKeeperImpl.getSingleton();
        Object instance = exchange.getIn().getHeader(INSTANCE_HEADER);
        Class<?> pojoClass = instance.getClass();
        String pojoClassName = pojoClass.getName();

        KeyModel keyModel = config.getMappingForDestination(pojoClassName).getDestinationKey();
        KeyHolder key = new KeyHolder(pojoClass);
        if (keyModel.isInPojo()) {
            // get the key from the POJO
            for (String keyname : keyModel.getKeyNames()) {
                key.add(pojoHelper.getField(pojoClassName, keyname).get(instance));
            }
        } else if (keyModel.isInMessageHeader()) {
            for (String keyname : keyModel.getKeyNames()) {
                key.add(exchange.getIn().getHeader(keyname));
            }
        } else if (keyModel.isInExchangeProperties()) {
            for (String keyname : keyModel.getKeyNames()) {
                key.add(exchange.getProperty(keyname));
            }
        }
        instanceKeeper.addKey(instance, key);
    }

}
