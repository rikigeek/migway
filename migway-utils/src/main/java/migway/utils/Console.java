package migway.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Console class to use java.io.Console if possible. Otherwhise, use System.in and System.out
 * 
 * @author Sébastien Tissier
 *
 */
public class Console {
    private java.io.Console console;
    private BufferedReader input;

    /**
     * Return true if at least the console or the input/output streams are opened
     * If false, it means there is no way to interact with this class. Every output or 
     * input attempt will fail.
     * @return
     */
    public boolean isInitialized() {
        return (console != null || input != null);
    }
    public Console() {
        // Init the console
        console = System.console();
        if (console != null) {
            // Ok, the console is initialized
            return; 
        }

        // No console. Use the classical way
        input = new BufferedReader(new InputStreamReader(System.in));

    }

    public String readLine() throws Exception {
        if (console != null)
            return console.readLine();
        if (input != null)
            return input.readLine();
        throw new Exception("Console is not initialized");
        
    }
    public Console format(String format, Object... args) throws Exception {
        return printf(format, args);
    }
    public Console printf(String format, Object... args) throws Exception {
        if (console != null) {
            console.printf(format, args);
            return this;
        }
        if (input != null) {
            System.out.printf(format, args);
            return this;
        }
        throw new Exception("Console is not initialized");
    }
}
