package ru.practicum.item.mapper;

import lombok.Data;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(LocalDateTime.now())
                .build();
    }
}
