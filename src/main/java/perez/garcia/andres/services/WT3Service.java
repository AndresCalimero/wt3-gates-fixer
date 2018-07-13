package perez.garcia.andres.services;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import perez.garcia.andres.models.Airport;
import perez.garcia.andres.models.Gate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WT3Service {

    private enum Procedure {ARRIVAL, DEPARTURE}

    private static DoubleProperty progress = new SimpleDoubleProperty(0);

    private WT3Service() {
    }

    public static void fixGates(Path xplanePath, Airport airport) throws Exception {
        progress.setValue(0);
        File groundRoutesFile = xplanePath.resolve("ClassicJetSimUtils").resolve("WorldTraffic").resolve("GroundRoutes").toFile();
        if (groundRoutesFile.exists()) {
            fixGatesOfProcedure(Procedure.ARRIVAL, groundRoutesFile, airport);
            fixGatesOfProcedure(Procedure.DEPARTURE, groundRoutesFile, airport);
        } else {
            throw new RuntimeException("The GroundRoutes folder does not exist.");
        }
    }

    private static void fixGatesOfProcedure(Procedure procedure, File groundRoutesFile, Airport airport) throws Exception {
        File airportGroundRoutesFile = groundRoutesFile.toPath().resolve((procedure == Procedure.ARRIVAL ? "Arrival" : "Departure")).resolve(airport.getICAO()).toFile();
        if (airportGroundRoutesFile.exists()) {
            File[] groundRoutesFiles = airportGroundRoutesFile.listFiles();
            if (groundRoutesFiles != null) {
                long numberOfFiles = groundRoutesFiles.length * 2;
                for (File groundRoute : groundRoutesFiles) {
                    fixGate(procedure, groundRoute, airport);
                    progress.set(progress.doubleValue() + 1.0 / numberOfFiles);
                }
            }
        } else {
            throw new RuntimeException("The airport does not have ground routes.");
        }
    }

    private static void fixGate(Procedure procedure, File groundRouteFile, Airport airport) throws Exception {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(groundRouteFile.toPath(), StandardCharsets.UTF_8));
        boolean gateLineFound = false;

        for (int i = 0; i < fileContent.size(); ) {
            String line = fileContent.get(i);
            if (gateLineFound) {
                String[] lineParts = line.split(" ");
                Gate gate = airport.getGate(lineParts[6]);
                if (gate != null) {
                    String newGateLine = gate.getLat() + " " + gate.getLon() + " " + lineParts[2] + " " + gate.getHeading() + " " + lineParts[4] + " " + lineParts[5] + " " + lineParts[6];
                    //System.out.println(line + " -> " + newGateLine);
                    fileContent.set(i, newGateLine);
                }
                break;
            } else if (procedure == Procedure.DEPARTURE && line.equals("STARTSTEERPOINTS")) {
                gateLineFound = true;
                i++;
            } else if (procedure == Procedure.ARRIVAL && line.equals("ENDSTEERPOINTS")) {
                gateLineFound = true;
                i--;
            } else {
                i++;
            }
        }

        Files.write(groundRouteFile.toPath(), fileContent, StandardCharsets.UTF_8);
    }

    public static double getProgress() {
        return progress.get();
    }

    public static DoubleProperty progressProperty() {
        return progress;
    }
}
