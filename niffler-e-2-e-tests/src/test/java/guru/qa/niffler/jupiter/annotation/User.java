package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static guru.qa.niffler.model.User.DEFAULT_USER;

@ExtendWith({
        CategoryExtension.class,
        SpendingExtension.class
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface User {

    //TODO: Скорректировать тип, после того как enum будет удален
    guru.qa.niffler.model.User user() default DEFAULT_USER;

    Category[] categories() default {};

    Spending[] spending() default {};
}
