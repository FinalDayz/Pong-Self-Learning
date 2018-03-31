import java.awt.Color;

public class NetworkLearn {
	boolean done = false;
	int matchTime = 0;
	int matches = 0;
	
	int currentTick = 0;
	Game currentGame;
	
	double gameWidth = Setup.gameWidth;
	double gameHeight = Setup.gameHeight;
	
	double fitness1 = 0;
	double fitness2 = 0;
	
	NetworkLearn(){
		matchTime = Setup.matchTime;
		matches = Setup.matches;
		
	}
	
	//complete one match with 2 neuralNetworks
	double[] getFitness(NeuralNetwork nn1, NeuralNetwork nn2, int threadNumber){
		double paddleWidth = Setup.paddleWidth;
		double ballWidth = 31;
		currentGame = new Game(gameWidth, gameHeight, Setup.speed, paddleWidth, ballWidth);
		boolean display = false;
		currentTick = 0;
		
		fitness1 = 0;
		fitness2 = 0;
		done = false;
		
		if(threadNumber == 0){
//			display = true;
//			Setup.GUI.displayGame(currentGame);
//			currentTick++;
		}
		
		long seed = System.currentTimeMillis();
		currentGame.setSeed(seed);
		currentGame.ballUp();
		currentGame.resetGame();

		for(int matchCount = 0; matchCount < matches; matchCount++){
			
			while(currentTick < matchTime){
				
				//Setup.GUI.pongGame = currentGame;
				//Setup.GUI.fitness = new double[]{fitness1, fitness2};
			//	Setup.GUI.gameState = currentTick * 1.0 / matchTime;
			//	Setup.GUI.gameNumber = matchCount;
			//	Setup.GUI.totalGameNumber = matches;
				
				//get all inputs and map it between 0 and 1
				double ballX = currentGame.getBallX() / gameWidth;
				double ballY = currentGame.getBallY() / gameHeight;
				
				double ballSpeedX = currentGame.getBallSpeedX()/ currentGame.getMaximumBallSpeed();
				double ballSpeedY = currentGame.getBallSpeedY() / currentGame.getMaximumBallSpeed();
				
				double p1 = (currentGame.getPosition1() + paddleWidth / 2) / gameWidth;
				double p2 = (currentGame.getPosition2() + paddleWidth / 2) / gameWidth;
				
				//             1              2              3              4                   5                      6
				//inputs = (Ball X pos), (Ball Y pos), (Ball speed X) (Ball speed Y), (Player 1 position X) , (Player 2 position x)
				nn1.setInput(1 - ballX, 1 - ballY, ballSpeedX, ballSpeedY, 1 - p1, 1 - p2);
				
				//invert input (1 - input) because the player is mirrored relative to the other player
				nn2.setInput(ballX, ballY, -ballSpeedX, -ballSpeedY, p2, p1);
				
				/**
				 	nn1.setInput(1 - ballX, 1 - ballY, ballSpeedX, ballSpeedY, 1 - p1, 1 - p2);
					nn2.setInput(ballX, ballY, -ballSpeedX, -ballSpeedY, p2, p1);
				 */
				
				
				
				//nn2.setInput(ballX, ballY, ballSpeedX, ballSpeedY, p1, p2);
				//get the output
				double[] output1 = nn1.getOutput();
				double[] output2 = nn2.getOutput();
				
				//if the output is < 0.5 , go to left, else go right
				
				if(output1[0] <= 0.5){
					currentGame.moveRight1();
				}else if(output1[0] > 0.5){
					currentGame.moveLeft1();
				}
				
				if(output2[0] >= 0.5){
					currentGame.moveRight2();
				}else if(output2[0] < 0.5){
					currentGame.moveLeft2();
				}


				int gameState = currentGame.gameTick();

				//System.out.println("Gamestate: "+gameState+" , thed number: "+threadNumber);
				
				
				//add or remove fitness 
				if(gameState == 1){
					fitness2 -= 8;
					if(currentGame.hasCollide()){
						fitness1 += 15;
					}
					
				}else if(gameState == 2){
					fitness1 -= 8;
					if(currentGame.hasCollide()){
						fitness2 += 15;
					}
				}
				
					
				if(gameState == 1 || gameState == 2 || gameState == 3){
					currentGame.resetGame();
					nn1.resetNetwork();
					nn2.resetNetwork();
				//	System.out.print(" "+currentGame.getBallSpeedY()+" "+currentGame.getBallSpeedX()+" "+currentGame.getRandInfo()+" | ");
				}
			
				if(!NetworkControll.fast)
					try {
						Thread.sleep(NetworkControll.speed);
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				currentTick++;
			}
			
		//	System.out.println("Match ended: fitness "+fitness1+" 2: "+fitness2);
			currentTick = 0;
			//switch ball spawn side

			if(currentGame.ballDirection == 2){
				//generate new random
				seed = System.currentTimeMillis();
				currentGame.setSeed(seed);
				
				currentGame.ballUp();
				currentGame.invertX = false;
				
			}else{
				currentGame.invertX = true;
				//set random to other match
				currentGame.setSeed(seed);
				currentGame.ballDown();
			}
			
			currentGame.resetGame();

		}
		//System.out.println("================");
		//System.out.println("ALL END");
		return new double[]{fitness1, fitness2};
	}
	
	void println(Object o){
		System.out.println(o);
	}
	
}

