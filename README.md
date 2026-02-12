# SolveIt Website

This project is a full-stack web application inspired by StackOverflow.  
It allows users to post questions, submit answers, interact with other developers, and participate in a voting system.  

- **Access SolveIt** - https://deluxe-haupia-91bcb3.netlify.app/login

## 🚀 Features

- **Single Page Application** - Smooth scrolling navigation between sections
- **Dark Mode Toggle** - Beautiful dark/light theme switcher
- **Fully Responsive** - Optimized for all devices (mobile, tablet, desktop)
- **Modern UI/UX** - Inspired by contemporary design trends
- **Smooth Animations** - Elegant fade-in and hover effects
- **Four Main Sections**:
  - Home - Hero section with compelling introduction
  - Skills - Showcase your technical expertise with visual progress bars
  - Projects - Display your featured work
  - Contact - Contact form and social links

## 🛠️ Tech Stack

- **React** - JavaScript library for building user interfaces
- **CSS** - Custom styling with CSS variables for theming
- **Bootstrap** - (Optional) For additional responsive utilities
- **Lucide React** - Beautiful icon library


## 📂 Project Structure

```
root
│
├── solveit_webapp → Frontend (React)
├── stackoverflow → Backend (Spring Boot)


### 🔹 Frontend (`solveit_webapp`)
Built using React and modern UI tools.

Main folders:
- `assets/`
- `components/`
- `context/`
- `pages/`
- `service/`
- `util/`
- `App.jsx`
- `main.jsx`

### 🔹 Backend (`stackoverflow`)
Spring Boot REST API.

Main packages:
- `config`
- `controller`
- `dto`
- `entity`
- `enums`
- `exception`
- `filters`
- `repository`
- `service`
- `util`
- `resources/`
  - `application.properties`
  - `application-dev.properties`
  - `application-prod.properties`

```

## 🚀 Technologies Used

### Frontend
- React
- JavaScript
- Bootstrap
- CSS

### Backend
- Java
- Spring Boot
- JWT (Authentication)
- Cloudinary (Image Upload)
- Brevo (Email Service)
- PostgreSQL, MySQL

---

# 🧪 Environment Variables Guide

```properties
jwt.secret.key=your_secret_key
jwt.expiration.time=your_expiration_time

cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

brevo.api.key=your_brevo_key
brevo.from.email=your_brevo_from_email
```

## Backend Local (`application-dev.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password

```

## Backend Producion (`application-prod.properties`)
```properties
spring.datasource.url=jdbc:postgresql://render_db_url
spring.datasource.username=render_username
spring.datasource.password=render_password
```

## 🌍 Deployment (Production)

### Backend
- Hosted on **Render**
- Database: **PostgreSQL (Render)**
- Configuration: `application-prod.properties`

### Frontend
- Hosted on **Netlify**

---

## 💻 Local Development Setup

### 1️⃣ Backend Setup

- Use **MySQL**
- Configure database in: application-dev.properties
- Start the Spring Boot application.

---

### 2️⃣ Frontend Setup

Navigate to the frontend folder:

```bash
cd solveit_webapp
npm install
npm run dev
```

🔐 Features
- User Authentication (JWT)
- Ask & Answer Questions
- Voting System
- Update User information
- Image Upload (Cloudinary)
- Account activation through email link (Brevo)
- Secure REST API
- Role-based access (Admin/User)


