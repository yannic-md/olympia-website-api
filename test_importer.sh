#!/bin/bash
# Test Script für Excel Importer
# Dieses Skript testet alle drei Import-Endpunkte

BASE_URL="http://localhost:8080"
USER_ID="1"

echo "=========================================="
echo "Excel Importer - Test Script"
echo "=========================================="
echo ""

# Farben für Output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Import Countries
echo -e "${YELLOW}[TEST 1]${NC} Importiere Länder aus Excel-Datei"
echo "Endpoint: POST /api/imports/countries"
echo ""

curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test_data/countries_sample.xlsx" \
  -F "userId=$USER_ID" \
  "$BASE_URL/api/imports/countries" \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | jq '.'

echo ""
echo "=========================================="
echo ""

# Test 2: Import Athletes
echo -e "${YELLOW}[TEST 2]${NC} Importiere Athleten aus Excel-Datei"
echo "Endpoint: POST /api/imports/athletes"
echo ""

curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test_data/athletes_sample.xlsx" \
  -F "userId=$USER_ID" \
  "$BASE_URL/api/imports/athletes" \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | jq '.'

echo ""
echo "=========================================="
echo ""

# Test 3: Import Results
echo -e "${YELLOW}[TEST 3]${NC} Importiere Ergebnisse aus Excel-Datei"
echo "Endpoint: POST /api/imports/results"
echo ""

curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test_data/results_sample.xlsx" \
  -F "userId=$USER_ID" \
  "$BASE_URL/api/imports/results" \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | jq '.'

echo ""
echo "=========================================="
echo ""

# Test 4: Test mit fehlerhaften Daten
echo -e "${YELLOW}[TEST 4]${NC} Importiere Datei mit Fehlern"
echo "Endpoint: POST /api/imports/countries"
echo ""

curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test_data/countries_with_errors.xlsx" \
  -F "userId=$USER_ID" \
  "$BASE_URL/api/imports/countries" \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | jq '.'

echo ""
echo "=========================================="
echo "Tests abgeschlossen!"
echo "=========================================="

