package migway.demo.dds.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainGuiListener implements KeyListener {
    private static Logger LOGGER = LoggerFactory.getLogger(MainGuiListener.class); 
    private MainGui gui;
    private PlatformManager manager;

    /**
     * The listener that will receives all order from user.
     * Then it dispatch commands to the GUI and to the manager
     * @param gui
     * @param manager
     */
    public MainGuiListener(MainGui gui, PlatformManager manager) {
        this.gui = gui;
        this.manager = manager;
        LOGGER.info("List of Available keys");
        LOGGER.info("  UP/DOWN: Increase/Decrease speed of the platform");
        LOGGER.info("  LEFT/RIGH: Change the platform orientation");
        LOGGER.info("  NUMPAD 4/6/2/8: decrease/increase roll and pitch");
        LOGGER.info("  Q: Stop the platform");
        LOGGER.info("  S: Start the platform");
        LOGGER.info("  +/-: cycle platform");
        LOGGER.info("  U: Send an update of Platform capabilities");
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_UP:
            // Speedup
            manager.speedUp();
            break;
        case KeyEvent.VK_DOWN:
            manager.speedDown();
            break;
        case KeyEvent.VK_RIGHT:
            // orient right
            manager.turnRight();
            break;
        case KeyEvent.VK_LEFT:
            // orient left
            manager.turnLeft();
            break;
        case KeyEvent.VK_PLUS:
            // Next platform
            manager.selectNextPlatform();
            break;
        case KeyEvent.VK_MINUS:
            // Previous platform
            manager.selectNextPlatform(-1); 
            break;
        case KeyEvent.VK_Q:
            manager.stop();
            break;
        case KeyEvent.VK_S:
            manager.start();
            break;
        case KeyEvent.VK_NUMPAD6:
            manager.rollRight();
            break;
        case KeyEvent.VK_NUMPAD4:
            manager.rollLeft();
            break;
        case KeyEvent.VK_NUMPAD8:
            manager.pitchDown();
            break;
        case KeyEvent.VK_NUMPAD2:
            manager.pitchUp();
            break;
        case KeyEvent.VK_U:
            manager.updatePlatform();
            break;
        }
        
        gui.setDebugString(String.format("Key : %d", e.getKeyCode()));
        LOGGER.debug(String.format("Key : %d %d %c", e.getKeyCode(), e.getExtendedKeyCode(), e.getKeyChar()));
        gui.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        gui.setDebugString("");
        gui.repaint();
    }

}
