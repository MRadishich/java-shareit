package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ForbiddenException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockBean
    public ItemService itemService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    private ItemDto itemDto;
    private Long userId;

    @BeforeEach
    public void before() {
        itemDto = new ItemDto(
                1L,
                "New item",
                "New item",
                true,
                null,
                null,
                null,
                null
        );

        userId = 1L;
    }


    @Test
    @SneakyThrows
    public void createItem_whenItemDtoIsValid_thenInvokeItemServiceAndReturnHttpStatusCode201() {
        //when
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //then
        verify(itemService, times(1)).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    public void createItem_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    public void createItem_whenBodyIsEmpty_thenReturnBadRequest() {
        //when
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    public void createItem_whenUserIdIsNotNumber_thenReturnBadRequest() {
        //when
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "one")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    public void createItem_whenServiceThrowNotFoundException_thenReturnNotFound() {
        //when
        when(itemService.createItem(any(), anyLong())).thenThrow(new NotFountException("User not found."));

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //then
        verify(itemService, times(1)).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    public void createComment_whenCommentIsValid_thenInvokeItemService() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now().withNano(0)
        );

        //when
        when(itemService.createComment(commentDto, itemDto.getId(), userId)).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                .content(objectMapper.writeValueAsString(commentDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        verify(itemService, times(1)).createComment(commentDto, itemDto.getId(), userId);
    }

    @Test
    @SneakyThrows
    public void createComment_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(itemService.createComment(commentDto, itemDto.getId(), userId)).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createComment(commentDto, itemDto.getId(), userId);
    }

    @Test
    @SneakyThrows
    public void createComment_whenBodyIsEmpty_thenReturnBadRequest() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(itemService.createComment(commentDto, itemDto.getId(), userId)).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createComment(commentDto, itemDto.getId(), userId);
    }

    @Test
    @SneakyThrows
    public void createComment_whenUserIdIsNotNumber_thenReturnBadRequest() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(itemService.createComment(commentDto, itemDto.getId(), userId)).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "one")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).createComment(commentDto, itemDto.getId(), userId);
    }

    @Test
    @SneakyThrows
    public void createComment_whenServiceThrowBadRequestException_thenReturnBadRequest() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "text",
                "new user",
                LocalDateTime.now().withNano(0)
        );

        //when
        when(itemService.createComment(commentDto, itemDto.getId(), userId)).thenThrow(new BadRequestException("Bad request"));

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, times(1)).createComment(commentDto, itemDto.getId(), userId);
    }

    @Test
    @SneakyThrows
    void getItemsByUserId_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        when(itemService.getItemsByOwnerId(eq(userId), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).getItemsByOwnerId(eq(userId), any());
    }

    @Test
    @SneakyThrows
    void getItemsByUserId_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        when(itemService.getItemsByOwnerId(eq(userId), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(itemService).getItemsByOwnerId(userIdCaptor.capture(), pageableCaptor.capture());

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageableCaptor.getValue().getSort());
    }

    @Test
    @SneakyThrows
    void getItemsByUserId_whenParamSizeLessZero_thenReturnBadRequest() {
        //when
        when(itemService.getItemsByOwnerId(eq(userId), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items?size=-10")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).getItemsByOwnerId(eq(userId), any());
    }

    @Test
    @SneakyThrows
    void getItemById_whenMissingHeaderWithUserId_whenReturnBadRequest() {
        when(itemService.getItemById(any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).getItemById(any(), any());
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvoked_whenInvokeItemServiceWithGivenUserIdAndItemId() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> itemIdCaptor = ArgumentCaptor.forClass(Long.class);
        long itemId = 10L;

        //when
        when(itemService.getItemById(any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(itemService, times(1)).getItemById(userIdCaptor.capture(), itemIdCaptor.capture());
        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(itemId, itemIdCaptor.getValue());
    }

    @Test
    @SneakyThrows
    void getItemsByKeyword_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        when(itemService.getItemsByText(any(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(itemService).getItemsByText(textCaptor.capture(), pageableCaptor.capture());

        assertNull(textCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageableCaptor.getValue().getSort());
    }

    @Test
    @SneakyThrows
    void getItemsByKeyword_whenInvokedWithTextParam_thenInvokeItemServiceWithGivenText() {
        //given
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        when(itemService.getItemsByText(any(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=акк")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(itemService).getItemsByText(textCaptor.capture(), pageableCaptor.capture());

        assertEquals("акк", textCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageableCaptor.getValue().getSort());
    }

    @Test
    @SneakyThrows
    void getItemsByKeyword_whenParamSizeLessZero_thenReturnBadRequest() {
        //when
        when(itemService.getItemsByText(any(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?size=-10")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(itemService, never()).getItemsByText(any(), any());
    }

    @Test
    @SneakyThrows
    void updateItemById_whenServiceThrowNotFoundException_thenReturnNotFound() {
        doThrow(new NotFountException("item not found.")).when(itemService).updateItemById(any(), any(), any());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //then
        verify(itemService, times(1)).updateItemById(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void updateItemById_whenServiceThrowForbiddenException_thenReturnForbidden() {
        doThrow(new ForbiddenException("forbidden exception.")).when(itemService).updateItemById(any(), any(), any());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //then
        verify(itemService, times(1)).updateItemById(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void deleteItemById_whenServiceThrowNotFoundException_thenReturnNonFound() {
        //when
        doThrow(new NotFountException("item not found.")).when(itemService).deleteItemById(any(), any());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //then
        verify(itemService, times(1)).deleteItemById(any(), any());
    }

    @Test
    @SneakyThrows
    void deleteItemById_whenServiceThrowForbiddenException_thenReturnForbidden() {
        //when
        doThrow(new ForbiddenException("forbidden exception.")).when(itemService).deleteItemById(any(), any());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //then
        verify(itemService, times(1)).deleteItemById(any(), any());
    }
}