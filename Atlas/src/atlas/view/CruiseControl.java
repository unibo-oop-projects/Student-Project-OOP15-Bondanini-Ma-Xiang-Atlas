package atlas.view;

import atlas.utils.Units;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * This class contains part of the UI which is used to control the simulation.
 * It provides the following functionalities: 
 * - Play/Pause 
 * - Show date or time of the simulation 
 * - A way to change the simulation speed 
 * - EDIT and ADD buttons 
 * - Search and eventually info/screenshots 
 * 
 * @author MaXX
 *
 */
public class CruiseControl extends HBox {
	protected Button buttonPlay = new Button("PLAY");// setGraphic
	protected Button buttonStop = new Button("STOP");
	private boolean play;

	protected Label labelTime = new Label("Sample: 01/01/2000");

	protected TextField textSpeed = new TextField();
	protected ChoiceBox<Units> choiceSpeedUnit = new ChoiceBox<>();

	protected Button buttonEdit = new Button("EDIT");
	protected Button buttonAdd = new Button("ADD");

	protected Button buttonSearch = new Button();

	public CruiseControl() {
		this.getChildren().add(0, buttonStop);
		this.getChildren().add(1, labelTime);
		this.getChildren().add(2, textSpeed);
		this.getChildren().add(3, choiceSpeedUnit);
		this.getChildren().add(4, buttonEdit);
		this.getChildren().add(5, buttonAdd);
		this.getChildren().add(6, buttonSearch);
		this.play = false;
		this.choiceSpeedUnit.getItems().addAll(Units.values());
		
		/*Setting actions*/
		this.buttonPlay.setOnAction( a -> {
			switchPlayStop();
			//do something else
		});
		this.buttonStop.setOnAction( a -> {
			switchPlayStop();
			//do something else
		});
	}

	private void switchPlayStop() {
		this.getChildren().remove(this.play ? this.buttonPlay : this.buttonStop);
		this.getChildren().add(0, this.play ? this.buttonStop : this.buttonPlay);
		this.play = !this.play;
	}
}