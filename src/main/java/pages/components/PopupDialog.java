package pages.components;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PopupDialog extends BasePage {

    // ============================================
    // ---- Component Elements ----
    // ============================================
    @FindBy(css = "div[role='dialog']")
    private WebElement dlgContainer;

    @FindBy(css = "div[role='dialog'] h2")
    private WebElement lblDialogTitle;

    @FindBy(css = "button[class*='confirm']")
    private WebElement btnConfirm;

    @FindBy(css = "button[class*='cancel']")
    private WebElement btnCancel;

    @FindBy(css = "button[class*='deny']")
    private WebElement btnDeny;

    // ============================================
    // ---- Constructor ----
    // ============================================
    public PopupDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Waits ----
    public void waitForDialogToBeVisible() {
        waitForVisibilityOfElementLocated(dlgContainer);
    }

    public void waitForDialogToBeInvisible() {
        waitForInvisibilityOfElementLocated(dlgContainer);
    }

    // ---- Dialog interactions ----
    public void clickConfirmButton() {
        waitForVisibilityOfElementLocated(dlgContainer);
        click(btnConfirm);
    }

    public void clickCancelButton() {
        waitForVisibilityOfElementLocated(dlgContainer);
        click(btnCancel);
    }

    public void clickDenyButton() {
        waitForVisibilityOfElementLocated(dlgContainer);
        click(btnDeny);
    }

    // ---- Getters ----
    public boolean isDialogDisplayed() {
        return isElementDisplayed(dlgContainer);
    }

    public String getDialogTitle() {
        return getText(lblDialogTitle);
    }
}