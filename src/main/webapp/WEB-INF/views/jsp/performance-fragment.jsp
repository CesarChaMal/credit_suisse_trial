<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="performance-grid">
    <div class="metric-card">
        <div class="metric-label">🚀 Parallel Stream</div>
        <div class="metric-value">${parallelStreamTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🔄 RxJava Reactive</div>
        <div class="metric-value">${rxJavaTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">📦 Spring Batch</div>
        <div class="metric-value">${batchTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🔧 Manual Batch</div>
        <div class="metric-value">${manualBatchTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🐌 Single Thread</div>
        <div class="metric-value">${singleThreadTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🎯 Functional</div>
        <div class="metric-value">${functionalTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">⚡ CompletableFuture</div>
        <div class="metric-value">${asyncTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🌊 Kafka Stream</div>
        <div class="metric-value">${kafkaTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🎆 Modern Java 24</div>
        <div class="metric-value">${modernJavaTime}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">🏢 Spring Batch</div>
        <div class="metric-value">${springBatchTime}</div>
    </div>
</div>
<div class="info-box">
    <div class="status-success">${performanceComparison}</div>
</div>