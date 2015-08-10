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
        switch (value) {
            case "fire":
                return Fire;
            case "water":
                return Water;
            case "wood":
                return Wood;
            case "earth":
                return Earth;
            case "metal":
                return Metal;
            default:
                return null;
        }
    }
}
