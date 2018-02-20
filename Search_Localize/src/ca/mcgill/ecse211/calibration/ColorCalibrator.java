package ca.mcgill.ecse211.calibration;

import ca.mcgill.ecse211.model.Robot;

import ca.mcgill.ecse211.lightsensor.LightSensorController;

public class ColorCalibrator extends Thread {
	// modify this to match the color of the block
	public final String EXPECTED_COLOR="EMPTY";
	public final double DISTANCE=0;
	private LightSensorController cont;
	private static float[] color=new float[Robot.colorProvider.sampleSize()];
	private static float[] ambientColor = new float[Robot.colorProvider.sampleSize()];
	private static int[] ambientLight;
	
	
	// constructor here
	public ColorCalibrator(LightSensorController cont) {
		this.cont=cont;
		// needs to fetch ambient light once and use it as a base for calcualtion
		//Robot.colorProvider = Robot.colorSensor.getAmbientMode(); // the sample size should be 3
		//ambientColor = new float[Robot.colorProvider.sampleSize()];
		//Robot.colorProvider.fetchSample(ambientColor, 0);
		//ambientLight[0]=(int) (ambientColor[0] * 100.0);
		//System.out.println("Ambient light: "+ambientLight[0]);
		// switch mode of colorSensor to getRGBMode()
		//Robot.colorSensor.getRGBMode();
		System.out.println("========Below is rsv file=======");
		System.out.println("Red,Blue,Yellow,Distance,Actual_color");
	}
	
	public void run() {
		double lightVal[]=new double[3];
		int counter=0;
		while (true) {
			Robot.colorProvider.fetchSample(color, 0); // acquire data
			lightVal[0] = color[0] * 1000.0; // get R value
			lightVal[1] = color[1] * 1000.0; // get G value
			lightVal[2] = color[2] * 1000.0; // get B value
			// print to RSV file format up to two floating point precision
			System.out.printf("%.2f,%.2f,%.2f,%.2f,%s\n",lightVal[0],lightVal[1],lightVal[1],DISTANCE,EXPECTED_COLOR);
			
			// check if there us an object in front
			int tb=findColor();
			cont.processLightData(tb);
			
			counter++;
			try {
				// 10hz refresh rate
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int findColor() {
		// 1: Red, 2: Blue, 3: Yellow, 4: White
		int color=0;
		return color;
	}
}