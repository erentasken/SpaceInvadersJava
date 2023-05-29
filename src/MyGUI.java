import javax.swing.*;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map.Entry;

import Main.*;

public class MyGUI extends JFrame {
	static HashMap<String, String> playerInfo = new HashMap<String, String>();
	PlayerScoreManager scoreBoard;
	PlayerScoreManager scoreManager;
	Game currentGame;
	String inputString;
	HighScoreGUI highScoreGUI;
	int junk=0;
	int playerNumber = 0;
    public MyGUI() throws IOException {
        initializeMenuBar();
        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());//
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        try {
			scoreBoard = new PlayerScoreManager();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        scoreManager = new PlayerScoreManager();
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
        setJMenuBar(menuBar);
        
        
        highScore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				inputString = scoreBoard.readTheFile();
				System.out.println(inputString);
	            highScoreGUI = new HighScoreGUI(inputString);
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
                    		currentGame.stop=true;
                    	}
                        Game game = new Game();
                        game.startGame();
                        add(game, BorderLayout.CENTER);//add(game);
                        game.requestFocusInWindow();
                        currentGame = game;
                        Thread stopIndicatorThread = new Thread(()->{
                    		while(!currentGame.stop) {
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
                System.exit(0);
            }
        });
        
        highScore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
        	
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
					new MyGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

    }
}