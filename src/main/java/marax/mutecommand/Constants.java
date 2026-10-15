package marax.mutecommand;

public interface Constants {
    String[] TIMEUNITS = {
        "seconds",
        "minutes",
        "hours",
        "days",
        "weeks",
        "months",
        "years"
    };

    int[] TIMEUNITS_TOSECONDS = {
        1,
        60,
        60 * 60,
        60 * 60 * 24,
        60 * 60 * 24 * 7,
        60 * 60 * 24 * 7 * 4,
        60 * 60 * 24 * 7 * 4 * 12
    };
}