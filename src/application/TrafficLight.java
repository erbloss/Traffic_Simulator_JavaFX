package application;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * This class defines the simulation of a traffic light that
 * uses enumerated type for light color options. 
 * Each color has a specific time frame that it is employed for.
 *  
 *  Elizabeth Bloss
 *  UMGC, CMSC335    
 *  December 2024
 *  Project 3
 */

enum TRAFFIC_LIGHT_COLOR { RED, YELLOW, GREEN}

public class TrafficLight implements Runnable{

	private TRAFFIC_LIGHT_COLOR tlColor;  	// current color of light
	private final int METERS_TO_NEXT = 100;	// 1000 meters between all traffic lights on a 1:10 scale
	private int x; 					  	 	// x position of traffic light
	private static int currentX = 0;		// keep track of previous light placement
	private int idNum;						// unique identification number
	private static int count = 0;			// keep track of how many traffic lights
	
	private volatile boolean stop = false;  // flag
	private volatile boolean pause = false;	// flag
	private boolean changed = false;	 	// flag
	private ReentrantLock lock = new ReentrantLock();
	Thread thread;
	private Circle circle;					// represent traffic light as circle
	
	
	// default constructor sets light to red
	public TrafficLight(){
		tlColor = TRAFFIC_LIGHT_COLOR.GREEN;
		this.idNum = ++count;
		currentX = currentX + METERS_TO_NEXT; 
		this.x = currentX;
		drawLight();
	}	
	// constructor accepts initial light color as parameter
	public TrafficLight(TRAFFIC_LIGHT_COLOR color){
		this.tlColor = color;
		this.idNum = ++count;
		currentX = currentX + METERS_TO_NEXT; 
		this.x = currentX;
		drawLight();
		changeColor();
	}	
	
	// *** GETTER METHODS ***
	public synchronized int getID() {
		return idNum;
	}
	public synchronized TRAFFIC_LIGHT_COLOR getColor() {
		return tlColor;
	}
	public synchronized int getPosition() {
		return x;
	}
	public Circle getCircle() {
		return circle;
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
	public void run() {
		while(!stop) { 
			// handle pause
			synchronized(lock) {
				while(pause){
					try {
						lock.wait();
					} catch(InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
			try {
				switch(tlColor) {
					case GREEN:
						Thread.sleep(5000); // 5 seconds 
						break;
					case YELLOW:
						Thread.sleep(2000); // 2 second
						break;
					case RED:
						Thread.sleep(5000); // 5 seconds
						break;
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				pause = true;	
				
			}
			changeColor();
		}
		// stop flag true
	}
	
	// *** OTHER METHODS ***
	
	// synchronized method to change the color of the traffic light
	synchronized void changeColor() {
		switch(tlColor) {
		case RED:
			tlColor = TRAFFIC_LIGHT_COLOR.GREEN;
			circle.setFill(Color.GREEN);
			break;
		case YELLOW:
			tlColor = TRAFFIC_LIGHT_COLOR.RED;
			circle.setFill(Color.RED);
			break;
		case GREEN:
			tlColor = TRAFFIC_LIGHT_COLOR.YELLOW;
			circle.setFill(Color.YELLOW);
		}
		changed = true;
		notify();  // signals that the light has changed
	}
	
	// synchronized method to wait until a light change occurs
	synchronized void waitForChange() {
		try {
			while(!changed)
				wait(); // waiting for light to change
			changed = false; // reset
		} catch(InterruptedException ie) {
			System.out.println(ie);
		}
	}
	
	// method to create the circle object
	private void drawLight() {
		circle = new Circle(15, Color.GREEN);
		circle.setCenterX(x);
		circle.setCenterY(35); // mid-way for road 70 wide
		circle.setStroke(Color.WHITE);
	}
	
	// method to return a string representation of the intersection
	@Override
	public String toString() {
		return "Intersection " + this.getID();
	}

}
