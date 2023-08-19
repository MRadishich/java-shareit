package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInnerDtoTest {

    @Autowired
    private JacksonTester<BookingInnerDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testBookingInnerDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now();
        BookingInnerDto bookingInnerDto = new BookingInnerDto(
                1L,
                10L,
                currentTime.minusDays(1),
                currentTime.plusDays(1)
        );

        //when
        JsonContent<BookingInnerDto> content = jacksonTester.write(bookingInnerDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(10);
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo(currentTime.plusDays(1).withNano(0).toString());
    }
}