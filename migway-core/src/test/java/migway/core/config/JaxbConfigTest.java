package migway.core.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class JaxbConfigTest {
    /**
     * Load a configuration that use all possible structure/type/elements
     * This configuration is created for validation (JUnit test)
     * 
     * @param config
     */
    private ConfigHelper loadTestingConfig() {
        ConfigHelper config = new ConfigHelper();
        //
        ClassStructure classA = new ClassStructure("ns.classA");
        ClassStructure classB = new ClassStructure("ns.classB", new String[] { "HLA" }, new String[] { "class_b" });
        ClassStructure classC = new ClassStructure("ns.classC", new String[] { "HLA", "DDS" }, new String[] { "class_c", "classeC" });
        config.addStructure(classA);
        config.addStructure(classB);
        config.addStructure(classC);

        classA.addBasic("basicInt1", null, null, BasicTypeEnum.INT);
        classA.addClass("refClassB", null, null, "ns.classB");
        classA.addArray("arrayString1", null, null, BasicTypeEnum.STRING, 0, 0);
        classA.addArray("arrayClassC", null, null, "ns.classC", 0, 10);
        classA.addArray("arrayArray1", null, null, new ArrayType(BasicType.INT, 10, 10), 1, 6);
        classA.addClass("day", null, null, "ns.enumA");

        classB.addBasic("x", null, null, BasicTypeEnum.DOUBLE);
        classB.addBasic("y", null, null, BasicTypeEnum.DOUBLE);
        classB.addBasic("z", null, null, BasicTypeEnum.DOUBLE);

        classC.addArray("moves", null, null, "ns.classB", 0, 50);

        EnumStructure enumA = new EnumStructure("ns.enumA");
        config.addStructure(enumA);
        enumA.addEnumValue("MON");
        enumA.addEnumValue("TUE");
        enumA.addEnumValue("WED");
        enumA.addEnumValue("THU");
        enumA.addEnumValue("FRI");
        enumA.addEnumValue("SAT");
        enumA.addEnumValue("SUN");

        UnionStructure unionA = new UnionStructure("ns.unionA", new String[] { "DDS" }, new String[] { "union_A" });
        unionA.setGlobalRemoteName("unionA");
        config.addStructure(unionA);
        unionA.addBasic("choice", null, null, BasicTypeEnum.BOOLEAN);
        unionA.addClass("static", null, null, "ns.classB");
        unionA.addClass("mobile", null, null, "ns.classC");
        unionA.setDiscrimant("choice");
        unionA.addAlternative(Boolean.toString(true), "mobile");
        unionA.addAlternative(Boolean.toString(false), "static");
        
        // Add mapping
        List<Mapping> mappings = new ArrayList<Mapping>();
        Mapping mapping;
        ElementMapping elementMapping;
        KeyModel key;


        mapping = new Mapping();
        mappings.add(mapping);

        mapping.setDestination(classA.getName());
        key = new KeyModel();
        key.setMessageHeader("basicInt1");
        mapping.setDestinationKey(key);

        mapping.resetSources();
        // Attitude
        key = new KeyModel();
        key.addKeyName("x");
        mapping.addSource(classB.getName(), key);
        key = new KeyModel();
        key.addKeyName("moves");
        mapping.addSource(classC.getName(), key);



        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(classA.getElement("refClassB"));
        elementMapping.setSource(classB.getElement("y"));
        key = new KeyModel();
        key.addKeyName("*");
        elementMapping.setSourceKey(key);
        key = new KeyModel();
        key.addKeyName("z");
        elementMapping.setDestinationKey(key);

        // Add mapping
        mapping = new Mapping();
        mappings.add(mapping);
        
        mapping.setDestination(classB.getName());
        mapping.resetSources();
        mapping.addSource(classA.getName(), null);
        
        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(classB.getElement("z"));
        elementMapping.setSource(classA.getElement("basicInt1"));
        
        
        
        config.getMappings().addAll(mappings);
        return config;
        
    }
    @Test
    public void testMarshalUnmarshalConfig() {
        ConfigHelper config = loadTestingConfig();
        assertNotNull(config);
        assertTrue(config.saveConfig(new File("target/mig-testingConfig.xml")));
        
        ConfigHelper config2 = ConfigHelper.loadConfig("target/mig-testingConfig.xml");
        assertNotNull(config2);
        
        assertEquals(config, config2);
        
        
        
    }

    @Test
    public void testMarshall() throws Exception {
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:testingconfig"));

        JAXBContext jaxb = JAXBContext.newInstance("migway.core.config");

        Marshaller marshaller = jaxb.createMarshaller();

        assertNotNull(marshaller);

        marshaller.marshal(conf, new FileOutputStream(new File("target/config1.xml")));

        Object conf2 = jaxb.createUnmarshaller().unmarshal(new FileInputStream(new File("src/test/resources/config2.xml")));

        assertNotNull(conf2);
        assertTrue(conf2 instanceof ConfigHelper);
        ConfigHelper config2 = (ConfigHelper) conf2;
        config2.loadFromList();
        ValidateTestingConfigTest.assertTestingConfig(conf);
        ValidateTestingConfigTest.assertTestingConfig((ConfigHelper) conf2);

    }

    @Test
    public void testMappingConfig() {
        ConfigHelper conf = ConfigHelper.loadConfig(new File("migway:hla.tanklab"));
        conf.appendConfig(new File("migway:dds-GVA.LDM"));
        conf.appendConfig("migway:map-demo-tanklab");
//        MappingSampleFactory.loadMappingTankLab(conf);

        assertTrue(conf.getPojos().size() > 0);
        assertTrue(conf.getMappings().size() > 0);
        // conf.getPojos().clear();
        // assertTrue(conf.getPojos().size() == 0);
        conf.saveConfig(new File("target/mapping.xml"));

        ConfigHelper conf2 = ConfigHelper.loadConfig(new File("target/mapping.xml"));
        assertEquals(conf.getMappings().size(), conf2.getMappings().size());
        for (int i = 0; i < conf.getMappings().size(); i++) {
            Mapping map1 = conf.getMappings().get(i);
            Mapping map2 = conf2.getMappings().get(i);
            // Check destination
            assertEquals(map1.getDestination(), map2.getDestination());
            // Check source
            for (int j = 0; j < map1.getSources().size(); j++)
                assertEquals(map1.getSources().get(j), map2.getSources().get(j));
            // Check elements
            // assertTrue(Arrays.equals(map1.getElementMapping(),
            // map2.getElementMapping()));
            for (int j = 0; j < map1.getElementMapping().length; j++) {
                assertEquals(map1.getElementMapping()[j].getDestination().elementName, map2.getElementMapping()[j].getDestination().elementName);
                assertEquals(map1.getElementMapping()[j].getSource().elementName, map2.getElementMapping()[j].getSource().elementName);
            }
            // Check CompletionRule
            assertEquals(map1.getCompletionRule(), map2.getCompletionRule());
        }

    }
}
