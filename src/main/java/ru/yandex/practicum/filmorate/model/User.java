package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @Email
    @NotBlank
    private String email;
    @Pattern(regexp = "[a-zA-Z0-9]{4,12}$",
            message = "username must be of 4 to 12 length with no special characters")
    private String login;
    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
