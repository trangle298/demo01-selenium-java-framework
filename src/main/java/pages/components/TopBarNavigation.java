package pages.components;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page component for top navigation bar.
 * Handles login, register, logout, and user profile links.
 */
public class TopBarNavigation extends BasePage {

    // ============================================
    // ---- Component Elements ----
    // ============================================

    // ---- Navigation Links ----
    @FindBy (css = "header a[href='/sign-in']")
    private WebElement lnkLogin;
    @FindBy (css = "header a[href='/sign-up']")
    private WebElement lnkRegister;
    @FindBy (css = "header a[href='/account']")
    private WebElement lnkUserProfile;
    @FindBy (xpath = "//a[h3='Đăng xuất']")
    private WebElement lnkLogout;

    // ---- Logout Confirmation Alert & Buttons ----
    @FindBy (xpath = ".//h2[text()='Bạn có muốn đăng xuất ?']")
    private WebElement alertLogoutConfirmation;
    @FindBy (xpath = "//div[.//h2[text()='Bạn có muốn đăng xuất ?']]//button[text()='Đồng ý']")
    private WebElement btnConfirmLogout;
    @FindBy (xpath = "//div[.//h2[text()='Bạn có muốn đăng xuất ?']]//button[text()='Hủy']")
    private WebElement btnCancelLogout;
    @FindBy (xpath = "//h2[text()='Đã đăng xuất']")
    private WebElement alertLogoutSuccess;

    // ============================================
    // ---- Constructor ----
    // ============================================
    public TopBarNavigation(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    public void clickLogoutLink() {
       click(lnkLogout);
    }

    public void clickConfirmLogoutButton() {
       click(btnConfirmLogout);
    }

    public void clickCancelLogoutButton() {
       click(btnCancelLogout);
    }

    public void logout() {
         clickLogoutLink();
         waitForVisibilityOfElementLocated(alertLogoutConfirmation);
         clickConfirmLogoutButton();
    }

    public void cancelLogout() {
        waitForVisibilityOfElementLocated(alertLogoutConfirmation);
        clickCancelLogoutButton();
    }

    public void waitForUserProfileLink() {
        waitForVisibilityOfElementLocated(lnkUserProfile);
    }
    public String getUserProfileName() {
        return getText(lnkUserProfile);
    }

    public boolean isUserProfileVisible() {
        return isElementDisplayed(lnkUserProfile);
    }

    public boolean isLoginLinkVisible() {
        return isElementDisplayed(lnkLogin);
    }

    public boolean isLogoutSuccessAlertVisible() {
        return isElementDisplayedShort(alertLogoutSuccess);
    }

}
