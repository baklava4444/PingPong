package GamePack;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel{
    Ball ball;
    Paddle player1;
    Paddle player2;
    Timer timer;
    Score score;
    int player2Score = 0;
    int player1Score = 0;
    Boolean isPaused = true;
    private JButton btnReset, btnHost, btnJoin, btnResults;
    Boolean GAME_OVER = false;
    private NetworkManager netManager;
    private boolean isMultiplayer = false;

    
    public GamePanel() {
    	double PanelBounds = this.getBounds().width;
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.addKeyListener(new MyKeyAdapter());
        this.setFocusable(true);
        
        //button reset
        btnReset = new JButton("RESET SCORE");
        styleButton(btnReset, new Color(0, 255, 204));
        btnReset.setBounds(275, 280, 250, 50);     
        btnReset.addActionListener(e -> {
        	player2Score = 0;
        	player1Score = 0;
            ball.reset();
            
            toggleButtons(false);
            isPaused = false;
            this.requestFocusInWindow(); // Give keyboard focus back to the game!
        });
        
        //button host
        btnHost = new JButton("HOST");
        styleButton(btnHost, new Color(153, 51, 255)); // Purple glow
        btnHost.setBounds(275, 360, 115, 50);
        btnHost.addActionListener(e -> {
        	netManager.startHost(5000);
            this.requestFocusInWindow();
        });
        
        //button join
        btnJoin = new JButton("JOIN");
        styleButton(btnJoin, new Color(255, 102, 0));  // Orange glow
        btnJoin.setBounds(410, 360, 115, 50);
        btnJoin.addActionListener(e -> {
        String targetIP = JOptionPane.showInputDialog(
                this, 
                "Enter the Host's IP Address:\n", 
                "Connect to Multiplayer Game", 
                JOptionPane.QUESTION_MESSAGE
            );

            // 2. Handle the user's input safely
            if (targetIP != null && !targetIP.trim().isEmpty()) {
                System.out.println("Attempting to connect to IP: " + targetIP.trim());
                
                // 3. Pass the entered IP into your network manager on port 5000
                netManager.startClient(targetIP.trim(), 5000); 
            } else {
                System.out.println("Join canceled or empty IP entered.");
            }
            this.requestFocusInWindow();
        });
        
        //button results
        btnResults = new JButton("HISTORY");
        styleButton(btnResults, new Color(255, 204, 0)); // Neon Yellow outline glow
        btnResults.setBounds(275, 440, 250, 50);          // Placed right underneath Host/Join
        btnResults.addActionListener(e -> {
            showLeaderboardPopup();
            this.requestFocusInWindow(); // Maintain control layout mapping
        });
        
        netManager = new NetworkManager(new NetworkManager.NetworkListener() {
            @Override
            public void onConnected() {
                isMultiplayer = true;
                System.out.println("Game Connected! Let's play.");
                // If Host, start your gameTimer loop here!
            }

            @Override
            public void onDataReceived(String data) {
                handleNetworkData(data);
            }
        });
       
        this.add(btnResults);
        this.add(btnReset);
        this.add(btnHost);
        this.add(btnJoin);
        this.add(btnResults);
        
        // Spawn our entities
        ball = new Ball(400, 300, 20);
        player1 = new Paddle(10, 250, 20, 100);
        player2 = new Paddle(770, 250, 20, 100);
        score = new Score(800,600);
        
        // Start Game Loop Timer
        timer = new Timer(8, e -> {
            if(!isPaused) {
            	checkWinCondition();
            	
            	ball.move();
            	player1.move();
            	player2.move();
            	score.write(player1Score,player2Score);
              	
            	bounce(player1 , player2);
            	checkOutOfBounds();
            	if(isMultiplayer && netManager.isHost()) {
            			String gamestate = ball.x + "," + ball.y + "," + player1.y + ","
            					+ player1Score + "," + player2Score + "," + isPaused;
            			netManager.sendData(gamestate);
            		}     
            	else if(isMultiplayer && !netManager.isHost()) {
	            		String gamestate = String.valueOf(player2.y);
	            		netManager.sendData(gamestate);	
            		}
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!isPaused) {
        	ball.draw(g);    // Tell the ball to draw itself
        	player1.draw(g,Color.red); // Tell player 1 to draw itself
        	player2.draw(g,Color.blue); // Tell player 2 to draw itself
        	score.draw(g);        	
        }else {
        	if(GAME_OVER) {
        		g.setColor(new Color(255, 0, 102)); // Neon Pink/Red for Game Over
                g.setFont(new Font("Consolas", Font.BOLD, 65));
                g.drawString("GAME OVER", 235, 150);
                
                // Determine and announce the winner
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 24));
                String winner = (score.player1 >= 10) ? "PLAYER 1 WINS!" : "PLAYER 2 WINS!";
                g.drawString(winner, 300, 210);
                
                g.setFont(new Font("Consolas", Font.PLAIN, 16));
                g.setColor(new Color(150, 150, 150));
                g.drawString("Click RESET SCORE to play again", 275, 240);
        	}else {
        		g.setColor(new Color(10, 15, 30, 235)); 
        		g.fillRect(0, 0, 800, 600);
        		
        		g.setColor(new Color(0, 180, 255, 100));
        		g.drawRect(20, 20, 760, 560);
        		
        		g.setColor(Color.WHITE);
        		g.setFont(new Font("Consolas", Font.BOLD, 55));
        		g.drawString("GAME PAUSED", 240, 180);
        		
        		g.setFont(new Font("Consolas", Font.ITALIC, 18));
        		g.setColor(new Color(200, 200, 200));
        		g.drawString("Press SPACEBAR to Resume", 275, 220);     		
        	}
        }
    }
    
    public void checkOutOfBounds() {
    	Rectangle ballBounds = ball.getBounds();
    	
    	// Example: Player 2 scores if ball exits left side
    	if (ballBounds.x + ballBounds.width < 0) {
    		player2Score++;
    		ball.reset();
    		//return true;
    	}
    	// Example: Player 1 scores if ball exits right side
    	else if (ballBounds.x > this.getWidth()) {
    		player1Score++;
    		ball.reset();
    		//return true;
    	}
    	//return false;
    }
    
    public Boolean isHit(Paddle p) {
    	if(ball.getBounds().intersects(p.getBounds()))
    		return true;
    	else {
			return false;
		}
    }
    
    public void bounce(Paddle p1, Paddle p2) {
    	if(isHit(p1)) {
    		ball.xVelocity = Math.abs(ball.xVelocity); 
            ball.x = p1.x + p1.width; 
            ball.increaseSpeed();
            System.out.println("velocity :" + ball.xVelocity + " - " + ball.yVelocity);
    	}if(isHit(p2)){
    		ball.xVelocity = -Math.abs(ball.xVelocity);                   
            ball.x = p2.x - ball.diameter;
            ball.increaseSpeed();
            System.out.println("velocity :" + ball.xVelocity + " - " + ball.yVelocity);
    	}
    	else if(ball.getBounds().y <= 0 || ball.getBounds().y >= this.getHeight()){
    		ball.yVelocity *= -1;
    	}
    }
    
    private void styleButton(JButton button, Color glowColor) {
        button.setFont(new Font("Consolas", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(20, 40, 80)); // Dark slate blue background
        button.setFocusPainted(false);                // Removes the ugly default text border
        button.setBorder(BorderFactory.createLineBorder(glowColor, 2)); // Custom neon border
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    // Quickly shows or hides all buttons at once
    private void toggleButtons(boolean visible) {
        btnReset.setVisible(visible);
        btnHost.setVisible(visible);
        btnJoin.setVisible(visible);
        btnResults.setVisible(visible);
    }
    
    private void checkWinCondition() {
        if (score.player1 >= 10 || score.player2 >= 10) {
            GAME_OVER = true;
            isPaused = true;
            toggleButtons(true);
             
            // Determine winner name and format score string
            String winnerName = (score.player1 >= 10) ? "BLUE" : "RED"; 
            String finalScoreText = score.player1 + "-" + score.player2;
            
            // SAVE TO DATABASE
            DatabaseManager.saveMatchResult(winnerName, finalScoreText);
        }
    }
    
    private void showLeaderboardPopup() {
        List<String> history = DatabaseManager.getLast10Results();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAST 10 MATCHES ===\n\n");
        
        if (history.isEmpty()) {
            sb.append("No matches recorded yet.\nPlay a game to 10 points!");
        } else {
            for (String record : history) {
                sb.append(record).append("\n");
            }
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 204, 0), 5));

        JOptionPane.showMessageDialog(this, scrollPane, "Match History", JOptionPane.PLAIN_MESSAGE);
    }
    
public class MyKeyAdapter extends KeyAdapter {
    	
    	public void keyPressed(KeyEvent e) {
    		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
    			isPaused = !isPaused;    			
    			toggleButtons(isPaused); 
    			GAME_OVER = false;
    		    }
    		else {
    			player1.keyPressed(e, KeyEvent.VK_W, KeyEvent.VK_S);
    			player2.keyPressed(e, KeyEvent.VK_UP, KeyEvent.VK_DOWN);    			
    		}
    		
    	}
    	
    	public void keyReleased(KeyEvent e) {
    		if(!isPaused) {
    			player1.keyReleased(e, KeyEvent.VK_W, KeyEvent.VK_S);
    			player2.keyReleased(e, KeyEvent.VK_UP, KeyEvent.VK_DOWN);    			
    			}
    		}
    }
	
	private void handleNetworkData(String data) {
	    String[] tokens = data.split(",");
	
	    if (netManager.isHost()) {
	        // The Host received data from Client. The client only sends its paddle Y position.
	        player2.y = Integer.parseInt(tokens[0]);
	    } else {
	        // The Client received data from Host. Update everything to match the host perfectly!
	        ball.x = Integer.parseInt(tokens[0]);
	        ball.y = Integer.parseInt(tokens[1]);
	        player1.y = Integer.parseInt(tokens[2]);
	        player1Score = Integer.parseInt(tokens[3]);
	        player2Score = Integer.parseInt(tokens[4]);
	        isPaused = Boolean.parseBoolean(tokens[5]);
	        toggleButtons(isPaused);
	    }
	    
	    repaint(); // Force UI refresh to show the new positions!
	}
}
