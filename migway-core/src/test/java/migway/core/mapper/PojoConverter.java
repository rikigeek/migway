package migway.core.mapper;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PojoConverter extends TypeConverterSupport 
{
    private static final Logger LOG = LoggerFactory.getLogger(PojoConverter.class);

    public static OutPojo toOutPojo(InPojo in) {
        LOG.debug("Type conversion: InPojo -> OutPojo");
        OutPojo out = new OutPojo();
        out.outB = in.inB + in.inC;
        out.outC = in.inB - in.inC;
        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        LOG.debug("Request to convert " + value.getClass().toString() + " into " + type.toString());
        if (!type.equals(OutPojo.class))
            return null;
        if (! (value instanceof InPojo))
            return null;
        return (T) toOutPojo((InPojo) value);
    }

}
