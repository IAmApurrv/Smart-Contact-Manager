# Smart Contact Manager

Smart Contact Manager is a Spring Boot-based application designed to efficiently store and handle contacts. The application incorporates modern web development practices to provide a user-friendly experience. It includes features such as a search tool, email OTP security, and payment integration using Razorpay.

## Key Features

- **Efficient Contact Storage**: Store contacts in a highly efficient manner to ensure quick access and management.
- **Search Tool**: Easily find specific contact information with the built-in search tool.
- **Enhanced Security**: Email OTP functionality adds an extra layer of security.
- **Payment Integration**: Integration with Razorpay for seamless payment processing.

## Technologies Used

- **Backend**: Spring Boot, Java
- **Frontend**: Thymeleaf, HTML5, CSS3, Bootstrap
- **Database**: MySQL
- **Payment Gateway**: Razorpay

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven
- MySQL
- An email service for OTP functionality
- Razorpay account for payment integration

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/IAmApurrv/Smart-Contact-Manager.git
   cd Smart-Contact-Manager
   ```

2. **Set up the database**:
   - Create a MySQL database named `scm`.
   - Update the database configuration in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/scm
     spring.datasource.username=username
     spring.datasource.password=password
     ```

3. **Update Email Service Configuration**:
   - In `src/main/java/com/smart/service/EmailService.java`, change the password for your Gmail account:
     ```java
     return new PasswordAuthentication("your-gmail-username", "your-gmail-password");
     ```

4. **Install dependencies and run the application**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application**:
   Open your browser and navigate to `http://localhost:8080`.

## Usage

- **Login/Registration**: Users can register and log in using their email addresses.
- **Add Contacts**: Once logged in, users can add new contacts with details like name, phone number, email, and address.
- **Search Contacts**: Use the search tool to quickly find specific contacts.
- **OTP Security**: Enhanced security with email OTP for critical operations.
- **Payments**: Users can make payments via the integrated Razorpay payment gateway.

## Screenshots

### Home
![Home](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Home.png?raw=true)

### Sign-up
![Sign-up](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Sign-up.png?raw=true)

### Login
![Login](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Login.png?raw=true)

### Forgot Email Form
![Forgot-Email-Form](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Forgot-Email-Form.png?raw=true)

### Verify OTP
![Verify-OTP](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Verify-OTP.png?raw=true)

### Dashboard
![Dashboard](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Dashboard.png?raw=true)

### Profile
![Profile](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Profile.png?raw=true)

### Add Contact
![Add-Contact](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Add-Contact.png?raw=true)

### View Contacts
![View-Contacts](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/View-Contacts.png?raw=true)

### Update Contact
![Update-Contact](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Update-Contact.png?raw=true)

### Contact Details
![Contact-Details](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Contact-Details.png?raw=true)

### Settings
![Settings](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Settings.png?raw=true)

### Feedback
![Feedback](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Feedback.png?raw=true)

### Payment
![Payment](https://github.com/IAmApurrv/Smart-Contact-Manager/blob/main/Screenshots/Payment.png?raw=true)
