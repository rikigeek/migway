package migway.test;

import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32BE;

import java.util.Map;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.pitch.camelHLA.HLAComponent;

public class CamelHlaTest extends CamelTestSupport {
    private static Logger LOGGER = LoggerFactory.getLogger(CamelHlaTest.class);

    private static final String FEDERATION_NAME = "TankGame";
    private static final String FEDERATE_NAME = "camelgw";
    private static final String FOM = "tank.xml";
    private static final String[] FOM_MODULES = new String[] {};
    private static final String HOST = "localhost";
    private static final int PORT = 8989;
    private static final String LSD = "C:\\Users\\seb\\prti1516e\\prti1516eLRC.settings";

    private EncoderFactory encoderFactory;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                LOGGER.debug("configure route");
                from("direct:map").to("mock:premap")
                .setHeader("HlaClass", constant("Toto"))
                .to("mock:map");
                

                from("hla:interaction/Fire")
                .process(new Processor() {
                    
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        @SuppressWarnings("unchecked")
                        Map<String, byte[]> body = (Map<String, byte[]>) exchange.getIn().getBody();
                        HLAinteger32BE coder =  encoderFactory.createHLAinteger32BE();
                        coder.decode(body.get("x"));
                        int x = coder.getValue();
                        coder.decode(body.get("y"));
                        int y = coder.getValue();
                        
                        LOGGER.info("x = {}, y = {}", x, y);
                    }
                })
                .log("Fire");
                from("hla:object/Tank?attributes=x y orientation").log("received something");

            }
        };
    }

    @Test
    public void testHeader() throws InterruptedException {
        MockEndpoint map = getMockEndpoint("mock:map");
        MockEndpoint premap = getMockEndpoint("mock:premap");
        map.expectedMessageCount(1);
        map.expectedHeaderReceived("HlaClass", "Toto");
        premap.expectedMessageCount(1);
        premap.expectedHeaderReceived("HlaClass", null);
        template.sendBody("direct:map", "toto");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected void startCamelContext() throws Exception {

        CamelContext context = context();
        LOGGER.debug("register HLA component");
        context.addComponent("hla", new HLAComponent(FEDERATION_NAME, FEDERATE_NAME, FOM, FOM_MODULES, HOST, PORT, LSD));
        LOGGER.debug("Start Camel context");
        encoderFactory = hla.rti1516e.RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        super.startCamelContext();
    }

    @Test
    public void testTankReception() throws InterruptedException {
        // Wait 10 seconds
        Thread.sleep(10_000);
        assertTrue(true);
    }

}
