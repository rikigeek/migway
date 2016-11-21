package migway.demo.dds;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import migway.demo.dds.gui.MainGui;
import migway.demo.dds.gui.MainGuiListener;
import migway.demo.dds.gui.PlatformManager;
import migway.demo.dds.model.Platform;

public class PlatformApp {
    public static void main(String[] args) {

        // Create a controller for entities in the model, and add an entity to
        // it
        PlatformManager manager = new PlatformManager();
        Platform platform = new Platform();
        manager.addPlatform(platform);

        // Create the main UI
        JFrame frame = new JFrame();
        // Create the panel that will display the entities
        MainGui gui = new MainGui();
        // register GUI to receive platform updates
        manager.addPlatformListener(gui);

        // Create a Input listener, that will control the main loop and the
        // entities (through the controller)
        MainGuiListener listener = new MainGuiListener(gui, manager);

        
        // Build the main window
        frame.add(gui);
        frame.setSize(800, 600);
        // Make it reacts on key pressed
        frame.addKeyListener(listener);
        // Make the application close on window close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Stop all thread:
                // Stop the manager
                manager.stop();
                // Release the window
                frame.dispose();
            }
        });
        // And finally show it
        frame.setVisible(true);
        
        // And now we can start the manager
        manager.start();
    }
}
