package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingInnerDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {

    private final Long id;

    @NotBlank(groups = Create.class)
    private final String name;

    @NotBlank(groups = Create.class)
    private final String description;

    @NotNull(groups = Create.class)
    private final Boolean available;

    private final BookingInnerDto lastBooking;

    private final BookingInnerDto nextBooking;

    private final List<CommentInnerDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
