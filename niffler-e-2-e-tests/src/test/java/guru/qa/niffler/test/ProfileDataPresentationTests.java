package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.ui.core.Browser;
import guru.qa.niffler.ui.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.User.DEFAULT_USER;

@DisplayName("Отображение данных профиля")
class ProfileDataPresentationTests {

    @Test
    @Category(isArchived = true)
    @DisplayName("Отображение архивной категории в списке, если опция \"Show archived\" активна")
    void archivedCategoryShouldPresentInCategoriesWhenShowArchivedIsEnabledTest(CategoryJson category) {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .goToProfilePage()
                .showArchivedCategories()
                .checkCategoryIsArchived(category.getName());
    }

    @Test
    @Category(isArchived = false)
    @DisplayName("Отображение активной категории в списке категорий после перехода на страницу профиля")
    void activeCategoryShouldPresentInCategoriesWhenProfilePageIsOpenedTest(CategoryJson category) {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .goToProfilePage()
                .checkCategoryIsActive(category.getName());
    }


    @Test
    @Category(isArchived = false)
    @DisplayName("Исчезновение активной категории из списка после её архивации. Опция \"Show archived\" не активна")
    void activeCategoryShouldNotPresentInCategoriesAfterArchiveThatTest(CategoryJson category) {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .goToProfilePage()
                .checkCategoryIsActive(category.getName())
                .archiveCategory(category.getName())
                .checkCategoryIsNotPresent(category.getName());
    }

    @Test
    @Category(isArchived = true)
    @DisplayName("Появление архивной категории в списке после её разархивации")
    void archivedCategoryShouldBeActiveAndPresentInCategoriesAfterUnarchiveThatTest(CategoryJson category) {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .goToProfilePage()
                .showArchivedCategories()
                .checkCategoryIsArchived(category.getName())
                .unarchiveCategory(category.getName())
                .checkCategoryIsActive(category.getName());
    }
}
