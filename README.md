# MyHealthAI - UI Automation Project

This project is a **TestNG-based automation suite** for testing the **MyHealthAI** application using **Selenium WebDriver** and **Extent Reports**. It validates key workflows including login, dashboard tile interaction, patient communication, group creation, and logout functionalities.

---

## 🚀 Tech Stack

- **Language:** Java
- **Build Tool:** Maven
- **Test Framework:** TestNG
- **Reporting:** Extent Reports
- **Automation:** Selenium WebDriver

---

## 🧪 Test Scenarios Covered

1. **Login** – Validates successful login with valid credentials.
2. **Dashboard Tile - Total Patients** – Verifies patient count consistency.
3. **Patient List - Select for Communication** – Verifies selection and modal display.
4. **SMS Group Creation** – Validates SMS communication group flow.
5. **Confirmation Popup** – Ensures correct patient count in popup.
6. **Group List (SMS)** – Verifies if SMS group is created correctly.
7. **Dashboard Tile - Non-Consented but Eligible** – Validates filtered patient list.
8. **Email Communication Modal** – Validates modal shows correct patients with email.
9. **Email Sending** – Verifies email communication is sent successfully.
10. **Group List (Email)** – Validates email group creation.
11. **Logout** – Confirms user can successfully logout.

---

## 🔐 Login Credentials

> ⚠️ **Note:** These are sample credentials for automation testing purposes only.

- **URL:** [https://doctorstaging.myhealthai.io/](https://doctorstaging.myhealthai.io/)
- **Username:** `carlos@test.com`
- **Password:** `Admin@456`

---

## 📁 Project Structure

MyHealthAI/
├── pom.xml
├── testng.xml
├── /reports/
│ └── extent-report.html
├── /src/
│ ├── /main/java/pages/
│ │ ├── BasePage.java
│ │ ├── LoginPage.java
│ │ ├── DashboardPage.java
│ │ ├── PatientListPage.java
│ │ ├── CommunicationModal.java
│ │ └── GroupPage.java
│ └── /test/java/tests/
│ ├── BaseTest.java
│ ├── LoginTest.java
│ ├── DashboardTest.java
│ ├── CommunicationTest.java
│ └── LogoutTest.java


---

## ⚙️ Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/raghursks/MyHealthAI.git
   cd MyHealthAI

