import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager {
    private final List bullets;
    public BulletManager(){
        bullets = new ArrayList();
    }

    public boolean start(){
        if(bullets != null) return true;
        return false;
    }

    public void bulletLoop(Game game){
        if(!bullets.isEmpty()){
            Iterator<JLabel> iterator = bullets.iterator();
            while(iterator.hasNext()){
                JLabel bullet = iterator.next();
                int bulletX = bullet.getX();
                int bulletY = bullet.getY();
                bullet.setLocation(bulletX, bulletY-7);
                bullet.setVisible(true);
                checkBulletCollision(game, bullet);
                if(!bullet.isVisible() || bullet.getY() < -bullet.getHeight()){
                    iterator.remove();
                    game.remove(bullet);
                }
            }
        }
    }

    public void throwBullet(Game game, Player player, boolean fireAllowed){
        if(!fireAllowed) return;
        int spaceshipX = player.getLabel().getX();
        int spaceshipY = player.getLabel().getY();


        if (game.bullet != null) {
            game.remove(game.bullet);
            game.repaint();
        }

        String bulletImgName = "ammo2.png";
        URL iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the enemy image.");
            return;
        }
        createBulletLabel(game, spaceshipX+15, spaceshipY-20);
    }

    private void createBulletLabel(Game game, int x, int y){
        String bulletImgName = "ammo2.png";
        URL iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the enemy image.");
            return;
        }
        Icon icon = new ImageIcon(iconPath);
        JLabel localLabel = new JLabel(icon);
        localLabel.setOpaque(false);
        localLabel.setBounds(x, y, 30, 30);
        localLabel.setVisible(true);

        game.add(localLabel);
        bullets.add(localLabel);
    }

    public void checkBulletCollision(Game game, JLabel bullet){
        if(bullet == null){
            return;
        }
        Iterator<Enemy> enemyIterator = game.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            if (!enemyLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }

            Rectangle enemyBounds = enemyLabel.getBounds();
            Rectangle bulletBounds = bullet.getBounds();
            if (bullet.isVisible() && bulletBounds.intersects(enemyBounds)) {
                enemy.decreaseHP();
                if (enemy.getHP() <= 0) {
                    enemy.handleEnemyDestroyed(game);
                    enemyIterator.remove(); // Remove the enemy from the list
                }
                bullet.setVisible(false);
            }
        }

        bulletAnimation(bullet);

    }
    private void bulletAnimation(JLabel bullet){
        URL otherIconPath = getClass().getResource("ammo1.png");
        URL otherIcon1Path = getClass().getResource("ammo2.png");
        Thread bulletAnimation = new Thread(()->{
            boolean flag = true;
            while(bullet.isVisible()) {
                Icon otherIcon1 = null;
                if (flag) {
                    Icon otherIcon = null;
                    otherIcon1 = null;
                    if (otherIconPath == null) System.out.println("Ammo2 icon path null");
                    else otherIcon = new ImageIcon(otherIconPath);
                    bullet.setIcon(otherIcon);
                    flag = false;
                    try {
                        Thread.sleep(670);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                if (otherIconPath == null) System.out.println("Ammo1 icon path null");
                else otherIcon1 = new ImageIcon(otherIcon1Path);
                bullet.setIcon(otherIcon1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                flag = true;
            }
        });
        bulletAnimation.start();
    }
}
