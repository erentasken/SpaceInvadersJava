import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class SpaceVehicle extends JPanel implements KeyListener {
    private JLabel playerLabel;
    private ImageIcon icon;
    private Set<Integer> pressedKeys;
    private boolean fireAllowed;
    private JLabel bullet;
    private JLabel enemyLabel;
    private int playerSpeed = 7;

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

        URL enemyIconPath = getClass().getResource("alien1.png");
        ImageIcon enemyIcon = new ImageIcon(enemyIconPath);

        if (enemyIcon != null) {
            enemyLabel = new JLabel(enemyIcon);
            enemyLabel.setBounds(10, 50, 50, 50);
            add(enemyLabel);
        } else {
            System.out.println("Failed to load the enemy image.");
        }

        pressedKeys = new HashSet<>();

        startGameLoop();
    }

    private void startGameLoop() {
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveLabel();
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

                // Check collision with enemy
                Rectangle bulletBounds = newBullet.getBounds();
                Rectangle enemyBounds = enemyLabel.getBounds();
                if (bulletBounds.intersects(enemyBounds)) {
                    // Bullet hits the enemy
                    URL image1URL = getClass().getResource("giphy.gif");
                    if (image1URL != null) {
                        ImageIcon image1Icon = new ImageIcon(image1URL);
                        enemyLabel.setIcon(image1Icon);

                        // Wait for 1 second
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Reset enemy image
                        enemyLabel.setIcon(icon);
                    }
                    break;  // Exit bullet thread
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
}
