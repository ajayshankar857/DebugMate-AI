# DebugMate AI 🤖

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gemini%20AI-LLM%20Powered-4285F4?style=for-the-badge&logo=google&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/REST%20API-JWT%20Secured-FF6C37?style=for-the-badge"/>
</p>

<p align="center">
  <strong>An enterprise-grade Agentic AI system that acts as a senior software engineer mentor — 
  analyzing code, performing root cause analysis, and teaching developers to write better software.</strong>
</p>

---

## 🎯 Project Overview

DebugMate AI is a production-quality **Agentic AI** application built with **Java Spring Boot** and **Google Gemini LLM**. It goes far beyond a simple CRUD application — it implements a full AI agent pipeline that:

1. **Understands context** — Detects the programming language, error type, and developer skill level
2. **Reasons through problems** — Performs multi-step root cause analysis like a senior engineer
3. **Generates corrected code** — Produces a fixed version with diff visualization
4. **Teaches and mentors** — Adapts explanation depth based on learning mode (Beginner/Expert)
5. **Tracks progress** — Builds a personalized learning profile with gamification

---

## 🏗️ Enterprise Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      DebugMate AI System                        │
├──────────────┬──────────────────────────────────────────────────┤
│              │         AI Agent Pipeline                         │
│   Frontend   │  ┌─────────────┐    ┌──────────────────────┐    │
│              │  │  Prompt     │    │   Gemini 1.5 Flash   │    │
│  Monaco IDE  │→ │  Engineering│ →  │   (LLM Engine)       │ →  │
│  Diff Viewer │  │  + Context  │    │   Structured JSON    │    │
│  Charts      │  └─────────────┘    └──────────────────────┘    │
│              │         ↓                                         │
│              │  ┌─────────────────────────────────────┐         │
│              │  │  Root Cause Analysis Engine          │         │
│              │  │  Error Detection + Severity Scoring  │         │
│              │  │  Code Quality Scoring (0-100)        │         │
│              │  │  Learning Mode Adaptation            │         │
│              │  └─────────────────────────────────────┘         │
│              │         ↓                                         │
│              │  ┌──────────────┐    ┌───────────────────┐       │
│              │  │  Spring Boot │    │   MySQL Database  │       │
│              │  │  REST APIs   │ ←→ │   JPA/Hibernate   │       │
│              │  │  JWT Auth    │    │   Debug Sessions  │       │
│              │  └──────────────┘    └───────────────────┘       │
└──────────────┴──────────────────────────────────────────────────┘
```

---

## 🚀 Key Features & Enterprise AI Capabilities

| Feature | Technology | Description |
|---|---|---|
| **AI Debugging Agent** | Gemini LLM + Prompt Engineering | Autonomous code analysis with senior engineer persona |
| **Root Cause Analysis** | LLM Chain-of-Thought | Multi-step reasoning: What → Why → Where → Fix |
| **Stack Trace Analysis** | Java Exception Parser + LLM | Extracts root cause from nested exception chains |
| **SQL Query Optimizer** | SQL Pattern Matching + LLM | Detects bad JOINs, missing clauses, suggests optimized queries |
| **Code Evolution Diff** | Monaco Diff Editor | Side-by-side visual comparison of buggy vs fixed code |
| **Learning Mode Adaptation** | Dynamic Prompt Engineering | Beginner/Intermediate/Expert explanations |
| **Voice Mentoring** | Web Speech Synthesis API | AI reads explanations aloud |
| **Code Quality Scoring** | LLM-evaluated metrics | 0-100 score: Readability, Complexity, Naming, Maintainability |
| **Gamification Engine** | XP + Streaks + Achievements | Keeps developers engaged and learning |
| **JWT Authentication** | Spring Security + JJWT | Stateless, production-grade token security |
| **Analytics Dashboard** | Chart.js | Language distribution, error heatmaps, progress tracking |
| **Debug Timeline** | JPA Session History | Replay past debugging sessions |

---

## 🧠 Agentic AI Design

DebugMate AI implements **Agentic AI principles** — the system doesn't just respond to queries; it acts with a goal:

```
Input: Broken Code
   ↓
[Agent Step 1] Classify language & error category
   ↓
[Agent Step 2] Build context-aware prompt with system persona
   ↓
[Agent Step 3] Call Gemini LLM for analysis
   ↓
[Agent Step 4] Parse structured JSON response
   ↓
[Agent Step 5] Persist session, calculate XP, update learning profile
   ↓
Output: Root Cause + Corrected Code + Learning Recommendations
```

This follows the **Plan → Act → Observe → Learn** agentic loop.

---

## 🛠️ Tech Stack

### Backend
- **Java 17** — Core language
- **Spring Boot 3.3.x** — Application framework
- **Spring Security** — JWT stateless authentication
- **Spring Data JPA + Hibernate** — ORM layer
- **Google Gemini API** — LLM engine (via REST HTTP client)
- **JJWT 0.12.x** — JWT token generation & validation
- **Lombok** — Boilerplate reduction
- **Maven** — Build tool

### Frontend
- **HTML5 / CSS3 / JavaScript ES6**
- **Monaco Editor** — VS Code-grade code editor in browser
- **Monaco Diff Editor** — Side-by-side code comparison
- **Chart.js** — Analytics visualizations
- **Bootstrap 5** — Responsive layout
- **Web Speech API** — Voice explanations

### Database
- **MySQL 8.0** — Primary relational database
- Tables: `users`, `roles`, `user_profiles`, `debug_sessions`, `bug_knowledge_base`

### DevOps / Deployment
- **Docker + Docker Compose** — Containerization
- **Maven** — CI/CD build pipeline
- Environment-variable driven configuration (12-factor app)

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8.0

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/debugmate-ai.git
cd debugmate-ai
```

### 2. Configure Database
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/debugmate_db?createDatabaseIfNotExist=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:yourpassword}
```

### 3. Set Gemini API Key (Optional — runs in Simulation Mode without it)
```bash
# Windows PowerShell
$env:GEMINI_API_KEY = "your-gemini-api-key-here"

# Linux/macOS
export GEMINI_API_KEY="your-gemini-api-key-here"
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

### 5. Access the Application
Open `http://localhost:8080` in your browser.

---

## 🐳 Docker Deployment

```bash
# Build image
docker build -t debugmate-ai .

# Run with environment variables
docker run -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=yourpassword \
  -e GEMINI_API_KEY=your-key \
  debugmate-ai
```

Or use Docker Compose:
```bash
docker-compose up -d
```

---

## 📡 REST API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register new developer account |
| `POST` | `/api/auth/login` | Login and receive JWT token |

### AI Debug Engine
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/debug/analyze` | Analyze code with AI agent |
| `GET`  | `/api/debug/history` | Get last 10 debug sessions |

### User & Profile
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/users/profile` | Get profile stats (XP, streak, accuracy) |
| `GET`  | `/api/users/me` | Get current authenticated user details |

### Request Body — `/api/debug/analyze`
```json
{
  "code": "public void method() { String s = null; s.length(); }",
  "language": "Java",
  "learningMode": "BEGINNER",
  "sessionType": "DEBUG"
}
```
`sessionType`: `DEBUG` | `STACKTRACE` | `SQL` | `EXPLAIN`

### Response Structure
```json
{
  "sessionId": 1,
  "programmingLanguage": "Java",
  "severity": "HIGH",
  "confidenceScore": 92,
  "codeQualityScore": "58/100",
  "detectedErrors": [
    {
      "type": "NullPointerException",
      "lineNumber": 2,
      "description": "Calling .length() on null String reference",
      "fix": "Initialize `s` before use or add null check",
      "severity": "HIGH"
    }
  ],
  "correctedCode": "public void method() { String s = \"\"; if (s != null) s.length(); }",
  "explanation": "A NullPointerException occurs when...",
  "rootCause": "The variable `s` was assigned null...",
  "bestPractices": ["Use Optional<String>", "Apply null checks"],
  "relatedConcepts": ["Null Object Pattern", "Defensive Programming"],
  "interviewQuestion": "Q: When does NullPointerException occur?",
  "confidenceScore": 92,
  "learningTip": "🎯 Mentor Tip: Always initialize variables...",
  "timestamp": "2026-07-02T10:30:00"
}
```

---

## 📁 Project Structure

```
debugmate-ai/
├── src/main/java/com/debugmate/ai/
│   ├── DebugMateApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java        # Spring Security + JWT filters
│   │   ├── WebMvcConfig.java          # MVC route mapping
│   │   └── DatabaseSeeder.java        # Auto-seeds roles on startup
│   ├── controller/
│   │   ├── AuthController.java        # /api/auth/*
│   │   ├── UserController.java        # /api/users/*
│   │   └── DebugController.java       # /api/debug/*
│   ├── service/
│   │   ├── GeminiService.java         # LLM integration + prompt engineering
│   │   ├── DebugService.java          # AI orchestration + session persistence
│   │   ├── AuthService.java           # Registration + JWT login
│   │   └── UserServiceImpl.java       # Profile management
│   ├── entity/
│   │   ├── User.java                  # User aggregate root
│   │   ├── Role.java                  # Authorization roles
│   │   ├── UserProfile.java           # XP, streak, accuracy stats
│   │   └── DebugSession.java          # Persisted AI analysis sessions
│   ├── dto/                           # Request/Response DTOs
│   ├── repository/                    # JPA Repositories
│   ├── security/                      # JWT token provider & filters
│   └── exception/                     # Global exception handling
├── src/main/resources/
│   ├── application.yml                # Config (env-variable driven)
│   └── static/
│       ├── index.html                 # Dashboard (protected)
│       ├── debug.html                 # AI Debugger (Monaco Editor)
│       ├── login.html                 # Login page
│       ├── register.html              # Registration page
│       ├── css/style.css              # Glassmorphism dark theme
│       └── js/auth.js                 # Client-side auth utilities
├── src/test/java/com/debugmate/ai/
│   └── AuthServiceTest.java           # JUnit 5 + Mockito unit tests
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## 🧪 Testing

```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest
```

**Test Coverage:**
- `AuthServiceTest` — Registration, duplicate validation, JWT login flow

---

## 🎮 Gamification System

| Action | XP Earned |
|--------|-----------|
| Analyze code | +10 XP |
| Each error found | +5 XP |
| Daily login streak | Streak counter + badge |
| 10 errors solved | 🏆 "Bug Hunter" badge |
| 7-day streak | 🔥 "Consistent Coder" badge |

---

## 🌱 Simulation Mode

DebugMate AI works fully without a Gemini API key. It runs in **Mentor Simulation Mode** — providing rich, hand-crafted senior engineer responses for Java, Python, JavaScript, SQL, and Spring Boot code. This ensures the project is fully demonstrable offline or in interviews.

---

## 📈 Roadmap

- [x] Module 1: JWT Authentication, User Profiles, Database Setup
- [x] Module 2: AI Debugging Core, Monaco Editor, Diff View
- [ ] Module 3: Stack Trace Analysis, SQL Debugger
- [ ] Module 4: Dashboard Analytics with Charts
- [ ] Module 5: Gamification, Achievements, AI Chat Assistant
- [ ] Module 6: Bug Library, Interview Prep, PDF Report Export

---

## 👨‍💻 Author

Built as a Final Year / Company Assessment Portfolio Project demonstrating enterprise-grade Java Spring Boot development, Agentic AI integration, and production-quality software engineering practices.

---

## 📄 License

MIT License — Free to use for educational and portfolio purposes.
