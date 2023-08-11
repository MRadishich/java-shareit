package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private final Long id;

    @NotBlank(groups = Create.class)
    private final String name;

    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private final String email;
}
