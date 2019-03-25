package Pacman; // In Pacman.java

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class runs the application by instantiating the top-level object, 
 * setting up the scene, and showing the stage.
 */
public class App extends Application {

	/**
     * Overrides the parent class' start method in order to apply the
     * specifications of this application.
     */
	@Override
    public void start(Stage stage) {
    
    PaneOrganizer organizer = new PaneOrganizer();
    stage.setTitle("Pacman");
    Scene scene = new Scene(organizer.getRoot());
    stage.setScene(scene);
    stage.show();
    }

    public static void main(String[] argv) {
        launch(argv);
    }
}
