package ca.mcgill.ecse211.lightsensor;

import ca.mcgill.ecse211.display.Display;
import ca.mcgill.ecse211.model.Robot;
import lejos.hardware.Sound;

public class ColorTest extends Thread {
	// modify this to match the color of the block
	//public final String EXPECTED_COLOR="EMPTY";
	//public final double DISTANCE=0;
	private LightSensorController cont;
	
	private static float[] color=new float[Robot.colorProvider.sampleSize()];
	//private static float[] ambientColor = new float[Robot.colorProvider1.sampleSize()];
	//private static int[] ambientLight;
	private static int targetColor;
	private static double[] targetValue = new double [Robot.colorProvider.sampleSize()]; 
	private static String colorName = "EMPTY";
	public static Display colorDisplay;
	
	public static int EXPECTED=-1;
	
	// constructor here
	public ColorTest(LightSensorController cont) {
		this.cont=cont;
		// needs to fetch ambient light once and use it as a base for calcualtion
		//Robot.colorProvider = Robot.colorSensor.getAmbientMode(); // the sample size should be 3
		//ambientColor = new float[Robot.colorProvider.sampleSize()];
		//Robot.colorProvider.fetchSample(ambientColor, 0);
		//ambientLight[0]=(int) (ambientColor[0] * 100.0);
		//System.out.println("Ambient light: "+ambientLight[0]);
		// switch mode of colorSensor to getRGBMode()
		//Robot.colorSensor.getRGBMode();
		//this.targetColor = targetColor;
		System.out.println("========Below is rsv file=======");
		System.out.println("Red,Blue,Yellow,Distance,Result,Expected");
	}
	
	public void run() {
		Robot.lcd.clear();
		
		int counter=0;  //counter?
		while (true) {
			Robot.colorProvider.fetchSample(color, 0); // acquire data
			int ifFound = findColor();
			
			if(ifFound == 1) {
				Sound.beep();
				Robot.lcd.drawString("Object detected", 0, 0);
				Robot.lcd.drawString("RED", 0, 1);
			}else if(ifFound == 2) {
				Sound.beep();
				Robot.lcd.drawString("Object detected", 0, 0);
				Robot.lcd.drawString("BLUE", 0, 1);
			}else if(ifFound == 3) {
				Sound.beep();
				Robot.lcd.drawString("Object detected", 0, 0);
				Robot.lcd.drawString("YELLOW", 0, 1);
			}else if(ifFound == 4) {
				Sound.beep();
				Robot.lcd.drawString("Object detected", 0, 0);
				Robot.lcd.drawString("WHITE", 0, 1);
			}
			
			// check if there us an object in front
			// return value only when a color block is detected
			//cont.processLightData(tb);
			counter++;
			
			try {
				// 10hz refresh rate
				Thread.sleep(300);
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
	public int findColor() {
		// 1: Red, 2: Blue, 3: Yellow, 4: White -99: noise 
		int ifFound = 0;
		double lightVal[]=new double[3];
		
		double differencewithRed1, differencewithRed2, differencewithRed3 = 0; 
		double differencewithYellow1, differencewithYellow2, differencewithYellow3 = 0;
		double differencewithBlue1, differencewithBlue2, differencewithBlue3 = 0; 
		double differencewithWhite1, differencewithWhite2, differencewithWhite3 = 0;
		
		lightVal[0] = color[0] * 1000.0; // R value
		lightVal[1] = color[1] * 1000.0; // G value
		lightVal[2] = color[2] * 1000.0; // B value
		if(lightVal[0] + lightVal[1] + lightVal[2] <= 20) {
			ifFound = 0;	
		}else {
			differencewithRed1 = Math.sqrt(Math.pow((lightVal[0] - 110.78), 2) + Math.pow((lightVal[1] - 15.69), 2) + Math.pow((lightVal[2] - 15.69), 2));
			differencewithRed2 = Math.sqrt(Math.pow((lightVal[0] - 74.51), 2) + Math.pow((lightVal[1] - 11.76), 2) + Math.pow((lightVal[2] - 11.76), 2));
			differencewithRed3 = Math.sqrt(Math.pow((lightVal[0] - 34.31), 2) + Math.pow((lightVal[1] - 4.90), 2) + Math.pow((lightVal[2] - 4.90), 2));
			differencewithYellow1 = Math.sqrt(Math.pow((lightVal[0] - 146.08), 2) + Math.pow((lightVal[1] - 112.75), 2) + Math.pow((lightVal[2] - 112.75), 2));
			differencewithYellow2 = Math.sqrt(Math.pow((lightVal[0] - 97.06), 2) + Math.pow((lightVal[1] - 73.53), 2) + Math.pow((lightVal[2] - 73.53), 2));
			differencewithYellow3 = Math.sqrt(Math.pow((lightVal[0] - 48.04), 2) + Math.pow((lightVal[1] - 34.31), 2) + Math.pow((lightVal[2] - 34.31), 2));
			differencewithBlue1 = Math.sqrt(Math.pow((lightVal[0] - 14.71), 2) + Math.pow((lightVal[1] - 40.20), 2) + Math.pow((lightVal[2] - 40.20), 2));
			differencewithBlue2 = Math.sqrt(Math.pow((lightVal[0] - 9.80), 2) + Math.pow((lightVal[1] - 25.49), 2) + Math.pow((lightVal[2] - 25.49), 2));
			differencewithBlue3 = Math.sqrt(Math.pow((lightVal[0] - 2.94), 2) + Math.pow((lightVal[1] - 9.80), 2) + Math.pow((lightVal[2] - 9.80), 2));
			differencewithWhite1 = Math.sqrt(Math.pow((lightVal[0] - 185.29), 2) + Math.pow((lightVal[1] - 213.73), 2) + Math.pow((lightVal[2] - 213.73), 2));
			differencewithWhite2 = Math.sqrt(Math.pow((lightVal[0] - 104.90), 2) + Math.pow((lightVal[1] - 117.65), 2) + Math.pow((lightVal[2] - 117.65), 2));
			differencewithWhite3 = Math.sqrt(Math.pow((lightVal[0] - 62.75), 2) + Math.pow((lightVal[1] - 67.65), 2) + Math.pow((lightVal[2] - 67.65), 2));		
			if((differencewithRed1 < 10) || (differencewithRed2 < 10) || (differencewithRed3 <10)) {
				ifFound = 1;
			}else if((differencewithYellow1 < 10) || (differencewithYellow2 < 10) || (differencewithYellow3 <10)){
				ifFound = 2;
			}else if((differencewithBlue1 < 10) || (differencewithBlue2 < 10) || (differencewithBlue3 <10)) {
				ifFound = 3;
			}else if((differencewithWhite1 < 10) || (differencewithWhite2 < 10) || (differencewithWhite3 <10)) {
				ifFound = 4;
			}
			
		}
		// print to RSV file format up to two floating point precision
		System.out.printf("%.2f,%.2f,%.2f,%.2f,%d,%s\n",lightVal[0],lightVal[1],lightVal[1],Robot.getDistance()-1,ifFound,EXPECTED);

		return ifFound;
	}
	
	
}