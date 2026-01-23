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
### APK :  [jfc-contact-task.apk](https://github.com/steward25/JFC-Contact-Tasks/releases/download/v1.0.0/jfc-contact-task-v1.0.0.apk)
### **Screenshots/Recordings**
|JWT Provider|
|-----|
|<img width="1693" height="645" alt="Screenshot 2026-01-22 003415" src="https://github.com/user-attachments/assets/61f5303a-09cc-4d63-8a8e-59ac93edb373" />|

|Register|Login| Forgot Password |
|-----|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_064704" src="https://github.com/user-attachments/assets/7675543b-f1f6-4ca3-b345-d5c74b491126" />|<img width="300" height="2553" alt="Screenshot_20260123_064547" src="https://github.com/user-attachments/assets/91fb7c17-103a-4890-92ef-b2cf3411a8da" />|<img width="600" height="710" alt="Screenshot 2026-01-23 094801" src="https://github.com/user-attachments/assets/ff385977-0323-4b1f-9ee5-dede436adbee" />|


Main Pages
|Tasks|Businesses| People|Management|
|-----|-----|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_090012" src="https://github.com/user-attachments/assets/c69e761d-936f-44d8-96f3-95702177ca2b" />|<img width="300" height="2553" alt="Screenshot_20260123_085129" src="https://github.com/user-attachments/assets/af86f5e9-c3f8-4ab4-8c9b-ddabcee27fe3" />|<img width="300" height="2553" alt="Screenshot_20260123_085140" src="https://github.com/user-attachments/assets/4c0eb9b7-17a7-4d68-ac6e-363aa5b0b628" />|<img width="300" height="2553" alt="Screenshot_20260123_091324" src="https://github.com/user-attachments/assets/fcf7105d-0c39-4781-90a2-70b23a866050" />|

Task List Page
| Create Task| Open Task| Completed Task|
|-----|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_091614" src="https://github.com/user-attachments/assets/71de299f-0880-4816-ab68-1aa33de71354" />|<img width="300" height="2553" alt="Screenshot_20260123_090012" src="https://github.com/user-attachments/assets/e1948092-33aa-4721-aeac-a0e08d9dc278" />|<img width="300" height="2553" alt="Screenshot_20260123_090042" src="https://github.com/user-attachments/assets/5d24ad04-878e-4c67-b402-e49e3156561c" />|

Businesses Page
|Add Business|Edit Business|Task History For Specific Business|
|-----|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_091948" src="https://github.com/user-attachments/assets/f25bb07c-ca11-414d-a579-1090a7502b35" />|<img width="300" height="2553" alt="Screenshot_20260123_092041" src="https://github.com/user-attachments/assets/647684c5-bc04-40e6-8ad7-0ce6f8dea0ed" />|<img width="300" height="2553" alt="Screenshot_20260123_090308" src="https://github.com/user-attachments/assets/de432e82-1970-491a-8b1a-be7fb4765f69" />|


People Page
|Add Person|Edit Person|Task History For Specific Person|
|-----|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_084749" src="https://github.com/user-attachments/assets/716747d2-ec6d-4e22-8d72-b2964087e26b" />|<img width="300" height="2553" alt="Screenshot_20260123_092415" src="https://github.com/user-attachments/assets/f35dcf96-83c2-4f49-b33c-50859f10d514" />|<img width="300" height="2553" alt="Screenshot_20260123_090210" src="https://github.com/user-attachments/assets/b9064939-6742-4ff6-8027-47db62d50641" />|

Management Page
|Add/Update/Delete  Category|Add/Update/Delete Tag|
|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_084154" src="https://github.com/user-attachments/assets/129fb8c2-ecd7-4feb-a40a-1de752e6e44d" />|<img width="300" height="2553" alt="Screenshot_20260123_084302" src="https://github.com/user-attachments/assets/251f01fd-2a77-4b5c-bb94-1bc8064ee8ba" />|
|<img width="300" height="2553" alt="Screenshot_20260123_084348" src="https://github.com/user-attachments/assets/820710d8-282e-4f14-9d7c-98b033827e78" />|<img width="300" height="2553" alt="Screenshot_20260123_092903" src="https://github.com/user-attachments/assets/e2cfb205-329e-45bb-a55c-474dff71079c" />|
|<img width="300" height="2553" alt="Screenshot_20260123_093000" src="https://github.com/user-attachments/assets/7e6c9556-a4cf-43c5-9b16-b4b0b39f414e" />|<img width="300" height="2553" alt="Screenshot_20260123_093030" src="https://github.com/user-attachments/assets/61e8500b-a05e-429b-89ad-3eb7f5b95738" />|

Profile Page
|Update Profile|Change Password|
|-----|-----|
|<img width="300" height="2553" alt="Screenshot_20260123_090326" src="https://github.com/user-attachments/assets/891797e2-dbdc-4973-95c2-3b6ac328443c" />|<img width="300" height="2553" alt="Screenshot_20260123_090342" src="https://github.com/user-attachments/assets/5086cc48-ed02-43c2-a37b-225b4af53b61" />|




