package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.CurrencyValues;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserEntity {
    private UUID id;
    private String username;
    private String firstname;
    private String surname;
    private String fullname;
    private CurrencyValues currency;
    private byte[] photo;
    private byte[] photoSmall;
}
