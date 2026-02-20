# PowerShell Interactive Test Menu

$BaseUrl = "http://localhost:8080"
$UserId = "1"

function Show-Menu {
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║       Excel Importer - Interaktives Test-Menu     ║" -ForegroundColor Cyan
    Write-Host "╚═══════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Verfügbare Tests:" -ForegroundColor Yellow
    Write-Host "  1️⃣  Länder importieren (clean)"
    Write-Host "  2️⃣  Athleten importieren (clean)"
    Write-Host "  3️⃣  Ergebnisse importieren (clean)"
    Write-Host "  4️⃣  Länder mit Fehlern importieren"
    Write-Host "  5️⃣  Athleten mit Fehlern importieren"
    Write-Host "  6️⃣  Ergebnisse mit Fehlern importieren"
    Write-Host "  7️⃣  Alle Tests nacheinander ausführen"
    Write-Host "  8️⃣  Test-Dateien neu erstellen"
    Write-Host "  9️⃣  Alle Tests mit detailliertem Output"
    Write-Host "  🔟  Datenbank-Status überprüfen"
    Write-Host "  0️⃣  Beenden"
    Write-Host ""
}

function Test-Server {
    try {
        $response = Invoke-WebRequest -Uri "$BaseUrl/api/imports/countries" `
            -Method Options `
            -ErrorAction Stop
        return $true
    } catch {
        return $false
    }
}

function Run-Test {
    param(
        [string]$TestName,
        [string]$Endpoint,
        [string]$FilePath
    )

    Write-Host ""
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Yellow
    Write-Host "[$TestName]" -ForegroundColor Yellow
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Yellow

    # Check file exists
    if (-not (Test-Path $FilePath)) {
        Write-Host "❌ Datei nicht gefunden: $FilePath" -ForegroundColor Red
        Write-Host "Tipp: Führe erst Option 8️⃣ aus um Test-Dateien zu erstellen"
        return
    }

    $Uri = "$BaseUrl$Endpoint"
    Write-Host "Endpoint: POST $Endpoint" -ForegroundColor Cyan
    Write-Host "Datei: $(Split-Path $FilePath -Leaf)" -ForegroundColor Cyan
    Write-Host ""

    try {
        $FilePath = Resolve-Path $FilePath

        Write-Host "🔄 Sende Request..." -ForegroundColor Blue

        $response = Invoke-WebRequest -Uri $Uri `
            -Method Post `
            -Form @{
                file = Get-Item -Path $FilePath
                userId = $UserId
            } `
            -ContentType "multipart/form-data" `
            -ErrorAction Stop

        # Parse JSON response
        $json = $response.Content | ConvertFrom-Json

        # Pretty print with colors
        Write-Host ""
        Write-Host "✅ Response erhalten (HTTP $($response.StatusCode))" -ForegroundColor Green
        Write-Host ""

        Write-Host "📊 Import Summary:" -ForegroundColor Cyan
        Write-Host "  Status:             " -NoNewline
        if ($json.status -eq "COMPLETED") {
            Write-Host "$($json.status)" -ForegroundColor Green
        } else {
            Write-Host "$($json.status)" -ForegroundColor Red
        }
        Write-Host "  Import Type:        $($json.importType)" -ForegroundColor Cyan
        Write-Host "  Filename:           $($json.filename)" -ForegroundColor Cyan
        Write-Host "  Total Records:      $($json.totalRecords)" -ForegroundColor Cyan
        Write-Host "  ✅ Successful:       $($json.successfulRecords)" -ForegroundColor Green
        Write-Host "  ❌ Failed:           $($json.failedRecords)" -ForegroundColor Red
        Write-Host "  Log ID:             $($json.importLogId)" -ForegroundColor Cyan
        Write-Host ""

        Write-Host "Message: $($json.message)" -ForegroundColor Yellow

        # Show errors if any
        if ($json.errors -and $json.errors.Count -gt 0) {
            Write-Host ""
            Write-Host "⚠️  Fehler Details:" -ForegroundColor Yellow
            Write-Host ""

            foreach ($error in $json.errors) {
                Write-Host "  Row $($error.rowNumber): $($error.errorCode)" -ForegroundColor Red
                Write-Host "    Message: $($error.errorMessage)"
                if ($error.fieldName) {
                    Write-Host "    Field: $($error.fieldName) = '$($error.fieldValue)'"
                }
                Write-Host ""
            }
        }

    } catch {
        Write-Host "❌ Fehler beim Test:" -ForegroundColor Red
        Write-Host "$($_)" -ForegroundColor Red

        if ($_.Exception.Response.StatusCode) {
            Write-Host "HTTP Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }

    Write-Host ""
}

function Create-TestFiles {
    Write-Host ""
    Write-Host "📁 Erstelle Test-Dateien..." -ForegroundColor Yellow
    Write-Host ""

    # Check if Python is available
    if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
        Write-Host "❌ Python nicht gefunden!" -ForegroundColor Red
        Write-Host "Bitte installiere Python oder erstelle die Dateien manuell."
        Write-Host "Siehe: TESTING_GUIDE.md"
        return
    }

    # Run Python script
    try {
        python create_test_files.py
        Write-Host ""
        Write-Host "✅ Test-Dateien erstellt!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Fehler beim Erstellen der Dateien:" -ForegroundColor Red
        Write-Host "$($_)" -ForegroundColor Red
    }
}

function Check-Database {
    Write-Host ""
    Write-Host "🔍 Überprüfe Datenbankverbindung..." -ForegroundColor Yellow
    Write-Host ""

    # This would require MySQL CLI, so we'll just show instructions
    Write-Host "Verbinde mit der Datenbank:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  mysql -h localhost -u user -p secret olympia"
    Write-Host ""
    Write-Host "Nützliche Abfragen:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  # Überprüfe Import-Logs:"
    Write-Host "  SELECT * FROM import_logs ORDER BY imported_at DESC;"
    Write-Host ""
    Write-Host "  # Überprüfe importierte Länder:"
    Write-Host "  SELECT * FROM countries;"
    Write-Host ""
    Write-Host "  # Überprüfe importierte Athleten:"
    Write-Host "  SELECT * FROM athletes;"
    Write-Host ""
    Write-Host "  # Überprüfe Import-Fehler:"
    Write-Host "  SELECT * FROM import_errors ORDER BY created_at DESC;"
    Write-Host ""
}

function Run-AllTests {
    Write-Host ""
    Write-Host "🔄 Führe alle Tests nacheinander aus..." -ForegroundColor Cyan
    Write-Host ""

    Run-Test "Test 1: Countries Import (clean)" "/api/imports/countries" "./test_data/countries_sample.xlsx"
    Start-Sleep -Seconds 2

    Run-Test "Test 2: Athletes Import (clean)" "/api/imports/athletes" "./test_data/athletes_sample.xlsx"
    Start-Sleep -Seconds 2

    Run-Test "Test 3: Results Import (clean)" "/api/imports/results" "./test_data/results_sample.xlsx"
    Start-Sleep -Seconds 2

    Run-Test "Test 4: Countries with Errors" "/api/imports/countries" "./test_data/countries_with_errors.xlsx"

    Write-Host ""
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
    Write-Host "✅ Alle Tests abgeschlossen!" -ForegroundColor Green
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
}

function Run-AllTestsVerbose {
    Write-Host ""
    Write-Host "🔄 Führe alle Tests mit detailliertem Output aus..." -ForegroundColor Cyan
    Write-Host ""

    # Check file exists first
    $testFiles = @(
        "./test_data/countries_sample.xlsx",
        "./test_data/athletes_sample.xlsx",
        "./test_data/results_sample.xlsx",
        "./test_data/countries_with_errors.xlsx"
    )

    foreach ($file in $testFiles) {
        if (-not (Test-Path $file)) {
            Write-Host "❌ Datei nicht gefunden: $file" -ForegroundColor Red
            Write-Host "Bitte erstelle erst die Test-Dateien (Option 8️⃣)" -ForegroundColor Yellow
            return
        }
    }

    Run-AllTests
}

# Main loop
do {
    # Clear screen (optional)
    # Clear-Host

    Show-Menu

    # Check server
    Write-Host "Server-Status: " -NoNewline
    if (Test-Server) {
        Write-Host "✅ Erreichbar" -ForegroundColor Green
    } else {
        Write-Host "❌ Nicht erreichbar (http://localhost:8080)" -ForegroundColor Red
        Write-Host "Starte die App: ./gradlew bootRun" -ForegroundColor Yellow
    }
    Write-Host ""

    $choice = Read-Host "Wähle eine Option (0-9)"

    switch ($choice) {
        "1" { Run-Test "Test 1: Countries Import" "/api/imports/countries" "./test_data/countries_sample.xlsx" }
        "2" { Run-Test "Test 2: Athletes Import" "/api/imports/athletes" "./test_data/athletes_sample.xlsx" }
        "3" { Run-Test "Test 3: Results Import" "/api/imports/results" "./test_data/results_sample.xlsx" }
        "4" { Run-Test "Test 4: Countries with Errors" "/api/imports/countries" "./test_data/countries_with_errors.xlsx" }
        "5" { Run-Test "Test 5: Athletes with Errors" "/api/imports/athletes" "./test_data/athletes_with_errors.xlsx" }
        "6" { Run-Test "Test 6: Results with Errors" "/api/imports/results" "./test_data/results_with_errors.xlsx" }
        "7" { Run-AllTests }
        "8" { Create-TestFiles }
        "9" { Run-AllTestsVerbose }
        "10" { Check-Database }
        "0" {
            Write-Host ""
            Write-Host "👋 Auf Wiedersehen!" -ForegroundColor Cyan
            Write-Host ""
            exit
        }
        default { Write-Host "❌ Ungültige Option" -ForegroundColor Red }
    }

    Write-Host ""
    Write-Host "Drücke Enter um fortzufahren..." -ForegroundColor Gray
    Read-Host | Out-Null

} while ($true)

