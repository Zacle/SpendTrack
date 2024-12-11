<p><a target="_blank" href="https://app.eraser.io/workspace/Vpp13yukn8E9xUvejWnZ" id="edit-in-eraser-github-link"><img alt="Edit in Eraser" src="https://firebasestorage.googleapis.com/v0/b/second-petal-295822.appspot.com/o/images%2Fgithub%2FOpen%20in%20Eraser.svg?alt=media&amp;token=968381c8-a7e7-472a-8ed6-4a6626da5501"></a></p>

## 
# **SpendTrack Architecture**
## 1. Introduction
### 1.1 Purpose
This document outlines the architecture of the **SpendTrack **app, detailing its design, components, and interactions. It is intended for developers, architects, and stakeholders involved in the project. The document provides a comprehensive overview of the system to ensure a shared understanding among the team.

### 1.2 Scope
The architecture covers the multi-module of the **SpendTrack** app, including:

- Android application with Firebase as a remote database.
- Room as a local database for offline-first functionality.
- Real-time data synchronization between Firebase and Room.
- Multi-platform support, including Android and Web.
### 1.3 Definitions, Acronyms, and Abbreviations
-  **Jetpack Compose**: Used for modern UI development, leveraging custom composables and animations for a dynamic and responsive design.
- **MVVM**: Acts as a bridge between the UI and the underlying data layer. Holds UI-related data and provides data manipulation methods. Observes changes in data from the Use Case or Repository and exposes it to the UI components using [﻿Coroutines](https://github.com/Kotlin/kotlinx.coroutines)  and [﻿Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) .
- **Use Cases**: Contains business logic and use case implementations. Coordinates data flow between the presentation and data layers.
- **Repository**: A design pattern that mediates data from different data sources.
- **DataStore**: A data storage solution that allows to store key-value pairs or typed objects with [﻿protocol buffers.](https://developers.google.com/protocol-buffers)  
- **Room**: An SQLite object mapping library for Android.
- **DAO**: Data Access Object, an interface that provides methods to access the database.
- **Firebase**: A platform developed by Google for creating mobile and web applications.
- **Hilt**: Provide scalable and maintainable object creation and management, optimizing app architecture and development workflow.
### 1.4 References
-  [﻿Firebase Documentation](https://firebase.google.com/docs)  
-  [﻿Room Documentation](https://developer.android.com/training/data-storage/room)  
-  [﻿Android's App Architecture](https://developer.android.com/topic/architecture)  
### 1.5 Overview
This document includes:

- Architectural style and rationale.
- System stakeholders and concerns.
- High-level system description.
- Key architectural strategies.
- Detailed system architecture, including layers, modules, and database design.
- Key architectural decisions.
- Quality attributes.
- Identified risks and technical debt.
## 2. Architectural Representation
### 2.1 Architectural Style and Rationale
The system follows a modular architecture with MVVM (Model-View-ViewModel) pattern. The rationale for this choice includes:

- **Separation of Concerns**: MVVM separates business logic from UI, making the app more maintainable and testable.
- **Scalability**: Modular architecture allows independent development and scaling of components.
- **Offline-First Approach**: Ensures the app is usable without internet connectivity by using Room for local storage and synchronization with Firebase when online.
## 3. System Stakeholders and Concerns
### 3.1 Stakeholders
- **End Users**: Require reliable and intuitive expense tracking.
- **Developers**: Need a maintainable and extensible codebase.
- **Project Managers**: Focus on timely delivery and meeting business requirements.
- **QA Team**: Ensures the app meets quality standards.
### 3.2 System Concerns
- **Performance**: Efficient data storage and retrieval.
- **Scalability**: Handle increasing data and user load.
- **Security**: Protect user data and ensure secure authentication.
- **Maintainability**: Ease of adding new features and fixing bugs.
## 4. System Overview
### 4.1 High-Level Description
The **SpendTrack** app allows users to track their expenses, set budgets, and receive real-time updates across multiple devices. It uses Firebase for remote storage and real-time synchronization and Room for local storage to support offline usage.

## 5. Architectural Strategies
### 5.1 Key Strategies
- **Offline-First**: Use Room for local data storage to ensure app functionality without internet.
- **Real-Time Sync**: Utilize Firebase Realtime Database to sync data across devices instantly.
- **Modular Design**: Separate features into different modules for better maintainability and scalability.
## 6. System Architecture
### 6.1 Overview of Layers/Modules
#### 1. Presentation Layer:
- **Jetpack Compose**: Responsible for handling UI components and user interactions.
- **ViewModels**: Acts as a bridge between the UI and the underlying data layer. Holds UI-related data and provides data manipulation methods. Observes changes in data from the repository and exposes it to the UI components using Flow.
#### 2. Domain Layer:
- **Use Cases/Interactors**: Contains business logic and use case implementations. Coordinates data flow between the presentation and data layers.
#### 3. Data Layer:
- **Repositories**: Abstract interfaces responsible for managing data operations (e.g., fetching data from local or remote sources, caching).
- **Data Sources**: Concrete implementations of repositories. These can include local data sources (e.g., Room database and DataStore) and remote data sources (e.g., Firebase).
- **Models**: POJO (Plain Old Java Objects) representing data entities used within the app.
- **Local Data Storage**: Provides methods for interacting with local databases (e.g., Room Persistence Library for SQLite).
#### 4. Dependency Injection:
- **Dagger Hilt**: A modern dependency injection library for Android that helps manage dependencies and facilitate modularization.
#### 5. Other Components:
- **Network and Error Handling**: Utility classes for managing network requests and handling errors gracefully.
- **Navigation Component**: Handles navigation between different screens within the app.
#### 6. Libraries and Frameworks:
- **Jetpack Components**: Utilize Android Jetpack components such as LiveData, ViewModel, Room Persistence Library, Navigation Component, etc.
- **Gson/Moshi**: JSON serialization/deserialization libraries for parsing API responses.
- **Coroutine/Flow**: For handling asynchronous operations and data streams.
- **Glide/Picasso**: Image loading libraries for loading and caching images.
#### 7. Testing:
- **Unit Tests**: Test individual components such as ViewModel, Use Cases, and Repository implementations.
- **Integration Tests**: Test interactions between different layers of the architecture.
- **UI Tests**: Test UI components using frameworks like Espresso or UI Automator.
#### 8. Architecture Components:
- **Lifecycle**: Manage lifecycle-aware components and handle lifecycle events.
- **Flow**: Notify UI components about data changes.
- **ViewModel**: Store and manage UI-related data in a lifecycle-conscious way.
- **Room**: Provide an abstraction layer over SQLite for database operations.
#### 9. Modularization:
- **Feature Modules**: Organize code into feature-specific modules to promote code reusability and maintainability.
### 6.2 Component Diagrams
![System Architecture](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---1GD8DmKAn0ikyWDelijs2---figure---sHzW50i0zYwTUyxpRlnIpQ.png "System Architecture")

### 6.3 Database Design
![Database](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---Fy3pjmOzpEKwH_o8YmI4y---figure---mfMa5bqXZO6yM_xqoBIwwg.png "Database")

## 7. Key Architectural Decisions
### 7.1 Decision Log
- **Choice of Firebase**: Selected for its real-time synchronization capabilities.
- **Room for Local Storage**: Chosen for its simplicity and efficient offline support.
- **MVVM Pattern**: Ensures separation of concerns and ease of testing.
## 8. Quality Attributes
### 8.1 Performance
- **Requirements**: Fast data access and minimal latency.
- **Support**: Use of Room for local caching and Firebase for real-time updates.
### 8.2 Scalability
- **Considerations**: Firebase scales automatically with user load.
- **Strategies**: Modular design allows scaling individual components as needed.
### 8.3 Security
- **Measures**: Use Firebase Authentication for secure user login.
- **Considerations**: Ensure encrypted communication and data storage.
### 8.4 Maintainability
- **Design**: Modular architecture and MVVM pattern facilitate maintenance.
- **Practices**: Regular code reviews and automated testing.
## 9. Risks and Technical Debt
### 9.1 Identified Risks
- **Data Sync Issues**: Potential conflicts during synchronization.
- **Network Dependency**: Reliance on Firebase for real-time updates.
- **Synchronization Failure**: Retry synchronization in case WorkManager failed to sync
### 9.2 Technical Debt
- **Areas**: Possible need for optimization in data synchronization logic.
- **Plans**: Regular refactoring and code reviews to address technical debt.


# **SpendTrack Features' Requirements**
## Feature 0: Onboarding
### Introduction & Overview
This section outlines the implementation of the Onboarding feature for the SpendTrack app. The onboarding process ensures that new users are familiarized with the app’s features and functionalities.

### Goals
- Ensure new users understand the app’s core features and how to use them.
- Provide a smooth and informative introduction to the app.
### System Design Overview & Architecture
#### 1. Describe Functionality
- Check if the user has already completed the onboarding process.
- If not, display the onboarding screens to introduce the app’s features.
- Store the onboarding completion status in DataStore.
#### 2. Feature Scope
- Design and implement onboarding screens.
- Integrate DataStore to track onboarding completion status.
#### 3. Strategies
- Use DataStore to persist the onboarding status.
- Display onboarding screens only if the user has not completed the process.
#### 4. Diagram 
![Onboarding flow chart](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---HS8Rf66l1w1IVLWWRFh6k---figure---PoTHh0briJez32wOXKPcbw.png "Onboarding flow chart")

![Onboarding sequence diagram](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---Oxh6qTduYzKbNz7CE1ejY---figure---F4OeYEqpiYx-La4IMNaGDQ.png "Onboarding sequence diagram")

#### 5. System Changes
##### 5.1 UI/UX
- **Onboarding Screens**: Series of screens explaining the app’s features and how to use them.
- **Launch Screen**: Initial screen to check onboarding status and navigate accordingly.
##### 5.2 API
- No external API required for onboarding functionality.
##### 5.3 DB
- **DataStore**: Used to store and check onboarding completion status.
#### 6. Decisions & Tradeoff Considerations
##### 6.1 Reasons behind this design decision
- Using DataStore ensures a simple and efficient way to persist the onboarding status.
##### 6.1.1 Pros and Cons
- **Pros**: Simple implementation, ensures users are familiarized with the app.
- **Cons**: Adds initial steps before the user can start using the app.
##### 6.1.2 Alternative ideas and tradeoffs
- Use SharedPreferences: Similar but less modern and flexible compared to DataStore.
##### 6.1.3 Potential problems
- Users may skip the onboarding and miss important information.
##### 6.1.3 Dependencies to consider
- Proper integration with DataStore.
##### 6.2 I chose this design over alternatives because…
- DataStore provides a modern, efficient, and flexible solution for persisting simple data like the onboarding status.
## Feature 1: User Authentication
### Introduction & Overview
This document details the implementation of User Authentication for the SpendTrack app, using Firebase Authentication to ensure secure login and registration.

### Goals
- Securely authenticate users.
- Provide a seamless login and registration experience.
- Protect user data through authentication.
### System Design Overview & Architecture
#### 1. Describe Functionality
- Users can register with email and password or using their Google account.
- Users can log in using their credentials.
- Password reset functionality.
#### 2. Project Scope
- Implement Firebase Authentication.
- Design user-friendly login and registration screens.
- Ensure secure data handling during authentication processes.
#### 3. Strategies
- Use Firebase Authentication for backend authentication services.
- Securely handle user credentials and sessions.
#### 4. Diagram
![Auth flow chart](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---GYFjC3hybZdJw1O7GBpPL---figure---ArOeKYk--sIMj2KYI4rvXw.png "Auth flow chart")

![Authentication sequence diagram](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---eYhXiiWnvgsuVya_kIT7V---figure---KdCeHQzIKEhmY69joykpyw.png "Authentication sequence diagram")

### System Changes
#### 1. UI/UX
- **Login Screen**: Allows users to input email and password to log in.
- **Registration Screen**: Allows users to create a new account.
- **Password Reset Screen**: Allows users to reset their password.
- **Verify Email Screen**: Inform users to verify their email address before getting authorized.
#### 2. API
- Firebase Authentication API for registration, login, and password reset.
#### 3. DB
- User data stored in Firebase Authentication.
### Decisions & Tradeoff Considerations
#### 1. Reasons behind this design decision
- Firebase Authentication provides a robust and secure solution for user authentication with minimal setup.
#### 2. Pros and Cons
- **Pros**: Secure, easy to implement, handles authentication logic.
- **Cons**: Dependency on Firebase, potential downtime issues.
#### 3. Alternative ideas and tradeoffs
- Custom authentication system: More control but increased complexity and security concerns.
#### 4. Potential problems
- Dependency on external service (Firebase).
#### 5. Dependencies to consider
- Internet connection for authentication requests.
#### 6. I chose this design over alternatives because…
- Firebase Authentication provides a secure, reliable, and easy-to-implement solution for user authentication.
## Feature 2: Expense Tracking
### Introduction & Overview
This document outlines the implementation of Expense Tracking within the SpendTracker, allowing users to manage their expenses effectively.

### Goals
- Enable users to track and manage expenses.
- Provide an intuitive interface for adding, editing, and deleting expenses.
- Expenses should be synced with the Firebase database.
- Schedule a WorkManager if user is offline and cannot be synced with the Firebase
### System Design Overview & Architecture
#### 1. Describe Functionality
- Users can add new expenses with details like amount, category, date, and payment type.
- Users can edit existing expenses.
- Users can delete expenses.
##### 2. Project Scope
- Design and implement UI for managing expenses.
- Integrate local and remote databases for storing expense data.
##### 3. Strategies
- Use Room for local storage and Firebase for remote synchronization.
- Ensure offline capability with data synchronization when online using WorkManager.
#### 4. Diagram
![Features main flow](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---Q_JNKJLEj_igUpZlUQcIb---figure---5wykdJ6dLu0gB2Ex4k5ONA.png "Features main flow")

### System Changes
##### 1. UI/UX
- **Expense List Screen**: Displays a list of all expenses.
- **Add/Edit Expense Screen**: Form to add or edit expenses.
##### 2. API
- Use Firebase Realtime Database for remote expense data storage and synchronization.
##### 3. DB
- Local: Room database with tables for expenses.
- Remote: Firebase Realtime Database for syncing expenses.
### Decisions & Tradeoff Considerations
##### 1. Reasons behind this design decision
- Room ensures offline capability; Firebase provides real-time synchronization.
##### 2. Pros and Cons
- **Pros**: Offline support, real-time updates.
- **Cons**: Complex synchronization logic.
##### 3. Alternative ideas and tradeoffs
- Purely local storage: Simpler but lacks cross-device sync.
##### 4. Potential problems
- Data conflicts during synchronization.
##### 5. Dependencies to consider
- Reliable internet connection for Firebase synchronization.
##### 6. I chose this design over alternatives because…
- Combining Room and Firebase provides the best balance of offline capability and real-time synchronization.



<!-- eraser-additional-content -->
## Diagrams
<!-- eraser-additional-files -->
<a href="/README-entity-relationship-1.eraserdiagram" data-element-id="cjChCuEimtVwW7_JEBn-7"><img src="/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---diagram----e09650385fcabb25a5be3d18f324a440.png" alt="" data-element-id="cjChCuEimtVwW7_JEBn-7" /></a>
<a href="/README-sequence-diagram-2.eraserdiagram" data-element-id="2o9Kx3pgBmakrWgUyXPq7"><img src="/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---diagram----96d99e5adf5c4546894cd269cd645f05.png" alt="" data-element-id="2o9Kx3pgBmakrWgUyXPq7" /></a>
<a href="/README-sequence-diagram-3.eraserdiagram" data-element-id="Z1eMMdzCONG-wacofKN-Z"><img src="/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---diagram----3d71fc69f577f22dc0b6877c2629e785.png" alt="" data-element-id="Z1eMMdzCONG-wacofKN-Z" /></a>
<a href="/README-sequence-diagram-4.eraserdiagram" data-element-id="YQBn935g_CHYznDQGKdyg"><img src="/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---diagram----675e4785e4f1eb0320356be3cde828a3.png" alt="" data-element-id="YQBn935g_CHYznDQGKdyg" /></a>
<!-- end-eraser-additional-files -->
<!-- end-eraser-additional-content -->
<!--- Eraser file: https://app.eraser.io/workspace/Vpp13yukn8E9xUvejWnZ --->