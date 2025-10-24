package guru.qa.niffler.model.api;

import guru.qa.niffler.model.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

import static guru.qa.niffler.common.utils.NifflerFaker.randomCategoryName;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CategoryJson {
    private UUID id;
    private String name;
    private String username;
    private boolean archived;

    public static CategoryJson create(String username, String name) {
        CategoryJson category = new CategoryJson(null, randomCategoryName(), username, false);
        return name.isEmpty()
                ? category
                : category.setName(name);
    }

    public static CategoryJson fromEntity(CategoryEntity entity) {
        return new CategoryJson()
                .setId(entity.getId())
                .setName(entity.getName())
                .setUsername(entity.getUsername())
                .setArchived(entity.isArchived());
    }
}
