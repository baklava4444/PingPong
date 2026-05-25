package GamePack;

import java.awt.*;


public class Score {
    static int GAME_WIDTH;
    static int GAME_HEIGHT;
    int player1;
    int player2;

    Score(int width, int height) {
        Score.GAME_WIDTH = width;
        Score.GAME_HEIGHT = height;
    }
    
    public void write(int p1 , int p2) {
    	this.player1 = p1;
    	this.player2 = p2;
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.PLAIN, 60));
        
        g.drawString(String.valueOf(player1 / 10) + String.valueOf(player1 % 10), (GAME_WIDTH / 2) - 85, 50);
        g.drawString(String.valueOf(player2 / 10) + String.valueOf(player2 % 10), (GAME_WIDTH / 2) + 20, 50);
        
    }
}