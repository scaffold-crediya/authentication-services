package co.com.jhompo.model.role;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {
    private int id;
    private String name;
    private String description;
}
