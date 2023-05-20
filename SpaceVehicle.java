import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class SpaceVehicle extends JPanel implements KeyListener {
    private JLabel playerLabel;
    private JLabel bullet;
    private ImageIcon icon;
    private Set<Integer> pressedKeys;
    private List<Enemy> enemies;
    private boolean fireAllowed;
    private boolean creatingEnemies;

    private int playerSpeed = 7;
    private int playerHP = 10; // Player's hit points

    SpaceVehicle() {
        this.setSize(500, 500);
        this.setLayout(null);
        this.setFocusable(true); // Set panel focusable
        this.requestFocusInWindow(); // Request focus for key events
        this.addKeyListener(this);

        URL iconPath = getClass().getResource("alien1.png");
        icon = new ImageIcon(iconPath);

        if (icon.getImage() != null) {
            playerLabel = new JLabel();
            playerLabel.setBounds(0, 0, 47, 48);
            playerLabel.setIcon(icon);
            playerLabel.setOpaque(true);
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

        startGameLoop();
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

                URL enemyURL = getClass().getResource("giphy.gif");
                if (enemyURL == null) {
                    System.out.println("Failed to load the enemy image.");
                    return;
                }

                ImageIcon enemyIcon = new ImageIcon(enemyURL);
                JLabel enemyLabel = new JLabel(enemyIcon);
                enemyLabel.setBounds(x, y, 50, 50);
                int enemyHP = 5; // Enemy's hit points
                int enemySpeed = 2; // Enemy's movement speed
                Enemy enemy = new Enemy(enemyLabel, enemyHP, enemySpeed);
                enemies.add(enemy);
                add(enemyLabel);
            }

            creatingEnemies = false;
        });

        createEnemies.start();
    }

    private void startGameLoop() {

        createEnemies(5);
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveLabel();
                moveEnemies(); // Move the enemies downward
                createEnemiesIfRequired(5);
            }
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
            throwBullet();
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

        checkCollision(); // Check collision with enemies
    }

    private void checkCollision() {
        Rectangle playerBounds = playerLabel.getBounds();

        // Check collision with each enemy
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            if (!enemyLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }

            Rectangle enemyBounds = enemyLabel.getBounds();
            if (playerBounds.intersects(enemyBounds)) {
                // Player hits the enemy
                playerHP--;
                if (playerHP <= 0) {
                    // Player is destroyed, handle game over
                    handleGameOver();
                }
            }

            Rectangle bulletBounds = bullet.getBounds();
            if (bullet.isVisible() && bulletBounds.intersects(enemyBounds)) {
                // Bullet hits the enemy
                enemy.decreaseHP();
                if (enemy.getHP() <= 0) {
                    // Enemy is destroyed, handle enemy destruction
                    handleEnemyDestroyed(enemy);
                    enemyIterator.remove(); // Remove the enemy from the list
                }
                // Remove the bullet
                bullet.setVisible(false);
            }
        }
    }

    private void handleGameOver() {
        System.exit(1);
    }

    private boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    private void throwBullet() {
        if (!fireAllowed) return;
        int spaceshipX = playerLabel.getX();
        int spaceshipY = playerLabel.getY();

        var bulletURL = getClass().getResource("/alien1.png");
        if (bulletURL == null) {
            System.out.println("Failed to load the bullet image.");
            return;
        }

        ImageIcon bulletIcon = new ImageIcon(bulletURL);
        JLabel newBullet = new JLabel(bulletIcon);
        newBullet.setBounds(spaceshipX + 20, spaceshipY - 20, 10, 20);
        add(newBullet);

        Thread bulletThread = new Thread(() -> {
            while (newBullet.isVisible() && newBullet.getY() >= 0) {
                int bulletX = newBullet.getX();
                int bulletY = newBullet.getY();

                newBullet.setLocation(bulletX, bulletY - 10);

                // Check collision with enemies
                Rectangle bulletBounds = newBullet.getBounds();
                for (Enemy enemy : enemies) {
                    JLabel enemyLabel = enemy.getLabel();
                    Rectangle enemyBounds = enemyLabel.getBounds();
                    if (bulletBounds.intersects(enemyBounds)) {
                        // Bullet hits the enemy
                        enemy.decreaseHP();
                        if (enemy.getHP() <= 0) {
                            // Enemy is destroyed, handle enemy destruction
                            handleEnemyDestroyed(enemy);
                        }
                        // Remove the bullet
                        remove(newBullet);
                        newBullet.setVisible(false);
                        break;  // Exit the loop when an enemy is hit
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            SwingUtilities.invokeLater(() -> {
                remove(newBullet);
                repaint();
            });
        });

        fireAllowed = false;
        bulletThread.start();
    }

    private void handleEnemyDestroyed(Enemy enemy) {
        // Enemy is destroyed logic goes here
        // Example: Remove the enemy, award points, etc.
        JLabel enemyLabel = enemy.getLabel();
        enemyLabel.setVisible(false); // Set the enemy label as not visible
        remove(enemyLabel); // Remove the enemy from the container
        repaint(); // Repaint the container to update the UI
        // Continue with other game logic
    }

    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            JLabel enemyLabel = enemy.getLabel();
            int x = enemyLabel.getX();
            int y = enemyLabel.getY();
            int dy = enemy.getSpeed(); // Enemy movement speed

            enemyLabel.setLocation(x, y + dy);

            if (y > getHeight() || !enemyLabel.isVisible()) {
                // Enemy has reached the bottom of the screen or is not visible, remove it
                iterator.remove();
                remove(enemyLabel);
            }
        }
    }
}


