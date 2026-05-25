package GamePack;

import java.awt.*;
import java.util.Random;


public class Ball {
	public int x;
    public int y;
    public double xVelocity;
    public double yVelocity;
    public int diameter;
    private final int MAX_SPEED = 19;
    private final double ACCELERATION = 0.75;
    Random random;
    
    
    public Ball(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        Random random = new Random();
        int randomNumber1 = random.nextBoolean() ? 1 : -1;
        int randomNumber2 = random.nextBoolean() ? 1 : -1;
        
        xVelocity = 6.0 * randomNumber1;
        yVelocity = 6.0 * randomNumber2;
    }
    
    public void move() {
    	x += xVelocity; y += yVelocity; 
    	}
    public void draw(Graphics g) { 
    	g.setColor(Color.white);
    	g.fillOval(x, y, diameter, diameter); }
    public Rectangle getBounds() { return new Rectangle(x, y, diameter, diameter); }
    public void reset() {
    	this.x = 400;
    	this.y = 300;
    	this.diameter = 20;
    	
    	Random random = new Random();
    	int randomNumber1 = random.nextBoolean() ? 1 : -1;
    int randomNumber2 = random.nextBoolean() ? 1 : -1;
       
    xVelocity = 6.0 * randomNumber1;
    yVelocity = 6.0 * randomNumber2;
       
    }
    public void increaseSpeed() {
        if (Math.abs(xVelocity) < MAX_SPEED) {
            xVelocity = xVelocity < 0 ? xVelocity - ACCELERATION : xVelocity + ACCELERATION;
            yVelocity = yVelocity < 0 ? yVelocity - ACCELERATION : yVelocity + ACCELERATION;
            if (xVelocity == 0) {
                xVelocity = (xVelocity < 0) ? -1 : 1;
            }
        }
    }
}
