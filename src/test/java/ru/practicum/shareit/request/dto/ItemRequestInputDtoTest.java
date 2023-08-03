package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemRequestInputDtoTest {

    @Autowired
    JacksonTester<ItemRequestInputDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testItemRequestInputDtoTest() {
        //given
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto(
                "I need item"
        );

        //when
        JsonContent<ItemRequestInputDto> content = jacksonTester.write(itemRequestInputDto);

        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo("I need item");
    }
}