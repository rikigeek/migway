package sebi.dds.chat;

import java.util.concurrent.TimeoutException;

import migway.utils.Console;

import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.policy.Partition;
import org.omg.dds.core.policy.PolicyFactory;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.Publisher;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.Subscriber;
import org.omg.dds.topic.Topic;

import dds.gva.HelloWorldData.Msg;

public class MainApp {
    private DomainParticipant dp;
    private int userId = (int) (System.currentTimeMillis() % 100);
    private DataWriter<Msg> writer;
    private DataReader<Msg> reader;
    private Publisher pub;
    private PolicyFactory policyFactory;
    private Subscriber sub;

    public void connect() {
        System.setProperty(ServiceEnvironment.IMPLEMENTATION_CLASS_NAME_PROPERTY, "org.opensplice.dds.core.OsplServiceEnvironment");
        ServiceEnvironment env = ServiceEnvironment.createInstance(null);
        DomainParticipantFactory factory = DomainParticipantFactory.getInstance(env);
        policyFactory = PolicyFactory.getPolicyFactory(env);

        dp = factory.createParticipant(0);
    }

    public void initializeCommunication() {
        // The topic that will be exchanged
        Topic<Msg> topic = dp.createTopic("HelloWorldMsg", Msg.class);
        
        pub = dp.createPublisher();

        Partition qosPartition = policyFactory.Partition().withName("sebi");
        pub.setQos(pub.getQos().withPolicies(qosPartition));
        
        sub = dp.createSubscriber();

        writer = pub.createDataWriter(topic);
        reader = sub.createDataReader(topic);
    }

    public void chat() throws Exception {
        // First, we announce ourself
        Msg msg = new Msg(userId, "Hello boy!");
        try {
            writer.write(msg);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        
        // we start a new Listener
        Listener listener = new Listener(reader);
        Thread listenerThread = new Thread(listener);
        listenerThread.start();
        
        // And we send all information typed on keyboard
        boolean localStop = false;
//        InputStreamReader inReader = new InputStreamReader(System.in);
//        new BufferedInputStream(inReader);
        
        Console c = new Console();
        
        if (!c.isInitialized()) {
            synchronized (listener) {
                listenerThread.interrupt();
//                listener.stop = true;
//                listener.notify();
            }
            throw new Exception("No console!");
        }

        c.printf("%s\n", writer.getQos().getDeadline().getPeriod());
        c.printf("%s\n", writer.getQos().getDurability().getKind());
        c.printf("%s\n", writer.getQos().getHistory().getDepth());
        c.printf("%s\n", writer.getQos().getHistory().getKind());

        c.printf("%s\n", writer.getQos().getLifespan().getDuration());
        c.printf("%s\n", writer.getQos().getLiveliness().getKind());
        c.printf("%s\n", writer.getQos().getOwnership().getKind());
        
        c.printf("%s\n", pub.getQos().getPartition().getName().toString());


        while(!localStop) {
            c.printf("Entrez votre commande%n");
            String line = c.readLine();
            if ("quit".equalsIgnoreCase(line)) localStop = true;
            msg.message=line;
            try{
            writer.write(msg);
            } catch(TimeoutException e) {
                e.printStackTrace();
            }
        }
        synchronized(listener) {
            listenerThread.interrupt();
        }
    }

    public static void main(String[] args) {
        MainApp chatter = new MainApp();
        chatter.connect();
        chatter.initializeCommunication();
        try {
            chatter.chat();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
