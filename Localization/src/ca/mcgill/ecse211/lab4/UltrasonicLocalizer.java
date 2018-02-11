package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.model.Robot;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.ultrasonic.UltrasonicController;
import lejos.hardware.Sound;

public class UltrasonicLocalizer implements UltrasonicController{
	private int dist;
	// assume of robot is placed at original, the maximum usable distance used for localizalition
	private double maxD=Robot.TILE_SIZE*Math.sqrt(2);
	private double lAngle=0;
	private double rAngle=0;
	private Odometer odometer;
	// this array holds all 
	private int[][] env=new int[180][2];
	public UltrasonicLocalizer(Odometer odometer) {
		this.odometer=odometer;
	}
	/**
	 * This method localizes the robot 
	 */
	public void localize() {
		// setAcceleration and speed to very slow for accurate reading
		Robot.usMotor.setAcceleration(50);
		Robot.usMotor.setSpeed(50);
		// normalize the motor to 0 degree
		Robot.usMotor.rotateTo(0);
		Robot.usMotorAngle=0;
		
		// falling edge: robot is facing away from the wall
		if(Robot.loc==Robot.LocalizationCategory.FALLING_EDGE) {
			// the robot detects a falling edge, switch direction and detect another falling edge
			while(dist>maxD-1) {
				// find the left wall
				double thetaCurrent=this.odometer.getTheta();
				Robot.turnTo(thetaCurrent-1);
			}
			lAngle=this.odometer.getTheta();
			
			// try to find the other falling edge
			while(dist>maxD-1) {
				// find the left wall
				double thetaCurrent=this.odometer.getTheta();
				Robot.turnTo(thetaCurrent+1);
			}
			rAngle=this.odometer.getTheta();			
			// after both angle has been found
			Robot.turnTo(rAngle-lAngle);
			
		}else if(Robot.loc==Robot.LocalizationCategory.RISING_EDGE) {
			// the robot detects a rising edge, switch direction and detect another falling edge
						while(dist<maxD-1) {
							// find the left wall
							double thetaCurrent=this.odometer.getTheta();
							Robot.turnTo(thetaCurrent+1);
						}
						lAngle=this.odometer.getTheta();
						
						// try to find the other falling edge
						while(dist<maxD-1) {
							// find the left wall
							double thetaCurrent=this.odometer.getTheta();
							Robot.turnTo(thetaCurrent-1);
						}
						rAngle=this.odometer.getTheta();			
						// after both angle has been found
						Robot.turnTo(rAngle-lAngle);
		}else {
			// the category is not set, abort
			Robot.lcd.clear();
			Robot.lcd.drawString("Loc check failed, abort", 0, 0);
		}
		//verify finally
		//verifyLocalization();
		// reset theta
		this.odometer.setTheta(0);
		Sound.beep();
	}
	
	/*
	/**
	 * This method helps to verify and correct the robot again assuming
	 * the robot is already close to 0 degree axis
	 
	public void verifyLocalization() {
		// find distance to left and right wall
		int lDist=0;
		int rDist=0;
		Robot.usMotor.rotate(-90);
		lDist=dist;
		Robot.usMotor.rotate(180);
		rDist=dist;
		
		if(lDist-rDist<3) {
			// no correction is necessary
		}else {
			// needs to apply correction again
			if(lDist>rDist) {
				Robot.turnTo(-5);
			}else {
				Robot.turnTo(5);
			}
		}
	}
	*/
	@Override
	public void processUSData(int distance) {
		this.dist=distance;
	}
	
	/**
	 * @return ultrasonic sensor reading
	 */
	@Override
	public int readUSDistance() {
		return this.dist;
	}
	

	
	
}
