package ru.main.passcode.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.main.passcode.models.Authority;

@Data
@NoArgsConstructor
public class PersonDTO {
    private long id;
    @NotBlank(message = "Поле не может быть пустым")
    @Email(message = "Не валидный email")
    private String username;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть от 8 знаков")
    private String password;
    @NotBlank(message = "Поле не может быть пустым")
    private String passwordConfirm;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 2, max = 10, message = "Имя может быть от 2 до 10 символов")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Имя должно быть в формате: 'Иван'")
    private String firstName;
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Фамилия должно быть в формате: 'Иванов'")
    private String lastname;
    private Authority authority;
}
