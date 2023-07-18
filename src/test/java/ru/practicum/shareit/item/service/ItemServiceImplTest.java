package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ForbiddenException;
import ru.practicum.shareit.exception.model.NotFountException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private Long userId;

    @BeforeEach
    public void createItemDto() {
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
    public void createItem_whenUserIsNotExists_thenTrowNotFoundException() {
        //when
        when(userRepository.existsById(userId)).thenReturn(false);
        Exception exception = assertThrows(NotFountException.class, () -> itemService.createItem(itemDto, userId));

        //then
        String expectedMessage = "User with id = " + userId + " not found.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createItem_whenSaveItem_thenSetOwnerId() {
        //given
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);

        //when
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any())).thenReturn(new Item());

        itemService.createItem(itemDto, userId);

        //then
        verify(itemRepository).save(itemCaptor.capture());
        assertEquals(userId, itemCaptor.getValue().getOwnerId());
    }

    @Test
    public void createItem_whenSaved_thenReturnItemDto() {
        //given
        Item newItem = new Item(
                1L,
                "New item",
                "New item",
                true,
                null,
                null,
                null,
                null
        );
        //when
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any())).thenReturn(newItem);

        ItemDto returnedItemDto = itemService.createItem(itemDto, userId);

        //then
        verify(itemRepository, times(1)).save(any());
        assertEquals(newItem.getId(), returnedItemDto.getId());
        assertEquals(newItem.getName(), returnedItemDto.getName());
        assertEquals(newItem.getDescription(), returnedItemDto.getDescription());
        assertEquals(newItem.getAvailable(), returnedItemDto.getAvailable());
    }

    @Test
    public void getItemById_whenItemNotExists_thenThrowNotFoundException() {
        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.empty());
        String expectedMessage = "Item with id = " + itemDto.getId() + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.getItemById(userId, itemDto.getId()));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getItemById_whenUserIsNotOwner_thenReturnItemDtoWithoutBookings() {
        //given
        Booking lastBooking = new Booking(
                1L,
                null,
                null,
                BookingStatus.APPROVED,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(4));

        Booking nextBooking = new Booking(
                2L,
                null,
                null,
                BookingStatus.APPROVED,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3));

        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                List.of(lastBooking, nextBooking),
                null
        );

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        itemDto = itemService.getItemById(userId, itemDto.getId());

        //then
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    public void getItemById_whenUserIsOwner_thenReturnItemDtoWithBookings() {
        //given

        User booker = new User(
                1L,
                "new User",
                "user@email.ru"
        );

        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        Booking lastBooking = new Booking(
                1L,
                item,
                booker,
                BookingStatus.APPROVED,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(4));

        Booking nextBooking = new Booking(1L,
                item,
                booker,
                BookingStatus.APPROVED,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3));

        item.setBookings(List.of(lastBooking, nextBooking));

        userId = 3L;

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        itemDto = itemService.getItemById(userId, itemDto.getId());

        //then
        assertEquals(lastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(lastBooking.getStart(), itemDto.getLastBooking().getStart());
        assertEquals(lastBooking.getEnd(), itemDto.getLastBooking().getEnd());
        assertEquals(nextBooking.getId(), itemDto.getNextBooking().getId());
        assertEquals(nextBooking.getStart(), itemDto.getNextBooking().getStart());
        assertEquals(nextBooking.getEnd(), itemDto.getNextBooking().getEnd());
    }

    @Test
    public void getItemsByOwnerId_whenInvoked_thenReturnListItemDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<Item> items = List.of(
                new Item(
                        1L,
                        "New item 1",
                        "New item 1",
                        true,
                        null,
                        null,
                        null,
                        null
                ),
                new Item(
                        2L,
                        "New item 2",
                        "New item 2",
                        true,
                        null,
                        null,
                        null,
                        null
                ),
                new Item(
                        3L,
                        "New item 3",
                        "New item 3",
                        true,
                        null,
                        null,
                        null,
                        null
                )
        );

        //when
        when(itemRepository.findByOwnerId(userId, pageable)).thenReturn(items);

        List<ItemDto> itemDtos = itemService.getItemsByOwnerId(userId, pageable);

        //then
        verify(itemRepository, times(1)).findByOwnerId(userId, pageable);
        assertEquals(items.size(), itemDtos.size());
        assertEquals(items.get(0).getId(), itemDtos.get(0).getId());
        assertEquals(items.get(0).getName(), itemDtos.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemDtos.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemDtos.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemDtos.get(1).getId());
        assertEquals(items.get(1).getName(), itemDtos.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemDtos.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemDtos.get(1).getAvailable());
        assertEquals(items.get(2).getId(), itemDtos.get(2).getId());
        assertEquals(items.get(2).getName(), itemDtos.get(2).getName());
        assertEquals(items.get(2).getDescription(), itemDtos.get(2).getDescription());
        assertEquals(items.get(2).getAvailable(), itemDtos.get(2).getAvailable());
    }

    @Test
    public void getItemsByOwnerId_whenKeywordIsNull_thenReturnEmptyList() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        //when
        List<ItemDto> itemDtos = itemService.getItemsByKeyword(null, pageable);

        //then
        assertTrue(itemDtos.isEmpty());
    }

    @Test
    public void getItemsByKeyword_whenKeywordIsBlank_thenReturnEmptyList() {
        //given
        String keyword = "   ";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        //when
        List<ItemDto> itemDtos = itemService.getItemsByKeyword(keyword, pageable);

        //then
        assertTrue(itemDtos.isEmpty());
    }

    @Test
    public void getItemsByKeyword_whenKeywordIsValid_thenReturnListItemDto() {
        //given
        String keyword = "new";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<Item> items = List.of(
                new Item(
                        1L,
                        "New item 1",
                        "New item 1",
                        true,
                        null,
                        null,
                        null,
                        null
                ),
                new Item(
                        2L,
                        "New item 2",
                        "New item 2",
                        true,
                        null,
                        null,
                        null,
                        null
                ),
                new Item(
                        3L,
                        "New item 3",
                        "New item 3",
                        true,
                        null,
                        null,
                        null,
                        null
                )
        );

        //when
        when(itemRepository.findByNameAndDescription(keyword, keyword, pageable)).thenReturn(items);
        List<ItemDto> itemDtos = itemService.getItemsByKeyword(keyword, pageable);

        //then
        verify(itemRepository, times(1)).findByNameAndDescription(keyword, keyword, pageable);
        assertEquals(items.size(), itemDtos.size());
        assertEquals(items.get(0).getId(), itemDtos.get(0).getId());
        assertEquals(items.get(0).getName(), itemDtos.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemDtos.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemDtos.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemDtos.get(1).getId());
        assertEquals(items.get(1).getName(), itemDtos.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemDtos.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemDtos.get(1).getAvailable());
        assertEquals(items.get(2).getId(), itemDtos.get(2).getId());
        assertEquals(items.get(2).getName(), itemDtos.get(2).getName());
        assertEquals(items.get(2).getDescription(), itemDtos.get(2).getDescription());
        assertEquals(items.get(2).getAvailable(), itemDtos.get(2).getAvailable());
    }

    @Test
    public void updateItemById_whenItemNotExists_thenThrowNotFoundException() {
        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.empty());
        String expectedMessage = "Item with id = " + itemDto.getId() + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.updateItemById(itemDto.getId(), itemDto, userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateItemById_whenUserNotExists_thenThrowNotFoundException() {
        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(new Item()));
        when(userRepository.existsById(userId)).thenReturn(false);
        String expectedMessage = "User with id = " + userId + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.updateItemById(itemDto.getId(), itemDto, userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateItemById_whenUserIsNotItemOwner_thenThrowForbiddenException() {
        //given
        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);
        String expectedMessage = "Access denied. User with id = " + userId + " does not have permission to change this item.";
        Exception exception = assertThrows(ForbiddenException.class, () -> itemService.updateItemById(itemDto.getId(), itemDto, userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateItemById_whenInvoked_thenSaveAndReturnUpdatedItem() {
        //given
        Long ownerId = 3L;

        ItemDto updatedItemDto = new ItemDto(
                1L,
                "Updated item 1",
                "Updated item 1",
                true,
                ownerId,
                null,
                null,
                null
        );

        Item savedItem = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        Item updatedItem = new Item(
                1L,
                "Updated item 1",
                "Updated item 1",
                true,
                3L,
                null,
                null,
                null
        );

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(savedItem));
        when(userRepository.existsById(ownerId)).thenReturn(true);

        ItemDto returnedItemDto = itemService.updateItemById(savedItem.getId(), updatedItemDto, ownerId);

        //then
        verify(itemRepository, times(1)).save(updatedItem);
        assertEquals(updatedItemDto.getId(), returnedItemDto.getId());
        assertEquals(updatedItemDto.getName(), returnedItemDto.getName());
        assertEquals(updatedItemDto.getDescription(), returnedItemDto.getDescription());
    }

    @Test
    public void deleteItemById_whenItemNotExists_thenThrowNotFoundException() {
        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.empty());
        String expectedMessage = "Item with id = " + itemDto.getId() + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.deleteItemById(itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteItemById_whenUserNotExists_thenThrowNotFoundException() {
        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(new Item()));
        when(userRepository.existsById(userId)).thenReturn(false);
        String expectedMessage = "User with id = " + userId + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.deleteItemById(itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteItemById_whenUserIsNotOwner_thenThrowForbiddenException() {
        //given
        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);
        String expectedMessage = "Access denied. User with id = " + userId + " does not have permission to change this item.";
        Exception exception = assertThrows(ForbiddenException.class, () -> itemService.deleteItemById(itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteItemById_whenInvoked_thenInvokeItemRepository() {
        //given
        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        //when
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(userRepository.existsById(item.getOwnerId())).thenReturn(true);
        itemService.deleteItemById(itemDto.getId(), item.getOwnerId());

        //then
        verify(itemRepository, times(1)).deleteById(itemDto.getId());
    }

    @Test
    public void createComment_whenItemNotExists_thenThrowNotFoundException() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.empty());
        String expectedMessage = "Item with id = " + itemDto.getId() + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.createComment(commentDto, itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createComment_whenUserNotExists_thenThrowNotFoundException() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        String expectedMessage = "User with id = " + userId + " not found.";
        Exception exception = assertThrows(NotFountException.class, () -> itemService.createComment(commentDto, itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createComment_whenUserNotRentItem_thenThrowBadRequestException() {
        //given
        CommentDto commentDto = new CommentDto(
                null,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findByBookerIdAndItemId(userId, itemDto.getId())).thenReturn(List.of());
        String expectedMessage = "User with id = " + userId + " did not rent this item.";
        Exception exception = assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto, itemDto.getId(), userId));

        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createComment_whenSave_thenSetCreatedAndAuthorAndItem() {
        //given
        Item item = new Item(
                1L,
                "New item 1",
                "New item 1",
                true,
                3L,
                null,
                null,
                null
        );

        User user = new User(
                1L,
                "new user",
                "user@email.ru"
        );

        Booking booking = new Booking(
                1L,
                item,
                user,
                BookingStatus.APPROVED,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1)
        );

        CommentDto commentDto = new CommentDto(
                1L,
                "new comment",
                "new user",
                LocalDateTime.now()
        );

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(userId, itemDto.getId())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(new Comment(1L, "text", item, user, LocalDateTime.now()));
        itemService.createComment(commentDto, itemDto.getId(), userId);

        //then
        verify(commentRepository).save(commentCaptor.capture());
        assertNotNull(commentCaptor.getValue().getCreated());
        assertEquals(user, commentCaptor.getValue().getAuthor());
        assertEquals(item, commentCaptor.getValue().getItem());
    }
}