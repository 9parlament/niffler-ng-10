package guru.qa.niffler.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum User {
    DEFAULT_USER("niffler", "123");

    private final String username;
    private final String password;
}
