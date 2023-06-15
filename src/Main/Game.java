package Main;
import Manager.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
@SuppressWarnings("serial")
public class Game extends JPanel implements KeyListener{
    public JLabel enemyLabel;
    protected boolean fireAllowed;
    private final Set<Integer> pressedKeys;

    public int score =0;
    private static final double BULLET_DELAY = 2e8;
    private long lastBulletTime = 0;
    private long time = System.nanoTime();
    private int stopWatch = 0;
    private int levelUpTimes = 10;

    private final BulletManager bulletManager; 
    private final EntityManager entityManager;
    private final StatusBarManager statusBarManager;
    private final SoundManager soundManager;
    
    public boolean reset = false;
    private Timer timer;
    public boolean gameOver = false;

    private int fps = 0;
    private int frameCount = 0;
    private long lastFPSTime = 0;
    boolean resetOnce = true;
    private boolean resume = false;

    private Background background;

    public JLayeredPane layeredPane; 
    
    public boolean wait = false; // trash
    public boolean mouseTrigger =false; // trash
    
    
    
    public Game() {
    	pressedKeys = new HashSet<>(); 
    	bulletManager = new BulletManager();
        entityManager = new EntityManager();
        statusBarManager = new StatusBarManager(this);;
        soundManager = new SoundManager();
        layeredPane = new JLayeredPane();;
    }
     
    public boolean startGame() { // it is unnecessary to be a boolean
    	setTheFrame();
    	getSoundManager().startGameSound();
    	getEntityManager().createPlayerLabel(this);
    	getStatusBarManager().updateScore(0);
    	getStatusBarManager().updateLife(3);
    	getStatusBarManager().setBounds(0, 0, this.getWidth(), this.getHeight());
    	layeredPane.add(getStatusBarManager());
    	startGameLoop();
    	return true;
    }
    
    private void resetPlayGround() {
    	if(resetOnce) { // that is for trigerring only once in the loop.
    		resetOnce = false;
        	background.deleteBackground();
            getEntityManager().deleteAllEnemies(this);
            getEntityManager().deletePlayer(this);
            getBulletManager().deleteAllBullets(this);
            getSoundManager().stopGameSound();
            repaint();
            revalidate();
    	}
    }

    private void startGameLoop() {
        background = new Background(this);
        timer = new Timer(15, e -> {
        	if(gameOver) {
        		resetPlayGround();
        		getSoundManager().startGameOverSound();
        		getStatusBarManager().gameOverTable(this, score);
        		timer.stop();
        	}
        	else if (reset) {
        		resetPlayGround();
        		getSoundManager().startGameSound();
                timer.stop();
                return;
            }else{
            	while(isResume()) {
            		try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	frameCount++;
                if(getStopWatch() == 0 ) getEntityManager().spawnEnemy(this, getEntityManager().getSpawnedEnemyCounter());
                setStopWatch((int) ((System.nanoTime()-time)/1000000000));
                getBulletManager().bulletLoop(this);
                getEntityManager().movePlayer(this);
                getEntityManager().enemyLoop(this);
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
            getStatusBarManager().updateFPS(fps);
        });
        fpsTimer.start();
    }
    
    
    private void setStopWatch(int i) {
		// TODO Auto-generated method stub
		stopWatch = i;
	}

	public void resumeGame(boolean boolVal) {
    	this.setResume(boolVal);
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
                getBulletManager().throwBullet(this, getEntityManager().player, fireAllowed); // convert it into getPlayer()
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

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public boolean isResume() {
		return resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

	public StatusBarManager getStatusBarManager() {
		return statusBarManager;
	}

	public BulletManager getBulletManager() {
		return bulletManager;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public int getLevelUpTimes() {
		return levelUpTimes;
	}

	public void setLevelUpTimes(double d) {
		this.levelUpTimes = (int) d;
	}

	public int getStopWatch() {
		return stopWatch;
	}
}