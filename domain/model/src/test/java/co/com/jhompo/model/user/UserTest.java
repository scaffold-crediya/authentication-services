package co.com.jhompo.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private UUID testId;
    private String testFirstName;
    private String testLastName;
    private String testAddress;
    private String testPhoneNumber;
    private String testEmail;
    private BigDecimal testBaseSalary;
    private LocalDate testBirthDate;
    private String testIdentityDocument;
    private String testPassword;
    private int testRoleId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testFirstName = "María";
        testLastName = "González";
        testAddress = "Carrera 45 #12-34, Cartagena";
        testPhoneNumber = "+57 301 234 5678";
        testEmail = "maria.gonzalez@crediya.com";
        testBaseSalary = new BigDecimal("4500000");
        testBirthDate = LocalDate.of(1988, 8, 20);
        testIdentityDocument = "52345678";
        testPassword = "securePass2024";
        testRoleId = 2;
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create User with NoArgsConstructor")
        void shouldCreateUserWithNoArgsConstructor() {
            // When
            User user = new User();

            // Then
            assertNotNull(user);
            assertNull(user.getId());
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
            assertNull(user.getAddress());
            assertNull(user.getPhoneNumber());
            assertNull(user.getEmail());
            assertNull(user.getBaseSalary());
            assertNull(user.getBirthDate());
            assertNull(user.getIdentityDocument());
            assertNull(user.getPassword());
            assertEquals(0, user.getRoleId());
        }

        @Test
        @DisplayName("Should create User with AllArgsConstructor")
        void shouldCreateUserWithAllArgsConstructor() {
            // When
            User user = new User(
                    testId, testFirstName, testLastName, testAddress,
                    testPhoneNumber, testEmail, testBaseSalary, testBirthDate,
                    testIdentityDocument, testPassword, testRoleId
            );

            // Then
            assertAll("User AllArgsConstructor properties",
                    () -> assertEquals(testId, user.getId()),
                    () -> assertEquals(testFirstName, user.getFirstName()),
                    () -> assertEquals(testLastName, user.getLastName()),
                    () -> assertEquals(testAddress, user.getAddress()),
                    () -> assertEquals(testPhoneNumber, user.getPhoneNumber()),
                    () -> assertEquals(testEmail, user.getEmail()),
                    () -> assertEquals(testBaseSalary, user.getBaseSalary()),
                    () -> assertEquals(testBirthDate, user.getBirthDate()),
                    () -> assertEquals(testIdentityDocument, user.getIdentityDocument()),
                    () -> assertEquals(testPassword, user.getPassword()),
                    () -> assertEquals(testRoleId, user.getRoleId())
            );
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create User with Builder pattern")
        void shouldCreateUserWithBuilder() {
            // When
            User user = User.builder()
                    .id(testId)
                    .firstName(testFirstName)
                    .lastName(testLastName)
                    .address(testAddress)
                    .phoneNumber(testPhoneNumber)
                    .email(testEmail)
                    .baseSalary(testBaseSalary)
                    .birthDate(testBirthDate)
                    .identityDocument(testIdentityDocument)
                    .password(testPassword)
                    .roleId(testRoleId)
                    .build();

            // Then
            assertAll("Builder pattern properties",
                    () -> assertEquals(testId, user.getId()),
                    () -> assertEquals(testFirstName, user.getFirstName()),
                    () -> assertEquals(testLastName, user.getLastName()),
                    () -> assertEquals(testAddress, user.getAddress()),
                    () -> assertEquals(testPhoneNumber, user.getPhoneNumber()),
                    () -> assertEquals(testEmail, user.getEmail()),
                    () -> assertEquals(testBaseSalary, user.getBaseSalary()),
                    () -> assertEquals(testBirthDate, user.getBirthDate()),
                    () -> assertEquals(testIdentityDocument, user.getIdentityDocument()),
                    () -> assertEquals(testPassword, user.getPassword()),
                    () -> assertEquals(testRoleId, user.getRoleId())
            );
        }

        @Test
        @DisplayName("Should create User with partial Builder")
        void shouldCreateUserWithPartialBuilder() {
            // When
            User user = User.builder()
                    .firstName(testFirstName)
                    .email(testEmail)
                    .roleId(testRoleId)
                    .build();

            // Then
            assertAll("Partial builder properties",
                    () -> assertNull(user.getId()),
                    () -> assertEquals(testFirstName, user.getFirstName()),
                    () -> assertNull(user.getLastName()),
                    () -> assertNull(user.getAddress()),
                    () -> assertNull(user.getPhoneNumber()),
                    () -> assertEquals(testEmail, user.getEmail()),
                    () -> assertNull(user.getBaseSalary()),
                    () -> assertNull(user.getBirthDate()),
                    () -> assertNull(user.getIdentityDocument()),
                    () -> assertNull(user.getPassword()),
                    () -> assertEquals(testRoleId, user.getRoleId())
            );
        }

        @Test
        @DisplayName("Should create User with toBuilder")
        void shouldCreateUserWithToBuilder() {
            // Given
            User originalUser = User.builder()
                    .id(testId)
                    .firstName("Carlos")
                    .lastName("Rodríguez")
                    .email("carlos@crediya.com")
                    .roleId(1)
                    .build();

            String newFirstName = "Ana";
            String newEmail = "ana@crediya.com";

            // When
            User modifiedUser = originalUser.toBuilder()
                    .firstName(newFirstName)
                    .email(newEmail)
                    .build();

            // Then
            assertAll("ToBuilder functionality",
                    () -> assertEquals(testId, modifiedUser.getId()),
                    () -> assertEquals(newFirstName, modifiedUser.getFirstName()),
                    () -> assertEquals("Rodríguez", modifiedUser.getLastName()),
                    () -> assertEquals(newEmail, modifiedUser.getEmail()),
                    () -> assertEquals(1, modifiedUser.getRoleId())
            );
        }

        @Test
        @DisplayName("Should create empty User with Builder")
        void shouldCreateEmptyUserWithBuilder() {
            // When
            User user = User.builder().build();

            // Then
            assertAll("Empty builder",
                    () -> assertNull(user.getId()),
                    () -> assertNull(user.getFirstName()),
                    () -> assertNull(user.getLastName()),
                    () -> assertNull(user.getAddress()),
                    () -> assertNull(user.getPhoneNumber()),
                    () -> assertNull(user.getEmail()),
                    () -> assertNull(user.getBaseSalary()),
                    () -> assertNull(user.getBirthDate()),
                    () -> assertNull(user.getIdentityDocument()),
                    () -> assertNull(user.getPassword()),
                    () -> assertEquals(0, user.getRoleId())
            );
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersAndSettersTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = new User();
        }

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            // When
            user.setId(testId);

            // Then
            assertEquals(testId, user.getId());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void shouldSetAndGetFirstName() {
            // When
            user.setFirstName(testFirstName);

            // Then
            assertEquals(testFirstName, user.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void shouldSetAndGetLastName() {
            // When
            user.setLastName(testLastName);

            // Then
            assertEquals(testLastName, user.getLastName());
        }

        @Test
        @DisplayName("Should set and get address")
        void shouldSetAndGetAddress() {
            // When
            user.setAddress(testAddress);

            // Then
            assertEquals(testAddress, user.getAddress());
        }

        @Test
        @DisplayName("Should set and get phoneNumber")
        void shouldSetAndGetPhoneNumber() {
            // When
            user.setPhoneNumber(testPhoneNumber);

            // Then
            assertEquals(testPhoneNumber, user.getPhoneNumber());
        }

        @Test
        @DisplayName("Should set and get email")
        void shouldSetAndGetEmail() {
            // When
            user.setEmail(testEmail);

            // Then
            assertEquals(testEmail, user.getEmail());
        }

        @Test
        @DisplayName("Should set and get baseSalary")
        void shouldSetAndGetBaseSalary() {
            // When
            user.setBaseSalary(testBaseSalary);

            // Then
            assertEquals(testBaseSalary, user.getBaseSalary());
        }

        @Test
        @DisplayName("Should set and get birthDate")
        void shouldSetAndGetBirthDate() {
            // When
            user.setBirthDate(testBirthDate);

            // Then
            assertEquals(testBirthDate, user.getBirthDate());
        }

        @Test
        @DisplayName("Should set and get identityDocument")
        void shouldSetAndGetIdentityDocument() {
            // When
            user.setIdentityDocument(testIdentityDocument);

            // Then
            assertEquals(testIdentityDocument, user.getIdentityDocument());
        }

        @Test
        @DisplayName("Should set and get password")
        void shouldSetAndGetPassword() {
            // When
            user.setPassword(testPassword);

            // Then
            assertEquals(testPassword, user.getPassword());
        }

        @Test
        @DisplayName("Should set and get roleId")
        void shouldSetAndGetRoleId() {
            // When
            user.setRoleId(testRoleId);

            // Then
            assertEquals(testRoleId, user.getRoleId());
        }

        @Test
        @DisplayName("Should handle all setters and getters in sequence")
        void shouldHandleAllSettersAndGettersInSequence() {
            // When
            user.setId(testId);
            user.setFirstName(testFirstName);
            user.setLastName(testLastName);
            user.setAddress(testAddress);
            user.setPhoneNumber(testPhoneNumber);
            user.setEmail(testEmail);
            user.setBaseSalary(testBaseSalary);
            user.setBirthDate(testBirthDate);
            user.setIdentityDocument(testIdentityDocument);
            user.setPassword(testPassword);
            user.setRoleId(testRoleId);

            // Then
            assertAll("All getters after setters",
                    () -> assertEquals(testId, user.getId()),
                    () -> assertEquals(testFirstName, user.getFirstName()),
                    () -> assertEquals(testLastName, user.getLastName()),
                    () -> assertEquals(testAddress, user.getAddress()),
                    () -> assertEquals(testPhoneNumber, user.getPhoneNumber()),
                    () -> assertEquals(testEmail, user.getEmail()),
                    () -> assertEquals(testBaseSalary, user.getBaseSalary()),
                    () -> assertEquals(testBirthDate, user.getBirthDate()),
                    () -> assertEquals(testIdentityDocument, user.getIdentityDocument()),
                    () -> assertEquals(testPassword, user.getPassword()),
                    () -> assertEquals(testRoleId, user.getRoleId())
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null values in builder")
        void shouldHandleNullValuesInBuilder() {
            // When
            User user = User.builder()
                    .id(null)
                    .firstName(null)
                    .lastName(null)
                    .address(null)
                    .phoneNumber(null)
                    .email(null)
                    .baseSalary(null)
                    .birthDate(null)
                    .identityDocument(null)
                    .password(null)
                    .roleId(0)
                    .build();

            // Then
            assertAll("Null values handling",
                    () -> assertNull(user.getId()),
                    () -> assertNull(user.getFirstName()),
                    () -> assertNull(user.getLastName()),
                    () -> assertNull(user.getAddress()),
                    () -> assertNull(user.getPhoneNumber()),
                    () -> assertNull(user.getEmail()),
                    () -> assertNull(user.getBaseSalary()),
                    () -> assertNull(user.getBirthDate()),
                    () -> assertNull(user.getIdentityDocument()),
                    () -> assertNull(user.getPassword()),
                    () -> assertEquals(0, user.getRoleId())
            );
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            // When
            User user = User.builder()
                    .firstName("")
                    .lastName("")
                    .address("")
                    .phoneNumber("")
                    .email("")
                    .identityDocument("")
                    .password("")
                    .build();

            // Then
            assertAll("Empty strings handling",
                    () -> assertEquals("", user.getFirstName()),
                    () -> assertEquals("", user.getLastName()),
                    () -> assertEquals("", user.getAddress()),
                    () -> assertEquals("", user.getPhoneNumber()),
                    () -> assertEquals("", user.getEmail()),
                    () -> assertEquals("", user.getIdentityDocument()),
                    () -> assertEquals("", user.getPassword())
            );
        }

        @Test
        @DisplayName("Should handle negative roleId")
        void shouldHandleNegativeRoleId() {
            // Given
            int negativeRoleId = -5;

            // When
            User user = User.builder()
                    .roleId(negativeRoleId)
                    .build();

            // Then
            assertEquals(negativeRoleId, user.getRoleId());
        }

        @Test
        @DisplayName("Should handle zero and negative baseSalary")
        void shouldHandleZeroAndNegativeBaseSalary() {
            // Given
            BigDecimal zeroSalary = BigDecimal.ZERO;
            BigDecimal negativeSalary = new BigDecimal("-1000");

            // When
            User userWithZero = User.builder()
                    .baseSalary(zeroSalary)
                    .build();

            User userWithNegative = User.builder()
                    .baseSalary(negativeSalary)
                    .build();

            // Then
            assertEquals(zeroSalary, userWithZero.getBaseSalary());
            assertEquals(negativeSalary, userWithNegative.getBaseSalary());
        }

        @Test
        @DisplayName("Should handle future birthDate")
        void shouldHandleFutureBirthDate() {
            // Given
            LocalDate futureBirthDate = LocalDate.now().plusYears(10);

            // When
            User user = User.builder()
                    .birthDate(futureBirthDate)
                    .build();

            // Then
            assertEquals(futureBirthDate, user.getBirthDate());
        }

        @Test
        @DisplayName("Should handle past birthDate")
        void shouldHandlePastBirthDate() {
            // Given
            LocalDate pastBirthDate = LocalDate.of(1900, 1, 1);

            // When
            User user = User.builder()
                    .birthDate(pastBirthDate)
                    .build();

            // Then
            assertEquals(pastBirthDate, user.getBirthDate());
        }

        @Test
        @DisplayName("Should handle maximum roleId")
        void shouldHandleMaximumRoleId() {
            // Given
            int maxRoleId = Integer.MAX_VALUE;

            // When
            User user = User.builder()
                    .roleId(maxRoleId)
                    .build();

            // Then
            assertEquals(maxRoleId, user.getRoleId());
        }

        @Test
        @DisplayName("Should handle large baseSalary")
        void shouldHandleLargeBaseSalary() {
            // Given
            BigDecimal largeSalary = new BigDecimal("999999999999999.99");

            // When
            User user = User.builder()
                    .baseSalary(largeSalary)
                    .build();

            // Then
            assertEquals(largeSalary, user.getBaseSalary());
        }

        @Test
        @DisplayName("Should handle long strings")
        void shouldHandleLongStrings() {
            // Given
            String longString = "a".repeat(1000);

            // When
            User user = User.builder()
                    .firstName(longString)
                    .lastName(longString)
                    .address(longString)
                    .phoneNumber(longString)
                    .email(longString)
                    .identityDocument(longString)
                    .password(longString)
                    .build();

            // Then
            assertAll("Long strings handling",
                    () -> assertEquals(longString, user.getFirstName()),
                    () -> assertEquals(longString, user.getLastName()),
                    () -> assertEquals(longString, user.getAddress()),
                    () -> assertEquals(longString, user.getPhoneNumber()),
                    () -> assertEquals(longString, user.getEmail()),
                    () -> assertEquals(longString, user.getIdentityDocument()),
                    () -> assertEquals(longString, user.getPassword())
            );
        }
    }

    @Nested
    @DisplayName("Colombian Specific Data Tests")
    class ColombianDataTests {

        @Test
        @DisplayName("Should handle Colombian names correctly")
        void shouldHandleColombianNamesCorrectly() {
            // Given
            String firstName = "José María";
            String lastName = "Pérez-Hernández";

            // When
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();

            // Then
            assertEquals(firstName, user.getFirstName());
            assertEquals(lastName, user.getLastName());
        }

        @Test
        @DisplayName("Should handle Colombian phone numbers")
        void shouldHandleColombianPhoneNumbers() {
            // Given
            String mobilePhone = "+57 300 123 4567";
            String landlinePhone = "+57 5 123 4567";

            // When
            User userMobile = User.builder()
                    .phoneNumber(mobilePhone)
                    .build();

            User userLandline = User.builder()
                    .phoneNumber(landlinePhone)
                    .build();

            // Then
            assertEquals(mobilePhone, userMobile.getPhoneNumber());
            assertEquals(landlinePhone, userLandline.getPhoneNumber());
        }

        @Test
        @DisplayName("Should handle Colombian addresses")
        void shouldHandleColombianAddresses() {
            // Given
            String cartagenaAddress = "Calle 33 #3-123, Centro Histórico, Cartagena";
            String bogotaAddress = "Carrera 15 #93-47, Bogotá D.C.";

            // When
            User userCartagena = User.builder()
                    .address(cartagenaAddress)
                    .build();

            User userBogota = User.builder()
                    .address(bogotaAddress)
                    .build();

            // Then
            assertEquals(cartagenaAddress, userCartagena.getAddress());
            assertEquals(bogotaAddress, userBogota.getAddress());
        }

        @Test
        @DisplayName("Should handle Colombian salary ranges")
        void shouldHandleColombianSalaryRanges() {
            // Given - Salarios típicos en Colombia (2024)
            BigDecimal minimumWage = new BigDecimal("1160000");      // SMMLV 2024
            BigDecimal mediumSalary = new BigDecimal("2500000");     // Salario medio
            BigDecimal highSalary = new BigDecimal("15000000");      // Salario alto

            // When
            User userMinimum = User.builder()
                    .baseSalary(minimumWage)
                    .build();

            User userMedium = User.builder()
                    .baseSalary(mediumSalary)
                    .build();

            User userHigh = User.builder()
                    .baseSalary(highSalary)
                    .build();

            // Then
            assertEquals(minimumWage, userMinimum.getBaseSalary());
            assertEquals(mediumSalary, userMedium.getBaseSalary());
            assertEquals(highSalary, userHigh.getBaseSalary());
        }
    }
}