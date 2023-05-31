package GUI;
import Main.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HighScoreGUI extends JFrame {
    public HighScoreGUI(String highScores, Game currentGame) {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("HIGH SCORES");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("HIGH SCORES");
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.ALLBITS);

        JTextArea highScoresArea = new JTextArea(highScores);
        highScoresArea.setForeground(Color.WHITE);
        highScoresArea.setBackground(Color.BLACK);
        highScoresArea.setFont(new Font("Arial", Font.PLAIN, 18));
        highScoresArea.setEditable(false);
        highScoresArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(highScoresArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel contentPanel = new JPanel();
        //contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.BLACK);
        //contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        contentPanel.add(titleLabel);
        //contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some vertical spacing
        contentPanel.add(scrollPane);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(contentPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Perform any necessary cleanup or actions here
                currentGame.resumeGame(false);
            }
        });
    }
}
