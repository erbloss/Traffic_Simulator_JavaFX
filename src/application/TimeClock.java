package application;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.scene.control.TextField;
/*
 * This class defines the time clock for the simulation application.
 * It implements Runnable.
 * The text field of the GUI application will be updated at 1 second 
 * intervals with the current elapsed time in seconds.
 * 
 *  Elizabeth Bloss
 *  UMGC, CMSC335    
 *  December 2024
 *  Project 3
 */

public class TimeClock implements Runnable {

	private static int timeCounter;  	// in seconds
	private int elapsedCurrent = 0;		// holds the currently elapsed amount of time
	private volatile boolean stop = false;    
	private volatile boolean pause = false;
	private TextField tf = new TextField();
	private ReentrantLock lock = new ReentrantLock();
	
	// constructor that accepts a Text Field as parameter 
	public TimeClock(TextField tf) {
		this.tf = tf;
		tf.setText("0 seconds");
	}
	
	// default constructor
	public TimeClock() {
	}

	// *** GETTER METHOD ***
	// returns the total elapsed time in seconds
	synchronized int getElapsedTime() {	
		return elapsedCurrent;
	}
	
	// *** FLAG METHODS ***
	synchronized void stop() {
		if(pause) {
			Thread.currentThread().interrupt();
		}
		pause = false;
		stop = true;
	}
	public void pause() {
		synchronized(lock) {
			pause = true;
		}
	}
	public void resume() {
		synchronized(lock) {
			pause = false;
			lock.notify();
		}
	}
	
	// *** RUN METHOD *** 
	// to increase counter by 1 then sleep 1 second
	// sets Text Field with elapsed time
	@Override
	public void run() {
		while(!stop) {
			synchronized(lock) {
				while(pause) {
					try { lock.wait();
					} catch(InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
			}
			try {
				elapsedCurrent = timeCounter++;
				Platform.runLater(()->{
					tf.setText(elapsedCurrent + " seconds");
				});
				Thread.sleep(1000); // sleep 1 second
			} catch(InterruptedException ie) {
				System.out.println(ie);
			}
		}
		// stop requested
		System.out.println("A timer thread has been stopped");
	}

}
