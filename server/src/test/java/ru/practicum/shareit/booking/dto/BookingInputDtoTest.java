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
class BookingInputDtoTest {

    @Autowired
    private JacksonTester<BookingInputDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testBookingInputDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now();
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                currentTime.minusDays(1),
                currentTime.plusDays(1)
        );

        //when
        JsonContent<BookingInputDto> content = jacksonTester.write(bookingInputDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo(currentTime.plusDays(1).withNano(0).toString());
    }
}