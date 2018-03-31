import java.util.ArrayList;
import java.util.List;

public class Setup {
	
	static String version =  "0.53";
	
	//game width and height
	static double gameWidth = 1000;
	static double gameHeight = 900;
	
	//two players
	static int playerCount = 2;
	//paddle width
	static int paddleWidth = 150;	//150
	//speed of player
	static double speed = 10;
	
	//the number of previous parent networks used in the current generation
	static int previousNetworks = 3;
	//the number of game ticks one match has
	static int matchTime = 12000;    // 12000 ticks
	//the number of matches one game has
	static int matches = 2;       //2
	//how much percent of the brain mutates
	static double startMuatteRate = 5; // %
	
	//the number of networks per generation
	static int networkCount = 36;
	//the number of threads (should be divisible with the networkcount)
	static int threadCount = 4;
	//speeds:
	/**
	 
	 	1 thread : 5.6
	 	2 threads: 9.54
	 	3 threads: 12.05
		4 threads: 13.2	
		5 threads: 13.9
		
	 */
	
	//how many generations has to pass by to update the gui to the new network
	static int guiNetworkUpdate = 10;
	
	static double[][] networkMatrix = null;
	static NeuralNetwork[][] networks = new NeuralNetwork[playerCount][networkCount];
	static Game pongGame;
	static GameGUI GUI;
	static NetworkControll networkControll;
	
	static boolean playerPlay = false;
	static int playerSide = 1;
	
	static NeuralNetwork displayNetwork1;
	static NeuralNetwork displayNetwork2;

	static long frameCounter = 0;
	static double playerSpeed = 8;
	
	static boolean dontUpdate = false;
	
	static double updatePerSecondDisplay = 100;
	static boolean pauseOnScreenGame = false;
	
	public static void main(String[] arg){
		System.out.println("Arguments: \n0: threads(can speed things up, not to much!), \n1: networkCount(number of different networks per generation), \n2: mutateRate(rate the network will mutate in percent)\n");
		System.out.println("Standards: 0:"+threadCount+", 1:"+networkCount+", 2:"+startMuatteRate);
		System.out.println();
		
		if(arg.length > 0)
			threadCount = Integer.parseInt(arg[0]);
		
		if(arg.length > 1)
			networkCount = Integer.parseInt(arg[1]);
		
		if(arg.length > 2)
			startMuatteRate = Integer.parseInt(arg[2]);
		
		System.out.println("Starting, Java: "+System.getProperty("sun.arch.data.model")+" bit, with "+threadCount+" threads, "+networkCount+" networks, "+startMuatteRate+"% mutate rate");
		System.out.println();
		//check if everyting went OK
		if(setup())
			GUILoop();
		return;
		
		
	}
	
	
	static void GUILoop(){
		new Thread(new Runnable(){
			public void run() {
				displayNetwork1 = NetworkControll.parentNetwork;
				displayNetwork2 = NetworkControll.parentNetwork;
				boolean reseted = false;
   				Console.speed = "Games/Sec: 0";
   				Console.genPerSec = "Generations/Min: 0";
		   		while(true){
		   			frameCounter++;
		   			try {
						Thread.sleep((int) Math.round(1000 / updatePerSecondDisplay));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		   			if(pauseOnScreenGame){
		   				continue;
		   			}
		   			
		   			if(frameCounter % 100 == 1){
		   				if(NetworkControll.fast){
			   				double speed = System.currentTimeMillis() - (NetworkControll.startTime - NetworkControll.addedTime);
			   				
			   				speed = NetworkControll.matchCount / (speed/1000);
			   				Console.speed = "Games/Sec: "+Math.round(speed * 100)/100.0;
			   				Console.genPerSec = "Generations/Min: "+Math.round(speed / networkCount * 60 * 1000)/1000.0;
		   				}
		   				
		   			}
		   			
		   			if(displayNetwork1 != null && displayNetwork2 != null){
		   				if(playerPlay){
		   					if(playerSide == 0)
		   						networkPlay(pongGame, displayNetwork2, GUI.playerRight, GUI.playerLeft);
		   					else
		   						networkPlay(pongGame, displayNetwork1, GUI.playerRight, GUI.playerLeft);
		   				}else
		   					networkPlay(pongGame, displayNetwork1, displayNetwork2);
			   			
			   			int status = pongGame.gameTick();
			   			if(status == 1 || status == 2 || status == 3){
			   				pongGame.resetGame();
						}
		   			}
		   			
		   			
		   			//Update network every 10 generation
		   			if(NetworkControll.generation % guiNetworkUpdate == 1 && !dontUpdate){
		   				if(!reseted){
		   					if(!playerPlay){
			   					displayNetwork1 = NetworkControll.parentNetwork.copy();
			   					displayNetwork2 = NetworkControll.parentNetwork.copy();
				   				pongGame.resetGame();
			   				}
			   				GUI.fitness = new double[]{NetworkControll.fitness[0], NetworkControll.fitness[1]};
			   				reseted = true;
		   				}
		   			}else{
		   				reseted = false;
		   			}
		   			
		   		
		   			
		   		}
			}
		}).start();
	}
	
	static boolean setup(){
		pongGame = new Game(gameWidth, gameHeight, speed, Setup.paddleWidth, 30);

		//neuralNetwork neurons
		networkMatrix = new double[][]{
			new double[5],
			new double[9],
			new double[8],
			new double[6],
			new double[2]
		};
		
		/*networkMatrix = new double[][]{
			new double[1],
			new double[1],
			new double[2]
		};*/
		
		//create new randomly generated neural networks
		for(int i = 0; i < networks.length; i++){
			for(int j = 0; j < networks[i].length; j++){
				networks[i][j] = new NeuralNetwork(networkMatrix);
				//add 2 bits of memory
				networks[i][j].setRecurrent();
				networks[i][j].generateRandomNetwork();
				System.out.println(startMuatteRate);
				networks[i][j].setMutateRate(startMuatteRate);
				//networks[i][j].setCanEditMutateRate(true);
			}
		}
		
		//NetworkControll.parentNetwork[0] = networks[0][0].copy();
		//NetworkControll.parentNetwork[1] = networks[1][0].copy();
		NetworkControll.parentNetwork = networks[0][0].copy();
		Console console = new Console();
		
		GUI = new GameGUI(pongGame);
		GUI.totalNetworks = networkCount;
		
		//sconsole.executeCommand("setspeed 1001");
		if(networkCount % threadCount != 0){
			System.err.println("CAN'T LAUNCH! network count can't be devided by the thread count ");
			for(int i = 0;i < 10; i++)
				console.console("CAN'T LAUNCH! network count can't be devided by the thread count");
			GUI = null;
			return false;
		}else
			
		networkControll = new NetworkControll (threadCount, networks[0]);
	//	console.executeCommand("setbrain  &0 &1\n&2 0.0,,1.5317\n&2 0.0,,-0.5418&1 \n&2 0.3712,,0.9399,-2.761 &0 &1 \n&2 0.0,,3.381\n&2 0.0,,-2.8914&1 \n&2 -0.253,,0.7269,-3.3638");
		
		return true;
		
		
	}
	
	
	//simulate a game with 
	static void networkPlay(Game game, NeuralNetwork nn1, boolean goesRight, boolean goesLeft){
		int state = 0;
		if(goesRight && ! goesLeft)
			state = 1;
		if(goesLeft && ! goesRight)
			state = 2;

		networkPlay(game, nn1, nn1, true, state);
	}
	
	static void networkPlay(Game game, NeuralNetwork nn1, NeuralNetwork nn2){
		networkPlay(game, nn1, nn2, false, 0);
	}
	
	///simulate a game based on a network or fixed input
	static void networkPlay(Game game, NeuralNetwork nn1, NeuralNetwork nn2, boolean isPlayer, int state){
		double ballWidth = 30;
		
		//get all inputs and map it between 0 and 1
		double ballX = game.getBallX() / game.getWidth();
		double ballY = game.getBallY() / game.getHeight();
		
		double ballSpeedX = game.getBallSpeedX()/ game.getMaximumBallSpeed();
		double ballSpeedY = game.getBallSpeedY() / game.getMaximumBallSpeed();
		
		double p1 = (game.getPosition1() + paddleWidth / 2) / game.getWidth();
		double p2 = (game.getPosition2() + paddleWidth / 2) / game.getHeight();
		
		//             1              2              3              4                   5                      6
		//inputs = (Ball X pos), (Ball Y pos), (Ball speed X) (Ball speed Y), (Player 1 position X) , (Player 2 position x)
		
		nn1.setInput(1 - ballX, 1 - ballY, ballSpeedX, ballSpeedY, 1 - p1, 1 - p2);
		//GUI.info1 = "ballX: "+r(nn1.inputs[0])+",\t ballY: "+r(nn1.inputs[1])+",\t ballSpeedX: "+r(nn1.inputs[2])+", ballSpeedY: "+r(nn1.inputs[3])+",p1: "+r(nn1.inputs[4])+", p2: "+r(nn1.inputs[5]);
		
		//invert input (1 - input) because the player is mirrored relative to the other player
		if(!isPlayer)
			nn2.setInput(ballX, ballY, -ballSpeedX, -ballSpeedY, p2, p1);
			//GUI.info2 = "ballX: "+r(nn2.inputs[0])+", ballY: "+r(nn2.inputs[1])+", ballSpeedX: "+r(nn2.inputs[2])+", ballSpeedY: "+r(nn2.inputs[3])+", p1: "+r(nn2.inputs[4])+", p2: "+r(nn2.inputs[5]);
			//nn2.setInput(ballX, ballY, ballSpeedX, ballSpeedY, p1, p2);
		//get the output
		double[] output1 = nn1.getOutput();
		
		
		//if the output is < 0.5 , go to left, else go right
		if(output1[0] < 0.5){
			game.moveRight1();
		}else if(output1[0] > 0.5){
			game.moveLeft1();
		}
		
			
		if(!isPlayer){
			double[] output2 = nn2.getOutput();
			if(output2[0] > 0.5){
				game.moveRight2();
			}else if(output2[0] < 0.5){
				game.moveLeft2();
			}
		}else{
			game.setMovingSpeed(Setup.playerSpeed);
			if(state == 1){
				game.moveRight2();
			}
			if(state == 2){
				game.moveLeft2();
			}
			game.setMovingSpeed(Setup.speed);
		}
	}
	
	static double r(double x){
		return Math.round(x * 10.0)/10.0;
	}
}
