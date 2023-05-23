import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager {
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
            checkBulletCollision(enemies, playerLabel, game, bullet);
            if(!bullet.isVisible() || bullet.getY() < -bullet.getHeight()){
                iterator.remove();
                game.remove(bullet);
            }
        }
    }

    public void throwBullet(boolean fireAllowed, JLabel playerLabel, Game game, JLabel bullet){
        if(!fireAllowed) return;
        int spaceshipX = playerLabel.getX();
        int spaceshipY = playerLabel.getY();

        if (bullet != null) {
            game.remove(bullet);
            game.repaint();
        }

        var bulletURL = getClass().getResource("/laserbolt-1.png");
        if (bulletURL == null) {
            System.out.println("Failed to load the bullet image.");
            return;
        }

        var url = getClass().getResource("/laserbolt-1.png");
        ImageIcon bulletIcon = new ImageIcon(url);
        JLabel newBullet = new JLabel(bulletIcon);
        newBullet.setOpaque(false);
        newBullet.setBounds(spaceshipX + 13, spaceshipY - 20, 30, 30);
        bullet.setVisible(true);
        game.add(newBullet);
        bullets.add(newBullet);

    }

    public void checkBulletCollision(List enemies, JLabel playerLabel, Game game, JLabel bullet){
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            if (!enemyLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }
            Rectangle playerBounds = playerLabel.getBounds();
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
