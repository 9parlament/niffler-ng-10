package guru.qa.niffler.database.dao.spring.jdbc.mapper;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserdataUserRowMapper implements RowMapper<UserEntity> {
    public static final UserdataUserRowMapper INSTANCE = new UserdataUserRowMapper();

    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setCurrency(CurrencyValues.valueOf(rs.getString("currency")))
                .setFirstname(rs.getString("firstname"))
                .setSurname(rs.getString("surname"))
                .setPhoto(rs.getBytes("photo"))
                .setPhotoSmall(rs.getBytes("photo_small"))
                .setFullname(rs.getString("full_name"));
    }
}
