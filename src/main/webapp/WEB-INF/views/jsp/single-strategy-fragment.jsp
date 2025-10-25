<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test="${not empty error}">
        <div class="info-box">
            <div class="status-error">${error}</div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="metric-card">
            <div class="metric-label">✅ ${strategyName}</div>
            <div class="metric-value">${executionTime}</div>
        </div>
        <div class="info-box">
            <div class="status-success">
                <strong>📊 File:</strong> ${fileName} | <strong>📄 Records:</strong> ${recordCount} | 
                <strong>⚡ Performance:</strong> ${executionTime} | <strong>✅ Status:</strong> Completed
            </div>
        </div>
    </c:otherwise>
</c:choose>