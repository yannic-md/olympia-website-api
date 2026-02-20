# PowerShell Test Script für Excel Importer
# Dieses Skript testet alle drei Import-Endpunkte

$BaseUrl = "http://localhost:8080"
$UserId = "1"

function Test-Import {
    param(
        [string]$TestName,
        [string]$Endpoint,
        [string]$FilePath
    )

    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Yellow
    Write-Host "[TEST] $TestName" -ForegroundColor Yellow
    Write-Host "=========================================" -ForegroundColor Yellow
    Write-Host "Endpoint: POST $Endpoint"
    Write-Host "Datei: $FilePath"
    Write-Host ""

    $Uri = "$BaseUrl$Endpoint"

    # Erstelle MultipartForm
    $FilePath = Resolve-Path $FilePath -ErrorAction Stop

    try {
        $response = Invoke-WebRequest -Uri $Uri `
            -Method Post `
            -Form @{
                file = Get-Item -Path $FilePath
                userId = $UserId
            } `
            -ContentType "multipart/form-data" `
            -ErrorAction Stop

        Write-Host "Status: " -NoNewline
        Write-Host "✓ $($response.StatusCode)" -ForegroundColor Green
        Write-Host ""

        # Parse und pretty-print JSON
        $json = $response.Content | ConvertFrom-Json
        Write-Host ($json | ConvertTo-Json -Depth 10) -ForegroundColor Green

    } catch {
        Write-Host "Status: " -NoNewline
        Write-Host "✗ Fehler" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "╔═════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Excel Importer - Test Script           ║" -ForegroundColor Cyan
Write-Host "║  Stellen Sie sicher, dass die App läuft ║" -ForegroundColor Cyan
Write-Host "╚═════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Test 1: Import Countries
Test-Import -TestName "Länder importieren" `
    -Endpoint "/api/imports/countries" `
    -FilePath "./test_data/countries_sample.xlsx"

# Test 2: Import Athletes
Test-Import -TestName "Athleten importieren" `
    -Endpoint "/api/imports/athletes" `
    -FilePath "./test_data/athletes_sample.xlsx"

# Test 3: Import Results
Test-Import -TestName "Ergebnisse importieren" `
    -Endpoint "/api/imports/results" `
    -FilePath "./test_data/results_sample.xlsx"

# Test 4: Import mit Fehlern
Test-Import -TestName "Länder mit Fehlern importieren" `
    -Endpoint "/api/imports/countries" `
    -FilePath "./test_data/countries_with_errors.xlsx"

Write-Host ""
Write-Host "=========================================" -ForegroundColor Yellow
Write-Host "Tests abgeschlossen!" -ForegroundColor Yellow
Write-Host "=========================================" -ForegroundColor Yellow
Write-Host ""

