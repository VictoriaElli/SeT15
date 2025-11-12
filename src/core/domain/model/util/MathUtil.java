package domain.model.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * En hjelpeklasse for å håndtere avrunding av desimaltall.
 * Bruker BigDecimal for presis avrunding og gir mulighet for å angi antall desimaler.
 */
public class MathUtil {

    /**
     * Avrunder en verdi til et spesifisert antall desimaler.
     *
     * @param value Verdien som skal avrundes
     * @param places Antall desimaler verdien skal avrundes til
     * @return Den avrundede verdien som en double
     * @throws IllegalArgumentException hvis 'places' er mindre enn 0
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Antall desimaler kan ikke være negativ");

        // Bruker BigDecimal for å håndtere avrunding med ønsket presisjon
        BigDecimal bd = BigDecimal.valueOf(value);

        // Setter ønsket antall desimaler og spesifiserer avrundingsmodus
        bd = bd.setScale(places, RoundingMode.HALF_UP);  // RoundingMode.HALF_UP betyr "rund opp" når det er midt i mellom
        return bd.doubleValue();  // Konverterer tilbake til double og returnerer
    }
}
