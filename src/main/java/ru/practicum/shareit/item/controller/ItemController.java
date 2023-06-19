package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.ToCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER) Long userId, @Validated(ToCreate.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) Long userId, @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@RequestHeader(HEADER) Long userId,
                                      @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemBookingDto> getItems(@RequestHeader(HEADER) Long userId,
                                         @PositiveOrZero @RequestParam(value = "from",
                                                 defaultValue = "0", required = false) int from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10",
                                                 required = false) int size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @PositiveOrZero @RequestParam(value = "from",
                                            defaultValue = "0", required = false) int from,
                                    @Positive @RequestParam(value = "size", defaultValue = "10",
                                            required = false) int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId,
                                    @Validated(ToCreate.class) @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}
