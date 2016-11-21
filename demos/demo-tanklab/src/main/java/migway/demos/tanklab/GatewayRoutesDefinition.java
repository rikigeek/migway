package migway.demos.tanklab;
/**
 *                          Vortex Gateway
 *
 *    This software and documentation are Copyright 2010 to 2015 PrismTech
 *    Limited and its licensees. All rights reserved. See file:
 *
 *                           docs/LICENSE.html
 *
 *    for full copyright notice and license terms.
 */



import java.io.File;

import migway.core.config.ConfigHelper;
import migway.core.mapper.KeyProcessor;
import migway.core.mapper.Mapper;
import migway.plugins.dds.DdsiProcessor;
import migway.plugins.hla.HlaTestProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayRoutesDefinition extends RouteBuilder {
    private static Logger LOG = LoggerFactory.getLogger(GatewayRoutesDefinition.class);
    
    private ConfigHelper config;
    private DdsiProcessor ddsInterface;
    private HlaTestProcessor hlaInterface;
    private Mapper mapper;
    private File fom;
    private KeyProcessor keyProcessor;
    

    public GatewayRoutesDefinition() {
        LOG.info("Route Definition loader");
        //
        config = ConfigHelper.loadConfig(new File("migway:hla.tanklab"));
        config.appendConfig(new File("migway:dds-GVA.LDM"));
        config.appendConfig(new File("migway:map-demo-tanklab"));
        config.appendConfig(new File("migway:key-demo-tanklab"));
        
        fom = new File("tank.xml");
        ddsInterface = new DdsiProcessor(config);
        hlaInterface = new HlaTestProcessor(config, fom);
        mapper = new Mapper(config);
        keyProcessor = new KeyProcessor(config);
        // 
        LOG.debug("All processors are now loaded");
    }

    @Override
    public void configure() throws Exception {
        
        from("ddsi:Attitude:0/LDM.navigation.NavAttitudeState_T")
            .process(ddsInterface)
            .to("direct:mapping");
        
        from("ddsi:Position:0/LDM.navigation.NavPositionState_T")
            .process(ddsInterface)
            .to("direct:mapping");
        
        from("direct:mapping")
            .process(mapper)
            .recipientList().method(MapperRoute.class, "route").stop(); 

        from("direct:stop").stop(); // Route to stop message
        
        from("hla:interaction/Fire")
            .to("log:fire");

        from("hla:object/Tank?attributes=x y orientation")
            .process(hlaInterface);
        
        from("direct:tanklab.Tank")
            .process(hlaInterface)
            .to("hla:object/Tank?attributes=x y orientation")
            // Save the instance handle of this value
            .process(keyProcessor);
    }


}
