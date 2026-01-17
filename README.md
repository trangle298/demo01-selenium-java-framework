# Introduction

A robust, scalable, and feature-rich Selenium test automation framework built with Java, TestNG, and Gradle. This framework follows industry best practices including the Page Object Model (POM), centralized configuration management, and comprehensive reporting capabilities.

## Key Features
- **Page Object Model (POM)** - Maintainable and reusable page objects with component-based architecture
- **Multi-Browser Support** - Chrome, Firefox, Edge, and Safari
- **Parallel Execution** - Run tests in parallel for faster execution with thread-safe driver management
- **Data-Driven Testing** - Support for test data providers and external JSON files
- **Comprehensive Reporting** - Detailed and rich HTML reports with Extent Reports, automatic screenshots on test failures
- **Logging** - Detailed logging with Log4j2
- **Flexible Configuration** - Properties-based configuration with override support (System Properties > Environment Variables > config.properties)
- **Internationalization (i18n)** - Support for Vietnamese and other languages via properties files
- **Thread-Safe** - ThreadLocal WebDriver for parallel test execution
- **API Integration** - API clients for test data discovery and verification
- **Test Data Generation** - Dynamic test data generation using DataFaker library


## Tech Stack
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 11+ | Programming Language |
| **Selenium WebDriver** | 4.35.0 | Browser Automation |
| **TestNG** | 7.11.0 | Test Framework |
| **Gradle** | 8.x | Build Tool |
| **Log4j2** | 2.25.1 | Logging |
| **ExtentReports** | 5.1.2 | Test Reporting |
| **Apache Commons IO** | 2.20.0 | File Utilities |
| **Jackson** | 2.17.0 | JSON Processing |
| **DataFaker** | 2.3.1 | Test Data Generation |
| **Lombok** | 1.18.42 | Code Generation |


## Project Structure
```
src/
├── main/java/
│   ├── api/                        # API clients and data extractors
│   │   ├── cinema/                 # Cinema API integration
│   │   ├── movie/                  # Movie API integration
│   │   └── showtime/               # Showtime API integration
│   ├── base/                       # Base classes
│   │   ├── BasePage.java           # Common page actions
│   │   └── BaseTest.java           # Base test setup
│   ├── config/                     # Configuration and constants (URLs, routes)
│   ├── drivers/                    # WebDriver management (Factory pattern)
│   │   ├── DriverManagerFactory.java
│   │   └── ...
│   ├── listeners/                  # TestNG listeners
│   ├── model/                      # Data models, DTOs, and enums
│   ├── pages/                      # Page Objects (POM)
│   │   ├── components/             # Reusable UI components
│   │   ├── HomePage.java           # Page class
│   │   └── ...
│   ├── reports/                    # Reporting utilities
│   └── utils/                      # Utility classes
│
└── test/
    ├── java/
    │   ├── helpers/                # Test helpers and data providers
    │   └── testcases/              # Test classes
    │       ├── auth/               # Authentication tests
    │       ├── ...                 # Other test classes grouped by feature
    │       └── e2e/                # End-to-end tests
    └── resources/                  # Test resources (config, test data, TestNG suites)

test-output/                        # Generated reports and screenshots (auto-generated)

logs/                               # Application logs (date-stamped)
build.gradle                        # Gradle build configuration
gradlew                             # Gradle wrapper (Unix/Linux/macOS)
```
## Test Data Strategy
Due to a lack of backend access and data seeding capabilities, tests dynamically discover eligible test data via public APIs. This ensures correctness in a shared, mutable environment but increases execution time.

In a controlled test environment, these tests would instead:
- Create required data via API
- Use seeded fixtures
- Reset state between tests
- Remove dynamic discovery logic

## Prerequisites
Before running the tests, ensure you have the following installed:

- **Java Development Kit (JDK)** 11 or higher
- **Gradle** 8.x or use the provided Gradle wrapper
- **Git** (for cloning the repository)
- **Supported Browsers:**
  - Google Chrome (latest)
  - Mozilla Firefox (latest)
  - Microsoft Edge (latest)
  - Safari (macOS only)

## Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/trangle298/demo01-selenium-java-framework.git
   cd demo01-selenium-java-framework
   ```

2. **Install dependencies:**
   ```bash
   gradlew build
   ```
   Or without wrapper:
   ```bash
   gradle build
   ```

3. **Verify installation:**
   ```bash
   gradlew clean test --tests testcases.auth.RegisterTest
   ```
   
## Configuration
### 1. Configuration Files

#### `src/test/resources/config.properties`
Main configuration file for application settings:

```properties
# Browser Configuration
browser=chrome

# Page Load Strategy
# Set to true for faster page loads (doesn't wait for all resources like images/css)
# Set to false for complete page load (waits for everything)
eagerPageLoadStrategy=true

# Application URLs
base.url=https://demo1.cybersoft.edu.vn

# Timeouts (in seconds)
# Default explicit wait - used by WebDriverWait in BasePage
explicit.wait=10

# Short timeout for quick checks (error messages, alerts that appear immediately)
short.wait=3

# Long timeout for slow operations (API calls, page redirects, complex interactions)
long.wait=20
```

**Supported browsers:** `chrome`, `firefox`, `edge`, `safari`

**Timeout Usage:**
- `explicit.wait` - Default for most element interactions
- `short.wait` - Use `isElementDisplayedShort()` for quick checks
- `long.wait` - Use `isElementDisplayedLong()` for slow operations
**Supported browsers:** chrome, firefox, edge, safari
   ```bash
   # Run with Firefox instead of Chrome
   gradlew test -Dbrowser=firefox
   
   # Multiple properties
   gradlew test -Dbrowser=edge -Dbase.url=https://staging.example.com
   ```

2. **Environment Variables**
   ```bash
   set BASE_URL=https://staging.example.com
   ```

3. **config.properties file**
   ```properties
   browser=chrome
   base.url=https://demo1.cybersoft.edu.vn
   ```

4. **Default values** (lowest priority)

### 3. Test Data Configuration

- **test-users.json** - Predefined test user accounts
- **messages_vi.properties** - Vietnamese language strings for validation

## Add New Tests
### Page Object Model (POM)

This project uses the Page Object Model (POM) pattern to keep test code maintainable and reusable. The main concepts are:

- **Pages**: Represent full application pages (e.g., `LoginPage`, `HomePage`, `AccountPage`). Each page class contains selectors and methods for interacting with that page.
- **Components**: Represent reusable UI parts that can appear on multiple pages (e.g., form fields, navigation bars). Components are usually placed in the `pages/components/` folder and can be used by page classes.
- **BasePage**: Contain common methods and utilities shared by all page classes (e.g., navigation, waiting for elements).

All pages extend `BasePage` and use `@FindBy` annotations:

```java
public class LoginPage extends BasePage {
    @FindBy(id = "username")
    private WebElement usernameField;
    
    public void enterUsername(String username) {
        sendKeys(usernameField, username);
    }
}
```

### Test Grouping

Tests are organized using TestNG groups for flexible execution. Available groups:

**By Test Type:**
- `component` - Single component isolated tests (e.g., login form validation, individual field checks)
- `integration` - Multi-component integration tests (e.g., login → account page navigation)
- `e2e` - Full user journey end-to-end tests (e.g., register → login → update account → logout)

**By Feature Area:**
**By Type:** `component`, `integration`, `e2e`
**By Feature:** `auth`, `register`, `booking`, `browsing`, `account`
**By Priority:** `smoke`, `critical`, `regression`, `negative`
}

@Test(groups = {"integration", "auth", "account", "smoke"})
public void testLoginThenNavigateToAccount() {
public void testRegisterWithValidData() {
    // Test implementation
gradlew test -Dgroups=smoke


Key features of `BaseTest`:
- **Before/After hooks** - Suite, Test, Method levels
- **Screenshot capture** - Automatic screenshots on test failures

2. **Use Helper Classes for Verification**

- **Automatic reporting** - ExtentReport integration
- **Screenshot capture** - On test failures

2. **Follow AAA Pattern:**
       // Act
       loginPage.login("user@example.com", "password");
       
       // Assert
       assertTrue(loginPage.isLoginSuccessful());
   }
   ```

4. **Use Descriptive Test Names:**
   ```java
   @Test
   public void testRegisterWithValidData_ShouldCreateNewAccount();
   
   @Test
3. **Use Descriptive Test Names:**
   ```

5. **Add Test Groups:**
   
   @Test(groups = {"component", "auth", "negative"})
4. **Add Test Groups:**
   ```

       return new Object[][] {
           {"user1@test.com", "password1"},
5. **Use Data Providers for Data-Driven Tests:**
       };
7. **Use Soft Assertions for E2E Tests:**
   public void testLoginWithDifferentUsers(String email, String password)
```java
gradlew test
```

### Run Specific Test Suite

```bash
# E2E tests only
gradlew test -DsuiteXmlFile=testng-e2e.xml

# Or use the main suite
gradlew test -DsuiteXmlFile=testng.xml
```

### Run Tests by Group

```bash
# Smoke tests
gradlew test -Dgroups=smoke

# Multiple groups
gradlew test -Dgroups=smoke,auth
```

### Run Specific Test Class

```bash
# Run a specific test class
gradlew test --tests testcases.auth.RegisterTest

# Run a specific test method
gradlew test --tests testcases.auth.RegisterTest.testValidRegister
```

### Run Tests by Package

```bash
# All authentication tests
gradlew test --tests "testcases.auth.*"

# All E2E tests
gradlew test --tests "testcases.e2e.*"
```

### Run Tests with Different Browser

```bash
# Using system property
gradlew test -Dbrowser=firefox

# Or modify config.properties
browser=chrome
```

### Parallel Execution

The framework supports parallel test execution configured in `testng.xml`:

```xml
<suite name="All Test Suite" parallel="classes" thread-count="4">
```

Adjust `thread-count` based on your system resources.

## Reporting
The framework uses **ExtentReports** as the primary reporting solution. TestNG executes the tests and triggers ExtentReports through the `TestListener` class, creating comprehensive HTML reports with rich visualizations.

### How It Works

```
TestNG (Test Execution) → TestListener (Hooks) → ExtentReportManager → ExtentReport.html
```

### ExtentReports - Main Report

After test execution, view the comprehensive HTML dashboard:

- **Location:** `test-output/ExtentReport.html`
- **Features:**
    - Test execution summary with pass/fail statistics
    - Individual test details with steps
    - Screenshots embedded for failures
    - Execution timeline and duration
    - System and environment information
    - Categorization by test groups
    - Real-time test status

**Open report:**
```bash
# Windows
start test-output\ExtentReport.html

# macOS/Linux
open test-output/ExtentReport.html
```

### TestNG Configuration

TestNG default HTML reporters are **disabled** in this framework to keep the `test-output/` folder clean:

```groovy
useDefaultListeners = false  // Only ExtentReports is generated
```

**Why disabled?**
- ExtentReports provides superior reporting capabilities
- Prevents duplicate/unnecessary HTML files
- Keeps test-output folder clean and focused
- Faster test execution (less I/O overhead)

**Note:** If you need TestNG's default reports, set `useDefaultListeners = true` in `build.gradle`


---