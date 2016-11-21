package migway.test.benchmark;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.JndiRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to benchmark Camel performances
 * 
 * @author Sébastien Tissier
 *
 */
public class CamelBenchmark {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private CamelContext camel;

    private Context createJndiContext() throws Exception {
        // This code is a copy/paste from
        // org.apache.camel.test.junit4.CamelTestCase.createJndiContext()
        Properties properties = new Properties();

        properties.put("java.naming.factory.initial", "org.apache.camel.util.jndi.CamelInitialContextFactory");
        return new InitialContext(new Hashtable<Object, Object>(properties));
    }

    public void addRoutes(RouteBuilder routes) throws Exception {
        camel.addRoutes(routes);
        
    }

    public CamelBenchmark() throws Exception {
        // Register Codec
        JndiRegistry jndi = new JndiRegistry(createJndiContext());
        // Initialize the context and add the registry to the context
        camel = new DefaultCamelContext(jndi);
    }

    public void startCamel() throws Exception {
        camel.start();
    }

    /** 
     * Initiate the execution of an benchmark
     * @param inputEndpoint
     * @param outputEndpoint
     * @param nbMessages
     * @param messagesBody
     * @param messageHeaders
     * @return
     */
    public PerfResult execute(String inputEndpoint, String outputEndpoint, int nbMessages, Object[] messagesBody, Hashtable<String, Object> messageHeaders) {
        // current result
        PerfResult result = new PerfResult();

        TestPerformer tp = new TestPerformer(camel);

        // Build a CamelMessage
        Message camelMessage = new DefaultMessage();
        for (String key : messageHeaders.keySet()) {
            camelMessage.setHeader(key, messageHeaders.get(key));
        }
        
        // Set a default value to description of the PerfResult
        result.description = String.format("[%s -> %s / %s]", inputEndpoint, outputEndpoint, messagesBody[0].getClass().getSimpleName());
        
        // do the tests by sending nbMessages messages
        result =  tp.run(inputEndpoint, outputEndpoint, nbMessages, messagesBody, messageHeaders, result);
        
        if (log.isDebugEnabled())
            System.out.println(result.toString());
        return result;

    }

}
