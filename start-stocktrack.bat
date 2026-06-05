@echo off
setlocal

set "ROOT_DIR=%~dp0"
cd /d "%ROOT_DIR%"

call "%ROOT_DIR%mvnw.cmd" -q -f "%ROOT_DIR%StockTrack\pom.xml" javafx:run

endlocal
