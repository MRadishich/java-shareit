package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findByRequesterId_whenInvoked_thenReturnItemRequestsOnlyWithGivenRequesterId() {
        //given
        User owner = new User(
                null,
                "new user",
                "user@emal.com"
        );

        User user = new User(
                null,
                "old user",
                "old@emal.com"
        );

        Item item1 = new Item(
                null,
                "new item 1",
                "item 1",
                true,
                1L,
                null,
                null,
                null
        );

        Item item2 = new Item(
                null,
                "item 2",
                "New item 2",
                true,
                1L,
                null,
                null,
                null
        );

        ItemRequest itemRequest1 = new ItemRequest(
                null,
                2L,
                "I need items",
                LocalDateTime.now().minusDays(3),
                List.of(item1)
        );
        ItemRequest itemRequest2 = new ItemRequest(
                null,
                2L,
                "I need items",
                LocalDateTime.now().minusDays(1),
                List.of(item2)
        );

        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        entityManager.persist(owner);
        entityManager.persist(user);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);

        //when
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(2L, sort);

        //then
        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest2, itemRequests.get(0));
        assertEquals(itemRequest1, itemRequests.get(1));
    }

    @Test
    void findByRequesterIdNot_whenInvoked_thenReturnOtherItemRequests() {
        //given
        User owner = new User(
                null,
                "new user",
                "user@emal.com"
        );

        User user1 = new User(
                null,
                "old user",
                "old@emal.com"
        );

        User user2 = new User(
                null,
                "new old user",
                "newold@emal.com"
        );

        Item item1 = new Item(
                null,
                "new item 1",
                "item 1",
                true,
                1L,
                null,
                null,
                null
        );

        Item item2 = new Item(
                null,
                "item 2",
                "New item 2",
                true,
                1L,
                null,
                null,
                null
        );

        ItemRequest itemRequest1 = new ItemRequest(
                null,
                2L,
                "I need items",
                LocalDateTime.now().minusDays(3),
                List.of(item1)
        );

        ItemRequest itemRequest2 = new ItemRequest(
                null,
                3L,
                "I need items",
                LocalDateTime.now().minusDays(1),
                List.of(item2)
        );

        ItemRequest itemRequest3 = new ItemRequest(
                null,
                3L,
                "I need items",
                LocalDateTime.now().plusDays(1),
                List.of(item1)
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        entityManager.persist(owner);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);
        entityManager.persist(itemRequest3);

        //when
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdNot(2L, pageable);

        //then
        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest3, itemRequests.get(0));
        assertEquals(itemRequest2, itemRequests.get(1));
    }
}