package ca.mcgill.ecse211.lightsensor;

import ca.mcgill.ecse211.model.Robot;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LightSensor extends Thread {
	private LightSensorController cont;
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	private static SampleProvider colorProvider = colorSensor.getRGBMode(); // the sample size should be 3
	private static float[] color=new float[Robot.colorProvider.sampleSize()];
	private static float[] ambientColor = new float[colorProvider.sampleSize()];
	private static int[] ambientLight;
	
	// constructor here
	public LightSensor(LightSensorController cont) {
		this.cont=cont;
		// needs to fetch ambient light once and use it as a base for calcualtion
		colorProvider = colorSensor.getAmbientMode(); // the sample size should be 3
		ambientColor = new float[colorProvider.sampleSize()];
		colorProvider.fetchSample(ambientColor, 0);
		ambientLight[0]=(int) (ambientColor[0] * 100.0);
		System.out.println("Ambient light: "+ambientLight[0]);
		// switch mode of colorSensor to getRGBMode()
		colorSensor.getRGBMode();
	}
	
	public void run() {
		System.out.println("Sample size is: "+colorProvider.sampleSize());
		int lightVal[]=new int[3];
		while (true) {
			colorProvider.fetchSample(color, 0); // acquire data
			lightVal[0] = (int) (color[0] * 100.0); // get R value
			lightVal[1] = (int) (color[1] * 100.0); // get G value
			lightVal[2] = (int) (color[2] * 100.0); // get B value
			int tb=findColor();
			if(tb!=0) {
				cont.processLightData(tb);
			}else {
				// do nothing
			}
			try {
				// 10hz refresh rate
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int findColor() {
		// 1: Red, 2: Blue, 3: Yellow, 4: White
		int color=0;
		//ToDO: algorithm that determines the color
		
		return color;
	}
}
