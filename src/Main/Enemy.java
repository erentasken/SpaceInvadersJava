package Main;
import javax.swing.*;

public class Enemy { //enemy
    private final JLabel label;
    private int hp;
    private final int speed;

    public Enemy(JLabel label, int hp, int speed) {
        this.label = label;
        this.hp = hp;
        this.speed = speed;
    }

    public void handleEnemyDestroyed(Game game) {
        label.setVisible(false); // it will be removed in moveEnemies. It is indicator for removing.
        game.score++;
        game.statusBarManager.updateScore(game.score);
    }

    public JLabel getLabel() {
        return label;
    }

    public int getHP() {
        return hp;
    }

    public int getSpeed() {
        return speed;
    }

    public void decreaseHP() {
        hp--;
    }
}
