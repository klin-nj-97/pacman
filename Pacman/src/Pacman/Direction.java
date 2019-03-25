package Pacman;

/**
 * Enums to represent the directions Pacman and the Ghosts are moving in
 */
public enum Direction {
	LEFT, RIGHT, UP, DOWN;
	
	/**
	 * Returns the opposite direction to be used in Ghost's BFS method to prevent
	 * 180-degree turns
	 */
	public Direction getOpposite() {
		
		switch (this) {
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			case UP:
				return DOWN;
			case DOWN:
				return UP;
		}
		return this;
	}
}
