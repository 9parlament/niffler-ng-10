package guru.qa.niffler.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

/**
 * Управляет конфигурацией тестовых окружений.
 * <p>
 * Автоматически загружает настройки из файла конфигурации, соответствующего
 * активному профилю. Профиль определяется через системную переменную {@code profile}.
 * <p>
 * <b>Профиль по умолчанию:</b> {@code local}
 */
@Config.Sources("classpath:config.${profile}.properties")
public interface Configuration extends Config {
    Configuration CFG = ConfigFactory.create(Configuration.class);

    // Browser Configuration
    @Key("ui.browser.browser")
    String browser();

    @Key("ui.browser.version")
    String browserVersion();

    @Key("ui.browser.remote")
    boolean isRemoteBrowser();

    @Key("ui.browser.remoteUrl")
    String browserRemoteUrl();

    // URL Configuration
    @Key("url.front.base")
    String frontBaseUrl();

    @Key("url.front.auth")
    String frontAuthUrl();

    @Key("url.api.spend")
    String apiSpendUrl();

    @Key("url.api.auth")
    String apiAuthUrl();

    // Database Configuration
    @Key("db.username")
    String dbUsername();

    @Key("db.password")
    String dbPassword();

    @Key("db.jdbcUrl")
    String dbJdbcUrl();
}
