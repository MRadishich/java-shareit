package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestOutputDto createRequest(ItemRequestInputDto requestDto, Long requesterId);

    List<ItemRequestOutputDto> getRequestsByRequesterId(Long requesterId, String param, Sort.Direction sort);

    List<ItemRequestOutputDto> getOtherRequests(Long userId, Pageable pageable);

    ItemRequestOutputDto getRequestById(Long requestId, Long userId);
}
