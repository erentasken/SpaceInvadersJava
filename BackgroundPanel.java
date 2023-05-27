import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;
    private Image doubleBuffer;
    private int yPos; // Y position of the background

    public BackgroundPanel(Game game) {
        backgroundImage = new ImageIcon(game.getClass().getResource("image5.jpg")).getImage(); // Replace "path_to_image.jpg" with your image path
        yPos = 0;
        setDoubleBuffered(true);
        Timer timer = new Timer(16, e -> {
            yPos += 1; // Increment the Y position
            int visibleHeight = getHeight()-yPos;
            repaint(0, yPos, getWidth(), visibleHeight);
            // Trigger repainting of the panel
        });
        timer.start();
    }

    @Override
    public void update(Graphics g) {
        Dimension size = getSize();
        if (doubleBuffer == null || doubleBuffer.getWidth(this) != size.width || doubleBuffer.getHeight(this) != size.height) {
            doubleBuffer = createImage(size.width, size.height);
        }
        if (doubleBuffer != null) {
            Graphics g2 = doubleBuffer.getGraphics();
            paint(g2);
            g2.dispose();
            g.drawImage(doubleBuffer, 0, 0, null);
        } else {
            super.update(g);
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int imageHeight = backgroundImage.getHeight(this);
        int panelHeight = getHeight();

        // Calculate the visible portion of the image based on the current Y position
        int visibleHeight = panelHeight - yPos;
        if (visibleHeight > imageHeight)
            visibleHeight = imageHeight;
        if (visibleHeight <= 0)
            return; // Nothing to draw if the visible height is zero or negative

        // Draw the visible portion of the image
        g.drawImage(backgroundImage, 0, yPos, getWidth(), yPos + visibleHeight, 0, 0, getWidth(), visibleHeight, this);

        // If needed, draw the remaining portion of the image at the top
        if (visibleHeight < panelHeight) {
            int remainingHeight = panelHeight - visibleHeight;
            g.drawImage(backgroundImage, 0, yPos + visibleHeight, getWidth(), yPos + panelHeight, 0, visibleHeight, getWidth(), imageHeight, this);
        }
    }
}
