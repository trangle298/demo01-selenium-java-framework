package pages;

import config.urlConstants;
import model.enums.RegisterField;
import model.ui.RegisterDataUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.PopupDialog;

/**
 * Page Object for Registration page.
 * Handles registration form interactions and field validation.
 */
public class RegisterPage extends CommonPage {

    // ============================================
    // ---- Page Elements ----
    // ============================================
    // ---- Form button ----
    @FindBy(css = "button[type='submit']")
    private WebElement btnRegister;

    // ---- Form Error alert ----
    @FindBy(css = "div[role='alert']")
    private WebElement alertRegisterError;

    // ---- Components ----
    private PopupDialog dlgSuccess;

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
        driver.get(url(urlConstants.REGISTER));
    }
    
    // ---- Form interactions: fill fields, click buttons ----
    public void enterFieldInput(RegisterField fieldType, String fieldInput) {
        WebElement txtInputField = getInputField(fieldType);
        enterText(txtInputField, fieldInput);
    }

    // Enter text and blur field to trigger field validation
    public void enterFieldInputAndBlur(RegisterField fieldType, String fieldInput) {
        LOG.info("Fill " + fieldType + " field with input: " + fieldInput + " and blur");
        WebElement txtInputField = getInputField(fieldType);
        enterText(txtInputField, fieldInput);
        blurField(txtInputField);
    }

    public void updateFieldInputAndBlur(RegisterField fieldType, String newInput) {
        WebElement txtInputField = getInputField(fieldType);
        clear(txtInputField);
        enterText(txtInputField, newInput);
        blurField(txtInputField);
    }

    public void clickRegister() {
        LOG.info("Click Submit button");
        click(btnRegister);
    }

    public void fillRegisterFormThenSubmit(String username, String password, String confirmPassword, String fullName, String email) {
        LOG.info("Fill Register form and submit");
        enterFieldInput(RegisterField.USERNAME, username);
        enterFieldInput(RegisterField.PASSWORD, password);
        enterFieldInput(RegisterField.CONFIRM_PASSWORD, confirmPassword);
        enterFieldInput(RegisterField.FULL_NAME, fullName);
        enterFieldInput(RegisterField.EMAIL, email);
        clickRegister();
    }

    public void fillRegisterFormThenSubmit(RegisterDataUI inputValues) {
        fillRegisterFormThenSubmit(
                inputValues.getUsername(),
                inputValues.getPassword(),
                inputValues.getConfirmPassword(),
                inputValues.getFullName(),
                inputValues.getEmail()
        );
    }

    // ---- Getters ----
    // Get success dialog state and text
    public boolean isRegisterSuccessDialogDisplayed() {
        return dlgSuccess.isDialogDisplayed();
    }

    public String getRegisterSuccessMsgText() {
        return dlgSuccess.getDialogTitle();
    }

    // Get register error alert state and text
    public boolean isRegisterErrorAlertDisplayed() {
        return isElementDisplayed(alertRegisterError);
    }

    public String getRegisterErrorMsgText() {
        return getText(alertRegisterError);
    }

    // Get validation message visibility state and text
    public boolean isFieldValidationMsgDisplayed(RegisterField field) {
        By byLblFieldValidation = getFieldValidationMsgLocator(field);
        return isElementDisplayed(byLblFieldValidation);
    }

    public String getFieldValidationText(RegisterField fieldType) {
        By byLblFieldValidation = getFieldValidationMsgLocator(fieldType);
        WebElement lblFieldError = waitForVisibilityOfElementLocatedBy(byLblFieldValidation);
        return getText(lblFieldError);
    }

    public boolean isFieldValidationMsgNotDisplayed(RegisterField fieldType) {
        By byLblFieldValidation = getFieldValidationMsgLocator(fieldType);
        return isElementNotDisplayed(byLblFieldValidation);
    }

    // ============================================
    // Private Helper Methods
    // ============================================
    private WebElement getInputField(RegisterField field) {
        String fieldId = field.getFieldId();
        if (fieldId == null) {
            LOG.warn("Unknown field name: " + field);
            return null;
        }
        return waitForVisibilityOfElementLocatedBy(By.id(fieldId));
    }

    /**
     * Dynamically get error element for any field following the pattern: id="{fieldId}-helper-text"
     *
     * @param field Field name in English (e.g., "username", "password", "email")
     * @return WebElement for the error label, or null if field not found
     */
    private By getFieldValidationMsgLocator(RegisterField field) {
        String fieldId = field.getFieldId();
        if (fieldId == null) {
            LOG.warn("Unknown field name: " + field);
            return null;
        }
        // Dynamically construct locator: id = "{fieldId}-helper-text"
        String id = fieldId + "-helper-text";
        return By.id(id);
    }
}