package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    @Min(1)
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friendsId;
}
