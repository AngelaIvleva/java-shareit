package ru.practicum.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.booking.dto.BookingDateDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemBookingDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

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
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = checkUserExistence(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(
                    () -> new NotFoundException(String.format("Запрос id %d не найден", itemDto.getRequestId())));
            item.setItemRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDto> getItems(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemBookingDto> itemBookingDtoList = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageRequest)
                .stream()
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
    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of((from / size), size);
        return itemRepository.searchItemsByText(text, pageRequest).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long authorId, CommentDto commentDto) {
        Item item = checkItemExistence(itemId);
        User author = checkUserExistence(authorId);
        Optional<Booking> booking = bookingRepository.findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(
                item, author, LocalDateTime.now());
        if (booking.isEmpty()) {
            log.warn((String.format("Пользователь %s %d никогда не бронировал %s",
                    author.getName(), authorId, item.getName())));
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
