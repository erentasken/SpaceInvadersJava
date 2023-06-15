package GUI;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map.Entry;

import Main.*;
import Manager.*;
@SuppressWarnings("serial")
public class MyGUI extends JFrame {
	static HashMap<String, String> playerInfo = new HashMap<String, String>();
	private PlayerScoreManager scoreBoard;
	private Game currentGame;
	private String inputString;
	@SuppressWarnings("unused")
	private HighScoreGUI highScoreGUI;
	private int playerNumber = 0;
	
	JLabel startLabel;
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
                scoreBoard.deleteTheFile();
                System.exit(0);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // mouse listener for clicking on
                if (currentGame == null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                        	int var;
                        	String password = null;
                        	String nickname = JOptionPane.showInputDialog(null ,"Enter a nickname: ", "Login", JOptionPane.PLAIN_MESSAGE);
                        	if (nickname != null && !nickname.isEmpty()) {
                        	    password = JOptionPane.showInputDialog(null ,"Enter a password: ", "Login", JOptionPane.PLAIN_MESSAGE);
                        	    var = verifyPlayerInfo(nickname, password);
                				if(var==-1) {
                					JOptionPane.showMessageDialog(null, "There is no saved user with that nickname, first register from file tab.");
                					return;
                				}else if(var==0) {
                        		JOptionPane.showMessageDialog(null, "Password is not matching with that nickname, re-enter");
                				}else {
                					triggerGameStart();
                				}
                        	}
                        }
                    });
                }
            }
        });
        
        Icon icon = new ImageIcon(getClass().getResource("/resources/icons/startingIcon.png"));
        startLabel = new JLabel(icon);
        startLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
        startLabel.setVisible(true);
        add(startLabel);
        revalidate();
        repaint();
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
        
        pack();
        

        
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            	if(currentGame!=null)currentGame.resumeGame(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            	if(currentGame!=null)currentGame.resumeGame(false);
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        
        helpMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            	if(currentGame!=null)currentGame.resumeGame(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            	if(currentGame!=null)currentGame.resumeGame(false);
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        
        highScore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentGame!=null) currentGame.resumeGame(true);
				// TODO Auto-generated method stub
				inputString = scoreBoard.readTheFile();
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
				String nickname = JOptionPane.showInputDialog(null ,"Enter a nickname: ", "Register", JOptionPane.PLAIN_MESSAGE);
				String password = JOptionPane.showInputDialog(null ,"Enter a password: ", "Register", JOptionPane.PLAIN_MESSAGE);
				playerInfo.put(nickname, password);	
			}
        });

        playGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    	int var;
                    	String password = null;
                    	String nickname = JOptionPane.showInputDialog(null ,"Enter a nickname: ", "Login", JOptionPane.PLAIN_MESSAGE);
                    	if (nickname != null && !nickname.isEmpty()) {
                    	    password = JOptionPane.showInputDialog(null ,"Enter a password: ", "Login", JOptionPane.PLAIN_MESSAGE);
                    	    // Rest of your code to handle the password input
                    	    var = verifyPlayerInfo(nickname, password);
            				if(var==-1) {
            					JOptionPane.showMessageDialog(null, "There is no saved user with that nickname, first register from file tab.");
            					return;
            				}else if(var==0) {
                    		JOptionPane.showMessageDialog(null, "Password is not matching with that nickname, re-enter");
            				}else {
            					triggerGameStart();
            				}
                    	}
        				
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
    
    public int verifyPlayerInfo(String nickname, String password) {
        if (playerInfo.containsKey(nickname)) {
            String storedPassword = playerInfo.get(nickname);
            if(storedPassword.equals(password))return 1;
            return 0;
        }else {
        	return -1;
        }
    }
    
    public void triggerGameStart() { // in order to trigger to starting game .
    	remove(startLabel);
    	if(currentGame!=null) { // if there is game in progress when the user click 
    		currentGame.gameOver=false; // on playGame button, then game is going to 
    		currentGame.reset=true; // reset = true , 
    	}
        Game game = new Game();
        game.startGame();
        add(game, BorderLayout.CENTER); //add(game);
        game.requestFocusInWindow();
        currentGame = game;
        Thread stopIndicatorThread = new Thread(()->{ // that thread catchs whether the game is stop or not 
    		while(!currentGame.reset) {
    			if(currentGame.gameOver) {
    				//currentGame.getStatusBarManager().deleteGameOverTable();
    				saveTheScore();
    				break;
    			}
    			System.out.print("");
    			continue;
    		}
    		if(!currentGame.gameOver) { // if the game is not over but the user clicked play again, that scope saves the score and player info into a file additionally stops the game sound
    			currentGame.getSoundManager().stopGameSound();
    			saveTheScore();
    		}
    	});
    	stopIndicatorThread.start();
    }
    
    public void saveTheScore() { // when the game is over, it saves the score into a file in line 270
		int score = currentGame.score;
   		int counter = 0;
		String output = null;
		//player clicked play game 
		//when the game is running 
		System.out.println("reseting the game");
		
		for(Entry<String, String> entry: playerInfo.entrySet()){ // after the terminating, updates the player number.
            if(counter == playerNumber){
                output = entry.getKey();
            }
            counter++;
		}
        try {
			scoreBoard.writeToFile(output, score); // updates the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) { // runs the game GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyGUI();
            }
        });

    }
}