# PayZen API

A comprehensive Spring Boot application for managing employee payroll, attendance, and salary structures.

## Features

- **Employee Management**: CRUD operations for employee records
- **Attendance Tracking**: Mark and track employee attendance
- **Salary Structure Management**: Define and manage salary structures
- **Pagination Support**: All list endpoints support pagination
- **API Documentation**: Swagger/OpenAPI documentation
- **Database Integration**: PostgreSQL with MyBatis
- **Exception Handling**: Global exception handling with proper error responses

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **ORM**: MyBatis
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup

1. Create a PostgreSQL database named `payroll_db`
2. Run the SQL scripts in the `scripts` folder:
   \`\`\`bash
   psql -U postgres -d payroll_db -f scripts/init-database.sql
   psql -U postgres -d payroll_db -f scripts/seed-data.sql
   \`\`\`

### Running the Application

1. Clone the repository
2. Update database configuration in `application.yml`
3. Run the application:
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

### Using Docker

1. Build and run with Docker Compose:
   \`\`\`bash
   docker-compose up --build
   \`\`\`

## API Documentation

Once the application is running, access the Swagger UI at:
- http://localhost:8080/swagger-ui.html

## API Endpoints

### Employee Management
- `GET /api/v1/employees` - Get all employees (paginated)
- `GET /api/v1/employees/{id}` - Get employee by ID
- `POST /api/v1/employees` - Create new employee
- `PUT /api/v1/employees/{id}` - Update employee
- `DELETE /api/v1/employees/{id}` - Delete employee

### Attendance Management
- `GET /api/v1/attendance` - Get all attendance records (paginated)
- `GET /api/v1/attendance/employee/{employeeId}` - Get attendance by employee
- `POST /api/v1/attendance` - Mark attendance

### Salary Structure Management
- `GET /api/v1/salary-structures` - Get all salary structures (paginated)
- `GET /api/v1/salary-structures/{id}` - Get salary structure by ID
- `POST /api/v1/salary-structures` - Create new salary structure
- `PUT /api/v1/salary-structures/{id}` - Update salary structure
- `DELETE /api/v1/salary-structures/{id}` - Delete salary structure

## Configuration

### Application Properties

The application supports multiple profiles:
- `dev` - Development environment
- `prod` - Production environment

### Environment Variables

- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `DATABASE_URL` - Database connection URL

## Project Structure

\`\`\`
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── aub/
│   │           └── payrollapi/
│   │               ├── base/                  # Base classes or utilities
│   │               ├── config/               # Configuration classes
│   │               ├── controller/           # REST controllers
│   │               ├── exception/            # Exception handling
│   │               ├── jwt/                  # JWT-related classes
│   │               ├── model/
│   │               │   ├── dto/
│   │               │   │   ├── request/      # Request DTOs
│   │               │   │   └── response/     # Response DTOs
│   │               │   ├── entity/           # Entity classes
│   │               │   ├── enums/            # Enumerations
│   │               │   ├── mapper/           # Mapping utilities (e.g., MapStruct)
│   │               │   └── enums/            # Enumerations (appears twice, likely a typo in image; keeping as shown)
│   │               ├── repository/           # Data access layer (e.g., MyBatis repositories)
│   │               ├── service/
│   │               │   ├── implementation/   # Service implementations
│   │               ├── utils/                # Utility classes for services
│   │               └── PayrollApiApplication # Main Spring Boot application class
│   └── resources/
│       ├── images/                # Static image files
│       ├── static/                # Static resources (e.g., CSS, JS)
│       ├── templates/             # Thymeleaf or other template files
│       ├── application.properties # Main configuration
│       └── schema.sql             # Database schema
├── test/
│   ├── java/
│   │   └── org/
│   │       └── aub/
│   │           └── payrollapi/
│   │               └── PayrollApiApplicationTests # Test class
└── .gitignore                    # Git ignore file (if present in root)
\`\`\`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
