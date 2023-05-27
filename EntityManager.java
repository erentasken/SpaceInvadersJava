import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityManager {
    Player player;
    private int playerSpeed = 7;
    protected int playerHP = 10; // Player's hit points

    int enemyHP = 5; // Enemy's hit points
    int enemySpeed = 1; // Enemy's movement speed
    private String enemyOneImgName = "enemy1.png";

    private boolean creatingEnemies;
    private Icon icon;
    private URL iconPath;
    private JLabel localLabel;
    private int spawnRate;


    public EntityManager(){

    }

    public void createPlayerLabel(Game game){
        try{
            String playerImgName = "player.png";
            iconPath = game.getClass().getResource(playerImgName);
            icon = new ImageIcon(iconPath);
            localLabel = new JLabel();
            localLabel.setBounds(game.getWidth()/2-50, game.getHeight()-160, 55, 44);
            localLabel.setIcon(icon);
            localLabel.setOpaque(false);
            game.add(localLabel);
            player = new Player(localLabel, playerHP, playerSpeed);
        }catch (NullPointerException e){
            System.out.println("Failed to load the player image.");
        }
    }

    void movePlayer(Game game) {
        int x = player.getLabel().getX();
        int y = player.getLabel().getY();
        int dx = 0;
        int dy = 0;

        if (game.isKeyPressed(KeyEvent.VK_W)) {
            dy -= playerSpeed;
        }
        if (game.isKeyPressed(KeyEvent.VK_A)) {
            dx -= playerSpeed;
        }
        if (game.isKeyPressed(KeyEvent.VK_S)) {
            dy += playerSpeed;
        }
        if (game.isKeyPressed(KeyEvent.VK_D)) {
            dx += playerSpeed;
        }

        // Adjust for diagonal movement
        if ((game.isKeyPressed(KeyEvent.VK_W) || game.isKeyPressed(KeyEvent.VK_S))
                && (game.isKeyPressed(KeyEvent.VK_A) || game.isKeyPressed(KeyEvent.VK_D))) {
            double diagonalSpeed = playerSpeed / Math.sqrt(2);
            dx = (int) (diagonalSpeed * Math.signum(dx));
            dy = (int) (diagonalSpeed * Math.signum(dy));
        }
        player.getLabel().setLocation(x + dx, y + dy);
        //game.playerLabel.setLocation(x + dx, y + dy);
        checkPlayerCollision(game);
    }

    private void checkPlayerCollision(Game game){
        Iterator<Enemy> enemyIterator = game.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            JLabel enemyLabel = enemy.getLabel();
            if (!enemyLabel.isVisible()) {
                continue; // Skip collision detection if enemy label is not visible
            }

            Rectangle playerBounds = player.getLabel().getBounds();
            //Rectangle playerBounds = game.playerLabel.getBounds();
            Rectangle enemyBounds = enemyLabel.getBounds();
            if (playerBounds.intersects(enemyBounds)) {
                player.decreaseHP();
                if (player.getHP() <= 0) {
                    game.handleGameOver();
                }
            }
        }
    }

    public void enemyLoop(Game game){
        //if(game.currentTime == 0)  initialiseEnemies(game, 5);
        moveEnemies(game);
        spawnEnemy(game, spawnRate);
        System.out.println(game.currentTime);
    }

    private void initialiseEnemies(Game game, int count) { // original method
        creatingEnemies = true;
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
                createEnemyLabel(game, x, y);
            }
            creatingEnemies = false;
        });
        createEnemies.start();
    }
    private void createEnemyLabel(Game game, int x, int y){
        iconPath = getClass().getResource(enemyOneImgName);
        if (iconPath == null) {
            System.out.println("Failed to load the enemy image.");
            return;
        }
        icon = new ImageIcon(iconPath);
        localLabel = new JLabel(icon);
        localLabel.setBounds(x,y,icon.getIconWidth(),icon.getIconHeight());
        game.enemyLabel = localLabel;
        Enemy enemy = new Enemy(game.enemyLabel, enemyHP, enemySpeed);
        game.enemies.add(enemy);
        game.add(game.enemyLabel);
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
        if (game.enemies.isEmpty() && !creatingEnemies) initialiseEnemies(game, count);
        spawnerUpdater(game);
    }

    private void spawnerUpdater(Game game){
        if(game.currentTime == game.levelUpTimes){
            spawnRate += game.currentTime / 1.5;
            game.levelUpTimes*= 3.0 /2;
        }
    }
}

