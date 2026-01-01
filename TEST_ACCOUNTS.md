# Test Accounts Guide

## How to Access User and Admin Accounts

### Test Accounts

#### Regular User Account
- **Email:** `user@test.com`
- **Password:** `password123`
- **Access:** Standard user features (dashboard, practice, vocabulary, etc.)

#### Admin Account
- **Email:** `admin@test.com`  
- **Password:** `admin123`
- **Access:** All user features + Admin Dashboard

### Creating Test Accounts

1. **Register New Account:**
   - Open the app
   - Navigate to "Sign Up" from the login screen
   - Enter email and password (minimum 8 characters)
   - Complete registration

2. **For Admin Access:**
   - After registering, you need to set the user role in Firebase Firestore:
     - Go to Firebase Console → Firestore Database
     - Find the `users` collection
     - Locate your user document
     - Add a field `role` with value `"admin"` (or ensure email contains "admin")
   - Alternatively, register with an email containing "admin" (e.g., `admin@test.com`)

### Admin Features

When logged in as an admin, you'll see:
- **Admin Dashboard** section in Settings
- Access to:
  - User Management
  - Content CMS (Lessons, Vocabulary, Exercises)
  - Platform metrics

### Troubleshooting

**App crashes after login:**
- The app now has better error handling
- If it still crashes, check Logcat for specific error messages
- Ensure Firebase is properly configured
- Make sure you have internet connection for first-time login

**Can't see Admin section:**
- Ensure your email contains "admin" or is exactly "admin@test.com"
- Check that you're logged in (check Settings screen)
- Admin section appears automatically if conditions are met

### Notes

- Test accounts are for development only
- In production, implement proper role-based access control
- Admin detection currently checks email - enhance with proper role checking from Firestore

