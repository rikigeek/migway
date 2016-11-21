package migway.test.units;

import migway.utils.Console;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import se.pitch.camelHLA.HLAComponent;
//import edu.cyc14.ddsi.DdsiComponent;

public class MainApp {

    public static void main(String[] args) {
        // create Camel context
        final CamelContext ctx = new DefaultCamelContext();
        HLAComponent hlaComp;
//        DdsiComponent ddsiComponent;
        // add Routes
        try {

            String federationName = "TankGame";
            String federate = "CamelGw";
            // String fom =
            // "C:\\Program Files\\PitchActorsCore\\fom\\RPR2-D17_2010.xml";
            // String fom =
            // "D:\\StageCYC14-Source\\Licences\\Pitch\\PitchTraining\\TankLab\\tank.xml";
            String fom = "tank.xml";
            String[] additionnalFoms = new String[] {};
            String crcHost = "localhost";
            int crcPort = 8989;
            String lsd = "C:\\Users\\seb\\prti1516e\\prti1516eLRC.settings";
            hlaComp = new HLAComponent(federationName, federate, fom, additionnalFoms, crcHost, crcPort, lsd);
            ctx.addComponent("hla", hlaComp);

//            ddsiComponent = new DdsiComponent(ctx);
//            ctx.addComponent("ddsi", ddsiComponent);

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

            ctx.stop();
            // hlaComp.stop();
            // ddsiComponent.stop();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FAILED");

            try {
                ctx.stop();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }

}
