Credit Suisse Trial - Performance Comparison
===============================

## Overview
Financial instrument processing application with comprehensive performance comparison across **10 different processing strategies**, demonstrating various programming paradigms and optimization techniques including modern Java 24 features and multithreaded instrument loading.

## Technologies
* Maven 3.9+
* Spring Boot 3.4.4
* H2 Database (latest)
* RxJava 3.1.8 (reactive processing)
* Java 24 with Preview Features
* CompletableFuture (async processing)
* Streams & Functional Programming
* Apache Kafka Streams (distributed streaming)
* Virtual Threads & Structured Concurrency
* Spring Batch Framework
* Multithreaded File Processing

## Processing Strategies
1. **Parallel Stream** - Java 8 parallel streams with ForkJoinPool
2. **RxJava** - Reactive programming with observables and schedulers
3. **Batch** - Spring Batch framework with chunk processing
4. **Manual Batch** - Explicit loops with memory management (50 items per batch)
5. **Single-Threaded** - Sequential processing with simulated delays
6. **Functional** - Pure functional programming with higher-order functions
7. **CompletableFuture** - Async processing with custom thread pool
8. **Kafka Stream** - Distributed streaming with fallback to in-memory processing
9. **Modern Java 24** - Virtual threads with structured concurrency and pattern matching
10. **Spring Batch** - Enterprise batch processing with job execution framework

## Features
* **Ten Processing Strategies**: Comprehensive comparison across different paradigms
* **Multithreaded Instrument Loading**: Parallel file processing with chunked reading (1000 lines per chunk)
* **Factory Pattern**: Centralized strategy management with ProcessingStrategyFactory
* **Performance Measurement**: Execution time tracking for all approaches
* **Memory Protection**: Built-in memory monitoring (80% threshold)
* **Error Handling**: Graceful degradation and comprehensive exception handling
* **Interactive Web Interface**: Initial full comparison + individual strategy selection
* **Real-time Processing**: AJAX-based performance testing with centralized progress bar management
* **Dataset Management**: On-demand file generation with multithreaded optimization
* **Async Operations**: Multiple simultaneous tests with individual progress tracking
* **Performance Registry**: Historical test results with ranking and timing data
* **Auto-Sorting**: Performance grid automatically sorts from fastest to slowest

## Running the Application

### Web Application
```shell
$ mvn spring-boot:run
```
Access: `http://localhost:8080/`

The web interface will show:
- **Dataset Size Selection**: Small (1K), Medium (10K), Large (100K), X-Large (1M) with async processing
- **Individual Strategy Selection**: Choose specific strategies with independent progress tracking
- **Multithreaded Performance**: Optimized instrument loading across all strategies
- **Real-time Results**: AJAX-based execution with centralized progress bar management
- **Performance Registry**: Historical test results with rankings and timing data
- **Auto-Sorting Grid**: Performance metrics automatically sorted from fastest to slowest
- **Memory Protection**: Built-in monitoring and graceful error handling
- **Failed Strategy Handling**: Clear visual indication and proper sorting of failed tests

### Console Application
```shell
$ mvn exec:java -Dexec.mainClass="com.credit_suisse.app.CreditSuisseTrialApplication"
```
Outputs performance comparison directly to console.

### Linux Script
```shell
$ ./run-app.sh
```

## Architecture
- **CalculatorEngine**: Main coordinator using Strategy pattern (singleton removed)
- **ProcessingStrategy**: Interface for different processing approaches
- **ProcessingStrategyFactory**: Factory pattern for centralized strategy management
- **MultithreadedFileProcessor**: Parallel instrument loading with chunked processing
- **ParallelStreamProcessingStrategy**: Java 8 parallel streams implementation
- **RxJavaProcessingStrategy**: Reactive programming with observables
- **SpringBatchProcessingStrategy**: Simplified batch processing (database concurrency resolved)
- **ManualBatchProcessingStrategy**: Explicit loop-based batch processing
- **SingleThreadedProcessingStrategy**: Sequential processing for comparison
- **FunctionalProcessingStrategy**: Pure functional programming approach
- **CompletableFutureProcessingStrategy**: Async processing with custom thread pools
- **KafkaStreamProcessingStrategy**: Distributed streaming with intelligent fallback
- **ModernJavaProcessingStrategy**: Virtual threads with structured concurrency (Java 24)
- **ProgressBarManager**: Centralized progress bar management class
- **Interactive Web UI**: Async dataset and strategy testing with individual progress bars
- **Performance Registry**: Historical test tracking with rankings and timing data
- **Auto-Sorting Grid**: Dynamic performance metric sorting from fastest to slowest
- **Memory Protection**: Built-in memory monitoring across all strategies
- **Multithreaded Optimization**: Parallel file reading and instrument parsing

## IDE Setup
1. `mvn eclipse:eclipse`
2. Import via **existing projects into workspace**
3. Run CreditSuisseTrialApplication.java for console testing
4. Run `mvn spring-boot:run` for web testing
