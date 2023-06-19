package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void deleteById(Long userId);

    boolean existsById(Long userId);

    boolean existsByEmail(String email);
}
