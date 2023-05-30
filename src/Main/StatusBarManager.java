package Main;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StatusBarManager extends JPanel {
    private JLabel scoreLabel;
    private JLabel lifeLabel;
    private JLabel FPSlabel;
    JLabel localLabel;
    int counter = 0;
    String gameOverIMG;
    URL iconPath;
    boolean resetOnce = true;
    
    public StatusBarManager(Game game) {
        setOpaque(false);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
        scoreLabel.setForeground(Color.WHITE);
        
        lifeLabel = new JLabel("Life: ");
        lifeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        lifeLabel.setForeground(Color.WHITE);
        
        FPSlabel = new JLabel("FPS: 60");
        FPSlabel.setFont(new Font("Arial", Font.BOLD, 15));
        FPSlabel.setForeground(Color.WHITE);
        
        //add(Box.createRigidArea(new Dimension(50, 0))); // Add spacing between labels
        add(lifeLabel);
        add(scoreLabel);
        add(FPSlabel);
    }
    
    
    private void resetStatusBarInitialiseGameOver(Game game) {
    	if(resetOnce) {
            remove(scoreLabel);
            remove(lifeLabel);
            remove(FPSlabel);
            setLayout(null);
            gameOverIMG = "/icons/gameOver/Game4.png";
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
            gameOverIMG = "/icons/gameOver/Game5.png";
        else if (counter % 2 == 1)
            gameOverIMG= "/icons/gameOver/Game4.png";
        counter++;
        iconPath = getClass().getResource(gameOverIMG);
        if (iconPath == null) {
            System.out.println("Failed to load the ammo image.");
            return;
        }
        Icon icon1 = new ImageIcon(iconPath);
        localLabel.setIcon(icon1);
    }

//    public void gameOverTable(Game game, int score) {
//        remove(scoreLabel);
//        remove(lifeLabel);
//        remove(FPSlabel);
//        setLayout(null);
//        JLabel gameOverLabel = new JLabel("Game Over");
//        gameOverLabel.setLayout(null);
//        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        gameOverLabel.setForeground(Color.WHITE);
//
//        JLabel scoreLabel = new JLabel("Your Score: " + score);
//        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        scoreLabel.setForeground(Color.WHITE);
//
//        setLayout(new BorderLayout()); // Set the layout manager of the status bar panel
//        add(gameOverLabel, BorderLayout.NORTH);
//
//        add(scoreLabel, BorderLayout.CENTER);
//
//        revalidate();
//        repaint();
//    }
    
    public void updateFPS(int FPS) {
    	FPSlabel.setText("FPS: " + FPS);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }
}

