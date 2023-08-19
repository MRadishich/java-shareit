package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInnerDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testItemDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now();

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

        //when
        JsonContent<ItemDto> content = jacksonTester.write(itemDto);

        //when
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("new item");
        assertThat(content).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(currentTime.minusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(currentTime.plusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(currentTime.plusDays(3).withNano(0).toString());
    }

}