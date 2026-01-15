package pages;

import config.Routes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends CommonPage {

    @FindBy (id = "taiKhoan")
    private WebElement txtAccountLogin;

    @FindBy (id = "matKhau")
    private WebElement txtPasswordLogin;

    @FindBy (css = "button[type='submit']")
    private WebElement btnLogin;

    @FindBy (css = "div[role='dialog']")
    private WebElement alertLoginSuccess;

    @FindBy (xpath = "//div[@role='dialog']//h2")
    private WebElement lblLoginSuccessMsg;

    @FindBy (id = "matKhau-helper-text")
    private WebElement lblInvalidPasswordMsg;

    @FindBy (css = "div[role='alert']")
    private WebElement alertLoginError;


    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void navigateToLoginPage() {
        LOG.info("Navigate to Login Page");
        driver.get(url(Routes.LOGIN));
    }

    public void enterAccount(String account) {
        sendKeys(txtAccountLogin, account);
    }

    public void enterPassword(String password) {
        sendKeys(txtPasswordLogin, password);
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

    public boolean isLoginSuccessMessageDisplayed() {
        return isElementDisplayed(alertLoginSuccess, 2);
    }

    public boolean isInvalidPasswordMessageDisplayed() {
        return isElementDisplayed(lblInvalidPasswordMsg, 2);
    }

    public boolean isLoginErrorMessageDisplayed() {
        return isElementDisplayed(alertLoginError, 2);
    }

    public String getLoginSuccessMsgText() {
        return getText(lblLoginSuccessMsg);
    }

    public String getPasswordErrorMsgText() {
        return getText(lblInvalidPasswordMsg);
    }

    public String getLoginErrorMsgText() {
        return getText(alertLoginError);
    }

}

