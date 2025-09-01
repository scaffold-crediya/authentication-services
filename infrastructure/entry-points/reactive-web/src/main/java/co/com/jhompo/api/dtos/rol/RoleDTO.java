package co.com.jhompo.api.dtos.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO para las respuestas de la API
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleDTO{
        int id;
        String name;
        String description;
}

