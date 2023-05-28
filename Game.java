import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Game extends JPanel implements KeyListener{
    protected JLabel enemyLabel;
    protected boolean fireAllowed;
    private final Set<Integer> pressedKeys;
    protected List<Enemy> enemyList;


    protected int score =0;
    private static final double BULLET_DELAY = 2e8;
    long lastBulletTime = 0;
    long time = System.nanoTime();
    int currentTime = 0;
    int levelUpTimes = 10;
    private final BulletManager bulletManager = new BulletManager();
    protected EntityManager entityManager = new EntityManager();
    StatusBarManager statusBarManager = new StatusBarManager(this);
    boolean stop = false;
    private Timer timer;

    Background background;
    JLayeredPane layeredPane = new JLayeredPane();
    Game() {
        layeredPane.setBounds(0,0,500,500);
        add(layeredPane);
        statusBarManager = new StatusBarManager(this);

        this.setBackground(Color.BLACK);
        this.setSize(500, 500);
        setPreferredSize(new Dimension(500,500));
        this.setLayout(null);
        this.setFocusable(true); // Set panel focusable
        this.addKeyListener(this);
        entityManager.createPlayerLabel(this);
        pressedKeys = new HashSet<>();
        enemyList = new ArrayList<>();
        statusBarManager.updateScore(0);
        statusBarManager.updateLife(10);
        statusBarManager.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(statusBarManager);
        startGameLoop();
    }

    private void startGameLoop() {
        background = new Background(this);
        timer = new Timer(16, e -> {
            if (stop) {
                timer.stop();
                statusBarManager.gameOverTable(this, score);
            }else{
                if(currentTime == 0 ) entityManager.spawnEnemy(this, 5);
                currentTime = (int) ((System.nanoTime()-time)/1000000000);
                bulletManager.bulletLoop(this);
                entityManager.movePlayer(this);
                entityManager.enemyLoop(this);
            }
        });
        timer.start();

    }

    void handleGameOver() {
        stop=true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {
            fireAllowed = true;
        }
    }

    boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
}


