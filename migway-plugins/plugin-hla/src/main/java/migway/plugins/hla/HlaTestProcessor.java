package migway.plugins.hla;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIinternalError;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import migway.core.config.ConfigHelper;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface with HLA component.
 * TODO this implementation is not finished. All output are wrong
 * 
 * @author Sébastien Tissier
 *
 */
public class HlaTestProcessor extends HlaBaseInterface {
    private static final Logger LOG = LoggerFactory.getLogger(HlaTestProcessor.class);

    private Map<String, byte[]> message = new HashMap<String, byte[]>();
    private String[] keys;
    private int currentKeyId;
    @SuppressWarnings("unused")
    private String currentKey;
    @SuppressWarnings("unused")
    private byte[] currentBuffer;
    @SuppressWarnings("unused")
    private int currentBufferPosition;

    @SuppressWarnings("unused")
    private EncoderFactory encoderFactory;

    private FomParser fom;
    
    private File fomFile = null;
    public final static String DEFAULT_FOM_FILE = "GVA_RPR2-D20_2010.xml";

    private void hlaInit() {
        if (fomFile == null)
            fomFile = new File(DEFAULT_FOM_FILE);
        try {
            encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
            fom = new FomParser();
            fom.parse(fomFile);
            LOG.info(fomFile.getName() + " FOM is loaded");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public HlaTestProcessor() throws RTIinternalError {
        super(ConfigHelper.loadConfig(new File("migway:mytanksim")), new File(DEFAULT_FOM_FILE));
        fomFile = new File(DEFAULT_FOM_FILE);
        hlaInit();
    }

    /**
     * Create the Interface, and configure it with a Migway configuration and a FOM file
     * @param config the configuration of the Migway instance
     * @param fom the FOM file
     * @throws RTIinternalError when RTI failed to initialize
     */
    public HlaTestProcessor(ConfigHelper config, File fom)  {
        super(config, fom);
        if (fom == null)
            this.fomFile = new File(DEFAULT_FOM_FILE);
        else 
            this.fomFile = fom;
        hlaInit();
    }

    @Override
    protected String getClassName(Object headerValue) {
        if (!(headerValue instanceof String))
            throw new IllegalArgumentException("Header " + getHeaderClassName() + ": value is not a string ("
                    + headerValue.getClass().getName() + ")");
        // headerValue is a string
        String header = (String) headerValue;
//        String className = "";
//        className = "edu.cyc14.essais.pojo.rprfom." + header;

//        return className;
        return header;

    };

    @Override
    protected boolean validate(Exchange exchange) {
        return true;
    }

    @Override
    protected String setHeaderClassName() {
        return "HLAtypeName";
    }

    @Override
    protected String setInterfaceTypeName() {
        return "HLA";
    }

    @Override
    protected int maxBufferSize(Object pojo) {
        return 5000;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initializeInputBuffer(Object buffer) {
        if (isABuffer(buffer)) {
            // I didn't find a way to cast without any warning.
            try {
                message = Map.class.cast(buffer);
            } catch (ClassCastException e) {
                return false;
            }

            keys = new String[message.size()];
            // Extract the key list
            keys = message.keySet().toArray(keys);
            //
            currentKeyId = 0;
            //
            currentKey = keys[currentKeyId];
            //
            currentBuffer = message.get(keys[currentKeyId]);
            //
            currentBufferPosition = 0;
        }
        return true;
    }

    @Override
    protected boolean initializeOutputBuffer(Object rootPojo) {
        message = new HashMap<String, byte[]>();
        return true;
    }

    @Override
    protected Object getFinalizedOutputBuffer() {
        return message;
    }

    @Override
    protected boolean isABuffer(Object content) {
        return expectedBodyClass().isInstance(content);
    }

    @Override
    protected Class<?> expectedBodyClass() {
        // TODO Auto-generated method stub
        return Map.class;
    }
}
