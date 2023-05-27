import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyGUI extends JFrame {
    private JMenuBar menuBar;
    public MyGUI() {
        initializeMenuBar();
        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());//
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


        playGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Game game = new Game();
                        add(game, BorderLayout.CENTER);//add(game);
                        game.requestFocusInWindow();
                    }
                });
            }
        });

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyGUI();
            }
        });

    }
}