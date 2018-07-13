package perez.garcia.andres.models;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Airport {

    private String ICAO;
    private File aptData;
    private List<Gate> gates = new ArrayList<>();
    private boolean gatesExtracted = false;

    public Airport(File aptData) throws Exception {
        this.aptData = aptData;
        extractICAO();
    }

    private void extractICAO() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(aptData);

        Element objectsElement = document.getRootElement().getChild("objects");

        for (Element object : objectsElement.getChildren("object")) {
            if (object.getAttributeValue("class").equals("WED_Airport") && object.getChild("airport").getAttributeValue("kind").equalsIgnoreCase("Airport")) {
                ICAO = object.getChild("airport").getAttributeValue("icao");
                break;
            }
        }
    }

    private void extractGates() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(aptData);

        Element objectsElement = document.getRootElement().getChild("objects");

        for (Element object : objectsElement.getChildren("object")) {
            if (object.getAttributeValue("class").equalsIgnoreCase("WED_RampPosition")) {
                String type = object.getChild("ramp_start").getAttributeValue("type");
                if (type.equalsIgnoreCase("Tie-Down") || type.equalsIgnoreCase("Gate")) {
                    String gateName = object.getChild("hierarchy").getAttributeValue("name");
                    String lat = object.getChild("point").getAttributeValue("latitude");
                    String lon = object.getChild("point").getAttributeValue("longitude");
                    String heading = object.getChild("point").getAttributeValue("heading");
                    gates.add(new Gate(gateName, lat, lon, heading));
                }
            }
        }
        gatesExtracted = true;
    }

    public List<Gate> getGates() throws Exception {
        if (!gatesExtracted) extractGates();
        return gates;
    }

    public Gate getGate(String name) throws Exception {
        for (Gate gate : getGates()) {
            if (gate.getName().equalsIgnoreCase(name)) {
                return gate;
            }
        }

        return null;
    }

    public String getICAO() {
        return ICAO;
    }

    @Override
    public String toString() {
        return "[" + getICAO() + "] " + aptData.getParentFile().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(ICAO, airport.ICAO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ICAO);
    }
}
