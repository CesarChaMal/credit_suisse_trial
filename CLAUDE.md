# CLAUDE.md - AI Assistant Guide for Credit Suisse Trial Project

> **Last Updated:** 2025-11-13
> **Project Version:** 1.0-SNAPSHOT
> **Java Version:** 24 (with Java 8 compatibility option)

## Table of Contents
1. [Project Overview](#project-overview)
2. [Codebase Structure](#codebase-structure)
3. [Technology Stack](#technology-stack)
4. [Development Setup](#development-setup)
5. [Architecture & Design Patterns](#architecture--design-patterns)
6. [Key Conventions](#key-conventions)
7. [Testing Strategy](#testing-strategy)
8. [Common Development Workflows](#common-development-workflows)
9. [Performance Considerations](#performance-considerations)
10. [Important Notes for AI Assistants](#important-notes-for-ai-assistants)

---

## Project Overview

### Purpose
A sophisticated **financial instrument processing application** demonstrating 10 different processing strategies for performance comparison. This is a technical demonstration project showcasing various programming paradigms, optimization techniques, and modern Java features.

### Business Context
Processes time-series data for financial instruments (currencies, commodities, derivatives) with configurable database multipliers. Originally designed as a technical assessment exercise, it has evolved into a comprehensive performance comparison platform.

### Key Capabilities
- **10 Processing Strategies:** Parallel Stream, RxJava, Spring Batch, Manual Batch, Single-Threaded, Functional, CompletableFuture, Kafka Stream, Modern Java 24, Spring Batch Framework
- **Multithreaded File Processing:** Handles files up to gigabytes in size with chunked processing (1000 lines per chunk)
- **Interactive Web Interface:** AJAX-based performance testing with real-time progress tracking
- **Dataset Management:** On-demand generation of test datasets (1K, 10K, 100K, 1M records)
- **Memory Protection:** Built-in 80% memory threshold monitoring
- **Performance Registry:** Historical test results with automatic ranking

### Business Rules (Critical)
As defined in `task.txt` and implemented in `src/main/java/com/credit_suisse/app/core/InstrumentCalculator.java`:

1. **INSTRUMENT1:** Mean (average) of all prices × 1.05 multiplier
2. **INSTRUMENT2:** Mean of November 2014 prices only × 1.10 multiplier
3. **INSTRUMENT3:** Custom "on-the-fly" calculation (sum × 2) × 1.15 multiplier
4. **Other Instruments:** Sum of newest 10 elements × database multiplier
5. **Default Multiplier:** 1.0 if no database entry exists
6. **Current Date Assumption:** 19-Dec-2014 (no data after this date)
7. **Business Days Only:** Monday-Friday (dates are validated)
8. **Database Multiplier Cache:** Values assumed stable for 5 seconds

---

## Codebase Structure

### Directory Layout

```
credit_suisse_trial/
├── src/
│   ├── main/
│   │   ├── java/com/credit_suisse/app/
│   │   │   ├── config/                    # Spring Boot configuration
│   │   │   │   ├── SpringRootConfig.java  # Root app context with JDBC
│   │   │   │   ├── SpringWebConfig.java   # MVC configuration
│   │   │   │   ├── SpringBatchConfig.java # Batch framework setup
│   │   │   │   ├── KafkaConfig.java       # Kafka streaming config
│   │   │   │   └── db/                    # Database configurations (H2, HSQL, Derby)
│   │   │   ├── core/                      # Core business logic
│   │   │   │   ├── CalculatorEngine.java  # Main coordinator (Strategy pattern)
│   │   │   │   ├── ProcessingStrategy.java # Strategy interface
│   │   │   │   ├── ProcessingStrategyFactory.java # Factory pattern
│   │   │   │   ├── MultithreadedFileProcessor.java # Parallel file reading
│   │   │   │   ├── FileProcessor.java     # Simple file I/O
│   │   │   │   ├── InstrumentCalculator.java # Business logic implementation
│   │   │   │   ├── MultiplierService.java # Database multiplier service
│   │   │   │   ├── [10 Strategy implementations]
│   │   │   │   └── module/                # Calculation modules
│   │   │   │       ├── AverageModule.java
│   │   │   │       ├── AverageMonthModule.java
│   │   │   │       ├── OnFlyModule.java
│   │   │   │       └── AverageNewstInstrumentsModule.java
│   │   │   ├── dao/                       # Data Access Layer
│   │   │   │   ├── InstrumentPriceModifierDao.java (interface)
│   │   │   │   └── InstrumentPriceModifierDaoImpl.java
│   │   │   ├── model/                     # Domain models
│   │   │   │   ├── Instrument.java        # Abstract base (Strategy pattern)
│   │   │   │   ├── Instrument1.java, Instrument2.java, Instrument3.java
│   │   │   │   ├── InstrumentPriceModifier.java
│   │   │   │   └── InstrumentCalculateBehavior.java
│   │   │   ├── servlet3/                  # Web initialization
│   │   │   │   └── WebAppInitializer.java
│   │   │   ├── util/                      # Utilities
│   │   │   │   ├── InstrumentFileGenerator.java
│   │   │   │   ├── OptimizedInstrumentFileGenerator.java
│   │   │   │   ├── InstrumentUtil.java
│   │   │   │   ├── TimeFormatter.java
│   │   │   │   └── CommonConstants.java
│   │   │   ├── web/controller/            # MVC Controllers
│   │   │   │   └── WelcomeController.java
│   │   │   └── CreditSuisseTrialApplication.java # Main entry point
│   │   ├── resources/
│   │   │   ├── db/sql/                    # Database init scripts
│   │   │   │   ├── h2/, hsql/, derby/
│   │   │   │   │   ├── create-db.sql
│   │   │   │   │   └── insert-data.sql
│   │   │   ├── application*.properties    # Spring Boot config
│   │   │   ├── logback.xml                # Logging configuration
│   │   │   └── [input files].txt          # Sample datasets
│   │   └── webapp/WEB-INF/
│   │       ├── views/jsp/                 # JSP templates
│   │       │   ├── welcome.jsp            # Main dashboard
│   │       │   ├── performance-fragment.jsp
│   │       │   ├── single-strategy-fragment.jsp
│   │       │   └── file-generator.jsp
│   │       └── spring-web-servlet.xml, web.xml
│   └── test/java/com/credit_suisse/app/   # JUnit 5 tests (15 files)
├── pom.xml                                 # Maven (Java 24)
├── pom_java8.xml                          # Legacy Maven (Java 8)
├── run-app.sh                             # Cross-platform launcher
├── .env                                   # Environment configuration
├── .gitignore                             # Comprehensive ignore patterns
├── README.md                              # User documentation
├── task.txt                               # Original requirements
└── example_input.txt                      # Sample input data
```

### File Statistics
- **Total Java Files:** 45 main + 15 test = 60 files
- **Total Lines:** ~2,123 lines of Java code
- **Test Coverage:** 15 comprehensive test files covering all major components

### Key Files to Understand First

When starting work on this project, read these files in order:

1. **`task.txt`** - Original requirements and business rules
2. **`README.md`** - Project overview and running instructions
3. **`src/main/java/com/credit_suisse/app/core/CalculatorEngine.java`** - Main coordinator
4. **`src/main/java/com/credit_suisse/app/core/ProcessingStrategy.java`** - Strategy interface
5. **`src/main/java/com/credit_suisse/app/core/InstrumentCalculator.java`** - Business logic
6. **`src/main/java/com/credit_suisse/app/model/Instrument.java`** - Domain model
7. **`pom.xml`** - Dependencies and build configuration

---

## Technology Stack

### Core Technologies
- **Java:** 24 with preview features (virtual threads, records, pattern matching)
  - **Alternative:** Java 8 support via `pom_java8.xml`
- **Build Tool:** Maven 3.9+
- **Framework:** Spring Boot 3.4.4
  - Spring Web MVC
  - Spring Data JDBC
  - Spring Batch
  - Spring Kafka
  - Spring Boot Actuator

### Databases
- **Primary:** H2 Database (in-memory, latest version)
- **Alternatives:** HSQL, Derby
- **Connection Pooling:** Apache Commons DBCP2

### Libraries & Frameworks
- **RxJava 3.1.8** - Reactive programming with observables
- **Apache Kafka Streams** - Distributed processing (optional)
- **Spring Dotenv 4.0.0** - Environment variable management
- **Logback** - Logging framework via SLF4J

### View Layer
- **JSP** with JSTL tags
- **Embedded Tomcat** (Jasper engine)
- **Modern CSS** with gradients and animations
- **Vanilla JavaScript** - No external JS frameworks (pure AJAX)

### Testing
- **JUnit 5** (Jupiter API)
- **Spring Boot Test**
- **Spring Kafka Test**

### Modern Java Features Utilized
- ✅ Virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`)
- ✅ Structured concurrency
- ✅ Pattern matching and enhanced switch expressions
- ✅ Records (Java 14+)
- ✅ `var` keyword (Java 10+)
- ✅ Stream API and functional programming
- ✅ CompletableFuture for async operations
- ✅ ForkJoinPool for parallel processing

---

## Development Setup

### Prerequisites
- **Java 24** (recommended) or Java 8 (legacy mode)
- **Maven 3.9+**
- **Git**
- **Optional:** SDKMAN for Java version management

### Initial Setup

```bash
# Clone repository (if applicable)
git clone <repository-url>
cd credit_suisse_trial

# Build project
mvn clean install

# Run tests
mvn test

# Start web application
mvn spring-boot:run
# Access at: http://localhost:8080/

# Or use the cross-platform launcher
./run-app.sh
```

### Alternative: Console Mode

```bash
# Run console application directly
mvn exec:java -Dexec.mainClass="com.credit_suisse.app.CreditSuisseTrialApplication"
```

### IDE Setup (Eclipse)

```bash
mvn eclipse:eclipse
# Then: File → Import → Existing Projects into Workspace
```

### Environment Configuration

Edit `.env` file for custom configuration:

```properties
SPRING_PROFILES_ACTIVE=dev
KAFKA_BOOTSTRAP_SERVERS=172.23.114.82:9092
PROCESSING_BATCH_SIZE=1000
MEMORY_THRESHOLD=0.8
PROCESSING_TIMEOUT=30000
DB_URL=jdbc:h2:mem:testdb
SERVER_PORT=8080
```

### Maven Profiles

**Java 24 (default):** Use `pom.xml`
```bash
mvn clean install
mvn spring-boot:run
```

**Java 8 (legacy):** Use `pom_java8.xml`
```bash
mvn clean install -f pom_java8.xml
mvn jetty:run -f pom_java8.xml
```

### Running with Preview Features

Java 24 preview features are automatically enabled via Maven configuration:
```xml
<maven.compiler.enablePreview>true</maven.compiler.enablePreview>
<jvmArguments>--enable-preview</jvmArguments>
```

---

## Architecture & Design Patterns

### 1. Strategy Pattern (Core Design)

**Location:** `src/main/java/com/credit_suisse/app/core/`

**Interface:**
```java
public interface ProcessingStrategy {
    Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao);
}
```

**Usage:**
```java
CalculatorEngine engine = new CalculatorEngine("input.txt");
engine.setProcessingStrategy(new ParallelStreamProcessingStrategy());
Map<String, Double> results = engine.calculate(dao);
```

**10 Strategy Implementations:**
1. `ParallelStreamProcessingStrategy` - Java 8 parallel streams with ForkJoinPool
2. `RxJavaProcessingStrategy` - Reactive observables with schedulers
3. `SpringBatchProcessingStrategy` - Simplified Spring Batch
4. `ManualBatchProcessingStrategy` - Explicit loop-based batching (50 items/batch)
5. `SingleThreadedProcessingStrategy` - Sequential processing baseline
6. `FunctionalProcessingStrategy` - Pure functional programming
7. `CompletableFutureProcessingStrategy` - Async processing with custom thread pool
8. `KafkaStreamProcessingStrategy` - Distributed streaming with fallback
9. `ModernJavaProcessingStrategy` - Virtual threads + structured concurrency (Java 24)
10. `SpringBatchProcessingStrategy` - Enterprise batch processing framework

**When to Use:**
- Adding new processing strategies: Implement `ProcessingStrategy` interface
- Switching strategies at runtime: Use `CalculatorEngine.setProcessingStrategy()`
- Performance comparison: Iterate through all strategies via `ProcessingStrategyFactory`

### 2. Factory Pattern

**Location:** `src/main/java/com/credit_suisse/app/core/ProcessingStrategyFactory.java`

**Purpose:** Centralized strategy creation with type-safe enumeration

**Usage:**
```java
ProcessingStrategy strategy = ProcessingStrategyFactory.createStrategy(
    ProcessingStrategyFactory.StrategyType.PARALLEL_STREAM
);

// Iterate all strategies for comparison
for (ProcessingStrategyFactory.StrategyType type : ProcessingStrategyFactory.StrategyType.values()) {
    ProcessingStrategy strategy = ProcessingStrategyFactory.createStrategy(type);
    // ... perform testing
}
```

**When to Use:**
- Adding new strategy: Add enum value and case to factory
- Getting all strategies: Use `StrategyType.values()`
- Type-safe strategy selection in web controller

### 3. Strategy Pattern in Domain Model

**Location:** `src/main/java/com/credit_suisse/app/model/`

**Purpose:** Pluggable calculation behaviors for different instrument types

```java
public abstract class Instrument {
    private InstrumentCalculateBehavior calculateBehavior;

    public double calculate() {
        return calculateBehavior.calculate(this);
    }
}

public class Instrument1 extends Instrument {
    public Instrument1() {
        setCalculateBehavior(new AverageModule());
    }
}
```

**When to Use:**
- Adding new instrument types: Extend `Instrument` and assign appropriate behavior
- Changing calculation logic: Implement `InstrumentCalculateBehavior` interface

### 4. Multithreaded File Processing Pattern

**Location:** `src/main/java/com/credit_suisse/app/core/MultithreadedFileProcessor.java`

**Purpose:** Handle large files (gigabytes) without memory issues

**Key Features:**
- Chunked reading (1000 lines per chunk)
- Parallel parsing with CompletableFuture
- ForkJoinPool for optimal CPU utilization
- Memory protection with 80% threshold

**Implementation Pattern:**
```java
List<List<String>> chunks = createChunks(lines, CHUNK_SIZE);

List<CompletableFuture<List<Instrument>>> futures = chunks.stream()
    .map(chunk -> CompletableFuture.supplyAsync(() ->
        chunk.parallelStream()
            .map(InstrumentUtil::defineOf)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()),
        ForkJoinPool.commonPool()))
    .collect(Collectors.toList());

return futures.stream()
    .map(CompletableFuture::join)
    .flatMap(List::stream)
    .collect(Collectors.toList());
```

**When to Use:**
- Processing files > 100MB
- Generating large datasets
- Any operation that reads entire file into memory

### 5. Memory Protection Pattern

**Location:** All processing strategies

**Implementation:**
```java
Runtime runtime = Runtime.getRuntime();
long maxMemory = runtime.maxMemory();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();

if (usedMemory > maxMemory * 0.8) {
    throw new OutOfMemoryError("Insufficient memory for processing");
}
```

**When to Use:**
- Before processing large datasets
- Inside tight loops for continuous monitoring
- Required in all new processing strategies

### 6. Database Multiplier Caching

**Location:** `src/main/java/com/credit_suisse/app/core/MultiplierService.java`

**Purpose:** Reduce database calls (per requirements: multipliers stable for 5 seconds)

**When to Use:**
- Querying instrument multipliers
- Avoid querying database for each record

### 7. Separation of Concerns (Layered Architecture)

```
Controller Layer (web.controller)
    ↓
Service Layer (core)
    ↓
DAO Layer (dao)
    ↓
Database (H2/HSQL/Derby)
```

**When to Use:**
- Adding new features: Place logic in appropriate layer
- Database queries: Always go through DAO layer
- Business logic: Place in service layer (core package)
- Web endpoints: Add to controller layer

---

## Key Conventions

### Naming Conventions

#### Packages
- `com.credit_suisse.app.config` - Configuration classes
- `com.credit_suisse.app.core` - Core business logic
- `com.credit_suisse.app.core.module` - Calculation modules
- `com.credit_suisse.app.dao` - Data access objects
- `com.credit_suisse.app.model` - Domain models
- `com.credit_suisse.app.util` - Utility classes
- `com.credit_suisse.app.web.controller` - Web controllers
- `com.credit_suisse.app.servlet3` - Web initialization

#### Classes
- Strategy implementations: `*ProcessingStrategy`
- Calculation modules: `*Module`
- DAOs: `*Dao` (interface) and `*DaoImpl` (implementation)
- Configuration: `*Config`
- Controllers: `*Controller`

#### Files
- Input files: `{size}_input.txt` or `{size}_optimized.txt`
- Dataset sizes: `SMALL` (1K), `MEDIUM` (10K), `LARGE` (100K), `XLARGE` (1M)
- Generated datasets: `dataset_{size}.txt` (gitignored)

### Code Style

#### Logging
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

// Usage
logger.info("Processing {} records", count);
logger.debug("Detailed debug info: {}", details);
logger.error("Error occurred during processing", exception);
```

#### Exception Handling
- Wrap checked exceptions in `RuntimeException` with meaningful messages
- Include original exception for stack traces
- Log errors before throwing
- Use specific exception types when available

```java
try {
    // ... processing
} catch (IOException e) {
    logger.error("Failed to read file: {}", filePath, e);
    throw new RuntimeException("File processing failed", e);
}
```

#### Dependency Injection
- **Preferred:** Constructor injection
- **Spring Beans:** Use `@Autowired` annotation
- **Controllers:** Field injection acceptable

```java
@Service
public class MultiplierService {
    private final InstrumentPriceModifierDao dao;

    @Autowired
    public MultiplierService(InstrumentPriceModifierDao dao) {
        this.dao = dao;
    }
}
```

#### Resource Management
- Use try-with-resources for `AutoCloseable` objects
- Explicit cleanup in finally blocks for manual resource management
- Close streams, connections, and executors

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // ... processing
} // Automatically closed
```

### Data File Format

**CSV Format:**
```
INSTRUMENT_NAME,DATE,VALUE
INSTRUMENT1,12-Mar-2015,12.21
INSTRUMENT2,03-Nov-2014,15.67
```

**Rules:**
- 3 columns: instrument name, date, value
- Date format: `dd-MMM-yyyy` (e.g., `12-Mar-2015`)
- Business days only (Monday-Friday)
- No header row in actual data files
- Current date assumed: `19-Dec-2014`

### Performance Measurement

**Pattern:**
```java
long startTime = System.currentTimeMillis();
// ... processing
long duration = System.currentTimeMillis() - startTime;
logger.info("Processing completed in {}", TimeFormatter.formatTime(duration));
```

**TimeFormatter Output:**
- `123ms` for milliseconds
- `1.23s` for seconds
- `1m 23s` for minutes

### Constants

**Location:** `src/main/java/com/credit_suisse/app/util/CommonConstants.java`

```java
public static final String INSTRUMENT1 = "INSTRUMENT1";
public static final String INSTRUMENT2 = "INSTRUMENT2";
public static final String INSTRUMENT3 = "INSTRUMENT3";
public static final int INSTRUMENTS_COUNT = 10000;
public static final int NEWST = 10; // Newest elements for calculation
```

### Database Schema

**Table:** `INSTRUMENT_PRICE_MODIFIER`

```sql
CREATE TABLE INSTRUMENT_PRICE_MODIFIER (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR2(256) NOT NULL UNIQUE,
  multiplier NUMBER(10,2) NOT NULL
);
```

**Initial Data:**
```sql
INSERT INTO INSTRUMENT_PRICE_MODIFIER (name, multiplier) VALUES ('INSTRUMENT1', 1.05);
INSERT INTO INSTRUMENT_PRICE_MODIFIER (name, multiplier) VALUES ('INSTRUMENT2', 1.10);
INSERT INTO INSTRUMENT_PRICE_MODIFIER (name, multiplier) VALUES ('INSTRUMENT3', 1.15);
```

**Location:** `src/main/resources/db/sql/{h2,hsql,derby}/`

---

## Testing Strategy

### Test Organization

**Location:** `src/test/java/com/credit_suisse/app/`

**Total Tests:** 15 comprehensive test files

**Categories:**
1. **Strategy Tests** - Each processing strategy has dedicated tests
2. **Core Component Tests** - Calculator engine, business logic
3. **Infrastructure Tests** - DAO, factory, utilities

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CalculatorEngineTest

# Run tests with preview features (automatic)
mvn test

# Run tests with coverage
mvn clean verify
```

### Test Pattern

**Standard Test Structure:**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorEngineTest {

    @Test
    void testSetProcessingStrategy() {
        // Arrange
        CalculatorEngine engine = new CalculatorEngine("src/main/resources/input.txt");
        ProcessingStrategy strategy = new ParallelStreamProcessingStrategy();

        // Act
        engine.setProcessingStrategy(strategy);
        Map<String, Double> results = engine.calculate(null);

        // Assert
        assertNotNull(results);
        assertTrue(results.containsKey("INSTRUMENT1"));
    }
}
```

### Key Test Files

1. **`CalculatorEngineTest`** - Strategy switching and coordination
2. **`ProcessingStrategyFactoryTest`** - Factory pattern validation
3. **`InstrumentCalculatorTest`** - Business logic verification
4. **`InstrumentPriceModifierDaoTest`** - Database access
5. **`MultiplierServiceTest`** - Caching behavior
6. **`[Strategy]ProcessingStrategyTest`** - Individual strategy testing
7. **`TimeFormatterTest`** - Utility functions

### Testing Best Practices for This Project

1. **Always test with small datasets first** - Use `src/main/resources/input.txt`
2. **Test memory protection** - Verify strategies handle large files
3. **Test database multipliers** - Verify correct calculation with multipliers
4. **Test edge cases** - Empty files, invalid dates, missing instruments
5. **Test concurrency** - Ensure thread-safe implementations
6. **Mock external dependencies** - Database should be in-memory for tests

---

## Common Development Workflows

### Adding a New Processing Strategy

**Steps:**

1. **Create strategy class** in `src/main/java/com/credit_suisse/app/core/`:
   ```java
   public class MyNewProcessingStrategy implements ProcessingStrategy {
       @Override
       public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
           // Implementation
       }
   }
   ```

2. **Add to factory enum** in `ProcessingStrategyFactory.java`:
   ```java
   public enum StrategyType {
       // ... existing strategies
       MY_NEW_STRATEGY
   }

   public static ProcessingStrategy createStrategy(StrategyType type) {
       return switch (type) {
           // ... existing cases
           case MY_NEW_STRATEGY -> new MyNewProcessingStrategy();
       };
   }
   ```

3. **Create test class** in `src/test/java/com/credit_suisse/app/`:
   ```java
   class MyNewProcessingStrategyTest {
       @Test
       void testProcessing() {
           // Test implementation
       }
   }
   ```

4. **Run comprehensive tests:**
   ```bash
   mvn test
   mvn spring-boot:run
   # Test via web UI at http://localhost:8080/
   ```

5. **Update documentation** - Add strategy description to README.md

### Adding a New Calculation Module

**Steps:**

1. **Create module class** in `src/main/java/com/credit_suisse/app/core/module/`:
   ```java
   public class MyNewModule implements InstrumentCalculateBehavior {
       @Override
       public double calculate(Instrument instrument) {
           // Calculation logic
       }
   }
   ```

2. **Assign to instrument** in model class or `InstrumentCalculator`:
   ```java
   if (instrumentName.equals("INSTRUMENT4")) {
       instrument.setCalculateBehavior(new MyNewModule());
   }
   ```

3. **Add database multiplier** (if needed):
   ```sql
   INSERT INTO INSTRUMENT_PRICE_MODIFIER (name, multiplier) VALUES ('INSTRUMENT4', 1.20);
   ```

4. **Test the module:**
   ```bash
   mvn test -Dtest=InstrumentCalculatorTest
   ```

### Modifying Business Logic

**Critical File:** `src/main/java/com/credit_suisse/app/core/InstrumentCalculator.java`

**Process:**

1. **Understand requirements** - Review `task.txt` for business rules
2. **Locate calculation method** - Find appropriate calculation module
3. **Modify calculation** - Update logic in module or `InstrumentCalculator`
4. **Update tests** - Modify expected values in test files
5. **Validate** - Run all tests and web UI comparison

**Example:**
```java
// Changing INSTRUMENT1 calculation from mean to median
public class MedianModule implements InstrumentCalculateBehavior {
    @Override
    public double calculate(Instrument instrument) {
        List<Double> prices = instrument.getPrices();
        Collections.sort(prices);
        int middle = prices.size() / 2;
        return prices.size() % 2 == 0
            ? (prices.get(middle - 1) + prices.get(middle)) / 2.0
            : prices.get(middle);
    }
}
```

### Adding Web Endpoints

**File:** `src/main/java/com/credit_suisse/app/web/controller/WelcomeController.java`

**Pattern:**
```java
@RequestMapping(value = "/my-endpoint", method = RequestMethod.GET)
public String myEndpoint(@RequestParam String param, Model model) {
    try {
        // Processing logic
        model.addAttribute("result", result);
        return "fragment-name"; // JSP fragment
    } catch (Exception e) {
        logger.error("Error in endpoint", e);
        model.addAttribute("error", e.getMessage());
        return "error-fragment";
    }
}
```

**Testing:**
```bash
mvn spring-boot:run
curl http://localhost:8080/my-endpoint?param=value
```

### Generating Test Datasets

**Via Web UI:**
```
http://localhost:8080/
Click "Generate Dataset" → Select size → Generate
```

**Via Code:**
```java
OptimizedInstrumentFileGenerator generator = new OptimizedInstrumentFileGenerator();
generator.generateFile("dataset_large.txt", 100000); // 100K records
```

**Programmatically:**
```bash
mvn exec:java -Dexec.mainClass="com.credit_suisse.app.util.OptimizedInstrumentFileGenerator" -Dexec.args="dataset.txt 10000"
```

### Database Management

**Switch Database:** Modify `src/main/java/com/credit_suisse/app/config/SpringRootConfig.java`

```java
@Bean
public DataSource dataSource() {
    // H2 (current)
    return H2DataSourceConfig.dataSource();

    // Alternative: HSQL
    // return HSQLDataSourceConfig.dataSource();

    // Alternative: Derby
    // return DerbyDataSourceConfig.dataSource();
}
```

**View Database Console (H2):**
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)
```

### Performance Tuning

**Key Areas:**

1. **Memory Settings:**
   ```bash
   export MAVEN_OPTS="-Xmx4g -Xms1g"
   mvn spring-boot:run
   ```

2. **Thread Pool Sizing:**
   - Modify `ForkJoinPool.commonPool()` parallelism
   - Adjust `Executors.newFixedThreadPool(n)` size
   - Tune virtual thread executor (Java 24)

3. **Batch Size Configuration:**
   - Change `CHUNK_SIZE` in `MultithreadedFileProcessor` (default: 1000)
   - Modify `BATCH_SIZE` in batch strategies (default: 50)

4. **Database Connection Pooling:**
   - Configure in `application.properties`:
     ```properties
     spring.datasource.hikari.maximum-pool-size=10
     spring.datasource.hikari.minimum-idle=2
     ```

---

## Performance Considerations

### Memory Management

**Critical for Large Files:**

1. **Always use `MultithreadedFileProcessor`** for files > 10MB
2. **Never load entire file into memory** - use chunked processing
3. **Monitor memory threshold** - implement 80% check before processing
4. **Stream processing** - prefer streams over loading all data

**Anti-Pattern (DO NOT DO):**
```java
// BAD: Loads entire file into memory
List<String> allLines = Files.readAllLines(Paths.get(inputPath));
```

**Correct Pattern:**
```java
// GOOD: Chunked processing
MultithreadedFileProcessor processor = new MultithreadedFileProcessor();
List<Instrument> instruments = processor.readInstrumentsFromFile(inputPath);
```

### Processing Strategy Selection Guide

**When to Use Each Strategy:**

| Strategy | Best For | Avoid When |
|----------|----------|------------|
| **Parallel Stream** | General purpose, CPU-bound tasks | I/O-bound operations |
| **RxJava** | Event-driven, async pipelines | Simple synchronous tasks |
| **Spring Batch** | Enterprise ETL, database writes | Real-time processing |
| **Manual Batch** | Fine-grained control, custom logic | Simple operations |
| **Single-Threaded** | Baseline comparison, debugging | Production use |
| **Functional** | Pure transformations, immutability | Stateful operations |
| **CompletableFuture** | Async I/O, independent tasks | CPU-bound parallel tasks |
| **Kafka Stream** | Distributed systems, event sourcing | Single-machine deployments |
| **Modern Java (Virtual Threads)** | High I/O concurrency, thousands of tasks | CPU-intensive calculations |
| **Spring Batch Framework** | Complex workflows, job management | Simple calculations |

### Optimization Checklist

When optimizing performance:

- [ ] Profile with JProfiler, VisualVM, or JFR
- [ ] Measure baseline with Single-Threaded strategy
- [ ] Test with realistic data sizes (100K+ records)
- [ ] Monitor memory usage during execution
- [ ] Check CPU utilization (should be near 100% for CPU-bound)
- [ ] Validate results match across all strategies
- [ ] Consider I/O vs CPU bottlenecks
- [ ] Test on target deployment environment

### Known Performance Characteristics

**Typical Performance (100K records):**

1. **Fastest:** Modern Java (Virtual Threads) - ~500ms
2. **Fast:** Parallel Stream, CompletableFuture - ~800ms
3. **Medium:** RxJava, Functional - ~1200ms
4. **Slow:** Manual Batch, Spring Batch - ~2000ms
5. **Baseline:** Single-Threaded - ~4000ms

**Note:** Actual performance depends on hardware, data distribution, and memory.

### Scalability Limits

**Tested Configurations:**

- **Maximum File Size:** 1M records (~50MB)
- **Maximum Memory:** 4GB JVM heap
- **Recommended Parallelism:** Number of CPU cores × 2
- **Optimal Chunk Size:** 1000-5000 records per chunk

**Beyond These Limits:**
- Consider distributed processing (Kafka strategy)
- Use disk-based databases instead of in-memory
- Implement pagination for web UI results

---

## Important Notes for AI Assistants

### Critical Business Rules (DO NOT MODIFY Without User Approval)

1. **INSTRUMENT1 = Mean × 1.05** - Hardcoded in requirements
2. **INSTRUMENT2 = November 2014 Mean × 1.10** - Date-specific calculation
3. **INSTRUMENT3 = Custom (sum × 2) × 1.15** - "On-the-fly" demonstration
4. **Other Instruments = Sum of Newest 10 × Multiplier** - Per `task.txt`
5. **Current Date = 19-Dec-2014** - Assumed context from requirements
6. **Business Days Only** - Monday-Friday validation required

### Files to NEVER Modify

- **`task.txt`** - Original requirements (historical artifact)
- **`example_input.txt`** - Sample input data reference
- **Database init scripts** - Unless explicitly requested

### Files That Should Be Generated (Not Committed)

Per `.gitignore`, these files are generated dynamically:
- `dataset_*.txt` - Large test datasets
- `input_*.txt` (except examples) - Generated input files
- `*.log` - Application logs
- `target/` - Maven build artifacts

### When Adding New Features

**Always Consider:**

1. **Memory Impact** - Will this handle 1M records?
2. **Thread Safety** - Is this safe for parallel execution?
3. **Database Multipliers** - Does this respect the 5-second cache rule?
4. **Error Handling** - What happens on failures?
5. **Testing** - Can this be unit tested?
6. **Performance** - Will this impact comparison results?
7. **Documentation** - Update README.md and this file

### Common Pitfalls

1. **Don't remove singleton pattern** - Already removed (was CalculatorEngine)
2. **Don't modify business logic without tests** - Always validate with test suite
3. **Don't load entire file into memory** - Use MultithreadedFileProcessor
4. **Don't skip memory checks** - 80% threshold is critical
5. **Don't assume sorted data** - Input is explicitly unsorted per requirements
6. **Don't ignore date validation** - Business days only
7. **Don't hardcode file paths** - Use parameters or configuration
8. **Don't forget to test all strategies** - Changes must work across all 10

### Debugging Tips

**Enable Debug Logging:**

Edit `src/main/resources/logback.xml`:
```xml
<logger name="com.credit_suisse.app" level="debug"/>
```

**Useful Log Points:**
- File reading: `MultithreadedFileProcessor`
- Strategy execution: `CalculatorEngine.calculate()`
- Database queries: `InstrumentPriceModifierDaoImpl`
- Calculation results: `InstrumentCalculator`

**Console Output:**
```bash
mvn spring-boot:run | tee app.log
```

**Memory Debugging:**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap-dump.hprof"
```

### Git Workflow

**Current Branch:** `claude/claude-md-mhxxb07jes6he9fq-01Xxe44Qs28Qtt379W9YqhAs`

**Commit Pattern:**
```bash
git add <files>
git commit -m "Add new processing strategy for X

- Implement XProcessingStrategy
- Add factory integration
- Include comprehensive tests
- Update documentation"
```

**Before Committing:**
- [ ] Run `mvn test` - all tests pass
- [ ] Run `mvn clean install` - build succeeds
- [ ] Test web UI - http://localhost:8080/
- [ ] Update CLAUDE.md if architecture changed
- [ ] Update README.md if user-facing features changed

### Communication with Users

**When Presenting Changes:**

1. **Explain business impact** - How does this affect calculations?
2. **Provide performance metrics** - Before/after comparisons
3. **Reference file locations** - Use `file_path:line_number` format
4. **Show test results** - Demonstrate validation
5. **Highlight risks** - Memory, thread safety, breaking changes

**Example:**
```
I've added a new processing strategy called VectorizedProcessingStrategy
that uses SIMD operations for faster calculations.

Location: src/main/java/com/credit_suisse/app/core/VectorizedProcessingStrategy.java:1

Performance improvement: 100K records in 300ms (was 800ms)
Memory usage: Stable at 45% (was 60%)
Tests: All 15 test files pass

Next steps:
1. Test with 1M record dataset
2. Add to factory enum
3. Update README.md strategy list
```

### Extension Points

**Easily Extensible:**
- New processing strategies (just implement interface)
- New calculation modules (just implement behavior)
- New instrument types (extend Instrument class)
- New database types (add config class)

**Difficult to Extend (Architectural):**
- File format (hardcoded CSV parsing)
- Business rules (hardcoded INSTRUMENT1/2/3)
- Date format (hardcoded `dd-MMM-yyyy`)
- Web UI framework (JSP, not modern React/Vue)

**If User Requests Major Changes:**
- Clarify scope and impact
- Propose incremental approach
- Highlight breaking changes
- Suggest testing strategy

---

## Additional Resources

### Documentation Files
- **`README.md`** - User-facing documentation
- **`task.txt`** - Original requirements specification
- **`CLAUDE.md`** (this file) - AI assistant guide
- **JavaDoc** - Inline documentation in source files

### External Dependencies Documentation
- [Spring Boot 3.4 Docs](https://docs.spring.io/spring-boot/docs/3.4.4/reference/html/)
- [RxJava 3 Docs](https://reactivex.io/documentation/observable.html)
- [Spring Batch Docs](https://docs.spring.io/spring-batch/reference/)
- [Apache Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Java 24 Release Notes](https://openjdk.org/projects/jdk/24/)

### Code Navigation Quick Reference

**Find Files:**
```bash
find src -name "*Strategy*"
find src -name "*Instrument*"
find src -name "*Test.java"
```

**Search Code:**
```bash
grep -r "INSTRUMENT1" src/
grep -r "ProcessingStrategy" src/
grep -r "multiplier" src/
```

**Count Lines:**
```bash
find src -name "*.java" | xargs wc -l
```

---

## Changelog

### 2025-11-13 - Initial Creation
- Created comprehensive CLAUDE.md for AI assistants
- Documented all 10 processing strategies
- Detailed architecture and design patterns
- Established conventions and best practices
- Added troubleshooting and extension guides

---

## Questions or Issues?

When working on this project, if you encounter:

1. **Ambiguous requirements** → Refer to `task.txt` for original specification
2. **Build failures** → Check Java version (24 with preview features)
3. **Test failures** → Review business logic in `InstrumentCalculator.java`
4. **Performance issues** → Use profiling tools and compare strategies
5. **Memory errors** → Verify chunked processing is enabled
6. **Database issues** → Check `SpringRootConfig.java` and SQL scripts

**For architectural questions:** Review this CLAUDE.md file and README.md
**For business logic questions:** Review task.txt and InstrumentCalculator.java
**For implementation examples:** Check existing strategy implementations

---

**Remember:** This is a performance comparison platform. When in doubt, maintain parity across all strategies and prioritize correctness over optimization.
