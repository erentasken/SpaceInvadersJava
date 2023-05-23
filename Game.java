import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Game extends JPanel implements KeyListener{
    protected JLabel playerLabel;
    protected JLabel bullet;
    private ImageIcon icon;
    private Set<Integer> pressedKeys;
    protected List<Enemy> enemies;
    protected static boolean fireAllowed;
    private boolean creatingEnemies;
    private int playerSpeed = 7;
    protected int playerHP = 10; // Player's hit points
    Scoreboard scoreboard = new Scoreboard();
    private int score =0;
    private static final double BULLET_DELAY = 2e8;
    long lastBulletTime = 0;
    long time = System.nanoTime();
    int currentTime = 0;
    int spawner = 0;
    int levelUpTimes = 10;
    private BulletManager bulletManager;
    boolean damageOn = true;

    Game() {
        this.setBackground(Color.BLACK);
        this.add(scoreboard);
        this.setSize(500, 500);
        this.setLayout(null);
        this.setFocusable(true); // Set panel focusable
        this.addKeyListener(this);

        URL iconPath = getClass().getResource("SpaceShip.png");
        icon = new ImageIcon(iconPath);

        if (icon.getImage() != null) {
            playerLabel = new JLabel();
            playerLabel.setBounds(0, 0, 60, 62);
            playerLabel.setIcon(icon);
            playerLabel.setOpaque(false);
            add(playerLabel);
            bullet = new JLabel(new ImageIcon(getClass().getResource("alien1.png")));
            bullet.setBounds(0, -20, 10, 20);
            bullet.setVisible(false);
            this.add(bullet);
        } else {
            System.out.println("Failed to load the player image.");
        }
        pressedKeys = new HashSet<>();
        enemies = new ArrayList<>();
        bulletManager = new BulletManager();

        startGameLoop();

    }

    private void startGameLoop() {
        createEnemies(5);
        Timer timer = new Timer(16, e -> {
            currentTime = (int) ((System.nanoTime()-time)/1000000000);
            bulletManager.bulletUpdate(playerLabel, enemies, this);
            moveLabel();
            moveEnemies(); // Move the enemies downward
            spawnerUpdater();
            createEnemiesIfRequired(spawner);
            repaint();
        });
        timer.start();
    }

    private void spawnerUpdater(){
        if(currentTime == levelUpTimes){
            spawner += currentTime / 1.5;
            levelUpTimes*=3/2;
        }
    }


    private void createEnemiesIfRequired(int count) {
        if (enemies.isEmpty() && !creatingEnemies) createEnemies(count);
    }

    private void createEnemies(int count) {
        creatingEnemies = true;

        var createEnemies = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int x = random.nextInt(getWidth() - 50); // Random x position
                int y = -50; // Starting position above the screen

                URL enemyURL = getClass().getResource("enemy.png");
                if (enemyURL == null) {
                    System.out.println("Failed to load the enemy image.");
                    return;
                }

                ImageIcon enemyIcon = new ImageIcon(enemyURL);
                JLabel enemyLabel = new JLabel(enemyIcon);
                enemyLabel.setBounds(x, y, 64, 64);
                int enemyHP = 5; // Enemy's hit points
                int enemySpeed = 1; // Enemy's movement speed
                Enemy enemy = new Enemy(enemyLabel, enemyHP, enemySpeed);
                enemies.add(enemy);
                add(enemyLabel);
            }

            creatingEnemies = false;
        });
        createEnemies.start();
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
                bulletManager.throwBullet(fireAllowed, playerLabel, this, bullet);
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

    private void moveLabel() {
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

        bulletManager.checkBulletCollision(enemies, playerLabel, this, bullet); // Check collision with enemies
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
        scoreboard.updateScore(score);
    }

    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        List<JLabel> labelsToRemove = new ArrayList<>();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            JLabel enemyLabel = enemy.getLabel();
            int x = enemyLabel.getX();
            int y = enemyLabel.getY();
            int dy = enemy.getSpeed();

            enemyLabel.setLocation(x, y + dy);

            if (y > getHeight() || !enemyLabel.isVisible()) {
                // Enemy has reached the bottom of the screen or is not visible, mark it for removal
                iterator.remove();
                labelsToRemove.add(enemyLabel);
            }
        }

        // Remove the marked labels from the container
        for (JLabel label : labelsToRemove) {
            remove(label);
        }

        repaint(); // Repaint the container to update the UI
    }
}