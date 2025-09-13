package co.com.jhompo.api.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDTOsTest {

    @Test
    void authDTOs_ShouldBeInstantiable() {
        AuthDTOs container = new AuthDTOs();
        assertNotNull(container);
    }

    @Test
    void loginRequestDTO_ShouldCreateWithEmailAndPassword() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // When
        AuthDTOs.LoginRequestDTO loginRequest = new AuthDTOs.LoginRequestDTO(email, password);

        // Then
        assertNotNull(loginRequest);
        assertEquals(email, loginRequest.email());
        assertEquals(password, loginRequest.password());
    }

    @Test
    void loginRequestDTO_ShouldAllowNullValues() {
        // Given & When
        AuthDTOs.LoginRequestDTO loginRequest = new AuthDTOs.LoginRequestDTO(null, null);

        // Then
        assertNotNull(loginRequest);
        assertNull(loginRequest.email());
        assertNull(loginRequest.password());
    }

    @Test
    void loginRequestDTO_ShouldHandleEmptyStrings() {
        // Given
        String emptyEmail = "";
        String emptyPassword = "";

        // When
        AuthDTOs.LoginRequestDTO loginRequest = new AuthDTOs.LoginRequestDTO(emptyEmail, emptyPassword);

        // Then
        assertNotNull(loginRequest);
        assertEquals("", loginRequest.email());
        assertEquals("", loginRequest.password());
    }

    @Test
    void loginRequestDTO_ShouldBeEqualWhenSameValues() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // When
        AuthDTOs.LoginRequestDTO loginRequest1 = new AuthDTOs.LoginRequestDTO(email, password);
        AuthDTOs.LoginRequestDTO loginRequest2 = new AuthDTOs.LoginRequestDTO(email, password);

        // Then
        assertEquals(loginRequest1, loginRequest2);
        assertEquals(loginRequest1.hashCode(), loginRequest2.hashCode());
    }

    @Test
    void loginRequestDTO_ShouldNotBeEqualWhenDifferentValues() {
        // Given & When
        AuthDTOs.LoginRequestDTO loginRequest1 = new AuthDTOs.LoginRequestDTO("test1@example.com", "password1");
        AuthDTOs.LoginRequestDTO loginRequest2 = new AuthDTOs.LoginRequestDTO("test2@example.com", "password2");

        // Then
        assertNotEquals(loginRequest1, loginRequest2);
    }

    @Test
    void loginRequestDTO_ShouldHaveToStringMethod() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        AuthDTOs.LoginRequestDTO loginRequest = new AuthDTOs.LoginRequestDTO(email, password);

        // When
        String toString = loginRequest.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("LoginRequestDTO"));
        assertTrue(toString.contains(email));
        assertTrue(toString.contains(password));
    }

    @Test
    void loginResponseDTO_ShouldCreateWithToken() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0";

        // When
        AuthDTOs.LoginResponseDTO loginResponse = new AuthDTOs.LoginResponseDTO(token);

        // Then
        assertNotNull(loginResponse);
        assertEquals(token, loginResponse.token());
    }

    @Test
    void loginResponseDTO_ShouldAllowNullToken() {
        // Given & When
        AuthDTOs.LoginResponseDTO loginResponse = new AuthDTOs.LoginResponseDTO(null);

        // Then
        assertNotNull(loginResponse);
        assertNull(loginResponse.token());
    }

    @Test
    void loginResponseDTO_ShouldHandleEmptyToken() {
        // Given
        String emptyToken = "";

        // When
        AuthDTOs.LoginResponseDTO loginResponse = new AuthDTOs.LoginResponseDTO(emptyToken);

        // Then
        assertNotNull(loginResponse);
        assertEquals("", loginResponse.token());
    }

    @Test
    void loginResponseDTO_ShouldBeEqualWhenSameToken() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

        // When
        AuthDTOs.LoginResponseDTO response1 = new AuthDTOs.LoginResponseDTO(token);
        AuthDTOs.LoginResponseDTO response2 = new AuthDTOs.LoginResponseDTO(token);

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void loginResponseDTO_ShouldNotBeEqualWhenDifferentTokens() {
        // Given & When
        AuthDTOs.LoginResponseDTO response1 = new AuthDTOs.LoginResponseDTO("token1");
        AuthDTOs.LoginResponseDTO response2 = new AuthDTOs.LoginResponseDTO("token2");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void loginResponseDTO_ShouldHaveToStringMethod() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        AuthDTOs.LoginResponseDTO loginResponse = new AuthDTOs.LoginResponseDTO(token);

        // When
        String toString = loginResponse.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("LoginResponseDTO"));
        assertTrue(toString.contains(token));
    }

    @Test
    void bothDTOs_ShouldBeIndependent() {
        // Given
        AuthDTOs.LoginRequestDTO request = new AuthDTOs.LoginRequestDTO("test@example.com", "password");
        AuthDTOs.LoginResponseDTO response = new AuthDTOs.LoginResponseDTO("token123");

        // Then
        assertNotNull(request);
        assertNotNull(response);
        assertNotEquals(request.getClass(), response.getClass());
    }


}