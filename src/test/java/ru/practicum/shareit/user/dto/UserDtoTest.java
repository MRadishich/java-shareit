package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    JacksonTester<UserDto> jacksonTester;

    @Test
    @SneakyThrows
    public void testUserDto() {
        //given
        UserDto userDto = new UserDto(
                1L,
                "user",
                "user@email.ru"
        );

        //when
        JsonContent<UserDto> content = jacksonTester.write(userDto);

        //then
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("user@email.ru");
    }
}