import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Bullet extends Game {
    private static ArrayList<Bullet> bulletList = new ArrayList<>();
    private JLabel bulletLabel;

    public Bullet(int spaceshipX, int spaceshipY) {
        URL bulletURL = getClass().getResource("/laserbolt-1.png");
        ImageIcon bulletIcon = new ImageIcon(bulletURL);
        bulletLabel = new JLabel(bulletIcon);
        bulletLabel.setOpaque(false);
        bulletLabel.setBounds(spaceshipX + 13, spaceshipY - 20, 30, 30);
        //bulletList.add(this);
    }

    public void checkCollision() {
        Rectangle bulletBounds = bulletLabel.getBounds();
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            Rectangle enemyBounds = enemyLabel.getBounds();
            if (bulletBounds.intersects(enemyBounds)) {
                // Bullet hits the enemy
                enemy.decreaseHP();
                if (enemy.getHP() <= 0) {
                    // Enemy is destroyed, handle enemy destruction
                    handleEnemyDestroyed(enemy);
                    enemyIterator.remove(); // Remove the enemy from the list
                }
                // Remove the bullet
                removeBullet(this);
                break; // Exit the loop when an enemy is hit
            }
        }
    }

    public JLabel getBulletLabel() {
        return bulletLabel;
    }

    public static ArrayList<Bullet> getBulletList() {
        return bulletList;
    }

    public void removeBullet(Bullet bullet) {
        bulletList.remove(bullet);
        bullet.getBulletLabel().setVisible(false);
        super.setBackground(Color.WHITE);
    }
}
