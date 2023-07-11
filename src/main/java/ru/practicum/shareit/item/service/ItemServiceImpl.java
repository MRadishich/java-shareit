package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ForbiddenException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);

        item = itemRepository.save(item);

        return ItemMapper.toItemDtoForOwner(item);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));

        if (item.getOwnerId().equals(userId)) {
            return ItemMapper.toItemDtoForOwner(item);
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long userId) {
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDtoForOwner)
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> getItemsByKeyword(String keyword) {
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return List.of();
        }

        return itemRepository.findByNameAndDescription(keyword, keyword)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItemById(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + itemId + " not found."));

        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        if (item.getOwnerId().longValue() != userId.longValue()) {
            throw new ForbiddenException("Access denied. User with id = " + userId +
                    " does not have permission to change this item.");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        itemRepository.save(item);

        return ItemMapper.toItemDtoForOwner(item);
    }

    @Override
    @Transactional
    public void deleteItemById(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFountException("Item with id = " + itemId + " not found.");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId) {
        LocalDateTime currentTime = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setCreated(currentTime);

        User author = userRepository.findById(authorId).orElseThrow(
                () -> new NotFountException("User with id = " + authorId + " not found."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFountException("Item with id = " + authorId + " not found."));

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(authorId, itemId)
                .stream().filter(booking -> booking.getStatus() == BookingStatus.APPROVED
                        && booking.getStart().isBefore(currentTime))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new BadRequestException("User with id = " + authorId + " did not rent this item.");
        }

        comment.setAuthor(author);
        comment.setItem(item);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
