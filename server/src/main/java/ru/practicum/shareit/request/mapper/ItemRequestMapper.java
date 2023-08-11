package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestOutputDto toDto(ItemRequest itemRequest) {
        return new ItemRequestOutputDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems() == null ?
                        null : itemRequest.getItems().stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
        );
    }

    public ItemRequest toItemRequest(ItemRequestInputDto itemRequestInputDto, Long requesterId) {
        return ItemRequest.builder()
                .requesterId(requesterId)
                .description(itemRequestInputDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }
}
