package pages;

import config.Routes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage extends CommonPage {

    // Form elements
    @FindBy (id = "taiKhoan")
    private WebElement txtUsername;
    @FindBy (id = "matKhau")
    private WebElement txtPassword;
    @FindBy (id = "confirmPassWord")
    private WebElement txtConfirmPassword;
    @FindBy (id = "hoTen")
    private WebElement txtFullName;
    @FindBy (id = "email")
    private WebElement txtEmail;
    @FindBy (css = "button[type='submit']")
    private WebElement btnRegister;

    // Form alert elements
    @FindBy (css = "div[role='dialog']")
    private WebElement alertRegisterSuccess;
    @FindBy(css = "div[role='dialog'] h2")
    private WebElement lblRegisterSuccessMessage;
    @FindBy(css = "div[role='alert']")
    private WebElement alertRegisterError;

    // Field error message elements
    @FindBy(id = "taiKhoan-helper-text")
    private WebElement lblUsernameError;
    @FindBy(id = "matKhau-helper-text")
    private WebElement lblPasswordError;
    @FindBy(id = "confirmPassWord-helper-text")
    private WebElement lblConfirmPasswordError;
    @FindBy(id = "hoTen-helper-text")
    private WebElement lblFullNameError;
    @FindBy(id = "email-helper-text")
    private WebElement lblEmailError;

    public RegisterPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Navigation
    public void navigateToRegisterPage() {
        LOG.info("Navigate to Register page");
        driver.get(url(Routes.REGISTER));
    }

    // Form interaction methods
    public void enterAccount(String account) {
        sendKeys(txtUsername, account);
    }

    public void enterPassword(String password) {
        sendKeys(txtPassword, password);
    }

    public void enterConfirmPassword(String password) {
        sendKeys(txtConfirmPassword, password);
    }

    public void enterFullName(String name) {
        sendKeys(txtFullName, name);
    }

    public void enterEmail(String email) {
        sendKeys(txtEmail, email);
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

    public void fillAndSubmitRegisterForm(String username, String password, String confirmPassword, String fullName, String email) {
        fillRegisterForm(username, password, confirmPassword, fullName, email);
        clickRegister();
    }

    // Check alert visibility and get text
    public boolean isRegisterSuccessAlertDisplayed() {
        return isElementDisplayed(alertRegisterSuccess, 2);
    }

    public String getRegisterSuccessMsgText() {
        return getText(lblRegisterSuccessMessage);
    }

    public boolean isRegisterErrorAlertDisplayed() {
        return isElementDisplayed(alertRegisterError, 2);
    }

    public String getRegisterErrorMsgText() {
        return getText(alertRegisterError);
    }

    // Generic method to check field error by field name
    public boolean isFieldValidationErrorDisplayed(String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "username" -> isElementDisplayed(lblUsernameError, 2);
            case "password" -> isElementDisplayed(lblPasswordError, 2);
            case "confirmpassword" -> isElementDisplayed(lblConfirmPasswordError, 2);
            case "fullname" -> isElementDisplayed(lblFullNameError, 2);
            case "email" -> isElementDisplayed(lblEmailError, 2);
            default -> false;
        };
    }

    public String getFieldErrorText(String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "username" -> getText(lblUsernameError);
            case "password" -> getText(lblPasswordError);
            case "confirmpassword" -> getText(lblConfirmPasswordError);
            case "fullname" -> getText(lblFullNameError);
            case "email" -> getText(lblEmailError);
            default -> "";
        };
    }

}
