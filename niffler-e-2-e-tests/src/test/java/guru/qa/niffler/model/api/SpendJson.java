package guru.qa.niffler.model.api;

import guru.qa.niffler.model.CurrencyValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.RUB;

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

    public static SpendJson create(CategoryJson category, Double amount, String description, String username) {
        return new SpendJson(null, new Date(), category, RUB, amount, description, username);
    }
}
