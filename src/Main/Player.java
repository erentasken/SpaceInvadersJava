package Main;
import javax.swing.*;


public class Player { //enemy
    private final JLabel label;
    private int hp;
    private final int speed;

    public Player(JLabel label, int hp, int speed) {
        this.label = label;
        this.hp = hp;
        this.speed = speed;
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
