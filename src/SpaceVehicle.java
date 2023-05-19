import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class SpaceVehicle extends JFrame implements KeyListener {
    JLabel label;
    ImageIcon icon;
    boolean wKeyPressed;
    boolean aKeyPressed;
    boolean sKeyPressed;
    boolean dKeyPressed;
    boolean fireAllowed;
    JLabel bullet;

    SpaceVehicle() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.addKeyListener(this);

        URL iconPath = getClass().getResource("alien1.png");
        icon = new ImageIcon(iconPath);

        if (icon.getImage()!=null) {
            label = new JLabel();
            label.setBounds(0, 0, 47, 48);
            label.setIcon(icon);
            label.setOpaque(true);
            this.getContentPane().setBackground(Color.GRAY);
            this.add(label);

            bullet = new JLabel(new ImageIcon(getClass().getResource("alien1.png")));
            bullet.setBounds(0, -20, 10, 20);
            bullet.setVisible(false);
            this.add(bullet);

            this.setVisible(true);
        } else {
            System.out.println("Failed to load the image.");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                wKeyPressed = true;
                break;
            case KeyEvent.VK_A:
                aKeyPressed = true;
                break;
            case KeyEvent.VK_S:
                sKeyPressed = true;
                break;
            case KeyEvent.VK_D:
                dKeyPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                throwBullet();
                break;
        }
        moveLabel();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // keyReleased = called whenever a button is released
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                wKeyPressed = false;
                break;
            case KeyEvent.VK_A:
                aKeyPressed = false;
                break;
            case KeyEvent.VK_S:
                sKeyPressed = false;
                break;
            case KeyEvent.VK_D:
                dKeyPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                fireAllowed = true;
        }
        moveLabel();
    }

    private void moveLabel() {
        int x = label.getX();
        int y = label.getY();
        int dx = 0;
        int dy = 0;

        if (wKeyPressed) {
            dy -= 10;
        }
        if (aKeyPressed) {
            dx -= 10;
        }
        if (sKeyPressed) {
            dy += 10;
        }
        if (dKeyPressed) {
            dx += 10;
        }
        if(dKeyPressed && sKeyPressed || dKeyPressed && wKeyPressed || wKeyPressed && aKeyPressed || aKeyPressed && sKeyPressed){
            dx/=2;
            dy/=2;
        }

        label.setLocation(x + dx, y + dy);
    }

    private void throwBullet() {
        if(fireAllowed != true) return;
        int spaceshipX = label.getX();
        int spaceshipY = label.getY();

        var bulletURL = getClass().getResource("/alien1.png");
        if (bulletURL == null) {
            System.out.println("Failed to load the bullet image.");
            return;
        }

        ImageIcon bulletIcon = new ImageIcon(bulletURL);
        JLabel newBullet = new JLabel(bulletIcon);
        newBullet.setBounds(spaceshipX + 20, spaceshipY - 20, 10, 20);
        this.getContentPane().add(newBullet);

        Thread bulletThread = new Thread(() -> {
            while (newBullet.isVisible() && newBullet.getY() >= 0) {
                int bulletX = newBullet.getX();
                int bulletY = newBullet.getY();

                newBullet.setLocation(bulletX, bulletY - 10);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(() -> {
                this.getContentPane().remove(newBullet);
                this.repaint();
            });
        });

        fireAllowed =false;

        bulletThread.start();
    }


    public static void main(String[] args) {
        new SpaceVehicle();
    }
}
