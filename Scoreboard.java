import javax.swing.*;
import java.awt.*;

public class Scoreboard extends JLabel { //scoreboard
    Scoreboard() {
        setText("Score: 0"); // Initialize the scoreboard with a default score of 0
        setHorizontalAlignment(JLabel.CENTER);
        setFont(new Font("Arial", Font.BOLD, 24));
        setBounds(0, 0, 200, 30);
        setForeground(Color.cyan);
        setOpaque(false);
    }

    public void updateScore(int score) {
        setText("Score: " + score); // Update the scoreboard with the new score
        repaint();
    }
}
