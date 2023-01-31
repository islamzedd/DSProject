package xml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.lang.Runnable;

public class Main extends Application{
	
	@Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader fxmlLoader=new FXMLLoader(Main.class.getResource("Main.fxml"));
        Parent root=fxmlLoader.load();
        Controller control=(Controller) fxmlLoader.getController();
        control.init(stage);
        Scene s=new Scene(root);
        stage.setScene(s);
        stage.setResizable(false);
        stage.setTitle("XML ");
        stage.show();
        
    }

	public static void main(String[] args) {
		launch(args);
	}

}
