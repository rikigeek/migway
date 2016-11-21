package migway.plugins.hla;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class GeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        Logger logger = Logger.getLogger("migway.plugins.hla");
        logger.setLevel(Level.INFO);

        GeneratorConfiguration conf = new GeneratorConfiguration();

        String commandLineArg = " -d pojos -n pojo RPR2-D20_2010.xml config/config.xml";
        String[] args = commandLineArg.trim().split(" ");
        assertEquals(6, args.length);

        boolean result = conf.parse(args);
        assertTrue(result);

        HlaGenerator generator = new HlaGenerator(conf);
        generator.generateAll();

        assertTrue(true);
    }

    /**
     * Test correct command line
     */
    @Test
    public void testConfigurationParser() {
        GeneratorConfiguration conf = new GeneratorConfiguration();

        String commandLineArg = " -d pojos -n pojo RPR2-D20_2010.xml config/config.xml";
        String[] args = commandLineArg.trim().split(" ");
        assertEquals(6, args.length);

        boolean result = conf.parse(args);
        assertTrue(result);

        assertFalse(conf.isHelp());
        assertFalse(conf.isVerbose());
        assertEquals("RPR2-D20_2010.xml", conf.getFomFile());
        assertEquals("pojos", conf.getOutputPojoLocation());
        assertEquals("config/config.xml", conf.getOutputConfigFile());
        assertEquals("pojo", conf.getNamespace());
    }

    @Test
    public void testConfigurationParserHelp() {
        GeneratorConfiguration conf = new GeneratorConfiguration();

        String commandLineArg = " -d pojos -n pojo RPR2-D20_2010.xml config/config.xml -h";
        String[] args = commandLineArg.trim().split(" ");
        assertEquals(7, args.length);

        boolean result = conf.parse(args);
        assertTrue(result);

        assertTrue(conf.isHelp());
        assertFalse(conf.isVerbose());
        assertEquals("RPR2-D20_2010.xml", conf.getFomFile());
        assertEquals("pojos", conf.getOutputPojoLocation());
        assertEquals("config/config.xml", conf.getOutputConfigFile());
        assertEquals("pojo", conf.getNamespace());
    }

    @Test
    public void testConfigurationParserError() {
        GeneratorConfiguration conf = new GeneratorConfiguration();

        String commandLineArg = " -z -d pojos -n pojo RPR2-D20_2010.xml config/config.xml -h";
        String[] args = commandLineArg.trim().split(" ");
        assertEquals(8, args.length);

        boolean result = conf.parse(args);
        assertFalse(result);

        // Default values: because first parameter was incorrect, all other parameters/arguments were ignored 
        assertFalse(conf.isHelp());
        assertFalse(conf.isVerbose());
        assertNull(conf.getFomFile());
        assertEquals("", conf.getOutputPojoLocation());
        assertEquals("config.xml", conf.getOutputConfigFile());
        assertEquals("pojo", conf.getNamespace());
    }
}
