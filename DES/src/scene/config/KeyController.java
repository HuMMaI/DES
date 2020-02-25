package scene.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sun.awt.SunHints;

public class KeyController {
    @FXML
    private TextField keyField;
    @FXML
    private Button btnAccept;

    @FXML
    public void accept(ActionEvent actionEvent) {
        if (keyField.getText().isEmpty()){
            Alert alert  = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Input field is empty.\nPlease, enter key and try again!");
            alert.showAndWait();
        } else {
            Controller.setKey(keyField.getText());
            KeyWindow.getKeyStage().close();
        }
    }
}
