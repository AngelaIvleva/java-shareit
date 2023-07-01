package ru.practicum.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.State;
import ru.practicum.client.BaseClient;
import ru.practicum.exception.BadRequestException;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Long bookerId, BookingDto bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> changeStatus(Long ownerId, Long bookingId, Boolean approved) {
        return patch(
                "/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByBooker(Long bookerId, String state, int from, int size) {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(Long ownerId, String state, int from, int size) {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}