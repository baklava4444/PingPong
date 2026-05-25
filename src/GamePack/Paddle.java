package GamePack;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Paddle {
    public int x, y, width, height, yVelocity;
    
    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void keyPressed(KeyEvent e, int upKey, int downKey) {
	     if (e.getKeyCode() == upKey) {
	         yVelocity = -12; // Move Up (Negative Y goes towards the top of the screen)
	     }
	     if (e.getKeyCode() == downKey) {
	         yVelocity = 12;  // Move Down
	     }
	 }

    public void keyReleased(KeyEvent e, int upKey, int downKey) {
	     if (e.getKeyCode() == upKey || e.getKeyCode() == downKey) {
	         yVelocity = 0;
	     }
	 }
    public void move() {
        y += yVelocity;
        
        if (y <= 0) y = 0;
        
        if (y >= 500) y = 500; 
    }
    public void draw(Graphics g, Color c) {
    	g.setColor(c);
    	g.fillRect(x, y, width, height); }
    public Rectangle getBounds() { return new Rectangle(this.x, this.y, this.width, this.height); }
    
}