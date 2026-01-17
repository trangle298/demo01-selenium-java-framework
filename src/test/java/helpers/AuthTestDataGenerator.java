package helpers;

import model.RegisterRequest;
import net.datafaker.Faker;

import java.util.UUID;

/**
 * Generates random test data for authentication and account forms using Faker library.
 */
public class AuthTestDataGenerator {

    private static final Faker faker = new Faker();

    // ---- Generate valid register data ----
    public static RegisterRequest generateValidRegisterData() {

        String taiKhoan = UUID.randomUUID().toString();
        String matKhau = faker.internet().password();

        return new RegisterRequest(
                taiKhoan,
                matKhau,
                matKhau, // confirmPassWord
                faker.name().fullName(),
                taiKhoan + "@example.com"
        );
    }

    // ---- Generate invalid auth/account data ----
    public static String generateInvalidShortPassword() {
        return faker.internet().password(1, 5);
    }

    public static String generateInvalidNameContainingNumbers() {
        return faker.name().firstName() + faker.number().digits(3);
    }

    // ---- Generate modified valid data based on current values ----
    public static String generateNewName(String currentName) {
        return currentName + faker.name().firstName();
    }

    public static String generateNewEmail(String currentEmail) {
        String[] parts = currentEmail.split("@");
        String newEmail = parts[0] + faker.number().digits(3) + "@" + parts[1];
        return newEmail;
    }

    public static String generateNewPhoneNumber(String currentPhoneNumber) {
        if (currentPhoneNumber.isEmpty())
            return faker.phoneNumber().cellPhone();
        else return currentPhoneNumber + faker.number().digits(2);
    }

    public static String generateNewPassword(String currentPassword) {
        return currentPassword + faker.number().digits(2);
    }
}
