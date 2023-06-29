package ru.practicum.request.service;

import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getItemRequests(Long userId);

    List<ItemRequestResponseDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);

}
