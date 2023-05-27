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

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }
}

