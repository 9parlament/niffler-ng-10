package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.api.SpendJson;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity
@Table(name = "SPEND")
@Accessors(chain = true)
public class SpendEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private Date spendDate;
    @Enumerated(STRING)
    private CurrencyValues currency;
    private Double amount;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
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
