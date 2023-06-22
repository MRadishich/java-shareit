package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Map<Long, User> USERS = new HashMap<>();
    private static final AtomicLong USER_ID = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (Objects.isNull(user.getId())) {
            long userId = USER_ID.getAndIncrement();
            user.setId(userId);
            USERS.put(userId, user);
        } else {
            USERS.put(user.getId(), user);
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(USERS.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : USERS.values()) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(USERS.values());
    }

    @Override
    public void deleteById(Long userId) {
        USERS.remove(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return USERS.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return USERS.values()
                .stream()
                .map(User::getEmail)
                .noneMatch(e -> e.equals(email));
    }
}
