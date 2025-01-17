package supply.server.data.utils;

import supply.server.configuration.exception.IncorrectParameterException;

public enum Unit {
    PCS("шт"),

    KG("кг"),
    G("г"),

    L("л"),
    ML("мл"),
    M3("м³"),
    CM3("см³"),

    M("м"),
    CM("см"),
    MM("мм"),

    M2("м²"),
    CM2("см²");

    private final String shortName;

    Unit(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public static Unit fromString(String shortName) {
        for (Unit unit : Unit.values()) {
            if (unit.getShortName().equalsIgnoreCase(shortName)) {
                return unit;
            }
        }
        throw new IncorrectParameterException("Unknown unit: " + shortName);
    }
}