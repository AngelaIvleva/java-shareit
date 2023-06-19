package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> jacksonTester;

    @Test
    void bookingDtoJsonTest() throws IOException {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-06-17T17:40:27.285586"))
                .end(LocalDateTime.parse("2023-06-18T17:40:27.285586"))
                .build();

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-17T17:40:27.285586");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-18T17:40:27.285586");
    }

}