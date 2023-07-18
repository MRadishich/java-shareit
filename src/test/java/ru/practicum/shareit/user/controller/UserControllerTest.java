package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.AlreadyExistsException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void createUser_whenBodyIsEmpty_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenNameIsNull_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                null,
                "email@eamil.ru"
        );

        //when
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenNameIsBlank_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                "   ",
                "email@eamil.ru"
        );

        //when
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenEmailIsNull_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                null
        );

        //when
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenEmailIsNotValid_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                "email .ru"
        );

        //when
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenThrowDataIntegrityViolationException_thenReturnConflict() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                "email@email.ru"
        );

        //when
        when(userService.createUser(userDto)).thenThrow(new DataIntegrityViolationException("email not unique."));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //then
        verify(userService, times(1)).createUser(any());
    }

    @Test
    @SneakyThrows
    public void createUser_whenCreatedUser_thenReturnOk() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                "email@email.ru"
        );

        //when
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //then
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    @SneakyThrows
    public void getAllUsers_whenInvoked_thenReturnOk() {
        //when
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    public void getUserById_whenPathVariableIsNotNumber_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/users/one")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    public void getUserById_whenServiceThrowNotFoundException_thenReturnNotFound() {
        //when
        when(userService.getUserById(anyLong())).thenThrow(new NotFountException("user not found."));

        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //then
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @SneakyThrows
    public void getUserById_whenRequestIsValid_thenInvokeUserService() {
        //when
        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @SneakyThrows
    public void updateUserById_whenPathVariableIsNotNumber_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                "email@email.ru"
        );

        //when
        mockMvc.perform(patch("/users/one")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).updateUserById(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void updateUserById_whenBodyIsEmpty_thenReturnBadRequest() {
        //when
        mockMvc.perform(patch("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).updateUserById(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void updateUserById_whenEmailIsNotValid_thenReturnBadRequest() {
        //given
        UserDto userDto = new UserDto(
                null,
                "New User",
                "email .ru"
        );

        //when
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(userService, never()).updateUserById(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void updateUserById_whenNameIsNullAndEmailIsValid_thenInvokeUserService() {
        //given
        UserDto userDto = new UserDto(
                null,
                null,
                "email@email.ru"
        );

        Long userId = 1L;

        //when
        mockMvc.perform(patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(userService, times(1)).updateUserById(userId, userDto);
    }

    @Test
    @SneakyThrows
    public void updateUserById_whenServiceThrowAlreadyExistsException_thenConflict() {
        //given
        UserDto userDto = new UserDto(
                null,
                null,
                "email@email.ru"
        );

        Long userId = 1L;

        //when
        when(userService.updateUserById(userId, userDto)).thenThrow(new AlreadyExistsException("User with this email exists."));

        mockMvc.perform(patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //then
        verify(userService, times(1)).updateUserById(userId, userDto);
    }

    @Test
    @SneakyThrows
    void deleteUserById_whenInvoked_thenInvokeUserRepository() {
        //given
        Long userId = 1L;

        //when
        mockMvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(userService, times(1)).deleteUserById(userId);
    }
}