package domain.model;

/**
 * Representerer type unntak eller statusendring for ruter eller avganger.
 *
 * Brukes i {@link ExceptionEntry} for å angi hva slags avvik som gjelder:
 * - ekstra avganger
 * - forsinkelser
 * - kanselleringer
 * - utelatelser fra ruteplanen
 */
public enum ExceptionType {

    /**
     * Ekstra avganger eller uplanlagte ruter som legges til.
     * Typisk brukt når det kjøres flere avganger enn normalt på en dag.
     */
    EXTRA,

    /**
     * Forsinket avgang eller lengre ventetid enn planlagt.
     * Brukes for å indikere at avgangen ikke går på planlagt tidspunkt.
     */
    DELAYED,

    /**
     * Kansellert avgang eller rute.
     * Angir at den planlagte avgangen ikke gjennomføres.
     */
    CANCELLED,

    /**
     * Utelatt avgang eller rute fra ruteplanen.
     * Skiller seg fra CANCELLED ved at avgangen kanskje aldri var planlagt på denne dagen.
     */
    OMITTED
}
