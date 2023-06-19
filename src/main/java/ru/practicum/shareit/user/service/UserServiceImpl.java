package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AlreadyExistsException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Received a request to create a new user: {}.", userDto);

        if (!userRepository.existsByEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("User with email = " + userDto.getEmail() + " already exists.");
        }

        User savedUser = userRepository.save(UserDtoMapper.fromDto(userDto));

        return UserDtoMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Received a request to search for a user with id = {}.", userId);

        return userRepository.findById(userId)
                .map(UserDtoMapper::toDto)
                .orElseThrow(() -> new NotFountException("User with id = " + userId + " not found."));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Received a request to search all users.");

        return userRepository.findAll()
                .stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUserById(Long userId, UserDto userDto) {
        log.info("Received a request to update a user with id = " + userId +
                ". New value: " + userDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFountException("User with id = " + userId + " not found."));

        if (userDto.getEmail() != null && !user.getEmail().isEmpty()) {
            Optional<User> userByEmail = userRepository.findByEmail(userDto.getEmail());

            if (userByEmail.isPresent() && !userByEmail.get().equals(user)) {
                throw new AlreadyExistsException("User with email = " + userDto.getEmail() + " already exists.");
            }

            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) user.setName(userDto.getName());

        return UserDtoMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Received a request to delete a user with id = " + userId);

        userRepository.deleteById(userId);
    }
}
