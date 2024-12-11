<p><a target="_blank" href="https://app.eraser.io/workspace/Vpp13yukn8E9xUvejWnZ" id="edit-in-eraser-github-link"><img alt="Edit in Eraser" src="https://firebasestorage.googleapis.com/v0/b/second-petal-295822.appspot.com/o/images%2Fgithub%2FOpen%20in%20Eraser.svg?alt=media&amp;token=968381c8-a7e7-472a-8ed6-4a6626da5501"></a></p>

## 
**SpendTrack Architecture**

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
![System Architecture](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---BzpPwM1Fg9jsezDfGHxWX---figure---sHzW50i0zYwTUyxpRlnIpQ.png "System Architecture")

### 6.3 Database Design
![Database](/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---figure---hp7GC54MJKJcYH03UTxAs---figure---mfMa5bqXZO6yM_xqoBIwwg.png "Database")

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



<!-- eraser-additional-content -->
## Diagrams
<!-- eraser-additional-files -->
<a href="/README-entity-relationship-1.eraserdiagram" data-element-id="cjChCuEimtVwW7_JEBn-7"><img src="/.eraser/Vpp13yukn8E9xUvejWnZ___mbHLwx3aAyOSFQFXDLP8FU2S4x63___---diagram----e09650385fcabb25a5be3d18f324a440.png" alt="" data-element-id="cjChCuEimtVwW7_JEBn-7" /></a>
<!-- end-eraser-additional-files -->
<!-- end-eraser-additional-content -->
<!--- Eraser file: https://app.eraser.io/workspace/Vpp13yukn8E9xUvejWnZ --->