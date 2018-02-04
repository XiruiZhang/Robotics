/*
 * SquareDriver.java
 */
package ca.mcgill.ecse211.lab3;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;

/**
 * This class is used to drive the robot on the demo floor.
 */

// navigation class should not extends thread
public class Navigation{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static final double TILE_SIZE = 30.48;
	private int ODOMETER_PERIOD= 25;
	
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor,rightMotor;
	private double track;
	private double wr;
	
	private double xCurrent;
	private double yCurrent;
	private double thetaCurrent;
	
	Navigation(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		  double track,double wr) {
		this.odometer=odometer;
		this.rightMotor=rightMotor;
		this.leftMotor=leftMotor;
		this.track=track;
		this.wr=wr;
		}
	
	// thread behavior
	public void run() {
		long updateStart, updateEnd;
		while (true) {
			updateStart = System.currentTimeMillis();
			// the thead fetches current x, y and theta
			getCurrentPos();
			
			// this ensures that the odometer only runs once every period
		      updateEnd = System.currentTimeMillis();
		      if (updateEnd - updateStart < ODOMETER_PERIOD) {
		        try {
		          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
		        } catch (InterruptedException e) {
		          e.printStackTrace();
		        }
		      }
		}
	}
	/* this method drives the robot to a position specified
	 * as x and y
	 */
	public void travelTo(double xDest,double yDest) {
		// convert coordinate to length
		xDest=xDest * TILE_SIZE;
		yDest=yDest * TILE_SIZE;
		
		double dX=xDest-xCurrent;
		double dY=yDest-yCurrent;
		double linearDistance=getLinearDistance(dX, dY);
		/*
		 * Calculate the angle using tangent
		 */
		double angularDistance=Math.atan2(dX,dY)-thetaCurrent;
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
		// synchronous turn to the desired degree
		turnTo(angularDistance);
		// move the linear distance
		leftMotor.setAcceleration(600);
		rightMotor.setAcceleration(600);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.rotate(convertDistance(wr, linearDistance), true);
		rightMotor.rotate(convertDistance(wr, linearDistance), false);
		leftMotor.endSynchronization();
		
	}
	
	/* this method supplements the travelTo method
	 * it turns the robot in place into the right direction before moving
	 */
	public void turnTo(double thetaDest) {
		/*
		 * Set acceleration to 3000
		 * the default speed is 6000
		 * this gives a smooth acceleration
		 */
		leftMotor.setAcceleration(300);
		rightMotor.setAcceleration(300);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		thetaDest=Math.toDegrees(thetaDest);
		int rotationAngle=convertAngle(wr, track, thetaDest);
		/*
		 * Initialize a synchronous action to avoid slip
		 */
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.rotate(rotationAngle, true);
		rightMotor.rotate(-rotationAngle);
		leftMotor.endSynchronization();
	}
	
	/*
	 * this method fetches the x and y position from odometer
	 */
	private void getCurrentPos() {
		/* 
		 * changed the visibility of lock in odometer class to public
		 * here the method locks up the thead before accessing data
		 */
		synchronized (odometer.lock) {
			this.thetaCurrent=odometer.getXYT()[2];
			this.xCurrent=odometer.getXYT()[0];
			this.yCurrent=odometer.getXYT()[1];
		}
	}
	
  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
  private static double getLinearDistance(double x,double y) {
	  return Math.hypot(x, y);
  }
}
