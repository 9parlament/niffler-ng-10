package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

//TODO: Додумать реализацию и подключить
public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }
}
