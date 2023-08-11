package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestOutputDto createRequest(ItemRequestInputDto requestDto, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFountException("User with id = " + requesterId + " not found.");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, requesterId);
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutputDto> getRequestsByRequesterId(Long requesterId, String param, Sort.Direction sort) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFountException("User with id = " + requesterId + " not found.");
        }

        return itemRequestRepository.findByRequesterId(requesterId, Sort.by(sort, param))
                .stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutputDto> getOtherRequests(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        return itemRequestRepository.findByRequesterIdNot(userId, pageable)
                .stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestOutputDto getRequestById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFountException("User with id = " + userId + " not found.");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFountException("Request with id = " + requestId + " not found."));

        return ItemRequestMapper.toDto(itemRequest);
    }
}
