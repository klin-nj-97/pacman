package Pacman;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Wrapper class to represent one square on the game's board.
 */
public class SmartSquare {
	private Rectangle _structure;
	private ArrayList<Collidable> _storage;
	private Boolean _isWall;
	private Boolean _checkStorage;

	/**
	 * Instantiates node to represent square, sets it dimensions, sets it to not
	 * a wall by default.
	 */
	public SmartSquare() {
		_structure = new Rectangle();
		_structure.setHeight(Constants.SQUARE_SIZE);
		_structure.setWidth(Constants.SQUARE_SIZE);
		_isWall = false;
		_checkStorage = true;
		_storage = new ArrayList<Collidable>();
	}

	/**
	 * Sets the color of the square.
	 */
	public void setCol(Color fillColor) {
		_structure.setFill(fillColor);
	}

	/**
	 * Returns the square's node.
	 */
	public Node getNode() {
		return _structure;
	}

	/**
	 * Sets the square's x-position.
	 */
	public void setX(double x) {
		_structure.setX(x);
	}

	/**
	 * Sets the square's y-position.
	 */
	public void setY(double y) {
		_structure.setY(y);
	}

	/**
	 * Gets the square's x-position.
	 */
	public double getX() {
		return _structure.getX();
	}

	/**
	 * Gets the square's y-position.
	 */
	public double getY() {
		return _structure.getY();
	}

	/**
	 * Adds a collidable item to the square's ArrayList.
	 */
	public void addItem(Collidable item) {
		_storage.add(item);
	}

	/**
	 * Removes a collidable item from the square's ArrayList.
	 */
	public void removeItem(Collidable item) {
		_storage.remove(item);

	}

	/**
	 * Returns true if the ArrayList contains anything.
	 */
	public Boolean checkStorage() {
		if (_storage.size() > 0) {
			return true;
		}
		if (_storage.size() == 0) {
			return false;
		}
		return null;
	}

	/**
	 * Returns the ith index of the square's ArrayList.
	 */
	public Collidable getItem(int i) {
		if (_checkStorage) {
			return _storage.get(i);
		}
		return null;
	}

	/**
	 * Returns the ArrayList
	 */
	public ArrayList<Collidable> getList() {
		return _storage;
	}

	/**
	 * Makes the square a wall.
	 */
	public void makeWall() {
		_isWall = true;
	}

	/**
	 * Checks whether the square is a wall.
	 */
	public Boolean checkWall() {
		return _isWall;
	}

	/**
	 * Removes all elements from the ArrayList.
	 */
	public void clearStorage() {
		_storage.clear();
	}

}