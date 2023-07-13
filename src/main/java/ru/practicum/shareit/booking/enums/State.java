package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    APPROVED,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;


    public static State getEnum(String name) {
        for (State value : State.values()) {
            if (name.equalsIgnoreCase(value.name())) {
                return value;
            }
        }

        throw new IllegalArgumentException("Unknown state: " + name);
    }
}
