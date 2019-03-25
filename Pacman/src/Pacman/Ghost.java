package Pacman;

import java.util.LinkedList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The class to represent Ghosts that chase and can eat Pacman or become
 * frightened and flee from Pacman to be eaten.
 */
public class Ghost implements Collidable {
	private Rectangle _structure;
	private Direction _prevDir;
	private Game _game;

	/**
	 * Associates with the game in order to update point and life counters.
	 * Instantiates an instance of structure to represent a Ghost, enables color
	 * setting, adds it to the pane, and sets the initial previous direction of BFS
	 * to an arbitrary DOWN, to be updated.
	 */
	public Ghost(Pane pane, Color fillColor, Game game) {
		_game = game;
		_structure = new Rectangle();
		_structure.setHeight(Constants.SQUARE_SIZE);
		_structure.setWidth(Constants.SQUARE_SIZE);
		_structure.setFill(fillColor);
		pane.getChildren().add(_structure);
		_prevDir = Direction.DOWN;
	}

	/**
	 * Returns the Ghost's rectangle node
	 */
	public Rectangle getNode() {
		return _structure;
	}

	/**
	 * Changes the rectangle node's fill color
	 */
	public void changeCol(Color fillColor) {
		_structure.setFill(fillColor);
	}

	@Override
	/**
	 * If a Ghost is in chase or scatter mode and collides, Pacman's location is
	 * reset to the starting location, the Ghosts' conditions are reset to those at
	 * the start of the game, the pen's counter is reset, Pacman's returned
	 * direction enum for movement is reset, and the PaneOrganizer decrements one
	 * life from its counter. If a Ghost is in frightened mode, its current square
	 * removes it from its ArrayList, its location is reset to inside of the pen,
	 * the pen's LinkedList adds it, it is added to its new square's ArrayList
	 * within the pen, and the pen's counter is reset
	 */
	public void collide(Pane pane) {
		if (_game.chaseStatus() == true || _game.scatterStatus() == true) {
			_game.getPachim().setY((17 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
			_game.getPachim().setX((11 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
			_game.resetBlinky();
			_game.resetPinky();
			_game.resetInky();
			_game.resetClyde();
			_game.resetPenCount();
			_game.resetDirection();
			_game.getPaneOrg().updateLives();
		}
		if (_game.frightenedStatus() == true) {
			_game.getBoard()[(int) (this.getY() / Constants.SQUARE_SIZE)][(int) (this.getX() / Constants.SQUARE_SIZE)]
					.removeItem(this);
			_structure.setY(10 * Constants.SQUARE_SIZE);
			_structure.setX(11 * Constants.SQUARE_SIZE);
			_game.getPen().addLast(this);
			_game.getBoard()[(int) (this.getY() / Constants.SQUARE_SIZE)][(int) (this.getX() / Constants.SQUARE_SIZE)]
					.addItem(this);
			_game.resetPenCount();
		}
	}

	/**
	 * If a Ghost is in Chase or Scatter mode, no points are returned. If it is
	 * frightened, 200 are returned.
	 */
	public int returnPoints() {
		if (_game.chaseStatus() == true || _game.scatterStatus() == true) {
			return 0;
		}
		if (_game.frightenedStatus() == true) {
			return 200;
		}
		return 0;
	}

	/**
	 * A BoardCoordinate is to be passed in as a target of this call. The distance
	 * to be compared to is reset to an arbitrary value at the beginning of every
	 * call, a new LinkedList of BoardCoordinates is made, a new 2D Array of
	 * Direction enums is made, and the Ghost's current location is stored. Valid
	 * neighbors are then checked in all four directions: valid being not a wall,
	 * not in the opposite direction that the Ghost just moved in, and within the
	 * playable board. Valid neighbors are then added to the LinkedList and its
	 * respective direction is stored in the Array. While this LinkedList is not
	 * empty, the neighbor at the beginning of the LinkedList is removed and stored
	 * to be checked for valid neighbors of its own: the same conditions for
	 * validity hold. Should this stored neighbor be closer to the target of the BFS
	 * call than the previous closest neighbor, the closest neighbor is updated to
	 * this stored one. Neighbors of neighbors are checked, added, removed, and so
	 * on and so forth to the LinkedList appropriately until the LinkedList is
	 * empty. The closest cell at the end of this process sees its Direction
	 * returned returns and the Ghost moves in this direction.
	 */
	public Direction BFS(BoardCoordinate targetCell) {
		double smallestDist = 1000;
		BoardCoordinate closestCell = null;
		LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
		Direction[][] dirArray = new Direction[Constants.NUM_ROWS][Constants.NUM_COLUMNS];
		BoardCoordinate ghostLoc = new BoardCoordinate((int) _structure.getY() / Constants.SQUARE_SIZE,
				(int) _structure.getX() / Constants.SQUARE_SIZE, false);

		/*
		 * Checking to see if neighbors are valid: on board, is not a wall, and has not
		 * been visited
		 */
		Boolean validNeighDown = true;
		Boolean validNeighUp = true;
		Boolean validNeighRight = true;
		Boolean validRightTunnel = false;
		Boolean validNeighLeft = true;
		Boolean validLeftTunnel = false;

		if (ghostLoc.getRow() + 1 > 22
				|| _game.getBoard()[ghostLoc.getRow() + 1][ghostLoc.getColumn()].checkWall() == true
				|| _prevDir.getOpposite() == Direction.DOWN) {
			validNeighDown = false;
		}
		if (ghostLoc.getRow() - 1 < 0
				|| _game.getBoard()[ghostLoc.getRow() - 1][ghostLoc.getColumn()].checkWall() == true
				|| _prevDir.getOpposite() == Direction.UP) {
			validNeighUp = false;
		}
		if (ghostLoc.getColumn() + 1 > 22
				|| _game.getBoard()[ghostLoc.getRow()][ghostLoc.getColumn() + 1].checkWall() == true
				|| _prevDir.getOpposite() == Direction.RIGHT) {
			validNeighRight = false;
		}

		if (ghostLoc.getRow() == 11 && ghostLoc.getColumn() == 22) {
			validRightTunnel = true;
		}

		if (ghostLoc.getColumn() - 1 < 0
				|| _game.getBoard()[ghostLoc.getRow()][ghostLoc.getColumn() - 1].checkWall() == true
				|| _prevDir.getOpposite() == Direction.LEFT) {
			validNeighLeft = false;
		}

		if (ghostLoc.getRow() == 11 && ghostLoc.getColumn() == 0) {
			validLeftTunnel = true;
		}

		/*
		 * If neighbor is valid, assigning BoardCoordinate to that neighbor, storing its
		 * direction in 2D Array, and adding respective BoardCoordinate to queue
		 */
		if (validNeighDown) {
			BoardCoordinate neighDown = new BoardCoordinate(ghostLoc.getRow() + 1, ghostLoc.getColumn(), false);
			dirArray[neighDown.getRow()][neighDown.getColumn()] = Direction.DOWN;
			queue.addLast(neighDown);
		}

		if (validNeighUp) {
			BoardCoordinate neighUp = new BoardCoordinate(ghostLoc.getRow() - 1, ghostLoc.getColumn(), false);
			dirArray[neighUp.getRow()][neighUp.getColumn()] = Direction.UP;
			queue.addLast(neighUp);
		}

		if (validNeighRight || validRightTunnel) {
			BoardCoordinate neighRight = null;
			if ((ghostLoc.getRow() == 11 && ghostLoc.getColumn() == 22) && dirArray[11][0] == null) {
				neighRight = new BoardCoordinate(11, 0, false);
			}
			if ((ghostLoc.getRow() == 11 && ghostLoc.getColumn() >= 0 && ghostLoc.getColumn() < 22)
					&& dirArray[11][ghostLoc.getColumn() + 1] == null) {
				neighRight = new BoardCoordinate(11, ghostLoc.getColumn(), false);
			}

			if ((ghostLoc.getRow() != 11 && ghostLoc.getColumn() != 22)
					&& dirArray[11][ghostLoc.getColumn() + 1] == null) {
				neighRight = new BoardCoordinate(ghostLoc.getRow(), ghostLoc.getColumn() + 1, false);
			}

			if (neighRight != null) {
				dirArray[neighRight.getRow()][neighRight.getColumn()] = Direction.RIGHT;
				queue.addLast(neighRight);
			}
		}

		if (validNeighLeft || validLeftTunnel) {
			BoardCoordinate neighLeft = null;
			if ((ghostLoc.getRow() == 11 && ghostLoc.getColumn() == 0) && dirArray[11][22] == null) {
				neighLeft = new BoardCoordinate(11, 22, false);
			}
			if ((ghostLoc.getRow() == 11 && ghostLoc.getColumn() > 0 && ghostLoc.getColumn() <= 22)
					&& dirArray[11][ghostLoc.getColumn() - 1] == null) {
				neighLeft = new BoardCoordinate(11, ghostLoc.getColumn() - 1, false);
			}

			if ((ghostLoc.getRow() != 11 && ghostLoc.getColumn() != 22)
					&& dirArray[11][ghostLoc.getColumn() - 1] == null) {
				neighLeft = new BoardCoordinate(ghostLoc.getRow(), ghostLoc.getColumn() - 1, false);
			}

			if (neighLeft != null) {
				dirArray[neighLeft.getRow()][neighLeft.getColumn()] = Direction.LEFT;
				queue.addLast(neighLeft);
			}
		}

		while (!queue.isEmpty()) {
			BoardCoordinate currentCell = queue.removeFirst();

			double currentDist = Math.sqrt(Math.abs(currentCell.getRow() - targetCell.getRow())
					* Math.abs(currentCell.getRow() - targetCell.getRow())
					+ Math.abs(currentCell.getColumn() - targetCell.getColumn())
							* Math.abs(currentCell.getColumn() - targetCell.getColumn()));

			if (currentDist < smallestDist) {
				smallestDist = currentDist;
				closestCell = currentCell;
			}

			/*
			 * Checking to see if current cell's neighbors are valid: on board, is not a
			 * wall, and has not been visited
			 */
			Boolean validNextNeighDown = true;
			Boolean validNextNeighUp = true;
			Boolean validNextNeighRight = true;
			Boolean validNextRightTunnel = false;
			Boolean validNextNeighLeft = true;
			Boolean validNextLeftTunnel = false;
			if (currentCell.getRow() + 1 > 22
					|| _game.getBoard()[currentCell.getRow() + 1][currentCell.getColumn()].checkWall() == true
					|| dirArray[currentCell.getRow() + 1][currentCell.getColumn()] != null) {
				validNextNeighDown = false;
			}
			if (currentCell.getRow() - 1 < 0
					|| _game.getBoard()[currentCell.getRow() - 1][currentCell.getColumn()].checkWall() == true
					|| dirArray[currentCell.getRow() - 1][currentCell.getColumn()] != null) {
				validNextNeighUp = false;
			}
			if (currentCell.getColumn() + 1 > 22
					|| _game.getBoard()[currentCell.getRow()][currentCell.getColumn() + 1].checkWall() == true
					|| dirArray[currentCell.getRow()][currentCell.getColumn() + 1] != null) {
				validNextNeighRight = false;
			}

			if (currentCell.getRow() == 11 && currentCell.getColumn() == 22) {
				validNextRightTunnel = true;
			}

			if (currentCell.getColumn() - 1 < 0
					|| _game.getBoard()[currentCell.getRow()][currentCell.getColumn() - 1].checkWall() == true
					|| dirArray[currentCell.getRow()][currentCell.getColumn() - 1] != null) {
				validNextNeighLeft = false;
			}

			if (currentCell.getRow() == 11 && currentCell.getColumn() == 0) {
				validNextLeftTunnel = true;
			}

			/*
			 * If neighbor is valid, assigning BoardCoordinate to that neighbor, storing its
			 * direction in 2D Array, and adding respective BoardCoordinate to queue
			 */
			if (validNextNeighDown) {
				BoardCoordinate nextNeighDown = new BoardCoordinate(currentCell.getRow() + 1, currentCell.getColumn(),
						false);
				dirArray[nextNeighDown.getRow()][nextNeighDown.getColumn()] = dirArray[currentCell.getRow()][currentCell
						.getColumn()];
				queue.addLast(nextNeighDown);
			}
			if (validNextNeighUp) {
				BoardCoordinate nextNeighUp = new BoardCoordinate(currentCell.getRow() - 1, currentCell.getColumn(),
						false);
				dirArray[nextNeighUp.getRow()][nextNeighUp.getColumn()] = dirArray[currentCell.getRow()][currentCell
						.getColumn()];
				queue.addLast(nextNeighUp);
			}
			if (validNextNeighRight || validNextRightTunnel) {
				BoardCoordinate nextNeighRight = null;
				if ((currentCell.getRow() == 11 && currentCell.getColumn() == 22) && dirArray[11][0] == null) {
					nextNeighRight = new BoardCoordinate(11, 0, false);
				}
				if ((currentCell.getRow() == 11 && currentCell.getColumn() >= 0 && currentCell.getColumn() < 22)
						&& dirArray[11][currentCell.getColumn() + 1] == null) {
					nextNeighRight = new BoardCoordinate(11, currentCell.getColumn(), false);
				}

				if ((currentCell.getRow() != 11 && currentCell.getColumn() != 22)
						&& dirArray[11][currentCell.getColumn() + 1] == null) {
					nextNeighRight = new BoardCoordinate(currentCell.getRow(), currentCell.getColumn() + 1, false);
				}
				if (nextNeighRight != null) {
					dirArray[nextNeighRight.getRow()][nextNeighRight
							.getColumn()] = dirArray[currentCell.getRow()][currentCell.getColumn()];
					queue.addLast(nextNeighRight);
				}
			}
			if (validNextNeighLeft || validNextLeftTunnel) {
				BoardCoordinate nextNeighLeft = null;
				if ((currentCell.getRow() == 11 && currentCell.getColumn() == 0) && dirArray[11][22] == null) {
					nextNeighLeft = new BoardCoordinate(11, 22, false);
				}
				if ((currentCell.getRow() == 11 && currentCell.getColumn() > 0 && currentCell.getColumn() <= 22)
						&& dirArray[11][currentCell.getColumn() - 1] == null) {
					nextNeighLeft = new BoardCoordinate(11, currentCell.getColumn() - 1, false);
				}

				if ((currentCell.getRow() != 11 && currentCell.getColumn() != 22)
						&& dirArray[11][currentCell.getColumn() - 1] == null) {
					nextNeighLeft = new BoardCoordinate(currentCell.getRow(), currentCell.getColumn() - 1, false);
				}

				if (nextNeighLeft != null) {
					dirArray[nextNeighLeft.getRow()][nextNeighLeft
							.getColumn()] = dirArray[currentCell.getRow()][currentCell.getColumn()];
					queue.addLast(nextNeighLeft);
				}
			}
		}

		_prevDir = dirArray[closestCell.getRow()][closestCell.getColumn()];
		return dirArray[closestCell.getRow()][closestCell.getColumn()];

	}

	/**
	 * Sets the Ghost's y-position.
	 */
	public void setY(double y) {
		_structure.setY(y);
	}

	/**
	 * Sets the Ghost's x-position.
	 */
	public void setX(double x) {
		_structure.setX(x);
	}

	/**
	 * Gets the Ghost's y-position.
	 */
	public double getY() {
		return _structure.getY();
	}

	/**
	 * Gets the Ghost's x-position.
	 */
	public double getX() {
		return _structure.getX();
	}
}
