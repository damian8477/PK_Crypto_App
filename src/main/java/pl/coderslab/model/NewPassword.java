package pl.coderslab.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewPassword {
    @NotBlank(message = "Błędne hasło")
    @Size(min = 5, message = "Hasło musi zawierać conajmniej 5 znaków")
    private String pass1;
    @NotBlank(message = "Błędne hasło")
    private String pass2;
    @NotBlank(message = "Wprowadź token otrzymany w wiadomości email")
    @Size(min = 6, max = 6, message = "Nie prawidłowy token")
    private String token;
}
