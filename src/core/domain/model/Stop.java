package org.byferge.core.domain.model;

import org.byferge.core.domain.model.util.MathUtil;

// representerer et stoppested i rutesystemet, med navnm koordinater og status.
public class Stop {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private boolean isActive = true;

    // Constructors
    // brukes når stoppestedet allerede finnes i systemet.
    public Stop(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();
    }

    // brukes for å opprette et nytt stoppested uten ID.
    public Stop(String name, double latitude, double longitude) {
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();
    }

    // brukes når bare ID og navn er kjent - koordinater kan legges til senere.
    public Stop(int id, String name) {
        this(id, name, 0, 0);
    }

    // brukes når bare navnet er kjent - koordinater kan legges til senere.
    public Stop(String name) {
        this(name, 0, 0);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        validateStop();
    }

    public void setLatitude(double latitude) {
        this.latitude = MathUtil.round(latitude, 6);
        validateStop();
    }

    public void setLongitude(double longitude) {
        this.longitude = MathUtil.round(longitude, 6);
        validateStop();
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    // Validation
    // metode som sjekker at datene for stoppestedet er logiske før det lagres
    private void validateStop() {
        //denne sjekker at stoppet har et navn.
        //hvis ikke den har et navn, sendes en feilmelding
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Stop name cannot be empty");
        }
        //denne sjekker at breddegraden er en riktig verdi.
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        //denne sjekker at lengdegraden er en riktig verdi.
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    // Overrides
    // returnerer lesbar print av stoppestedet
    @Override
    public String toString() {
        return String.format("%s (%.6f, %.6f)", name, latitude, longitude);
    }

    // sjekker om to objekter er like: hvis begge har id brukes denne, hvis ikke, brukes navn og koordinater.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop other = (Stop) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        return name != null && name.equals(other.name)
                && Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    // genererer en hash basert på ID hvis den finnes, ellers på navn og koordinater.
    @Override
    public int hashCode() {
        if (id != 0) return Integer.hashCode(id);

        int result = name != null ? name.hashCode() : 0;
        long latBits = Double.doubleToLongBits(latitude);
        long lonBits = Double.doubleToLongBits(longitude);
        result = 31 * result + (int)(latBits ^ (latBits >>> 32));
        result = 31 * result + (int)(lonBits ^ (lonBits >>> 32));
        return result;
    }

}
