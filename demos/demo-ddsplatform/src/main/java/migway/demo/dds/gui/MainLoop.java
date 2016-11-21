package migway.demo.dds.gui;

public class MainLoop implements Runnable {

    private PlatformManager manager;

    public MainLoop(PlatformManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        boolean loop = true;
        while (loop) {
            manager.tick();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // If interrupted, stop the loop and finish the thread
                loop = false;
            }
        }

    }

}
