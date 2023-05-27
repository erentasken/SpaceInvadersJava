import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityManager {
    int enemyHP = 5; // Enemy's hit points
    int enemySpeed = 1; // Enemy's movement speed
    private String enemyOneImgName = "enemy.png";
    private String playerImgName = "player.png";
    private String bulletImgName = "alien1.png";
    private Icon icon;
    private URL iconPath;
    private JLabel localLabel;
    private int spawnRate;
    public EntityManager(){
    }

    public void initialisePlayerLabel(Game game){
        try{
            iconPath = game.getClass().getResource(playerImgName);
            icon = new ImageIcon(iconPath);
            localLabel = new JLabel();
            localLabel.setBounds(0, 0, 64, 51);
            localLabel.setIcon(icon);
            localLabel.setOpaque(false);
            game.playerLabel = localLabel;
            game.add(game.playerLabel);
        }catch (NullPointerException e){
            System.out.println("Failed to load the player image.");
        }
    }

    public void initialiseBulletLabel(Game game){
        localLabel = new JLabel(new ImageIcon(getClass().getResource(bulletImgName)));
        localLabel.setBounds(0, -20, 10, 20);
        localLabel.setVisible(false);
        game.bullet = localLabel;
        game.setComponentZOrder(game.bullet, 1);
        game.add(game.bullet);
    }

    public void initialiseEnemies(Game game, int count) {
        game.creatingEnemies = true;
        var createEnemies = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                int x = random.nextInt(game.getWidth() - 50); // Random x position
                int y = -50; // Starting position above the screen

                iconPath = getClass().getResource(enemyOneImgName);
                if (iconPath == null) {
                    System.out.println("Failed to load the enemy image.");
                    return;
                }
                icon = new ImageIcon(iconPath);
                localLabel = new JLabel(icon);
                localLabel.setBounds(x,y,64,64);

                game.enemyLabel = localLabel;
                Enemy enemy = new Enemy(game.enemyLabel, enemyHP, enemySpeed);
                game.enemies.add(enemy);
                game.add(game.enemyLabel);
            }
            game.creatingEnemies = false;
        });
        createEnemies.start();
    }

    public void enemyLoop(Game game){
        moveEnemies(game);
        spawnEnemy(game, spawnRate);
        System.out.println(game.currentTime);
    }

    private void moveEnemies(Game game){
        Iterator<Enemy> iterator = game.enemies.iterator();
        List<JLabel> labelsToRemove = new ArrayList<>();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            JLabel enemyLabel = enemy.getLabel();
            int x = enemyLabel.getX();
            int y = enemyLabel.getY();
            int dy = enemy.getSpeed();

            enemyLabel.setLocation(x, y + dy);

            if (y > game.getHeight() || !enemyLabel.isVisible()) {
                // Enemy has reached the bottom of the screen or is not visible, mark it for removal
                iterator.remove();
                labelsToRemove.add(enemyLabel);
            }
        }
        // Remove the marked labels from the container
        for (JLabel label : labelsToRemove) {
            game.remove(label);
        }
        game.repaint(); // Repaint the container to update the UI
    }

    public void spawnEnemy(Game game,int count){
        if (game.enemies.isEmpty() && !game.creatingEnemies) initialiseEnemies(game, count);
        spawnerUpdater(game);
    }

    private void spawnerUpdater(Game game){
        if(game.currentTime == game.levelUpTimes){
            spawnRate += game.currentTime / 1.5;
            game.levelUpTimes*=3/2;
        }
    }





    public void initialiseEnemies1(Game game, int count) {
        Thread initialise = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    if (game.currentTime % 10 == 0 && game.currentTime != 0) {
                        Random random = new Random();
                        int x = random.nextInt(game.getWidth() - 50); // Random x position
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
                        game.enemies.add(enemy);
                        game.add(enemyLabel);
                    }

                    Thread.sleep(10000); // Delay for 10 seconds
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        initialise.start();
    }

}

