# Implementation Plan: Deploying TripSplitter Live

To make **TripSplitter** live, we will move from a local setup (XAMPP/Tomcat) to a cloud-based environment. This plan ensures the transition is smooth and follows best practices.

## Phase 1: Code Updates for Production

### 1. Update Database Configuration
Modify `DBConnection.java` to read credentials from environment variables. This allows the same code to work locally (with defaults) and on the server.

### 2. Add Docker Support
Add a `Dockerfile` to the root. This "packages" the application with Tomcat and its dependencies, making it compatible with modern hosting platforms like **Railway** or **Render**.

## Phase 2: Cloud Infrastructure

### 1. Database Setup (Aiven for MySQL)
Use **Aiven** to host the MySQL database for free.
- Create a free MySQL instance on Aiven.
- Get the Connection URL, Username, and Password.
- Import `database.sql` into the Aiven database.

### 2. Web Hosting (Railway.app)
Use **Railway.app** to host the Java application.
- Connect your GitHub repository: `https://github.com/Meetlimbani25/TripSplitter`.
- Set the environment variables in Railway:
    - `DB_URL`
    - `DB_USER`
    - `DB_PASSWORD`
- Railway will detect the `Dockerfile` and build/deploy the app automatically.

## Phase 3: Final Verification
- Verify the live URL.
- Test User Registration and Trip Creation.
- Confirm database persistence.
