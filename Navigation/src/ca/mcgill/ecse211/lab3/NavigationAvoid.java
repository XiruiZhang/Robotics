package ca.mcgill.ecse211.lab3;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.ultrasonic.UltrasonicController;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.robotics.RegulatedMotor;
/**
 * This class is used to drive the robot on the demo floor while avoiding obstacles
 */

// navigation class should not extends thread
public class NavigationAvoid extends Thread implements UltrasonicController{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static final double TILE_SIZE = 30.48;
	private int ODOMETER_PERIOD= 25;
	
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor,rightMotor;
	private EV3MediumRegulatedMotor usMotor;
	private double track;
	private double wr;
	
	private double xCurrent;
	private double yCurrent;
	private double thetaCurrent;
	private int distToWall;
	
	int state=0;
	double xOrigin=0;
	double yOrigin=0;
	double dirCompensation=-1;
	boolean isOffsetX=false;
	boolean isFinished=false;
	
	NavigationAvoid(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		  EV3MediumRegulatedMotor usMotor,double track,double wr) {
		this.odometer=odometer;
		this.rightMotor=rightMotor;
		this.leftMotor=leftMotor;
		this.usMotor=usMotor;
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
	
	@Override
	public void processUSData(int distance) {
		this.distToWall=distance;
		/*
		 * this state variable defines the state of robot
		 * -1: robot has not detected obstacle
		 * 1: robot moving x and avoiding
		 * 2: robot moving x and avoided
		 * 3: robot moving y and avoiding
		 * 4: robot moving y and avoided
		 * 5: robot moving back x and avoiding
		 * 
		 */

		LCD.drawString("Dist: "+distance, 0, 0);
		/*
		 * If the sensor detects the robot is
		 * within 5 cm of the block, make a immediate right turn of 90 degree
		 * and rotate usSensor 90 degree
		 */
		if(this.distToWall<5) {
			// when robot first detects obstacle
			if(state==-1) {
				turnTo(90);
				usMotor.rotate(90);
				xOrigin=odometer.getXYT()[0];
				yOrigin=odometer.getXYT()[1];
				// set the state to 1, robot changed direction
				state=1;
				// here causes the offset
			}else if(state==2) {
				// do nothing, the robot needs to keep moving y
				state=3;			
			}else if(state==4) {
				// compensate the distance back
				// check which direction
				if(isOffsetX) {
					dirCompensation=odometer.getXYT()[0]-xOrigin;
					moveForward(dirCompensation);
				}else {
					dirCompensation=odometer.getXYT()[1]-yOrigin;
					moveForward(dirCompensation);
				}
			}
		}else {
			// robot past obstacle in x
			if(state==1) {
				turnTo(-90);
				state=2;	
			}else if (state==3) {
				// check which direction it turned
				if(odometer.getXYT()[0]-xOrigin>3) {
					// extra x was moved
					isOffsetX=true;
				}else {
					isOffsetX=false;
				}
				turnTo(-90);
				state=4;
			}else if(state==5) {
				usMotor.rotate(-90);
				turnTo(90);
				if(isOffsetX) {
					moveForward(dirCompensation);
				}else {
					moveForward(dirCompensation);
				}
			}
		}
		
		
	}

	@Override
	public int readUSDistance() {
		return this.distToWall;
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
		isFinished=true;
		
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
	
	/* this method is only used to avoid obstacles
	 * it moves the robot forward in current direction 10cm
	 */
	public void moveForward(double linearDist) {
		/*
		 * Set acceleration to 3000
		 * the default speed is 6000
		 * this gives a smooth acceleration
		 */
		leftMotor.setAcceleration(600);
		rightMotor.setAcceleration(600);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.rotate(convertDistance(wr, linearDist), true);
		rightMotor.rotate(convertDistance(wr, linearDist), false);
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
  /*
   * This class helps return from the thread
   */
  public boolean isFinished() {
	  return isFinished;
  }
}
