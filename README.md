# Twilio_SMS_Client_Service
Twilio SMS Client - Java Web Application  for Telecom SMS Services - ITI Telecom-45..

## Overview
The **Twilio SMS Client** is a web-based application that allows users to send and manage SMS messages using their Twilio accounts. The system has two types of users:

- **Customers**: Can send SMS, view their message history, and manage their profile.
- **Administrators**: Can manage customer accounts and view SMS statistics.

## Features

### 1. Authentication & User Management
- Users must **log in** using a **username and password**.
- After login, users are redirected to a **home page** based on their role (Customer or Administrator).
- Customers must **validate their phone number (MSISDN)** using an SMS code sent via Twilio.
- Customers can **log out and switch accounts** at any time.

### 2. Administrator Capabilities
- View a **list of all Customers**.
- **Add, edit, and remove** Customers.
- View **SMS statistics** (number of messages sent per Customer).

### 3. Customer Capabilities
- **Sign up** and enter profile details:
  - Name, Birthday, Password, Phone Number (MSISDN), Job, Email, Address.
  - Twilio Account SID, Token, and Allowed SenderID.
- **Send SMS** messages by specifying:
  - **From** (SenderID), **To** (Receiver), and **Body**.
- **View SMS history** with details:
  - From, To, Body, Date.
- **Search SMS history** by:
  - Sender, Receiver, or Date range.
- **Delete specific SMS messages** from history.
- **Edit profile information** after registration.
- **Support for Inbound SMS (Callback URL)**.
- Customers can view a list of all **received SMS messages** on their Twilio account.

---

## Requirements
Before installing and running the application, ensure you have the following installed:

- **Operating System**: CentOS 9
- **Java Development Kit (JDK)**: JDK 23
- **Java EE Servlet Container**: Apache Tomcat
- **Database**: MySQL and postgres
- **Development Environment**: NetBeans 24
- **Twilio Account** with API credentials (SID & Auth Token)

---

## Installation & Setup

### **Step 1: Install Required Packages**
Before setting up the project, ensure the required dependencies are installed on CentOS 9.

#### **1.1 Install Java (JDK 23)**
```bash
sudo dnf install jdk-23_linux-x64_bin.rpm
```
Verify the installation:
```bash
java -version
```

#### **1.2 Install Apache Tomcat**
Download and install **Tomcat 9**:
```bash
sudo dnf install tomcat tomcat-webapps tomcat-admin-webapps -y
```
Start and enable Tomcat:
```bash
sudo systemctl enable --now tomcat
```

#### **1.3 Install MySQL**
```bash
sudo dnf install mysql-server -y
sudo systemctl enable --now mysqld
```
Secure the MySQL installation:
```bash
sudo mysql_secure_installation
```

---

### **Step 2: Clone the Repository**
Download the project from your Git repository:
```bash
git https://github.com/ToniEmad/Twilio_SMS_Client_Service.git
cd TSMS
```

---

### **Step 3: Configure the Database**
1. **Log in to MySQL**:
   ```bash
   mysql -u root -p
   ```
2. **Create the database**:
   ```sql
   CREATE DATABASE twilio;
   ```
3. **Create a database user**:
   ```sql
   CREATE USER 'twilio_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON twilio_sms_client.* TO 'twilio_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. **Create the required tables**:
   ```sql
   USE twilio_sms_client;

   CREATE TABLE customer (
    msisdn VARCHAR(20) ,
    username VARCHAR(255) NOT NULL UNIQUE,
    birthday DATE,
    password TEXT NOT NULL,
    job VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    address TEXT,
    twillio_sid TEXT,
    twillio_token TEXT,
    sms_status ENUM('active', 'inactive'),
    sender_id VARCHAR(20) PRIMARY KEY,
    sms_validate TINYINT(1) DEFAULT 0,
    acc_id INT,
    FOREIGN KEY (acc_id) REFERENCES account(acc_id) ON DELETE SET NULL
   );


   CREATE TABLE account (
    acc_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role ENUM('admin', 'user') NOT NULL
   );

   
   CREATE TABLE sms (
    s_id INT PRIMARY KEY AUTO_INCREMENT,
    from_msisdn VARCHAR(20) NOT NULL,
    to_msisdn VARCHAR(20) NOT NULL,
    body TEXT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('sent', 'failed', 'pending') NOT NULL,
    acc_id INT,
    FOREIGN KEY (from_msisdn) REFERENCES customer(sender_id) ON DELETE CASCADE,
    FOREIGN KEY (acc_id) REFERENCES account(acc_id) ON DELETE SET NULL
   );


   
   ```

---

### **Step 4: Configure the Application**
Edit the **database configuration** file in `config.properties`:
```
db.url=jdbc:mysql://localhost:3306/TSMS
db.user=twilio_user
db.password=password
```
Edit **Twilio API credentials**:
```
twilio.sid=your_twilio_account_sid
twilio.token=your_twilio_auth_token
twilio.sender_id=your_twilio_sender_id
```

---

### **Step 5: Build and Deploy the Application**
#### **5.1 Compile the project**
1. Open **NetBeans 24**.
2. Load the **TSMS** project.
3. Configure the **Java EE Server** to use **Tomcat**.
4. Build the project using:
   ```
   Clean and Build
   ```

#### **5.2 Deploy the WAR file to Tomcat**
Copy the compiled `.war` file to Tomcat:
```bash
sudo cp /path/to/twilio_sms_client.war /var/lib/tomcat/webapps/
```
Restart Tomcat:
```bash
sudo systemctl restart tomcat
```

---

### **Step 6: Access the Application**
Once deployed, open your browser and navigate to:
```
http://localhost:8080/TSMS
```
- **Customer Login**: Use the credentials created during sign-up.
- **Administrator Login**: Use admin credentials stored in the database.

---

## License
This project is licensed under the **MIT License**.
