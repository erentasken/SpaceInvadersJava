package Main;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

@SuppressWarnings("serial")
public class StatusBarManager extends JPanel {
    private JLabel scoreLabel;
    private JLabel lifeLabel;
    private JLabel FPSlabel;
    private JLabel level;
    private JLabel localLabel;
    private int counter = 0;
    private int spawnerLevel = 1;
    private String gameOverIMG;
    private URL iconPath;
    boolean resetOnce = true;
    
    public StatusBarManager(Game game) {
        setOpaque(false);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
        scoreLabel.setForeground(Color.WHITE);
        
        lifeLabel = new JLabel("Life: 3");
        lifeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        lifeLabel.setForeground(Color.WHITE);
        
        level = new JLabel("Mob Spawner Level: 0");
        level.setFont(new Font("Arial", Font.BOLD, 15));
        level.setForeground(Color.WHITE);
        
        
        FPSlabel = new JLabel("FPS: 60");
        FPSlabel.setFont(new Font("Arial", Font.BOLD, 15));
        FPSlabel.setForeground(Color.WHITE);
        
        
        add(level);
        add(Box.createRigidArea(new Dimension(35, 0))); // Add spacing between labels
        add(lifeLabel);
        add(Box.createRigidArea(new Dimension(35, 0))); // Add spacing between labels
        add(scoreLabel);
        add(Box.createRigidArea(new Dimension(35, 0))); // Add spacing between labels
        add(FPSlabel);
    }
    
    
    private void resetStatusBarInitialiseGameOver(Game game) {
    	if(resetOnce) {
            remove(scoreLabel);
            remove(lifeLabel);
            remove(FPSlabel);
            remove(level);
            setLayout(null);
            gameOverIMG = "/resources/icons/gameOver/Game4.png";
            iconPath = getClass().getResource(gameOverIMG);
            if (iconPath == null) {
                System.out.println("Failed to load the ammo image.");
                return;
            }
            Icon icon = new ImageIcon(iconPath);
            localLabel = new JLabel(icon);
            //localLabel.setOpaque(false);
            localLabel.setBounds(0, 0, game.getWidth(), game.getHeight());
            localLabel.setVisible(true);
            add(localLabel);
            revalidate();
            repaint();
    	}

    }
    
    
    public void gameOverTable(Game game, int score) {
    	resetStatusBarInitialiseGameOver(game);
    	resetOnce = false;
        if (counter % 2 == 0)
            gameOverIMG = "/resources/icons/gameOver/Game5.png";
        else if (counter % 2 == 1)
            gameOverIMG= "/resources/icons/gameOver/Game4.png";
        counter++;
        iconPath = getClass().getResource(gameOverIMG);
        if (iconPath == null) {
            System.out.println("Failed to load the ammo image.");
            return;
        }
        Icon icon1 = new ImageIcon(iconPath);
        localLabel.setIcon(icon1);
    }
    
    public void deleteGameOverTable() {
    	try {
    		remove(localLabel);
    	}catch(NullPointerException e) {
    		System.out.println("exception handled"); 
    	}
    	
    }

    
    public void updateFPS(int FPS) {
    	FPSlabel.setText("FPS: " + FPS);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }
    
    public void updateLevel() {
        level.setText("Mob Spawner Level: " + spawnerLevel++);
    }
}

