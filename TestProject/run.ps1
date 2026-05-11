# Apache Maven Build Skipper
# This project requires Maven to build and run.
# Please install Apache Maven from https://maven.apache.org/download.cgi
# After installation, ensure 'mvn' is in your PATH, then run:
#   mvn clean javafx:run

Write-Host "Maven is not installed or not in PATH." -ForegroundColor Red
Write-Host ""
Write-Host "To run this JavaFX application:" -ForegroundColor Yellow
Write-Host "1. Install Apache Maven from: https://maven.apache.org/download.cgi" -ForegroundColor White
Write-Host "2. Add Maven to your PATH environment variable" -ForegroundColor White
Write-Host "3. Run: mvn clean javafx:run" -ForegroundColor White
Write-Host ""
Write-Host "Alternatively, use your IDE (IntelliJ IDEA, Eclipse, VS Code) to run MainApp.java" -ForegroundColor Yellow
Write-Host ""
Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
