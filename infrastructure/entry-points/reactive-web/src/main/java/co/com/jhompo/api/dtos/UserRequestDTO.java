package co.com.jhompo.api.dtos;

import co.com.jhompo.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;
    private LocalDate dateBirth;
    private String identityDocument;


    // Method to convert the DTO to a domain object
    public User toDomain() {
        return User.builder()
                .firstName(this.firstName)
                .lastName(this.lastName)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .baseSalary(this.baseSalary)
                .birthDate(this.dateBirth)
                .identityDocument(this.identityDocument).build();
    }
}
