package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ForbiddenException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Received a request to create a new item: {}. OwnerId = {}.", itemDto, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        Item item = ItemDtoMapper.fromDto(itemDto);
        item.setOwnerId(userId);

        item = itemRepository.save(item);

        return ItemDtoMapper.toDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Received a request to search for a item with id = {}.", itemId);

        return itemRepository.findById(itemId)
                .map(ItemDtoMapper::toDto)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long userId) {
        log.info("Received a request to search for all items of the user with id = " + userId + " .");

        return itemRepository.findAll()
                .stream()
                .filter(item -> Objects.equals(userId, item.getOwnerId()))
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItemById(Long itemId, ItemDto itemDto, Long userId) {
        log.info("Received a request to update a item with id = {}. New value: {}", itemId, itemDto);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));

        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        if (item.getOwnerId().longValue() != userId.longValue()) {
            throw new ForbiddenException("User with id = " + userId +
                    " is not the owner of item with id = " + item.getId() + ". Refusal to update.");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return ItemDtoMapper.toDto(item);
    }

    @Override
    public void deleteItemById(Long itemId) {
        log.info("Received a request to delete a item with id = {}.", itemId);

        if (!itemRepository.existsById(itemId)) {
            throw new NotFountException("Item with id = " + itemId + " not found.");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getItemsByKeyword(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return List.of();
        }

        return itemRepository.getItemsByKeyword(text)
                .stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
