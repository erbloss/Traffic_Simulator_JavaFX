package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
/*
 * This class defines the main GUI application of a traffic simulator program.
 * The GUI includes buttons to loop through the simulation 
 * by starting, pausing, continuing, and stopping.  
 * There is a time stamp marking 1 second intervals.
 * There is a road view pane included to display real time movement.
 * There is also the ability to add up to 10 cars and 10 intersections.
 *  
 *  Elizabeth Bloss
 *  UMGC, CMSC335    
 *  December 2024
 *  Project 3
 */

public class Main extends Application{
	
	
	private ObservableList<Car> cars = FXCollections.observableArrayList();
	private ObservableList<TrafficLight> trafficLights = FXCollections.observableArrayList();
	private ListView<Car> carLV = new ListView<Car>(); 
	private ListView<TrafficLight> lightLV = new ListView<TrafficLight>();
	private TextField timeTF;
	private TimeClock time;	
	private RoadView roadView;
	private boolean pause = false;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//###########################################################
			// 						BUILD GUI
			//###########################################################
			primaryStage.setTitle("Project 3 Application");
			BorderPane bpane = new BorderPane();
			bpane.setPadding(new Insets(30, 30, 30, 30));
			Scene scene = new Scene(bpane,1080,600);
			
			// set font styles
			Font header = Font.font("verdana", FontWeight.BOLD, 16);
			Font sub = Font.font("verdana", FontWeight.BOLD, 12);
		
			// define and style buttons
			Button startB = new Button("Start");
			startB.setPrefSize(200, 25);
			startB.setStyle("-fx-background-color: Green");
			startB.setTextFill(Color.WHITE);
			startB.setFont(sub);
			
			Button stopB = new Button("Stop");
			stopB.setPrefSize(200, 25);
			stopB.setStyle("-fx-background-color: Red");
			stopB.setTextFill(Color.WHITE);
			stopB.setFont(sub);
			stopB.setDisable(true);
			
			Button pauseB = new Button("Pause");
			pauseB.setPrefSize(200, 25);
			pauseB.setStyle("-fx-background-color: Gold");
			pauseB.setTextFill(Color.WHITE);
			pauseB.setFont(sub);
			pauseB.setDisable(true);
			
			Button continueB = new Button("Continue");
			continueB.setPrefSize(200, 25);
			continueB.setStyle("-fx-background-color: Blue");
			continueB.setTextFill(Color.WHITE);
			continueB.setFont(sub);
			continueB.setDisable(true);
			
			Button addCarB = new Button("Add Car");
			addCarB.setPrefSize(200, 10);
			addCarB.setDisable(true);
			addCarB.setTooltip(new Tooltip("Add up to 10 cars"));
			
			Button addLightB = new Button("Add Intersection");
			addLightB.setPrefSize(200, 10);
			addLightB.setDisable(true);
			addLightB.setTooltip(new Tooltip("Add up to 10 traffic lights"));
			
			// set size of list views for cars and traffic lights
			carLV.setMaxSize(200,155);
			lightLV.setMaxSize(200, 155);
			
			// set up timer text field using TimeClock object
			Label l1 = new Label("\n\nELAPSED TIME:");
			l1.setFont(sub);
			timeTF = new TextField();
			timeTF.setEditable(false);
			timeTF.setPrefWidth(200);
			timeTF.setMaxWidth(200);
			time = new TimeClock(timeTF);
			
			// define text for output areas
			Label l2 = new Label("CAR #: ");
			TextField carIDTF = new TextField("-----");		// car selected
			carIDTF.setEditable(false);
			Label l3 = new Label("POSITION: ");
			TextField posTF = new TextField("-----");		// position of selected car
			posTF.setEditable(false);
			Label l4 = new Label("SPEED (km/hr): ");
			TextField speedTF = new TextField("-----");		// speed of selected car
			speedTF.setEditable(false);
			Label l5 = new Label("INTERSECTION #: ");
			TextField interIDTF = new TextField("-----");	// intersection selected
			interIDTF.setEditable(false);
			Label l6 = new Label("CURRENT LIGHT COLOR: ");
			TextField colorTF = new TextField("-----");		// current light status
			colorTF.setEditable(false);
			Label l7 = new Label("POSITION: ");				// position of selected intersection
			TextField iPosTF = new TextField("-----");
			iPosTF.setEditable(false);
			
			// create and fill V Boxes
			VBox vb1 = new VBox(10);	// for left side
			VBox vb2 = new VBox(10);	// for center
			VBox vb3 = new VBox(10);	// for right
			vb1.setAlignment(Pos.CENTER);
			vb2.setAlignment(Pos.CENTER_LEFT);
			vb3.setAlignment(Pos.CENTER_LEFT);
			Label sLabel = new Label("TRAFFIC SIMULATION");
			Label cLabel = new Label("CARS:");
			Label iLabel = new Label("INTERSECTIONS:");
			sLabel.setFont(header);
			cLabel.setFont(header);
			iLabel.setFont(header);
			vb1.setPadding(new Insets(5, 50, 5, 5));
			vb2.setPadding(new Insets(5, 5, 5, 5));
			vb3.setPadding(new Insets(5, 5, 5, 5));
			
			vb1.getChildren().addAll(sLabel, startB, pauseB, continueB, stopB, 
					l1, timeTF);
			vb2.getChildren().addAll(cLabel, addCarB, carLV, l2, carIDTF, l4, 
					speedTF, l3, posTF, l5);
			vb3.getChildren().addAll(iLabel, addLightB, lightLV, l5, interIDTF, 
					l6, colorTF, l7, iPosTF);
			
			// h box to be populated by all V boxes
			HBox hb1 = new HBox(10);
			hb1.setAlignment(Pos.CENTER);
			hb1.getChildren().addAll(vb1, vb2, vb3);
			bpane.setCenter(hb1);
			
			// create road view pane and add to bottom of border pane
			roadView = new RoadView();
			bpane.setBottom(roadView);
			
			//#############################################################
			// 					HANDLE EVENTS         
			//#############################################################
			
			// BUTTON EVENTS
			// START BUTTON **************
			startB.setOnAction(e ->{
				// enable/disable appropriate buttons
				startB.setDisable(true); 
				stopB.setDisable(false);
				pauseB.setDisable(false);
				addCarB.setDisable(false);
				addLightB.setDisable(false);
				
				// create and start timer thread
				Thread t1 = new Thread(time);
				t1.start();
				
				// initialize 3 traffic light objects and add to list view 
				TrafficLight tl1 = new TrafficLight(TRAFFIC_LIGHT_COLOR.RED);
				TrafficLight tl2 = new TrafficLight(TRAFFIC_LIGHT_COLOR.GREEN);
				TrafficLight tl3 = new TrafficLight(TRAFFIC_LIGHT_COLOR.YELLOW);
				trafficLights.add(tl1);
				trafficLights.add(tl2);
				trafficLights.add(tl3);
				lightLV.setItems(trafficLights);
			
				// initialize 3 car objects and add to list view 
				Car car1 = new Car(0, trafficLights);
				Car car2 = new Car(100, trafficLights); 
				Car car3 = new Car(200, trafficLights);
				
				// add cars and traffic lights to the road view pane
				showCar(car1);
				showCar(car2);
				showCar(car3);
				roadView.getChildren().addAll(
					tl1.getCircle(), tl2.getCircle(), tl3.getCircle());
				
				// create and start threads for traffic lights and cars
				Thread light1 = new Thread(tl1);
				Thread light2 = new Thread(tl2);
				Thread light3 = new Thread(tl3);
				Thread c1 = new Thread(car1);
				Thread c2 = new Thread(car2);
				Thread c3 = new Thread(car3);
				light1.start();
				light2.start();
				light3.start();
				c1.start();
				c2.start();
				c3.start();
			});
			
			// PAUSE BUTTON ****************
			pauseB.setOnAction(e ->{
				pauseB.setDisable(true);
				continueB.setDisable(false);
				pauseThreads();			
			});
			
			// CONTINUE BUTTON *************
			continueB.setOnAction(e ->{		
				continueB.setDisable(true);
				pauseB.setDisable(false);
				resumeThreads();
			});
			
			// STOP BUTTON *****************
			stopB.setOnAction(e ->{			
				stopB.setDisable(true);
				pauseB.setDisable(true);
				continueB.setDisable(true);
				addCarB.setDisable(true);
				addLightB.setDisable(true);
				stopThreads();
				System.exit(0);
			});
			
			// ADD CAR BUTTON **************
			addCarB.setOnAction(e ->{		
				if(cars.size() > 9) {		// max 10 cars
					addCarB.setDisable(true);
				} else {
					Car newCar = new Car(0, trafficLights);
					showCar(newCar);
					Thread t = new Thread(newCar);
					t.start();	
					if(pause) {
						// program is in pause state when car is added
						newCar.pause();
					}
				}
			});			
			
			// ADD INTERSECTION BUTTON *****
			addLightB.setOnAction(e ->{ 
				if(trafficLights.size() < 10) { // max 10 lights
					TrafficLight newLight = new TrafficLight();
					trafficLights.add(newLight);
					lightLV.setItems(trafficLights);
					roadView.getChildren().add(newLight.getCircle());
					for(Car c: cars) {
						// add new light to the traffic light list of each car
						c.updateLightList(trafficLights);
					}
					Thread t = new Thread(newLight);
					t.start();
					if(pause) {
						// program is in pause state when light is added
						newLight.pause();
					}
				} else {
					addLightB.setDisable(true);
				}
			});
			
			// LIST VIEW EVENTS
			// Cars
			carLV.setOnMouseClicked(e ->{	// CAR LIST VIEW
				try {
					Car currentCar = carLV.getSelectionModel().getSelectedItem();
					synchronized(currentCar) {
						carIDTF.setText("Car " + currentCar.getID());
						speedTF.setText(currentCar.getSpeed() + " km/hr");
						posTF.setText((int)currentCar.getPosition() + "0 meters");
					}
				} catch (NullPointerException npe) {
				}
			});
			
			// Intersections
			lightLV.setOnMouseClicked(e ->{	// INTERSECTION LIST VIEW	
				try {					
					TrafficLight currentTL = 
						lightLV.getSelectionModel().getSelectedItem();
					synchronized(currentTL) {
						interIDTF.setText("Intersection " + currentTL.getID());
						colorTF.setText("" + currentTL.getColor());	
						iPosTF.setText(currentTL.getPosition() + "0 meters");
					}
				} catch (NullPointerException npe) {
				}
			});
			
			//#################################################################
			// 							SHOW STAGE
			//#################################################################
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	//#########################################################################
	//						OTHER METHODS
	//#########################################################################
	
	// Method to terminate all threads for cars, traffic lights, and timer
	private void stopThreads() {
		for (int i=0; i < trafficLights.size(); i++) {
			if(trafficLights.get(i) != null) {
				trafficLights.get(i).stop();
			}
		}
		for(int i=0; i < cars.size(); i++) {
			if(cars.get(i) != null) {
				cars.get(i).stop();
			}
		}
		time.stop();
	}
	
	// Method to pause all threads for cars, traffic lights, and timer
	private void pauseThreads() {
		pause = true;
		for (int i=0; i < trafficLights.size(); i++) {
			if(trafficLights.get(i) != null) {
				trafficLights.get(i).pause();
			}
		}
		for(int i=0; i < cars.size(); i++) {
			if(cars.get(i) != null) {
				cars.get(i).pause();
			}
		}
		time.pause();
	}
	
	// Method to pause all threads for cars, traffic lights, and timer
		private void resumeThreads() {
			pause = false;
			for (int i=0; i < trafficLights.size(); i++) {
				if(trafficLights.get(i) != null) {
					trafficLights.get(i).resume();
				}
			}
			for(int i=0; i < cars.size(); i++) {
				if(cars.get(i) != null) {
					cars.get(i).resume();
				}
			}
			time.resume();
		}
	
	// Method to display a car on the road with its numeric ID 
	private void showCar(Car car) {
		Platform.runLater(() ->{
			cars.add(car);
			carLV.setItems(cars);
			roadView.getChildren().add(car.getRectangle());
			roadView.getChildren().add(car.getCarLabel());
		});
	}
	
	// Getter method for returning the traffic lights list
	public ObservableList<TrafficLight> getLightList(){
		return trafficLights;
	}
	
}
