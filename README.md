# ReTailX - Intelligent Retail Management System

Welcome to **ReTailX**, a robust, modern, and scalable retail management Android application built with Kotlin. ReTailX is designed to streamline retail operations ranging from billing and inventory management to AI-powered sales insights and employee tracking. It leverages cutting-edge Android technologies and architectural best practices to deliver a premium user experience for both Store Managers and Sales Executives.

---

## 📥 Download APK

<p align="center">
  <a href="https://github.com/rajatt04/ReTailX/releases/download/1.0/app-release.apk">
    <img src="https://img.shields.io/badge/Download-ReTailX%20APK-FF6B6B?style=for-the-badge&logo=android&logoColor=white" />
  </a>
</p>
<p align="center">
  ⚡ Fast • 🎯 Minimal • 📱 Built with Kotlin & MVVM
</p>

---

## App Screenshots

Below are some screenshots of the ReTailX application showcasing its features and user interface:

| Screenshot 1      | Screenshot 2      | Screenshot 3      |Screenshot 4       |
|-------------------|-------------------|-------------------|-------------------|
| ![Image1](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195739.jpg?raw=true) | ![Image2](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195745.jpg?raw=true) | ![Image3](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195805.jpg?raw=true) |![Image4](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195838.jpg?raw=true) |

| Screenshot 5      | Screenshot 6      | Screenshot 7      |Screenshot 8       |
|-------------------|-------------------|-------------------|-------------------|
| ![Image5](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195904.jpg?raw=true) | ![Image6](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_195909.jpg?raw=true) | ![Image7](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_200011.jpg?raw=true) |![Image8](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/ReTailX/Screenshot_20260417_200034.jpg?raw=true) |


## 📱 Key Features

*   **Role-Based Access Control (RBAC):** Distinct dashboards and feature sets for Admin/Managers and Sales Executives/Employees.
*   **Intelligent Billing System:** Seamless cart management, instant PDF bill generation, and printing support.
*   **Smart Store Management**
    *   **Barcode Scanning:** Swift product lookup and bill addition powered by ML Kit.
    *   **Inventory Tracking:** Real-time stock status, low stock alerts, and smart reordering.
    *   **Draft Products:** Save products conditionally to reduce internet redundancy when uploading media files.
*   **Sales & Analytics (Gemini AI Integration):** Deep visual analytics, top-selling product insights, and AI-driven predictive insights powered by Google's Gemini LLM.
*   **Employee & Customer Relations:** Built-in Employee Grid tracking, user activity logs, and customer database management.
*   **Secure & Accessible:**
    *   Real-time data parity using Firebase Firestore and Storage.
    *   Biometric App Lock and lock screen support for iron-clad data security.

## 🛠 Tech Stack & Architecture

ReTailX follows modern Android App Architecture (MVVM) and relies on highly robust, up-to-date technologies:

*   **Language:** Kotlin (Target SDK 35)
*   **Architecture:** Model-View-ViewModel (MVVM) with Single Activity pattern (`NavHostFragment`) + Hilt DI.
*   **UI System:** Android Views, Material Design, ViewBinding, and Lottie Animations.
*   **Dependency Injection:** Dagger Hilt
*   **Database & Local Storage:**
    *   **Room Database:** Handles local draft persistence and caching (e.g., `DraftProduct`).
    *   **DataStore / SharedPreferences:** Secure preference handling (`SecurityPreferences`).
*   **Backend & Cloud Services:**
    *   **Firebase Authentication:** Reliable user authentication.
    *   **Firebase Firestore:** Real-time NoSQL database mapping.
    *   **Firebase Storage:** Secure media and document storage.
*   **Networking:** Retrofit + GSON with `ImgBBService` for alternate image hosting.
*   **AI & Machine Learning:**
    *   `Google Generative AI (Gemini)`: Powers the intelligent chat and insights feature.
    *   `Google ML Kit`: Handles hardware-accelerated Barcode Scanning.
*   **Additional SDKs:**
    *   `Mapbox / OSMDroid`: For location and map integrations.
    *   `MPAndroidChart`: For engaging, interactive sales charting.
    *   `Android CameraX`: For seamless camera preview during barcode execution.

## 📋 Project Structure

The project encompasses a clean architecture approach, systematically separating responsibilities across specific packages inside `com.rajatt7z.retailx`:

*   **`models`**: Domain models (e.g., *Bill, Product, Order, Employee*).
*   **`viewmodel`**: UI-facing State Holders handling logic & events (e.g., *OrderViewModel, AuthViewModel, ProductViewModel*).
*   **`repository`**: Single Source of Truth for data bridging Firebase & Room (e.g., *AuthRepository, ProductRepository*).
*   **`fragments`**: Modular UI screens segregated logically into features like `orders`, `products`, and `settings`.
*   **`adapters`**: RecyclerView bindings.
*   **`database`**: Local Room database configuration and DAOs.
*   **`di`**: Hilt Dependency Injection modules (`AppModule`).
*   **`utils`**: High-level helpers involving `BiometricHelper`, `GeminiHelper`, `PdfGenerator`, and Camera/Network rules.

## 🚀 Getting Started

Follow these instructions to set up ReTailX on your local development machine:

### Prerequisites
1.  **Android Studio** (Koala Feature Drop or newer).
2.  **JDK 17** (or as mandated by target SDK limits).
3.  Active Firebase Project with Authentication, Firestore, and Storage enabled.
4.  API Keys for ImgBB and Gemini API.

### Installation

1.  **Clone the Repository**
    ```sh
    git clone https://github.com/rajatt04/ReTailX.git
    cd ReTailX
    ```

2.  **Add Configuration Files**
    *   Download your `google-services.json` from your Firebase Console and place it in the `app/` directory.
    *   Create a `local.properties` file in the root directory and append your secure API keys:
        ```properties
        API_KEY="YOUR_GEMINI_API_KEY_HERE"
        IMGBB_API_KEY="YOUR_IMGBB_API_KEY_HERE"
        ```

3.  **Build and Run**
    *   Synchronize Gradle (`File > Sync Project with Gradle Files`).
    *   Build and deploy the application (`Run > Run 'app'`) to an Emulator or a physical Android Device.

## 🔒 Security & Privacy

ReTailX adheres strictly to Android's Best Practices:
*   Granular Permissions usage (Camera, Location, Notifications).
*   Biometric Auth prompt handling via `Biometrics prompt API`.
*   Encrypted preferences to abstract critical local data instances.

## 📄 License
*This project does not currently declare an explicit open-source license. Please consult the repository owner for permissions regarding commercial or open-source usage.*
