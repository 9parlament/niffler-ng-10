package guru.qa.niffler.model.test.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    EMPTY,
    WITH_FRIEND,
    WITH_INCOME_REQUEST,
    WITH_OUTCOME_REQUEST;
}
