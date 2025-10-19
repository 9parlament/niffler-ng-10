package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.api.SpendJson;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class SpendEntity {
    private UUID id;
    private String username;
    private Date spendDate;
    private CurrencyValues currency;
    private Double amount;
    private String description;
    private CategoryEntity category;

    public static SpendEntity fromJson(SpendJson spendJson) {
        return new SpendEntity()
                .setId(spendJson.getId())
                .setUsername(spendJson.getUsername())
                .setSpendDate(spendJson.getSpendDate())
                .setCurrency(spendJson.getCurrency())
                .setAmount(spendJson.getAmount())
                .setDescription(spendJson.getDescription())
                .setCategory(CategoryEntity.fromJson(spendJson.getCategory()));
    }
}
