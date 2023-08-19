package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void toComment_whenInvoked_thenReturnComment() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        CommentDto commentDto = new CommentDto(
                1L,
                "Good item",
                "user",
                currentTime
        );

        Comment expectedComment = new Comment(
                1L,
                "Good item",
                null,
                null,
                currentTime
        );

        //when
        Comment returnedComment = CommentMapper.toComment(commentDto);

        //then
        assertEquals(expectedComment, returnedComment);
    }

    @Test
    void toCommentDto_whenInvoked_thenReturnCommentDto() {
        //given
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        Comment comment = new Comment(
                1L,
                "Good item",
                null,
                new User(1L, "user", "user@email.ru"),
                currentTime
        );

        CommentDto expectedCommentDto = new CommentDto(
                1L,
                "Good item",
                "user",
                currentTime
        );

        //when
        CommentDto returnedCommentDto = CommentMapper.toCommentDto(comment);

        //then
        assertEquals(expectedCommentDto, returnedCommentDto);

    }

    @Test
    void toCommentInnerDto() {
    }
}