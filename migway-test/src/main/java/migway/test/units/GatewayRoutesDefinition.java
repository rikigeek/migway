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
package migway.test.units;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.RTIinternalError;

import java.util.HashMap;
import java.util.Map;

import migway.plugins.dds.DdsiProcessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import LDM.PojoFactory;
import LDM.platform.PlatformCapability_T;

public class GatewayRoutesDefinition extends RouteBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(GatewayRoutesDefinition.class);
    private EncoderFactory encoderFactory;
    

    public GatewayRoutesDefinition() {
        try {
            encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        } catch (RTIinternalError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void configure() throws Exception {
        from("file://test/fom").to("xslt://fom2html.xsl").to("file://test/out?fileName=${file:name.noext}.html");

        from("hla:interaction/Fire").log("Fire");
        from("hla:object/Tank?attributes=orientation x y").process(new Processor() {
            @SuppressWarnings("unchecked")
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, byte[]> message = new HashMap<String, byte[]>();
                
                int x = 0, y = 0, orientation = 0;

                try {
                    // body = (Map<String, byte[]>) exchange.getIn().getBody();
                    Object body = exchange.getIn().getBody();

                    Class<?> mapClass = message.getClass();
                    if (mapClass.isInstance(body)) {
                        message = (Map<String, byte[]>) mapClass.cast(body);
                    } else {
                        LOGGER.error("Received body is not the correct type");
                        return;
                    }
                    
                    for (String attr : message.keySet() )
                    {
                        LOGGER.info("--- Attribute " + attr);
                        LOGGER.debug("--- value = " + message.get(attr).getClass().getSimpleName());
                        // decode int
                        HLAinteger32BE hlaint = encoderFactory.createHLAinteger32BE();
                        LOGGER.debug("HLAinteger32BE length = {}",hlaint.getEncodedLength());
                        hlaint.decode(message.get(attr));
                        LOGGER.debug("After decode: value = {}, length = {}", hlaint.getValue(), hlaint.getEncodedLength());
                        if ("x".equals(attr))
                            x = hlaint.getValue();
                        if ("y".equals(attr))
                            y = hlaint.getValue();
                        if ("orientation".equals(attr))
                            orientation = hlaint.getValue();
                        
                    }
                } catch (ClassCastException e) {
                    LOGGER.error("Received body is not the correct type " + e.toString());

                }
                LOGGER.info(String.format("X/Y/ORIENTATION = %d/%d/%d", x, y, orientation));
                // Store this in a more complicated structure
                // 1/ Variant Record
                // SpatialVariantStruct
                HLAoctet deadReckoningAlgorithmEnum =  encoderFactory.createHLAoctet();
                HLAvariantRecord<HLAoctet> spatialVariantStruct = encoderFactory.createHLAvariantRecord(deadReckoningAlgorithmEnum);
                // Case 0
                HLAfixedRecord spatialStatic = encoderFactory.createHLAfixedRecord();
                HLAfixedRecord worldLocation = encoderFactory.createHLAfixedRecord();
                HLAfloat64BE x64 = encoderFactory.createHLAfloat64BE(x);
                HLAfloat64BE y64 = encoderFactory.createHLAfloat64BE(y);
                HLAfloat64BE z64 = encoderFactory.createHLAfloat64BE(0);
                
                worldLocation.add(x64);
                worldLocation.add(y64);
                worldLocation.add(z64);
                spatialStatic.add(worldLocation);
                HLAboolean isFrozen = encoderFactory.createHLAboolean(false);
                spatialStatic.add(isFrozen);
                HLAfixedRecord orientationSpatial = encoderFactory.createHLAfixedRecord();
                HLAfloat32BE psi = encoderFactory.createHLAfloat32BE(0.0F);
                HLAfloat32BE theta = encoderFactory.createHLAfloat32BE(orientation);
                HLAfloat32BE phi = encoderFactory.createHLAfloat32BE(0.0F);
                
                orientationSpatial.add(psi);
                orientationSpatial.add(theta);
                orientationSpatial.add(phi);
                spatialStatic.add(orientationSpatial);
                
                HLAoctet deadReckoningAlgorithm =  encoderFactory.createHLAoctet((byte) 0);
                spatialVariantStruct.setVariant(deadReckoningAlgorithm, spatialStatic);
                
                message.put("RelativeSpatial", spatialVariantStruct.toByteArray());
                // Case 1 
                // Case 2 
                // Case 3 
                // Case 4 
                // ...

                // System.out.println(body.size());
            }
        }).to("log:migway");

        from("ddsi:Attitude:0/LDM.navigation.NavAttitudeState_T").process(new DdsiProcessor()).to("log:migway");
        from("ddsi:Platform:0/LDM.platform.PlatformCapability_T").process(new DdsiProcessor()).process(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                Object o = exchange.getIn().getBody();

                System.out.println(o.getClass());
                if (o instanceof LDM.platform.PlatformCapability_T) {
                    PojoFactory.debugPlatform((PlatformCapability_T) o);
                }

            }
        });

    }
}
