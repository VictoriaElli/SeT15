package domain.model;

public enum TimeMode {
    DEPART,   // ønsket tidspunkt = avgang fra fromStop
    ARRIVAL,  // ønsket tidspunkt = ankomst til toStop
    NOW       // ønsket tidspunkt = nå
}
