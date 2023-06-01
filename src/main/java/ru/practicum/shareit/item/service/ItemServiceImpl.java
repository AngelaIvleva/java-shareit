package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь %d не найден", userId));
        });
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Объект %d не найден", itemId));
        });
        if (item.getOwner().getId() != userId) {
            log.info(String.format("Пользователь %d не является владельцем %s", userId, itemDto.getName()));
            throw new NotFoundException(String.format("Пользователь %d не является владельцем %s",
                    userId, itemDto.getName()));
        } else {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.save(item));
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Объект id %d не найден", itemId));
        });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
