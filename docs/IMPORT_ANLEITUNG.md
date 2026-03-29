# ✅ CORRECT IMPORT GUIDE

## 🎯 Problem
Results are not being imported because athletes don't exist.

## ✅ Solution: Correct Import Order

### **IMPORTANT: Follow the import order!**

```
Step 1️⃣  Import COUNTRIES (FIRST!)
Step 2️⃣  Import ATHLETES (SECOND)
Step 3️⃣  Import RESULTS (LAST)
```

---

## 📋 Step-by-Step Instructions

### **Step 1: Import Countries**

```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin \
  -F "file=@countries_test.csv"
```

**Expectation:**
```json
{
  "status": "COMPLETED",
  "successfulRecords": 10,
  "failedRecords": 0
}
```

✅ **10 Countries should be imported**

---

### **Step 2: Import Athletes**

```bash
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin \
  -F "file=@athletes_test.csv"
```

**Expectation:**
```json
{
  "status": "COMPLETED",
  "successfulRecords": 10,
  "failedRecords": 0
}
```

✅ **10 Athletes should now be in the database:**
- Mikaela Shiffrin (US)
- Marco Odermatt (CH)
- Petra Vlhova (SK)
- Alexis Pinturault (FR)
- Sofia Goggia (IT)
- Johannes Boe (NO)
- Marte Roeiseland (NO)
- Nathan Chen (US)
- Yuzuru Hanyu (JP)
- Ireen Wust (NL)

---

### **Step 3: Import Results**

```bash
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin \
  -F "file=@results_test.csv"
```

**Expectation:**
```json
{
  "status": "COMPLETED",
  "successfulRecords": 8,
  "failedRecords": 0
}
```

✅ **8 Results should now be imported**

---

## 📊 Database Verification

### SQL Commands to Check:

```sql
-- Verify countries
SELECT COUNT(*) as country_count FROM countries WHERE code IN ('us', 'de', 'fr', 'gb', 'jp', 'cn', 'au', 'ca', 'it', 'es');

-- Verify athletes
SELECT COUNT(*) as athlete_count FROM athletes 
WHERE (first_name = 'Sofia' AND last_name = 'Goggia')
   OR (first_name = 'Mikaela' AND last_name = 'Shiffrin')
   OR (first_name = 'Nathan' AND last_name = 'Chen');

-- Verify results
SELECT COUNT(*) as result_count FROM results 
WHERE athlete_id IN (SELECT id FROM athletes WHERE first_name IN ('Sofia', 'Mikaela', 'Nathan', 'Yuzuru', 'Ireen'));
```

---

## 🔴 If You Still Have Errors:

### **Error Source 1: Results shows 0 successful records**

**Check:**
```bash
# Check import errors
curl -X GET http://localhost:8080/api/imports/logs \
  -u admin:admin | jq '.[] | select(.importType=="RESULTS")'
```

**Possible Errors:**
- `ATHLETE_NOT_FOUND` → Athletes were not imported
- `SPORT_NOT_FOUND` → Sport doesn't exist (case-sensitive!)
- `INVALID_NUMBER_FORMAT` → rank or timeOrPoints formatted incorrectly

### **Error Source 2: Cache shows old data**

**Solution - Clear cache:**
```bash
# Admin Dashboard: Reset cache manually
# Or: Restart server
```

---

## 📋 Test File Contents

### **countries_test.csv**
- us, de, fr, gb, jp, cn, au, ca, it, es

### **athletes_test.csv**
- Mikaela Shiffrin (us)
- Marco Odermatt (ch)
- Petra Vlhova (sk)
- Alexis Pinturault (fr)
- Sofia Goggia (it)
- Johannes Boe (no)
- Marte Roeiseland (no)
- Nathan Chen (us)
- Yuzuru Hanyu (jp)
- Ireen Wust (nl)

### **results_test.csv**
- Sofia Goggia - Alpine Skiing - Rank 1 - GOLD
- Mikaela Shiffrin - Alpine Skiing - Rank 2 - SILVER
- Petra Vlhova - Alpine Skiing - Rank 3 - BRONZE
- Johannes Boe - Biathlon - Rank 1 - GOLD
- Marte Roeiseland - Biathlon - Rank 2 - SILVER
- Nathan Chen - Figure Skating - Rank 1 - GOLD
- Yuzuru Hanyu - Figure Skating - Rank 2 - SILVER
- Ireen Wust - Speed Skating - Rank 1 - GOLD

**All Athletes and Sports exist in the database seeding!**

---

## ✅ Checklist

- [ ] Countries successfully imported (10 records)
- [ ] Athletes successfully imported (10 records)
- [ ] Results successfully imported (8 records)
- [ ] No errors in import logs
- [ ] Data visible in frontend
- [ ] Cache cleared if necessary

**Status: Ready to test!** 🎉

