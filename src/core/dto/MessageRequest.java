package dto;

// Dette er et DTO som brukes for å ta imot nye driftsmeldinger fra frontend
public class MessageRequest {
    public String message;
    public int routeId;
    public String validFrom;
    public String validTo;
    public boolean isActive;
}
