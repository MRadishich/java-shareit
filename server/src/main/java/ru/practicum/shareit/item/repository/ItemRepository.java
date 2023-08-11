package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE UPPER(i.name) LIKE(UPPER(CONCAT('%', :name, '%'))) " +
            "OR UPPER(i.description) LIKE(UPPER(CONCAT('%', :description, '%'))) " +
            "AND i.available = true")
    List<Item> findByNameAndDescription(String name, String description, Pageable pageable);

    List<Item> findByOwnerId(Long userId, Pageable pageable);
}
