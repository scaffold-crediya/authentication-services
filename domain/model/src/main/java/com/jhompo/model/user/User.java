package com.jhompo.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final BigDecimal baseSalary;
    private final LocalDate birthDate;
    private final String identityDocument;
}
