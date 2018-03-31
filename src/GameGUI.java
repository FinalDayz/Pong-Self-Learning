import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameGUI extends JPanel{
	Game pongGame = null;
	double FPS = 60;
	Color gameColor = Color.BLACK;
	
	JFrame gameFrame;
	private int msFPS = 1000/50;
	private long prefms = 0;
	
	double[] fitness = new double[Setup.playerCount];
	
	boolean playerRight = false;
	boolean playerLeft = false;
		
	int generationTime = 0;
	int frameTicks = 0;
	
	double gameState = 0;
	int gameNumber = 0;
	int totalGameNumber = 0;
	int network = 0;
	int totalNetworks = 0;
	
	String info1 = "";
	String info2 = "";
	
	GameGUI(Game pongGame){
		this.pongGame = pongGame;
		
		createPanel();
		new Thread(new Runnable(){
			public void run() {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				beginTimer();
			}
				
		}).start();
		createActionListeners();
		
	}
	
	private void createActionListeners() {
		gameFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 39 || e.getKeyCode() == 68) {
					playerRight = true;
				} else if (e.getKeyCode() == 37 || e.getKeyCode() == 65) {
					playerLeft = true;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == 39 || e.getKeyCode() == 68) {
					playerRight = false;
				} else if (e.getKeyCode() == 37 || e.getKeyCode() == 65) {
					playerLeft = false;
				}
			}

		});
		
	}

	void displayGame(Game pongGame){
		this.pongGame = pongGame;
	}
	
	void displayGame(Game pongGame, Color gameColor){
		this.pongGame = pongGame;
		this.gameColor = gameColor;
	}
	
	void setFPS(double FPS){
		this.FPS = FPS;
		msFPS = (int) Math.round(1000 / FPS);
	}
	
	double getFPS() {
		return Math.round(1000/msFPS);
	}
	
	void setFitness(double fit1, double fit2){
		fitness[0] = fit1;
		fitness[1] = fit2;
		
	}
	
	private void createPanel(){
		gameFrame = new JFrame("Pong game");
		gameFrame.setVisible(true);
		gameFrame.setBounds(200, 10, (int) pongGame.getWidth() + 18, (int) pongGame.getHeight() + 47);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.add(this);
	
	}
	
	private void beginTimer(){
				
		while(true){
			if(pongGame != null){
				repaint();
				if(frameTicks % 5 == 2){
					Console.frame.repaint();
				}
			}
			try {
				prefms = System.currentTimeMillis();
				Thread.sleep(msFPS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			      
		}
		
	}
	
	
    @Override
    public void paint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        super.paint(g1);
        g.setColor(gameColor);
        frameTicks++;

        if(NetworkControll.fast && !Setup.playerPlay){
        	msFPS = 1000 / 25;
        }else{
        	msFPS = 1000 / 60;
        }

        RenderingHints hints = new RenderingHints(
        	    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        	);
        	g.setRenderingHints(hints);
        
        	g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString(info1, 20, 50); 
        g.drawString(info2, 20, (int) pongGame.getHeight() - 80);
        	
        int playerHeight = tint(pongGame.playerHeight);
        int playerWidth = tint(pongGame.playerWidth);
        double playerYDistance = pongGame.playerYDistance;
        double ballWidth = pongGame.ballWidth;
        
        g.fillRect(tint(pongGame.getPosition1()), tint(playerYDistance), playerWidth, playerHeight);
        
        g.fillRect(tint(pongGame.getPosition2()), tint(pongGame.getHeight() - playerYDistance - playerHeight), playerWidth, playerHeight);
        g.fillOval(tint(pongGame.getBallX() - pongGame.ballWidth/2), tint(pongGame.getBallY() - pongGame.ballWidth/2), tint(ballWidth), tint(ballWidth));
        
        g.setFont(new Font("Arial", Font.PLAIN, 17));
        
        g.drawString("Fitness: "+Math.round(fitness[0] *100)/100.0, 10, (int)Math.round(pongGame.getHeight()/2-40));
        g.drawString("Generation: "+NetworkControll.generation, 10, (int)Math.round(pongGame.getHeight()/2));
        g.drawString("Fitness: "+Math.round(fitness[1] *100)/100.0, 10, (int)Math.round(pongGame.getHeight()/2+40));
        
        g.setColor(Color.BLACK);
     //  g.setStroke(new BasicStroke(1));
        g.drawRect(tint(pongGame.getWidth() - 30), tint(pongGame.getHeight() / 2 - 250), 10, 500);
        g.setColor(new Color(100, 200, 100));
        g.fillRect(tint(pongGame.getWidth() - 29), tint(pongGame.getHeight() / 2 - 249), 9, tint(498 * (network * 1.0 / totalNetworks)));
        g.setColor(Color.BLACK);
       // g.drawString("game:"+gameNumber+"/"+totalGameNumber, tint(pongGame.getWidth() - 120), tint(pongGame.getHeight() / 2 + 10));
       // g.drawString("Network:"+network+"/"+totalNetworks, tint(pongGame.getWidth() - 130), tint(pongGame.getHeight() / 2 + 30));
    }
    
    int tint(double d){
    	return (int)Math.round(d);
    }
    
    void println(Object o){
    	System.out.println(o);
    }
}
