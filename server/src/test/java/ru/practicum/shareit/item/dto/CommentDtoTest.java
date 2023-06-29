package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.item.dto.CommentDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> jacksonTester;

    @Test
    void commentDtoJsonTest() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("that's ok")
                .build();

        JsonContent<CommentDto> result = jacksonTester.write(commentDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("that's ok");

    }

}