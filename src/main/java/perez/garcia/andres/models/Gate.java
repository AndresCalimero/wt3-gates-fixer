package perez.garcia.andres.models;

import java.util.Objects;

public class Gate {

    private String name;
    private String lat, lon, heading;

    public Gate(String name, String lat, String lon, String heading) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.heading = heading;
    }

    public String getName() {
        return name.replace(" ", "_");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return normalize(lat);
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return normalize(lon);
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getHeading() {
        return heading.split("\\.")[0];
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    private String normalize(String coord) {
        String[] parts = coord.split("\\.");
        if (parts[1].length() > 8) {
            return parts[0] + "." + parts[1].substring(0, 8);
        } else {
            return coord;
        }
    }

    @Override
    public String toString() {
        return getLat() + " " + getLon() + " " + getHeading() + " " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate gate = (Gate) o;
        return Objects.equals(name, gate.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
