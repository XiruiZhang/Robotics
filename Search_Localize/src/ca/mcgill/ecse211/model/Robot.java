package ca.mcgill.ecse211.model;

import ca.mcgill.ecse211.ultrasonic.UltrasonicController;
import ca.mcgill.ecse211.util.*;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * 
 * @author jamestang
 * This class defines the robot as a model
 * 	Specify sensor methods, drive methods, define feedback mechanism
 *	======== Set up =========== 
 *	Sensors:
 *	Ultrasonic sensor: Sensor Port S4
 *	Block light sensor (forward facing): Sensor Port S2
 *	Floor light sensor (downward facing): Sensor Port S3
 *	
 *	Motors:
 *	Left motor: Motor Port D
 *	Right motor: Motor Port B
 *	Ultrasonic base motor: Motor Port C
 */
public class Robot {
	// basic data measurement of robot-specific data
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 15.8;
	public static final double TILE_SIZE = 30.48;
	private static final int FORWARD_SPEED = 50;
	private static final int ROTATE_SPEED = 50;
	public static double lsOffset=2.0; // distance of light sensor to wheel axis
	public static int usMotorAngle=0;
	private static double OFF_CONST=1.02;
	public static DriveState driveState;
	public static LocalizationCategory loc;
	
	// locType and state of the robot
	public enum LocalizationCategory {
		NONE,
		FALLING_EDGE,
		RISING_EDGE;
	};
	// the state of the robot's drive system
	public enum DriveState {
		STOP,
		FORWARD,
		BACKWARD,
		TURN,
		TRAVEL;
	};
	/**
	 * This method initialize the robot into original state
	 */
	public static int init() {
		driveState=DriveState.STOP;
		loc=LocalizationCategory.NONE;
		return 1;
	}
	
	
	// define motors
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor usMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	/**
	 * This method put the robot in a fixed speed drive forward
	 */
	public static void driveForward() {
		leftMotor.setSpeed(50);
		rightMotor.setSpeed(50);
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * This method put the robot in a fixed speed drive backward
	 */
	public static void driveBackward() {
		leftMotor.setSpeed(50);
		rightMotor.setSpeed(50);
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * This method turn the robot continuously in place
	 * @param int direction: -1: turn left, 1: turn right
	 */
	public static void turn(String direction) {
		Robot.leftMotor.setSpeed(50);
		Robot.rightMotor.setSpeed(50);
		Robot.leftMotor.setAcceleration(50);
		Robot.rightMotor.setAcceleration(50);
		
		if(direction=="LEFT") {
			Robot.leftMotor.forward();
			Robot.rightMotor.backward();
		}else if(direction=="RIGHT") {
			Robot.leftMotor.backward();
			Robot.rightMotor.forward();
		}
	}
	
	/**
	 * This method puts the robot to a synchronized stop
	 */
	public static void stop() {
		Robot.leftMotor.stop(true);
		Robot.rightMotor.stop(false);
	}
	
	/**
	 * This method turns the robot to an angle specified by thetaDest
	 * @param thetaDest : the angle needs to be turned in radian
	 */
	public static void turnTo(double thetaDest) {
		/*
		 * Set acceleration to 3000 the default speed is 6000 this gives a smooth
		 * acceleration
		 */
		thetaDest=thetaDest/OFF_CONST;
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		thetaDest = Math.toDegrees(thetaDest);
		int rotationAngle = robotUtil.convertAngle(WHEEL_RAD, TRACK, thetaDest);
		/*
		 * Initialize a synchronous action to avoid slip
		 */
		leftMotor.rotate(rotationAngle, true);
		rightMotor.rotate(-rotationAngle);
	}
	
	/**
	 * This method drives robot to a specific location
	 * @author jamestang
	 * @param xDest: x coordinate in cm
	 * @param yDest: y coordinate in cm
	 */
	public static void travelTo(double linearDistance) {
		// move the linear distance possible improvement here
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(robotUtil.convertDistance(WHEEL_RAD, linearDistance), true);
		rightMotor.rotate(robotUtil.convertDistance(WHEEL_RAD, linearDistance), false);
	}
	/**
	 * This method drives robot to a specific location
	 * @param xDest: x coordinate in cm
	 * @param yDest: y coordinate in cm
	 */
	public static void travelTo(double xCurrent,double yCurrent,double thetaCurrent,double xDest,double yDest) {
		// convert coordinate to length
		System.out.println("In the model: xDest,yDest"+xDest+" "+yDest);
		double dX=xDest-xCurrent;
		double dY=yDest-yCurrent;
		double linearDistance=robotUtil.getLinearDistance(dX, dY);
		/*
		 * Calculate the angle using tangent
		 */
		double angularDistance=Math.atan2(dX,dY)-Math.toRadians(thetaCurrent);

		System.out.println("Angle before conversion: "+angularDistance);
		/*
		 * If needs to turn more than 180 degree
		 * turn the other way instead
		 */
		if(angularDistance>Math.PI) {
			// the angular disance will be negaive
			angularDistance = angularDistance-2*Math.PI;
		}else if(angularDistance < (-Math.PI)) {
			// if needs to turn more than -180 degree
			angularDistance = angularDistance+2*Math.PI;
		}
		turnTo(angularDistance);
		// move the linear distance possible improvement here
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(robotUtil.convertDistance(WHEEL_RAD, linearDistance), true);
		rightMotor.rotate(robotUtil.convertDistance(WHEEL_RAD, linearDistance), false);
	}
	
	// define Ultrasonic sensor
	public static SensorModes usSensor = new EV3UltrasonicSensor(SensorPort.S4); // the instance
	public static SampleProvider usDistance = usSensor.getMode("Distance"); // provides samples from this instance
	public static float[] usData = new float[usDistance.sampleSize()];
	public static UltrasonicController usController;
	
	// define light sensor
	public static EV3ColorSensor colorSensor=new EV3ColorSensor(SensorPort.S2);
	public static SampleProvider colorProvider=colorSensor.getRGBMode();
	private static float[] color=new float[Robot.colorProvider.sampleSize()];
	
	// define floor light sensor
	public static EV3ColorSensor floorColorSensor=new EV3ColorSensor(SensorPort.S3);
	public static SampleProvider floorColorProvider=colorSensor.getRedMode();
	
	/**
	 * This method fetch the color value from light sensor
	 * @return float: the value of light from light sensor
	 */
	public static float getColor() {
		Robot.colorProvider.fetchSample(color, 0);
		float lightVal=color[0]*1000;
		return lightVal;
	}
	
	/**
	 * This method fetch the color value from floor light sensor
	 * @return float: the value of light from light sensor
	 */
	public static float getFloorColor() {
		Robot.floorColorProvider.fetchSample(color, 0);
		float lightVal=color[0]*1000;
		return lightVal;
	}
	// define textLCD
	public static TextLCD lcd = LocalEV3.get().getTextLCD();
	
	// define localization type of the robot
	
	public static TextLCD getLCD() {
		return lcd;
	}
	
	/**
	 * Run diagonostic  on the robot
	 * @return integer, 0: normal, 1: faulty
	 */
	public static int runDiagonistic() {
		int ret=1;
		if(leftMotor.isStalled()) {
			System.out.println("Left motor is stalled");
			ret=1;
		}else if (rightMotor.isStalled()) {
			System.out.println("Right motor is stalled");
			ret=1;
		}else if(usMotor.isStalled()) {
			System.out.println("US motor is stalled");
		}
		return ret;		
	}
	
	public void clearLCD() {
		lcd.clear();
	}
}