import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class NetworkShow {
	
	int rowWidth = 150;
	int columnHeight = 40;
	
	int mouseX = -1;
	int mouseY = -1;
	NeuralNetwork network;
	int dX = 0;
	int dY = 0;
	
	
	
	NetworkShow(int x, int y, NeuralNetwork network){
		this.dX = x;
		this.dY = y;
		this.network = network;
	}
	
	void setNetwork(NeuralNetwork newNetwork){
		this.network = newNetwork;
	}
	
	void mouseMove(int x, int y){
		
	}
	
	void mouseClick(){
		
	}
	
	void drawNetwork(Graphics2D g){
		if(network == null)
			return;
		double[][][] connections = network.getConnections();
		double[][] neurons = network.getNeurons();

		int rectHeight = 20;
		int rectWidth = 34;
		g.setStroke(new BasicStroke(2));
		
		for(int x = 0; x < connections.length; x++){
			for(int y = 0; y < connections[x].length; y++){
				for(int y2 = 0; y2 < connections[x][y].length; y2++){
					int value = (int) Math.round(connections[x][y][y2] / 5.0 * 254);

					if(value != 0){
						if(value > 0)
							g.setColor(new Color(Math.abs(value), 0, 0));
						else
							g.setColor(new Color(0, Math.abs(value), 0));

						
						line(g, rowWidth * x, y * columnHeight, rowWidth + rowWidth * x, y2 * columnHeight);
					}
					 
				}
			}
		}
		
		g.setColor(Color.black);
		
		for(int x = 0; x < neurons.length; x++){
			for(int y = 0; y < neurons[x].length; y++){
				g.setColor(Color.gray);
				fillRect(g, rowWidth * x - rectWidth/2, y * columnHeight - rectHeight/2, rectWidth, rectHeight);
			//	System.out.print(neurons[x][y]+" ");
				g.setColor(Color.white);
				drawString(g, Math.round(neurons[x][y]*100)/100.0+"", rowWidth * x - 15, y * columnHeight + 5);
			}
			//System.out.println();
		}
		
		g.setColor(Color.BLACK);
		
	}
	
	void rect(Graphics2D g, int x, int y, int width, int height){
		g.drawRect(x + dX, y + dY, width, height);
	}
	
	void fillRect(Graphics2D g, int x, int y, int width, int height){
		g.fillRect(x + dX, y + dY, width, height);
	}
	
	void line(Graphics2D g, int x1, int y1, int x2, int y2){
		g.drawLine(x1 + dX, y1 + dY, x2 + dX, y2 + dY);
	}
	
	void drawString(Graphics2D g, String s, int x, int y){
		g.drawString(s, x + dX, y + dY);
	}
}
