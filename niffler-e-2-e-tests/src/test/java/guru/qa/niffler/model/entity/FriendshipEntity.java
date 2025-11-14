package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.FriendshipState;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity
@Table(name = "FRIENDSHIP")
@Accessors(chain = true)
public class FriendshipEntity {
    @EmbeddedId
    private FriendshipId friendShipId;
    @ManyToOne
    @JoinColumn(name = "requester_id", insertable = false, updatable = false)
    private UserEntity requester;
    @ManyToOne
    @JoinColumn(name = "addressee_id", insertable = false, updatable = false)
    private UserEntity addressee;
    @Enumerated(STRING)
    private FriendshipState status;
    private Date createdDate;

    public static FriendshipEntity createFriendship(UserEntity requester, UserEntity addressee) {
        return new FriendshipEntity()
                .setFriendShipId(new FriendshipId(requester.getId(), addressee.getId()))
                .setRequester(requester)
                .setAddressee(addressee)
                .setCreatedDate(new Date())
                .setStatus(FriendshipState.ACCEPTED);
    }

    public static FriendshipEntity createInvitation(UserEntity requester, UserEntity addressee) {
        return new FriendshipEntity()
                .setFriendShipId(new FriendshipId(requester.getId(), addressee.getId()))
                .setRequester(requester)
                .setAddressee(addressee)
                .setCreatedDate(new Date())
                .setStatus(FriendshipState.PENDING);
    }

    //TODO: Уточнить реализацию (во всех сущностях) после конфигурирования и взаимодействия с JPA
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipEntity that = (FriendshipEntity) o;
        return Objects.equals(friendShipId, that.friendShipId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(friendShipId);
    }

    @Embeddable
    public record FriendshipId(@Column(name = "requester_id") UUID requester,
                               @Column(name = "addressee_id") UUID addressee) {
    }
}
