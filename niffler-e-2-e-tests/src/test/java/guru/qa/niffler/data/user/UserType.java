package guru.qa.niffler.data.user;

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
