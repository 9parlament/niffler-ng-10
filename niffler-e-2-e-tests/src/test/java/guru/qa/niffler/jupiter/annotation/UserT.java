package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.model.test.user.UserType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserT {

    UserType value();
}
