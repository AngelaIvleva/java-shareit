package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = checkUserExistence(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = checkItemExistence(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
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

    @Transactional
    @Override
    public ItemBookingDto getItemById(Long userId, Long itemId) {
        Item item = checkItemExistence(itemId);
        checkUserExistence(userId);
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(item);

        itemBookingDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        Optional<Booking> last = bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.DESC, "end"));
        Optional<Booking> next = bookingRepository.findFirstByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            return itemBookingDto;
        }

        itemBookingDto.setLastBooking(last.map(BookingMapper::toBookingDateDto).orElse(null));
        itemBookingDto.setNextBooking(next.map(BookingMapper::toBookingDateDto).orElse(null));

        if (itemBookingDto.getLastBooking() == null && itemBookingDto.getNextBooking() != null) {
            itemBookingDto.setLastBooking(itemBookingDto.getNextBooking());
            itemBookingDto.setNextBooking(null);
        }

        return itemBookingDto;
    }

    @Transactional
    @Override
    public List<ItemBookingDto> getItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemBookingDto> itemBookingDtoList = items.stream()
                .map(ItemMapper::toItemBookingDto)
                .collect(Collectors.toList());

        for (ItemBookingDto itemBookingDto : itemBookingDtoList) {

            itemBookingDto.setComments(commentRepository.findAllByItemId(itemBookingDto.getId())
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

            Optional<Booking> last = bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusIs(
                    itemBookingDto.getId(), LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

            Optional<Booking> next = bookingRepository.findFirstByItemIdAndEndIsAfterAndStatusIs(
                    itemBookingDto.getId(), LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

            itemBookingDto.setLastBooking(
                    last.isEmpty() ? new BookingDateDto() : BookingMapper.toBookingDateDto(last.get()));
            itemBookingDto.setNextBooking(
                    next.isEmpty() ? new BookingDateDto() : BookingMapper.toBookingDateDto(next.get()));
        }

        itemBookingDtoList.sort(Comparator.comparing(o -> o.getLastBooking().getStart(),
                Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemBookingDto itemBookingDto : itemBookingDtoList) {
            if (itemBookingDto.getLastBooking().getBookerId() == null) {
                itemBookingDto.setLastBooking(null);
            }
            if (itemBookingDto.getNextBooking().getBookerId() == null) {
                itemBookingDto.setNextBooking(null);
            }
        }

        return itemBookingDtoList;
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

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long authorId, CommentDto commentDto) {
        Item item = checkItemExistence(itemId);
        User author = checkUserExistence(authorId);
        Optional<Booking> booking = bookingRepository.findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(
                item, author, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new BadRequestException(String.format("Пользователь %s %d никогда не бронировал %s",
                    author.getName(), authorId, item.getName()));
        }

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        log.info(String.format("Создание комментария к %s", item.getName()));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User checkUserExistence(Long userId) {
        log.info(String.format("Поиск пользователя с id %d", userId));
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя с id %d не найден", userId));
            throw new NotFoundException(String.format("Пользователь id %d  не найден", userId));
        });
    }

    private Item checkItemExistence(Long itemId) {
        log.info(String.format("Поиск объекта с id %d", itemId));
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Объект id %d  не найден", itemId));
            throw new NotFoundException(String.format("Объект id %d  не найден", itemId));
        });
    }
}
