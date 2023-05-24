import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager {
    private String bulletImgName = "laserbolt-1.png";
    private URL iconPath;
    private Icon icon;
    private JLabel localLabel;
    private List bullets;
    public BulletManager(){
        bullets = new ArrayList();
    }

    public boolean start(){
        if(bullets != null) return true;
        return false;
    }

    public void bulletUpdate(JLabel playerLabel, List enemies, Game game){
        Iterator<JLabel> iterator = bullets.iterator();
        while(iterator.hasNext()){
            JLabel bullet = iterator.next();
            int bulletX = bullet.getX();
            int bulletY = bullet.getY();
            bullet.setLocation(bulletX, bulletY-10);
            bullet.setVisible(true);
            checkBulletCollision(game, bullet);
            if(!bullet.isVisible() || bullet.getY() < -bullet.getHeight()){
                iterator.remove();
                game.remove(bullet);
            }
        }
    }

    public void throwBullet(Game game){
        if(!game.fireAllowed) return;
        int spaceshipX = game.playerLabel.getX();
        int spaceshipY = game.playerLabel.getY();

        if (game.bullet != null) {
            game.remove(game.bullet);
            game.repaint();
        }

        iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the enemy image.");
            return;
        }
        icon = new ImageIcon(iconPath);
        localLabel = new JLabel(icon);
        localLabel.setOpaque(false);
        localLabel.setBounds(spaceshipX + 13, spaceshipY - 20, 30, 30);
        localLabel.setVisible(true);
        game.add(localLabel);
        bullets.add(localLabel);

    }

    public void checkBulletCollision(Game game, JLabel bullet){
        Iterator<Enemy> enemyIterator = game.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            if (!enemyLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }
            Rectangle playerBounds = game.playerLabel.getBounds();
            Rectangle enemyBounds = enemyLabel.getBounds();
            if (playerBounds.intersects(enemyBounds) && game.damageOn) {
                game.playerHP--;
                if (game.playerHP <= 0) {
                    game.handleGameOver();
                }
            }
            Rectangle bulletBounds = bullet.getBounds();
            if (bullet.isVisible() && bulletBounds.intersects(enemyBounds)) {
                enemy.decreaseHP();
                if (enemy.getHP() <= 0) {
                    game.handleEnemyDestroyed(enemy);
                    enemyIterator.remove(); // Remove the enemy from the list
                }
                bullet.setVisible(false);
            }

        }
    }
}
