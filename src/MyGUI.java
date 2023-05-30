import javax.swing.*;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map.Entry;

import Main.*;

public class MyGUI extends JFrame {
	static HashMap<String, String> playerInfo = new HashMap<String, String>();
	private PlayerScoreManager scoreBoard;
	private Game currentGame;
	private String inputString;
	private HighScoreGUI highScoreGUI;
	int junk=0;
	int playerNumber = 0;
    public MyGUI(){
        initializeMenuBar();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 500));
        setLayout(new BorderLayout());//
        setLocationRelativeTo(null);
        setVisible(true);
        
        try {
			scoreBoard = new PlayerScoreManager();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Perform any necessary cleanup or actions here
                scoreBoard.deleteTheFile();
                System.exit(0);
            }
        });
        pack();
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
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);

        
        highScore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentGame!=null) currentGame.resumeGame(true);
				// TODO Auto-generated method stub
				inputString = scoreBoard.readTheFile();
				System.out.println(inputString);
				if(inputString !=null) highScoreGUI = new HighScoreGUI(inputString, currentGame);
			}
        	
        });
        
        about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				currentGame.resumeGame(true);
				JOptionPane.showMessageDialog(null, "Eren Tasken\neren.tasken@std.yeditepe.edu.tr\n20210702054");
				currentGame.resumeGame(false);
			}
        });
        
        register.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String nickname = JOptionPane.showInputDialog("Enter a nickname: ", null);
				String password = JOptionPane.showInputDialog("Enter a password: ", null);
				playerInfo.put(nickname, password);	
			}
        });

        playGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    	if(currentGame!=null) {
                    		currentGame.gameOver=false;
                    		currentGame.reset=true;
                    	}
                        Game game = new Game();
                        game.startGame();
                        add(game, BorderLayout.CENTER);//add(game);
                        game.requestFocusInWindow();
                        currentGame = game;
                        Thread stopIndicatorThread = new Thread(()->{
                    		while(!currentGame.reset) {
                    			System.out.print("");
                    			continue;
                    		}
                    		int score = currentGame.score;
                       		int counter = 0;
                    		String output = null;
                    		//player clicked play game 
                    		//when the game is running 
                    		System.out.println("reseting the game");
                    		
                    		for(Entry<String, String> entry: playerInfo.entrySet()){
                                if(counter == playerNumber){
                                    output = entry.getKey();
                                }
                                counter++;
                    		}
                            System.out.println("The key for value " + output + " is the score : " + score);
                            try {
								scoreBoard.writeToFile(output, score);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            
                    	});
                    	stopIndicatorThread.start();
                    }
                });
            }
        });

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	scoreBoard.deleteTheFile();
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