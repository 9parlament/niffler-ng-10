package guru.qa.niffler.common.utils;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@NoArgsConstructor(access = PRIVATE)
public class NifflerFaker {
    private static final Faker FAKER = new Faker();

    public static String getCategoryName() {
        return FAKER.funnyName().name() + FAKER.number().randomDigit();
    }

    public static String getUserName() {
        return FAKER.gameOfThrones().character().replace(SPACE, EMPTY) + FAKER.number().randomDigit();
    }
}
