package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUser_whenInvoked_thenReturnUser() {
        //given
        UserDto userDto = new UserDto(
                1L,
                "new user",
                "user@email.ru"
        );

        User expectedUser = new User(
                1L,
                "new user",
                "user@email.ru"
        );

        //when
        User returnedUser = UserMapper.toUser(userDto);

        //then
        assertEquals(expectedUser, returnedUser);
    }

    @Test
    void toUserDto_whenInvoked_thenReturnUserDto() {
        //given
        User user = new User(
                1L,
                "new user",
                "user@email.ru"
        );

        UserDto expectedUserDto = new UserDto(
                1L,
                "new user",
                "user@email.ru"
        );

        //when
        UserDto returnedUserDto = UserMapper.toUserDto(user);

        //then
        assertEquals(expectedUserDto, returnedUserDto);
    }
}