package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  
  // define class variables
  private int distance;
  private int prevDist;
  public static final int allowedDistance = 30;
  public static final int allowedDeviation =3;
  public static final int stopDistance = 15;
  public int distError=0;
  
  // define lcd object
  public static TextLCD t = LocalEV3.get().getTextLCD();
  
  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
	// the robot mains a counter-clockwise rotation  
	this.distance = distance;
    // validate distance and mare sure the distance is not invalid
	// if distance is above tolerence, use last usable data instead
	// test 0: status: not tested
	// situation: general
	// response: no distance is above 2000
	
	if(isDistanceValid(distance)==true) {
    		//if distance is not above 20000
    		prevDist=distance;
    }else {
    		distance=prevDist;
    }
	// measure how far off the sensor is from the allowedDistance
	distError = allowedDistance - distance;
	// apply corrections using speed
    if (distance<=stopDistance) {
    		/*
    		// apply emergency course alteration to avoid crash
    		// test 1: status: not tested
    		// situation: sensor value <= 15 response: 
    		// response: right Motor rotates backward 2 circles
    		 *
    		 */
    		WallFollowingLab.leftMotor.rotate(-360);
    		WallFollowingLab.rightMotor.rotate(-1080);
    		//WallFollowingLab.leftMotor.backward();
    		//WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		updateStatus("Avoiding crash", distance,-1,-2);
  	}else if(Math.abs(distError)<=allowedDeviation) {
  		/*
  		// maintain current course
		// test 2: status: not tested
		// situation: sensor value between 27 and 33 
		// response: move straight
		 * 
		 */
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
    		WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        updateStatus("Go straight", distance,motorHigh,motorHigh);
    		//Printer.updateLCD("Distance; "+distance+"Turn right");
    }else if(distError >0) {
    		/*	
    		// turn right
		// test 3: status: not tested
		// situation: sensor value between 15 and 27 
		// response: turn right
		 * 
		 */
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorLow);
    		WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        updateStatus("Turn right", distance,motorHigh,motorLow);
    		//Printer.updateLCD("Distance; "+distance+"Move Straight");
    }else if(distError<=0){
    		/*
    		// turn left
		// test 3: status: not tested
		// situation: sensor value >=33 
		// response: turn left
		 */
    		WallFollowingLab.leftMotor.setSpeed(motorLow);
		WallFollowingLab.rightMotor.setSpeed(motorHigh);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
        updateStatus("Turn left", distance,motorLow,motorHigh);
    		//Printer.updateLCD("Distance; "+distance+"Turn Left");
    }
  }
  
  /*
   * @see ca.mcgill.ecse211.wallfollowing.UltrasonicController#readUSDistance()
   * read Utrasonic sensor distance 
   */
  @Override
  public int readUSDistance() {
    return this.distance;
  }
  
  /*
   *  based on obserrvation and testing in lab
   *  value above 20000 is due to sensor error
   */
  public boolean isDistanceValid(int distance) {
	  // false situation
	  if(distance>=20000) {
		  return false;
	  }
	  return true;
  }
  /* 
   * a class that prints to LCD
   */
  public static void updateStatus(String update,int distance2,int lSpeed,int rSpeed) {
	  t.clear();
	  t.drawString("Status: "+update, 0, 0);
	  t.drawString("Distance: "+distance2, 0, 1);
	  t.drawString("Left Speed: "+lSpeed, 0, 2);
	  t.drawString("Right Speed: "+rSpeed, 0,3);
	  t.drawString("------------------", 0,4);
  }
}
