package Pacman;

import javafx.scene.layout.Pane;

/**
 * An interface to be used for every instance of a Dot, Energizer, and Ghost as
 * all interact with Pacman
 */
public interface Collidable {
	public void setY(double y);

	public void setX(double x);

	public void collide(Pane pane);

	public int returnPoints();

	public Direction BFS(BoardCoordinate targetCell);

	public double getY();

	public double getX();
}
