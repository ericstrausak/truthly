package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}