package guru.qa.niffler.common.utils;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@NoArgsConstructor(access = PRIVATE)
public class NifflerFaker {
    private static final Faker FAKER = new Faker();

    public static String randomCategoryName() {
        return FAKER.funnyName().name() + FAKER.number().randomDigit();
    }

    public static String randomUserName() {
        return FAKER.gameOfThrones().character().replace(SPACE, EMPTY) + FAKER.number().randomDigit();
    }

    public static String randomPassword() {
        return String.valueOf(FAKER.number().numberBetween(100, 999));
    }
}
