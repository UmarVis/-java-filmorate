package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @Email
    @NotBlank
    private String email;
    @Pattern(regexp = "[a-zA-Z0-9]{4,12}$",
            message = "username must be of 6 to 12 length with no special characters")
    private String login;
    private String name;

    @NonNull
    private LocalDate birthday;
    private Set<Integer> friendsId;
}
