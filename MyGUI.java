// MyGUI.java
import javax.swing.*;
import java.awt.*;

public class MyGUI extends JFrame {
    private JMenuBar menuBar;

    public MyGUI() {
        initializeMenuBar();
        initializeComponents();

        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem register = new JMenuItem("Register");
        JMenuItem playGame = new JMenuItem("Play Game");
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

        setJMenuBar(menuBar);
    }

    private void initializeComponents() {
        add(new SpaceVehicle());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyGUI::new);
    }
}
