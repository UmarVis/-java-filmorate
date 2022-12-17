package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    @Min(1)
    private Integer id;
    @NonNull
    @NotEmpty
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private Integer duration;
    private Set<Integer> likeId;
}
