package migway.demos.tanklab;


import migway.utils.Console;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.pitch.camelHLA.HLAComponent;

public class MainApp {
    private static Logger LOG = LoggerFactory.getLogger(MainApp.class);
    
    public static void main(String[] args) {
        // create Camel context
        final CamelContext ctx = new DefaultCamelContext();
        HLAComponent hlaComp;
        // add Routes
        try {
            LOG.debug("Registering HLA Camel Component");
            // Add HLA Component
            String federationName = "TankGame";
            String federate = "CamelGw";
            String fom = "tank.xml";
            String[] additionnalFoms = new String[] {};
            String crcHost = "localhost";
            int crcPort = 8989;
            String lsd = "";
            hlaComp = new HLAComponent(federationName, federate, fom, additionnalFoms, crcHost, crcPort, lsd);
            ctx.addComponent("hla", hlaComp);
            LOG.info("HLA Camel Component registered");

            // Add routes
            ctx.addRoutes(new GatewayRoutesDefinition());

        } catch (Exception e) {

            System.err.println("Failed to create route ");
            e.printStackTrace();

            return;
        }
        try {
            // start Camel engine and routes
            ctx.start();

            // Thread.currentThread().join();
            Thread.sleep(2000);
            String line = "";
            // Wait until
            Console console = new Console();
            while (!"quit".equalsIgnoreCase(line)) {
                if ("PWD".equals(line))
                    console.printf("Hello world\n");
                console.printf("quit<ENTER> to stop\n");
                line = console.readLine();
            }
            LOG.info("Stopping Camel Context");
            ctx.stop();
            // hlaComp.stop();
            // ddsiComponent.stop();

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Something FAILED during Camel context start or during Console listening ");

            try {
                ctx.stop();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }

}
