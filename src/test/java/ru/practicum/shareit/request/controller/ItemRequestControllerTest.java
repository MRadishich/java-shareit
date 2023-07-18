package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    public ItemRequestService requestService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    MockMvc mockMvc;

    private ItemRequestInputDto inputDto;
    private Long userId;

    @BeforeEach
    public void before() {
        inputDto = new ItemRequestInputDto(
                "I need a item"
        );

        userId = 1L;
    }

    @Test
    @SneakyThrows
    void createRequest_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).createRequest(any(), any());
    }

    @Test
    @SneakyThrows
    void createRequest_whenBodyIsEmpty_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).createRequest(any(), any());
    }

    @Test
    @SneakyThrows
    void createRequest_whenDescriptionIsNull_thenReturnBadRequest() {
        //given
        ItemRequestInputDto invalidDto = new ItemRequestInputDto(null);

        //when
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).createRequest(any(), any());
    }

    @Test
    @SneakyThrows
    void createRequest_whenDescriptionIsBlank_thenReturnBadRequest() {
        //given
        ItemRequestInputDto invalidDto = new ItemRequestInputDto("  ");

        //when
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).createRequest(any(), any());
    }

    @Test
    @SneakyThrows
    void createRequest_whenRequestIsValid_thenInvokeService() {
        //when
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated());

        //then
        verify(requestService, times(1)).createRequest(inputDto, userId);
    }

    @Test
    @SneakyThrows
    void createRequest_whenServiceThrowNotFoundException_thenReturnNotFound() {
        //when
        when(requestService.createRequest(inputDto, userId)).thenThrow(new NotFountException("User not found."));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());

        //then
        verify(requestService, times(1)).createRequest(inputDto, userId);
    }

    @Test
    @SneakyThrows
    void getRequests_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).getRequestsByRequesterId(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void getRequests_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> sortParamCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Sort.Direction> dirCaptor = ArgumentCaptor.forClass(Sort.Direction.class);
        //when
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        //then
        verify(requestService).getRequestsByRequesterId(userIdCaptor.capture(), sortParamCaptor.capture(), dirCaptor.capture());
        assertEquals(1L, userIdCaptor.getValue());
        assertEquals("created", sortParamCaptor.getValue());
        assertEquals(Sort.Direction.DESC, dirCaptor.getValue());
    }

    @Test
    @SneakyThrows
    void getRequests_whenParametersIsProvided_thenInvokeServiceWithGivenParameters() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> sortParamCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Sort.Direction> dirCaptor = ArgumentCaptor.forClass(Sort.Direction.class);
        //when
        mockMvc.perform(get("/requests?sort=id&dir=asc")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        //then
        verify(requestService).getRequestsByRequesterId(userIdCaptor.capture(), sortParamCaptor.capture(), dirCaptor.capture());
        assertEquals(1L, userIdCaptor.getValue());
        assertEquals("id", sortParamCaptor.getValue());
        assertEquals(Sort.Direction.ASC, dirCaptor.getValue());
    }

    @Test
    @SneakyThrows
    void getOtherRequests_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).getOtherRequests(any(), any());
    }

    @Test
    @SneakyThrows
    void getOtherRequests_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        //then
        verify(requestService).getOtherRequests(userIdCaptor.capture(), pageableCaptor.capture());
        assertEquals(1L, userIdCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "created"), pageableCaptor.getValue().getSort());
    }

    @Test
    @SneakyThrows
    void getOtherRequests_whenParametersIsProvided_thenInvokeServiceWithGivenParameters() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        mockMvc.perform(get("/requests/all?from=13&size=10&sort=id&dir=asc")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        //then
        verify(requestService).getOtherRequests(userIdCaptor.capture(), pageableCaptor.capture());
        assertEquals(1L, userIdCaptor.getValue());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageableCaptor.getValue().getSort());
    }

    @Test
    @SneakyThrows
    void getRequest_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        //then
        verify(requestService, never()).getRequestById(any(), any());
    }

    @Test
    @SneakyThrows
    void getRequest_whenRequestIsValid_thenInvokeService() {
        //given
        Long requestId = 1L;

        //when
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        //then
        verify(requestService, times(1)).getRequestById(requestId, userId);
    }
}