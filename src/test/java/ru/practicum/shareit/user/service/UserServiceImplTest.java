package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void createUser_whenSaved_thenReturnUserDtoWithId() {
        //given
        UserDto userDto = new UserDto(
                null,
                "new user",
                "email@email.ru"
        );

        UserDto expectedReturnedUserDto = new UserDto(
                1L,
                "new user",
                "email@email.ru"
        );

        User user = new User(
                1L,
                "new user",
                "email@email.ru"
        );

        //when
        when(userRepository.save(any())).thenReturn(user);
        UserDto returnedUserDto = userService.createUser(userDto);

        //then
        assertEquals(expectedReturnedUserDto, returnedUserDto);
    }

    @Test
    public void getUserById_whenUserIsNotExists_thenThrowNotFoundException() {
        //given
        UserDto expectedReturnedUserDto = new UserDto(
                1L,
                "new user",
                "email@email.ru"
        );

        User user = new User(
                1L,
                "new user",
                "email@email.ru"
        );

        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto returnedUserDto = userService.getUserById(userId);

        //then
        assertEquals(expectedReturnedUserDto, returnedUserDto);
    }

    @Test
    public void getUserById_whenUserIsExists_thenReturnUserDto() {
        //given
        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFountException.class, () -> userService.getUserById(userId));
        String expectedMessage = "User with id = " + userId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getAllUsers_whenUsersNotFound_thenReturnEmptyUserDtosList() {
        //when
        when(userRepository.findAll()).thenReturn(List.of());
        List<UserDto> userDtos = userService.getAllUsers();

        //then
        assertTrue(userDtos.isEmpty());
    }

    @Test
    public void getAllUsers_whenUsersAreFound_thenReturnUserDtosList() {
        //given
        List<User> users = List.of(
                new User(
                        1L,
                        "new user 1",
                        "user1@eamil.ru"
                ),
                new User(
                        2L,
                        "new user 2",
                        "user2@eamil.ru"
                ),
                new User(
                        3L,
                        "new user 3",
                        "user3@eamil.ru"
                )
        );

        //when
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> userDtos = userService.getAllUsers();

        //then
        assertEquals(users.size(), userDtos.size());
        assertEquals(users.get(0).getId(), userDtos.get(0).getId());
        assertEquals(users.get(0).getName(), userDtos.get(0).getName());
        assertEquals(users.get(0).getEmail(), userDtos.get(0).getEmail());
        assertEquals(users.get(1).getId(), userDtos.get(1).getId());
        assertEquals(users.get(1).getName(), userDtos.get(1).getName());
        assertEquals(users.get(1).getEmail(), userDtos.get(1).getEmail());
        assertEquals(users.get(2).getId(), userDtos.get(2).getId());
        assertEquals(users.get(2).getName(), userDtos.get(2).getName());
        assertEquals(users.get(2).getEmail(), userDtos.get(2).getEmail());
    }

    @Test
    public void updateUserById_whenUserIsNotExists_thenThrowNotFoundException() {
        //given
        UserDto userDto = new UserDto(
                1L,
                null,
                "email@email.ru"
        );

        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFountException.class, () -> userService.updateUserById(userId, userDto));
        String expectedMessage = "User with id = " + userId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateUserById_whenUpdated_thenReturnUserDtoWithNewValue() {
        //given
        UserDto userDto = new UserDto(
                1L,
                null,
                "newEmail@email.ru"
        );

        User oldUser = new User(
                1L,
                "old user",
                "oldEmail@email.ru"
        );

        User updatedUser = new User(
                1L,
                "old user",
                "newEmail@email.ru"
        );

        Long userId = 1L;

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any())).thenReturn(updatedUser);
        UserDto returnUserDto = userService.updateUserById(userId, userDto);

        //then
        verify(userRepository).save(userCaptor.capture());
        assertEquals(1L, userCaptor.getValue().getId());
        assertEquals("old user", userCaptor.getValue().getName());
        assertEquals("newEmail@email.ru", userCaptor.getValue().getEmail());
        assertEquals(1L, returnUserDto.getId());
        assertEquals("old user", returnUserDto.getName());
        assertEquals("newEmail@email.ru", returnUserDto.getEmail());
    }

    @Test
    public void deleteUserById_whenInvoked_thenInvokeUserRepository() {
        //given
        Long userId = 1L;

        //when
        userService.deleteUserById(1L);

        //then
        verify(userRepository, times(1)).deleteById(userId);
    }
}