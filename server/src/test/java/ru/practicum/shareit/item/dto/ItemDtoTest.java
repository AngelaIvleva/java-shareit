package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> jacksonTester;

    @Test
    void itemDtoJsonTest() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("wand")
                .description("description")
                .build();

        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("wand");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

}