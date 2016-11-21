package migway.demo.tests;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import migway.demo.dds.DdsManager;
import migway.demo.dds.model.Platform;
import migway.plugins.dds.DdsiProcessor;

public class ATest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("ddsi:Platform:0/LDM.platform.PlatformCapability_T").wireTap("mock:ddsplatform").process(new DdsiProcessor())
                        .to("mock:platform");

                // from("direct:toto").wireTap("mock:ddsplatform").to("mock:platform");
                // from("ddsi:Platform:0/LDM.platform.PlatformCapability_T").log("toto");

            }
        };
    }

    @Test
    public void testPlatformMapping() {
        MockEndpoint mockPlatform = getMockEndpoint("mock:platform");
        MockEndpoint mockDdsPlatform = getMockEndpoint("mock:ddsplatform");
        mockPlatform.expectedMinimumMessageCount(1);
        mockDdsPlatform.expectedMinimumMessageCount(1);

        try {
            // Thread.sleep(500);
            sendDdsPacket();
            Thread.sleep(5000);
            mockPlatform.assertIsSatisfied();
            mockDdsPlatform.assertIsSatisfied();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Exchange> exchanges = mockPlatform.getReceivedExchanges();
        assertEquals("LDM.platform.PlatformCapability_T", exchanges.get(0).getIn().getBody().getClass().getName());
    }

    private void sendDdsPacket() {
        Platform platform = new Platform();
        platform.setCurrentSpeed(0.0);
        platform.setDistanceTravelled(0);
        platform.setHeight(0);
        platform.setLatitude(45.3);
        platform.setLongitude(5.25);
        platform.setPitch(0.0);
        platform.setRoll(0.0);
        platform.setYaw(45.0);

        // Connect to DDS
        DdsManager manager = new DdsManager();
        // Initialize the platform to DDS
        manager.addPlatform(platform);
        for (int i = 0; i < 50; i++) {
            platform.setHeight(i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            manager.reflectPlatformCapability(platform);
        }
        manager.stop();
    }
}
