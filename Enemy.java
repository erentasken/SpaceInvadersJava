import javax.swing.*;

public class Enemy { //enemy
    private JLabel label;
    private int hp;
    private int speed;

    public Enemy(JLabel label, int hp, int speed) {
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
