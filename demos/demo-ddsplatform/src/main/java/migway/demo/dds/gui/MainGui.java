package migway.demo.dds.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import migway.demo.dds.model.Platform;

public class MainGui extends JPanel implements IPlatformListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Platform> platforms;
    private String debugString;

    public MainGui() {
        debugString = "";
    }

    @Override
    public void paint(Graphics g) {
        // Get the area size
        int h = this.getHeight();
        int w = this.getWidth();
        int baseOffsetX = 20;
        int baseOffsetY = 40;
        int platformNum = 0;
        int colX, colY;
        colX = colY = 0;

        // Clear the area
        g.clearRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        // Draw a grid
        g.setColor(Color.WHITE);
        for (int x = 0; x < w; x += 250) {
            g.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += 150) {
            g.drawLine(0, y, w, y);
        }

        if (platforms == null) return;
        // Display information about each platform
        for (Platform p : platforms) {
            int startX = colX * 250 + baseOffsetX;
            int startY = colY * 150 + baseOffsetY;
            int lineHeight = 20;
            g.drawString(String.format("Platform #%d %s", platformNum, p.getIdentification()), startX, startY);
            g.drawString(String.format("P/R/Y : %.2f %.2f %.2f", p.getPitch(), p.getRoll(), p.getYaw()), startX, startY + lineHeight);
            g.drawString(String.format("Position : %.2f %.2f %.2f", p.getLatitude(), p.getLongitude(), p.getHeight()), startX, startY + 2
                    * lineHeight);
            g.drawString(String.format("Speed %.2fm/s - Distance : %.0fm", p.getCurrentSpeed(), p.getDistanceTravelled()), startX, startY
                    + 3 * lineHeight);

            platformNum++;
            colX++;
            if (colX > 3) {
                colX = 0;
                colY++;
            }
        }

        g.setColor(Color.RED);
        g.drawString(debugString, 100, 10);
    }

    public void setDebugString(String format) {
        this.debugString = format;
        this.repaint();
    }

    @Override
    public void reflectPlatformUpdate(List<Platform> platforms) {
        this.platforms = platforms;
        // Some platforms has been updated
        repaint();

    }

    @Override
    public void reflectPlatformUpdate(Platform platform) {
        // Do nothing. Wait for reflect on all updates
    }

}
