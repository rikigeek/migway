package migway.core.interfaces;

import java.io.File;

import migway.core.config.ConfigHelper;


/**
 * Interface with a pseudo Dds component. In this test, the component is a
 * direct component that exchanges a Byte Buffer The format of this buffer is
 * defined by the Idl file
 * 
 * Message Headers: TopicClass is the class of the topic contains in the
 * bytebuffer.
 * 
 * @author Sébastien Tissier
 *
 */
public class FakeDdsInterface extends DefaultInterface {
    // We use Default Interface as the parent class. Everything actually usefull is there, and shared with HlaInterface 
    
    protected File idlStructFile;

    public FakeDdsInterface(ConfigHelper configHelper) {
        super(configHelper);
    }
    
    public FakeDdsInterface() {
        super();
    }

    @Override 
    protected String setInterfaceTypeName() {
        return "DDS";
    }
    
    @Override
    protected String setHeaderClassName() {
        return "TopicClass";
    }


}
