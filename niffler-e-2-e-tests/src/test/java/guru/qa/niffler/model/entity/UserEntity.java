package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.CurrencyValues;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "USER")
@Accessors(chain = true)
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String firstname;
    private String surname;
    private String fullname;
    private CurrencyValues currency;
    private byte[] photo;
    private byte[] photoSmall;
}
