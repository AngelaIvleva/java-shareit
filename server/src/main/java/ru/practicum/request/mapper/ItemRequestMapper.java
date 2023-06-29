package ru.practicum.request.mapper;


import lombok.Data;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.util.ArrayList;

@Data
public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDto requestDto, User requestor) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .requestor(requestor)
                .description(requestDto.getDescription())
                .created(requestDto.getCreated())
                .build();
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .requestorId(itemRequest.getRequestor().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestResponseDto mapToItemRequestResponseDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .requestorId(itemRequest.getRequestor().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }

}
