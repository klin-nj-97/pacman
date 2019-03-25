package Pacman;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * This is the top-level object class that will contain the different panes of
 * the application and delegate responsibility to the Game class.
 */
public class PaneOrganizer {
	private BorderPane _root;
	private Label _gameLabel;
	private Label _pointsLabel;
	private Label _livesLabel;
	private int _points;
	private int _lives;

	/**
	 * The constructor of the PaneOrganizer where the BorderPane, animation 
	 * pane, menu pane, responsive labels, and responsive button are 
	 * instantiated. The labels and button are then added to their appropriate 
	 * menu pane.
	 */
	public PaneOrganizer() {
		_root = new BorderPane();
		new Game(this);
		VBox menuPane = new VBox(Constants.VBOX_SPACING);
		menuPane.setPrefWidth(Constants.VBOX_WIDTH);
		menuPane.setAlignment(Pos.CENTER);
		_root.setRight(menuPane);
		_points = 0;
		_lives = 3;
		_gameLabel = new Label("You know what to do");
		_pointsLabel = new Label("Points: " + _points);
		_livesLabel = new Label("Lives: " + _lives);
		Button button = new Button("Quit");
		button.setOnAction(new QuitHandler());
		button.setFocusTraversable(false);
		menuPane.getChildren().addAll(_pointsLabel, _livesLabel, _gameLabel, button);
	}
	
	/**
	 * This method returns the BorderPane and is used in the App class to set 
	 * the scene.
	 */
	public BorderPane getRoot() {
		return _root;
	}
	
	/**
	 * This class implements the EventHandler interface to set the action of 
	 * the button to quitting the application.
	 */
	private class QuitHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			System.exit(0);
		}
	}
	
	/**
	 * Updates respective amount of points upon successful collision.
	 */
	public void updatePoints(int points) {
		_points = _points + points;
		_pointsLabel.setText("Points: " + _points);
	}
	
	/**
	 * Decrements amount of lives upon collision with chasing or scattering
	 * Ghost.
	 */
	public void updateLives() {
		_lives = _lives - 1;
		_livesLabel.setText("Lives: " + _lives);
	}
	
	/**
	 * Returns amount of lives.
	 */
	public int getLives() {
		return _lives;
	}
	
	/**
	 * Lets player know game is over.
	 */
	public void gameOver() {
		_gameLabel.setText("Game over");
	}
	
	/**
	 * Lets player know game has won.
	 */
	public void gameWin() {
		_gameLabel.setText("You win");
	}
}
