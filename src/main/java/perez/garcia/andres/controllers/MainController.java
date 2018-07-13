package perez.garcia.andres.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import perez.garcia.andres.models.Airport;
import perez.garcia.andres.services.AirportsService;
import perez.garcia.andres.services.WT3Service;

import java.nio.file.Paths;
import java.util.List;

public class MainController extends Controller {

    @FXML
    private TextField xplanePathTextField;

    @FXML
    private Button fixGatesButton;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox<Airport> airportComboBox;

    @FXML
    private ProgressBar progressBar;

    @Override
    protected void loaded() {


    }

    private void loadAirports() {
        airportComboBox.promptTextProperty().setValue("Loading...");
        fixGatesButton.disableProperty().set(true);
        loadButton.disableProperty().set(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        Task loadAirportsTask = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    List<Airport> airports = AirportsService.loadAirports(Paths.get(xplanePathTextField.getText()));
                    Platform.runLater(() -> {
                        if (!airports.isEmpty()) {
                            airportComboBox.setItems(FXCollections.observableArrayList(airports));
                            airportComboBox.getSelectionModel().selectFirst();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.initOwner(getStage());
                            alert.setTitle("Warning");
                            alert.setHeaderText("Cannot find any airport!");
                            alert.setContentText("Unable to find any compatible airport in the Custom Scenery folder.");
                            alert.showAndWait();
                            airportComboBox.promptTextProperty().setValue("No airports found.");
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initOwner(getStage());
                        alert.setTitle("Error");
                        alert.setHeaderText("Cannot load airports!");
                        alert.setContentText("There was an error trying to get the airports:\n\n" + e.getMessage());
                        alert.showAndWait();
                    });
                } finally {
                    Platform.runLater(() -> {
                        progressBar.setProgress(0);
                        fixGatesButton.disableProperty().set(false);
                        loadButton.disableProperty().set(false);
                    });
                }
                return null;
            }
        };

        new Thread(loadAirportsTask).start();
    }

    @FXML
    private void onLoadButtonActionPerformed() {
        loadAirports();
    }

    @FXML
    private void onFixGatesButtonActionPerformed() {
        fixGatesButton.disableProperty().set(true);
        loadButton.disableProperty().set(true);
        progressBar.progressProperty().bind(WT3Service.progressProperty());

        Task fixGatesTask = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Airport airport = airportComboBox.getSelectionModel().getSelectedItem();
                    WT3Service.fixGates(Paths.get(xplanePathTextField.getText()), airport);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(getStage());
                        alert.setTitle("Info");
                        alert.setHeaderText("Gates successfully fixed!");
                        alert.setContentText("Gates of " + airport.getICAO() + " successfully fixed.");
                        alert.showAndWait();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initOwner(getStage());
                        alert.setTitle("Error");
                        alert.setHeaderText("Cannot fix gates!");
                        alert.setContentText("There was an error trying to fix the gates of the airport:\n\n" + e.getMessage());
                        alert.showAndWait();
                    });
                } finally {
                    Platform.runLater(() -> {
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(0);
                        fixGatesButton.disableProperty().set(false);
                        loadButton.disableProperty().set(false);
                    });
                }
                return null;
            }
        };

        new Thread(fixGatesTask).start();
    }

}
