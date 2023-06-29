package ru.practicum.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.client.ItemClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.ToCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER) Long userId,
                                             @Validated(ToCreate.class) @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) Long userId, @PathVariable("itemId") Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER) Long userId,
                                              @PathVariable("itemId") Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HEADER) Long userId,
                                           @PositiveOrZero @RequestParam(value = "from",
                                                   defaultValue = "0", required = false) int from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10",
                                                   required = false) int size) {
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER) Long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(value = "from",
                                                     defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(value = "size", defaultValue = "10",
                                                     required = false) int size) {
        return itemClient.searchItem(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId,
                                                @Validated(ToCreate.class) @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
