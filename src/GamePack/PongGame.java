package GamePack;

import javax.swing.JFrame;

public class PongGame extends JFrame {
    public PongGame() {
        this.add(new GamePanel()); // Adds the engine to the window
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new PongGame();
    }
}
