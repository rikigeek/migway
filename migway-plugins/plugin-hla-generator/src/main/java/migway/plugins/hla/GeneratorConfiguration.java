package migway.plugins.hla;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class GeneratorConfiguration {
    private String fomFile = null;
    private String outputConfig = "config.xml";
    private String outputPojoFolder = "";
    private String pojoNamespace = "pojo";
    private boolean verbose = false;
    private boolean help = false;
    private static final int MIN_ARGS_COUNT = 1;
    private Level previousLevel;

    public String getFomFile() {
        return fomFile;
    }

    public String getOutputConfigFile() {
        return outputConfig;
    }

    public String getOutputPojoLocation() {
        return outputPojoFolder;
    }

    public String getNamespace() {
        return pojoNamespace;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public GeneratorConfiguration() {
    }

    public boolean parse(String args[]) {
        int count = args.length;
        int argumentNumber = 0; // # of arguments (not parameter) actually
                                // parsed.
        if (count < MIN_ARGS_COUNT)
            return false;
        int i = 0;
        while (i < count) {
            String argName = args[i];
            if (argName.equals("-h") || argName.equals("--help")) {
                // Display help
                help = true;
                return true;
            } else if (argName.equals("-n") || argName.equals("--namespace")) {
                // namespace of the generated POJO
                pojoNamespace = args[i + 1];
                i++;
            } else if (argName.equals("-d") || argName.equals("--pojoFolder")) {
                // folder to store generated POJO
                outputPojoFolder = args[i + 1];
                i++;
            } else if (argName.equals("-v") || argName.equals("--verbose")) {
                // verbose mode
                verbose = true;
            } else if (argName.startsWith("-")) {
                return false;
            } else {
                // Parse arguments
                if (argumentNumber == 0) {
                    // First is fom file location
                    fomFile = argName;
                    argumentNumber++;
                } else if (argumentNumber == 1) {
                    // second is location of configuration file
                    outputConfig = argName;
                    argumentNumber++;
                } else {
                    // More arguments are illegal
                    return false;
                }
            }
            i++;
        }
        // First argument is mandatory: the fom file
        if (argumentNumber == 0)
            return false;
        else
            return true;
    }

    public static void help() {
        System.err.println("Usage: java -jar plugin-hla-generator [-d|--pojoFolder <pojoFolder>] [-n|--namespace <namespace>] [-v|--verbose] [-h|--help] <FOM> [<Config>]");
        System.err.println("      <FOM>    : path to input FOM file");
        System.err.println("      <Config> : path to output configuration file");
        System.err.println("      <pojoFolder> : location where classes are generated. Default is current folder");
        System.err.println("      <namespace>  : namespace for generated Java classes");
        System.err.println(" ");
        System.err.println(" Generates a configuration for Migway and POJO .java files, from a FOM file");
    }

    public void applyLogLevel() {
        Logger logger = Logger.getLogger("migway.plugins.hla");
        previousLevel = logger.getEffectiveLevel();

        if (isVerbose()) {
            // Change to DEBUG
            if (previousLevel.isGreaterOrEqual(Level.DEBUG)) {
                // Don't change, keep the default level (set in configuration
                // file)
            } else {
                logger.setLevel(Level.DEBUG);
            }
        }
    }
}
