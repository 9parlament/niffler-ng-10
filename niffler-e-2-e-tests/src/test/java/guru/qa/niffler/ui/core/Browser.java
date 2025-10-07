package guru.qa.niffler.ui.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.ui.page.Page;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static guru.qa.niffler.config.Configuration.CFG;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Browser {

    //TODO: Подключить в extension
    public static void init() {
        Configuration.browser = CFG.browser();
        if (CFG.isRemoteBrowser()) setRemoteProperties();
    }

    public static <T> T open(Class<T> pageObj) {
        Page page = pageObj.getAnnotation(Page.class);
        if (Objects.isNull(page)) {
            throw new IllegalArgumentException("PageObj-классы должны быть аннотированы @Page");
        }
        if (page.auth()) {
            return Selenide.open(CFG.frontAuthUrl() + page.value(), pageObj);
        } else {
            setCookies();
            return Selenide.open(CFG.frontBaseUrl() + page.value(), pageObj);
        }
    }

    private static void setCookies() {
        //TODO: Добавить реализацию установки авторизационных кук
    }

    private static void setRemoteProperties() {
        //TODO: Добавить реализацию конфигурирования настроек для удалённого браузера
        //TODO: Но сначала нужно собрать образы с браузерами для arm64 )=
    }
}
