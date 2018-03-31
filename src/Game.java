import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Game {
	
	private double position1, position2, width, height, ballX, ballY, ballSpeedX, ballSpeedY, movingSpeed = 0;
	double ballWidth = 31;
	double playerWidth = 500;
	
	double playerYDistance = 15;
	double playerHeight = 10;
	private boolean gameOver = false;
	//0 = random, 1 = to player 1, 2 = to player 2
	 int ballDirection = 0;
	
	private boolean hasCollide = false;
	double maximumBallSpeed = 13;
	
	static Random rand = new Random(System.currentTimeMillis());
	
	static int randomIndex = 0;
	static long randSeed = 0;
	
	boolean invertX = false;
	
	Game(double width, double height, double movingSpeed){
		setup(width, height, movingSpeed,  playerWidth, ballWidth);
	}
	
	Game(double width, double height, double movingSpeed, double playerWidth){
		setup(width, height, movingSpeed, playerWidth, ballWidth);
	}
	
	Game(double width, double height, double movingSpeed, double playerWidth, double ballWidth){
		setup(width, height, movingSpeed, playerWidth, ballWidth);
	}
	
	
	//reset and setup the variables
	private void setup(double width, double height, double movingSpeed, double playerWidth, double ballWidth){
		this.width = width;
		this.height = height;
		
		this.movingSpeed = movingSpeed;
		
		ballX = width / 2;
		ballY = height / 2;
		
		ballSpeedX = 0;
		ballSpeedY = 0;
		
		this.playerWidth = playerWidth;
		this.ballWidth = ballWidth;
		
		position1 = width / 2 - playerWidth / 2;
		position2 = width / 2 - playerWidth / 2;
		resetGame();
	}
	
	void ballUp(){
		ballDirection = 1;
	}
	
	void ballDown(){
		ballDirection = 2;
	}
	
	void setMovingSpeed(double speed){
		movingSpeed = speed;
	}
	
	double getWidth(){
		return this.width;
	}
	
	double getHeight(){
		return this.height;
	}
	
	void println(Object o){
		System.out.println(o);
	}
	
	
	//reset everything and give the ball a random direction
	void resetGame(){
		ballX = width / 2;
		ballY = height / 2;
		
		position1 = width / 2 - playerWidth / 2;
		position2 = width / 2 - playerWidth / 2;
		
		ballSpeedX = random((int) ((maximumBallSpeed-3)*2)) / 2.0;
		//ballSpeedX = 5.5;

		ballSpeedY = (maximumBallSpeed-1) - ballSpeedX;
		
		if(random(2) == 2)
			ballSpeedX = -ballSpeedX;
		
		if(invertX)
			ballSpeedX = -ballSpeedX;
		
		if(ballDirection == 0 && random(2) == 2)
			ballSpeedY = - ballSpeedY;
		
		if(ballDirection == 1)
			ballSpeedY = -Math.abs(ballSpeedY);
		
		if(ballDirection == 2)
			ballSpeedY = Math.abs(ballSpeedY);
		
		if(ballSpeedY > 0) {
			ballY -= 300;
		} else {
			ballY += 300;
		}
		
		gameOver = false;
		hasCollide = false;
	}
	
	//random(4) = 1, 2, 3, 4
	//random(2) = 1,2
	public static int random(int to) {
		//randomIndex++;
		return (int) Math.floor(rand.nextDouble() * (to) + 1);
	}
	
	//calculate 1 game tick
	int gameTick(){
		//0 = normal, 1 = player1 has scored, 2 = player2 has scored, 3 = game is over and has to get reset
		//4 = player1 touched the ball, 5 = player2 touched the ball
		int gameStatus = 0;
		
		ballX += ballSpeedX;
		ballY += ballSpeedY;
		
		//only change direction when collide with a player
		if(ballSpeedY < 0 && 
				collideRect(new Rectangle2D.Double(ballX - ballWidth / 2, ballY - ballWidth / 2, ballWidth, ballWidth),
						new Rectangle2D.Double(position1, playerYDistance, playerWidth, playerHeight)
						)){
			hasCollide = true;
			ballSpeedY = Math.abs(ballSpeedY);
			gameStatus = 4;
			//calculate how much off the ball is from the center and give it a bit of an effect 
			ballEffect(position1);
		}
		
		if(ballSpeedY > 0 && 
				collideRect(new Rectangle2D.Double(ballX - ballWidth / 2, ballY - ballWidth / 2, ballWidth, ballWidth),
						new Rectangle2D.Double(position2, height - playerYDistance - playerHeight, playerWidth, playerHeight))){
			hasCollide = true;
			ballSpeedY = -Math.abs(ballSpeedY);
			gameStatus = 5;
			//calculate how much off the ball is from the center and give it a bit of an effect 
			ballEffect(position2);
			
		}
		

		//if they hit the walls, bounch back
		if(ballX + ballWidth  >= width){
			ballSpeedX = -Math.abs(ballSpeedX);
		}
	
		if(ballX - ballWidth <= 0){
			ballSpeedX = Math.abs(ballSpeedX);
		}
		
		//a player scored
		if(ballY - ballWidth >= height){
			gameStatus = 1;
			//if the game already finished
			if(gameOver)
				gameStatus = 3;
			gameOver = true;
		}
		
		if(ballY + ballWidth <= 0){
			gameStatus = 2;
			if(gameOver)
				gameStatus = 3;
			gameOver = true;
		}
		
		return gameStatus;
	}
	
	//if the player hits the ball, give the ball a bit of effect based on how close the ball is to the edge of the rectangle
	void ballEffect(double playerPos){
		double ballEffext = (ballX - (playerPos + playerWidth / 2)) / playerWidth;
	
		ballEffext = ballEffext * 8;

		
		int multipli = 1;
		if(ballSpeedY < 0)
			multipli = -1;
		
		ballSpeedX += ballEffext;
		if(ballSpeedX > (maximumBallSpeed - 2))
			ballSpeedX = (maximumBallSpeed - 2);
		if(ballSpeedX < -(maximumBallSpeed - 2))
			ballSpeedX = -(maximumBallSpeed - 2);
		ballSpeedY = ((maximumBallSpeed-0.5) - Math.abs(ballSpeedX)) * multipli;

	}
	
	void setSeed(long seed){
		rand.setSeed(seed);
		//randSeed = seed;
	}
	
	private boolean collideRect(Rectangle2D rect1, Rectangle2D rect2){
		return rect1.intersects(rect2);
	}
	
	String getRandInfo() {
		String seed = Math.round((randSeed / 100000000.0) - Math.floor(randSeed / 100000000.0) * 100000000) + "";
		return "("+seed+" i: "+randomIndex+")";
	}
	
	void moveRight1(){
		position1 += movingSpeed;
		if(position1 + playerWidth > width)
			position1 = width - playerWidth;
	}
	
	void moveRight2(){
		position2 += movingSpeed;
		if(position2 + playerWidth > width)
			position2 = width - playerWidth;
	}
	
	void moveLeft1(){
		position1 -= movingSpeed;
		if(position1 < 0)
			position1 = 0;
	}
	
	void moveLeft2(){
		position2 -= movingSpeed;
		if(position2 < 0)
			position2 = 0;
	}
	
	boolean hasCollide(){
		return this.hasCollide;
	}
	
	double getMaximumBallSpeed(){
		return maximumBallSpeed;
	}
	
	double getBallSpeedX(){
		return ballSpeedX;
	}
	
	double getBallSpeedY(){
		return ballSpeedY;
	}
	
	double getPosition1(){
		return position1;
	}
	
	double getPosition2(){
		return position2;
	}
	
	double getBallX(){
		return ballX;
	}
	
	double getBallY(){
		return ballY;
	}
	
	void setPosition1(double xPos){
		position1 = xPos;
	}
	void setPosition2(double xPos){
		position2 = xPos;
	}
	
}
