package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        return service.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return service.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUserById(@PathVariable long userId,
                                  @RequestBody UserDto userDto) {
        return service.updateUserById(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        service.deleteUserById(userId);
    }
}
