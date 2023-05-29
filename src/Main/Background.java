package Main;
import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.List;
public class Background {
    private JLabel[][] tileLabels;
    private final Game game;
    private ImageIcon[] tileIcons;
    private final int tileSize;
    private int numVerticalTiles;

    public Background(Game game) {
        this.game = game;
        tileSize = 25;

        // Load tile images from resources
        tileIcons = new ImageIcon[5];
        for (int i = 0; i < tileIcons.length; i++) {
            URL iconPath = getClass().getResource("../icons/background/oto" + i +".png");
            if (iconPath == null) {
                System.out.println("Failed to load tile " + i + " image.");
                return;
            }
            tileIcons[i] = new ImageIcon(iconPath);
        }

        // Calculate the number of tiles required to fill the game window
        int numHorizontalTiles = game.getWidth() / tileSize;
        numVerticalTiles = game.getHeight() / tileSize;

        // Create a 2D array of JLabels to represent the tiles
        tileLabels = new JLabel[numVerticalTiles][numHorizontalTiles];

        // Create and position the tile labels
        for (int row = 0; row < numVerticalTiles; row++) {
            for (int col = 0; col < numHorizontalTiles; col++) {
                shuffleTileIcons();
                int tileIndex = (row + col) % tileIcons.length; // Determine which tile image to use
                ImageIcon tileIcon = tileIcons[tileIndex];
                JLabel tileLabel = new JLabel(tileIcon);
                tileLabel.setBounds(col * tileSize, row * tileSize, tileSize, tileSize);
                game.add(tileLabel);
                tileLabels[row][col] = tileLabel;
            }
        }

        // Create the background label
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, game.getWidth(), game.getHeight());
        //game.layeredPane.add(backgroundLabel, Integer.valueOf(0));
        // Set the rendering order
        game.setComponentZOrder(backgroundLabel, 0);

        startScrolling();
    }



    private void startScrolling() {
        Thread thread = new Thread(() -> {
            Random random = new Random(); // Create a random number generator
            while (true) {
                if (game.stop) break;
                for (int row = 0; row < tileLabels.length; row++) {
                    for (int col = 0; col < tileLabels[row].length; col++) {
                        JLabel tileLabel = tileLabels[row][col];
                        shuffleTileIcons();
                        tileLabel.setLocation(tileLabel.getX(), tileLabel.getY() - 1);
                        if (tileLabel.getY() < -tileSize) {
                            shuffleTileIcons();
                            int newRow = (numVerticalTiles - 1 + row) % numVerticalTiles;
                            tileLabel.setLocation(tileLabel.getX(), tileLabel.getY() + numVerticalTiles * tileSize);
                            updateTile(tileLabel, newRow, col);
                        }
                    }
                }

                // Shuffle the tile icons randomly
                shuffleTileIcons();

                game.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    private void shuffleTileIcons() {
        List<ImageIcon> iconList = Arrays.asList(tileIcons);
        Collections.shuffle(iconList);
        tileIcons = iconList.toArray(new ImageIcon[0]);
    }


    private void updateTile(JLabel tileLabel, int row, int col) {
        int tileIndex = (row + col) % tileIcons.length;
        ImageIcon tileIcon = tileIcons[tileIndex];
        tileLabel.setIcon(tileIcon);
    }
    
    public void deleteBackground() {
        // Remove all the tile labels
        for (int row = 0; row < numVerticalTiles; row++) {
            for (int col = 0; col < tileLabels[row].length; col++) {
                JLabel tileLabel = tileLabels[row][col];
                game.remove(tileLabel);
            }
        }

        // Repaint the game panel to reflect the changes
        game.repaint();
    }
}
