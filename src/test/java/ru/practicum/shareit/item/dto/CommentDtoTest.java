package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testCommentDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        CommentDto commentDto = new CommentDto(
                1L,
                "I need item",
                "user",
                currentTime
        );

        //when
        JsonContent<CommentDto> content = jacksonTester.write(commentDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("I need item");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("user");
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(currentTime.withNano(0).toString());
    }

}