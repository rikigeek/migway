package migway.demo.dds.gui;

import java.util.ArrayList;
import java.util.List;

import migway.demo.dds.DdsManager;
import migway.demo.dds.model.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformManager {
    private static Logger LOGGER = LoggerFactory.getLogger(PlatformManager.class);
    private List<IPlatformListener> listeners = new ArrayList<IPlatformListener>();
    private List<Platform> platforms = new ArrayList<Platform>();
    int selectedPlatform = 0;
    private Thread tLoop;
    private DdsManager ddsManager;

    public PlatformManager() {
        ddsManager = new DdsManager();
    }

    public void addPlatformListener(IPlatformListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void reflect() {
        for (IPlatformListener l : listeners) {
            l.reflectPlatformUpdate(platforms);
        }
        for (Platform platform : platforms)
            ddsManager.reflectPlatform(platform);
    }

    public void tick() {
        for (Platform p : platforms) {
            p.move(0.02);
            reflect();
        }

    }

    public void selectNextPlatform(int i) {
        selectedPlatform = (selectedPlatform + i) % platforms.size();

    }

    public void selectNextPlatform() {
        selectNextPlatform(1);
    }

    public void turnLeft() {
        platforms.get(selectedPlatform).turn(-5.0);
    }

    public void turnRight() {
        platforms.get(selectedPlatform).turn(+5.0);

    }

    public void speedUp() {
        platforms.get(selectedPlatform).speedUp(2.0);
    }

    public void speedDown() {
        platforms.get(selectedPlatform).speedUp(-2.0);
    }

    public void rollRight() {
        platforms.get(selectedPlatform).roll(+2.0);
    }

    public void rollLeft() {
        platforms.get(selectedPlatform).roll(-2.0);
    }

    public void pitchDown() {
        platforms.get(selectedPlatform).pitch(-2.0);
    }

    public void pitchUp() {
        platforms.get(selectedPlatform).pitch(2.0);
    }

    public void addPlatform(Platform platform) {
        if (!platforms.contains(platform)) {
            platforms.add(platform);
            ddsManager.addPlatform(platform);
        }

    }

    /**
     * Stop the manager
     */
    public void stop() {
        if (tLoop != null)
            tLoop.interrupt();
        ddsManager.stop();
    }

    public void start() {
        if (tLoop != null) {
            LOGGER.info(String.format("%s - Alive: %b - Interrupted: %b - Daemon: %b", tLoop.getState(), tLoop.isAlive(),
                    tLoop.isInterrupted(), tLoop.isDaemon()));
            if (tLoop.isAlive())
                return;
        }
        LOGGER.info("Start a new thread");
        tLoop = new Thread(new MainLoop(this));
        tLoop.start();
        ddsManager.start();
    }

    public void updatePlatform() {
        for (Platform platform : platforms)
            ddsManager.reflectPlatformCapability(platform);

    }

}
