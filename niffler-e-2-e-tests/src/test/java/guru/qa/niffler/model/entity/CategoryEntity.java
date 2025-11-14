package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.api.CategoryJson;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "CATEGORY")
public class CategoryEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String username;
    private boolean archived;
    @OneToMany(mappedBy = "category", cascade = REMOVE)
    List<SpendEntity> spends = new ArrayList<>();

    public static CategoryEntity fromJson(CategoryJson categoryJson) {
        return new CategoryEntity()
                .setId(categoryJson.getId())
                .setName(categoryJson.getName())
                .setUsername(categoryJson.getUsername())
                .setArchived(categoryJson.isArchived());
    }
}
