package co.com.jhompo.api.dtos.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para las solicitudes de creación/actualización
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleRequestDTO {
    String name;
    String description;
}
