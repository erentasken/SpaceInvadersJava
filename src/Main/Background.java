package Main;
import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.List;
public class Background {
    private final JLabel[][] tileLabels;
    private final Game game;
    private ImageIcon[] tileIcons;
    private final int tileSize;
    private final int numVerticalTiles;

    public Background(Game game) {
        this.game = game;
        tileSize = 50; // size of the pictures in this case we are using 50x50 pictures

        tileIcons = new ImageIcon[5];
        loadIcons("/resources/icons/background/oto");

        // Calculate the number of tiles required to fill the game window
        int numHorizontalTiles = game.getWidth() / tileSize;
        numVerticalTiles = game.getHeight() / tileSize;

        tileLabels = new JLabel[numVerticalTiles][numHorizontalTiles];

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
        startScrolling();
    }
    

    public void loadIcons(String iconSetPath) {
    	for (int i = 0; i < tileIcons.length; i++) {
            URL iconPath = getClass().getResource(iconSetPath + i +".png");
            if (iconPath == null) {
                System.out.println("Failed to load tile " + i + " image.");
                return;
            }
            tileIcons[i] = new ImageIcon(iconPath);
        }
    }
    

    private void startScrolling() { // that renders the jlabels line by line 
        Thread thread = new Thread(() -> {
            while (true) {
            	while(game.isResume()) {
            		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
                if (game.reset || game.gameOver) break;
                for (int row = 0; row < tileLabels.length; row++) {
                    for (int col = 0; col < tileLabels[row].length; col++) {
                        JLabel tileLabel = tileLabels[row][col];
                        shuffleTileIcons();
                        tileLabel.setLocation(tileLabel.getX(), tileLabel.getY() - 1);
                        if (tileLabel.getY() < -tileSize) { // that means we are out of window, first shuffle the tiles then update the tiles
                            shuffleTileIcons();
                            int newRow = (numVerticalTiles - 1 + row) % numVerticalTiles; // we are updating the new row for rendering . 
                            tileLabel.setLocation(tileLabel.getX(), tileLabel.getY() + numVerticalTiles * tileSize);
                            updateTile(tileLabel, newRow, col);
                        }
                    }
                }

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
