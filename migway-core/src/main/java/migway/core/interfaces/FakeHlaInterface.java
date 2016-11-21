package migway.core.interfaces;

import migway.core.config.ConfigHelper;

public class FakeHlaInterface extends DefaultInterface {
    public FakeHlaInterface(ConfigHelper configHelper) {
        super(configHelper);
    }
    
    public FakeHlaInterface() {
        super();
    }
    @Override
    protected String setInterfaceTypeName() {
        return "HLA";
    }
    @Override
    protected String setHeaderClassName() {
        return "ObjectClass";
    }
}
