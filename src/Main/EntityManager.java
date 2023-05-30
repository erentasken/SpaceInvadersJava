package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityManager {
	public Player player;
	private int playerSpeed = 5;
	private int playerHP = 3; // Player's hit points

	private int enemyHP = 5; // Enemy's hit points
	private int enemySpeed = 1; // Enemy's movement speed

	private boolean creatingEnemies;
	private Icon icon;
	private URL iconPath;
	private JLabel localLabel;
	private int spawnRate;
	int counter = 0;
	boolean untouchable = false;
	int horizontalMoveEnemy = 1;
	
	public List<Enemy> enemyList;

	public EntityManager() {
		enemyList = new ArrayList<Enemy>();
	}

	public void createPlayerLabel(Game game) {
		try {
			String playerImgName = "/icons/playerIcons/player.png";
			iconPath = game.getClass().getResource(playerImgName);
			icon = new ImageIcon(iconPath);
			localLabel = new JLabel();
			localLabel.setBounds(game.getWidth() / 2 - 50, game.getHeight() - 160, icon.getIconWidth(), icon.getIconHeight());
			localLabel.setIcon(icon);
			localLabel.setOpaque(false);

			game.add(localLabel);
			// game.layeredPane.add(localLabel);
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

		playerAnimation();

		player.getLabel().setLocation(x + dx, y + dy);
		checkPlayerCollision(game);
	}

	private void playerAnimation() {
		String playerImgName;
		if (counter % 2 == 0)
			playerImgName = "/icons/playerIcons/player.png";
		else
			playerImgName = "/icons/playerIcons/player1.png";
		counter++;
		iconPath = this.getClass().getResource(playerImgName);
		icon = new ImageIcon(iconPath);
		player.getLabel().setIcon(icon);
	}

	private void checkPlayerCollision(Game game) {
		Iterator<Enemy> enemyIterator = game.entityManager.enemyList.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();
			JLabel enemyLabel = enemy.getLabel();
			if (!enemyLabel.isVisible()) {
				continue; // Skip collision detection if enemy label is not visible
			}

			Rectangle playerBounds = player.getLabel().getBounds();
			// Rectangle playerBounds = game.playerLabel.getBounds();
			Rectangle enemyBounds = enemyLabel.getBounds();
			if (playerBounds.intersects(enemyBounds) && !untouchable) {
				player.decreaseHP();
				game.statusBarManager.updateLife(player.getHP());
				Thread untouchablePlayer = new Thread(() -> {
					untouchable = true;
					try {
						Thread.sleep(2000);
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
		spawnEnemy(game, spawnRate);
	}

	private void initialiseEnemies(Game game, int count) { // original method
		creatingEnemies = true;
		Thread createEnemies = new Thread(() -> {
			if(game.resume) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(game.reset || game.gameOver) return;
			Random random = new Random();
			for (int i = 0; i < count; i++) {
				if (game.reset || game.gameOver) {
					System.out.println("hello");
					break;
				}
					
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				int x = random.nextInt(game.getWidth() - 50); // Random x position
				int y = -50; // Starting position above the screen
				createEnemyLabel(game, x, y);
			}
			game.bulletManager.startEnemyBulletTimer(game);
			creatingEnemies = false;
		});
		createEnemies.start();
	}

	private void createEnemyLabel(Game game, int x, int y) {
		String enemyOneImgName = "/icons/enemyIcons/enemy1.png";
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
			if(game.reset || game.gameOver)return;
			
			int counterEnemyAnimation = 0;
			while (enemy.isVisible()){
		    	String enemyImageName = null;
		    	URL iconPath;
		      	switch (counterEnemyAnimation % 4) {
					case 0:
						enemyImageName = "/icons/enemyIcons/enemy0.png";
						break;
					case 1:
						enemyImageName = "/icons/enemyIcons/enemy1.png";
						break;
					case 2:
						enemyImageName = "/icons/enemyIcons/enemy2.png";
						break;
					case 3:
						enemyImageName = "/icons/enemyIcons/enemy3.png";
						break;
					}
		      	iconPath = getClass().getResource(enemyImageName);
		      	if (iconPath != null) {
		      		ImageIcon icon = new ImageIcon(iconPath);
		            enemy.setIcon(icon);
		        }else System.out.println("can't uploaded the png");
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
		// = "/icons/playerIcons/player.png";
		while (iterator.hasNext()) {
			Enemy enemy = iterator.next();
			JLabel enemyLabel = enemy.getLabel();
			game.remove(enemyLabel);
			game.layeredPane.remove(enemyLabel);
			game.repaint();
			game.layeredPane.repaint();
			iterator.remove();
		}

	}

	private void moveEnemies(Game game) {
		Iterator<Enemy> iterator = enemyList.iterator();
		List<JLabel> labelsToRemove = new ArrayList<>();
		
		// = "/icons/playerIcons/player.png";
		while (iterator.hasNext()) {
			if(game.resume) continue;
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
			//if(!labelsToRemove.contains(enemyLabel)) enemyAnimation(enemyLabel, counterEnemyAnimation++);
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
		if (game.entityManager.enemyList.isEmpty() && !creatingEnemies) initialiseEnemies(game, count);
		spawnerUpdater(game);
	}

	private void spawnerUpdater(Game game) {
		if (game.stopWatch == game.levelUpTimes) {
			spawnRate += game.stopWatch / 1.5;
			game.levelUpTimes *= 3.0 / 2;
		}
	}
}
