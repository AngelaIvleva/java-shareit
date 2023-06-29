package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.client.ItemRequestClient;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.user.ToCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.item.controller.ItemController.HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER) Long userId,
                                                    @Validated(ToCreate.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(HEADER) Long userId,
                                                     @PositiveOrZero @RequestParam(value = "from",
                                                             defaultValue = "0", required = false) int from,
                                                     @Positive @RequestParam(value = "size", defaultValue = "10",
                                                             required = false) int size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(HEADER) Long userId,
                                                     @PathVariable("requestId") Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
