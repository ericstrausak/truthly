package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}