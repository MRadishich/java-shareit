package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findAll();

    void deleteById(Long itemId);

    boolean existsById(Long itemId);

    List<Item> getItemsByKeyword(String text);
}
