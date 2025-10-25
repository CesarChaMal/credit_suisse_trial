<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Credit Suisse Trial - Performance Comparison</title>
    <style>
        * { box-sizing: border-box; }
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; padding: 20px; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .container { max-width: 1200px; margin: 0 auto; }
        .header {
            text-align: center; 
            color: white; 
            margin-bottom: 30px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .card { 
            background: white; 
            margin: 20px 0; 
            padding: 25px; 
            border-radius: 15px; 
            box-shadow: 0 8px 32px rgba(0,0,0,0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.2);
        }
        .card-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }
        .card-icon {
            font-size: 24px;
            margin-right: 15px;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
        }
        .card-title { font-size: 24px; font-weight: 600; color: #333; margin: 0; }
        .info-box { 
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); 
            padding: 20px; 
            margin: 15px 0; 
            border-radius: 10px;
            border-left: 5px solid #667eea;
        }
        .control-panel {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            margin: 20px 0;
        }
        .button-group {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
            margin: 20px 0;
        }
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 25px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-size: 12px;
        }
        .btn:before {
            content: '';
            position: absolute;
            top: 0; left: -100%;
            width: 100%; height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            transition: left 0.5s;
        }
        .btn:hover:before { left: 100%; }
        .btn:hover { transform: translateY(-3px); box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
        .btn-dataset { background: linear-gradient(45deg, #28a745, #20c997); color: white; }
        .btn-dataset.medium { background: linear-gradient(45deg, #ffc107, #fd7e14); color: #333; }
        .btn-dataset.large { background: linear-gradient(45deg, #fd7e14, #dc3545); color: white; }
        .btn-dataset.xlarge { background: linear-gradient(45deg, #dc3545, #6f42c1); color: white; }
        .btn-strategy { background: linear-gradient(45deg, #667eea, #764ba2); color: white; }
        .btn-generate { background: linear-gradient(45deg, #17a2b8, #138496); color: white; }
        .generation-success { background: linear-gradient(135deg, #d4edda, #c3e6cb); color: #155724; padding: 15px; border-radius: 10px; margin: 10px 0; }
        .generation-error { background: linear-gradient(135deg, #f8d7da, #f1b0b7); color: #721c24; padding: 15px; border-radius: 10px; margin: 10px 0; }
        .generation-progress { background: linear-gradient(135deg, #e7f3ff, #cce7ff); color: #0066cc; padding: 15px; border-radius: 10px; margin: 10px 0; }
        .btn.active { 
            transform: scale(1.05) translateY(-2px); 
            box-shadow: 0 0 20px rgba(102, 126, 234, 0.6);
        }
        .performance-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 15px;
            margin: 20px 0;
        }
        .metric-card {
            background: linear-gradient(135deg, #fff 0%, #f8f9ff 100%);
            padding: 20px;
            border-radius: 12px;
            border: 1px solid #e0e6ff;
            transition: all 0.3s ease;
        }
        .metric-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.15);
        }
        .metric-label { font-weight: 600; color: #555; margin-bottom: 8px; }
        .metric-value { font-size: 18px; font-weight: 700; color: #28a745; }
        .spinner {
            display: inline-block;
            width: 20px; height: 20px;
            border: 3px solid #f3f3f3;
            border-top: 3px solid #667eea;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
        .status-success { color: #28a745; font-weight: 600; }
        .status-error { color: #dc3545; font-weight: 600; }
        .status-loading { color: #667eea; font-style: italic; }
        .progress-bar {
            width: 100%;
            height: 6px;
            background: #e0e6ff;
            border-radius: 3px;
            overflow: hidden;
            margin: 10px 0;
        }
        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #667eea, #764ba2);
            border-radius: 3px;
            animation: progress 2s ease-in-out infinite;
        }
        @keyframes progress {
            0% { width: 0%; }
            50% { width: 70%; }
            100% { width: 100%; }
        }

        @media (max-width: 768px) {
            .control-panel { grid-template-columns: 1fr; }
            .button-group { justify-content: center; }
            .performance-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🚀 Credit Suisse Performance Benchmark</h1>
        <p>Advanced multithreaded financial instrument processing comparison</p>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-icon">📊</div>
            <h2 class="card-title">System Overview</h2>
        </div>
        <div class="info-box">
            <p><strong>🎯 Purpose:</strong> Compare 10 different processing strategies for financial instrument calculations with real-time performance metrics and multithreaded optimization.</p>
            <p><strong>⚡ Features:</strong> Parallel loading, memory protection, AJAX processing, and comprehensive benchmarking.</p>
            <p><strong>📁 Current Input File:</strong> <code id="currentInputFile">Loading...</code></p>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-icon">⚙️</div>
            <h2 class="card-title">Performance Testing</h2>
        </div>
        
        <div class="control-panel">
            <div>
                <h3>📁 Dataset Size Selection</h3>
                <div class="button-group">
                    <button onclick="loadAllStrategies('SMALL', this)" class="btn btn-dataset" data-size="SMALL" title="1,000 instruments - Quick test">Small (1K)</button>
                    <button onclick="loadAllStrategies('MEDIUM', this)" class="btn btn-dataset medium" data-size="MEDIUM" title="10,000 instruments - Standard test">Medium (10K)</button>
                    <button onclick="loadAllStrategies('LARGE', this)" class="btn btn-dataset large" data-size="LARGE" title="100,000 instruments - Performance test">Large (100K)</button>
                    <button onclick="loadAllStrategies('XLARGE', this)" class="btn btn-dataset xlarge" data-size="XLARGE" title="1,000,000 instruments - Stress test">X-Large (1M)</button>
                </div>
                <div id="progress-small" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-medium" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-large" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-xlarge" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-custom-file" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div style="margin-top: 15px; text-align: center;">
                    <input type="file" id="customFile" accept=".txt,.csv" style="display: none;" onchange="loadCustomFile(this)">
                    <button onclick="document.getElementById('customFile').click()" class="btn btn-dataset" id="customFileBtn" title="Upload your own instrument file">📂 Custom File</button>
                </div>
            </div>
            
            <div>
                <h3>🔧 Individual Strategy Testing</h3>
                <div class="button-group">
                    <button onclick="runSingleStrategy('PARALLEL_STREAM', this)" class="btn btn-strategy" title="Java 8 parallel streams">Parallel Stream</button>
                    <button onclick="runSingleStrategy('RXJAVA', this)" class="btn btn-strategy" title="Reactive programming">RxJava</button>
                    <button onclick="runSingleStrategy('BATCH', this)" class="btn btn-strategy" title="Spring Batch framework">Batch</button>
                    <button onclick="runSingleStrategy('MANUAL_BATCH', this)" class="btn btn-strategy" title="Explicit batch loops">Manual Batch</button>
                    <button onclick="runSingleStrategy('SINGLE_THREADED', this)" class="btn btn-strategy" title="Sequential processing">Single Thread</button>
                    <button onclick="runSingleStrategy('FUNCTIONAL', this)" class="btn btn-strategy" title="Pure functional approach">Functional</button>
                    <button onclick="runSingleStrategy('COMPLETABLE_FUTURE', this)" class="btn btn-strategy" title="Async processing">CompletableFuture</button>
                    <button onclick="runSingleStrategy('KAFKA_STREAM', this)" class="btn btn-strategy" title="Distributed streaming">Kafka Stream</button>
                    <button onclick="runSingleStrategy('MODERN_JAVA', this)" class="btn btn-strategy" title="Virtual threads & structured concurrency">Modern Java 24</button>
                    <button onclick="runSingleStrategy('SPRING_BATCH', this)" class="btn btn-strategy" title="Enterprise batch processing">Spring Batch</button>
                </div>
                <div id="progress-parallel-stream" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-rxjava" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-batch" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-manual-batch" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-single-threaded" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-functional" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-completable-future" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-kafka-stream" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-modern-java" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
                <div id="progress-spring-batch" style="display: none; margin: 10px 0; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
            </div>
        </div>
        <div id="performanceSection">
            <div class="info-box" id="resultsQueue" style="display: none;">
                <h3 style="color: #28a745; margin-top: 0;">📊 Processing Results Queue</h3>
                <div id="queuedResults"></div>
            </div>
            <div class="performance-grid">
                <div class="metric-card">
                    <div class="metric-label">🚀 Parallel Stream</div>
                    <div class="metric-value" id="parallelStreamTime">${parallelStreamTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🔄 RxJava Reactive</div>
                    <div class="metric-value" id="rxJavaTime">${rxJavaTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">📦 Batch</div>
                    <div class="metric-value" id="batchTime">${batchTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🔧 Manual Batch</div>
                    <div class="metric-value" id="manualBatchTime">${manualBatchTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🐌 Single Thread</div>
                    <div class="metric-value" id="singleThreadTime">${singleThreadTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🎯 Functional</div>
                    <div class="metric-value" id="functionalTime">${functionalTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">⚡ CompletableFuture</div>
                    <div class="metric-value" id="asyncTime">${asyncTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🌊 Kafka Stream</div>
                    <div class="metric-value" id="kafkaTime">${kafkaTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🎆 Modern Java 24</div>
                    <div class="metric-value" id="modernJavaTime">${modernJavaTime}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">🏢 Spring Batch</div>
                    <div class="metric-value" id="springBatchTime">${springBatchTime}</div>
                </div>
            </div>
            <div class="info-box">
                <div style="background: #f8f9fa; padding: 20px; border-radius: 10px; border-left: 5px solid #28a745;">
                    <h3 style="color: #28a745; margin-top: 0;">📊 Performance Registry</h3>
                    <div id="performanceRegistry" style="font-family: monospace; font-size: 14px; line-height: 1.6;">
                        <div class="status-loading">📊 Calculating performance comparison...</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

<script>
// Initialize loading states with modern UI
function initializeLoadingStates() {
    const metrics = ['parallelStreamTime', 'rxJavaTime', 'batchTime', 'manualBatchTime', 'singleThreadTime', 'functionalTime', 'asyncTime', 'kafkaTime', 'modernJavaTime', 'springBatchTime'];
    metrics.forEach(id => {
        const element = document.getElementById(id);
        if (element) element.innerHTML = '<span class="spinner"></span> Processing...';
    });
    const comparison = document.getElementById('performanceComparison');
    if (comparison) comparison.innerHTML = '<span class="status-loading">📊 Calculating performance comparison...</span>';
}

// Removed automatic initialization - user must click to start processing

let currentFileSize = 'SMALL';
let resultsQueue = [];

// Centralized progress bar management
class ProgressBarManager {
    constructor() {
        this.bars = new Map();
    }
    
    create(containerId, type, title, color = '#007acc') {
        const container = document.getElementById(containerId);
        if (!container) return null;
        
        const barId = containerId + '-bar-' + Date.now();
        const html = 
            '<div style="font-weight: bold; margin-bottom: 15px; color: ' + color + ';">' + title + '</div>' +
            '<div style="margin: 10px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;">' +
                '<div style="font-weight: 500; margin-bottom: 8px; color: #333;">' + type + '</div>' +
                '<div style="background: #e9ecef; border-radius: 10px; height: 25px; overflow: hidden;">' +
                    '<div id="' + barId + '" style="background: linear-gradient(90deg, ' + color + ', ' + this.darkenColor(color) + '); height: 100%; width: 0%; transition: width 0.3s ease; display: flex; align-items: center; justify-content: center; color: white; font-size: 14px; font-weight: bold;">0%</div>' +
                '</div>' +
            '</div>';
        
        container.style.display = 'block';
        container.innerHTML = html;
        
        const progressBar = {
            id: barId,
            element: document.getElementById(barId),
            container: container,
            startTime: Date.now(),
            interval: null
        };
        
        this.bars.set(barId, progressBar);
        return barId;
    }
    
    start(barId, estimatedDuration) {
        const bar = this.bars.get(barId);
        if (!bar) return;
        
        this.stop(barId);
        
        bar.interval = setInterval(() => {
            const elapsed = (Date.now() - bar.startTime) / 1000;
            const progress = Math.min((elapsed / estimatedDuration) * 90 + Math.random() * 5, 95);
            
            if (bar.element) {
                bar.element.style.width = progress + '%';
                bar.element.textContent = Math.round(progress) + '%';
            }
        }, 300);
    }
    
    complete(barId) {
        const bar = this.bars.get(barId);
        if (!bar) return;
        
        this.stop(barId);
        if (bar.element) {
            bar.element.style.width = '100%';
            bar.element.textContent = '100%';
        }
        
        setTimeout(() => this.hide(barId), 2000);
    }
    
    stop(barId) {
        const bar = this.bars.get(barId);
        if (bar && bar.interval) {
            clearInterval(bar.interval);
            bar.interval = null;
        }
    }
    
    hide(barId) {
        const bar = this.bars.get(barId);
        if (bar && bar.container) {
            bar.container.style.display = 'none';
        }
        this.bars.delete(barId);
    }
    
    darkenColor(color) {
        const colorMap = {
            '#007acc': '#0056b3',
            '#28a745': '#20c997',
            '#17a2b8': '#138496'
        };
        return colorMap[color] || color;
    }
}

const progressManager = new ProgressBarManager();

function addToResultsQueue(fileSize, status, message, timestamp = new Date()) {
    const result = { fileSize, status, message, timestamp: timestamp.toLocaleTimeString() };
    resultsQueue.push(result);
    updateResultsDisplay();
}

function updateResultsDisplay() {
    const queueContainer = document.getElementById('resultsQueue');
    const queueResults = document.getElementById('queuedResults');
    
    if (resultsQueue.length === 0) {
        queueContainer.style.display = 'none';
        return;
    }
    
    queueContainer.style.display = 'block';
    
    let html = '';
    resultsQueue.slice(-10).forEach(result => {
        const statusClass = result.status === 'success' ? 'status-success' : result.status === 'error' ? 'status-error' : 'status-loading';
        const icon = result.status === 'success' ? '✅' : result.status === 'error' ? '❌' : '🔄';
        
        const borderColor = result.status === 'success' ? '#28a745' : result.status === 'error' ? '#dc3545' : '#007acc';
        html += '<div style="margin: 8px 0; padding: 10px; background: white; border-radius: 6px; border-left: 3px solid ' + borderColor + '; font-size: 13px;">' +
                '<span class="' + statusClass + '">' + icon + ' ' + result.timestamp + ' - ' + result.fileSize + ': ' + result.message + '</span>' +
                '</div>';
    });
    
    queueResults.innerHTML = html;
}

function loadAllStrategies(fileSize, buttonElement) {
    console.log('loadAllStrategies called with:', fileSize, buttonElement);
    
    // Fallback to data attribute if fileSize is empty
    if (!fileSize && buttonElement && buttonElement.dataset) {
        fileSize = buttonElement.dataset.size;
        console.log('Using data attribute:', fileSize);
    }
    
    // Additional fallback - try to get from event target
    if (!fileSize && typeof event !== 'undefined' && event && event.target && event.target.dataset) {
        fileSize = event.target.dataset.size;
        console.log('Using event target data:', fileSize);
    }
    
    if (!fileSize) {
        console.error('fileSize is required, received:', fileSize);
        return;
    }
    
    currentFileSize = fileSize;
    
    // Don't remove active from other buttons - allow multiple simultaneous operations
    if (buttonElement) {
        buttonElement.classList.add('active');
        buttonElement.disabled = true;
        buttonElement.innerHTML = buttonElement.innerHTML + ' <span class="spinner"></span>';
    }
    
    // Add to queue
    addToResultsQueue(fileSize, 'loading', 'Processing started...');
    
    // Update current input file display
    const inputFile = getInputFileForSize(fileSize);
    const currentFileElement = document.getElementById('currentInputFile');
    if (currentFileElement) {
        currentFileElement.textContent = inputFile;
    }
    
    // Create progress bar
    const containerId = 'progress-' + fileSize.toLowerCase();
    const estimatedTime = { 'SMALL': 8, 'MEDIUM': 15, 'LARGE': 25, 'XLARGE': 40 }[fileSize] || 20;
    const barId = progressManager.create(
        containerId,
        fileSize + ' Dataset - All Strategies Processing',
        '🚀 Processing ' + fileSize + ' Dataset (All Strategies)',
        '#007acc'
    );
    
    if (!barId) {
        console.error('Failed to create progress bar for:', containerId);
        return;
    }
    
    progressManager.start(barId, estimatedTime);
    
    fetch('performance?fileSize=' + fileSize)
        .then(response => response.text())
        .then(html => {
            // Complete progress animation
            progressManager.complete(barId);
            
            // Update content after brief delay - don't overwrite main section for async operations
            setTimeout(() => {
                // Extract and update performance metrics
                updatePerformanceMetrics(html);
                // Re-enable button
                if (buttonElement) {
                    buttonElement.disabled = false;
                    buttonElement.classList.remove('active');
                    buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
                }
                // Add to registry and queue with ranking
                addToPerformanceRegistry(fileSize, 'Dataset Size Test', html);
                addToResultsQueue(fileSize, 'success', 'Processing completed successfully');
            }, 800);
        })
        .catch(error => {
            progressManager.hide(barId);
            addToResultsQueue(fileSize, 'error', 'Processing failed - ' + error.message);
            // Re-enable button on error
            if (buttonElement) {
                buttonElement.disabled = false;
                buttonElement.classList.remove('active');
                buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
            }
        });
}

function runSingleStrategy(strategy, buttonElement) {
    // Add async button handling
    if (buttonElement) {
        buttonElement.classList.add('active');
        buttonElement.disabled = true;
        buttonElement.innerHTML = buttonElement.innerHTML + ' <span class="spinner"></span>';
    }
    
    // Add to queue
    addToResultsQueue(strategy, 'loading', 'Processing strategy...');
    
    // Update current input file display
    const inputFile = getInputFileForSize(currentFileSize);
    const currentFileElement = document.getElementById('currentInputFile');
    if (currentFileElement) {
        currentFileElement.textContent = inputFile;
    }
    
    // Create strategy progress bar
    const containerId = 'progress-' + strategy.toLowerCase().replace('_', '-');
    const estimatedTime = { 'SMALL': 3, 'MEDIUM': 6, 'LARGE': 12, 'XLARGE': 20 }[currentFileSize] || 8;
    const barId = progressManager.create(
        containerId,
        strategy + ' Processing (' + currentFileSize + ' Dataset)',
        '⚡ Executing ' + strategy + ' Strategy',
        '#28a745'
    );
    
    if (!barId) {
        console.error('Failed to create progress bar for strategy:', containerId);
        return;
    }
    
    progressManager.start(barId, estimatedTime);
    
    const url = 'single-strategy?strategy=' + encodeURIComponent(strategy) + '&fileSize=' + encodeURIComponent(currentFileSize);
    
    fetch(url)
        .then(response => response.text())
        .then(html => {
            progressManager.complete(barId);
            
            setTimeout(() => {
                // Re-enable button
                if (buttonElement) {
                    buttonElement.disabled = false;
                    buttonElement.classList.remove('active');
                    buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
                }
                // Add to registry and queue
                addToPerformanceRegistry(currentFileSize, 'Single Strategy: ' + strategy, html);
                addToResultsQueue(strategy, 'success', 'Strategy completed successfully');
            }, 600);
        })
        .catch(error => {
            progressManager.hide(barId);
            addToResultsQueue(strategy, 'error', 'Strategy failed - ' + error.message);
            // Re-enable button on error
            if (buttonElement) {
                buttonElement.disabled = false;
                buttonElement.classList.remove('active');
                buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
            }
        });
}

function getInputFileForSize(fileSize) {
    switch (fileSize.toUpperCase()) {
        case 'SMALL': return 'src' + '/' + 'main' + '/' + 'resources' + '/' + 'small_optimized.txt';
        case 'MEDIUM': return 'src' + '/' + 'main' + '/' + 'resources' + '/' + 'medium_optimized.txt';
        case 'LARGE': return 'src' + '/' + 'main' + '/' + 'resources' + '/' + 'large_optimized.txt';
        case 'XLARGE': return 'src' + '/' + 'main' + '/' + 'resources' + '/' + 'xlarge_optimized.txt';
        default: return 'src' + '/' + 'main' + '/' + 'resources' + '/' + 'input.txt';
    }
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Set initial file display
    const initialFile = getInputFileForSize('SMALL');
    const currentFileElement = document.getElementById('currentInputFile');
    if (currentFileElement) {
        currentFileElement.textContent = initialFile;
    }
    currentFileSize = 'SMALL';
});

function loadCustomFile(fileInput) {
    const file = fileInput.files[0];
    if (!file) return;
    
    const buttonElement = document.getElementById('customFileBtn');
    const fileSize = 'CUSTOM';
    
    // Add async button handling
    if (buttonElement) {
        buttonElement.classList.add('active');
        buttonElement.disabled = true;
        buttonElement.innerHTML = buttonElement.innerHTML + ' <span class="spinner"></span>';
    }
    
    // Add to queue
    addToResultsQueue(fileSize, 'loading', 'Processing custom file: ' + file.name);
    
    const formData = new FormData();
    formData.append('file', file);
    
    // Update current input file display
    document.getElementById('currentInputFile').textContent = 'Custom file: ' + file.name;
    
    // Use existing progress container for custom file
    const progressId = 'progress-custom-file';
    const progressContainer = document.getElementById(progressId);
    
    if (!progressContainer) {
        console.error('Progress container not found:', progressId);
        return;
    }
    
    progressContainer.style.display = 'block';
    progressContainer.innerHTML = `
        <div style="font-weight: bold; margin-bottom: 15px; color: #17a2b8;">📂 Processing Custom File: ${file.name}</div>
        <div style="margin: 10px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;">
            <div style="font-weight: 500; margin-bottom: 8px; color: #333;">File Analysis & Processing</div>
            <div style="background: #e9ecef; border-radius: 10px; height: 25px; overflow: hidden;">
                <div id="progress-bar-custom" style="background: linear-gradient(90deg, #17a2b8, #138496); height: 100%; width: 0%; transition: width 0.3s ease; display: flex; align-items: center; justify-content: center; color: white; font-size: 14px; font-weight: bold;">0%</div>
            </div>
        </div>
    `;
    
    // Clear any existing custom file interval
    if (window.customFileInterval) {
        clearInterval(window.customFileInterval);
    }
    
    // Animate custom file progress with improved timing
    const progressBar = document.getElementById('progress-bar-custom');
    let progress = 0;
    let startTime = Date.now();
    
    window.customFileInterval = setInterval(() => {
        const elapsed = (Date.now() - startTime) / 1000;
        const estimatedTotal = 10; // Assume 10 seconds for custom file processing
        
        const timeBasedProgress = Math.min((elapsed / estimatedTotal) * 85, 85);
        const randomFactor = Math.random() * 4;
        progress = Math.min(timeBasedProgress + randomFactor, 90);
        
        if (progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.textContent = Math.round(progress) + '%';
        }
    }, 300);
    
    fetch('performance-custom', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(html => {
        clearInterval(window.customFileInterval);
        const progressBar = document.getElementById('progress-bar-custom');
        if (progressBar) {
            progressBar.style.width = '100%';
            progressBar.textContent = '100%';
        }
        
        setTimeout(() => {
            document.getElementById('performanceSection').innerHTML = html;
            currentFileSize = 'CUSTOM';
            // Re-enable button
            if (buttonElement) {
                buttonElement.disabled = false;
                buttonElement.classList.remove('active');
                buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
            }
            // Hide progress container after completion
            setTimeout(() => {
                if (progressContainer) {
                    progressContainer.style.display = 'none';
                }
            }, 2000);
            // Add to registry and queue
            addToPerformanceRegistry('CUSTOM', 'Custom File: ' + file.name);
            addToResultsQueue(fileSize, 'success', 'Custom file processed successfully');
        }, 600);
    })
    .catch(error => {
        clearInterval(window.customFileInterval);
        addToResultsQueue(fileSize, 'error', 'Custom file failed - ' + error.message);
        document.getElementById('performanceSection').innerHTML = 
            '<div class="info-box"><div class="status-error">⚠️ Failed to process custom file - Please check format</div></div>';
        // Re-enable button on error
        if (buttonElement) {
            buttonElement.disabled = false;
            buttonElement.classList.remove('active');
            buttonElement.innerHTML = buttonElement.innerHTML.replace(' <span class="spinner"></span>', '');
        }
        if (progressContainer) {
            progressContainer.style.display = 'none';
        }
    });
}

function generateDataset(size) {
    console.log('Generating dataset for size:', size);
    const button = document.getElementById('gen' + size.charAt(0).toUpperCase() + size.slice(1).toLowerCase());
    const statusDiv = document.getElementById('generationStatus');
    
    if (!button || !statusDiv) {
        console.error('Button or status div not found');
        return;
    }
    
    button.disabled = true;
    button.innerHTML = '<span class="spinner"></span> Generating...';
    
    statusDiv.innerHTML = `<div class="generation-progress">🚀 Generating ${size} dataset with multithreaded optimization...</div>`;
    
    // Add progress bar for visual feedback
    statusDiv.innerHTML += `
        <div style="margin: 10px 0; background: #e9ecef; border-radius: 10px; height: 20px; overflow: hidden;">
            <div id="gen-progress-${size}" style="background: linear-gradient(90deg, #28a745, #20c997); height: 100%; width: 0%; transition: width 0.3s ease; display: flex; align-items: center; justify-content: center; color: white; font-size: 12px; font-weight: bold;">0%</div>
        </div>
    `;
    
    // Simulate progress during generation
    let progress = 0;
    const progressBar = document.getElementById('gen-progress-' + size);
    window.currentProgressInterval = setInterval(() => {
        progress += Math.random() * 8 + 2;
        if (progress > 95) progress = 95; // Don't complete until actual response
        if (progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.textContent = Math.round(progress) + '%';
        }
    }, 150);
    
    const formData = new FormData();
    formData.append('fileSize', size.toUpperCase());
    
    // Check which generator type is selected
    const generatorType = document.querySelector('input[name="datasetGeneratorType"]:checked')?.value || 'optimized';
    formData.append('optimized', generatorType === 'optimized' ? 'true' : 'false');
    formData.append('generatorType', generatorType);
    console.log('Sending form data: fileSize=' + size.toUpperCase() + ', generatorType=' + generatorType + ', optimized=' + (generatorType === 'optimized' ? 'true' : 'false'));
    console.log('FormData entries:', [...formData.entries()]);
    
    fetch('generate', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        console.log('Response status:', response.status);
        return response.text();
    })
    .then(data => {
        console.log('Response data length:', data.length);
        console.log('Response contains success:', data.includes('File generated successfully'));
        console.log('Response contains optimized:', data.includes('Optimized file generation completed'));
        console.log('Button element:', button);
        console.log('Size parameter:', size);
        
        // Clear progress after completion
        clearInterval(window.currentProgressInterval);
        
        if (data.includes('File generated successfully') || data.includes('Optimized file generation completed')) {
            statusDiv.innerHTML = `<div class="generation-success">✅ ${size} dataset generated successfully! Ready for performance testing.</div>`;
        } else {
            statusDiv.innerHTML = `<div class="generation-error">❌ Generation failed - please try again</div>`;
        }
        
        button.disabled = false;
        const sizeMap = {'SMALL': 'Small (1K)', 'MEDIUM': 'Medium (10K)', 'LARGE': 'Large (100K)', 'XLARGE': 'X-Large (1M)'};
        const sizeUpper = size.toUpperCase();
        console.log('Size upper:', sizeUpper);
        console.log('Size map lookup:', sizeMap[sizeUpper]);
        const sizeText = sizeMap[sizeUpper] || 'Unknown';
        const newText = `Generate ${sizeText}`;
        console.log('Setting button text to:', newText);
        button.innerHTML = newText;
        console.log('Button text after setting:', button.innerHTML);
    })
    .catch(error => {
        console.error('Fetch error:', error);
        statusDiv.innerHTML = `<div class="generation-error">❌ Failed to generate ${size} dataset. Error: ${error.message}</div>`;
        button.disabled = false;
        const sizeMap = {'SMALL': 'Small (1K)', 'MEDIUM': 'Medium (10K)', 'LARGE': 'Large (100K)', 'XLARGE': 'X-Large (1M)'};
        const sizeText = sizeMap[size.toUpperCase()] || 'Unknown';
        button.innerHTML = `Generate ${sizeText}`;
    });
}

let performanceHistory = [];
let currentPerformanceData = {};

function updatePerformanceMetrics(html) {
    // Extract performance ranking data
    const rankingMatch = html.match(/🏆 Performance Ranking:\s*([^\n]+)/i);
    if (!rankingMatch) return;
    
    const ranking = rankingMatch[1];
    
    // Parse individual strategy times from ranking
    const strategyMappings = {
        'Modern Java 24': 'modernJavaTime',
        'CompletableFuture': 'asyncTime',
        'Kafka Stream': 'kafkaTime',
        'Batch': 'batchTime',
        'Spring Batch': 'springBatchTime',
        'Functional': 'functionalTime',
        'RxJava': 'rxJavaTime',
        'Single-Threaded': 'singleThreadTime',
        'Manual Batch': 'manualBatchTime',
        'Parallel Stream': 'parallelStreamTime'
    };
    
    // Extract times and update display
    Object.keys(strategyMappings).forEach(strategyName => {
        const elementId = strategyMappings[strategyName];
        const regex = new RegExp(strategyName + ':\\s*([^,]+)', 'i');
        const match = ranking.match(regex);
        
        if (match) {
            let timeValue = match[1].trim();
            // Clean up HTML artifacts
            timeValue = timeValue.replace(/<\/div>.*$/, '').trim();
            
            const element = document.getElementById(elementId);
            if (element) {
                element.textContent = timeValue;
                element.style.color = timeValue === 'FAILED' ? '#dc3545' : '#28a745';
                
                // Store for sorting - convert various time formats to seconds
                let numericValue = 0;
                if (timeValue === 'FAILED') {
                    numericValue = Infinity; // Put failed strategies at the end
                } else if (timeValue.includes('min')) {
                    const minMatch = timeValue.match(/(\d+)\s*min\s*(\d+(?:\.\d+)?)\s*sec/);
                    if (minMatch) {
                        numericValue = parseInt(minMatch[1]) * 60 + parseFloat(minMatch[2]);
                    }
                } else {
                    numericValue = parseFloat(timeValue.replace(/[^0-9.]/g, ''));
                }
                
                if (!isNaN(numericValue)) {
                    currentPerformanceData[elementId] = { time: timeValue, numeric: numericValue };
                }
            }
        }
    });
    
    // Sort and reorder performance grid
    sortPerformanceGrid();
}

function sortPerformanceGrid() {
    const grid = document.querySelector('.performance-grid');
    if (!grid || Object.keys(currentPerformanceData).length === 0) return;
    
    // Get all metric cards
    const cards = Array.from(grid.children);
    
    // Sort cards by performance (fastest first)
    cards.sort((a, b) => {
        const aId = a.querySelector('.metric-value').id;
        const bId = b.querySelector('.metric-value').id;
        const aData = currentPerformanceData[aId];
        const bData = currentPerformanceData[bId];
        
        if (!aData && !bData) return 0;
        if (!aData) return 1;
        if (!bData) return -1;
        
        return aData.numeric - bData.numeric;
    });
    
    // Reorder in DOM
    cards.forEach(card => grid.appendChild(card));
}

function addToPerformanceRegistry(fileSize, testType, resultHtml = null) {
    const timestamp = new Date().toLocaleTimeString();
    const inputFile = getInputFileForSize(fileSize);
    
    // Get record count
    let recordCount = 'Unknown';
    switch(fileSize.toUpperCase()) {
        case 'SMALL': recordCount = '1,000'; break;
        case 'MEDIUM': recordCount = '10,000'; break;
        case 'LARGE': recordCount = '100,000'; break;
        case 'XLARGE': recordCount = '1,000,000'; break;
        case 'CUSTOM': recordCount = 'Variable'; break;
    }
    
    // Extract performance info from result HTML if available
    let performanceInfo = '';
    if (resultHtml) {
        if (testType.includes('Single Strategy')) {
            // Extract execution time from single strategy result
            const timeMatch = resultHtml.match(/Performance:<\/strong>\s*([^|]+)/i);
            if (timeMatch) {
                performanceInfo = ' - ' + timeMatch[1].trim();
            }
            // Update individual metric if available
            updatePerformanceMetrics(resultHtml);
        } else if (testType.includes('Dataset Size Test')) {
            // Extract ranking for dataset tests
            const rankingMatch = resultHtml.match(/🏆 Performance Ranking:\s*([^\n]+)/i);
            if (rankingMatch) {
                performanceInfo = ' - Ranking: ' + rankingMatch[1].trim();
            }
        }
    }
    
    const entry = {
        timestamp,
        fileSize,
        testType: testType + performanceInfo,
        inputFile,
        recordCount
    };
    
    performanceHistory.push(entry);
    updatePerformanceRegistry();
}

function updatePerformanceRegistry() {
    const registryDiv = document.getElementById('performanceRegistry');
    if (!registryDiv) return;
    
    if (performanceHistory.length === 0) {
        registryDiv.innerHTML = '<div class="status-loading">📊 Calculating performance comparison...</div>';
        return;
    }
    
    let html = '<div style="margin-bottom: 15px; font-weight: bold; color: #28a745;">📈 Performance Test History</div>';
    
    performanceHistory.slice(-8).forEach((entry, index) => {
        const isStrategy = entry.testType.includes('Single Strategy');
        const borderColor = isStrategy ? '#28a745' : '#007acc';
        const icon = isStrategy ? '⚡' : '📈';
        
        html += 
            '<div style="margin: 8px 0; padding: 12px; background: white; border-radius: 8px; border-left: 4px solid ' + borderColor + '; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">' +
                '<div style="font-weight: bold; color: ' + borderColor + '; margin-bottom: 5px;">' + icon + ' ' + entry.timestamp + ' - ' + entry.testType + '</div>' +
                '<div style="color: #666; font-size: 13px;">📁 <code>' + entry.inputFile + '</code></div>' +
                '<div style="color: #666; font-size: 13px;">📄 Records: <strong>' + entry.recordCount + '</strong> | Dataset: <strong>' + entry.fileSize + '</strong></div>' +
            '</div>';
    });
    
    if (performanceHistory.length > 8) {
        html += '<div style="text-align: center; color: #666; font-style: italic; margin-top: 10px;">... and ' + (performanceHistory.length - 8) + ' more tests</div>';
    }
    
    registryDiv.innerHTML = html;
}

// Check dataset availability on page load
fetch('check-datasets')
    .then(response => response.json())
    .then(data => {
        // Update button states based on available datasets
        const sizeMap = {'SMALL': '1K', 'MEDIUM': '10K', 'LARGE': '100K', 'XLARGE': '1M'};
        Object.keys(data).forEach(size => {
            const button = document.getElementById('gen' + size.charAt(0).toUpperCase() + size.slice(1).toLowerCase());
            if (button) {
                const baseText = `Generate ${size.charAt(0).toUpperCase() + size.slice(1).toLowerCase()} (${sizeMap[size]})`;
                if (data[size]) {
                    button.innerHTML = '✓ ' + baseText;
                    button.style.opacity = '0.7';
                } else {
                    button.innerHTML = baseText;
                }
            }
        });
    })
    .catch(error => {
        console.log('Dataset check failed:', error);
        // Ensure buttons have proper labels even if check fails
        const sizeMap = {'SMALL': '1K', 'MEDIUM': '10K', 'LARGE': '100K', 'XLARGE': '1M'};
        ['SMALL', 'MEDIUM', 'LARGE', 'XLARGE'].forEach(size => {
            const button = document.getElementById('gen' + size.charAt(0).toUpperCase() + size.slice(1).toLowerCase());
            if (button && button.innerHTML === 'Generate') {
                button.innerHTML = `Generate ${size.charAt(0).toUpperCase() + size.slice(1).toLowerCase()} (${sizeMap[size]})`;
            }
        });
    });
</script>

    <div class="card">
        <div class="card-header">
            <div class="card-icon">💰</div>
            <h2 class="card-title">Database Multipliers</h2>
        </div>
        <div class="info-box">
            <p><strong>📊 Purpose:</strong> Multiplier values from INSTRUMENT_PRICE_MODIFIER table that simulate external market influences.</p>
        </div>
        <div class="info-box">
            <pre style="background: white; padding: 15px; border-radius: 8px; overflow-x: auto;">${modifiers}</pre>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-icon">📋</div>
            <h2 class="card-title">Calculation Rules</h2>
        </div>
        <div class="info-box">
            <div style="display: grid; gap: 10px;">
                <div><strong>🎯 INSTRUMENT1:</strong> Mean (average) of all price values × 1.05 multiplier</div>
                <div><strong>📅 INSTRUMENT2:</strong> Mean of November 2014 prices only × 1.1 multiplier</div>
                <div><strong>⚙️ INSTRUMENT3:</strong> Custom "on-the-fly" calculation × 1.15 multiplier</div>
                <div><strong>📈 INSTRUMENT4-7:</strong> Sum of the newest 10 price elements × respective multipliers</div>
                <div><strong>📊 Other instruments:</strong> Sum of newest 10 elements (no multiplier = ×1.0)</div>
            </div>
        </div>
    </div>



    <div class="card">
        <div class="card-header">
            <div class="card-icon">🗂️</div>
            <h2 class="card-title">Dataset Management</h2>
        </div>
        <div class="info-box">
            <p><strong>🚀 Generate test datasets on-demand with multithreaded optimization</strong></p>
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; margin: 15px 0; border-radius: 15px; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);">
                <div style="color: white; font-size: 18px; font-weight: 600; margin-bottom: 15px; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);">
                    ⚙️ Generator Type Selection
                </div>
                <div style="display: flex; gap: 15px; justify-content: center;">
                    <label style="display: flex; align-items: center; background: rgba(255,255,255,0.95); padding: 12px 20px; border-radius: 25px; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(0,0,0,0.1); font-weight: 500;" onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 6px 20px rgba(0,0,0,0.15)'" onmouseout="this.style.transform='translateY(0px)'; this.style.boxShadow='0 4px 15px rgba(0,0,0,0.1)'">
                        <input type="radio" name="datasetGeneratorType" value="original" checked style="width: 18px; height: 18px; margin-right: 10px; accent-color: #667eea;">
                        <span style="color: #333; font-size: 14px;">🔧 Original Generator</span>
                    </label>
                    <label style="display: flex; align-items: center; background: rgba(255,255,255,0.95); padding: 12px 20px; border-radius: 25px; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(0,0,0,0.1); font-weight: 500;" onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 6px 20px rgba(0,0,0,0.15)'" onmouseout="this.style.transform='translateY(0px)'; this.style.boxShadow='0 4px 15px rgba(0,0,0,0.1)'">
                        <input type="radio" name="datasetGeneratorType" value="optimized" style="width: 18px; height: 18px; margin-right: 10px; accent-color: #667eea;">
                        <span style="color: #333; font-size: 14px;">⚡ Optimized Generator</span>
                    </label>
                </div>
            </div>
        </div>
        <div class="button-group">
            <button onclick="generateDataset('SMALL')" class="btn btn-dataset" id="genSmall">Generate Small (1K)</button>
            <button onclick="generateDataset('MEDIUM')" class="btn btn-dataset medium" id="genMedium">Generate Medium (10K)</button>
            <button onclick="generateDataset('LARGE')" class="btn btn-dataset large" id="genLarge">Generate Large (100K)</button>
            <button onclick="generateDataset('XLARGE')" class="btn btn-dataset xlarge" id="genXlarge">Generate X-Large (1M)</button>
        </div>
        <div id="generationStatus"></div>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-icon">🔧</div>
            <h2 class="card-title">Technical Architecture</h2>
        </div>
        <div class="info-box">
            <div style="display: grid; gap: 8px;">
                <div><strong>🏗️ Architecture:</strong> Strategy Pattern with pluggable processing strategies</div>
                <div><strong>⚡ Processing Strategies:</strong> 10 different approaches with multithreaded optimization</div>
                <div><strong>🏭 Factory Pattern:</strong> Centralized strategy management with ProcessingStrategyFactory</div>
                <div><strong>⏱️ Performance Measurement:</strong> System.currentTimeMillis() timing</div>
                <div><strong>📄 Input Format:</strong> CSV with INSTRUMENT_NAME,DATE,VALUE</div>
                <div><strong>🗄️ Database:</strong> H2 in-memory database for price modifiers</div>
                <div><strong>🛡️ Memory Protection:</strong> Built-in memory monitoring (80% threshold)</div>
                <div><strong>🧵 Multithreading:</strong> Parallel instrument loading with 1000-line chunks</div>
            </div>
        </div>
    </div>

</div>
</body>
</html>