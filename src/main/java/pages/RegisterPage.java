package pages;

import config.Routes;
import model.ui.RegisterInputs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.PopupDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Page Object for Registration page.
 * Handles registration form interactions and field validation.
 */
public class RegisterPage extends CommonPage {

    // ============================================
    // ---- Page Elements ----
    // ============================================

    // ---- Form fields ----
    @FindBy(id = "taiKhoan")
    private WebElement txtUsername;
    @FindBy(id = "matKhau")
    private WebElement txtPassword;
    @FindBy(id = "confirmPassWord")
    private WebElement txtConfirmPassword;
    @FindBy(id = "hoTen")
    private WebElement txtFullName;
    @FindBy(id = "email")
    private WebElement txtEmail;

    // ---- Form button ----
    @FindBy(css = "button[type='submit']")
    private WebElement btnRegister;

    // ---- Form Error alert ----
    @FindBy(css = "div[role='alert']")
    private WebElement alertRegisterError;

    // ---- Components ----
    private PopupDialog dlgSuccess;
    
    // ---- Static Fields & Initialization ----
    // Map for field name to field ID mapping (Vietnamese field names)
    // This is needed because the HTML uses Vietnamese IDs
    private static final Map<String, String> FIELD_ID_MAP = new HashMap<>();

    static {
        FIELD_ID_MAP.put("username", "taiKhoan");
        FIELD_ID_MAP.put("password", "matKhau");
        FIELD_ID_MAP.put("confirmpassword", "confirmPassWord");
        FIELD_ID_MAP.put("fullname", "hoTen");
        FIELD_ID_MAP.put("email", "email");
    }

    // ============================================
    // Constructor
    // ============================================
    public RegisterPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.dlgSuccess = new PopupDialog(driver);
    }
    
    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Navigation ----
    public void navigateToRegisterPage() {
        LOG.info("Navigate to Register page");
        driver.get(url(Routes.REGISTER));
    }
    
    // ---- Form interactions: fill fields, click buttons ----
    public void enterAccount(String account) {
        enterText(txtUsername, account);
    }

    public void enterPassword(String password) {
        enterText(txtPassword, password);
    }

    public void enterConfirmPassword(String password) {
        enterText(txtConfirmPassword, password);
    }

    public void enterFullName(String name) {
        enterText(txtFullName, name);
    }

    public void enterEmail(String email) {
        enterText(txtEmail, email);
    }

    public void fillRegisterForm(String username, String password, String confirmPassword, String fullName, String email) {
        LOG.info("Fill Register form");
        enterAccount(username);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);
        enterFullName(fullName);
        enterEmail(email);
    }

    public void clickRegister() {
        LOG.info("Click Submit button");
        click(btnRegister);
    }

    public void fillRegisterFormThenSubmit(String username, String password, String confirmPassword, String fullName, String email) {
        fillRegisterForm(username, password, confirmPassword, fullName, email);
        clickRegister();
    }

    public void fillRegisterFormThenSubmit(RegisterInputs inputValues) {
        fillRegisterForm(
                inputValues.getUsername(),
                inputValues.getPassword(),
                inputValues.getConfirmPassword(),
                inputValues.getFullName(),
                inputValues.getEmail()
        );
        clickRegister();
    }
    
    // ---- Getters ----
    // Get success dialog state and text
    public boolean isRegisterSuccessDialogDisplayed() {
        return dlgSuccess.isDialogDisplayed();
    }

    public String getRegisterSuccessMsgText() {
        return dlgSuccess.getDialogTitle();
    }

    // Get validation error state and text
    // fieldName (case-insensitive): "username", "password", "confirmPassword", "fullName", "email"
    public boolean isFieldValidationMsgDisplayed(String fieldName) {
        WebElement errorElement = getFieldValidationMsgElement(fieldName);
        if (errorElement == null) {
            return false;
        }
        return isElementDisplayedShort(errorElement);
    }

    public String getFieldValidationText(String fieldName) {
        WebElement errorElement = getFieldValidationMsgElement(fieldName);
        if (errorElement == null) {
            return "";
        }
        return getText(errorElement);
    }

    // Get register error alert state and text
    public boolean isRegisterErrorAlertDisplayed() {
        return isElementDisplayedShort(alertRegisterError);
    }

    public String getRegisterErrorMsgText() {
        return getText(alertRegisterError);
    }

    // ============================================
    // Private Helper Methods
    // ============================================
    /**
     * Dynamically get error element for any field following the pattern: id="{fieldId}-helper-text"
     *
     * @param fieldName Field name in English (e.g., "username", "password", "email")
     * @return WebElement for the error label, or null if field not found
     */
    private WebElement getFieldValidationMsgElement(String fieldName) {
        String fieldId = FIELD_ID_MAP.get(fieldName.toLowerCase());
        if (fieldId == null) {
            LOG.warn("Unknown field name: " + fieldName);
            return null;
        }

        // Dynamically construct locator: id = "{fieldId}-helper-text"
        String errorElementId = fieldId + "-helper-text";
        return waitForVisibilityOfElementLocatedBy(By.id(errorElementId));
    }
}