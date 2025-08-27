package co.com.jhompo.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String firstName;
    private  String lastName;
    private  String address;
    private  String phoneNumber;
    private  String email;
    private  BigDecimal baseSalary;
    private  LocalDate birthDate;
    private  String identityDocument;
}
