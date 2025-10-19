package guru.qa.niffler.ui.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.niffler.model.CurrencyValues;
import lombok.NoArgsConstructor;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@NoArgsConstructor(access = PRIVATE)
public class NifflerCondition {

    public static WebElementCondition amount(Double amount, CurrencyValues currency) {
        return new WebElementCondition("amount") {
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                String amountValue = element.getText();
                String expectedAmount = amount.toString().endsWith(".0")
                        ? amount.toString().replace(".0", EMPTY) + SPACE + currency.getSymbol()
                        : amount + SPACE + currency.getSymbol();

                return new CheckResult(amountValue.equals(expectedAmount), amountValue);
            }
        };
    }

    public static WebElementCondition date(Date date) {
        return new WebElementCondition("date") {
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                String dateValue = element.getText();
                String expectedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date);
                return new CheckResult(dateValue.equals(expectedDate), dateValue);
            }
        };
    }
}
