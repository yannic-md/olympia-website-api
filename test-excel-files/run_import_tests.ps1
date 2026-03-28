# PowerShell Test-Skript für Excel/CSV Importer
# Verwendet die Test-Dateien mit 100% funktionierenden Daten

Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host "  Olympia API - Import Functionality Test Script" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host ""

# Konfiguration
$ApiUrl = "http://localhost:8080"
$Username = "admin"
$Password = "admin"
$TestDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Credential = New-Object System.Management.Automation.PSCredential($Username, (ConvertTo-SecureString $Password -AsPlainText -Force))

Write-Host "Test-Verzeichnis: $TestDir" -ForegroundColor Yellow
Write-Host "API URL: $ApiUrl" -ForegroundColor Yellow
Write-Host ""

# Hilfsfunktion für Import
function Import-TestFile {
    param(
        [string]$FilePath,
        [string]$ImportType,
        [int]$TestNumber
    )

    $FileName = Split-Path -Leaf $FilePath
    Write-Host "=========================================================" -ForegroundColor Cyan
    Write-Host "Test $TestNumber`: Importing $FileName ($ImportType)" -ForegroundColor Cyan
    Write-Host "=========================================================" -ForegroundColor Cyan

    $Url = "$ApiUrl/api/imports/$ImportType"

    try {
        $Response = Invoke-WebRequest -Uri $Url `
            -Method Post `
            -Credential $Credential `
            -Form @{file = Get-Item -Path $FilePath} `
            -UseBasicParsing

        $JsonResponse = $Response.Content | ConvertFrom-Json

        Write-Host "Status: $($JsonResponse.status)" -ForegroundColor Green
        Write-Host "Total Records: $($JsonResponse.totalRecords)" -ForegroundColor White
        Write-Host "Successful: $($JsonResponse.successfulRecords)" -ForegroundColor Green
        Write-Host "Failed: $($JsonResponse.failedRecords)" -ForegroundColor Yellow
        Write-Host "Message: $($JsonResponse.message)" -ForegroundColor White

        if ($JsonResponse.errors -and $JsonResponse.errors.Count -gt 0) {
            Write-Host "Errors:" -ForegroundColor Red
            $JsonResponse.errors | ForEach-Object {
                Write-Host "  Row $($_.rowNumber): $($_.errorMessage)" -ForegroundColor Red
            }
        }
    }
    catch {
        Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host ""
    Start-Sleep -Seconds 1
}

# Test 1: Countries CSV
Import-TestFile -FilePath "$TestDir\countries_test.csv" -ImportType "countries" -TestNumber 1

# Test 2: Countries Excel
Import-TestFile -FilePath "$TestDir\countries_test.xlsx" -ImportType "countries" -TestNumber 2

# Test 3: Athletes CSV
Import-TestFile -FilePath "$TestDir\athletes_test.csv" -ImportType "athletes" -TestNumber 3

# Test 4: Athletes Excel
Import-TestFile -FilePath "$TestDir\athletes_test.xlsx" -ImportType "athletes" -TestNumber 4

# Test 5: Results CSV
Import-TestFile -FilePath "$TestDir\results_test.csv" -ImportType "results" -TestNumber 5

# Test 6: Results Excel
Import-TestFile -FilePath "$TestDir\results_test.xlsx" -ImportType "results" -TestNumber 6

Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan

