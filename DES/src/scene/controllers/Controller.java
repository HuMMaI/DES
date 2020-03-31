package scene.controllers;

import algorithm.Encoder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import scene.windows.KeyWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea firstTextArea;
    @FXML
    private TextArea secondTextArea;
    @FXML
    private Button startButton;

    private static String key;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secondTextArea.setEditable(false);
    }

    @FXML
    public void startAlgorithm(ActionEvent actionEvent) {
        if (firstTextArea.getText().isEmpty()){
            alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                    "Input field is empty.\nPlease, write some text and try again!");
            return;
        } else if (key == null){
            alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                    "Input field is empty.\nPlease, enter key and try again!");
            return;
        }

        Encoder encoder = new Encoder();
        StringBuilder encryptedText = encoder.startEncoder(new StringBuilder(firstTextArea.getText()), key);

        secondTextArea.setText(encryptedText.toString());
    }

    private void alertBox(Alert alert, String title, String headerText, String message) {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void setKey(ActionEvent actionEvent) throws Exception {
        KeyWindow keyWindow = new KeyWindow();
        keyWindow.start();
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        Controller.key = key;
    }
}
