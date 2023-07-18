package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private BookingInputDto bookingInputDto;
    private BookingOutputDto bookingOutputDto;

    private static final LocalDateTime START_DATE = LocalDateTime.now().plusMinutes(1).withNano(0);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(2);

    @BeforeEach
    public void before() {

        Item item = Item.builder()
                .id(1L)
                .description("new item")
                .ownerId(1L)
                .available(true)
                .build();

        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null,
                null
        );

        user = User.builder()
                .id(1L)
                .name("New user")
                .email("newUser@email.ru")
                .build();

        UserDto userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        bookingInputDto = new BookingInputDto(
                1L,
                START_DATE,
                END_DATE
        );

        bookingOutputDto = new BookingOutputDto(
                1L,
                itemDto,
                userDto,
                START_DATE,
                END_DATE,
                BookingStatus.WAITING
        );

    }

    @SneakyThrows
    @Test
    public void createBooking_whenBookingSuccessfullyCreated_thenReturnHttpStatusCode201() {
        //when
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        //then
        bookingService.createBooking(bookingInputDto, user.getId());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenBookingSuccessfullyCreated_thenReturnBookingOutputDto() {
        //when
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingOutputDto.getId()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id").value(bookingOutputDto.getItem().getId()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id").value(bookingOutputDto.getBooker().getId()))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.start").value(bookingOutputDto.getStart().toString()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.end").value(bookingOutputDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingOutputDto.getStatus().toString()));

        //then
        bookingService.createBooking(bookingInputDto, user.getId());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenHeaderWithUserIdContainsNotNumber_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "One")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenBodyIsEmpty_thenReturnBadRequest() {
        //when
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenItemIdIsNull_thenReturnBadRequest() {
        //given
        bookingInputDto.setItemId(null);

        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenStartDateIsNull_thenReturnBadRequest() {
        //given
        bookingInputDto.setStart(null);

        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenEndDateIsNull_thenReturnBadRequest() {
        //given
        bookingInputDto.setEnd(null);

        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenStartDateIsPast_thenReturnBadRequest() {
        //given
        bookingInputDto.setStart(LocalDateTime.now().minusDays(1));

        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //when
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenEndDateIsPast_thenReturnBadRequest() {
        //given
        bookingInputDto.setEnd(LocalDateTime.now().minusDays(1));

        //when
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenServiceThrowIllegalArgumentException_thenReturnBadRequest() {

        //when
        when(bookingService.createBooking(any(), any())).thenThrow(new IllegalArgumentException("exception."));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, times(1)).createBooking(any(), any());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenServiceThrowBadRequestException_thenReturnBadRequest() {

        //when
        when(bookingService.createBooking(any(), any())).thenThrow(new BadRequestException("exception."));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, times(1)).createBooking(any(), any());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenServiceThrowNotFoundException_thenReturnNotFound() {

        //when
        when(bookingService.createBooking(any(), any())).thenThrow(new NotFountException("exception."));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //then
        verify(bookingService, times(1)).createBooking(any(), any());
    }

    @SneakyThrows
    @Test
    public void getBooking_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getBooking_whenHeaderWithUserIdContainsNotNumber_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "One")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getBooking_whenRequestIsValid_thenReturnHttpStatusCode200() {
        //given
        Long bookingId = bookingOutputDto.getId();

        //when
        when(bookingService.getBooking(any(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        bookingService.getBooking(bookingId, user.getId());
    }

    @SneakyThrows
    @Test
    public void getBooking_whenBookingSuccessfullyCreated_thenReturnBookingOutputDto() {
        //given
        Long bookingId = bookingOutputDto.getId();

        //when
        when(bookingService.getBooking(any(), anyLong())).thenReturn(bookingOutputDto);

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingOutputDto.getId()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id").value(bookingOutputDto.getItem().getId()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id").value(bookingOutputDto.getBooker().getId()))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.start").value(bookingOutputDto.getStart().toString()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.end").value(bookingOutputDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingOutputDto.getStatus().toString()));

        //then
        bookingService.createBooking(bookingInputDto, user.getId());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenHeaderWithUserIdContainsNotNumber_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "One")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(bookingService).getBookingsByBookerIdAndState(
                userIdCaptor.capture(),
                stateCaptor.capture(),
                pageableCaptor.capture()
        );

        assertEquals(State.ALL, stateCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "start"), pageableCaptor.getValue().getSort());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenParamFromLessZero_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings?from=-1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenParamSizeLessZero_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings?size=-1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenParamSizeEqualsZero_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings?size=0")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenParamDirIsNotValid_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings?dir=ascdesc")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByBooker_whenStateIsNotValid_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings?dir=desc&state=new")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenMissingHeaderWithUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByOwnerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenHeaderWithUserIdContainsNotNumber_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "One")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsByOwnerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenParameterIsNotProvided_thenUseDefaultValues() {
        //given
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //when
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(bookingService).getBookingsByOwnerIdAndState(
                userIdCaptor.capture(),
                stateCaptor.capture(),
                pageableCaptor.capture()
        );

        assertEquals(State.ALL, stateCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "start"), pageableCaptor.getValue().getSort());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenParamFromLessZero_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/owner?from=-1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenParamSizeLessZero_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/owner?size=-1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenParamSizeEqualsZero_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/owner?size=0")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getBookingsByOwner_whenParamDirIsNotValid_thenReturnBadRequest() {
        //when
        mockMvc.perform(get("/bookings/owner?dir=ascdesc")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).getBookingsByBookerIdAndState(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void approvedBooking_whenHeaderWithUserIdContainsNotNumber_thenReturnBadRequest() {
        //when
        mockMvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).changeStatus(any(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    public void approvedBooking_whenPathVariableIsNotNumber_thenReturnBadRequest() {
        //when
        mockMvc.perform(patch("/bookings/one")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).changeStatus(any(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    public void approvedBooking_whenRequestParamIsNotBoolean_thenReturnBadRequest() {
        //when
        mockMvc.perform(patch("/bookings/1?approved=19")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //then
        verify(bookingService, never()).changeStatus(any(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    public void approvedBooking_whenRequestIsValid_thenInvokeBookingService() {
        //when
        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(bookingService, times(1)).changeStatus(1L, 1L, false);
    }
}