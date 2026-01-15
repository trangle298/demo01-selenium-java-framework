package pages;

import config.Routes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AccountPage extends CommonPage {

    @FindBy(css = "form")
    private WebElement accountForm;

    @FindBy (id = "taiKhoan")
    private WebElement txtUsername;

    @FindBy (id = "matKhau")
    private WebElement txtPassword;

    @FindBy (id = "hoTen")
    private WebElement txtFullName;

    @FindBy (id = "email")
    private WebElement txtEmail;

    @FindBy (id = "soDt")
    private WebElement txtPhoneNumber;

    @FindBy (css = "select[name='maLoaiNguoiDung']")
    private WebElement dropdownUserType;

    @FindBy (xpath = "//button[.='Cập Nhật']")
    private WebElement btnSaveChanges;

    @FindBy (xpath = "//div[@role='dialog']//h2")
    private WebElement alertFormFeedback;

    @FindBy(id = "hoTen-helper-text")
    private WebElement lblFullNameError;

    @FindBy (id = "email-helper-text")
    private WebElement lblEmailError;

    @FindBy (id = "matKhau-helper-text")
    private WebElement lblPasswordError;

    public AccountPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void navigateToAccountPage() {
        LOG.info("Navigate to Account Page");
        driver.get(url(Routes.ACCOUNT));
    }

    public String getUsername() {
        return getFieldValue(txtUsername);
    }

    public String getFullName() {
        return getFieldValue(txtFullName);
    }

    public String getEmail() {
        return getFieldValue(txtEmail);
    }

    public String getPhoneNumber() {
        return getFieldValue(txtPhoneNumber);
    }

    public String getPassword() {
        return getFieldValue(txtPassword);
    }

    public void changeName(String newName) {
        clearField(txtFullName);
        sendKeys(txtFullName, newName);
    }

    public void changePassword(String newPassword) {
        clearField(txtPassword);
        sendKeys(txtPassword, newPassword);
    }

    public void changeEmail(String newEmail) {
        clearField(txtEmail);
        sendKeys(txtEmail, newEmail);
    }

    public void changePhoneNumber(String newPhoneNumber) {
        clearField(txtPhoneNumber);
        sendKeys(txtPhoneNumber, newPhoneNumber);
    }

    public void changeUsername(String newUsername) {
        clearField(txtUsername);
        sendKeys(txtUsername, newUsername);
    }

    public void saveChanges() {
        click(btnSaveChanges);
    }

    public void waitForUpdateAlert(){
        waitForVisibilityOfElementLocated(alertFormFeedback);
    }

    public void waitForUpdateAlertToDisappear() {
        waitForInvisibilityOfElementLocated(alertFormFeedback);
    }

    public String getUpdateAlertText() {
        return getText(alertFormFeedback);
    }

    public boolean isUpdateAlertDisplayed() {
        return isElementDisplayed(alertFormFeedback, 2);
    }

    public boolean isNameValidationErrorDisplayed() {
        return isElementDisplayed(lblFullNameError, 2);
    }

    public String getNameValidationErrorText() {
        return getText(lblFullNameError);
    }

    public boolean isEmailValidationErrorDisplayed() {
        return isElementDisplayed(lblEmailError, 2);
    }

    public String getEmailValidationErrorText() {
        return getText(lblEmailError);
    }

    public boolean isPasswordValidationErrorDisplayed() {
        return isElementDisplayed(lblPasswordError, 2);
    }

    public String getPasswordValidationErrorText() {
        return getText(lblPasswordError);
    }





}
