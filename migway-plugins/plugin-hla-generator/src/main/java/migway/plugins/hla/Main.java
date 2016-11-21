package migway.plugins.hla;

public class Main {

    public static void main(String[] args) throws Exception {
        GeneratorConfiguration configuration = new GeneratorConfiguration();
        if (configuration.parse(args)) {
            if (configuration.isHelp()) {
                // If help requested, exit after
                GeneratorConfiguration.help();
                System.exit(0);
            } else {
                // set log level
                configuration.applyLogLevel();
                // Run the generator
                HlaGenerator gen; // = new HlaGenerator(configuration.getNamespace(), configuration.getFomFile(), configuration.getOutputConfigFile());
                gen = new HlaGenerator(configuration);
                
                gen.generateAll();
            }
        } else {
            GeneratorConfiguration.help();
            System.exit(-1);
        }
    }



}
