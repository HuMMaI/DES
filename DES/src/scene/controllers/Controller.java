package scene.controllers;

import algorithm.DESMode;
import algorithm.AlgorithmCore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import scene.windows.KeyWindow;

import java.net.URL;
import java.util.ArrayList;
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
    @FXML
    private CheckBox textMode;

    private static String key;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secondTextArea.setEditable(false);
        secondBinTextArea.setEditable(false);

        firstTextArea.setWrapText(true);
        secondTextArea.setWrapText(true);
        firstBinTextArea.setWrapText(true);
        secondBinTextArea.setWrapText(true);

        alertBox(new Alert(Alert.AlertType.INFORMATION), "Info", null,
                "The binary result will be automatically copied to the clipboard." +
                        "\nYou can switch to another mode and insert it.");
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

        if (textMode.isSelected()){
            mode = String.format("%s_CYRILLIC", mode);
        }

        AlgorithmCore encoder = new AlgorithmCore();
        StringBuilder[] strBinMapping = {
                new StringBuilder(firstTextArea.getText()),
                new StringBuilder(firstBinTextArea.getText())
        };

        String[] encryptedText =
                encoder.startAlgorithmCore(strBinMapping, key, DESMode.valueOf(mode.toUpperCase()));

        secondTextArea.setText(encryptedText[0]);
        secondBinTextArea.setText(encryptedText[1]);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(encryptedText[1]);
        clipboard.setContent(content);
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

    public void checkBoxListener(ActionEvent actionEvent) {
        firstTextArea.clear();
        secondTextArea.clear();
        firstBinTextArea.clear();
        secondBinTextArea.clear();
    }
}
