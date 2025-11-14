package guru.qa.niffler.database.repository.spring.jdbc.extractor;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipState;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserdataUserRowExtractor implements ResultSetExtractor<Map<UUID, UserEntity>> {
    public static final UserdataUserRowExtractor INSTANCE = new UserdataUserRowExtractor();

    @Override
    public Map<UUID, UserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserEntity> userStore = new HashMap<>();
        Set<FriendshipEntity> userFriendshipStore = new HashSet<>();
        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);
            if (!userStore.containsKey(userId)) {
                UserEntity user = new UserEntity()
                        .setId(userId)
                        .setUsername(rs.getString("username"))
                        .setFullName(rs.getString("full_name"))
                        .setSurname(rs.getString("surname"))
                        .setFirstname(rs.getString("firstname"))
                        .setCurrency(CurrencyValues.valueOf(rs.getString("currency")))
                        .setPhoto(rs.getBytes("photo"))
                        .setPhotoSmall(rs.getBytes("photo_small"));
                userStore.put(userId, user);
            }

            FriendshipEntity userFriendship = new FriendshipEntity()
                    .setFriendShipId(new FriendshipEntity.FriendshipId(
                            rs.getObject("requester_id", UUID.class),
                            rs.getObject("addressee_id", UUID.class)))
                    .setStatus(FriendshipState.valueOf(
                            rs.getString("status")));
            userFriendshipStore.add(userFriendship);
        }

        userFriendshipStore.forEach(friendshipEntity -> {
            UUID requesterId = friendshipEntity.getFriendShipId().requester();
            UserEntity requester = userStore.get(requesterId);
            friendshipEntity.setRequester(requester);
            requester.getRequests().add(friendshipEntity);

            UUID addresseeId = friendshipEntity.getFriendShipId().addressee();
            UserEntity addressee = userStore.get(addresseeId);
            friendshipEntity.setAddressee(addressee);
            addressee.getAddressees().add(friendshipEntity);
        });
        return userStore;
    }
}
