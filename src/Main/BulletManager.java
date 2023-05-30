package Main;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class BulletManager {
    private final List<JLabel> bulletList;
    private final List<JLabel> enemyBulletList;
    private Timer enemyBulletTimer;
    Random random;
    public BulletManager(){
        bulletList = new ArrayList<JLabel>();
        enemyBulletList = new ArrayList<JLabel>();
    }
    
    
    public void bulletLoop(Game game){
    	playerBulletLoop(game);
    	enemyBulletLoop(game);
    }
    
    public void playerBulletLoop(Game game){
        if(!bulletList.isEmpty()){
            Iterator<JLabel> iterator = bulletList.iterator();
            while(iterator.hasNext()){
                JLabel bullet = iterator.next();
                int bulletX = bullet.getX();
                int bulletY = bullet.getY();
                bullet.setLocation(bulletX, bulletY-7);
                bullet.setVisible(true);
                checkBulletCollision(game, bullet);
                if(!bullet.isVisible() || bullet.getY() < 0){
                    iterator.remove();
                    game.layeredPane.remove(bullet);
                }
            }
        }
    }
    
    public void enemyBulletLoop(Game game) {
    	if(enemyBulletTimer==null) startEnemyBulletTimer(game);
        if(!enemyBulletList.isEmpty()){
            Iterator<JLabel> iterator = enemyBulletList.iterator();
            while(iterator.hasNext()){
                JLabel bullet = iterator.next();
                if(!bullet.isVisible()) break;
                int bulletX = bullet.getX();
                int bulletY = bullet.getY();
                bullet.setLocation(bulletX, bulletY+5);
                bullet.setVisible(true);
                checkEnemyBulletCollision(game, bullet); 
                if(!bullet.isVisible() || bullet.getY() > game.getHeight()+bullet.getHeight()){
                    iterator.remove();
                    game.layeredPane.remove(bullet);
                }
            }
        }
    }
    
    public void checkEnemyBulletCollision(Game game, JLabel bullet){
        if(bullet == null){
            return;
        }
        Iterator<JLabel> enemyIterator = enemyBulletList.iterator();
        while (enemyIterator.hasNext()) {
            JLabel enemyBulletLabel= enemyIterator.next();
            if (!enemyBulletLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }

            Rectangle enemyBulletBounds = enemyBulletLabel.getBounds();
            Rectangle playerBounds = game.entityManager.player.getLabel().getBounds();
            if (enemyBulletLabel.isVisible() && enemyBulletBounds.intersects(playerBounds)) {
                game.entityManager.player.decreaseHP();
                game.statusBarManager.updateLife(game.entityManager.player.getHP());
                if (game.entityManager.player.getHP() <= 0) {
                    game.handleGameOver();
                }
                enemyBulletLabel.setVisible(false);
            }
        }
        bulletAnimation(bullet);
    }
    
    
    
    public void startEnemyBulletTimer(Game game) {
        enemyBulletTimer = new Timer();
        enemyBulletTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            	if (game.resume) {
            		System.out.println("hello");
                    enemyBulletTimer.cancel();
                    enemyBulletTimer.purge();
                    return; // Stop the timer if game.resume is true
                }
                if (game.entityManager.enemyList.isEmpty()) {
                    return; // No enemies to fire bullets
                }

                Random random = new Random();
                int enemyIndex = random.nextInt(game.entityManager.enemyList.size());
                Enemy enemy = game.entityManager.enemyList.get(enemyIndex);
                if (enemy == null || !enemy.getLabel().isVisible()) {
                    return; // Selected enemy is null or not visible
                }

                int enemyX = enemy.getLabel().getX();
                int enemyY = enemy.getLabel().getY() + enemy.getLabel().getHeight() + 10;
                createEnemyBulletLabel(game, enemyX + 15, enemyY);
                game.soundManager.enemyGunShotSound();
            }
        }, 2000, 2000);
    }
    
    private void createEnemyBulletLabel(Game game, int x, int y){
        String bulletImgName = "/icons/bulletIcons/ammo2.png";
        URL iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the ammo image.");
            return;
        }
        Icon icon = new ImageIcon(iconPath);
        JLabel localLabel = new JLabel(icon);
        localLabel.setOpaque(false);
        localLabel.setBounds(x, y, 30, 30);
        localLabel.setVisible(true);
        game.layeredPane.add(localLabel);
        enemyBulletList.add(localLabel);
    }



    public void throwBullet(Game game, Player player, boolean fireAllowed){
        if(!fireAllowed) return;
        if(player==null) return;
        int spaceshipX = player.getLabel().getX();
        int spaceshipY = player.getLabel().getY();
        String bulletImgName = "/icons/bulletIcons/ammo1.png";
        URL iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the ammo image.");
            return;
        }
        createBulletLabel(game, spaceshipX+15, spaceshipY-20);
    }

    private void createBulletLabel(Game game, int x, int y){
        String bulletImgName = "/icons/bulletIcons/ammo2.png";
        URL iconPath = getClass().getResource(bulletImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the ammo image.");
            return;
        }
        Icon icon = new ImageIcon(iconPath);
        JLabel localLabel = new JLabel(icon);
        localLabel.setOpaque(false);
        localLabel.setBounds(x, y, 30, 30);
        localLabel.setVisible(true);
        game.layeredPane.add(localLabel);
        bulletList.add(localLabel);
        game.soundManager.gunShotSound();
    }

    public void checkBulletCollision(Game game, JLabel bullet){
        if(bullet == null){
            return;
        }
        Iterator<Enemy> enemyIterator = game.entityManager.enemyList.iterator();
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

    }

    private void bulletAnimation(JLabel bullet){
        URL otherIconPath = getClass().getResource("/icons/bulletIcons/ammo1.png");
        URL otherIcon1Path = getClass().getResource("/icons/bulletIcons/ammo2.png");
        Thread bulletAnimation = new Thread(()->{
            boolean flag = true;
            while(bullet.isVisible()) {
                Icon otherIcon1 = null;
                if (flag) {
                    Icon otherIcon = null;
                    if (otherIconPath == null) System.out.println("Ammo2 icon path null");
                    else otherIcon = new ImageIcon(otherIconPath);
                    bullet.setIcon(otherIcon);
                    flag = false;
                    continue;
                }
                if (otherIconPath == null) System.out.println("Ammo1 icon path null");
                else otherIcon1 = new ImageIcon(otherIcon1Path);
                bullet.setIcon(otherIcon1);
                flag = true;
            }
        });
        bulletAnimation.start();
    }
    
    public void deleteAllBullets(Game game) {
    	Iterator<JLabel> iterator = bulletList.iterator();
        while(iterator.hasNext()){
        	JLabel bullet = iterator.next();
        	game.layeredPane.remove(bullet);
        	System.out.println("deleteplayerbullet");
            iterator.remove();
        }
        iterator = enemyBulletList.iterator();
        while(iterator.hasNext()){
        	JLabel bullet = iterator.next();
        	game.layeredPane.remove(bullet);
        	System.out.println("deleteenemybullet");
            iterator.remove();
        }
    }
}

