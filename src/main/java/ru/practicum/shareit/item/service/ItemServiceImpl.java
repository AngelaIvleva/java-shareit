package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userRepository.getUserById(userId);
        return itemRepository.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        if (itemRepository.getItems(userId).stream().noneMatch(item -> item.getId() == itemId)) {
            throw new NotFoundException(String.format("Объект %d не найден", itemId));
        }
        return itemRepository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text);
    }
}
