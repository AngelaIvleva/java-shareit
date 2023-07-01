package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.mapper.ItemRequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = checkUserExistence(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest request = requestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto, requestor));

        return ItemRequestMapper.mapToItemRequestDto(request);
    }

    @Override
    @Transactional
    public List<ItemRequestResponseDto> getItemRequests(Long userId) {
        checkUserExistence(userId);
        List<ItemRequestResponseDto> itemRequestResponseDtoList = requestRepository.findAllByRequestorId(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestResponseDto)
                .collect(Collectors.toList());

        return setItems(itemRequestResponseDtoList);
    }

    @Override
    @Transactional
    public List<ItemRequestResponseDto> getAllItemRequests(Long userId, int from, int size) {
        checkUserExistence(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<ItemRequestResponseDto> itemRequestResponseDtoList = requestRepository.findAllPageable(userId, pageRequest)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestResponseDto)
                .collect(Collectors.toList());

        return setItems(itemRequestResponseDtoList);
    }

    @Override
    @Transactional
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        checkUserExistence(userId);
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос id %d не найден", requestId)));

        ItemRequestResponseDto responseDto = ItemRequestMapper.mapToItemRequestResponseDto(request);

        responseDto.setItems(itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));

        return responseDto;
    }

    private User checkUserExistence(Long userId) {
        log.info(String.format("Поиск пользователя с id %d", userId));
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя с id %d не найден", userId));
            throw new NotFoundException(String.format("Пользователь id %d  не найден", userId));
        });
    }

    private List<ItemRequestResponseDto> setItems(List<ItemRequestResponseDto> itemRequestResponseDtoList) {
        for (ItemRequestResponseDto itemRequestResponseDto : itemRequestResponseDtoList) {
            itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequestResponseDto.getId())
                    .stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        }
        itemRequestResponseDtoList.sort(Comparator.comparing(ItemRequestResponseDto::getCreated,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return itemRequestResponseDtoList;
    }
}
