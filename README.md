# TripSplitter - Smart Trip Expense Sharing System

A complete Advanced Java web application for managing and splitting trip expenses among group members. Built using JSP, Servlet, JDBC, and MySQL with MVC architecture.

## Features

- **User Authentication** - Register, Login, Logout with session management
- **Dashboard** - Overview of trips, expenses, and quick actions
- **Trip Management** - Create trips, join via invite code, add members
- **Expense Tracking** - Add expenses with categories, auto-split equally
- **Balance Sheet** - View who owes whom with net balance calculation
- **Settlement** - Record and track payment settlements
- **Export** - Download trip reports as PDF and CSV

## Technology Stack

| Technology | Purpose |
|-----------|---------|
| Advanced Java | Core programming language |
| JSP | View layer (pages) |
| Servlet | Controller layer (request handling) |
| JDBC | Database connectivity |
| MySQL / PostgreSQL | Database |
| Apache Tomcat | Application server |
| HTML5 / CSS3 | Frontend structure and styling |
| Bootstrap 5 | Responsive UI framework |
| JavaScript | Client-side interactions |
| MVC | Architecture pattern |

## Project Structure

```
TripSplitter/
├── src/
│   ├── controller/          # Servlet classes
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── DashboardServlet.java
│   │   ├── CreateTripServlet.java
│   │   ├── JoinTripServlet.java
│   │   ├── TripDetailsServlet.java
│   │   ├── AddExpenseServlet.java
│   │   ├── SettleServlet.java
│   │   ├── DeleteTripServlet.java
│   │   ├── DeleteExpenseServlet.java
│   │   ├── ExportPdfServlet.java
│   │   └── ExportExcelServlet.java
│   ├── dao/                  # Data Access Objects
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   ├── TripDAO.java
│   │   ├── ExpenseDAO.java
│   │   └── SettlementDAO.java
│   ├── model/                # Model/Entity classes
│   │   ├── User.java
│   │   ├── Trip.java
│   │   ├── Expense.java
│   │   ├── Settlement.java
│   │   └── Balance.java
│   ├── filter/               # Authentication filter
│   │   └── AuthFilter.java
│   └── utility/              # Utility classes
│       ├── ValidationUtil.java
│       └── PasswordUtil.java
├── WebContent/
│   ├── css/
│   │   └── style.css
│   ├── js/
│   │   └── app.js
│   ├── includes/
│   │   ├── header.jsp
│   │   ├── footer.jsp
│   │   └── messages.jsp
│   ├── login.jsp
│   ├── register.jsp
│   ├── dashboard.jsp
│   ├── createTrip.jsp
│   ├── tripDetails.jsp
│   ├── addExpense.jsp
│   └── balance.jsp
├── WEB-INF/
│   ├── lib/                  # JAR files
│   └── web.xml
├── database.sql
└── README.md
```

## Setup Instructions

### Prerequisites

1. **JDK 8+** - Java Development Kit
2. **Eclipse EE** - IDE for Java EE development
3. **Apache Tomcat 9+** - Servlet container
4. **MySQL 8+** (with XAMPP) or **PostgreSQL** - Database server
5. **JDBC Driver** - PostgreSQL JDBC driver JAR

### Step 1: Database Setup

**For MySQL (XAMPP):**
1. Start XAMPP and launch MySQL
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Import the `database.sql` file
4. This creates the `trip_splitter` database with all tables

**For PostgreSQL (Supabase):**
- The database is already provisioned
- Tables are created automatically via migrations

### Step 2: Configure Database Connection

The app reads database settings from environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/trip_splitter
DB_USER=root
DB_PASSWORD=
```

For Aiven MySQL, copy the host, port, database name, user, and password from
the Aiven service overview and set:

```text
DB_URL=jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/YOUR_DATABASE?sslMode=REQUIRED&allowPublicKeyRetrieval=true
DB_USER=avnadmin
DB_PASSWORD=YOUR_AIVEN_PASSWORD
```

If Aiven requires CA verification, download the CA certificate to a location
outside this repository and use:

```text
DB_URL=jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/YOUR_DATABASE?sslMode=VERIFY_CA&sslCa=E:/secure/aiven-ca.pem
```

Never commit Aiven passwords, private keys, `.pem`, `.crt`, `.key`, `.p12`, or
`.jks` files. Use `.env.example` only as a template.

### Step 3: Eclipse Setup

1. Open Eclipse EE
2. Go to File > New > Dynamic Web Project
3. Name it `TripSplitter`
4. Set Target Runtime to Apache Tomcat 9+
5. Copy all source files into the project:
   - Java files go into `src/`
   - JSP files go into `WebContent/`
   - CSS/JS go into `WebContent/css/` and `WebContent/js/`
   - `web.xml` goes into `WEB-INF/`

### Step 4: Add JDBC Driver

1. Download the JDBC driver JAR:
   - MySQL: `mysql-connector-java-8.x.jar`
   - PostgreSQL: `postgresql-42.x.jar`
2. Place the JAR in `WEB-INF/lib/`
3. Add to Build Path in Eclipse

### Step 5: Run the Application

1. Right-click the project > Run As > Run on Server
2. Select Apache Tomcat
3. The application opens at: `http://localhost:8080/TripSplitter/`

## Application Flow

1. **Register** - Create a new account
2. **Login** - Sign in with credentials
3. **Dashboard** - View trips and statistics
4. **Create Trip** - Start a new trip (auto-generates invite code)
5. **Join Trip** - Enter invite code to join existing trip
6. **Add Expense** - Record expenses (auto-split equally)
7. **View Balance** - See who owes whom
8. **Settle Up** - Record payments and mark as settled
9. **Export** - Download reports as PDF or CSV

## Expense Split Logic

When an expense is added, it is split equally among all trip members:

```
Example:
- Total expense: Rs. 4,000
- Members: 4
- Per person share: Rs. 1,000

If Rahul paid Rs. 4,000:
- Priya owes Rs. 1,000
- Amit owes Rs. 1,000
- Sneha owes Rs. 1,000
- Rahul gets back Rs. 3,000
```

## Security Features

- Session-based authentication
- Authentication filter for protected pages
- PreparedStatement for SQL injection prevention
- Input validation and sanitization
- Password hashing with SHA-256

## Viva Questions & Answers

**Q: What is MVC architecture?**
A: Model-View-Controller. Model = Java classes (User, Trip, etc.), View = JSP pages, Controller = Servlets.

**Q: What is the role of AuthFilter?**
A: It intercepts requests to protected pages and redirects unauthenticated users to the login page.

**Q: How is expense splitting implemented?**
A: When an expense is added, the total amount is divided equally among all trip members. Each member's share is stored in the expense_splits table.

**Q: What is JDBC?**
A: Java Database Connectivity - an API for connecting Java applications to databases.

**Q: Why PreparedStatement?**
A: It prevents SQL injection by parameterizing queries, and improves performance through query plan caching.

**Q: What is session management?**
A: Using HttpSession to maintain user state across multiple requests. The session stores the logged-in user object.

## Required JAR Files

| JAR File | Purpose |
|----------|---------|
| postgresql-42.x.jar | PostgreSQL JDBC driver |
| mysql-connector-java-8.x.jar | MySQL JDBC driver (alternative) |
| servlet-api.jar | Servlet API (bundled with Tomcat) |
| jsp-api.jar | JSP API (bundled with Tomcat) |
| itextpdf-5.x.jar | PDF generation (optional) |
| poi-5.x.jar | Excel generation (optional) |

## Author

TripSplitter - Advanced Java Mini Project

## License

This project is for educational purposes.
