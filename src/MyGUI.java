import javax.swing.*;
import java.awt.*;

public class MyGUI extends JPanel{
    private JMenuBar menuBar;

    public MyGUI() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem register = new JMenuItem("Register");
        JMenuItem playGame= new JMenuItem("Play Game");
        JMenuItem highScore = new JMenuItem("High Score");
        JMenuItem quit = new JMenuItem("Quit");
        fileMenu.add(register);
        fileMenu.add(playGame);
        fileMenu.add(highScore);
        fileMenu.add(quit);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        helpMenu.add(about);

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setPreferredSize(new Dimension (535, 396));
        setLayout (null);

        add(menuBar);
        menuBar.setBounds(0, 0, 535, 25);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Space Invaders");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MyGUI());

        frame.setMaximumSize(new Dimension(1000,600));
        frame.setMinimumSize(new Dimension(535, 500));

        // align window to center of the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
