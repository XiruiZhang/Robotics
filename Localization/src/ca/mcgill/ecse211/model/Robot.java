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
 * Specify sensor methods, drive methods
 * Define feedback mechanism
 *
 */
public class Robot {
	// basic data measurement of robot-specific data
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 15.8;
	public static final double TILE_SIZE = 30.48;
	private static final int FORWARD_SPEED = 100;
	private static final int ROTATE_SPEED = 50;
	public static double lsOffset=2.0; // distance of light sensor to wheel axis
	public static int usMotorAngle=0;
	
	// locType and state of the robot
	public enum LocalizationCategory {
		FALLING_EDGE,
		RISING_EDGE;
	};
	public static LocalizationCategory loc;
	
	// define motors
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor usMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	
	/**
	 * This method turns the robot to an angle specified by thetaDest
	 * @author jamestang
	 * @param thetaDest : the angle needs to be turned in radian
	 */
	public static void turnTo(double thetaDest) {
		/*
		 * Set acceleration to 3000 the default speed is 6000 this gives a smooth
		 * acceleration
		 */
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		thetaDest = Math.toDegrees(thetaDest);
		int rotationAngle = Util.convertAngle(WHEEL_RAD, TRACK, thetaDest);
		/*
		 * Initialize a synchronous action to avoid slip
		 */
		leftMotor.rotate(rotationAngle, true);
		rightMotor.rotate(-rotationAngle);
	}
	
	// define Ultrasonic sensor
	public static SensorModes usSensor = new EV3UltrasonicSensor(SensorPort.S4); // the instance
	public static SampleProvider usDistance = usSensor.getMode("Distance"); // provides samples from this instance
	public static float[] usData = new float[usDistance.sampleSize()];
	public static UltrasonicController usController;
	
	// define light sensor
	public static EV3ColorSensor colorSensor=new EV3ColorSensor(SensorPort.S4);
	public static SampleProvider colorProvider=colorSensor.getRedMode();
	
	// define textLCD
	public static TextLCD lcd = LocalEV3.get().getTextLCD();
	
	// define localization type of the robot
	
	public static TextLCD getLCD() {
		return lcd;
	}
	
	public void clearLCD() {
		lcd.clear();
	}
	
	
	
}