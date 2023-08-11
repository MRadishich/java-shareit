package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutputDtoTest {

    @Autowired
    private JacksonTester<BookingOutputDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testBookingOutputDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now();

        UserDto userDto = new UserDto(
                1L,
                "new user",
                "user@email.ru"
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "item",
                "new item",
                true,
                3L,
                new BookingInnerDto(1L, 1L, currentTime.minusDays(2), currentTime.minusDays(1)),
                new BookingInnerDto(2L, 1L, currentTime.plusDays(2), currentTime.plusDays(3)),
                null
        );

        BookingOutputDto bookingOutputDto = new BookingOutputDto(
                1L,
                itemDto,
                userDto,
                currentTime.minusDays(1),
                currentTime.plusDays(1),
                BookingStatus.APPROVED
        );

        //when
        JsonContent<BookingOutputDto> content = jacksonTester.write(bookingOutputDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(content).extractingJsonPathStringValue("$.item.description").isEqualTo("new item");
        assertThat(content).extractingJsonPathStringValue("$.item.lastBooking.start")
                .isEqualTo(currentTime.minusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.item.lastBooking.end")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.item.nextBooking.start")
                .isEqualTo(currentTime.plusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.item.nextBooking.end")
                .isEqualTo(currentTime.plusDays(3).withNano(0).toString());
        assertThat(content).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.booker.name").isEqualTo("new user");
        assertThat(content).extractingJsonPathStringValue("$.booker.email").isEqualTo("user@email.ru");
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo(currentTime.plusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.APPROVED.toString());
    }
}