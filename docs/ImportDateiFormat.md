# 📋 Import File Format Guide

## 📖 Overview

This document describes the exact structure and requirements for files to be imported into the Olympia Website system. Three data types are supported:

1. **Countries**
2. **Athletes**
3. **Results**

---

## ⚠️ Important: Import Order

The import order is **CRITICAL**. Please follow this order:

```
1️⃣  COUNTRIES ← FIRST
2️⃣  ATHLETES ← SECOND
3️⃣  RESULTS ← LAST
```

**Reason:** Athletes need existing countries, and results need existing athletes.

---

## 📁 Supported File Formats

- ✅ **Excel**: `.xlsx`, `.xls`
- ✅ **CSV**: `.csv` (with comma separator)

---

## 1️⃣ Importing Countries

### 📋 Column Structure

| Column | Name | Data Type | Required | Description |
|--------|------|-----------|----------|-------------|
| A | `code` | String (2-3 chars) | ✅ Yes | Country code (e.g. "us", "de", "fr") - **Case-insensitive** |
| B | `name` | String | ✅ Yes | Full country name (e.g. "United States") |

### 📝 Example File (countries.csv)

```csv
code,name
us,United States
de,Germany
fr,France
gb,Great Britain
jp,Japan
cn,China
au,Australia
ca,Canada
it,Italy
es,Spain
```

### 📌 Rules & Requirements

| Rule | Description |
|------|-------------|
| **Uniqueness** | The `code` must be unique - duplicates are rejected |
| **Format** | The `code` is automatically converted to **lowercase** |
| **Length** | `code` should be 2-3 characters long |
| **Name** | Must not be empty |
| **Header** | First line MUST contain column names (`code`, `name`) |

### ❌ Common Errors

| Error | Reason | Solution |
|-------|--------|----------|
| `DUPLICATE_COUNTRY` | Country code already exists | Check code or remove duplicates |
| `INVALID_DATA` | Name is empty | Ensure all countries have a name |
| `MISSING_HEADER` | Header line missing | First line must contain `code,name` |

### ✅ Successful Response

```json
{
  "importLogId": 1,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "filename": "countries.csv",
  "totalRecords": 10,
  "successfulRecords": 10,
  "failedRecords": 0,
  "message": "Import completed. Success: 10, Failed: 0"
}
```

---

## 2️⃣ Importing Athletes

### 📋 Column Structure

| Column | Name | Data Type | Required | Description |
|--------|------|-----------|----------|-------------|
| A | `firstName` | String | ✅ Yes | Athlete's first name |
| B | `lastName` | String | ✅ Yes | Athlete's last name |
| C | `countryCode` | String (2-3 chars) | ✅ Yes | Country code (must exist in Countries) - **Case-insensitive** |
| D | `gender` | String (M/F or m/f) | ❌ Optional | Gender: `M` (Male) or `F` (Female) |

### 📝 Example File (athletes.csv)

```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
Petra,Vlhova,sk,F
Alexis,Pinturault,fr,M
Sofia,Goggia,it,F
Johannes,Boe,no,M
Marte,Roeiseland,no,F
Nathan,Chen,us,M
Yuzuru,Hanyu,jp,M
Ireen,Wust,nl,F
```

### 📌 Rules & Requirements

| Rule | Description |
|------|-------------|
| **Country Code** | The `countryCode` must exist in the Countries table, otherwise the import fails |
| **Uniqueness** | The combination name (firstName + lastName) should be unique but is not technically enforced |
| **Gender** | Only `M` or `F` accepted. Other values or empty values are allowed |
| **Header** | First line must contain column names: `firstName`, `lastName`, `countryCode` (and optionally `gender`) |
| **Names** | Must not be empty |
| **Case-Insensitivity** | Country code is automatically converted to lowercase |

### ❌ Common Errors

| Error | Reason | Solution |
|-------|--------|----------|
| `ATHLETE_COUNTRY_NOT_FOUND` | Country code does not exist | Ensure the country was imported first |
| `INVALID_DATA` | Name is empty | Both names (firstName, lastName) must be provided |
| `MISSING_HEADER` | Header line missing | First line must contain column names |
| `INVALID_GENDER` | Invalid gender format | Only use `M` or `F` (or leave field empty) |

### ⚠️ Important Note

**Countries must exist BEFORE importing athletes!** Otherwise the import will fail with `ATHLETE_COUNTRY_NOT_FOUND`.

### ✅ Successful Response

```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes.csv",
  "totalRecords": 10,
  "successfulRecords": 10,
  "failedRecords": 0,
  "message": "Import completed. Success: 10, Failed: 0"
}
```

---

## 3️⃣ Importing Results

### 📋 Column Structure

| Column | Name | Data Type | Required | Description |
|--------|------|-----------|----------|-------------|
| A | `athleteFirstName` | String | ✅ Yes | Athlete's first name (must exist in Athletes) |
| B | `athleteLastName` | String | ✅ Yes | Athlete's last name (must exist in Athletes) |
| C | `sport` | String | ✅ Yes | Sport name (e.g. "Alpine Skiing", "Biathlon") |
| D | `rank` | Number (Integer) | ✅ Yes | Ranking (e.g. 1, 2, 3) - must be ≥ 1 |
| E | `timeOrPoints` | String/Number | ✅ Yes | Time or points (e.g. "1:32.03" or "314.56") |
| F | `scoreType` | String (PTS/WINS/TIME) | ❌ Optional | Score type: `PTS` (Points), `WINS` (Wins), `TIME` (Time) |
| G | `medal` | String (GOLD/SILVER/BRONZE) | ❌ Optional | Medal type: `GOLD`, `SILVER`, `BRONZE` or empty |

### 📝 Example File (results.csv)

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Petra,Vlhova,Alpine Skiing,3,1:32.88,TIME,BRONZE
Johannes,Boe,Biathlon,1,23:45.2,TIME,GOLD
Marte,Roeiseland,Biathlon,2,20:23.1,TIME,SILVER
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
Yuzuru,Hanyu,Figure Skating,2,312.45,PTS,SILVER
Ireen,Wust,Speed Skating,1,1:43.50,TIME,GOLD
```

### 📌 Rules & Requirements

| Rule | Description |
|------|-------------|
| **Athlete Existence** | The athlete (with firstName + lastName) must exist in the Athletes table |
| **Ranking** | The `rank` must be a positive integer (≥ 1) |
| **Time/Points Format** | Can have various formats: `1:32.03` (time), `314.56` (points), `23:45.2` (time with ms) |
| **ScoreType** | Only `PTS`, `WINS` or `TIME` accepted. Optional, default is no specification |
| **Medal** | Only `GOLD`, `SILVER`, `BRONZE` or empty. **Case-sensitive!** |
| **Header** | First line must contain column names |
| **Sport** | Sport name is saved as specified - spelling is important |

### ✅ Valid Time Formats

```
"1:32.03"       → MM:SS.MS (Minutes:Seconds.Milliseconds)
"32.03"         → SS.MS (Seconds.Milliseconds)
"1:02:30.5"     → HH:MM:SS.MS (Hours:Minutes:Seconds.Milliseconds)
"2:30"          → MM:SS
```

### ✅ Valid Points Formats

```
"314.56"        → Decimal number
"100"           → Integer
"3.5"           → Decimal number with comma
```

### ❌ Common Errors

| Error | Reason | Solution |
|-------|--------|----------|
| `ATHLETE_NOT_FOUND` | Athlete does not exist | Ensure athletes were imported and names are correct |
| `INVALID_NUMBER_FORMAT` | rank or timeOrPoints formatted incorrectly | Rank must be integer, timeOrPoints can be time or number |
| `INVALID_RANK` | Rank is ≤ 0 | rank must be ≥ 1 |
| `INVALID_MEDAL` | Invalid medal format | Only `GOLD`, `SILVER`, `BRONZE` (uppercase) |
| `INVALID_SCORE_TYPE` | Invalid scoreType | Only `PTS`, `WINS`, `TIME` (uppercase) |

### ⚠️ Important Notes

1. **Athletes must exist BEFORE importing results!** Otherwise the import will fail with `ATHLETE_NOT_FOUND`.

2. **Names must match exactly!** (Case-sensitive)
   - Correct: ✅ `Sofia` + `Goggia`
   - Wrong: ❌ `sofia` + `goggia` or `Sofia` + `goggia`

3. **Sports** - Are saved as specified. Use consistent spelling:
   - Correct: ✅ All use "Alpine Skiing"
   - Wrong: ❌ Mix of "Alpine Skiing", "alpine skiing", "AlpineSKIING"

### ✅ Successful Response

```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "filename": "results.csv",
  "totalRecords": 8,
  "successfulRecords": 8,
  "failedRecords": 0,
  "message": "Import completed. Success: 8, Failed: 0"
}
```

---

## 🚀 Step-by-Step Import Guide

### Step 1: Prepare & Import Countries

```
1. Create or provide "countries.csv" file
2. Ensure columns are correct: code, name
3. In Admin Dashboard: Upload file
4. Start import and wait for confirmation
✅ Countries should now exist in database
```

### Step 2: Prepare & Import Athletes

```
1. Create or provide "athletes.csv" file
2. Ensure columns are correct: firstName, lastName, countryCode
3. Verify all countryCode values exist in Countries
4. In Admin Dashboard: Upload file
5. Start import and wait for confirmation
✅ Athletes should now exist in database
```

### Step 3: Prepare & Import Results

```
1. Create or provide "results.csv" file
2. Ensure columns are correct: athleteFirstName, athleteLastName, sport, rank, timeOrPoints
3. Verify all athleteFirstName + athleteLastName combinations exist in Athletes
4. In Admin Dashboard: Upload file
5. Start import and wait for confirmation
✅ Results should now exist in database
```

---

## 🛠️ Troubleshooting

### Problem: "Import failed with 0 successful records"

**Step 1:** Check import logs in Admin Dashboard
```
Look for: Import Errors → Click on failed import log
```

**Step 2:** Read failed rows
```
Each failed row is listed with the exact error
```

**Step 3:** Fix the error
```
- COUNTRY_NOT_FOUND? → Import countries first
- ATHLETE_NOT_FOUND? → Import athletes first
- Format error? → See column format above
```

### Problem: "Some rows failed, others successful"

```
✅ This is normal - successfully imported rows are saved
❌ Failed rows are not imported
→ Fix errors in failed rows
→ Import them again
```

### Problem: "Cache shows old data after import"

```
1. Wait 1-2 minutes (cache updates automatically)
   or
2. Refresh page (F5 or Ctrl+R)
   or
3. Contact admin for manual cache clear
```

---

## 📊 Example: Complete Import Workflow

Here is an example of a complete import workflow with real data:

### 📁 File 1: countries.csv
```csv
code,name
de,Germany
ch,Switzerland
at,Austria
```

### 📁 File 2: athletes.csv
```csv
firstName,lastName,countryCode,gender
Anna,Fenninger,at,F
Marcel,Hirscher,at,M
Lindsey,Vonn,us,F
```

### 📁 File 3: results.csv
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Anna,Fenninger,Alpine Skiing,1,1:45.23,TIME,GOLD
Marcel,Hirscher,Alpine Skiing,2,1:45.67,TIME,SILVER
Lindsey,Vonn,Alpine Skiing,3,1:46.12,TIME,BRONZE
```

### 🎯 Import Process:
```
1. Import countries.csv → 3 countries successful
2. Import athletes.csv → 3 athletes successful (error for "us" since USA not in countries.csv)
3. Import results.csv → 3 results successful
```

---

## 📞 Frequently Asked Questions

### Q: Can I import the same file twice?
**A:** No, for Countries it will result in duplicate errors. For Athletes and Results new entries are created.

### Q: Can I use Excel instead of CSV?
**A:** Yes, `.xlsx` and `.xls` files are accepted the same as CSV.

### Q: Can I change the column order?
**A:** No, column order is fixed. Columns must be in the specified order, but can have the same names.

### Q: Are country codes case-sensitive?
**A:** The country code (`code`) is automatically converted to lowercase, the name is saved as entered.

### Q: Are special characters like umlauts (ä, ö, ü) supported?
**A:** Yes, umlauts and special characters are fully supported and preserved.

### Q: Can I have empty rows in the file?
**A:** Empty rows at the end are ignored, but empty rows in the middle can cause errors.

---

## 📞 Support

If you have problems with imports:

1. **Check the logs** in Admin Dashboard
2. **Compare your file** with the examples in this document
3. **Follow the import order** (Countries → Athletes → Results)
4. **Contact an admin** for technical problems

---

**Last Updated:** 28.03.2026
**Version:** 1.0

