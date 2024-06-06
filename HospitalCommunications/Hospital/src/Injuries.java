package src;

import java.util.NoSuchElementException;

public enum Injuries {
    hip, knee, elbow;

    public static Injuries parse (String value) {
        return switch (value) {
            case "hip" -> Injuries.hip;
            case "knee" -> Injuries.knee;
            case "elbow" -> Injuries.elbow;
            default -> throw new NoSuchElementException();
        };
    }

    public static boolean checkCorrectness (String value) {
        try {
            Injuries.parse(value);
            return true;
        }
        catch (NoSuchElementException exception) {
            return false;
        }
    }

}
