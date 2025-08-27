package com.jhompo.api.dtos;

import java.util.UUID;
import com.jhompo.model.user.User;

public class UserResponseDTO {

    private UUID id;
    private String firstNames;
    private String lastNames;
    private String email;
    private String identificationDocument;

    // Getters
    public UUID getId() {
        return id;
    }
    public String getFirstNames() {
        return firstNames;
    }
    public String getLastNames() {
        return lastNames;
    }
    public String getEmail() {
        return email;
    }
    public String getIdentificationDocument() {
        return identificationDocument;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }
    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }
    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setIdentificationDocument(String identificationDocument) {
        this.identificationDocument = identificationDocument;
    }

    // Static method to convert a domain object to a DTO
    public static UserResponseDTO toDomain(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstNames(user.getFirstName()); // Assuming your User domain object has getFirstName
        dto.setLastNames(user.getLastName()); // Assuming your User domain object has getLastName
        dto.setEmail(user.getEmail());
        dto.setIdentificationDocument(user.getIdentityDocument());
        return dto;
    }
}
