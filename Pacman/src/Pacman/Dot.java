package Pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Holds information on the size and color of a dot to be eaten by Pacman.
 */
public class Dot implements Collidable {
	private Ellipse _structure;
	private Game _game;

	/**
	 * Associates the game in order to update the dot-energizer counter,
	 * instantiates an instance of an ellipse, sets its color, and adds it to the
	 * pane
	 */
	public Dot(Pane pane, Game game) {
		_game = game;
		_structure = new Ellipse();
		_structure.setRadiusX(Constants.DOT_SIZE);
		_structure.setRadiusY(Constants.DOT_SIZE);
		_structure.setFill(Color.LIGHTGREY);
		pane.getChildren().add(_structure);
	}

	/**
	 * Returns the Ellipse instance of the dot's structure
	 */
	public Ellipse getNode() {
		return _structure;
	}

	@Override
	/**
	 * Irrelevant method to be used by Ghost
	 */
	public Direction BFS(BoardCoordinate targetCell) {
		return null;
	}

	/**
	 * Sets the y-location of the dot's structure
	 */
	public void setY(double y) {
		_structure.setCenterY(y);
	}

	/**
	 * Sets the x-location of the dot's structure
	 */
	public void setX(double x) {
		_structure.setCenterX(x);
	}

	/**
	 * Removes the dot's structure from the pane when eaten by Pacman and updates
	 * the dot-energizer counter for game win conditions
	 */
	public void collide(Pane pane) {
		pane.getChildren().remove(_structure);
		_game.decDotEnergCount();
	}

	/**
	 * Returns point value to be added to the game's point counter
	 */
	public int returnPoints() {
		return 10;
	}

	/**
	 * Empty method
	 */
	public double getY() {
		return 0;
	}

	/**
	 * Empty method
	 */
	public double getX() {
		return 0;
	}
}