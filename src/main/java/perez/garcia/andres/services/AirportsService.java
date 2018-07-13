package perez.garcia.andres.services;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import perez.garcia.andres.models.Airport;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AirportsService {

    private AirportsService() {}

    public static List<Airport> loadAirports(Path xplanePath) throws Exception {
        List<Airport> airports = new ArrayList<>();
        File customSceneryFile = xplanePath.resolve("Custom Scenery").toFile();
        if (customSceneryFile.exists()) {
            File[] customSceneries = customSceneryFile.listFiles();

            for (File customScenery : Objects.requireNonNull(customSceneries)) {
                File aptData = new File(customScenery, "earth.wed.xml");
                if (aptData.exists() && isAnAirport(aptData)) {
                    airports.add(new Airport(aptData));
                }
            }
        } else {
            throw new RuntimeException("Invalid X-Plane folder.");
        }

        return airports;
    }

    private static boolean isAnAirport(File aptData) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(aptData);

            Element objectsElement = document.getRootElement().getChild("objects");
            boolean hasOnlyOneAirport = false;
            for (Element object : objectsElement.getChildren("object")) {
                if (object.getAttributeValue("class").equals("WED_Airport") && object.getChild("airport").getAttributeValue("kind").equals("Airport")) {
                    if (hasOnlyOneAirport) {
                        hasOnlyOneAirport = false;
                        break;
                    }
                    hasOnlyOneAirport = true;
                }
            }
            return hasOnlyOneAirport;

        } catch (Exception e) {
            return false;
        }
    }
}
