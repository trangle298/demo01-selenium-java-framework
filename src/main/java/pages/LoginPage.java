package pages;

import config.Routes;
import model.ui.LoginInputs;
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
        driver.get(url(Routes.LOGIN));
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

    public void fillLoginFormThenSubmit(LoginInputs loginInputs) {
        fillLoginFormThenSubmit(loginInputs.getTaiKhoan(), loginInputs.getMatKhau());
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
    public boolean isInvalidPasswordMsgDisplayed() {
        return isElementDisplayedShort(lblInvalidPasswordMsg);
    }

    public String getPasswordValidationText() {
        return getText(lblInvalidPasswordMsg);
    }

    // Get login error alert state and text
    public boolean isLoginErrorAlertDisplayed() {
        return isElementDisplayedShort(alertLoginError);
    }
    
    public String getLoginErrorMsgText() {
        return getText(alertLoginError);
    }
}