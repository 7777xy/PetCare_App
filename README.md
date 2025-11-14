# 🐾 PetCare App  

A multifunctional and comprehensive Android app built with **Kotlin**, designed to help pet owners easily manage their pets’ health, fitness, appointments, and reminders.  

---

## Navigation (Screens)

- **Home**: Overview of upcoming appointments, reminders, and pet tips.  
- **My Pets**: Add, edit, and view pet profiles along with lifestyle data (exercise routines & diet) and health history.
- **Appointments**: Track vet and vaccination appointment.  
- **Reminders**: Set notifications for important events such as medicine schedules, feeding times, and exercise routines
- **Appointment History**: A record of appointments that have already been completed.
- **Reminder History**: A record of reminders that have already been completed.


## Notifications

When a reminder is created on the Reminder screen, the app automatically schedules a notification for the specified date and time. The notification system uses Android’s alarm and notification APIs to ensure timely alerts, even if the app is running in the background or closed. This feature helps users stay on track with important pet-related tasks and ensures reminders are delivered reliably.

- **NotificationHelper.kt**: A utility class responsible for creating notification channels (for Android 8.0 and above) and building notification objects with titles, messages, and icons. It centralizes notification configuration to ensure consistency across the app.
- **ReminderReceiver.kt**: A BroadcastReceiver that is triggered when a scheduled reminder time is reached. It receives the alarm signal and uses NotificationHelper to display the appropriate notification to the user, even when the app is in the background.
- **AndroidManifest.xml**: Defines essential system-level configurations, including registering the ReminderReceiver so Android can trigger it at the correct time. It also declares required permissions for alarms and notifications.


## Data Storage

All application data are stored locally using the Room Database. Room provides a robust abstraction layer over SQLite, ensuring reliable data persistence, type-safety, and compile-time verification of SQL queries. By using Room, the app can efficiently store, retrieve, and update information such as pets, appointments, reminders, and user-generated records. This approach ensures data remains available across app restarts and supports seamless interaction between the UI and the underlying data through ViewModels and LiveData/StateFlow.

- **App Database**: The central Room database class that provides access to all DAOs and serves as the main entry point for persistent data operations.
- **Pet Dao, Appointment Dao & Reminder Dao**: Data Access Objects (DAO) that define SQL queries and database operations (insert, update, delete, retrieve) for each data type.
- **Pet Entity, Appointment Entity & Reminder Entity**: Entity classes that represent the tables in the Room database. Each entity defines the schema for its respective data model.
- **Pet Extensions, Appointment Extensions & Reminder Extensions**: Extension functions used to convert between database entities and UI/domain models, ensuring clean separation between storage representation and app logic.
- **MyPet ViewModel, Appointment Viewmodel & Reminder ViewModel**: These ViewModels manage the core data and business logic for their respective screens. They handle data retrieval, insertion, updates, and communication with the Room database through the Repository layer. Each ViewModel exposes reactive UI states to ensure that changes in the underlying data are immediately reflected in the UI, improving both performance and user experience.


## Tests

A comprehensive testing strategy was implemented to ensure the reliability and stability of the app. This includes unit tests for validating individual functions and ViewModel logic, integration tests to verify interactions between components such as the Repository and Room Database, and UI tests using Jetpack Compose testing tools to ensure the user interface behaves as expected. Together, these tests help maintain code quality, prevent regressions, and ensure a consistent user experience across different scenarios.

- **Home Unit Test, MyPet Unit Test, Appointment Unit Test & Reminder Unit Test**: Unit tests.
- **Home UI Test, MyPet UI Test, Appointment UI Test & Reminder UI Test**: UI tests.
- **MyPet Integration Test, Appointment Integration Test & Reminder Integration Test**: Integration tests (Since the Home Screen doesn't use a ViewModel, it doesn't require an integration test).
