package kosa.team4.gcs.serviceDBRead;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import kosa.team4.gcs.main.GcsMain;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceDBReadController implements Initializable {
    @FXML
    private Button btnOK;
    @FXML
    private Button btnCancel;
    @FXML private WebView webView;

    private WebEngine webEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOK.setOnAction(btnOKEventHandler);
        btnCancel.setOnAction(btnCancelEventHandler);
        initWebView();
    }

    public void initWebView() {
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("java", this); //얘가 중요! WEBVIEW 안에서 JAVA. 메소드를 실행하면. this는 serviceRequestListcontroller
            webEngine.executeScript("console.log = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
        });
        //HTML이 렌더링다 다 되면 (LOAD가 완료되면) addListener 실행
        webEngine.load("http://106.253.56.124:8084/FinalWebProject/management/gcsManagementList");
    }

    private EventHandler<ActionEvent> btnOKEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        }
    };

    private EventHandler<ActionEvent> btnCancelEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    };

    public void log(String text) {
        System.out.println(text);
    }

    public void receiveMissionInfo(String json) {
        GcsMain.instance.controller.handleMessage(json);
    }

    public void windowClose() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}
