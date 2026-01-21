###  **Description**

This PR implements the complete Authentication module for the JFC Contact and Tasks app. It transitions the UI from a generic blue theme to a Jollibee-inspired branding and adds critical security features like JWT retrieval and Password Reset.


**Key Changes**

  - Authentication Logic:
    -   Integrated Firebase createUserWithEmailAndPassword and signInWithEmailAndPassword.
    -   Added fetchJwt to retrieve the Firebase ID Token (JWT) for secure backend calls.
    -   Implemented signOut after registration to prevent auto-login (per requirements).
  
  - UI/UX Enhancements:
    -   Branding: Applied Jollibee Red gradient background and Jollibee Yellow for primary actions.
    -   Components: Added a unified Header Row containing the App Logo and Title.
    -   User Convenience: Added a "Remember Me" checkbox (local persistence) and a "Forgot Password" link.
    -   Visual Feedback: Integrated a CircularProgressIndicator and password visibility toggles.
  
  - Documentation: Added a comprehensive README.md with setup instructions and tech stack details.

**Technical Details**
  -   State Management: Uses StateFlow in JWTAuthViewModel to handle loading, login success, and error messages.
  -   Security: Ensures the idToken is refreshed and valid before allowing navigation to the main screen.
  -   Dependencies: Added material-icons-extended for visibility icons.

### **Screenshots/Recordings**
|JWT Provider|
|-----|
|<img width="1668" height="613" alt="Screenshot 2026-01-22 014051" src="https://github.com/user-attachments/assets/5ff89172-fae6-425b-b318-562868e615b3" />|

|Register|Login|
|-----|-----|
|<img width="300" height="2570" alt="Screenshot_20260122_002841" src="https://github.com/user-attachments/assets/6a91f2ea-064b-4f8f-af4e-86a8d3981538" />|<img width="300" height="2570" alt="Screenshot_20260122_002910" src="https://github.com/user-attachments/assets/c4ced271-fdb5-4480-a761-a41c7a253702" />|



