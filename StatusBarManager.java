import javax.swing.*;
import java.awt.*;

public class StatusBarManager extends JPanel {
    private JLabel scoreLabel;
    private JLabel lifeLabel;

    public StatusBarManager() {
        setOpaque(false);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
        scoreLabel.setForeground(Color.WHITE);

        lifeLabel = new JLabel("Life: ");
        lifeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        lifeLabel.setForeground(Color.WHITE);

        add(scoreLabel);
        add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing between labels
        add(lifeLabel);
    }

    public void gameOverTable(Game game, int score) {
        remove(scoreLabel);
        remove(lifeLabel);

        JLabel scoreLabel1 = new JLabel("Game Over");
        scoreLabel1.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel1.setForeground(Color.WHITE);

        JLabel gameOverLabel = new JLabel("Your Score: " + score);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setForeground(Color.WHITE);

        setLayout(new BorderLayout()); // Set the layout manager of the status bar panel
        add(scoreLabel1, BorderLayout.NORTH);
        add(gameOverLabel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }



    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }
}

