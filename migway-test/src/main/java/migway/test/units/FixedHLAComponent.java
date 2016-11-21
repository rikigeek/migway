package migway.test.units;

import se.pitch.camelHLA.HLAComponent;

public class FixedHLAComponent extends HLAComponent {

    public FixedHLAComponent(String federationName, String federateName, String fddFile, String[] fomModules, String crcHost, int crcPort,
            String lsd) throws Exception {
        super(federationName, federateName, fddFile, fomModules, crcHost, crcPort, lsd);
    }

//    @Override
//    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
//        return super.a(uri, remaining, parameters);
//    }

}
