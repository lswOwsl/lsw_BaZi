package lsw.model;

/**
 * Created by swli on 8/10/2015.
 */
public enum EnumFiveElement {
    Metal("金"),
    Wood("木"),
    Water("水"),
    Fire("火"),
    Earth("土");

    String text;
    EnumFiveElement(String text)
    {
        this.text = text;
    }

    public String toString() {
        return String.format(text);
    }

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
