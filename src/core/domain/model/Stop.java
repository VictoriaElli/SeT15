package domain.model;

import domain.model.util.MathUtil;

/**
 * Representerer et stoppested i rutesystemet.
 * Stoppestedet har et navn, koordinater (bredde- og lengdegrad), og en aktiv status.
 */
public class Stop {
    // --- Felt ---
    private int id;                   // unik identifikator for stoppestedet
    private String name;              // navnet på stoppestedet (f.eks. "Oslo S")
    private double latitude;          // breddegrad for stoppestedet (fra -90 til 90)
    private double longitude;         // lengdegrad for stoppestedet (fra -180 til 180)
    private boolean isActive = true;  // angir om stoppestedet er aktivt eller inaktivt, standard er aktivt

    // --- Konstruktører ---
    /**
     * Konstruktør som brukes når stoppestedet allerede finnes i systemet (med ID).
     *
     * @param id ID for stoppestedet
     * @param name Navn på stoppestedet
     * @param latitude Breddegrad for stoppestedet
     * @param longitude Lengdegrad for stoppestedet
     */
    public Stop(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();  // Validerer at stoppestedet har logiske data
    }

    /**
     * Konstruktør for å opprette et nytt stoppested uten ID.
     *
     * @param name Navn på stoppestedet
     * @param latitude Breddegrad for stoppestedet
     * @param longitude Lengdegrad for stoppestedet
     */
    public Stop(String name, double latitude, double longitude) {
        this.name = name;
        setLatitude(latitude);
        setLongitude(longitude);
        validateStop();
    }

    /**
     * Konstruktør når bare ID og navn er kjent. Koordinater kan legges til senere.
     *
     * @param id ID for stoppestedet
     * @param name Navn på stoppestedet
     */
    public Stop(int id, String name) {
        this(id, name, 0, 0);  // Setter standardverdier for bredde- og lengdegrad
    }

    /**
     * Konstruktør når bare navnet på stoppestedet er kjent. Koordinater kan legges til senere.
     *
     * @param name Navn på stoppestedet
     */
    public Stop(String name) {
        this(name, 0, 0);  // Setter standardverdier for bredde- og lengdegrad
    }

    // --- Validering ---
    /**
     * Validerer stoppestedet for å sikre at dataene er logiske før de lagres.
     * Dette inkluderer å sjekke at navn ikke er tomt, og at koordinatene er innenfor gyldige grenser.
     */
    private void validateStop() {
        // Sjekker at stoppestedet har et gyldig navn.
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Stop name cannot be empty");
        }

        // Sjekker at breddegraden er mellom -90 og 90.
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }

        // Sjekker at lengdegraden er mellom -180 og 180.
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    // --- Getters ---
    /**
     * Henter ID for stoppestedet.
     *
     * @return stoppestedets ID
     */
    public int getId() {
        return id;
    }

    /**
     * Henter navnet på stoppestedet.
     *
     * @return stoppestedets navn
     */
    public String getName() {
        return name;
    }

    /**
     * Henter breddegraden for stoppestedet.
     *
     * @return stoppestedets breddegrad
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Henter lengdegraden for stoppestedet.
     *
     * @return stoppestedets lengdegrad
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Henter om stoppestedet er aktivt eller inaktivt.
     *
     * @return true hvis stoppestedet er aktivt, ellers false
     */
    public boolean isActive() {
        return isActive;
    }

    // --- Setters ---
    /**
     * Setter ID for stoppestedet.
     *
     * @param id ID for stoppestedet
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter navnet på stoppestedet.
     *
     * @param name Navn på stoppestedet
     */
    public void setName(String name) {
        this.name = name;
        validateStop();  // Validere at navnet er gyldig
    }

    /**
     * Setter breddegraden for stoppestedet, og avrunder til 6 desimaler.
     *
     * @param latitude Breddegrad for stoppestedet
     */
    public void setLatitude(double latitude) {
        this.latitude = MathUtil.round(latitude, 6);  // Avrunder breddegraden til 6 desimaler
        validateStop();  // Validere at breddegraden er gyldig
    }

    /**
     * Setter lengdegraden for stoppestedet, og avrunder til 6 desimaler.
     *
     * @param longitude Lengdegrad for stoppestedet
     */
    public void setLongitude(double longitude) {
        this.longitude = MathUtil.round(longitude, 6);  // Avrunder lengdegraden til 6 desimaler
        validateStop();  // Validere at lengdegraden er gyldig
    }

    /**
     * Setter aktiv status for stoppestedet.
     *
     * @param active true for aktivt stoppested, false for inaktivt
     */
    public void setActive(boolean active) {
        isActive = active;
    }


    // --- Overrides ---
    /**
     * Returnerer en lettleselig representasjon av stoppestedet som kan brukes til utskrift.
     * Formatet er: "Navn (breddegrad, lengdegrad)".
     *
     * @return en lesbar streng som representerer stoppestedet
     */
    @Override
    public String toString() {
        return String.format("%s (%.6f, %.6f)", name, latitude, longitude);
    }

    /**
     * Sjekker om to stoppesteder er like. Hvis begge har ID, brukes denne for sammenligning.
     * Hvis ID ikke er satt, sammenlignes navn og koordinater.
     *
     * @param o objektet som skal sammenlignes
     * @return true hvis stoppestedene er like, ellers false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop other = (Stop) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;  // Hvis begge har ID, sammenlign dem
        }

        // Hvis ingen ID, sammenlign navn og koordinater
        return name != null && name.equals(other.name)
                && Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    /**
     * Genererer en hash-kode for stoppestedet. Hvis ID er satt, brukes den til å generere hash.
     * Hvis ikke, brukes navn og koordinater.
     *
     * @return hash-koden for stoppestedet
     */
    @Override
    public int hashCode() {
        if (id != 0) return Integer.hashCode(id);  // Hvis ID er satt, bruk den for hash-kode

        // Hvis ID ikke er satt, generer hash basert på navn og koordinater
        int result = name != null ? name.hashCode() : 0;
        long latBits = Double.doubleToLongBits(latitude);
        long lonBits = Double.doubleToLongBits(longitude);
        result = 31 * result + (int)(latBits ^ (latBits >>> 32));
        result = 31 * result + (int)(lonBits ^ (lonBits >>> 32));
        return result;
    }
}
