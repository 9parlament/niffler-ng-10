package guru.qa.niffler.ui.page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Page {
    /**
     * Относится ли страница к взаимодействию с процессами авторизации
     */
    boolean auth() default false;

    /**
     * Путь к странице
     */
    String value();
}
