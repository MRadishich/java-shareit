package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void findByNameAndDescription_whenInvoked_thenReturnItemsThatContainKeywordInNameOrDescription() {
        //given
        User owner = new User(
                null,
                "new user",
                "user@emal.com"
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

        Item item3 = new Item(
                null,
                "old item 2",
                "old item 2",
                true,
                1L,
                null,
                null,
                null
        );

        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));

        entityManager.persist(owner);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        //when
        List<Item> items = itemRepository.findByNameAndDescription("new", "new", pageable);

        //then
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    void findByOwnerId_whenInvoked_thenReturnOnlyItemsWithGivenOwnerId() {
        //given
        User owner1 = new User(
                null,
                "new user",
                "user@emal.com"
        );

        User owner2 = new User(
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

        Item item3 = new Item(
                null,
                "old item 2",
                "old item 2",
                true,
                2L,
                null,
                null,
                null
        );

        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        //when
        List<Item> items = itemRepository.findByOwnerId(1L, pageable);

        //then
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }
}