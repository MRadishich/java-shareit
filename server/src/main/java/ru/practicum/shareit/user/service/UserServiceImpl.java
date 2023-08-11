package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.AlreadyExistsException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User savedUser = userRepository.save(UserMapper.toUser(userDto));

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFountException("User with id = " + userId + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUserById(Long userId, UserDto userDto) {
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

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
