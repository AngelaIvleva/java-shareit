package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.item.controller.ItemController.HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(HEADER) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getItemRequests(@RequestHeader(HEADER) Long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequests(@RequestHeader(HEADER) Long userId,
                                                           @RequestParam(value = "from",
                                                                   defaultValue = "0", required = false) int from,
                                                           @RequestParam(value = "size", defaultValue = "10",
                                                                   required = false) int size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(@RequestHeader(HEADER) Long userId,
                                                     @PathVariable("requestId") Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
