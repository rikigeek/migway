package sebi.dds.chat;

import java.util.Iterator;

import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dds.gva.HelloWorldData.Msg;

public class Listener implements Runnable {
    private static Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private DataReader<Msg> reader;
    public boolean stop = false;
    public Listener(DataReader<Msg> reader) {
        this.reader = reader;
        
    }

    @Override
    public void run() {
        boolean localStop = false;
        while (!localStop) {
            Iterator<Sample<Msg>> msgList= reader.take();
            
            while (msgList.hasNext()) {
                Msg msg = msgList.next().getData();
                LOGGER.info("Id: " + msg.userID + " Message: " + msg.message);
            }

            
            // Check if we must stop
            synchronized (this) {
                localStop = stop;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                localStop = true;
            }
            
            
        }
        
        
        LOGGER.info("Finished");
    }

    
}
