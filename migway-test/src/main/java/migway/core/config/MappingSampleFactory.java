package migway.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MappingSampleFactory {

    public static void loadTankLab(ConfigHelper conf) {
        ConfigHelper structConf = ConfigHelper.loadConfig(new File("migway:hla.tanklab"));
        structConf.appendConfig(new File("migway:dds-GVA.LDM"));
        loadMappingTankLab(conf, structConf);
        loadKeysTanklab(conf);
    }

    static void loadMappingSample(ConfigHelper conf, ConfigHelper structureConfig) {
        List<Mapping> mappings = new ArrayList<Mapping>();
        Mapping mapping;
        ElementMapping elementMapping;

        Structure output;
        Structure input1;

        mapping = new Mapping();
        mappings.add(mapping);
        output = structureConfig.getPojoStructure("edu.cyc14.essais.pojo.DdsPojo");
        input1 = structureConfig.getPojoStructure("edu.cyc14.essais.pojo.MyPojo");
        mapping.setDestination(output.getName());
        mapping.resetSources();
        mapping.addSource(input1.getName(), null);

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("number"));
        elementMapping.setSource(input1.getElement("value"));

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("acknowledged"));
        elementMapping.setSource(input1.getElement("ok"));

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("description"));
        elementMapping.setSource(input1.getElement("name"));

        mapping = new Mapping();
        mappings.add(mapping);
        output = structureConfig.getPojoStructure("edu.cyc14.essais.pojo.MyPojo");
        input1 = structureConfig.getPojoStructure("edu.cyc14.essais.pojo.DdsPojo");
        mapping.setDestination(output.getName());
        mapping.resetSources();
        mapping.addSource(input1.getName(), null);

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("value"));
        elementMapping.setSource(input1.getElement("number"));

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("ok"));
        elementMapping.setSource(input1.getElement("acknowledged"));

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("name"));
        elementMapping.setSource(input1.getElement("description"));

        conf.getMappings().addAll(mappings);
    }

    static void loadMappingTankLab(ConfigHelper conf, ConfigHelper structureConfig) {
        List<Mapping> mappings = new ArrayList<Mapping>();
        Mapping mapping;
        ElementMapping elementMapping;

        Structure output;
        Structure input1, input2;

        mapping = new Mapping();
        mappings.add(mapping);
        output = structureConfig.getPojoStructure("tanklab.Tank");
        input1 = structureConfig.getPojoStructure("LDM.navigation.NavAttitudeState_T");
        input2 = structureConfig.getPojoStructure("LDM.navigation.NavPositionState_T");

        KeyModel key;
        mapping.setDestination(output.getName());
        mapping.resetSources();
        // Attitude
        key = new KeyModel();
        key.addKeyName("navElementId");
        mapping.addSource(input1.getName(), key);
        key = new KeyModel();
        key.addKeyName("navElementId");
        mapping.addSource(input2.getName(), key);


        key = new KeyModel();
        key.setMessageHeader("instanceHandle");
        mapping.setDestinationKey(key);

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("x"));
        elementMapping.setSource(input2.getElement("longitude"));
        key = new KeyModel();
        key.addKeyName("longitude");
        elementMapping.setSourceKey(key);
        key = new KeyModel();
        key.addKeyName("x");
        elementMapping.setDestinationKey(key);

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("y"));
        elementMapping.setSource(input2.getElement("latitude"));

        elementMapping = new ElementMapping();
        mapping.add(elementMapping);
        elementMapping.setDestination(output.getElement("orientation"));
        elementMapping.setSource(input1.getElement("yaw"));

        conf.getMappings().addAll(mappings);

    }

    static void loadKeysTanklab(ConfigHelper conf) {
    }
}
