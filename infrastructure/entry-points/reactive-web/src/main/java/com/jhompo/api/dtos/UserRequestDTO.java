package com.jhompo.api.dtos;


import com.jhompo.model.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UserRequestDTO {

    private String firstNames;
    private String lastNames;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;
    private LocalDate dateOfBirth;
    private String identificationDocument;

    public UserRequestDTO() {}

    public UserRequestDTO(String firstNames, String lastNames, String address, String phoneNumber, String email, BigDecimal baseSalary, LocalDate dateOfBirth, String identificationDocument) {
        this.firstNames = firstNames;
        this.lastNames = lastNames;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.baseSalary = baseSalary;
        this.dateOfBirth = dateOfBirth;
        this.identificationDocument = identificationDocument;
    }

    // Getters
    public String getFirstNames() { return firstNames; }
    public String getLastNames() { return lastNames; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getIdentificationDocument() { return identificationDocument; }

    // Setters
    public void setFirstNames(String firstNames) { this.firstNames = firstNames; }
    public void setLastNames(String lastNames) { this.lastNames = lastNames; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setIdentificationDocument(String identificationDocument) { this.identificationDocument = identificationDocument; }

    // Method to convert the DTO to a domain object
    public User toDomain() {
        User user = User.builder()
                    .firstName(this.firstNames)
                    .lastName(this.lastNames)
                    .address(this.address)
                    .phoneNumber(this.phoneNumber)
                    .email(this.email)
                    .baseSalary(this.baseSalary)
                    .birthDate(this.dateOfBirth)
                    .identityDocument(this.identificationDocument).build();
        return user;
    }
}