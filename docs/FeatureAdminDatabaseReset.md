# Admin Database Reset Feature

## Overview
This feature provides an admin-only endpoint to reset the database to a clean state while preserving the admin user.

## Endpoint

**POST** `/api/admin/reset`

- **Authentication:** Required (Admin role only)
- **Authorization:** ADMIN role
- **Request Body:** None

## What it does

The endpoint executes a SQL script that:
1. Deletes all data from tables in correct order (login_logs, imports, results, athletes, sports, countries)
2. Deletes all users EXCEPT the admin user (id = 1)
3. Resets auto-increment values to start fresh

## Testing with cURL

### 1. First, login as admin to get authentication

```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "adminpwd"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN",
  "message": "Admin login successful"
}
```

### 2. Reset the database (using HTTP Basic Auth)

```bash
curl -X POST http://localhost:8080/api/admin/reset \
  -u admin:adminpwd
```

**Expected Success Response (200 OK):**
```json
{
  "message": "Database reset successful. All data cleared except admin user.",
  "status": "success"
}
```

**Expected Error Response (401 Unauthorized) - if not admin:**
```json
{
  "message": "Access denied",
  "status": "error"
}
```

**Expected Error Response (500 Internal Server Error) - if SQL fails:**
```json
{
  "message": "Database reset failed: [error details]",
  "status": "error"
}
```

### 3. Verify reset worked - check that countries table is empty

```bash
curl -X GET http://localhost:8080/api/countries \
  -u admin:adminpwd
```

**Expected Response:**
```json
[]
```

### 4. Verify admin user still exists

```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "adminpwd"
  }'
```

**Expected Response:** Should still work and return admin user details

## Complete Test Sequence

```bash
# 1. Login as admin
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "adminpwd"}'

# 2. Check current countries (should have sample data)
curl -X GET http://localhost:8080/api/countries \
  -u admin:adminpwd

# 3. Reset database
curl -X POST http://localhost:8080/api/admin/reset \
  -u admin:adminpwd

# 4. Check countries again (should be empty)
curl -X GET http://localhost:8080/api/countries \
  -u admin:adminpwd

# 5. Verify admin can still login
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "adminpwd"}'
```

## Security Notes

- Only users with ADMIN role can access this endpoint
- The endpoint uses Spring Security's `@PreAuthorize("hasRole('ADMIN')")` annotation
- Attempting to access as a JUDGE user will result in 403 Forbidden
- The operation is logged with WARNING level for audit purposes

## Admin User Credentials

After reset, the following admin account remains:
- **Username:** admin
- **Password:** adminpwd
- **Email:** admin@example.com
- **Role:** ADMIN

