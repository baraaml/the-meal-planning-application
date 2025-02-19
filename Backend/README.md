# To-Do List Application

## Project Overview
This is a simple to-do list application with:
- **Frontend**: Built using Kotlin for Android.
- **Backend**: Powered by a server-side framework (Express.js).

The application enables users to:
- View a shared task list.
- Add new tasks.
- Manage task completion status.

---

## Features

### Frontend (Android App)
- **Task List Display**: View tasks in a RecyclerView.
- **Add Task**: Add tasks via a dedicated screen.
- **Task Completion**: Mark tasks as completed with visual cues.
- **API Integration**: Communicates with the backend to manage tasks.
- **Error Handling**: Provides feedback via Toast messages.

### Backend
- **Task Management API**: Endpoints to fetch and add tasks.
- **Basic Authentication**: Hardcoded user authentication for simplicity.
- **Database Integration**: Stores tasks and user information.
- **Deployment**: Hosted on a free service (e.g., Render or Heroku).

---

## Project Setup

### Frontend
1. Clone the repository.
2. Open the project in **Android Studio**.
3. Install dependencies in `build.gradle` (e.g., Retrofit, Coroutines).
4. Run the app on an emulator or physical device.

### Backend
1. Clone the repository.
2. Install dependencies:
   ```bash
   npm install  # For Node.js
