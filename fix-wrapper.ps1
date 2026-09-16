# Fix missing gradle-wrapper.jar
# Run this in PowerShell from project root if you get:
# "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"

$wrapperUrl = "https://github.com/gradle/gradle/raw/v8.11.1/gradle/wrapper/gradle-wrapper.jar"
$wrapperPath = "gradle/wrapper/gradle-wrapper.jar"

Write-Host "Downloading gradle-wrapper.jar from $wrapperUrl ..."
New-Item -ItemType Directory -Force -Path "gradle/wrapper" | Out-Null

try {
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperPath -UseBasicParsing
    Write-Host "Downloaded $wrapperPath ($((Get-Item $wrapperPath).Length) bytes)" -ForegroundColor Green
} catch {
    Write-Host "Failed to download via Invoke-WebRequest, trying alternative..." -ForegroundColor Yellow
    # Alternative: use gradle command if available
    if (Get-Command gradle -ErrorAction SilentlyContinue) {
        Write-Host "Found gradle in PATH, running 'gradle wrapper --gradle-version 8.11.1' ..."
        gradle wrapper --gradle-version 8.11.1
    } else {
        Write-Host "Please download manually:" -ForegroundColor Red
        Write-Host "1. Open https://github.com/gradle/gradle/raw/v8.11.1/gradle/wrapper/gradle-wrapper.jar in browser"
        Write-Host "2. Save as $wrapperPath"
        Write-Host "3. Or run: gradle wrapper --gradle-version 8.11.1 (if you have Gradle installed)"
    }
}

Write-Host "`nNow try: ./gradlew assembleDebug"
