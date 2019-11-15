package kosa.team4.gcs.serviceRequestList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kosa.team4.gcs.main.GcsMain;

public class ServiceRequestList {
    //Field
    private Stage stage;

    //Constructor
    public ServiceRequestList() {
        try {
            stage = new Stage(StageStyle.UTILITY);
            //stage.initModality(Modality.WINDOW_MODAL);
            //stage.initOwner(GcsMain.instance.primaryStage);
            BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("ServiceRequestList.fxml"));
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception e) {}
    }
    //Method
    public void show() {

        stage.show();
    }
    public void close() {

        stage.close();
    }
}
