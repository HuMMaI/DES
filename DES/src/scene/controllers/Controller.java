package scene.controllers;

import algorithm.DESMode;
import algorithm.Encoder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import scene.windows.KeyWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea firstTextArea;
    @FXML
    private TextArea secondTextArea;
    @FXML
    private TextArea firstBinTextArea;
    @FXML
    private TextArea secondBinTextArea;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox algorithmMode;

    private static String key;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secondTextArea.setEditable(false);
        secondBinTextArea.setEditable(false);
    }

    @FXML
    public void startAlgorithm(ActionEvent actionEvent) {
        String mode = algorithmMode.getValue() == null ? "" : algorithmMode.getValue().toString();

        if (firstTextArea.getText().isEmpty() && firstBinTextArea.getText().isEmpty()){
            alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                    "Input field is empty.\nPlease, write some text and try again!");
            return;
        } else if (key == null){
            alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                    "Input field is empty.\nPlease, enter key and try again!");
            return;
        } else if (mode.isEmpty()){
            alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                    "Mode not selected.\nPlease, select a mode and try again!");
            return;
        }

        if (!firstBinTextArea.getText().isEmpty()){
            char[] binChars = firstBinTextArea.getText().toCharArray();
            List<Character> binList = new ArrayList<>();

            for (char bin: binChars) {
                binList.add(bin);
            }

            Character result = binList.stream()
                    .filter(bin -> !bin.equals('0'))
                    .filter(bin -> !bin.equals('1'))
                    .findAny()
                    .orElse(null);

            if (result != null){
                alertBox(new Alert(Alert.AlertType.ERROR), "Error", null,
                        "Binary text set incorrectly.\nPlease, try again!");
                return;
            }
        }

        Encoder encoder = new Encoder();
        StringBuilder[] strBinMapping = {
                new StringBuilder(firstTextArea.getText()),
                new StringBuilder(firstBinTextArea.getText())
        };

        String[] encryptedText =
                encoder.startEncoder(strBinMapping, key, DESMode.valueOf(mode.toUpperCase()));

        secondTextArea.setText(encryptedText[0]);
        secondBinTextArea.setText(encryptedText[1]);
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
