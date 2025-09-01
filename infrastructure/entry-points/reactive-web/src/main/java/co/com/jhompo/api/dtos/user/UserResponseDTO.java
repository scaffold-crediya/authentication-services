package co.com.jhompo.api.dtos.user;

import co.com.jhompo.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String identityDocument;
    private BigDecimal baseSalary;
    private LocalDate birthDate;


    // Static method to convert a domain object to a DTO
    public static UserResponseDTO toDomain(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setAddress(user.getAddress());
        dto.setFirstName(user.getFirstName()); // Assuming your User domain object has getFirstName
        dto.setLastName(user.getLastName()); // Assuming your User domain object has getLastName
        dto.setEmail(user.getEmail());
        dto.setIdentityDocument(user.getIdentityDocument());
        dto.setBaseSalary(user.getBaseSalary());
        dto.setBirthDate(user.getBirthDate());
        return dto;
    }
}
