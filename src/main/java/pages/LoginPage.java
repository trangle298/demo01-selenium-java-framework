package pages;

import config.Routes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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
    @FindBy (css = "div[role='dialog']")
    private WebElement alertLoginSuccess;
    @FindBy (xpath = "//div[@role='dialog']//h2")
    private WebElement lblLoginSuccessMsg;
    @FindBy (css = "div[role='alert']")
    private WebElement alertLoginError;

    // ============================================
    // ---- Constructor ----
    // ============================================
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
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

    public void fillLoginFormAndSubmit(String account, String password) {
        LOG.info("Fill login form and submit");
        enterAccount(account);
        enterPassword(password);
        clickLoginButton();
    }
    
    // ---- Messages and alerts ----
    public boolean isInvalidPasswordMsgDisplayed() {
        return isElementDisplayedShort(lblInvalidPasswordMsg);
    }

    public String getPasswordValidationText() {
        return getText(lblInvalidPasswordMsg);
    }

    public boolean isLoginSuccessAlertDisplayed() {
        return isElementDisplayed(alertLoginSuccess);
    }

    public String getLoginSuccessMsgText() {
        return getText(lblLoginSuccessMsg);
    }

    public boolean isLoginErrorAlertDisplayed() {
        return isElementDisplayedShort(alertLoginError);
    }
    
    public String getLoginErrorMsgText() {
        return getText(alertLoginError);
    }

}

