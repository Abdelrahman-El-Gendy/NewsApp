# 🗞️ News App

A modern Android News App built using **Kotlin**, **Jetpack Compose**, and **Clean Architecture** principles. It supports real-time and offline reading experiences with local caching and a favorite articles system.

---

## 🛠️ Tech Stack & Tools

### 🧱 Architecture
- **MVVM** – Model-View-ViewModel architecture.
- **Clean Architecture** – Separation of concerns between layers (`data`, `domain`, `presentation`).


### 💄 UI
- **Jetpack Compose** – Modern declarative UI toolkit for Android.
- **State Management** – Using `ViewModel`, `MutableState`, and `StateFlow`.

### 📡 Networking
- **Retrofit** – For making HTTP requests.
- **Gson Converter** – For JSON serialization/deserialization.

### 🗃️ Local Storage
- **Room Database** – Local persistence of favorite and cached articles.
- **Coroutines** – For asynchronous operations and database interactions.
- **Flow / StateFlow** – For reactive and lifecycle-aware data streams.
- **DateStore** - For Saving the Authenticating Login 

### 🌐 Offline Support
- **Room Caching** – Automatically shows saved data when offline.
- **Error Handling** – Detects no internet and silently falls back to cache.

### 💡 Dependency Injection *(optional for future scalability)*
- **Hilt*** – Easily inject dependencies into your components.

---

## 📁 Project Structure
com.example.newsapp </br>
├── data </br>
│   ├── local </br>
│   ├── remote </br>
│   ├── repository </br>
│   └── mapper (optional) </br>
├── domain </br>
│   ├── model </br>
│   ├── repository </br>
│   └── usecase (optional) </br>
├── presentation </br>
│   ├── viewmodel </br>
│   └── screens </br>
## 📱 Demo

![App Demo](assets/demo.gif)
![Image](https://github.com/user-attachments/assets/1e17fab4-ccad-4a1d-bfba-3b5e0ab68676)

