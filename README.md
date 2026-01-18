# TDD Framework using Java, Selenium & TestNG

A comprehensive Test-Driven Development (TDD) automation framework built with Java, Selenium WebDriver, and TestNG for robust end-to-end testing of web applications.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Report Generation](#report-generation)
- [Best Practices](#best-practices)
- [Contributing](#contributing)

## Overview

This framework implements the Test-Driven Development (TDD) approach, where tests are written before the actual implementation. It provides a scalable, maintainable, and reusable automation testing solution for web applications using:

- **Java** - Primary programming language
- **Selenium WebDriver** - Web UI automation library
- **TestNG** - Testing framework with advanced capabilities
- **Maven** - Build and dependency management

## Features

✅ **Page Object Model (POM)** - Structured test design pattern for maintainability  
✅ **Cross-Browser Testing** - Support for Chrome, Firefox, Edge, and Safari  
✅ **Parallel Test Execution** - Leverage TestNG for parallel execution  
✅ **Comprehensive Reporting** - ChainTest Reports and TestNG HTML reports  
✅ **Screenshot & Video Capture** - Automatic capture on test failures  
✅ **Configurable Test Data** - External configuration files for test scenarios  
✅ **Logging & Monitoring** - Detailed logs for debugging and monitoring  
✅ **CI/CD Ready** - Jenkins integration support  
✅ **Data-Driven Testing** - Support for parameterized tests  
✅ **Wait Strategies** - Implicit and explicit waits for reliable test execution  

## Prerequisites

- **Java JDK 11 or higher**
- **Maven 3.6 or higher**
- **Git** - Version control
- **IDE** - Eclipse, IntelliJ IDEA, or Visual Studio Code
- **WebDriver** - ChromeDriver, GeckoDriver, EdgeDriver (based on browser choice)

## Project Structure

```
TDD-Framework-using-Java-Selenium-TestNG/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── pages/          # Page Object classes
│   │   │   ├── utils/          # Utility classes
│   │   │   ├── listeners/      # Test listeners
│   │   │   └── constants/      # Constants and enums
│   │   └── resources/
│   │       └── config/         # Configuration files
│   └── test/
│       ├── java/
│       │   └── tests/          # TestNG test classes
│       └── resources/
│           ├── testdata/       # Test data files
│           └── testng.xml      # TestNG configuration
├── Configurations/             # Configuration files
├── ExecutionReports/           # Test execution reports
├── ScreenShots/                # Screenshots on failure
├── seleniumJar/                # Required JAR files
├── pom.xml                     # Maven configuration
├── .project                    # Eclipse project file
├── .classpath                  # Eclipse classpath
└── README.md                   # This file
```

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/VishalGiram0842/TDD-Framework-using-Java-Selenium-TestNG.git
   cd TDD-Framework-using-Java-Selenium-TestNG
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Download WebDrivers**
   - Download appropriate WebDriver binaries for your OS
   - Place them in the project or configure the path in configuration files

## Configuration

Configuration parameters are managed through property files in the `Configurations/` directory.

### Key Configuration Files

- **config.properties** - Browser type, URL, timeouts, etc.
- **testdata.properties** - Test data for various scenarios

### Sample Configuration

```properties
# config.properties
browser=chrome
base_url=https://www.example.com
implicit_wait=10
explicit_wait=20
screenshot_on_failure=true
```

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=LoginTest
```

### Run Tests with TestNG XML
```bash
mvn clean test -Dsuitexml=testng.xml
```

### Run Tests in Parallel
Update `testng.xml`:
```xml
<suite name="ParallelSuite" parallel="methods" thread-count="4">
    <!-- test configurations -->
</suite>
```

### Run Tests on Specific Browser
```bash
mvn clean test -Dbrowser=firefox
```

## Report Generation

### ChainTest Reports
Reports are automatically generated after test execution in:
```
ExecutionReports/ExtentReports/
```

### TestNG HTML Reports
Default TestNG reports are generated in:
```
test-output/
```

### View Reports
- Open `ExecutionReports/index.html` in a web browser
- Check `test-output/index.html` for TestNG reports

## Best Practices

1. **Page Object Model** - Maintain separate page classes for each page
2. **Naming Conventions** - Follow Java naming standards (camelCase for methods, PascalCase for classes)
3. **Test Data Management** - Keep test data separate from test logic
4. **Waits Over Sleeps** - Use explicit waits instead of hard delays
5. **Single Responsibility** - Each test should verify a single functionality
6. **Assertions** - Use meaningful assertion messages
7. **Logging** - Add appropriate logs for debugging
8. **Error Handling** - Implement proper exception handling
9. **Code Reusability** - Extract common functionality into utility classes
10. **Version Control** - Commit frequently with meaningful messages

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Author

**Vishal Giram** - [GitHub Profile](https://github.com/VishalGiram0842)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

---

**Happy Testing! 🚀**
