"# AndroidApp-ReqResAPI" 

# ReqRes API Android Application

## Overview
Android app built with MVVM architecture, integrating the ReqRes API via Retrofit for seamless data fetching. The app features Room for local database management, advanced error handling, pagination, and comprehensive user CRUD operations including adding, updating, deleting, and modifying user images with real-time UI updates.

## Build and Run Instructions

### Prerequisites
- **Android Studio**: Make sure you have the latest version of Android Studio installed.
- **Java Development Kit (JDK)**: Ensure you have JDK 8 or higher installed.
- **Internet Connection**: Required for fetching data from the ReqRes API.

### Steps to Build and Run
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/vadimbel/ReqResAPI-AndroidApp.git

2. Open the Project:
   - Launch Android Studio.
   - Select "Open an existing project" and navigate to the cloned repository.
   - Open the project.

3. Build the Project:
   - In Android Studio, click on Build > Make Project to build the app and resolve dependencies.

4. Run the App:
   - Connect an Android device via USB or start an Android emulator.
   - Click the 'Run' button in Android Studio or select Run > Run 'app' from the menu.
  
5. APK File:
   - The APK file is available in the app/build/outputs/apk/debug/ directory after building the project.
  
6. Assumptions:
   - API Stability: It is assumed that the ReqRes API is stable and returns data consistently as per its documentation.
   - Device Compatibility: The app is developed and tested for devices running Android 5.0 (Lollipop, SDK 21) and above.

7. Challenges:
   - Network Reliability: Implemented a retry mechanism for API calls to handle transient network failures.
   - Pagination: Managing large datasets efficiently required careful handling of API pagination.
   - Error Handling: Ensuring that validation errors were clearly communicated to the user, both through UI feedback and toast messages.

8. Code Quality and Best Practices:
   - MVVM Architecture: The project follows the MVVM architecture to ensure a clean, maintainable, and testable codebase.
   - Code Organization: The project is organized into distinct packages (model, view, viewmodel, utils) to separate concerns and improve readability.
   - Used Retrofit for network communication with appropriate error handling.
   - Employed Room for local database management with proper schema handling.

