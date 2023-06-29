package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.ToCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = ToCreate.class)
    @Size(groups = ToCreate.class, min = 10, max = 200)
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
