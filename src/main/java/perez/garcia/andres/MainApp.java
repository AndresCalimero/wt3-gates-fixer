package perez.garcia.andres;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import perez.garcia.andres.images.Images;
import perez.garcia.andres.controllers.Controller;

import java.io.IOException;

public class MainApp extends Application {

    public static final String TITLE = "WT3 Gates Fixer";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(Images.ICON);
        primaryStage.setTitle(TITLE);
        primaryStage.setMinHeight(250);
        primaryStage.setMinWidth(410);
        primaryStage.initStyle(StageStyle.DECORATED);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/main.fxml"));
            BorderPane rootLayout = (BorderPane) loader.load();

            Controller controller = loader.getController();
            controller.setMainApp(this);
            controller.setStage(primaryStage);

            Scene scene = new Scene(rootLayout, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.show();

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
