package helpers;

import model.RegisterRequest;
import net.datafaker.Faker;

import java.util.UUID;

public class AuthTestDataGenerator {

    private static final Faker faker = new Faker();

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

    public static String generateInvalidShortPassword() {
        return faker.internet().password(1, 5);
    }

    public static String generateInvalidNameContainingNumbers() {
        return faker.name().firstName() + faker.number().digits(3);
    }

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
