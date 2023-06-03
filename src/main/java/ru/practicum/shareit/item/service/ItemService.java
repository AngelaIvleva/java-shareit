package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemBookingDto getItemById(Long userId, Long itemId);

    List<ItemBookingDto> getItems(Long userId);

    List<ItemDto> searchItem(String text);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
