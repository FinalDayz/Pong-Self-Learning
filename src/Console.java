import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.awt.event.ActionEvent;

public class Console extends JPanel{

	static JFrame frame;
	JTextField textField;
	JTextArea Console;
	
	
	String origionalText = "";
	int tabIndex = 0;
	static String[] lastInputs=new String[200];
	String[] commands;
	String[] comments;
	String[] syntax;
	int selected = 0;
	
	static String speed = "";
	static String genPerSec = "";
	
	NetworkShow networkShow;
	
	public Console() {
		commands = new String[]{"setSpeed ","setBrain ","player", "printBrain", "updateBrain","setSensitivity ", "help", "fps", "setUps ", "pause"};
		comments = new String[]{
				"Set the speed of the game in the background in frames per second",
				"Set the brain to a custom one",
				"Play against bot 1 or 2 (leave empty to exit player mode)",
				"Print the text that make up the brain to later set it again(setbrain)",
				"This happens automaticly, but when you have player activated, it doesn't",
				"Set the sensitivity of the player when player is activated, default 10",
				"Thats where our are in now -,-",
				"Get the current fps",
				"Se the speed (in updates per second) of the game on screen, default 100",
				"Pause or unpause the game on screen"
		};
		
		syntax = new String[]{
				"setSpeed [speed]",
				"setBrain [brain]",
				"player '1' or '2' or ''",
				"printBrain",
				"updateBrain",
				"setSensitivity [sensitivity]",
				"help",
				"fps",
				"setUps [speed]",
				"pause"
		};
		
		networkShow = new NetworkShow(30, 50, NetworkControll.parentNetwork);
		initialize();
		
	}
	

	private void initialize() {
		frame = new JFrame();
		frame.setVisible(true);
		frame.setBounds(150, 10, 1081, 954);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 357, 848);
		frame.getContentPane().add(scrollPane);
		
		frame.getContentPane().add(this);
		this.setBounds(387, 14, 1008, 893);
		
		Console = new JTextArea();
		Console.setWrapStyleWord(true);
		scrollPane.setViewportView(Console);
		
		Console.setLineWrap(true);
		Console.setEditable(false);
		
		scrollPane.setViewportView(Console);
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addKeyUp();
				executeCommand(textField.getText());
				textField.setText("");
			}
		});
		
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 9){
					selected = 0;
					tab();
				}else if(e.getKeyCode() == 40){
					arrowDown();
				}else if(e.getKeyCode() == 38){
					arrowUp();
				}
				
			}
			
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode() != 9){
					origionalText = textField.getText();
					tabIndex = 0;
				}
			}

			
		});
		
		textField.setFocusTraversalKeysEnabled(false);
		
		textField.setBounds(79, 873, 291, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblCommand = new JLabel("Command:");
		lblCommand.setBounds(12, 876, 68, 16);
		frame.getContentPane().add(lblCommand);
		console("Pong Machine learning version "+Setup.version);
		console("Commands: ");
		for(int i = 0;i < commands.length; i++){
			console("--"+commands[i]+" - "+syntax[i]);
		}
		frame.repaint();
	}
	
	 @Override
	    public void paint(Graphics g1) {
	        Graphics2D g = (Graphics2D)g1;
	        super.paint(g1);
	
	        RenderingHints hints = new RenderingHints(
	        	    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
	        	);
	        	g.setRenderingHints(hints);
	        
	        	networkShow.drawNetwork(g);
	        	
	        g.setFont(new Font("Arial", Font.BOLD, 15));
	        g.drawString(speed, 0, 15);
	        g.drawString(genPerSec, 0, 35);
	        
	        
	 }
	
	void addKeyUp(){
		selected=0;	
		
		for(int i=0;i<lastInputs.length;i++){
			if(lastInputs[i]==null){
				if(i==0){
					lastInputs[i]=textField.getText();
					break;
				}

				if(!textField.getText().equals(lastInputs[i-1]))
				lastInputs[i]=textField.getText();
						break;
			}
		}
	}
	
	void arrowUp(){
		//ARROW UP FOR PREVIOUS TYPED COMMANDS
		selected++;

		for(int i=lastInputs.length-1;i>=0;i--){
			if(lastInputs[i]!=null){
				try{
				textField.setText(lastInputs[i-selected+1]);
				}catch(Exception e1){
					selected--;
				}
				break;
			}
		}
			
	
		
		
	
		
	}
	
	void arrowDown(){
		//ARROW DOWN FOR MORE RECENT TYPED COMMANDS
		if(selected>0)selected--;
		for(int i=lastInputs.length-1;i>=0;i--){
			if(lastInputs[i]!=null){
				try{
					
				textField.setText(lastInputs[i-selected+1]);
				
				}catch(Exception e1){
					selected--;
				}
				break;
			}
		}
	}
	
	void tab() {
		String text = origionalText;
		boolean foundCommand = false;
		if(tabIndex >= commands.length)
			tabIndex = 0;
		for(int i = tabIndex; i < commands.length; i++){
			if(commands[i].toLowerCase().indexOf(text.toLowerCase()) == 0){
				foundCommand = true;
				tabIndex = i+1;
				textField.setText(commands[i]);

				return;
			}
		}
		
		if(!foundCommand){
			tabIndex = 0;
			for(int i = tabIndex; i < commands.length; i++){
				if(commands[i].toLowerCase().indexOf(text.toLowerCase()) == 0){
					tabIndex = i;
					textField.setText(commands[i]);
				}
			}
		}
		
	}
	
	void printHelp(){
		console("Command usage: ");
		for(int i = 0;i < commands.length; i++){
			console("--"+commands[i]+" - "+comments[i]+"");
		}
	}
	
	
	void executeCommand(String command){
		command = command.toLowerCase();
		
		if(command.indexOf("setspeed ") == 0){
			try{
				String value = command.toLowerCase().replace("setspeed ", "");
				double speed = Double.parseDouble(value);
				if(speed > 1000 || speed < 1){
					NetworkControll.fast = true;
					NetworkControll.startTime = System.currentTimeMillis();
					Setup.GUI.setFPS(60);
				}else{
					NetworkControll.addedTime += System.currentTimeMillis() - NetworkControll.startTime;
					NetworkControll.fast = false;
					NetworkControll.speed = (int)Math.round(1000 / speed);
					Setup.GUI.setFPS(60);
				}
				console("Sucessfully set the speed to "+speed);
			}catch(Exception e){
				console("Error while changing speed, you have to input a number for example: setSpeed 100");
			}
		}
		if(command.indexOf("printbrain") == 0){
			String output = "";
			NeuralNetwork[] networksToPrint = {NetworkControll.parentNetwork, NetworkControll.parentNetwork};
			
			for(int i = 0; i < networksToPrint.length; i++){
				
				double[][][] connections = networksToPrint[i].getConnections();
				double[][] biases = networksToPrint[i].bias;
				
				output += " &0 ";
				
				for(int x = 0; x < connections.length; x++){
					
					output += "&1 ";
					for(int y = 0; y < connections[x].length; y++){
						
						output += "\n&2 ";
						output += biases[x][y]+",";
						for(int z = 0; z < connections[x][y].length; z++){
							output += ","+connections[x][y][z];
						}
						
					}
					
				}
				
			}
			console(output);
			
			//String[]
		}

		if(command.indexOf("player ") == 0){
			int side = Integer.parseInt(command.replace("player ", ""));
			
			Setup.playerPlay = true;
			Setup.playerSide = side;
		}
		
		if(command.equals("player")){
			Setup.playerPlay = !Setup.playerPlay;
			
		}
		
		if(command.equals("pause")){
			Setup.pauseOnScreenGame = !Setup.pauseOnScreenGame;
		}
		
		if(command.equals("updatebrain")){
			Setup.displayNetwork1 = NetworkControll.parentNetwork.copy();
			Setup.displayNetwork2 = NetworkControll.parentNetwork.copy();
			Setup.pongGame.resetGame();
		}
		
		if(command.indexOf("setsensitivity ") == 0){
			try{
				Setup.playerSpeed = Double.parseDouble(command.replace("setsensitivity ", ""));
			}catch(Exception e){
				console("Error, you have to input a valid numer");
			}
			
		}
		
		if(command.indexOf("setups ") == 0){
			try{
				Setup.updatePerSecondDisplay = Double.parseDouble(command.replace("setups ", ""));
			}catch(Exception e){
				console("Error, you have to input a valid numer");
			}
		}
		
		if(command.equals("help")){
			printHelp();
		}
		
		if(command.equals("fps")){
			System.out.println(Setup.GUI.getFPS());
			console("Current FPS: "+Setup.GUI.getFPS()+" (the fps changes when the speed changes)");
		}

		if(command.indexOf("setbrain ") == 0){
			
			command = command.replace(" ", "").replace("\n", "");
			String[] brains = (command.replace("setbrain&0", "")).split("&0");
			System.out.println("Brain "+brains[0]);
			
			for(int i = 0; i < brains.length; i++){
				String[] rows = brains[i].split("&1");
				double[][][] connections = new double[rows.length][][];
				System.out.println("rows: "+rows.length);
				for(int row = 0; row < rows.length; row++){
					
					String[] col1 = rows[row].replaceFirst("&2", "").split("&2");
					connections[row] = new double[col1.length][];
					System.out.println("colls:: "+col1.length+" : "+Arrays.toString(col1));
				}
			}
			System.exit(0);
		}
		
		
		
	}
	
	
	void console(String what){
		Console.append(what+"\n");
	}
}
