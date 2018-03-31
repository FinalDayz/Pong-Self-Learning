import java.util.Arrays;

public class NetworkControll {
	NetworkLearn[] networkLearn;
	
	NeuralNetwork[] networks;
	
	private int totalDone = 0;
	private int networkCount = 0;
	private int threadCount = 0;
	
	static boolean stop = true;
	static int generation = 0;
	
	static double[] fitness;
	static double[] fitnessParent;
	static NeuralNetwork parentNetwork;
	
	static boolean fast = false;
	static int speed = 10;
	
	static int matchCount = 0;
	static long addedTime = 0;
	static long startTime = 0;
	
	static int currentWorkingNetworkIndex = -1;
	
	NetworkControll(int threadCount, NeuralNetwork[] networks){
		this.networks = networks;
		this.threadCount = threadCount;
		networkLearn = new NetworkLearn[threadCount];
		networkCount = networks.length;
		fitness = new double[networkCount];
		fitnessParent = new double[networkCount];
		int networksPerThread = networkCount / threadCount;
		stop = false;
		
		for(int i = 0; i < threadCount; i++){
			
			//Array if the indexes that each thread will use
			int[] usingNetworks = new int[networksPerThread];
			for(int j = 0; j < usingNetworks.length; j++){
				usingNetworks[j] = i * networksPerThread + j;
			}
			startThread(usingNetworks, i);
		}
	}
		
	//for example there are 20 networks, and 4 threads so that is 5 networks per thread
	//indexArray is the indexes of the networks it will test, ex: [5, 6, 7, 8, 9] index is in this case 1
	//other example: [0, 1, 2, 3, 4] index = 0
	
	void startThread(final int[] indexArray, final int index){
		networkLearn[index] = new NetworkLearn();
		System.out.println("Launch thread, "+index+" testing "+indexArray.length+" networks ("+Arrays.toString(indexArray)+")");
		new Thread(new Runnable(){
			public void run() {
				
		  		while(!stop){
		   			for(int i = 0; i < indexArray.length; i++){
		   				

		   				//get fitness from network
		   				int networkIndex = indexArray[i];
		   				currentWorkingNetworkIndex = networkIndex;
		   				
		   				
		   				//Setup.pongGame.resetGame();
		   				Setup.GUI.network++;
		   						
		   				double[] fit = networkLearn[index].getFitness(parentNetwork, networks[networkIndex], index);
		   				fitnessParent[networkIndex] = fit[0];
		   				fitness[networkIndex] = fit[1];

		   				
		   				if(fast){
		   					matchCount++;
		   					//totalMatchTime += System.currentTimeMillis() - first;
		   				}
		   				
		   				
		   			}
		   			//it calculated all things
		   			done();

		   			mutateConnections(indexArray);
		   			/*
		   			for(int j = 0; j < indexArray.length; j++){
						int index = indexArray[j];
						networks[index] = parentNetwork.copy();
						//networks[index].mutate();
					}
		   			
		   			println("\n PARENT");
		   			parentNetwork.println(parentNetwork.connections);
		   			
		   			for(int j = 0; j < indexArray.length; j++){
		   				int index = indexArray[j];
		   				println("CHILD "+index);
		   				networks[j].println(networks[j].connections);
		   			}
		   		//	println("0 CHILD");
		   			//workingNetworks[0].println(workingNetworks[0].connections);
		   			println("");
		   			*/
		   		}
			
			}
		}).start();
			
	}
	
	
	//if a thread is done
		void done(){
			totalDone++;
			//the last thread that completes, calculates the next generation
			if(totalDone >= threadCount){
				totalDone--;
				generation++;
				resetGeneration();
				Setup.GUI.network = 0;
				
				totalDone = 0;
				
				double totalMutation = 0;
				int networkCount = 0;
				for(int j = 0; j < networks.length; j++){
					totalMutation += networks[j].mutateRate;
					networkCount++;
				}
			}
			//wait for all the threads to complete
			while(totalDone != 0){
				try {
					Thread.sleep(0,100000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}

		
		//same NN against itself
		void resetGeneration(){
			
			//select the network with the best fitness
			int bestIndex = 0;
			double bestFitness = -999999;
			for(int i = 0; i < fitness.length; i++){
				if(fitness[i] > bestFitness){
					bestIndex = i;
					bestFitness = fitness[i];
				}
			}

			System.out.println("Choosed index: "+bestIndex+" With fitness: "+bestFitness+" (fitness sollid: "+fitnessParent[bestIndex]+")");
			
			double allFit = 0;
			double parentFit = 0;
			for(int i = 0; i < fitness.length; i++){
				allFit += fitness[i];
				parentFit += fitnessParent[i];
			}
			
			allFit /= fitness.length;
			parentFit /= fitnessParent.length;
			
			println("all fit average: "+allFit);
			System.out.println("Sollid average: "+parentFit);
			
			//only copy is the best network did BETTER than the parent
			if(bestFitness > fitnessParent[bestIndex]){
				System.out.println("It acctually did better! SO CHANGE PARENT WITH INDEX: "+bestIndex+" that is: ");
				parentNetwork = this.networks[bestIndex].copy();
				
			}else{
				System.out.println("It DID WORSE!");
			}
		}
		
		//mutate the connections 
		void mutateConnections(int[] indexArray){

			for(int j = 0; j < indexArray.length; j++){
				int index = indexArray[j];
				networks[index] = parentNetwork.copy();
				networks[index].mutate();
			}
		}
		
		void println(Object o){
			System.out.println(o);
		}
	
}
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================
//===================================================================================================================================================

/**

class somethingElse{
	
	NetworkLearn[] networkLearn;
	boolean stop = true;
	int totalDone = 0;
	int networkCount = 0;
	int threadCount = 0;
	
	static double[][] fitness;
	NeuralNetwork[][] networks;
	static NeuralNetwork[] parentNetwork = new NeuralNetwork[2];
	
	NeuralNetwork[][] previousNetworks = new NeuralNetwork[Setup.playerCount][Setup.previousNetworks];
	
	static int generation = 0;
	
	static boolean fast = false;
	static int speed = 10;
	
	static int matchCount = 0;
	static long addedTime = 0;
	static long startTime = 0;
	
	somethingElse(int threadCount, NeuralNetwork[][] networks){
		
		this.networks = networks;
		this.threadCount = threadCount;
		networkLearn = new NetworkLearn[threadCount];
		networkCount = networks[0].length;
		fitness = new double[Setup.playerCount][networkCount];
		int networksPerThread = networkCount / threadCount;
		stop = false;
		
		
		for(int i = 0; i < threadCount; i++){
			
			//Array if the indexes that each thread will use
			int[] usingNetworks = new int[networksPerThread];
			for(int j = 0; j < usingNetworks.length; j++){
				usingNetworks[j] = i * networksPerThread + j;
			}
			startThread(networks, usingNetworks, i);
		}
	}
	
	
	void startThread(final NeuralNetwork[][] networks, final int[] indexArray, final int index){
		networkLearn[index] = new NetworkLearn();
		System.out.println("Launch thread, "+index+" testing "+indexArray.length+" networks");
		new Thread(new Runnable(){
			public void run() {
				
		   		while(!stop){
		   			for(int i = 0; i < indexArray.length; i++){
		   				

		   				//give fitness to a network
		   				int networkIndex = indexArray[i];
		   				double[] fit = networkLearn[index].getFitness(networks[0][networkIndex], networks[1][networkIndex], index);
		   				fitness[0][networkIndex] = fit[0];
		   				fitness[1][networkIndex] = fit[1];
		   				
		   				if(fast){
		   					matchCount++;
		   					//totalMatchTime += System.currentTimeMillis() - first;
		   				}
		   				
		   				
		   			}
		   			//it calculated all things
		   			done();
		   			
		   			mutateConnections(networks, indexArray);
		   		}
			}
		}).start();
	}
	
	//mutate the connections 
	void mutateConnections(NeuralNetwork[][] networks, int[] indexArray){
		int mutatedSide = 1;
		if(fitness[0][0] < fitness[1][0])
			mutatedSide = 0;
		//if they have the same fitness, choose a random one
		if(fitness[0][0] == fitness[1][0] && Math.random() > 0.5)
			mutatedSide = 0;
		
		
		
		for(int i = 0; i < networks.length; i++){
		//	System.out.println("["+i+"] Networks to process: "+Arrays.toString(indexArray));
			for(int j = 0; j < indexArray.length; j++){
				int index = indexArray[j];
				networks[i][index] = parentNetwork[i].copy();
				//System.out.println("1: "+parentNetwork[i].connections[0][0][0]+" conn: "+networks[i][index].connections[0][0][0]+" ["+i+"]["+index+"]");
				
				//mutate network or copy from previous parent
				if(i == mutatedSide)
					if(index >= Setup.previousNetworks || previousNetworks[i][index] == null)
						networks[i][index].mutate();
					else
						networks[i][index] = previousNetworks[i][index];
					
				
				//System.out.println("2: "+parentNetwork[i].connections[0][0][0]+" conn: "+networks[i][index].connections[0][0][0]+" ["+i+"]["+index+"]");
				//System.out.println();
			}
		}
	}
	
	//if a thread is done
	void done(){
		totalDone++;
		//the last thread that completes, calculates the next generation
		if(totalDone >= threadCount){
			totalDone--;
			generation++;
			resetGeneration();
			
			totalDone = 0;
			
			for(int i = 0; i < networks.length; i++){
				double totalMutation = 0;
				int networkCount = 0;
				for(int j = 0; j < networks[i].length; j++){
					totalMutation += networks[i][j].mutateRate;
					networkCount++;
				}
			}
			
		}
		//wait for all the threads to complete
		while(totalDone != 0){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// one NN vs another one
	/*void resetGeneration(){
		for(int i = 0; i < Setup.playerCount; i++){
			//select the network with the best fitness
			int bestIndex = 0;
			double bestFitness = -999999;
			for(int j = 0; j < fitness[i].length; j++){
				if(fitness[i][j] > bestFitness){
					bestIndex = j;
					bestFitness = fitness[i][j];
				}
			}
			//System.out.println("Choosed index: "+bestIndex+" With fitness: "+bestFitness+" all: "+Arrays.toString(fitness[i]));
			fitness[i][0] = bestFitness;
			//set the parent network to the best network
			parentNetwork[i] = networks[i][bestIndex].copy();
			if(i == 0)
				Setup.GUI.setFitness(bestFitness, Setup.GUI.fitness[1]);
			else if(i == 1){
				Setup.GUI.setFitness(Setup.GUI.fitness[0], bestFitness);
			}
		
		}
		
		int mutatedSide = 1;
		if(fitness[0][0] < fitness[1][0])
			mutatedSide = 0;
		//if they have the same fitness, choose a random one
		if(fitness[0][0] == fitness[1][0] && Math.random() > 0.5)
			mutatedSide = 0;
		
		
		
		for(int i = 0; i < networks.length; i++){
			//only copy parent if 
			if(i == mutatedSide){
				//copy parent to the saved networks
				for(int j = previousNetworks[i].length - 1; j > 0; j--){

					if(previousNetworks[i][j-1] != null)
						previousNetworks[i][j] = previousNetworks[i][j-1].copy();
				}
				previousNetworks[i][0] = parentNetwork[i].copy();
			}
		}
		
	}* /
	
	//same NN against itself
	void resetGeneration(){
		int bestNetwork = -1;
		double bestFitnessAll = -999999;
		int bestNetworkIndex = -1;
		
		for(int i = 0; i < Setup.playerCount; i++){
			//select the network with the best fitness
			int bestIndex = 0;
			double bestFitness = -999999;
			for(int j = 0; j < fitness[i].length; j++){
				if(fitness[i][j] > bestFitness){
					bestIndex = j;
					bestFitness = fitness[i][j];
				}
			}
			
			
			//System.out.println("Choosed index: "+bestIndex+" With fitness: "+bestFitness+" all: "+Arrays.toString(fitness[i]));
			
		    fitness[i][0] = bestFitness;
		      
		     parentNetwork[i] = this.networks[i][bestIndex].copy();
			/*
			//set the parent network to the best network
			if(bestFitness > bestFitnessAll){
				bestFitnessAll = bestFitness;
				bestNetwork = i;
				bestNetworkIndex = bestIndex;
				
				//fitness[0][0] = bestFitness;
				//fitness[1][0] = bestFitness;
				
				parentNetwork[0] = networks[i][bestIndex].copy();
				parentNetwork[1] = networks[i][bestIndex].copy();
			}
			
			* /
			
			if(i == 0)
				Setup.GUI.setFitness(bestFitness, Setup.GUI.fitness[1]);
			else if(i == 1){
				Setup.GUI.setFitness(Setup.GUI.fitness[0], bestFitness);
			}
		
		}
		
		int mutatedSide = 1;
		if(fitness[0][0] < fitness[1][0])
			mutatedSide = 0;
		//if they have the same fitness, choose a random one
		if(fitness[0][0] == fitness[1][0] && Math.random() > 0.5)
			mutatedSide = 0;
		
		
		
		for(int i = 0; i < networks.length; i++){
			//only copy parent if 
			if(i == mutatedSide){
				//copy parent to the saved networks
				for(int j = previousNetworks[i].length - 1; j > 0; j--){

					if(previousNetworks[i][j-1] != null)
						previousNetworks[i][j] = previousNetworks[i][j-1].copy();
				}
				previousNetworks[i][0] = parentNetwork[i].copy();
			}
		}
		
	}
	
	void println(Object o){
		System.out.println(o);
	}
	/*
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
		double paddleWidth = Setup.paddleWidth;
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
		nn1.setInput(ballX, ballY, ballSpeedX, ballSpeedY, p1, p2);
		
		//invert input (1 - input) because the player is mirrored relative to the other player
		if(!isPlayer)
			nn2.setInput(1 - ballX, 1 - ballY, 1 - ballSpeedX, 1 - ballSpeedY, 1 - p2, 1 - p1);
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
	}* /
	
}
**/