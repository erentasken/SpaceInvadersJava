package Main;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;
import javax.swing.Timer;
public class Game extends JPanel implements KeyListener{
    public JLabel enemyLabel;
    protected boolean fireAllowed;
    private final Set<Integer> pressedKeys;

    public int score =0;
    private static final double BULLET_DELAY = 2e8;
    long lastBulletTime = 0;
    long time = System.nanoTime();
    public int stopWatch = 0;
    public int levelUpTimes = 10;
    final BulletManager bulletManager = new BulletManager();
    protected EntityManager entityManager = new EntityManager();
    public StatusBarManager statusBarManager = new StatusBarManager(this);;
    public boolean reset = false;
    private Timer timer;
    public boolean gameOver = false;

    private int fps = 0;
    private int frameCount = 0;
    private long lastFPSTime = 0;
    boolean resetOnce = true;
    boolean resume = false;


    Background background;
    public JLayeredPane layeredPane = new JLayeredPane();;
    
    public boolean wait = false;

    
    SoundManager soundManager = new SoundManager();
    
    public Game() {
    	pressedKeys = new HashSet<>();
    }
     
    public boolean startGame() {
    	setTheFrame();
    	soundManager.startGameSound();
    	entityManager.createPlayerLabel(this);
    	statusBarManager.updateScore(0);
    	statusBarManager.updateLife(3);
    	statusBarManager.setBounds(0, 0, this.getWidth(), this.getHeight());
    	layeredPane.add(statusBarManager);
    	startGameLoop();
    	
    	return true;
    }
    
    private void resetPlayGround() {
    	if(resetOnce) {
    		resetOnce = false;
        	background.deleteBackground();
            entityManager.deleteAllEnemies(this);
            entityManager.deletePlayer(this);
            bulletManager.deleteAllBullets(this);
            soundManager.stopGameSound();
    	}
    }

    private void startGameLoop() {
        background = new Background(this);
        timer = new Timer(15, e -> {
        	if(gameOver) {
        		resetPlayGround();
        		soundManager.startGameOverSound();
        		statusBarManager.gameOverTable(this, score);
        	}
        	else if (reset) {
                timer.stop();
                resetPlayGround();
                return;
            }else{
            	statusBarManager.deleteGameOverTable(this);
            	while(resume) {
            		System.out.println("game resumed");
            		try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	frameCount++;
                if(stopWatch == 0 ) entityManager.spawnEnemy(this, 5);
                stopWatch = (int) ((System.nanoTime()-time)/1000000000);
                bulletManager.bulletLoop(this);
                entityManager.movePlayer(this);
                entityManager.enemyLoop(this);
            }
        });
        timer.start();

        Timer fpsTimer = new Timer(1000, e -> {
        	
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastFPSTime;

            if (elapsedTime >= 1000) {
                fps = (int) (frameCount * 1000 / elapsedTime);
                frameCount = 0;
                lastFPSTime = currentTime;
            }
            statusBarManager.updateFPS(fps);
        });
        fpsTimer.start();
    }
    
    
    public void resumeGame(boolean boolVal) {
    	System.out.println("resume value " + boolVal);
    	this.resume = boolVal;
    	if(boolVal) timer.stop();
    	else timer.start();
    }
    
    
    private void setTheFrame() {
        layeredPane.setBounds(0,0,500,500);
        this.add(layeredPane);
        this.setBackground(Color.BLACK);
        this.setSize(500, 500);
        this.setLayout(null);
        this.setFocusable(true); // Set panel focusable
        this.addKeyListener(this);
    }

    public void handleGameOver() {
        gameOver=true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	if(reset || gameOver) return;
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        if (keyCode == KeyEvent.VK_SPACE) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastBulletTime;

            if (elapsedTime >= BULLET_DELAY) {
                bulletManager.throwBullet(this, entityManager.player, fireAllowed); // convert it into getPlayer()
                lastBulletTime = currentTime;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	if(reset) return;
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {
            fireAllowed = true;
        }
    }

    public boolean isKeyPressed(int keyCode) {
    	if(reset) return false;
    	else return pressedKeys.contains(keyCode);
    }
}