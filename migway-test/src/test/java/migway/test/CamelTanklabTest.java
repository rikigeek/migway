package migway.test;

import java.io.File;

import migway.core.config.ConfigHelper;
import migway.core.mapper.KeyProcessor;
import migway.core.mapper.Mapper;
import migway.plugins.dds.DdsiProcessor;
import migway.plugins.hla.HlaTestProcessor;
import migway.utils.ReflectionUtils;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.pitch.camelHLA.ExchangeHelper;
import se.pitch.camelHLA.HLAComponent;
import tanklab.Tank;

public class CamelTanklabTest extends CamelTestSupport {
    private static Logger LOG = LoggerFactory.getLogger(CamelHlaTest.class);

    private static final String FEDERATION_NAME = "TankGame";
    private static final String FEDERATE_NAME = "camelgw";
    private static final String FOM = "tank.xml";
    private static final String[] FOM_MODULES = new String[] {};
    // either use HOST + PORT.
    private static final String HOST = "localhost";
    private static final int PORT = 8989;
    // This connection string is used if HOST is empty (not null!)
    private static final String LSD = "C:\\Users\\seb\\prti1516e\\prti1516eLRC.settings";

    private ConfigHelper config;

    private HlaTestProcessor hlaInterface;
    public Object synchro = new Object();

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                config = ConfigHelper.loadConfig(new File("migway:hla.tanklab"));
                
                hlaInterface = new HlaTestProcessor(config, new File("tank.xml"));
                
                KeyProcessor keyProcessor = new KeyProcessor(config);

                from("direct:stop").stop(); // Route to stop message
                
                from("ddsi:Attitude:0/LDM.navigation.NavAttitudeState_T")
                    .process(new DdsiProcessor())
                    .to("direct:mapping");
                
                from("ddsi:Position:0/LDM.navigation.NavPositionState_T")
                    .process(new DdsiProcessor())
                    .to("direct:mapping");
                
                from("direct:mapping_test1")
                    .process(new Mapper(config))
                    .dynamicRouter(method(MapperRoute.class, "route")); // This doesn't really work, because it continues after sending, until the MapperRoute.route method return null
                
                from("direct:mapping")
                    .process(new Mapper(config))
                    .recipientList().method(MapperRoute.class, "route").stop(); 
                
                from("hla:interaction/Fire")
                    .setHeader("HlaClass", constant("tanklab.Fire"))
                    .log("fire")
                    .to("mock:interactionFire")
                    .process(hlaInterface)
                    .to("mock:interactionFirePojo");

                from("hla:object/Tank?attributes=x y orientation")
                    .setHeader("HlaClass", constant("tanklab.Tank"))
                    .to("mock:objectTank")
                    .process(hlaInterface)
                    .to("mock:objectTankPojo");
                
                from("direct:tanklab.Tank")
                    .to("direct:pojoTank");
                
                from("direct:pojoTank")
                    .process(hlaInterface)
                    .to("hla:object/Tank?attributes=x y orientation")
                    // Save the instance handle of this value
                    .process(keyProcessor)
                    .log(header(ExchangeHelper.HEADER_KEY_OBJECT_HANDLE).convertToString().toString());
            }
        };
    }
    @Override
    protected void startCamelContext() throws Exception {

        CamelContext context = context();
        LOG.debug("register HLA component");
        context.addComponent("hla", new HLAComponent(FEDERATION_NAME, FEDERATE_NAME, FOM, FOM_MODULES, HOST, PORT, LSD));
        LOG.debug("Start Camel context");
        
        
        super.startCamelContext();
    }
    
//    @Test
    public void testWriteCamelTank() throws Exception {
        tanklab.Tank tank = new Tank();
        tank.orientation = 0;
        tank.x = 100;
        tank.y = 100;
        
        template.sendBody("direct:pojoTank", tank);
        
        for (int i = 0; i < 3; i ++ ){
            Thread.sleep(1000); // wait 1s
            tank.x += 10;
            tank.y = tank.y;
            template.sendBody("direct:pojoTank", tank);
        }
        
    }

    @Test
    public void testDDSFullRoute() throws Exception {
        // Nothing special, simply wait
        Thread.sleep(6_0); //Wait 100 mn
    }
    
//    @Test
    public void testReadCamelTank() throws Exception {
        MockEndpoint mockFire = getMockEndpoint("mock:interactionFire");
        MockEndpoint mockTank = getMockEndpoint("mock:objectTank");
        MockEndpoint mockFirePojo = getMockEndpoint("mock:interactionFirePojo");
        MockEndpoint mockTankPojo = getMockEndpoint("mock:objectTankPojo");
        
        mockFire.expectedMinimumMessageCount(2);
        mockFirePojo.expectedMinimumMessageCount(2);
        mockTank.expectedMinimumMessageCount(5);
        mockTankPojo.expectedMinimumMessageCount(5);

//        synchro.wait(); // infinite wait... never stop
//        Thread.sleep(600000); // Wait 10 mn
        
        mockFire.assertIsSatisfied();
        mockFirePojo.assertIsSatisfied();
        mockTank.assertIsSatisfied();
        mockTankPojo.assertIsSatisfied();
        
        LOG.info("Received " + mockFire.getExchanges().size() + " Fire interactions ");
        LOG.info("Received " + mockFire.getReceivedCounter() + " Fire interactions count");
        LOG.info("Received " + mockTank.getExchanges().size() + " Tank Object message");
        LOG.info("Received " + mockTank.getReceivedCounter() + " Tank Object count");
        assertEquals(mockFire.getReceivedCounter(), mockFirePojo.getReceivedCounter());
        assertEquals(mockTank.getReceivedCounter(), mockTankPojo.getReceivedCounter());
        for (Exchange ex : mockFirePojo.getExchanges()) {
            ReflectionUtils.infoDeclaredReflect(ex.getIn().getBody());
        }
        for (Exchange ex : mockTankPojo.getExchanges()) {
            ReflectionUtils.infoDeclaredReflect(ex.getIn().getBody());
        }
    }

}
