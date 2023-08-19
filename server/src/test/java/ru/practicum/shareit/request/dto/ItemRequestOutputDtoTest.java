package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInnerDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutputDtoTest {

    @Autowired
    JacksonTester<ItemRequestOutputDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testItemRequestOutputDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now();
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto(
                1L,
                "I need item",
                currentTime,
                List.of(new ItemDto(
                        1L,
                        "item",
                        "new item",
                        true,
                        3L,
                        new BookingInnerDto(1L, 1L, currentTime.minusDays(2), currentTime.minusDays(1)),
                        new BookingInnerDto(2L, 1L, currentTime.plusDays(2), currentTime.plusDays(3)),
                        null
                ))
        );

        //when
        JsonContent<ItemRequestOutputDto> content = jacksonTester.write(itemRequestOutputDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("I need item");
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo(currentTime.withNano(0).toString());
        assertThat(content).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
        assertThat(content).extractingJsonPathStringValue("$.items[0].description").isEqualTo("new item");
        assertThat(content).extractingJsonPathStringValue("$.items[0].lastBooking.start")
                .isEqualTo(currentTime.minusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.items[0].lastBooking.end")
                .isEqualTo(currentTime.minusDays(1).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.items[0].nextBooking.start")
                .isEqualTo(currentTime.plusDays(2).withNano(0).toString());
        assertThat(content).extractingJsonPathStringValue("$.items[0].nextBooking.end")
                .isEqualTo(currentTime.plusDays(3).withNano(0).toString());
    }


}