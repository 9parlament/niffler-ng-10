package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("username")
    private String username;
    @JsonProperty("archived")
    private boolean archived;

    public static CategoryJson create(String username, String name) {
        CategoryJson category = new CategoryJson(null, randomCategoryName(), username, false);
        return name.isEmpty()
                ? category
                : category.setName(name);
    }
}
