package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.model.Robot;
import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;

public class LightLocalizer {
	private Odometer odometer;
	private float[] color;
	private float lightVal=100;
	private static final int BLACK_THRESHOLD=10;
	private int angle=0;
	
	public LightLocalizer(Odometer odometer) {
		this.odometer=odometer;
		color=new float[Robot.colorProvider.sampleSize()];
	}
	
	public void localize() {
		// continue moving forward until first black line is found
		while(lightVal<BLACK_THRESHOLD) {
			Robot.leftMotor.setSpeed(100);
			Robot.rightMotor.setSpeed(100);
			Robot.leftMotor.setAcceleration(300);
			Robot.rightMotor.setAcceleration(300);
			Robot.leftMotor.forward();
			Robot.rightMotor.forward();
			
			Robot.colorProvider.fetchSample(color, 0);
			lightVal=color[0]*1000;
		}
		// stop immediately
		Robot.leftMotor.stop();
		Robot.rightMotor.stop();
		// beep once for update
		Sound.beep();
		
		// we will turn counter clockwise until corss another line
		while(lightVal<BLACK_THRESHOLD) {
			Robot.turnTo(angle);
			angle+=3;
			Robot.colorProvider.fetchSample(color, 0);
			lightVal=color[0]*1000;
		}
		
		angle=0;
		while(lightVal<BLACK_THRESHOLD) {
			Robot.turnTo(angle);
			angle-=3;
			Robot.colorProvider.fetchSample(color, 0);
			lightVal=color[0]*1000;
		}
		
		// add correction here
	}
	
}
