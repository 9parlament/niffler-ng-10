package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.RUB;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpendJson {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("spendDate")
    private Date spendDate;
    @JsonProperty("category")
    private CategoryJson category;
    @JsonProperty("currency")
    private CurrencyValues currency;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("description")
    private String description;
    @JsonProperty("username")
    private String username;

    public static SpendJson create(CategoryJson category, Double amount, String description, String username) {
        return new SpendJson(null, new Date(), category, RUB, amount, description, username);
    }
}
