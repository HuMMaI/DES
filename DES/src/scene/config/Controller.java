package scene.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea firstTextArea;
    @FXML
    private TextArea secondTextArea;
    @FXML
    private Button startButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secondTextArea.setEditable(false);
    }

    @FXML
    public void startAlgorithm(ActionEvent actionEvent) {
        if (firstTextArea.getText().isEmpty()){
            Alert alert  = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Input field is empty.\nPlease, write some text and try again!");
            alert.showAndWait();
        }
    }
}
