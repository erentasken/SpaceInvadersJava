//package Main;
package Manager;
import Main.*;
import Entities.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class EntityManager {
	public Player player; 
	private final int playerSpeed = 5;
	private int playerHP = 3; // Player's hit points
	private int counter = 0; // counter for player animation
	private boolean untouchable = false; // after player taken a damage, there will be untouchable time interval.
	
	public List<Enemy> enemyList;
	private int enemyHP = 10; // Enemy's hit points
	private int enemySpeed = 1; // Enemy's movement speed
	private int horizontalMoveEnemy = 1;
	private boolean creatingEnemies; // controlling the period of enemy spawning
	private int spawnedEnemyCounter = 2; // time by time spawned enemy count is increasing
	
	private Icon icon;
	private URL iconPath;
	private JLabel localLabel;
	
	public EntityManager() {
		enemyList = new ArrayList<Enemy>();
	}

	public void createPlayerLabel(Game game) {
		try {
			String playerImgName = "/resources/icons/playerIcons/player.png";
			iconPath = game.getClass().getResource(playerImgName);
			icon = new ImageIcon(iconPath);
			localLabel = new JLabel();
			localLabel.setBounds(game.getWidth() / 2 - 50, game.getHeight() - 160, icon.getIconWidth(), icon.getIconHeight());
			localLabel.setIcon(icon);
			//localLabel.setOpaque(false);
			game.add(localLabel);
			player = new Player(localLabel, playerHP, playerSpeed);
		} catch (NullPointerException e) {
			System.out.println("Failed to load the player image.");
		}
	}

	public void movePlayer(Game game) {
		if (player == null)
			return;
		int x = player.getLabel().getX();
		int y = player.getLabel().getY();
		int dx = 0;
		int dy = 0;

		if (game.isKeyPressed(KeyEvent.VK_W) && player.getLabel().getY()>0 ) {
			dy -= playerSpeed;
		}
		if (game.isKeyPressed(KeyEvent.VK_A) && player.getLabel().getX()>0) {
			dx -= playerSpeed;
		}
		if (game.isKeyPressed(KeyEvent.VK_S) && y < game.getHeight() - player.getLabel().getHeight()) {
			dy += playerSpeed;
		}
		if (game.isKeyPressed(KeyEvent.VK_D) &&  x < game.getWidth() - player.getLabel().getWidth()) {
			dx += playerSpeed;
		}
		// Adjust for diagonal movement
		if ((game.isKeyPressed(KeyEvent.VK_W) || game.isKeyPressed(KeyEvent.VK_S))
				&& (game.isKeyPressed(KeyEvent.VK_A) || game.isKeyPressed(KeyEvent.VK_D))) {
			double diagonalSpeed = playerSpeed / Math.sqrt(2);
			dx = (int) (diagonalSpeed * Math.signum(dx));
			dy = (int) (diagonalSpeed * Math.signum(dy));
		}
		

	    Point mousePosition = game.getMousePosition();
	    if (mousePosition != null) {
	        int mouseX = mousePosition.x;
	        int mouseY = mousePosition.y;
	        
	        // Calculate the direction towards the mouse position
	        dx = mouseX - (x + player.getLabel().getWidth() / 2);
	        dy = mouseY - (y + player.getLabel().getHeight() / 2);
	        
	        // Normalize the direction vector
	        double magnitude = Math.sqrt(dx * dx + dy * dy);
	        dx = (int) (dx / magnitude * playerSpeed);
	        dy = (int) (dy / magnitude * playerSpeed);
	    }
		


		playerAnimation();

		player.getLabel().setLocation(x + dx, y + dy);
		checkPlayerCollision(game);
	}

	private void playerAnimation() {
		if(untouchable) {
			String playerImgName;
			player.getLabel().setVisible(true);
			if (counter % 2 == 0)
				playerImgName = "/resources/icons/playerIcons/player2.png";
			else
				playerImgName = "/resources/icons/playerIcons/player1.png";
			counter++;
			iconPath = this.getClass().getResource(playerImgName);
			icon = new ImageIcon(iconPath);
			player.getLabel().setIcon(icon);
		}else {
			String playerImgName;
			player.getLabel().setVisible(true);
			if (counter % 2 == 0)
				playerImgName = "/resources/icons/playerIcons/player.png";
			else
				playerImgName = "/resources/icons/playerIcons/player1.png";
			counter++;
			iconPath = this.getClass().getResource(playerImgName);
			icon = new ImageIcon(iconPath);
			player.getLabel().setIcon(icon);
		}

	}

	private void checkPlayerCollision(Game game) {
		Iterator<Enemy> enemyIterator = game.getEntityManager().enemyList.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();
			JLabel enemyLabel = enemy.getLabel();
			

			Rectangle playerBounds = player.getLabel().getBounds();
			Rectangle enemyBounds = enemyLabel.getBounds();
			if (playerBounds.intersects(enemyBounds) && !untouchable) {
				player.decreaseHP();
				game.getSoundManager().damageTaken();
				game.getStatusBarManager().updateLife(player.getHP());
				Thread untouchablePlayer = new Thread(() -> {
					untouchable = true;
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					untouchable = false;
				});
				untouchablePlayer.start();

				if (player.getHP() <= 0) {
					game.handleGameOver();
				}
			}
		}
	}

	public void enemyLoop(Game game) {
		moveEnemies(game);
		spawnEnemy(game, getSpawnedEnemyCounter());
	}
	private void initialiseEnemies(Game game, int count) {
	    System.out.println("initialiseEnemies is running");
	    creatingEnemies = true;
	    Thread createEnemies = new Thread(() -> {
	        System.out.println("createEnemies thread is running");
	        if (game.reset || game.gameOver || game.isResume())
	            return;

	        Random random = new Random();
	        LinkedList<Integer> availableXValues = new LinkedList<>(); // Queue for available x values
	        int z = random.nextInt(count);
	        int z1= random.nextInt(count);
	        int z2 = random.nextInt(count);

	        for (int i = 0; i < game.getWidth() - 60; i += 60) {
	            availableXValues.offer(i); // Add all possible x values to the queue
	        }

	        for (int i = 0; i < count; i++) {
	            if (game.reset || game.gameOver || game.isResume())
	                break;
	            System.out.println("new enemies are spawning");

	            if (z == i || z1 == i || z2 ==i) {
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException e) {
	                    throw new RuntimeException(e);
	                }
	                for (int j = 0; j < game.getWidth() - 60; j += 60) {
	    	            availableXValues.offer(j); // Add all possible x values to the queue
	    	        }
	            }

	            int x;
	            int y = -50; // Starting position above the screen

	            if (availableXValues.isEmpty()) {
	                System.out.println("No available x values. Breaking out of enemy creation loop.");
	                break;
	            }

	            x = availableXValues.poll(); // Take the first available x value from the queue
	            createEnemyLabel(game, x, y);
	        }
	        game.getBulletManager().startEnemyBulletTimer(game);
	        creatingEnemies = false;
	    });

	    createEnemies.start();
	}

	

	private void createEnemyLabel(Game game, int x, int y) {
		String enemyOneImgName = "/resources/icons/enemyIcons/enemy1.png";
		iconPath = getClass().getResource(enemyOneImgName);
		if (iconPath == null) {
			System.out.println("Failed to load the enemy image.");
			return;
		}
		icon = new ImageIcon(iconPath);
		localLabel = new JLabel(icon);
		localLabel.setBounds(x, y, 60, 60);
		game.enemyLabel = localLabel;
		Enemy enemy = new Enemy(game.enemyLabel, enemyHP, enemySpeed); //game'de enemyLabel olmasi sacma
		enemyList.add(enemy);
		game.layeredPane.add(game.enemyLabel);
		
		enemyAnimation(game, localLabel);
	}
	
	private void enemyAnimation(Game game, JLabel enemy) {
		Thread animationThread = new Thread(()->{
			if(game.reset || game.gameOver || game.isResume())return;
			
			int counterEnemyAnimation = 0;
			while (enemy.isVisible()){
				if(game.reset || game.gameOver || game.isResume())return;
		    	String enemyImageName = null;
		    	
		      	switch (counterEnemyAnimation % 4) {
					case 0:
						enemyImageName = "/resources/icons/enemyIcons/enemy0.png";
						break;
					case 1:
						enemyImageName = "/resources/icons/enemyIcons/enemy1.png";
						break;
					case 2:
						enemyImageName = "/resources/icons/enemyIcons/enemy2.png";
						break;
					case 3:
						enemyImageName = "/resources/icons/enemyIcons/enemy3.png";
						break;
					}
		      	URL iconPath= getClass().getResource(enemyImageName);
		      	if (iconPath != null) {
		      		ImageIcon icon = new ImageIcon(iconPath);
		            enemy.setIcon(icon);
		        }else System.out.println("can't uploaded the enemy png");
		        counterEnemyAnimation++;
		        try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		animationThread.start();
	}
	
	public void deletePlayer(Game game) {
		game.remove(player.getLabel());
	}
	
	public void deleteAllEnemies(Game game) {
		Iterator<Enemy> iterator = enemyList.iterator();
		while (iterator.hasNext()) {
			Enemy enemy = iterator.next();
			JLabel enemyLabel = enemy.getLabel();
			game.remove(enemyLabel);
			game.layeredPane.remove(enemyLabel);
			iterator.remove();
		}
		game.repaint();
		game.layeredPane.repaint();
	}
	

	private void moveEnemies(Game game) { 
		Iterator<Enemy> iterator = enemyList.iterator();
		List<JLabel> labelsToRemove = new ArrayList<>();
		
		// = "/icons/playerIcons/player.png";
		while (iterator.hasNext()) {
			if(game.isResume()) continue;
			try {
				Enemy enemy = iterator.next();
				JLabel enemyLabel = enemy.getLabel();
				
				int x = enemyLabel.getX();
				int y = enemyLabel.getY();
				int dy = enemy.getSpeed();

				if (x >= game.getWidth() - enemyLabel.getWidth())
					horizontalMoveEnemy = -1;
				if (x <= 0)
					horizontalMoveEnemy = 1;
				x += horizontalMoveEnemy;
				enemyLabel.setLocation(x, y + dy);

				if (y > game.getHeight() || !enemyLabel.isVisible()) {
					// Enemy has reached the bottom of the screen or is not visible, mark it for
					// removal
					iterator.remove();
					labelsToRemove.add(enemyLabel);
				}
			}catch(ConcurrentModificationException e) {
				System.out.println("exception handled");
			}

		}
		// Remove the marked labels from the container
		for (JLabel label : labelsToRemove) {
			game.remove(label);
			game.layeredPane.remove(label);
		}
		game.layeredPane.repaint();
		game.repaint(); // Repaint the container to update the UI
	}
	
	

	public void spawnEnemy(Game game, int count) {
		if (game.getEntityManager().enemyList.isEmpty() && !creatingEnemies) initialiseEnemies(game, count);
		spawnerUpdater(game);
	}

	private void spawnerUpdater(Game game) {
		
		if (game.getStopWatch() == game.getLevelUpTimes()) {
			setSpawnedEnemyCounter(getSpawnedEnemyCounter() + 3);
			if(spawnedEnemyCounter>=20) spawnedEnemyCounter= 3;
			game.setLevelUpTimes(game.getLevelUpTimes() + 10);
			game.getStatusBarManager().updateLevel();
		}
	}

	public int getSpawnedEnemyCounter() {
		return spawnedEnemyCounter;
	}

	public void setSpawnedEnemyCounter(int spawnedEnemyCounter) {
		this.spawnedEnemyCounter = spawnedEnemyCounter;
	}
}
