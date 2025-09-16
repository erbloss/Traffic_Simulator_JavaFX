package application;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
/*
 * This class defines the road view by extending Pane.  
 * A road is graphically represented as a rectangle. 
 * A center road divider and markers every
 * 1000 meters are included.  
 *  
 *  Elizabeth Bloss
 *  UMGC, CMSC335    
 *  December 2024
 *  Project 3
 */

public class RoadView extends Pane {
	private Rectangle road;			// draw road as rectangle
	private Rectangle roadDivide;	
	private Text m0, m1, m2, m3, m4, m5, 
		m6, m7, m8, m9, m10;
	
	public RoadView() {
		setPrefSize(1100, 75);
		road = new Rectangle(1020, 70, Color.SLATEGREY);
		roadDivide = new Rectangle(1020, 2, Color.YELLOW);
		roadDivide.setY(35);
		m0 = createMarker("0 meters", 16);
		m1 = createMarker("1000", 100);
		m2 = createMarker("2000", 200);
		m3 = createMarker("3000", 300);
		m4 = createMarker("4000", 400);
		m5 = createMarker("5000", 500);
		m6 = createMarker("6000", 600);
		m7 = createMarker("7000", 700);
		m8 = createMarker("8000", 800);
		m9 = createMarker("9000", 900);
		m10 = createMarker("10000", 1000);
		getChildren().addAll(road, roadDivide, 
				m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10);	
	}
	
	// method to create and style the distance markers
	// for the road display
	private Text createMarker(String s, int place) {
		Text m = new Text(s);
		m.setFill(Color.LIGHTBLUE);
		m.setX(place - 15);
		m.setY(15);
		return m;
	}
}
