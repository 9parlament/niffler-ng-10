package guru.qa.niffler.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.entity.UserEntity;
import guru.qa.niffler.model.test.TestData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
public class UserJson {
    private UUID id;
    private String username;
    private String firstname;
    private String surname;
    private String fullname;
    private CurrencyValues currency;
    private String photo;
    private String photoSmall;
    private FriendshipStatus friendshipStatus;
    @JsonIgnore private TestData testData;

    public static UserJson from(UserEntity userEntity) {
        return new UserJson()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname())
                .setSurname(userEntity.getSurname())
                .setFullname(userEntity.getFullName())
                .setCurrency(userEntity.getCurrency())
                .setPhoto(Arrays.toString(userEntity.getPhoto()))
                .setPhotoSmall(Arrays.toString(userEntity.getPhotoSmall()));
    }
}