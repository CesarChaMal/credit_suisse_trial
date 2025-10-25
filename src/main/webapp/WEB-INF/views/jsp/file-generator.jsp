<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>File Generator - Credit Suisse Trial</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
        .form-group { margin: 15px 0; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type=radio] { display: inline-block !important; margin-right: 6px; }
        label { display: inline-block; margin-right: 12px; font-weight: normal; }
        .form-group select { padding: 8px; width: 300px; }
        .btn { padding: 10px 20px; background: #007acc; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .btn:hover { background: #005a99; }
        .success { color: #28a745; font-weight: bold; padding: 10px; background: #d4edda; border-radius: 5px; }
        .error { color: #dc3545; font-weight: bold; padding: 10px; background: #f8d7da; border-radius: 5px; }
        .file-options { margin: 20px 0; }
        .file-option { margin: 10px 0; padding: 10px; background: #f8f9fa; border-radius: 5px; }
        .load-btn { background: #28a745; margin-left: 10px; }
        .load-btn:hover { background: #1e7e34; }
        .comparison-section { display: flex; gap: 20px; margin: 20px 0; }
        .generator-info { flex: 1; padding: 15px; background: #f8f9fa; border-radius: 5px; }
        .generator-info h3 { color: #007acc; margin-top: 0; }
        .benchmark-section { margin: 20px 0; padding: 15px; background: #fff3cd; border-radius: 5px; }
        #generateBtn:disabled { background: #6c757d; cursor: not-allowed; }
    </style>
</head>
<body>
<h1>Instrument File Generator</h1>

<div class="section">
    <h2>Generate Test Files</h2>
    <p>Generate instrument data files of different sizes for performance testing.</p>
    
    <form id="fileGenForm" method="post" action="${pageContext.request.contextPath}/generate" onsubmit="return validateForm()">
        <div class="form-group" style="background: linear-gradient(135deg, #007acc 0%, #005a99 100%); padding: 20px; margin: 15px 0; border-radius: 15px; box-shadow: 0 8px 25px rgba(0, 122, 204, 0.3);">
            <div style="color: white; font-size: 18px; font-weight: 600; margin-bottom: 15px; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);">
                ⚙️ Generator Type Selection
            </div>
            <div style="display: flex; gap: 15px; justify-content: center;">
                <label style="display: flex; align-items: center; background: rgba(255,255,255,0.95); padding: 12px 20px; border-radius: 25px; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(0,0,0,0.1); font-weight: 500;" onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 6px 20px rgba(0,0,0,0.15)'" onmouseout="this.style.transform='translateY(0px)'; this.style.boxShadow='0 4px 15px rgba(0,0,0,0.1)'">
                    <input type="radio" name="generatorType" value="original" checked style="width: 18px; height: 18px; margin-right: 10px; accent-color: #007acc;">
                    <span style="color: #333; font-size: 14px;">🔧 Original Generator</span>
                </label>
                <label style="display: flex; align-items: center; background: rgba(255,255,255,0.95); padding: 12px 20px; border-radius: 25px; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(0,0,0,0.1); font-weight: 500;" onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 6px 20px rgba(0,0,0,0.15)'" onmouseout="this.style.transform='translateY(0px)'; this.style.boxShadow='0 4px 15px rgba(0,0,0,0.1)'">
                    <input type="radio" name="generatorType" value="optimized" style="width: 18px; height: 18px; margin-right: 10px; accent-color: #007acc;">
                    <span style="color: #333; font-size: 14px;">⚡ Optimized Generator</span>
                </label>
            </div>
        </div>
        
        <div class="form-group">
            <label for="fileSize">File Size:</label>
            <select id="fileSize" name="fileSize" required>
                <option value="">-- Select File Size --</option>
                <option value="SMALL">Small (1K records)</option>
                <option value="MEDIUM">Medium (10K records)</option>
                <option value="LARGE">Large (100K records)</option>
                <option value="XLARGE">X-Large (1M records)</option>
            </select>
        </div>
        
        <input type="hidden" name="optimized" id="optimizedFlag" value="false">
        
        <button type="submit" class="btn" id="generateBtn" disabled>Generate File</button>
    </form>
    
    <c:if test="${not empty success}">
        <div class="success">${success}</div>
        <div class="info-box">
            <strong>📁 File Location:</strong> <code>src/main/resources/${generatedFile}</code>
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
</div>

<c:if test="${not empty generatedFile}">
<div class="section">
    <h2>File Generated Successfully</h2>
    <p>Your file <strong>${generatedFile}</strong> has been generated and is ready for testing.</p>
    
    <form method="get" action="/">
        <input type="hidden" name="inputFile" value="${generatedFile}">
        <button type="submit" class="btn load-btn">Load File & Run Performance Test</button>
    </form>
</div>
</c:if>

<div class="section">
    <h2>Generator Comparison</h2>
    <div class="comparison-section">
        <div class="generator-info">
            <h3>Original Generator</h3>
            <ul>
                <li>Traditional algorithm with console output</li>
                <li>Sequential processing</li>
                <li>Good for small to medium files</li>
            </ul>
        </div>
        <div class="generator-info">
            <h3>Optimized Generator</h3>
            <ul>
                <li>Modern Java 8+ features</li>
                <li>Parallel processing for large files</li>
                <li>Pre-computed constants</li>
                <li>Up to 70% faster generation</li>
            </ul>
        </div>
    </div>
    
    <div class="benchmark-section">
        <h3>Performance Benchmark</h3>
        <p>Compare both generators with identical 100K record files:</p>
        <a href="/benchmark" class="btn" style="background: #ffc107; color: black;">Run Benchmark</a>
    </div>
    
    <c:if test="${not empty benchmarkSuccess}">
        <div class="success">${benchmarkSuccess}</div>
    </c:if>
    
    <c:if test="${not empty benchmarkError}">
        <div class="error">${benchmarkError}</div>
    </c:if>
</div>

<div class="section">
    <p><a href="/">← Back to Performance Comparison</a></p>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    // Debug logging
    console.log('fileSize:', document.getElementById('fileSize'));
    console.log('options:', document.getElementById('fileSize')?.options);
    
    const generateBtn = document.getElementById('generateBtn');
    const sizeSelect = document.getElementById('fileSize');
    
    if (sizeSelect && generateBtn) {
        sizeSelect.addEventListener('change', () => {
            generateBtn.disabled = !sizeSelect.value;
        });
    }
});

function validateForm() {
    const generatorType = document.querySelector('input[name="generatorType"]:checked')?.value;
    const fileSize = document.getElementById('fileSize').value;
    
    if (!generatorType) {
        alert('Please select a generator type');
        return false;
    }
    
    if (!fileSize) {
        alert('Please select a file size');
        return false;
    }
    
    return true;
}
</script>

<script>
  document.addEventListener('DOMContentLoaded', () => { updateFileSizes(); });
</script>

<script>
  document.addEventListener('change', e => {
    if (e.target.name === 'generatorType') {
      let hidden = document.querySelector('input[name="optimized"]');
      if (!hidden) {
        hidden = Object.assign(document.createElement('input'), {type:'hidden', name:'optimized'});
        e.target.form.appendChild(hidden);
      }
      hidden.value = (e.target.value === 'optimized');
      updateFileSizes();
    }
  });
</script>

</body>
</html>