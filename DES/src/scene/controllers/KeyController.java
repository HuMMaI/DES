package scene.controllers;

import algorithm.enums.DESMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import scene.windows.KeyWindow;

import java.math.BigInteger;

public class KeyController {
    @FXML
    private TextField keyField;
    @FXML
    private Button btnAccept;
    @FXML
    private CheckBox keyMode;

    @FXML
    public void accept(ActionEvent actionEvent) {
        if (keyField.getText().isEmpty()){
            Alert alert  = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Input field is empty.\nPlease, enter key and try again!");
            alert.showAndWait();
        } if (new BigInteger(keyField.getText().getBytes()).toString(2).length() > 64){
            Alert alert  = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error");
            alert.showAndWait();
        } else {
            Controller.setKey(keyField.getText());

            if (keyMode.isSelected()){
                Controller.setKeyMode(DESMode.KEY_CYRILLIC);
            } else {
                Controller.setKeyMode(DESMode.KEY_ENG);
            }

            KeyWindow.getKeyStage().close();
        }
    }
}
