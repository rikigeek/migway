package migway.demos.typeconverters;

import org.apache.camel.Converter;

@Converter
public final class TemperatureConverter {

    private static final double KELVIN_FREEZIN_POINT = 273.15;

    @Converter
    public static Kelvin toKelvin(Celcius celcius) {
        Kelvin kelvin = new Kelvin();
        // Converti les Celcius en Kelvin
        kelvin.setValue(celcius.getValue() + KELVIN_FREEZIN_POINT);
        return kelvin;
    }

    @Converter
    public static Celcius toCelcius(Kelvin kelvin) {
        Celcius celcius = new Celcius();
        // converti les Kelvin en Celcius
        celcius.setValue(kelvin.getValue() - KELVIN_FREEZIN_POINT);
        return celcius;
    }

}