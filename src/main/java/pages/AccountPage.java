package pages;

import config.urlConstants;
import model.UserAccount;
import model.enums.AccountDataField;
import model.enums.UserType;
import model.ui.OrderEntry;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.OrderHistory;
import pages.components.PopupDialog;

/**
 * Page Object for Account management page.
 * Handles user profile updates and account information.
 */
public class AccountPage extends CommonPage {

    // ============================================
    // ---- Page Elements ----
    // ============================================
    // ---- Form container ----
    @FindBy (css = "form")
    private WebElement frmUserInfo;

    // ---- Form fields ----
    @FindBy (css = "select[name='maLoaiNguoiDung']")
    private WebElement selUserType;

    // ---- Form button ----
    @FindBy (xpath = "//button[.='Cập Nhật']")
    private WebElement btnSaveChanges;

    // ---- Components ----
    // Popup dialog for update response - success and error
    private PopupDialog dlgResponse;
    private OrderHistory orderHistory;

    // ============================================
    // Constructor
    // ============================================
    public AccountPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.dlgResponse = new PopupDialog(driver);
        this.orderHistory = new OrderHistory(driver);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Navigation ----
    public void navigateToAccountPage() {
        LOG.info("Navigate to Account Page");
        driver.get(url(urlConstants.ACCOUNT));
    }

    // ---- Wait ----
    public void waitForAccountFormDisplay() {
        waitForVisibilityOfElementLocated(frmUserInfo);
    }

    // ---- Form interactions: fill/update fields, click buttons ----
    public void changeName(String newName) {
        updateInputField(AccountDataField.FULL_NAME, newName);
    }

    public void changeEmail(String newEmail) {
        updateInputField(AccountDataField.EMAIL, newEmail);
    }

    public void changePhoneNumber(String newPhoneNumber) {
        updateInputField(AccountDataField.PHONE_NUMBER, newPhoneNumber);
    }

    public void changeUserInfoAndSave(String newName, String newEmail, String newPhoneNumber) {
        changeName(newName);
        changeEmail(newEmail);
        changePhoneNumber(newPhoneNumber);
        clickSaveBtn();
    }

    public void changePasswordAndSave(String newPassword) {
        updateInputField(AccountDataField.PASSWORD, newPassword);
        clickSaveBtn();
    }

    public void attemptToChangeUsername(String newUsername) {
        updateInputField(AccountDataField.USERNAME, newUsername);
    }

    public void attemptToChangeUserTypeToAdmin() {
        WebElement selUserType = getInputField(AccountDataField.USER_TYPE);
        selectDropdownOptionByValue(selUserType, UserType.ADMIN.getLabel());
    }

    public void clickSaveBtn() {
        click(btnSaveChanges);
    }

    public void closeSuccessDialog() {
        dlgResponse.clickConfirmButton();
        dlgResponse.waitForDialogToBeInvisible();
    }

    // ---- Getters for Account Form ----
    public boolean isAccountFormDisplayed() {
        return isElementDisplayed(frmUserInfo);
    }

    //  Get field values
    public String getUsername() {
        return getFieldValue(getInputField(AccountDataField.USERNAME));
    }

    public String getFullName() {
        return getFieldValue(getInputField(AccountDataField.FULL_NAME));
    }

    public String getEmail() {
        return getFieldValue(getInputField(AccountDataField.EMAIL));
    }

    public String getPhoneNumber() {
        return getFieldValue(getInputField(AccountDataField.PHONE_NUMBER));
    }

    public String getPassword() {
        return getFieldValue(getInputField(AccountDataField.PASSWORD));
    }

    public String getUserType() {
        return getFieldValue(getInputField(AccountDataField.USER_TYPE));
    }

    /**
     * Get all account data as UserAccount object.
     * Useful for verification against expected data.
     *
     * @return UserAccount object populated with current UI values
     */
    public UserAccount getAccountData() {
        boolean isFormDisplayed = isAccountFormDisplayed();

        if  (!isFormDisplayed) {
            throw new NoSuchElementException("Account Form is not displayed");
        }

        return UserAccount.builder()
                .taiKhoan(getUsername())
                .hoTen(getFullName())
                .email(getEmail())
                .soDt(getPhoneNumber())
                .matKhau(getPassword())
                .maLoaiNguoiDung(getUserType())
                .build();
    }

    // Get dialog state and text
    public boolean isUpdateResponseDialogDisplayed() {
        return dlgResponse.isDialogDisplayed();
    }

    public String getUpdateResponseMsgText() {
        return dlgResponse.getDialogTitle();
    }

    // Get validation error state and text
    public boolean isFieldValidationMsgDisplayed(AccountDataField field) {
        return isElementDisplayed(getByLblFieldValidationMsg(field));
    }

    public String getValidationMsgText(AccountDataField field) {
        WebElement lblValidationMsg = waitForVisibilityOfElementLocatedBy(getByLblFieldValidationMsg(field));
        return getText(lblValidationMsg);
    }

    // ---- Getters for Order History ----
    public boolean isOrderHistorySectionDisplayed() {
        return orderHistory.isOrderHistoryDisplayed();
    }

    public Integer getTotalOrderCount() {
        return orderHistory.getOrderCount();
    }

    public OrderEntry getLastOrderEntryDetails() {
        return orderHistory.getLastOrderEntryDetails();
    }

    // Private helpers
    private WebElement getInputField(AccountDataField field) {
        if (field.equals(AccountDataField.USER_TYPE)) {
            return selUserType;
        }

        String fieldId = field.getFieldId();
        return waitForVisibilityOfElementLocatedBy(By.id(fieldId));
    }

    private By getByLblFieldValidationMsg(AccountDataField field) {
       switch (field) {
           case FULL_NAME:
           case EMAIL:
           case PHONE_NUMBER:
           case PASSWORD:
               String fieldId = field.getFieldId();
               String lblValidationId = fieldId + "-helper-text";
               return By.id(lblValidationId);
           case USER_TYPE:
           case USERNAME:
               throw new IllegalArgumentException(field + "is read only. No field validation message exists.");
           default:
               throw new IllegalArgumentException("Invalid field" + field);
       }
    }

    private void updateInputField(AccountDataField field, String newValue) {
        LOG.info("Clear field: " + field + " and enter new value: " + newValue);
        WebElement inputField = getInputField(field);
        clear(inputField);
        enterText(inputField, newValue);
    }

}