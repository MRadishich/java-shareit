package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, Item> ITEMS = new HashMap<>();
    private static final AtomicLong ITEM_ID = new AtomicLong(1);

    @Override
    public Item save(Item item) {
        Long itemId = ITEM_ID.getAndIncrement();

        item.setId(itemId);
        ITEMS.put(itemId, item);

        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(ITEMS.get(itemId));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(ITEMS.values());
    }

    @Override
    public void deleteById(Long itemId) {
        ITEMS.remove(itemId);
    }

    @Override
    public boolean existsById(Long itemId) {
        return ITEMS.containsKey(itemId);
    }

    @Override
    public List<Item> getItemsByKeyword(String text) {
        return ITEMS.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
