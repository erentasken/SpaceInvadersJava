import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Game extends JPanel implements KeyListener{
    protected JLabel playerLabel;
    protected JLabel bullet;
    protected JLabel enemyLabel;
    protected boolean damageOn = true;
    protected boolean fireAllowed;
    private Set<Integer> pressedKeys;
    protected List<Enemy> enemies;

    protected boolean creatingEnemies;
    private int playerSpeed = 7;
    protected int playerHP = 10; // Player's hit points
    private int score =0;
    private static final double BULLET_DELAY = 2e8;
    long lastBulletTime = 0;
    long time = System.nanoTime();
    int currentTime = 0;
    int levelUpTimes = 10;
    private BulletManager bulletManager;
    private EntityManager entityManager = new EntityManager();
    StatusBarManager statusBarManager = new StatusBarManager();


    Game() {
        this.setBackground(Color.BLACK);
        this.setSize(500, 500);
        this.setLayout(null);
        this.setFocusable(true); // Set panel focusable
        this.addKeyListener(this);
        entityManager.initialisePlayerLabel(this);
        entityManager.initialiseBulletLabel(this);
        pressedKeys = new HashSet<>();
        enemies = new ArrayList<>();
        bulletManager = new BulletManager();
        statusBarManager.updateScore(0);
        statusBarManager.updateLife(5);
        statusBarManager.setBounds(5, 5, getWidth() - 10, 50);
        add(statusBarManager);
        startGameLoop();
    }

    private void startGameLoop() {
        entityManager.initialiseEnemies(this, 5);
        //createEnemies(5);
        Timer timer = new Timer(16, e -> {
            currentTime = (int) ((System.nanoTime()-time)/1000000000);
            bulletManager.bulletUpdate(playerLabel, enemies, this);
            movePlayer(playerSpeed);
            entityManager.enemyLoop(this);
        });
        timer.start();
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
                bulletManager.throwBullet(this);
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

    private void movePlayer(int playerSpeed) {
        int x = playerLabel.getX();
        int y = playerLabel.getY();
        int dx = 0;
        int dy = 0;

        if (isKeyPressed(KeyEvent.VK_W)) {
            dy -= playerSpeed;
        }
        if (isKeyPressed(KeyEvent.VK_A)) {
            dx -= playerSpeed;
        }
        if (isKeyPressed(KeyEvent.VK_S)) {
            dy += playerSpeed;
        }
        if (isKeyPressed(KeyEvent.VK_D)) {
            dx += playerSpeed;
        }

        // Adjust for diagonal movement
        if ((isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_S))
                && (isKeyPressed(KeyEvent.VK_A) || isKeyPressed(KeyEvent.VK_D))) {
            double diagonalSpeed = playerSpeed / Math.sqrt(2);
            dx = (int) (diagonalSpeed * Math.signum(dx));
            dy = (int) (diagonalSpeed * Math.signum(dy));
        }

        playerLabel.setLocation(x + dx, y + dy);

        bulletManager.checkBulletCollision(this, bullet); // Check collision with enemies
    }

    void handleGameOver() {
        System.exit(1);
    }

    private boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    protected void handleEnemyDestroyed(Enemy enemy) {
        JLabel enemyLabel = enemy.getLabel();
        enemyLabel.setVisible(false);
        remove(enemyLabel);
        repaint();
        score++;
        statusBarManager.updateScore(score);
    }

}