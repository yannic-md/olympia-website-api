@echo off
REM Test-Skript für Excel/CSV Importer
REM Verwendet die Test-Dateien mit 100%% funktionierenden Daten

echo.
echo ====================================================
echo   Olympia API - Import Functionality Test Script
echo ====================================================
echo.

REM Konfiguration
set API_URL=http://localhost:8080
set USERNAME=admin
set PASSWORD=admin
set TEST_DIR=%~dp0

echo Test-Verzeichnis: %TEST_DIR%
echo.

REM Funktion zum Importieren
echo ====================================================
echo Test 1: Countries importieren (CSV)
echo ====================================================
curl -X POST %API_URL%/api/imports/countries ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%countries_test.csv" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

timeout /t 2

echo ====================================================
echo Test 2: Countries importieren (Excel)
echo ====================================================
curl -X POST %API_URL%/api/imports/countries ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%countries_test.xlsx" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

timeout /t 2

echo ====================================================
echo Test 3: Athletes importieren (CSV)
echo ====================================================
echo HINWEIS: Some athletes may fail if countries don't exist
echo.
curl -X POST %API_URL%/api/imports/athletes ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%athletes_test.csv" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

timeout /t 2

echo ====================================================
echo Test 4: Athletes importieren (Excel)
echo ====================================================
curl -X POST %API_URL%/api/imports/athletes ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%athletes_test.xlsx" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

timeout /t 2

echo ====================================================
echo Test 5: Results importieren (CSV)
echo ====================================================
curl -X POST %API_URL%/api/imports/results ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%results_test.csv" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

timeout /t 2

echo ====================================================
echo Test 6: Results importieren (Excel)
echo ====================================================
curl -X POST %API_URL%/api/imports/results ^
  -u %USERNAME%:%PASSWORD% ^
  -F "file=@%TEST_DIR%results_test.xlsx" ^
  -H "Content-Type: multipart/form-data"
echo.
echo.

echo ====================================================
echo All tests completed!
echo ====================================================

