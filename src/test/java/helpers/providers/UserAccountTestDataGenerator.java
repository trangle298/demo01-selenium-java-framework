package helpers.providers;

import model.api.request.RegisterRequestPayload;
import model.UserAccount;
import model.ui.RegisterDataUI;
import net.datafaker.Faker;

import java.util.UUID;

/**
 * Generates random test data for authentication and account forms using Faker library.
 */
public class UserAccountTestDataGenerator {

    private static final Faker faker = new Faker();
    private static final Integer passwordMinLength = 6;
    private static final Integer passwordMaxLength = 50;
    private static final String EMAIL_DOMAIN = "@example.com";

    // ---- Generate valid register/account data ----
    public static String generateUniqueUsername() {
        return UUID.randomUUID().toString();
    }

    public static String generateNewUniqueEmail() {
        String defaultDomain = "@example.com";
        return UUID.randomUUID() + defaultDomain;
    }

    public static RegisterDataUI generateValidRegisterFormInputs() {
        UserAccount newUser = generateNewUserAccountInfo();
        RegisterDataUI inputs = new RegisterDataUI(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getPassword(),  // confirm password matches password
                newUser.getFullName(),
                newUser.getEmail()
        );
        return inputs;
    }

    public static RegisterRequestPayload generateRegisterRequestPayload() {
        UserAccount newUser = generateNewUserAccountInfo();
        return RegisterRequestPayload.builder()
                .taiKhoan(newUser.getUsername())
                .matKhau(newUser.getPassword())
                .hoTen(newUser.getFullName())
                .email(newUser.getEmail())
                .soDt(newUser.getPhoneNumber())
                .build();
    }

    // ---- Generate modified valid data based on current values ----
    public static String generateNewName(String currentName) {
        return currentName + faker.name().firstName();
    }

    public static String generateNewPhoneNumber(String currentPhoneNumber) {
        String newPhoneNumber;
        if (currentPhoneNumber.isEmpty())
            // Current system only accepts didits
            newPhoneNumber = faker.phoneNumber().phoneNumber().replaceAll("[^0-9]", "");
        else
            newPhoneNumber = currentPhoneNumber + faker.number().digits(2);
        return newPhoneNumber;
    }

    public static String generateNewPassword(String currentPassword) {
        if (currentPassword.length() == passwordMaxLength) {
            return currentPassword.substring(0, passwordMaxLength - 2);
        }
        return currentPassword + faker.number().digits(1);
    }

    // ---- Generate invalid data ----
    public static String generateShortPassword() {
        return faker.internet().password(1, passwordMinLength - 1);
    }

    public static String generatePasswordCustomLength(Integer length) {
        return  faker.internet().password(length, length);
    }

    public static String generateInvalidNameContainingNumbers() {
        return faker.name().firstName() + faker.number().digits(3);
    }

    // --- Private helper methods ----
    private static UserAccount generateNewUserAccountInfo() {
        String taiKhoan = generateUniqueUsername();
        String hoTen = faker.name().fullName();
        String matKhau = faker.internet().password(passwordMinLength, passwordMaxLength);
        String phoneNr = generateNewPhoneNumber("");

        return UserAccount.builder()
                .taiKhoan(taiKhoan)
                .hoTen(hoTen)
                .email(taiKhoan + EMAIL_DOMAIN)
                .matKhau(matKhau)
                .soDt(phoneNr)
                .build();
    }
}
