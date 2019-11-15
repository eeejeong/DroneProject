package kosa.team4.gcs.serviceMagnetOnOff;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceMagnetOnOffController implements Initializable {
    @FXML
    private Button btnOK;
    @FXML
    private Button btnCancel;
    @FXML
    private Button MagOn;
    @FXML
    private Button MagOff;

    private ElectroMagnet electroMagnet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOK.setOnAction(btnOKEventHandler);
        btnCancel.setOnAction(btnCancelEventHandler);
        MagOn.setOnAction(MagOnEventHandler);
        MagOff.setOnAction(MagOffEventHandler);
        try {
            electroMagnet = new ElectroMagnet();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private EventHandler<ActionEvent> MagOnEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            electroMagnet.magnetOn();
        }
    };
    private EventHandler<ActionEvent> MagOffEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            electroMagnet.magnetOff();
        }
    };
}
