package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.api.CategoryJson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class CategoryEntity {
    private UUID id;
    private String name;
    private String username;
    private boolean archived;

    public static CategoryEntity fromJson(CategoryJson categoryJson) {
        return new CategoryEntity()
                .setId(categoryJson.getId())
                .setName(categoryJson.getName())
                .setUsername(categoryJson.getUsername())
                .setArchived(categoryJson.isArchived());
    }
}
