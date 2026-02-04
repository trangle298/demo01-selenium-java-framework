# Introduction

A robust, scalable, and feature-rich Selenium test automation framework built with Java, TestNG, and Gradle. This framework follows industry best practices including the Page Object Model (POM), centralized configuration management, and comprehensive reporting capabilities.

## Key Features
- **Page Object Model (POM)** - Maintainable and reusable page objects with component-based architecture and shared UI components
- **Multi-Browser Support** - Chrome, Firefox, Edge, and Safari
- **Flexible Configuration** - Properties-based configuration with override support (System Properties > Environment Variables > config.properties)
- **Internationalization (i18n)** - Support for Vietnamese and other languages via properties files
- **Parallel Execution** - Run tests in parallel for faster execution with thread-safe driver management
- **API Integration** - API clients for test data creation, discovery and verification
- **Data-Driven Testing** - Support for test data providers and external JSON files
- **Test Data Generation** - Dynamic test data generation using DataFaker library
- **Reduced Boilerplate** - Lombok annotations (@Data) eliminate boilerplate code in model classes
- **Comprehensive Reporting** - Detailed and rich HTML reports with Extent Reports, automatic screenshots on test failures
- **Logging** - Detailed logging with Log4j2

## Tech Stack
| Technology | Version | Purpose |
|------------|--------|---------|
| **Java** | 21     | Programming Language |
| **Selenium WebDriver** | 4.35.0 | Browser Automation |
| **TestNG** | 7.11.0 | Test Framework |
| **Gradle** | 8.x    | Build Tool |
| **Log4j2** | 2.25.1 | Logging |
| **ExtentReports** | 5.1.2  | Test Reporting |
| **Apache Commons IO** | 2.20.0 | File Utilities |
| **Jackson** | 2.20.0 | JSON Processing |
| **Rest Assured** | 5.5.6 | API Testing |
| **Dotenv** | 3.0.0 | Environment Variables |
| **DataFaker** | 2.3.1  | Test Data Generation |
| **Lombok** | 1.18.42 | Code Generation |


## Project Structure
```
src/
├── main/java/
│   ├── api/                        # API integration layer
│   │   ├── services/               # Service classes (UserService, AuthService, etc.)
│   │   ├── ApiClient.java          # REST client wrapper
│   │   ├── ApiConfig.java          # API base URI resolver
│   │   └── ApiConstants.java       # API endpoint constants
│   ├── base/                      
│   │   └── BasePage.java           # Base page class
│   ├── config/                     # Configuration management
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
│       ├── DateTimeNormalizer.java # Date/time parsing utilities
│       ├── I18n.java               # Internationalization support
│       └── UTF8Control.java        # UTF-8 resource bundle control
│
└── test/
    ├── java/
    │   ├── base/                   # Base test class
    │   ├── helpers/                # Test helpers
    │   │   ├── actions/            # Test action helpers
    │   │   ├── providers/          # Test data providers
    │   │   └── verifications/      # Verification helpers
    │   └── testcases/              # Test classes organized by feature
    │       ├── authentication/     # Authentication tests
    │       ├── registration/       # Registration tests
    │       ├── e2e/                # End-to-end tests
    │       └── ...                 # Other test suites
    └── resources/                  # Test resources (config, test data, TestNG suites)

test-output/                        # Generated reports and screenshots (auto-generated)

logs/                               # Application logs (date-stamped)
.env.example                        # Template for environment variables
.env.qa                             # QA environment credentials (gitignored)
build.gradle                        # Gradle build configuration
gradlew                             # Gradle wrapper (Unix/Linux/macOS)
```
## Test Data Strategy

### User Account Management
Tests automatically create fresh user accounts via API before each test method and delete them after test completion. This ensures test isolation and prevents conflicts in concurrent test execution.

- **User Creation**: `TestUserProvider.createNewTestUser()` generates unique user data and registers via API
- **User Cleanup**: `TestUserProvider.deleteUser()` removes test users using admin authentication
- **Optimization**: Tests can skip user creation by omitting the `requiresUser` test group

### Showtime and Booking Data
Due to project scope limitations, showtime and booking data is currently fetched and filtered from existing API responses rather than being created per test. This approach is subject to change in future iterations to fully utilize API-based test data creation.

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
   gradlew clean test --tests testcases.registration.TC01_RegisterWithValidInputsTest
   ```
   
## Configuration

### 1. Configuration Files

#### `src/test/resources/config.properties`
Main configuration file for test settings:

```properties
# Environment host mapping - UI
env.qa.host=demo1
#env.prod.host=prod1 (example)
#env.staging.host=staging1 (example)

# Environment host mapping - API
api.env.qa.host=movie0706
# api.env.staging.host=movienew (example)

# Page Load Strategy
# Set to true for faster page loads (doesn't wait for all resources like images/css)
# Set to false for complete page load (waits for everything)
eagerPageLoadStrategy=true

# Timeouts (in seconds)
# Default explicit wait timeout - used by WebDriverWait in BasePage
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

#### `.env.qa` (Environment Credentials)
Stores test account credentials (gitignored for security). See `.env.example` for template structure.

### 2. Environment-Based URL Configuration

The framework uses environment-based URL construction. The environment is determined by the `-Denv` system property (defaults to `qa`).

**Example:** Running with `-Denv=qa` will construct:
- **UI Base URL**: `https://demo1.cybersoft.edu.vn` (from `env.qa.host=demo1`)
- **API Base URL**: `https://movie0706.cybersoft.edu.vn` (from `api.env.qa.host=movie0706`)

### 3. Configuration Override Priority

Configuration values are resolved in the following priority order (highest to lowest):

1. **System Properties** (highest priority)
   ```bash
   # Override browser selection
   gradlew test -Dbrowser=firefox
   
   # Override environment
   gradlew test -Denv=staging
   
   # Multiple overrides
   gradlew test -Dbrowser=edge -Denv=qa
   ```

2. **OS Environment Variables**
   ```bash
   # Windows
   set BROWSER=firefox
   
   # Unix/Linux/macOS
   export BROWSER=firefox
   ```

3. **.env file** (environment-specific credentials)

4. **config.properties** (default values, lowest priority)

### 4. Browser Configuration

The browser is selected via system property
```bash
gradlew test -Dbrowser=chrome    # Chrome
gradlew test -Dbrowser=firefox   # Firefox  
gradlew test -Dbrowser=edge      # Edge
gradlew test -Dbrowser=safari    # Safari (macOS only)
```

If not specified, defaults to `chrome`.

### 5. Test Data Configuration

- **messages_vi.properties** - Vietnamese language strings for validation of UI alerts and messages

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

Tests use TestNG groups for control over test data creation and flexible execution.

**Available Group:**
- `requiresUser` - Triggers automatic user creation before test and deletion after test (via `BaseTest`)

**Usage:**
```java
@Test(groups = "requiresUser",
      description = "Test successful login with valid credentials")
public void testSuccessfulLoginWithValidCredentials() {
    // Test implementation
    UserAccount testUser = getTestUser();  // User created automatically
}
```

**Tests without the `requiresUser` group** will skip user creation/deletion, which is useful for:
- Registration tests (creating their own users)
- Guest browsing tests
- UI validation tests that don't require authentication


## Run Tests
### Run All Tests
```java
gradlew test
```

### Run Specific Test Suite

```bash
# E2E tests only
gradlew test -DsuiteXmlFile=e2e.xml

# Smoke tests
gradlew test -DsuiteXmlFile=smoke.xml

# Regression tests
gradlew test -DsuiteXmlFile=regression.xml
```

### Run Specific Test Class

```bash
# Run a specific test class
gradlew test --tests testcases.registration.TC01_RegisterWithValidInputsTest

# Run a specific test method
gradlew test --tests testcases.registration.TC01_RegisterWithValidInputsTest.testRegisterWithValidInputs
```

### Run Tests by Package

```bash
# All authentication tests
gradlew test --tests "testcases.authentication.*"

# All registration tests
gradlew test --tests "testcases.registration.*"

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
<suite name="Smoke Suite" parallel="classes" thread-count="5">
```

Adjust `thread-count` based on your system resources.

## Reporting
The framework uses **ExtentReports** as the primary reporting solution. TestNG executes the tests and triggers ExtentReports through the `TestListener` class, creating comprehensive HTML reports with rich visualizations.

### How It Works

```
TestNG (Test Execution) → TestListener (Hooks) → ExtentReportManager → ExtentReport.html
```

### ExtentReports - Main Report

The report is automatically generated during test execution.

**1. Run tests:**
```bash
gradlew test
```

**2. After test execution, view the comprehensive HTML dashboard:**

- **Location:** `test-output/ExtentReport.html`
- **Features:**
    - Test execution summary with pass/fail statistics
    - Individual test details with steps
    - Screenshots embedded for failures
    - Execution timeline and duration
    - System and environment information
    - Categorization by test groups
    - Real-time test status

**3. Open report:**
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