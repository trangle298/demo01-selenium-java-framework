package pages;

import config.urlConstants;
import model.enums.LoginField;
import model.ui.LoginDataUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.PopupDialog;

/**
 * Page Object for Login page.
 * Handles login form interactions and validation.
 */
public class LoginPage extends CommonPage {

    // ============================================
    // ---- Page Elements ----
    // ============================================
    
    // ---- Form fields ----
    @FindBy (id = "taiKhoan")
    private WebElement txtAccountLogin;
    @FindBy (id = "matKhau")
    private WebElement txtPasswordLogin;
    
    // ---- Form button ----
    @FindBy (css = "button[type='submit']")
    private WebElement btnLogin;
    
    // ---- Field validation message ----
    @FindBy (id = "matKhau-helper-text")
    private WebElement lblInvalidPasswordMsg;
    
    // ---- Form alerts ----
    @FindBy (css = "div[role='alert']")
    private WebElement alertLoginError;

    // ---- Components ----
    private PopupDialog dlgSuccess;

    // ============================================
    // ---- Constructor ----
    // ============================================
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.dlgSuccess = new PopupDialog(driver);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================
    
    // ---- Navigation ----
    public void navigateToLoginPage() {
        LOG.info("Navigate to Login Page");
        driver.get(url(urlConstants.LOGIN));
    }

    // ---- Form interactions: fill fields, click button ----
    public void enterAccount(String account) {
        enterText(txtAccountLogin, account);
    }

    public void enterPassword(String password) {
        enterText(txtPasswordLogin, password);
    }

    public void clickLoginButton() {
        click(btnLogin);
    }

    public void fillLoginFormThenSubmit(String account, String password) {
        LOG.info("Fill login form and submit");
        enterAccount(account);
        enterPassword(password);
        clickLoginButton();
    }

    // ---- Getters ----
    // Get success dialog state and text
    public boolean isLoginSuccessDialogDisplayed() {
        return dlgSuccess.isDialogDisplayed();
    }

    public String getLoginSuccessMsgText() {
        return dlgSuccess.getDialogTitle();
    }

    // Get validation error state and text
    public boolean isValidationMessageDisplayed(LoginField fieldName) {
        WebElement lblValidationMsg = getValidationMsgElement(fieldName);
        return isElementDisplayedShort(lblValidationMsg);
    }

    public String getFieldValidationText(LoginField fieldName) {
        WebElement lblValidationMsg = getValidationMsgElement(fieldName);
        return getText(lblValidationMsg);
    }

    private WebElement getValidationMsgElement(LoginField field) {
        String fieldId = field.getFieldId();
        switch (field) {
            case LoginField.USERNAME:
            case LoginField.PASSWORD:
                String id = fieldId + "-helper-text";
                return waitForVisibilityOfElementLocatedBy(By.id(id));
            default:
                throw new RuntimeException("Invalid Login field: " + field);
        }
    }

    // Get login error alert state and text
    public boolean isLoginErrorAlertDisplayed() {
        return isElementDisplayedShort(alertLoginError);
    }
    
    public String getLoginErrorMsgText() {
        return getText(alertLoginError);
    }
}