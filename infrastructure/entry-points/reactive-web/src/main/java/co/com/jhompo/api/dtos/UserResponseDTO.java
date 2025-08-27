package co.com.jhompo.api.dtos;

import co.com.jhompo.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;


    // Static method to convert a domain object to a DTO
    public static UserResponseDTO toDomain(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName()); // Assuming your User domain object has getFirstName
        dto.setLastName(user.getLastName()); // Assuming your User domain object has getLastName
        dto.setEmail(user.getEmail());
        dto.setIdentityDocument(user.getIdentityDocument());
        return dto;
    }
}
