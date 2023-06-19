package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> jacksonTester;

    @Test
    void userDtoJsonTest() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Harry")
                .email("email@hp.com")
                .build();

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Harry");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@hp.com");
    }

}