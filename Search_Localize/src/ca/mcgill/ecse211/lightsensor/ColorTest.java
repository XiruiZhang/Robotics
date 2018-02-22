package ca.mcgill.ecse211.lightsensor;

import ca.mcgill.ecse211.model.Robot;
import lejos.hardware.Sound;

public class ColorTest extends Thread {
	// modify this to match the color of the block
	//public final String EXPECTED_COLOR="EMPTY";
	public final double DISTANCE=0;
	private LightSensorController cont;
	private static float[] color=new float[Robot.colorProvider.sampleSize()];

	private static double[] targetValue = new double [Robot.colorProvider.sampleSize()]; 
	private static String colorName = "EMPTY";
	
	// constructor here
	public ColorTest(LightSensorController cont, int targetColor) {
		this.cont=cont;
		// needs to fetch ambient light once and use it as a base for calcualtion
		//Robot.colorProvider = Robot.colorSensor.getAmbientMode(); // the sample size should be 3
		//ambientColor = new float[Robot.colorProvider.sampleSize()];
		//Robot.colorProvider.fetchSample(ambientColor, 0);
		//ambientLight[0]=(int) (ambientColor[0] * 100.0);
		//System.out.println("Ambient light: "+ambientLight[0]);
		// switch mode of colorSensor to getRGBMode()
		//Robot.colorSensor.getRGBMode();
		// set target color
		this.setTargetColor(targetColor);
		//System.out.println("========Below is rsv file=======");
		//System.out.println("Red,Blue,Yellow,Distance,Actual_color");
	}
	
	public void run() {

		
		int counter=0;  //counter?
		while (true) {
			Robot.colorProvider.fetchSample(color, 0); // acquire data	
			// print to RSV file format up to two floating point precision
			//System.out.printf("%.2f,%.2f,%.2f,%.2f,%s\n",lightVal[0],lightVal[1],lightVal[1],DISTANCE,EXPECTED_COLOR);
			boolean ifFound = findColor();
			if(ifFound == true) {
				Sound.beep();
				System.out.println("Color block found!");
				System.out.println(colorName);
				colorName="None";
				//Robot.turn("RIGHT");
			}
			//cont.processLightData(tb);
			counter++;
			
			try {
				// 10hz refresh rate
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method checks if the block is a color
	 * @param int: color of the block
	 */
	public boolean isColorBlock(int tb) {
		if(tb!=-1) {
			return true;
		}
		else 
			return false;
	}
	/**
	 * This method calculates the color of the block using RGB value
	 * @return
	 */
	public boolean findColor() {
		// 1: Red, 2: Blue, 3: Yellow, 4: White -99: noise 
		boolean ifFound = false;
		double lightVal[]=new double[3];
		
		lightVal[0] = color[0] * 1000.0; // R value
		lightVal[1] = color[1] * 1000.0; // G value
		lightVal[2] = color[2] * 1000.0; // B value
		double difference = Math.sqrt(Math.pow((color[0] - targetValue[0]), 2) + Math.pow((color[1] - targetValue[1]), 2) + Math.pow((color[2] - targetValue[2]), 2));
		
		if(difference < 1) {
			ifFound = true;
		}else {
			ifFound = false;
		}

		return ifFound;
	}
	/**
	*	ToDO: add javadoc here
	*/
	private void setTargetColor(int targetColor){
		switch(targetColor) {		//determine the targetBlock
			case 1:
				colorName = "RED";
				targetValue[0] = 74.51;
				targetValue[1] = 11.76;
				targetValue[2] = 11.76;
			break;
			
			case 2:
				colorName = "BLUE";
				targetValue[0] = 9.80;
				targetValue[1] = 25.49;
				targetValue[2] = 25.49;
			break;
			
			case 3:
				colorName = "YELLOW";
				targetValue[0] = 146.08;
				targetValue[1] = 112.75;
				targetValue[2] = 112.75;
			break;
			
			case 4:
				colorName = "WHITE";
				targetValue[0] = 104.90;
				targetValue[1] = 117.65;
				targetValue[2] = 117.65;
			break;	
		}
	}
}