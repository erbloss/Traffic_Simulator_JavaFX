package application;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/*
 * This class defines a car object. It implements Runnable so that each
 * car may run in a separate thread.  Cars are represented as rectangles
 * and are associated with a list of traffic lights on the road.  
 * Some of the methods included:
 * 1) to determine if the upcoming closest traffic light's color is red
 * 2) to drive/move the car along the x-axis
 * 3) to change status flags in accordance with pause, resume, stop
 * 4) to implement the run() method so cars move on the GUI pane 
 *  
 *  Elizabeth Bloss
 *  UMGC, CMSC335    
 *  December 2024
 *  Project 3
 */

public class Car implements Runnable{
	
	private final int idNum;					// unique identification number
	private static int count = 0;				// counter to track car ID numbers
	private double x = 0;						// position on x axis
	private final int Y = 40; 					// cars only travel linear west to east
	private int topSpeed;						// consistent max speed of car in km/hr
	private int currentSpeed;					// stores the current speed of the car (topSpeed or 0);
	private Rectangle carDisplay;				// car represented as rectangle 
	private Text carNumDisplay;					// to label rectangles 
	private List<TrafficLight> trafficLights;	// set of traffic lights car may encounter
	
	private volatile boolean stop = false;				// flag
	private volatile boolean pause = false;				// flag
	private ReentrantLock lock = new ReentrantLock();	
	
	// constructor that accepts x-position, and list of traffic lights
	public Car(int x, List<TrafficLight> trafficLights) {
		this.x = x;
		this.trafficLights = trafficLights;
		drawCar();
		idNum = ++count;
		drawLabel(idNum);
		topSpeed = ThreadLocalRandom.current().nextInt(30,60);
		// each car with speed a random number between 30 and 60 km/hr
		currentSpeed = topSpeed;	
	}
	
	// *** GETTER METHODS ***
	public synchronized int getID() {
		return idNum;
	}
	public synchronized int getSpeed() {
		return currentSpeed;
	}
	public synchronized double getPosition() {
		return carDisplay.getX();
	}
	public synchronized Rectangle getRectangle() {
		return carDisplay;
	}
	public synchronized Text getCarLabel() {
		return carNumDisplay;
	}
	
	// *** FLAG METHODS ***
	public void stop() {
		if(pause) {
			Thread.currentThread().interrupt();
		}
		pause = false;
		stop = true;
	}
	public void pause() {
		synchronized(lock) {
			Thread.currentThread().interrupt();
			pause = true;
		}
	}
	public void resume() {
		synchronized(lock) {
			pause = false;
			lock.notifyAll();
		}
	}
	
	// *** RUN METHOD ***
	@Override
	public void run() {
		while(!stop){  			
			synchronized(lock) {
				while(pause) {	// handle pause
					try { 
						lock.wait();	
					} catch(InterruptedException ie) {
						Thread.currentThread().interrupt();
					}	
				}
			}
			try {
				Platform.runLater(() -> {	
					boolean isRed = checkIfRed();
					if(isRed) {
						currentSpeed = 0; 
						// brake when red light within 300 meters
					} else {
						currentSpeed = this.topSpeed;
					}
					drive();
			});
				Thread.sleep(10); 
			} catch(InterruptedException ie) {
				stop = true;
			}
		}
		// stop flag true
	}	
	
	// *** OTHER METHODS ***
	// method to move the car along x axis
	private synchronized void drive() {
		double newX = getPosition() + getSpeed()/30; 
								//divide by minimum speed to slow execution
		if(newX > 1000) { 		// car exceeds road boundary
			newX = 0; 			// restart car at beginning of road
		}
		carDisplay.setX(newX);
		carNumDisplay.setX(newX + 14);
	}
	
	// Method to check if upcoming closest traffic light is red
	// returns true if it is red and within range
	// returns false if yellow, green, null, or out of range
	// (within range = a car 0-300 meter distance from light)
	private synchronized boolean checkIfRed() {
		TrafficLight tempTL = null;
		for(TrafficLight t: trafficLights) {
			if(t != null) {
				int tX = t.getPosition();
				double cX = carDisplay.getX();
				if(cX < tX && (tX - cX < 30)) {
					// light is ahead of car
					// && within a car's length distance
					tempTL = t;
				}
			}
		}
		while(tempTL != null && tempTL.getColor() == TRAFFIC_LIGHT_COLOR.RED) {
			// set car and ID label to position of the red light
			// accounting for length of car being 30
			carDisplay.setX(tempTL.getPosition() - 30); 
			carNumDisplay.setX(tempTL.getPosition() -16);
			return true;
		} 
		return false;
	}
	
	// Method to draw the car as a rectangle
	private void drawCar() {
		carDisplay = new Rectangle(30, 20, Color.WHITE);   // construct rectangle 
		carDisplay.setStroke(Color.BLACK);
		carDisplay.setX(this.x); 						   // default start car at 0
		carDisplay.setY(Y);
	}
	
	// Method to add a numeric label to a car
	private void drawLabel(int idNum) {
		carNumDisplay = new Text("" + idNum);			   // label for car shape
		carNumDisplay.setY(Y + 15);
		carNumDisplay.setX(this.x + 14);
		carNumDisplay.setRotate(90);
		Font font = Font.font("verdana", FontWeight.BOLD, 12);
		carNumDisplay.setFont(font);
	}

	// Method to update the list of traffic lights when more are added
	public void updateLightList(List<TrafficLight> trafficLights) {
		this.trafficLights = trafficLights;
	}
	
	// Method to override to String method and return the car ID
	@Override
	public String toString() {
		return "Car " + this.getID();
	}

}
