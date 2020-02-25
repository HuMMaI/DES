package scene.config;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KeyWindow{

    private static Stage keyStage = new Stage();

    public void start() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("setKeyWindow.fxml"));
        keyStage.setTitle("Key");
        keyStage.setScene(new Scene(root, 350, 150));
        keyStage.setResizable(false);
        keyStage.show();
    }

    public static Stage getKeyStage() {
        return keyStage;
    }
}
