package Pacman;

import java.util.LinkedList;

import cs015.fnl.PacmanSupport.BoardLocation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class is responsible for dealing with the graphical animation, logical
 * data structuring, and handling of key inputs of the application. A 2D Array
 * contains the squares of the board and a LinkedList contains Ghosts for the
 * pen.
 */
public class Game {
	private Pane _pane;
	private PaneOrganizer _paneOrganizer;
	private SmartSquare[][] _board;
	private Dot _dot;
	private Energizer _energizer;
	private Pachim _pachim;
	private Timeline _moving;
	private Timeline _ghostTimeline;
	private KeyHandler _keyHandler;
	private Direction _direction;
	private Ghost _blinky;
	private Ghost _pinky;
	private Ghost _inky;
	private Ghost _clyde;
	private Boolean _chase;
	private Boolean _scatter;
	private Boolean _frightened;
	private int _ghostCount;
	private int _frightenedCount;
	private int _dotEnergCount;
	private LinkedList<Ghost> _pen;
	private Timeline _penTimeline;
	private int _penCount;

	/**
	 * The constructor that instantiates a pane to contain the graphical movement
	 * and then adds the board to this pane. It associates itself with the top-level
	 * object PaneOrganizer class in order to set the animation pane to the center
	 * of the PaneOrganizer's BorderPane and to update the label. More details
	 * regarding this design choice in README. It calls the methods to set up the
	 * Timeline and TimeHandler responsible for moving Pacman, to instantiate a
	 * LinkedList for Ghosts, to set up the Timeline and TimeHandler for the Ghost
	 * pen, to set up the board using the support map's enums, sets up the Timeline
	 * and TimeHandler responsible for moving Ghosts, and assigns Booleans and ints
	 * to initial values.
	 */
	public Game(PaneOrganizer organizer) {
		_pane = new Pane();
		_pane.setPrefWidth(Constants.PANE_WIDTH);
		_pane.setPrefHeight(Constants.PANE_HEIGHT);
		_pane.setStyle("-fx-background-color: black;");
		_paneOrganizer = organizer;
		_paneOrganizer.getRoot().setCenter(_pane);
		this.setupPacHandler();
		this.setupKeyHandler();
		_pen = new LinkedList<Ghost>();
		this.setupPenHandler();
		this.setupBoard();
		_chase = true;
		_scatter = false;
		_frightened = false;
		this.setupGhostHandler();
		_ghostCount = 1;
		_frightenedCount = 1;
		_dotEnergCount = 186;
		_penCount = 0;
	}

	/**
	 * Accesses the support map's Array, instantiates a new Array of SmartSquares,
	 * and uses a double for-loop of row-major to assign attributes to the
	 * SmartSquare in a given support map's square's location dependent on the enum
	 * present there.
	 */
	private void setupBoard() {
		BoardLocation[][] layout = cs015.fnl.PacmanSupport.SupportMap.getMap(); //
		_board = new SmartSquare[Constants.NUM_ROWS][Constants.NUM_COLUMNS];
		for (int row = 0; row < Constants.NUM_ROWS; row++) {
			for (int col = 0; col < Constants.NUM_COLUMNS; col++) {
				SmartSquare square = new SmartSquare();
				if (layout[row][col] == BoardLocation.FREE) {
					square.setCol(Color.BLACK);
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_board[row][col] = square;
					_pane.getChildren().add(_board[row][col].getNode());
				}
				if (layout[row][col] == BoardLocation.WALL) {
					square.setCol(Color.WHITE);
					square.makeWall();
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_board[row][col] = square;
					_pane.getChildren().add(_board[row][col].getNode());
				}
				if (layout[row][col] == BoardLocation.DOT) {
					square.setCol(Color.BLACK);
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_board[row][col] = square;
					_pane.getChildren().add(_board[row][col].getNode());
					_dot = new Dot(_pane, this);
					square.addItem(_dot);
					/*
					 * To graphically position each dot in the center of its corresponding square
					 */
					_dot.setY((row * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
					_dot.setX((col * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);

				}
				if (layout[row][col] == BoardLocation.ENERGIZER) {
					square.setCol(Color.BLACK);
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_board[row][col] = square;
					_pane.getChildren().add(_board[row][col].getNode());
					_energizer = new Energizer(_pane, this);
					square.addItem(_energizer);
					_energizer.setY((row * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
					_energizer.setX((col * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);

				}
				if (layout[row][col] == BoardLocation.PACMAN_START_LOCATION) {
					square.setCol(Color.BLACK);
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_pachim = new Pachim(_pane);
					_pachim.setY((row * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
					_pachim.setX((col * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
					_board[row][col] = square;
					_pane.getChildren().add(_board[row][col].getNode());
				}
				/*
				 * The four ghosts are instantiated and graphically set to locations surrounding
				 * the GHOST_START_LOCATION enum's square
				 */
				if (layout[row][col] == BoardLocation.GHOST_START_LOCATION) {
					square.setCol(Color.BLACK);
					square.setY(row * Constants.SQUARE_SIZE);
					square.setX(col * Constants.SQUARE_SIZE);
					_board[row][col] = square;

					_blinky = new Ghost(_pane, Color.RED, this);
					_blinky.setY((row - 2) * Constants.SQUARE_SIZE);
					_blinky.setX(col * Constants.SQUARE_SIZE);
					_board[row - 2][col].addItem(_blinky);

					_pinky = new Ghost(_pane, Color.PINK, this);
					_pinky.setY(row * Constants.SQUARE_SIZE);
					_pinky.setX((col - 1) * Constants.SQUARE_SIZE);
					_pen.addLast(_pinky);
					_board[row][col - 1].addItem(_pinky);

					_inky = new Ghost(_pane, Color.TURQUOISE, this);
					_inky.setY(row * Constants.SQUARE_SIZE);
					_inky.setX(col * Constants.SQUARE_SIZE);
					_pen.addLast(_inky);
					_board[row][col].addItem(_inky);

					_clyde = new Ghost(_pane, Color.ORANGE, this);
					_clyde.setY(row * Constants.SQUARE_SIZE);
					_clyde.setX((col + 1) * Constants.SQUARE_SIZE);
					_pen.addLast(_clyde);
					_board[10][11].addItem(_clyde);

					_pane.getChildren().add(_board[row][col].getNode());
				}
			}
		}
		_dot.getNode().toFront();
		_energizer.getNode().toFront();
		_blinky.getNode().toFront();
		_pinky.getNode().toFront();
		_inky.getNode().toFront();
		_clyde.getNode().toFront();
		_pachim.getNode().toFront();
	}

	/**
	 * Returns whether the game is in chase mode.
	 */
	public Boolean chaseStatus() {
		return _chase;
	}

	/**
	 * Returns whether the game is in scatter mode.
	 */
	public Boolean scatterStatus() {
		return _scatter;
	}

	/**
	 * Returns whether the game is in frightened mode.
	 */
	public Boolean frightenedStatus() {
		return _frightened;
	}

	/**
	 * Switches the game to chase mode.
	 */
	private void makeChase() {
		_frightened = false;
		_chase = true;
		_scatter = false;
	}

	/**
	 * Switches the game to scatter mode.
	 */
	private void makeScatter() {
		_frightened = false;
		_chase = false;
		_scatter = true;
	}

	/**
	 * Switches the game to frightened mode.
	 */
	public void makeFrightened() {
		_frightened = true;
		_chase = false;
		_scatter = false;
	}

	/**
	 * Instantiates a KeyFrame specifying how long the animation should run for and
	 * which event handler to use it with. Also instantiates the Timeline itself,
	 * sets the animation to play indefinitely, and then plays the animation.
	 */
	private void setupPenHandler() {
		KeyFrame penFrame = new KeyFrame(Duration.seconds(Constants.DURATION), new PenHandler());
		_penTimeline = new Timeline(penFrame);
		_penTimeline.setCycleCount(Animation.INDEFINITE);
		_penTimeline.play();
	}

	/**
	 * Class to implement handle method specifying what should occur at the end of
	 * each KeyFrame for proper removal of Ghosts from the pen, both graphically and
	 * logically.
	 */
	private class PenHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (_penCount == 17) { // Releases a Ghost only upon reaching this count
				if (!_pen.isEmpty()) { // Releases a Ghost only if there is one

					Ghost removedGhost = _pen.removeFirst();
					_board[(int) (removedGhost.getY() / Constants.SQUARE_SIZE)][(int) (removedGhost.getX()
							/ Constants.SQUARE_SIZE)].removeItem(removedGhost);
					removedGhost.setY(8 * Constants.SQUARE_SIZE); // The square outside of the pen
					removedGhost.setX(11 * Constants.SQUARE_SIZE);
					_board[8][11].addItem(removedGhost);
					_penCount = 0;
				}

			}
			if (_penCount > 17) { // Ensures the pen can reach 17 again
				_penCount = 0;
			}
			_penCount = _penCount + 1;
		}

	}

	/**
	 * Instantiates a KeyFrame specifying how long the animation should run for and
	 * which event handler to use it with. Also instantiates the Timeline itself,
	 * sets the animation to play indefinitely, and then plays the animation.
	 */
	private void setupPacHandler() {
		KeyFrame moveFrame = new KeyFrame(Duration.seconds(Constants.PAC_DURATION), new PacHandler());
		_moving = new Timeline(moveFrame);
		_moving.setCycleCount(Animation.INDEFINITE);
		_moving.play();
	}

	/**
	 * Class to implement handle method specifying what should occur at the end of
	 * each KeyFrame for proper movement of Pacman. This class implements
	 * EventHandler interface.
	 */
	private class PacHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			/*
			 * Pacman will continue moving in a direction as long as the enum does not
			 * change
			 */
			if (_direction == Direction.LEFT) {
				Game.this.checkCollision();
				/*
				 * For tunnel wrapping
				 */
				if (_pachim.getY() == (11 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2
						&& _pachim.getX() == Constants.SQUARE_SIZE / 2) {
					_pachim.setX((22 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2);
				}
				if (Game.this.pacCheckLeft()) {
					_pachim.setX(_pachim.getX() - Constants.SQUARE_SIZE);
				}

				Game.this.checkCollision();
			}

			if (_direction == Direction.RIGHT) {
				Game.this.checkCollision();
				if (_pachim.getY() == (11 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2
						&& _pachim.getX() == (22 * Constants.SQUARE_SIZE) + Constants.SQUARE_SIZE / 2) {
					_pachim.setX(Constants.SQUARE_SIZE / 2);
				}
				if (Game.this.pacCheckRight()) {
					_pachim.setX(_pachim.getX() + Constants.SQUARE_SIZE);
				}
				Game.this.checkCollision();
			}

			if (_direction == Direction.UP) {
				Game.this.checkCollision();
				if (Game.this.pacCheckUp()) {
					_pachim.setY(_pachim.getY() - Constants.SQUARE_SIZE);
					Game.this.checkCollision();
				}
			}

			if (_direction == Direction.DOWN) {
				Game.this.checkCollision();
				if (Game.this.pacCheckDown()) {
					_pachim.setY(_pachim.getY() + Constants.SQUARE_SIZE);
					Game.this.checkCollision();
				}
			}
		}
	}

	/**
	 * Instantiates and adds KeyHandler responsible for moving Pacman. Sets the
	 * focus to the animation pane so that other nodes do not grab focus.
	 */
	private void setupKeyHandler() {
		_keyHandler = new KeyHandler();
		_pane.addEventHandler(KeyEvent.KEY_PRESSED, _keyHandler);
		_pane.setFocusTraversable(true);
	}

	/**
	 * Responsible for key input to change the enum returned that dictates Pacman's
	 * direction.
	 */
	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent e) {
			KeyCode keyPressed = e.getCode();
			switch (keyPressed) {

			case LEFT:
				if (Game.this.pacCheckLeft())
					_direction = Direction.LEFT;
				break;
			case RIGHT:
				if (Game.this.pacCheckRight())
					_direction = Direction.RIGHT;
				break;
			case UP:
				if (Game.this.pacCheckUp())
					_direction = Direction.UP;
				break;
			case DOWN:
				if (Game.this.pacCheckDown())
					_direction = Direction.DOWN;
				break;
			default:
				break;
			}
		}

	}

	/**
	 * Instantiates a KeyFrame specifying how long the animation should run for and
	 * which event handler to use it with. Also instantiates the Timeline itself,
	 * sets the animation to play indefinitely, and then plays the animation.
	 */
	private void setupGhostHandler() {
		KeyFrame ghostFrame = new KeyFrame(Duration.seconds(Constants.DURATION), new GhostHandler());
		_ghostTimeline = new Timeline(ghostFrame);
		_ghostTimeline.setCycleCount(Animation.INDEFINITE);
		_ghostTimeline.play();
	}

	/**
	 * Class to implement handle method specifying what should occur at the end of
	 * each KeyFrame for proper movement of Ghosts. Modes switch upon reaching a
	 * designated count and counters are incremented at the end of every KeyFrame.
	 * This class implements EventHandler interface.
	 */
	private class GhostHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (!_frightened) { // Only checks if the ghosts are not frightened
				if (_ghostCount == 100) {
					/*
					 * The game starts in chase mode and switches to scatter after 20 seconds
					 */
					Game.this.makeScatter();
				}
				if (_ghostCount == 135) {
					/*
					 * The game switches back to chase mode after 7 seconds
					 */
					Game.this.makeChase();
					/*
					 * The original cycle starts again
					 */
					_ghostCount = 1;
				}
			}
			if (_frightened) {
				if (_frightenedCount == 35) {
					/*
					 * After 7 seconds, chase resumes
					 */
					Game.this.makeChase();
					_frightenedCount = 1;
				}
			}

			/*
			 * Respective methods for four ghosts are called to chase Pacman should the mode
			 * be in chase
			 */
			if (_chase && !_scatter && !_frightened) {
				this.blinkyChase();
				this.pinkyChase();
				this.inkyChase();
				this.clydeChase();
			}

			/*
			 * Respective methods for four ghosts are called to scatter should the mode be
			 * in scatter
			 */
			if (_scatter && !_chase && !_frightened) {
				this.blinkyScatter();
				this.pinkyScatter();
				this.inkyScatter();
				this.clydeScatter();
			}

			/*
			 * Respective methods for four ghosts are called to choose a random turn in at
			 * intersection should the mode be in frightened
			 */
			if (_frightened && !_chase && !_scatter) {
				this.ghostFrightened(_blinky);
				this.ghostFrightened(_pinky);
				this.ghostFrightened(_inky);
				this.ghostFrightened(_clyde);
			}

			if (!_frightened) {
				/*
				 * Counter for chase and scatter incremented only if mode is not frightened
				 */
				_ghostCount = _ghostCount + 1;

			}
			if (_frightened) {
				/*
				 * Counter for frightened incremented only if mode is frightened
				 */
				_frightenedCount = _frightenedCount + 1;
			}
		}

		/**
		 * Resets Blinky's color, sets the target of BFS to Pacman's current location,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void blinkyChase() {
			_blinky.changeCol(Color.RED);
			Direction nextDir = null;
			BoardCoordinate pacLoc = new BoardCoordinate((int) _pachim.getY() / Constants.SQUARE_SIZE,
					(int) _pachim.getX() / Constants.SQUARE_SIZE, false);
			nextDir = _blinky.BFS(pacLoc);
			if (nextDir == Direction.LEFT) {
				/*
				 * For tunnel wrapping
				 */
				if (_blinky.getY() == 11 * Constants.SQUARE_SIZE && _blinky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckLeft(_blinky)) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(_blinky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {
				if (_blinky.getY() == 11 * Constants.SQUARE_SIZE && _blinky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckRight(_blinky)) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(_blinky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(_blinky)) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setY(_blinky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_blinky)) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setY(_blinky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Pinky's color, sets the target of BFS to Pacman's current location,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void pinkyChase() {
			_pinky.changeCol(Color.PINK);
			Direction nextDir = null;
			BoardCoordinate pacLoc = new BoardCoordinate((int) _pachim.getY() / Constants.SQUARE_SIZE,
					(int) (_pachim.getX() / Constants.SQUARE_SIZE) + 2, true);
			nextDir = _pinky.BFS(pacLoc);
			if (nextDir == Direction.LEFT) {
				if (_pinky.getY() == 11 * Constants.SQUARE_SIZE && _pinky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckLeft(_pinky)) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(_pinky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {
				if (_pinky.getY() == 11 * Constants.SQUARE_SIZE && _pinky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckRight(_pinky)) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(_pinky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(_pinky)) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setY(_pinky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_pinky)) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setY(_pinky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Inky's color, sets the target of BFS to Pacman's current location, and
		 * moves left, right, up, or down depending on the direction enum returned from
		 * the BFS call.
		 */
		private void inkyChase() {
			_inky.changeCol(Color.TURQUOISE);
			Direction nextDir = null;
			BoardCoordinate pacLoc = new BoardCoordinate((int) (_pachim.getY() / Constants.SQUARE_SIZE) - 4,
					(int) _pachim.getX() / Constants.SQUARE_SIZE, true);
			nextDir = _inky.BFS(pacLoc);
			if (nextDir == Direction.LEFT) {
				if (_inky.getY() == 11 * Constants.SQUARE_SIZE && _inky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckLeft(_inky)) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(_inky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {
				if (_inky.getY() == 11 * Constants.SQUARE_SIZE && _inky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckRight(_inky)) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(_inky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(_inky)) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setY(_inky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_inky)) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setY(_inky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Clyde's color, sets the target of BFS to Pacman's current location,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void clydeChase() {
			_clyde.changeCol(Color.ORANGE);
			Direction nextDir = null;
			BoardCoordinate pacLoc = new BoardCoordinate((int) (_pachim.getY() / Constants.SQUARE_SIZE) + 3,
					(int) (_pachim.getX() / Constants.SQUARE_SIZE) - 3, true);
			nextDir = _clyde.BFS(pacLoc);
			if (nextDir == Direction.LEFT) {
				if (_clyde.getY() == 11 * Constants.SQUARE_SIZE && _clyde.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckLeft(_clyde)) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(_clyde.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {
				if (_clyde.getY() == 11 * Constants.SQUARE_SIZE && _clyde.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckRight(_clyde)) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(_clyde.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(_clyde)) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setY(_clyde.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_clyde)) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setY(_clyde.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Blinky's color, sets the target of BFS to the upper left-hand corner,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void blinkyScatter() {
			_blinky.changeCol(Color.RED);
			Direction nextDir = null;
			BoardCoordinate upperLeft = new BoardCoordinate(0, 0, false);
			nextDir = _blinky.BFS(upperLeft);

			if (nextDir == Direction.LEFT) {
				if (_blinky.getY() == 11 * Constants.SQUARE_SIZE && _blinky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckLeft(_blinky)) {

					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(_blinky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {

				if (_blinky.getY() == 11 * Constants.SQUARE_SIZE && _blinky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckRight(_blinky)) {

					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setX(_blinky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();

				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(_blinky)) {

					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setY(_blinky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_blinky)) {

					Game.this.checkCollision();

					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].removeItem(_blinky);
					_blinky.setY(_blinky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX()
							/ Constants.SQUARE_SIZE)].addItem(_blinky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Pinky's color, sets the target of BFS to the upper right-hand corner,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void pinkyScatter() {
			_pinky.changeCol(Color.PINK);
			Direction nextDire = null;
			BoardCoordinate upperRight = new BoardCoordinate(0, 22, false);
			nextDire = _pinky.BFS(upperRight);

			if (nextDire == Direction.LEFT) {
				if (_pinky.getY() == 11 * Constants.SQUARE_SIZE && _pinky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckLeft(_pinky)) {

					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(_pinky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.RIGHT) {

				if (_pinky.getY() == 11 * Constants.SQUARE_SIZE && _pinky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckRight(_pinky)) {

					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setX(_pinky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();

				}
			}

			if (nextDire == Direction.UP) {
				if (Game.this.ghostCheckUp(_pinky)) {

					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setY(_pinky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_pinky)) {

					Game.this.checkCollision();

					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_pinky);
					_pinky.setY(_pinky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_pinky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Inky's color, sets the target of BFS to the lower left-hand corner,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void inkyScatter() {
			_inky.changeCol(Color.TURQUOISE);
			Direction nextDire = null;
			BoardCoordinate lowerLeft = new BoardCoordinate(22, 0, false);
			nextDire = _inky.BFS(lowerLeft);

			if (nextDire == Direction.LEFT) {
				if (_inky.getY() == 11 * Constants.SQUARE_SIZE && _inky.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckLeft(_inky)) {

					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(_inky.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.RIGHT) {

				if (_inky.getY() == 11 * Constants.SQUARE_SIZE && _inky.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckRight(_inky)) {

					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setX(_inky.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();

				}
			}

			if (nextDire == Direction.UP) {
				if (Game.this.ghostCheckUp(_inky)) {

					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setY(_inky.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_inky)) {

					Game.this.checkCollision();

					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_inky);
					_inky.setY(_inky.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
							.addItem(_inky);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Resets Clyde's color, sets the target of BFS to the lower right-hand corner,
		 * and moves left, right, up, or down depending on the direction enum returned
		 * from the BFS call.
		 */
		private void clydeScatter() {
			_clyde.changeCol(Color.ORANGE);
			Direction nextDire = null;
			BoardCoordinate lowerRight = new BoardCoordinate(22, 22, false);
			nextDire = _clyde.BFS(lowerRight);

			if (nextDire == Direction.LEFT) {
				if (_clyde.getY() == 11 * Constants.SQUARE_SIZE && _clyde.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckLeft(_clyde)) {

					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(_clyde.getX() - Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.RIGHT) {

				if (_clyde.getY() == 11 * Constants.SQUARE_SIZE && _clyde.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
				if (Game.this.ghostCheckRight(_clyde)) {

					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setX(_clyde.getX() + Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();

				}
			}

			if (nextDire == Direction.UP) {
				if (Game.this.ghostCheckUp(_clyde)) {

					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setY(_clyde.getY() - Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}

			if (nextDire == Direction.DOWN) {
				if (Game.this.ghostCheckDown(_clyde)) {

					Game.this.checkCollision();

					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.removeItem(_clyde);
					_clyde.setY(_clyde.getY() + Constants.SQUARE_SIZE);
					_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
							.addItem(_clyde);

					Game.this.checkCollision();
				}
			}
		}

		/**
		 * Changes the passed-in Ghost's color to light blue. The target of BFS is set
		 * to one of the four corners randomly at every timestep. The ghost turns in the
		 * direction of the enum returned from the BFS call respectively.
		 */
		private void ghostFrightened(Ghost ghost) {
			ghost.changeCol(Color.LIGHTBLUE);
			Direction nextDir = null;
			BoardCoordinate randCorner = null;
			int num = (int) (Math.random() * 4);
			switch (num) {
			case 0:
				randCorner = new BoardCoordinate(0, 0, false);
				break;
			case 1:
				randCorner = new BoardCoordinate(0, 22, false);
				break;
			case 2:
				randCorner = new BoardCoordinate(22, 0, false);
				break;
			case 3:
				randCorner = new BoardCoordinate(22, 22, false);
				break;
			}
			nextDir = ghost.BFS(randCorner);
			if (nextDir == Direction.LEFT) {
				if (ghost.getY() == 11 * Constants.SQUARE_SIZE && ghost.getX() == 0) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setX(22 * Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckLeft(ghost)) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setX(ghost.getX() - Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.RIGHT) {
				if (ghost.getY() == 11 * Constants.SQUARE_SIZE && ghost.getX() == 22 * Constants.SQUARE_SIZE) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setX(Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}

				if (Game.this.ghostCheckRight(ghost)) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setX(ghost.getX() + Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.UP) {
				if (Game.this.ghostCheckUp(ghost)) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setY(ghost.getY() - Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}
			}

			if (nextDir == Direction.DOWN) {
				if (Game.this.ghostCheckDown(ghost)) {
					Game.this.checkCollision();

					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.removeItem(ghost);
					ghost.setY(ghost.getY() + Constants.SQUARE_SIZE);
					_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() / Constants.SQUARE_SIZE)]
							.addItem(ghost);

					Game.this.checkCollision();
				}
			}

		}
	}

	/**
	 * Loops through the ArrayList of the current square that Pacman is in. For each
	 * object stored in the list, its point amount is updated to the point counter,
	 * its collision method is called resulting in its removal from the pane, and
	 * the square's ArrayList is cleared should there be any remainders in it.
	 * Conditions for game over and game win are checked at every call.
	 */
	private void checkCollision() {
		for (int i = 0; i < _board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX()
				/ Constants.SQUARE_SIZE)].getList().size(); i++) {
			_paneOrganizer.updatePoints(_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX()
					/ Constants.SQUARE_SIZE)].getItem(i).returnPoints());
			_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX() / Constants.SQUARE_SIZE)]
					.getItem(i).collide(_pane);
			if (_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX() / Constants.SQUARE_SIZE)]
					.checkStorage()) {
				_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX() / Constants.SQUARE_SIZE)]
						.removeItem(_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX()
								/ Constants.SQUARE_SIZE)].getItem(i));
			}
		}

		if (_paneOrganizer.getLives() == 0) {
			_paneOrganizer.gameOver();
			_pane.removeEventFilter(KeyEvent.KEY_PRESSED, _keyHandler);
			_moving.pause();
			_ghostTimeline.pause();
			_penTimeline.pause();
		}

		if (_dotEnergCount == 0) {
			_paneOrganizer.gameWin();
			_pane.removeEventFilter(KeyEvent.KEY_PRESSED, _keyHandler);
			_moving.pause();
			_ghostTimeline.pause();
			_penTimeline.pause();
		}
	}

	/**
	 * Checks whether Pacman can move to the square left of its current position
	 */
	private Boolean pacCheckLeft() {
		if (_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) ((_pachim.getX() - Constants.SQUARE_SIZE)
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether Pacman can move to the square right of its current position
	 */
	private Boolean pacCheckRight() {
		if (_board[(int) (_pachim.getY() / Constants.SQUARE_SIZE)][(int) (_pachim.getX() + Constants.SQUARE_SIZE)
				/ Constants.SQUARE_SIZE].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether Pacman can move to the square above its current position
	 */
	private Boolean pacCheckUp() {
		if (_board[(int) ((_pachim.getY() - Constants.SQUARE_SIZE) / Constants.SQUARE_SIZE)][(int) (_pachim.getX()
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether Pacman can move to the square below its current position
	 */
	private Boolean pacCheckDown() {
		if (_board[(int) ((_pachim.getY() + Constants.SQUARE_SIZE) / Constants.SQUARE_SIZE)][(int) (_pachim.getX()
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether a Ghost can move to the square left of its current position
	 */
	private Boolean ghostCheckLeft(Collidable ghost) {
		if (_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) ((ghost.getX() - Constants.SQUARE_SIZE)
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether a Ghost can move to the square right of its current position
	 */
	private Boolean ghostCheckRight(Collidable ghost) {
		if (_board[(int) (ghost.getY() / Constants.SQUARE_SIZE)][(int) (ghost.getX() + Constants.SQUARE_SIZE)
				/ Constants.SQUARE_SIZE].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether a Ghost can move to the square above its current position
	 */
	private Boolean ghostCheckUp(Collidable ghost) {
		if (_board[(int) ((ghost.getY() - Constants.SQUARE_SIZE) / Constants.SQUARE_SIZE)][(int) (ghost.getX()
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks whether a Ghost can move to the square below its current position
	 */
	private Boolean ghostCheckDown(Collidable ghost) {
		if (_board[(int) ((ghost.getY() + Constants.SQUARE_SIZE) / Constants.SQUARE_SIZE)][(int) (ghost.getX()
				/ Constants.SQUARE_SIZE)].checkWall() == true) {
			return false;
		} else
			return true;
	}

	/**
	 * Returns the 2D Array of SmartSquares
	 */
	public SmartSquare[][] getBoard() {
		return _board;
	}

	/**
	 * Returns the instance of Pacman
	 */
	public Pachim getPachim() {
		return _pachim;
	}

	/**
	 * Returns the instance of PaneOrganizer
	 */
	public PaneOrganizer getPaneOrg() {
		return _paneOrganizer;
	}

	/**
	 * Decrements the counter keeping track of how many dots and energizers are on
	 * the board
	 */
	public void decDotEnergCount() {
		_dotEnergCount = _dotEnergCount - 1;
	}

	/**
	 * Returns the LinkedList of Ghosts of the pen
	 */
	public LinkedList<Ghost> getPen() {
		return _pen;
	}

	/**
	 * Resets the counter for the pen that releases Ghosts periodically
	 */
	public void resetPenCount() {
		_penCount = 0;
	}

	/**
	 * Removes Blinky from its current square's ArrayList, sets its location to its
	 * initial square outside of the pen, and adds Blinky to that square's ArrayList
	 */
	public void resetBlinky() {
		_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX() / Constants.SQUARE_SIZE)]
				.removeItem(_blinky);
		_blinky.setY(8 * Constants.SQUARE_SIZE);
		_blinky.setX(11 * Constants.SQUARE_SIZE);
		_board[(int) (_blinky.getY() / Constants.SQUARE_SIZE)][(int) (_blinky.getX() / Constants.SQUARE_SIZE)]
				.addItem(_blinky);
	}

	/**
	 * Removes Pinky from its current square's ArrayList, sets its location to its
	 * initial square inside of the pen, and adds Pinky to that square's ArrayList
	 */
	public void resetPinky() {
		_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
				.removeItem(_pinky);
		_pinky.setY(10 * Constants.SQUARE_SIZE);
		_pinky.setX(10 * Constants.SQUARE_SIZE);
		_board[(int) (_pinky.getY() / Constants.SQUARE_SIZE)][(int) (_pinky.getX() / Constants.SQUARE_SIZE)]
				.addItem(_pinky);
		_pen.add(_pinky);
	}

	/**
	 * Removes Inky from its current square's ArrayList, sets its location to its
	 * initial square inside of the pen, and adds Inky to that square's ArrayList
	 */
	public void resetInky() {
		_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
				.removeItem(_inky);
		_inky.setY(10 * Constants.SQUARE_SIZE);
		_inky.setX(11 * Constants.SQUARE_SIZE);
		_board[(int) (_inky.getY() / Constants.SQUARE_SIZE)][(int) (_inky.getX() / Constants.SQUARE_SIZE)]
				.addItem(_inky);
		_pen.add(_inky);
	}

	/**
	 * Removes Clyde from its current square's ArrayList, sets its location to its
	 * initial square inside of the pen, and adds Clyde to that square's ArrayList
	 */
	public void resetClyde() {
		_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
				.removeItem(_clyde);
		_clyde.setY(10 * Constants.SQUARE_SIZE);
		_clyde.setX(12 * Constants.SQUARE_SIZE);
		_board[(int) (_clyde.getY() / Constants.SQUARE_SIZE)][(int) (_clyde.getX() / Constants.SQUARE_SIZE)]
				.addItem(_clyde);
		_pen.add(_clyde);
	}

	/**
	 * Resets Pacman's returned direction enum from the KeyHandler so that Pacman is
	 * not moving after a life is lost
	 */
	public void resetDirection() {
		_direction = null;
	}
}