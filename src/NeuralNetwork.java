import java.util.Arrays;

public class NeuralNetwork{
	
	private double[][] network;
	double[][][] connections;
	double[][] bias;

	
	double[] inputs;
	
	int memoryBits = 0;
	double[] memoryNeurons = new double[0];
	double output[];
	
	int[] recurrentCount;
	boolean recurrent = true;
	boolean useMemory = false;
	
	double mutateRate = 1.5;//%
	double ajustConnectionRate = 3.5;
	boolean canEditMutateRate = true;
	
	//5 = -5 to 5
	double randomConnections = 4.5;
	
	
	//creat the network and setup stuff
	NeuralNetwork(double[][] networkMatrix){
		network = copyArray(networkMatrix);
		output = new double[network[network.length - 1].length];
		this.inputs = network[0].clone();
	}
	
	
	void setRecurrent(){
		recurrent = true;
		recurrentCount = new int[network.length-2];
				
		for(int i = 0; i < network.length; i++){
			if(isRecurrentx(i)){
				recurrentCount[i] = network[i+1].length;
				network[i] = new double[network[i].length + network[i+1].length];
				
			}	
		}
		this.inputs = network[0].clone();
	}
	
	void setMutateRate(double percent){
		mutateRate = percent;
	}
	
	void setAjustConnectionRate(double percent){
		ajustConnectionRate = percent;
	}
	
	void setCanEditMutateRate(boolean canEditMutateRate){
		this.canEditMutateRate = canEditMutateRate;
	}
	
	private boolean isRecurrentx(int x){
		return (x < network.length-2);
	}
	
	private boolean isRecurrentxy(int x, int y){
		if(!isRecurrentx(x))
			return false;
		if(x >=	recurrentCount.length)
			return false;
		int normalNeurons = network[x].length - recurrentCount[x];
		
		return (y >= normalNeurons);
	}
	
	void setConnections(double[][][] connections){
		this.connections = connections;
	}
	

	NeuralNetwork copy() {
		double[][] networkCopy = new double[network.length][];
		for(int i = 0; i < networkCopy.length; i++){
			networkCopy[i] = new double[network[i].length];
		}
		
		NeuralNetwork network2 = new NeuralNetwork(network);
		network2.recurrentCount = recurrentCount.clone();
		network2.recurrent = true;
		network2.setConnections(copyArray(connections));
		network2.bias = copyArray(copyArray(bias));
		network2.setCanEditMutateRate(canEditMutateRate);
		network2.setMutateRate(this.mutateRate);
			
	    return network2;
	}
	
	//generate both random connection and bias
	void generateRandomNetwork(){
		generateRandomConnections();
		generateRandomBias();
	}
	
	void generateRandomConnections(){
		connections = randomConnections(network);
		
	}
	
	void resetNetwork(){
		memoryNeurons = new double[memoryBits];
		for (int x = 0; x < network.length; x++) {
			for (int y = 0; y < network[x].length; y++)
				network[x][y] = 0;
				
		}
	}
	
	void setMemory(int memoryBits){
		useMemory = true;
		this.memoryBits = memoryBits;
		memoryNeurons = new double[memoryBits];
		network[0] = new double[network[0].length + memoryBits];
		network[network.length - 1] = new double[network[network.length - 1].length + memoryBits];
	}
	
	//generate random connections
	double[][][] getConnections(){
		return this.connections;
	}
	
	//generate random bias
	void generateRandomBias(){
		bias = randomBias(network);
	}
	
	int getOutputLength(){
		return output.length;
	}
	
	
	//Calculate the output of the whole network with the inputs
	double[] getOutput(){
		//reset network values
		for (int x = 0; x < network.length; x++) {
			for (int y = 0; y < network[x].length; y++)
				if(!recurrent || !isRecurrentxy(x, y)){
					network[x][y] = 0;
				}
		}
		for(int i = 0; i < inputs.length; i++)
			if(!recurrent || !isRecurrentxy(0, i))
				network[0][i] = inputs[i];
		
		//use memory to add to the inputs
		if(useMemory)
			for(int i = 0; i < memoryNeurons.length; i++){
				int index = network[0].length - 1 - i;
				network[0][index] = memoryNeurons[i];
			}

		
		// loop through the layers
		for (int i = 0; i < network.length - 1; i++) {
			// loop through all network of the layer
			for (int infoNeuron = 0; infoNeuron < network[i].length; infoNeuron++) {

				// loop through all network of the next layer
				for (int setNeuron = 0; setNeuron < network[i + 1].length; setNeuron++) {
					
					//don't calculate input if the neuron is a recurrent
					if(!recurrent || !isRecurrentxy(i, setNeuron)){
						//previous neuron value times connection
						
						network[i + 1] [setNeuron] +=  network[i][infoNeuron] * connections[i][infoNeuron][setNeuron];
					
					}

				}
			}

			for (int setNeuron = 0; setNeuron < network[i + 1].length; setNeuron++) {
				if(!recurrent || !isRecurrentxy(i, setNeuron)){
					//don't add bias to the output
					if(i + 1 < network.length - 1){
						//add bias to value
						network[i + 1][setNeuron] += bias[i + 1][setNeuron];
						//System.out.println(i + 1+" ReLU");
						network[i + 1][setNeuron] = sigmoid(network[i + 1][setNeuron]);
					}else{
						//sigmoid through the value
					//System.out.println(i + 1+" sigmoid");
						network[i + 1][setNeuron] = sigmoid(network[i + 1][setNeuron]);
					}
					
					
					
					if(recurrent){
						if(i > 0){
							
							int x = i - 1;
							int y = setNeuron + (network[i-1].length -
									recurrentCount[i-1]);
							network[x][y] = network[i][setNeuron];
						}
					}
				}else{
					
				}
			}

		}

		//set memory to last few output bits
		if(useMemory)
			for(int i = 0; i < memoryNeurons.length; i++){
				int index = network[network.length - 1].length - 1 - i;
				memoryNeurons[i] = network[network.length - 1][index];
			}
		
		for(int i = 0; i < output.length; i++){
			output[i] = network[network.length - 1][i];
		}
		
		
		return output.clone();
	}
	
	//mutate values of connections (percent is 0 - 1)
	void mutate(){
		
		if(canEditMutateRate){
			mutateRate += (Math.random() * 2 - 1) * mutateRate / 13;
		} 
		
		for(int x = 0; x < connections.length; x++){

			if(connections[x] != null)
			for(int y = 0; y < connections[x].length; y++){
				
				for(int z = 0; z < connections[x][y].length; z++){
					//random copy value of connection to connection2
					if(connections[x][y][z] != 0)
						if(Math.random() < mutateRate/ 100.0){
							connections[x][y][z] = randomConnection();
						} else if(Math.random() < ajustConnectionRate/ 100.0) {
							
						}
					
					}
			}
		}
		
		
	}
	
	NeuralNetwork crossover(NeuralNetwork network2){
		double[][][] connections2 = copyArray(network2.getConnections());
		double[][] bias2 = copyArray(network2.bias);

		//crossover connections
		for(int x = 0; x < connections2.length; x++){
			if(connections2[x] !=null)
			for(int y = 0; y < connections2[x].length; y++){
				for(int z = 0; z < connections2[x][y].length; z++){
					//random copy value of connection to connection2
					if(Math.random() > 0.5){
						connections2[x][y][z] = 
								connections[x][y][z];
					}
				}
			}
		}

		//crossover bias
		for(int x = 0; x < bias.length; x++){
			for(int y = 0; y < bias[x].length; y++){
				//random copy value of connection to connection2
				if(Math.random() > 0.5){
					bias2[x][y] = bias[x][y];
				}
			}
		}
		
		NeuralNetwork net1 = new NeuralNetwork(network);
		net1.setConnections(connections2);
		net1.bias = copyArray(bias2);
		net1.setCanEditMutateRate(canEditMutateRate);
		net1.setMutateRate((network2.mutateRate + this.mutateRate) / 2);
		
		return net1;
	}
	
	
	void setInput(double... inputs){
		for(int i = 0; i < inputs.length; i++){
			this.inputs[i] = inputs[i];
		}
		
	}
	
	double[][] getNetwork(){
		return this.network;
	}
	
	double[][] getNeurons(){
		return this.network;
	}
	
	private double relu(double x){
			if(x < 0)
				return x / 40;
			return x;
	}
	
	private double tanh(double x){
		return Math.tanh(x);
	}
	
	private double sigmoid(double x){
		return (1 / (1 + Math.pow(Math.E, (-x))));
	}
	
	 void println(double[][][] o){
		 if(o == null){
			 println("NULL!");
			 return;
		 }
		for(int x = 0; x < o.length; x++){
			
			if(o[x] != null)
				for(int y = 0; y < o[x].length; y++){
					
					if(o[x][y] != null)
						println(x+" "+y+" : "+Arrays.toString(o[x][y]));
				}
		}
	}
	 
	 void println(double[][] o){
		for(int x = 0; x < o.length; x++){
			
			if(o[x] != null)
				println(Arrays.toString(o[x]));
		}
	}

	 
	//clone 2 dimension array
	private double[][] copyArray(double[][] array1){
		double[][] array2 = new double[array1.length][];
				
		for(int x = 0; x < array1.length; x++){
			array2[x] = new double[array1[x].length];
					
			for(int y = 0; y < array1[x].length; y++){
				array2[x][y] = array1[x][y];
				
			}
		}
		return array2;
	}
	

	 
	//clone 3 dimension array
	private double[][][] copyArray(double[][][] array1){
		double[][][] array2 = new double[array1.length][][];
		
		for(int x = 0; x < array1.length; x++){
			array2[x] = new double[array1[x].length][];
				
				for(int y = 0; y < array1[x].length; y++){
					array2[x][y] = new double[array1[x][y].length];
					
					for(int z = 0; z < array1[x][y].length; z++){
						array2[x][y][z] = array1[x][y][z];
					}
				}

		}
		return array2;
	}
	 
	//Generate random bias numbers
	private double[][] randomBias(double[][] networkMatrix){
		double[][] bias = new double[networkMatrix.length][];
		
		// X axis 
		for(int x = 0; x < networkMatrix.length; x++){
			bias[x] = new double[networkMatrix[x].length];
			//y axis
			for(int y = 0; y < networkMatrix[x].length; y++){
				if(x > 0)
					bias[x][y] = randomBias();
			}
		}
		
		return bias;
	}
	
	//Generate random connections
	private double[][][] randomConnections(double[][] networkMatrix){
		
		double[][][] connections = new double[networkMatrix.length-1][][];
		
		// X axis 
		for(int x = 0; x < networkMatrix.length-1; x++){
			connections[x] = new double[networkMatrix[x].length][];
			
			//first Y axis
			for(int y = 0; y < networkMatrix[x].length; y++){
				connections[x][y] = new double[networkMatrix[x+1].length];
				
				//second Y axis
				for(int y2 = 0; y2 < networkMatrix[x+1].length; y2++){
					if(!recurrent || !isRecurrentxy(x, y2))
						connections[x][y][y2] = randomConnection();
					
				}
			}
		}
		//System.exit(0);
		return connections;
	}

	//number between -1.5 and 1.5
	private double randomConnection(){
		return round(Math.random() * (randomConnections * 2) - randomConnections, 4);
	}
	
	//number between -1 and 1
	private double randomBias(){
		return round(Math.random() * 2 -1, 4);
	}
	
	
	private static void println(Object o){
		System.out.println(o);
	}
	
	private static void print(Object o){
		System.out.print(o);
	}
	
	private double round(double num, int decimals){
		double multiplier = Math.pow(10, decimals);
		return Math.round(num*multiplier)/multiplier;
	}
}
