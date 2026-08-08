@echo off
setlocal
cd /d "%~dp0"
if not exist ".github\workflows" mkdir ".github\workflows"
copy /Y "WORKFLOW-ANDROID-COPIA-VISIVEL.yml" ".github\workflows\compilar-android-apk.yml" >nul
echo.
echo OK - workflow criado em:
echo .github\workflows\compilar-android-apk.yml
echo.
echo Confira tambem se index.html, styles.css, app.js, build.gradle e settings.gradle estao na pasta.
pause
