package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingInputDto> {
    @Override
    public boolean isValid(BookingInputDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}