package guru.qa.niffler.model.api;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SpendJson {
    private UUID id;
    private Date spendDate;
    private CategoryJson category;
    private CurrencyValues currency;
    private Double amount;
    private String description;
    private String username;

    public static SpendJson create(CategoryJson category, Double amount, CurrencyValues currency, String description, String username) {
        return new SpendJson(null, new Date(), category, currency, amount, description, username);
    }

    public static SpendJson fromEntity(SpendEntity spendEntity) {
        return new SpendJson()
                .setId(spendEntity.getId())
                .setSpendDate(spendEntity.getSpendDate())
                .setCategory(CategoryJson.fromEntity(spendEntity.getCategory()))
                .setCurrency(spendEntity.getCurrency())
                .setAmount(spendEntity.getAmount())
                .setDescription(spendEntity.getDescription())
                .setUsername(spendEntity.getUsername());
    }
}
