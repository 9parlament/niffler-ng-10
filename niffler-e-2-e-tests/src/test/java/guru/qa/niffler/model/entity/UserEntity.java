package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.api.UserJson;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.RUB;
import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.nonNull;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
@Accessors(chain = true)
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String firstname;
    private String surname;
    private String fullName;
    @Enumerated(STRING)
    private CurrencyValues currency;
    private byte[] photo;
    private byte[] photoSmall;
    @OneToMany(mappedBy = "requester", fetch = FetchType.EAGER)
    private List<FriendshipEntity> requests = new ArrayList<>();
    @OneToMany(mappedBy = "addressee", fetch = FetchType.EAGER)
    private List<FriendshipEntity> addressees = new ArrayList<>();

    public static UserEntity create(String username) {
        return new UserEntity()
                .setUsername(username)
                .setCurrency(RUB);
    }

    public static UserEntity from(UserJson user) {
        return new UserEntity()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstname(user.getFirstname())
                .setSurname(user.getSurname())
                .setFullName(user.getFullname())
                .setCurrency(user.getCurrency())
                .setPhoto(nonNull(user.getPhoto()) ? Base64.getDecoder().decode(user.getPhoto()) : null)
                .setPhotoSmall(nonNull(user.getPhotoSmall()) ? Base64.getDecoder().decode(user.getPhotoSmall()) : null);
    }
}
