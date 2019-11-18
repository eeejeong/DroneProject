package syk.gcs.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AlertDialog {
    private static Logger logger = LoggerFactory.getLogger(AlertDialog.class);
    public static Optional<ButtonType> optional;
    public static Alert showOkButton(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.NONE, contentText,new ButtonType("승인", ButtonBar.ButtonData.OK_DONE), new ButtonType("거절", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setTitle(title);
        optional = alert.showAndWait();
        return alert;
    }

    public static Alert showNoButton(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.NONE, contentText);
        alert.setTitle(title);
        //프로그램에서 close() 메소드를 호출할 때 자동으로 다이얼로그가 닫히도록 설정
        alert.setResult(ButtonType.OK);
        alert.show();
        return alert;
    }
}
