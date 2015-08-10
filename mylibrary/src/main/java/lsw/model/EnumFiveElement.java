package lsw.model;

/**
 * Created by swli on 8/10/2015.
 */
public enum EnumFiveElement {
    Metal,
    Wood,
    Water,
    Fire,
    Earth;

    public static EnumFiveElement toEnum(String value) {
        if (value.equals("fire")) {
            return Fire;
        } else if (value.equals("water")) {
            return Water;
        } else if (value.equals("wood")) {
            return Wood;
        } else if (value.equals("earth")) {
            return Earth;
        } else if (value.equals("metal")) {
            return Metal;
        } else {
            return null;
        }
    }
}
