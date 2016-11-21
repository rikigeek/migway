package migway.demos.tanklab;

import java.util.Map;

import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperRoute {
    private static final Logger LOG = LoggerFactory.getLogger(MapperRoute.class);
    /**
     * Bean method to choose next route depending on POJO class
     * 
     * @param body
     * @return
     */
    @org.apache.camel.InOnly
    public String route(Object body, @org.apache.camel.Properties Map<String, Object> properties, @Headers Map<String, Object> headers) {
        try {
            Boolean skip = (Boolean) properties.get("SKIPMAPPING");
            LOG.debug("Route selection - Skip = {}", skip);
            if (skip != null && skip)
                return "direct:stop";
        } catch (ClassCastException e) {
            // Ignore not valid property
        }
        String bodyType = body.getClass().getName();
        LOG.debug("Send to direct:{}", bodyType);
        return "direct:" + bodyType;
    }
}
