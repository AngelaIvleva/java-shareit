package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.*;
import java.util.stream.Collectors;

import ru.practicum.shareit.item.model.Item;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> usersItems = new HashMap<>();
    private int id = 0;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        itemDto.setId(++id);
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setOwner(userId);
        usersItems.compute(userId, (id, items) -> {
            if (items == null) {
                items = new ArrayList<>();
            }
            items.add(item);
            return items;
        });
        log.info("Объект {} добавлен", item.getName());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = usersItems.get(userId).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .get();
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        usersItems.get(userId).removeIf(item1 -> item1.getId() == itemId);
        usersItems.get(userId).add(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Optional<Item> items = usersItems.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
        if (items.isPresent()) {
            return ItemMapper.toItemDto(items.get());
        } else {
            throw new NotFoundException(String.format("Объект %d не найден", itemId));
        }
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        if (usersItems.containsKey(userId)) {
            return usersItems.get(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        } else {
            throw new NotFoundException(String.format("Пользователь %d не найден " +
                    "или еще не добавил вещи для аренды", userId));
        }
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> itemList = new ArrayList<>();
        usersItems.forEach((userId, items) -> itemList.addAll(usersItems.get(userId)));

        return itemList.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) && !text.equals("") ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()) && !text.equals(""))
                .filter(Item::getAvailable)
                .sorted(Comparator.comparingLong(Item::getId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
