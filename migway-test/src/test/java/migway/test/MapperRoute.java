package migway.test;

import java.util.Map;

import org.apache.camel.Headers;

public class MapperRoute {
    /**
     * Bean method to choose next route depending on Pojo class
     * 
     * @param body
     * @return
     */
    @org.apache.camel.InOnly
    public String route(Object body, @org.apache.camel.Properties Map<String, Object> properties, @Headers Map<String, Object> headers) {
        try {
            Boolean skip = (Boolean) properties.get("SKIPMAPPING");
            if (skip != null && skip)
                return "direct:stop";
        } catch (ClassCastException e) {
            // Ignore not valid property
        }
        String bodyType = body.getClass().getName();
        return "direct:" + bodyType;
    }
}
