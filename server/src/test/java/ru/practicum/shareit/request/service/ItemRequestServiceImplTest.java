package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestInputDto itemRequestInputDto;

    private Long requesterId;

    @BeforeEach
    public void before() {
        itemRequestInputDto = new ItemRequestInputDto(
                "I need item"
        );

        requesterId = 1L;
    }

    @Test
    public void createRequest_whenUserNotExists_thenThrowNotFoundException() {
        //when
        when(userRepository.existsById(requesterId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> itemRequestService.createRequest(itemRequestInputDto, requesterId));
        String expectedMessage = "User with id = " + requesterId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createRequest_whenInvoked_thenSaveInRepository() {
        //given
        ItemRequest itemRequest = new ItemRequest(
                1L,
                requesterId,
                "text",
                LocalDateTime.now(),
                List.of()
        );

        //when
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        itemRequestService.createRequest(itemRequestInputDto, requesterId);

        //then
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getRequestsByRequesterId_whenUserIsNotExists_thenThrowNotFoundException() {
        //when
        when(userRepository.existsById(requesterId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> itemRequestService.getRequestsByRequesterId(requesterId, "id", Sort.Direction.DESC));
        String expectedMessage = "User with id = " + requesterId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getRequestsByRequesterId_whenInvoked_thenInvokeItemRequestRepository() {
        //given
        List<ItemRequest> itemRequests = List.of(
                new ItemRequest(
                        1L,
                        requesterId,
                        "text",
                        LocalDateTime.now(),
                        List.of()
                ),
                new ItemRequest(
                        2L,
                        requesterId,
                        "text",
                        LocalDateTime.now(),
                        List.of()
                )
        );

        //when
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "id"))).thenReturn(itemRequests);
        itemRequestService.getRequestsByRequesterId(requesterId, "id", Sort.Direction.DESC);

        //then
        verify(itemRequestRepository, times(1)).findByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Test
    public void getOtherRequests_whenUserIsNotExists_thenThrowNotFoundException() {
        //when
        when(userRepository.existsById(requesterId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> itemRequestService.getOtherRequests(requesterId, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"))));
        String expectedMessage = "User with id = " + requesterId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getOtherRequests_whenInvoked_thenInvokeItemRequestRepository() {
        //given
        List<ItemRequest> itemRequests = List.of(
                new ItemRequest(
                        1L,
                        requesterId,
                        "text",
                        LocalDateTime.now(),
                        List.of()
                ),
                new ItemRequest(
                        2L,
                        requesterId,
                        "text",
                        LocalDateTime.now(),
                        List.of()
                )
        );

        //when
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNot(requesterId, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")))).thenReturn(itemRequests);
        itemRequestService.getOtherRequests(requesterId, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));

        //then
        verify(itemRequestRepository, times(1)).findByRequesterIdNot(requesterId, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));
    }

    @Test
    void getRequestById_whenUserIsNotExists_thenThrowNotFoundException() {
        //given
        Long userId = 1L;

        //when
        when(userRepository.existsById(requesterId)).thenReturn(false);

        Exception exception = assertThrows(NotFountException.class,
                () -> itemRequestService.getRequestById(requesterId, userId));
        String expectedMessage = "User with id = " + requesterId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getRequestById_whenRequestIsNotExists_thenThrowNotFoundException() {
        //given
        Long requestId = 1L;

        //when
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFountException.class,
                () -> itemRequestService.getRequestById(requestId, requesterId));
        String expectedMessage = "Request with id = " + requestId + " not found.";

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getRequestById_whenInvoked_thenInvokeItemRequestRepository() {
        //given
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest(
                2L,
                requesterId,
                "text",
                LocalDateTime.now(),
                List.of()
        );

        //when
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        itemRequestService.getRequestById(requestId, requesterId);

        //then
        verify(itemRequestRepository, times(1)).findById(requestId);
    }
}