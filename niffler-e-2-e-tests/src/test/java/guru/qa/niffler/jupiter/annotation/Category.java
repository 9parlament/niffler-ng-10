package guru.qa.niffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {

    String name() default EMPTY;

    boolean isArchived();
}
