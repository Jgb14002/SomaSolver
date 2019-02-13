package application;

import controllers.UIController;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SomaController extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/ui.fxml")));
		stage.getIcons().add(new Image(SomaController.class.getResourceAsStream("/images/icon.png")));
		stage.setTitle("Soma Solver - Controller");
		stage.setResizable(false);
		stage.setScene(new Scene(loader.load()));

		UIController controller = loader.getController();
		controller.setStage(stage);
		stage.show();
	}
}
