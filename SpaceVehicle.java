import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.*;

public class SpaceVehicle extends JPanel implements KeyListener {
    private JLabel playerLabel;
    private ImageIcon icon;
    private Set<Integer> pressedKeys;
    private boolean fireAllowed;
    private JLabel bullet;
    private Set<JLabel> enemies;
    private int playerSpeed = 7;
    private int playerHP = 10; // Player's hit points
    private int enemyHP = 5; // Enemy's hit points

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
        enemies = new HashSet<>();

        startGameLoop();
    }

    private void startGameLoop() {
        this.createEnemies(5); // Create 10 enemies at the start
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveLabel();
                moveEnemies(); // Move the enemies downward
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
        for (JLabel enemy : enemies) {
            Rectangle enemyBounds = enemy.getBounds();
            if (playerBounds.intersects(enemyBounds)) {
                // Player hits the enemy
                playerHP--;
                if (playerHP <= 0) {
                    // Player is destroyed, handle game over
                    handleGameOver();
                }
            }
        }
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
                for (JLabel enemy : enemies) {
                    Rectangle enemyBounds = enemy.getBounds();
                    if (bulletBounds.intersects(enemyBounds)) {
                        // Bullet hits the enemy
                        enemyHP--;
                        if (enemyHP <= 0) {
                            // Enemy is destroyed, handle enemy destruction
                            handleEnemyDestroyed(enemy);
                        }
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

    private void handleEnemyDestroyed(JLabel enemy) {
        // Enemy is destroyed logic goes here
        // Example: Remove the enemy, award points, etc.
        remove(enemy); // Remove the enemy from the container
        enemies.remove(enemy); // Remove the enemy from the set
        repaint(); // Repaint the container to update the UI
        // Continue with other game logic
    }

    private void moveEnemies() {
        for (JLabel enemy : enemies) {
            int x = enemy.getX();
            int y = enemy.getY();
            int dy = 2; // Enemy movement speed

            enemy.setLocation(x, y + dy);

            if (y > getHeight()) {
                // Enemy has reached the bottom of the screen, remove it
                remove(enemy);
                enemies.remove(enemy);
            }
        }
    }

    private void createEnemies(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(getWidth() - 50); // Random x position
            int y = -50; // Starting position above the screen

            URL enemyURL = getClass().getResource("giphy.gif");
            if (enemyURL == null) {
                System.out.println("Failed to load the enemy image.");
                return;
            }

            ImageIcon enemyIcon = new ImageIcon(enemyURL);
            JLabel enemy = new JLabel(enemyIcon);
            enemy.setBounds(x, y, 50, 50);
            enemies.add(enemy);
            add(enemy);
        }
    }

    private void handleGameOver() {
        // Game over logic goes here
        // Example: Stop the game, display game over message, etc.
        System.exit(0); // Close the application for now
    }
}
