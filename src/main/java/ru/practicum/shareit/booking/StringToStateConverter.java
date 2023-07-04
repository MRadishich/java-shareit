package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.enums.State;

public class StringToStateConverter implements Converter<String, State> {

    @Override
    public State convert(String value) {
        return State.valueOf(value.toUpperCase());
    }
}
