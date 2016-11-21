package migway.plugins.hla;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import migway.core.config.ConfigHelper;
import migway.plugins.hla.HlaTestProcessor;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HlaProcessorTest {
    private static Logger LOG = LoggerFactory.getLogger(HlaProcessorTest.class);

    private void debugEnv() {
        Map<String, String> env = System.getenv();
        String[] keys = new String[1];
        keys = env.keySet().toArray(keys);
        Arrays.sort(keys, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return (o1.compareToIgnoreCase(o2));
            }
        });
        for (String key : keys) {
            LOG.trace(String.format("ENV : %s = %s", key.toUpperCase(), env.get(key)));
        }
        
    }
    
    
    @Test
    public void testInit() throws IOException {
        if (LOG.isTraceEnabled()) {
            debugEnv();
        }
        LOG.trace("trace");
        LOG.debug("debug");
        LOG.info("info");
        LOG.warn("warn");
        LOG.error("error");
        
        ConfigHelper config = ConfigHelper.loadConfig("migway:hla-RPRFOM.edu");
        assertNotNull(config);
        File fom = new File("RPR2-D20_2010.xml");
        assertNotNull(fom);
        assertTrue(fom.exists());
        HlaTestProcessor proc = new HlaTestProcessor(config, fom);
        IOUtils.toString(new File("pom.xml").toURI());

        assertNotNull(proc);

    }

}
