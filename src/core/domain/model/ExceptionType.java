package domain.model;

/**
 * Enum representing different types of exceptions or status updates for routes or services.
 */
public enum ExceptionType {
    // An extra departure or an unscheduled route
    EXTRA,

    // A delayed departure or an extended wait
    DELAYED,

    // A cancelled departure or route
    CANCELLED,

    // A departure or route omitted from the schedule
    OMITTED
}
