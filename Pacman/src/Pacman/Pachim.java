package Pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Contains the Pacman to move around the board according to user input.
 */
public class Pachim {
	private Ellipse _structure;

	/**
	 * Instantiates the node to represent Pacman, sets its color, and adds it to the
	 * pane.
	 */
	public Pachim(Pane pane) {
		_structure = new Ellipse();
		_structure.setRadiusX(Constants.PAC_SIZE);
		_structure.setRadiusY(Constants.PAC_SIZE);
		_structure.setFill(Color.YELLOW);
		pane.getChildren().add(_structure);
	}

	/**
	 * Sets the y-position.
	 */
	public void setY(double y) {
		_structure.setCenterY(y);
	}

	/**
	 * Sets the x-position.
	 */
	public void setX(double x) {
		_structure.setCenterX(x);
	}

	/**
	 * Gets the y-position.
	 */
	public double getY() {
		return _structure.getCenterY();
	}

	/**
	 * Gets the y-position.
	 */
	public double getX() {
		return _structure.getCenterX();
	}

	/**
	 * Returns the Ellipse node representing Pacman.
	 */
	public Ellipse getNode() {
		return _structure;
	}
}
