-- 0. Required extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1. Enum for App User Roles
CREATE TYPE user_role AS ENUM ('admin', 'manager');

-- 2. App Users
CREATE TABLE IF NOT EXISTS app_users
(
    user_id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    is_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    otp_code        VARCHAR(10),
    profile_img_url VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. User Role Mapping (many-to-many)
CREATE TABLE user_roles_map
(
    user_id UUID      NOT NULL,
    role    user_role NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES app_users (user_id) ON DELETE CASCADE
);

-- 4. Positions (for Employees, not user roles)
CREATE TABLE positions
(
    position_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    position_name VARCHAR(100) UNIQUE NOT NULL,
    description   TEXT
);

-- 5. Enum for Employee Status
CREATE TYPE employee_status AS ENUM ('active', 'inactive');

-- 6. Employees
CREATE TABLE employees
(
    employee_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manager_user_id UUID,
    first_name      VARCHAR(100)        NOT NULL,
    last_name       VARCHAR(100)        NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    phone_number    VARCHAR(20),
    date_of_birth   DATE,
    date_joined     DATE                NOT NULL,
    department      VARCHAR(100),
    position_id     UUID,
    status          employee_status  DEFAULT 'active',
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES positions (position_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_manager FOREIGN KEY (manager_user_id) REFERENCES app_users (user_id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- 7. Employee Document Uploads
CREATE TABLE employee_documents
(
    document_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    file_name   VARCHAR(255),
    file_type   VARCHAR(100),
    file_url    TEXT,
    uploaded_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_emp_doc FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 8. Enum for Attendance Status
CREATE TYPE attendance_status AS ENUM ('present', 'absent', 'leave');

-- 9. Attendance Records
CREATE TABLE attendance_records
(
    attendance_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID              NOT NULL,
    attendance_date TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    status          attendance_status NOT NULL,
    check_in_time   TIME,
    check_out_time  TIME,
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_emp FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unique_attendance UNIQUE (employee_id, attendance_date)
);

-- 10. Salary Structure
CREATE TABLE salary_structure
(
    structure_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    position_id           UUID,
    employee_id           UUID,
    base_salary           DECIMAL(10, 2) NOT NULL,
    tax_rate              DECIMAL(5, 2)    DEFAULT 0.00,
    allowance             DECIMAL(10, 2)   DEFAULT 0.00,
    bonus                 DECIMAL(10, 2)   DEFAULT 0.00,
    late_penalty_per_day  DECIMAL(10, 2)   DEFAULT 0.00,
    leave_penalty_per_day DECIMAL(10, 2)   DEFAULT 0.00,
    is_custom             BOOLEAN          DEFAULT FALSE,
    CONSTRAINT fk_salary_position FOREIGN KEY (position_id) REFERENCES positions (position_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_salary_emp FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 11. Salary Log
CREATE TABLE employee_salary_log
(
    salary_log_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id      UUID     NOT NULL,
    month            SMALLINT NOT NULL CHECK (month >= 1 AND month <= 12),
    year             SMALLINT NOT NULL,
    base_salary      DECIMAL(10, 2),
    present_days     INT,
    absent_days      INT,
    leave_days       INT,
    tax_amount       DECIMAL(10, 2),
    total_deductions DECIMAL(10, 2),
    total_bonus      DECIMAL(10, 2),
    net_salary       DECIMAL(10, 2),
    generated_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_salary_log FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unique_salary UNIQUE (employee_id, month, year)
);

-- 12. Payslips
CREATE TABLE payslips
(
    payslip_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id   UUID     NOT NULL,
    salary_log_id UUID     NOT NULL,
    month         SMALLINT NOT NULL CHECK (month >= 1 AND month <= 12),
    year          SMALLINT NOT NULL,
    pdf_url       TEXT,
    generated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payslip_emp FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_payslip_log FOREIGN KEY (salary_log_id) REFERENCES employee_salary_log (salary_log_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 13. Attendance Summary View
CREATE VIEW monthly_attendance_summary AS
SELECT employee_id,
       EXTRACT(MONTH FROM attendance_date)            AS month,
       EXTRACT(YEAR FROM attendance_date)             AS year,
       COUNT(CASE WHEN status = 'present' THEN 1 END) AS present_days,
       COUNT(CASE WHEN status = 'absent' THEN 1 END)  AS absent_days,
       COUNT(CASE WHEN status = 'leave' THEN 1 END)   AS leave_days
FROM attendance_records
GROUP BY employee_id, EXTRACT(YEAR FROM attendance_date), EXTRACT(MONTH FROM attendance_date);