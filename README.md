JFC Contact and Tasks 

🐝A modern Android application built with Jetpack Compose featuring a secure authentication system using Firebase Auth and JWT (JSON Web Tokens). The UI is designed with a vibrant theme inspired by the Jollibee color palette.✨ FeaturesSecure Authentication: Login and Registration powered by Firebase.JWT Integration: Fetches and manages ID Tokens for secure API communication.Modern UI: Built entirely with Jetpack Compose using Material 3.Account Management: * "Remember Me" functionality using local preferences.Password visibility toggles.Forgot Password / Email Password Reset.Loading States: Integrated CircularProgressIndicator for all network operations.🛠️ Tech StackLanguage: KotlinUI Framework: Jetpack ComposeBackend: Firebase AuthenticationArchitecture: MVVM (Model-View-ViewModel)Dependency Injection: ViewModel Factory🚀 Setup Instructions1. Firebase ConfigurationGo to the Firebase Console.Create a new project named JFC Contact and Tasks.Add an Android App using the package name: com.stewardapostol.jfc.Download the google-services.json file and place it in your app/ directory.Enable Email/Password authentication in the Firebase Auth settings.2. DependenciesEnsure your build.gradle.kts (App level) includes the following:Kotlindependencies {
// Firebase BoM
implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
implementation("com.google.firebase:firebase-auth")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")
}
📸 PreviewLogin ScreenRegistration ScreenNote: The UI uses the Jollibee color scheme:Primary Red: #E31837Secondary Yellow: #FFC72C📂 Project StructurePlaintextcom.stewardapostol.jfc
├── data
│   └── local          # SharedPreferences / PREF logic
├── ui
│   ├── auth           # AuthScreen & JWTAuthViewModel
│   └── theme          # Color, Type, and Theme definitions
└── MainActivity.kt    # Navigation & Entry point
📜 LicenseThis project is for educational purposes. All branding colors are inspired by Jollibee Foods Corporation.