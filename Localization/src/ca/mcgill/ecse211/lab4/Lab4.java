// Lab2.java
package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.model.*;

import ca.mcgill.ecse211.ultrasonic.UltrasonicController;
import ca.mcgill.ecse211.ultrasonic.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

public class Lab4 {

	public static Odometer odometer;
	public static Display odometryDisplay;

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;

		// Odometer related objects
		odometer = Odometer.getOdometer(Robot.leftMotor, Robot.rightMotor, Robot.TRACK, Robot.WHEEL_RAD);
		odometryDisplay = new Display(Robot.lcd);
		
		System.out.println("Remote user connected");
		do {
			// clear the display
			Robot.lcd.clear();
			// ask the user whether the motors should drive with obstacle avoidance
			Robot.lcd.drawString("< Left | Right >", 0, 0);
			Robot.lcd.drawString("L: Falling, R: Rising", 0, 1);
			Robot.lcd.drawString("Please select", 0, 2);
			// Record choice (left or right press)
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			System.out.println("Start Falling Edge");
			// update the robot model
			Robot.loc=Robot.LocalizationCategory.FALLING_EDGE;		
		} else {
			System.out.println("Start Rising Edge");
			// update the robot model
			Robot.loc=Robot.LocalizationCategory.RISING_EDGE;
		}
		// start all threads
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		Thread odoDisplayThread = new Thread(odometryDisplay);
		odoDisplayThread.start();
		UltrasonicPoller usPoller=new UltrasonicPoller(Robot.usDistance, Robot.usData, Robot.usController);
		usPoller.run();
		
		UltrasonicLocalizer usLocal=new UltrasonicLocalizer(odometer);
		// do something with localizer
		usLocal.localize();
		// beep twice when the localization finishes
		Sound.twoBeeps();
		Robot.lcd.clear();
		Robot.lcd.drawString("Complete Ultrasonic Loc", 0, 0);
		Robot.lcd.drawString("Press any Light Loc", 0, 1);
		
		// wait for button press to execute light localizer
		LightLocalizer lightLoc=new LightLocalizer(odometer);
		lightLoc.localize();
		Sound.twoBeeps();
		Robot.lcd.clear();
		Robot.lcd.drawString("Complete Light Loc", 0, 0);
		Robot.lcd.drawString("Finished", 0, 1);
		
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
