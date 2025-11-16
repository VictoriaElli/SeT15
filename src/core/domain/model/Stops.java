package domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import domain.model.util.MathUtil;

/**
 * Representerer et stoppested i rutesystemet med navn, koordinater og aktiv status.
 */
@Entity
public class Stops {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                   // Unik ID generert av databasen

    @Column(name = "name")
    private String name;              // Navn på stoppestedet

    @Column(name = "latitude")
    private double latitude;          // Breddegrad (-90 til 90)

    @Column(name = "longitude")
    private double longitude;         // Lengdegrad (-180 til 180)

    @Column(name = "isActive")
    private boolean isActive = true;  // Om stoppestedet er aktivt (standard: true)

    // --- Konstruktører ---

    /**
     * Full konstruktør når stoppestedet eksisterer med ID.
     */
    public Stops(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();
    }

    /**
     * Konstruktør for nytt stoppested uten ID (ID genereres av databasen).
     */
    public Stops(String name, double latitude, double longitude) {
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();
    }

    /**
     * Konstruktør når kun ID og navn er kjent.
     */
    public Stops(int id, String name) { this(id, name, 0, 0); }

    /**
     * Konstruktør når kun navn er kjent.
     */
    public Stops(String name) { this(name, 0, 0); }

    // --- Validering ---

    /**
     * Sjekker at navn ikke er tomt og at koordinater er innenfor gyldige grenser.
     */
    private void validateStop() {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Stop name cannot be empty");
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Latitude must be between -90 and 90");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }

    // --- Gettere ---

    public int getId() { return id; }

    public String getName() { return name; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public boolean isActive() { return isActive; }

    // --- Settere ---

    /**
     * Setter ID manuelt (brukes sjelden, normalt generert av databasen).
     */
    public void setId(int id) { this.id = id; }

    /**
     * Setter navn og validerer.
     */
    public void setName(String name) {
        this.name = name;
        validateStop();
    }

    /**
     * Setter breddegrad og avrunder til 6 desimaler.
     */
    public void setLatitude(double latitude) {
        this.latitude = MathUtil.round(latitude, 6);
        validateStop();
    }

    /**
     * Setter lengdegrad og avrunder til 6 desimaler.
     */
    public void setLongitude(double longitude) {
        this.longitude = MathUtil.round(longitude, 6);
        validateStop();
    }

    /**
     * Setter aktiv status for stoppestedet.
     */
    public void setActive(boolean active) { isActive = active; }

    // --- Overrides ---

    /**
     * Returnerer stoppested som "Navn (lat, lon)".
     */
    @Override
    public String toString() {
        return String.format("%s (%.6f, %.6f)", name, latitude, longitude);
    }

    /**
     * To stoppesteder regnes som like hvis ID er lik, eller navn og koordinater matcher.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stops)) return false;
        Stops other = (Stops) o;

        if (this.id != 0 && other.id != 0) return this.id == other.id;

        return name != null && name.equals(other.name)
                && Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    /**
     * Hash genereres fra ID hvis tilgjengelig, ellers fra navn og koordinater.
     */
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
