# AI-Based Duplicate Question Detection System

> An enterprise Java web application designed to assist faculty members in maintaining institutional question banks, detecting semantic duplicates and paraphrased questions using Natural Language Processing (NLP) embeddings and Cosine Similarity, dynamically balancing question papers across Course Outcomes (CO) and Bloom's Taxonomy, and auditing question reuse with visual analytics.

---

## 1. Project Objective

In higher education institutions, preparing balanced, high-quality examination question papers is a critical academic responsibility. Traditional approaches either perform no duplicate checking or rely strictly on exact keyword matching, which fails when questions express identical concepts using different words (synonymous paraphrasing).

The **AI-Based Duplicate Question Detection System**:
- Maintains a centralized, categorized institutional question bank.
- Uses **Natural Language Processing (NLP)** and vector space models to identify **semantic similarity** between questions rather than superficial exact matches.
- Alerts faculty with precise similarity percentages, color-coded visual risk levels, and ranked recommendations.
- Empowers faculty with non-blocking decision control (*Edit Question*, *Use Existing Question*, or *Save Anyway*).
- Provides an interactive **Question Paper Builder** that maps Unit distributions, Course Outcomes (CO1–CO5), and Bloom's Taxonomy cognitive balance in real-time.
- Visualizes duplicate detection rates, underutilized questions, and frequency trends via interactive reports.

---

## 2. System Architecture & Reference Flow

```
Faculty
   ↓
Login Module (Spring Security + BCrypt)
   ↓
Question Entry Module
   ↓
AI Duplicate Detection Engine (Text Normalization → Tokenization → N-Gram TF-IDF Vector → Cosine Metric)
   ↓
Question Database (JPA / MySQL)
   ↓
Duplicate / Review / Unique Decision (Non-Blocking Decision Panel)
   ↓
Question Paper Builder (Unit Coverage, CO Mapping %, Bloom's Balance Advisory)
   ↓
Reports & Analytics (Interactive Chart.js Dashboards & Usage Analysis)
```

### Layered Architecture
```
com.example.duplicatedetection
├── ai            # Pluggable NLP Similarity Engine, Vectorizer, and Preprocessor
├── config        # Spring Security, JPA, and App Properties configuration
├── controller    # Web MVC (Thymeleaf) & REST API (@RestController)
├── dto           # Data Transfer Objects & API Response Wrappers
├── entity        # Relational JPA Entities (Faculty, Question, Embeddings, Papers, Logs)
├── exception     # Global Exception Handler and custom HTTP status responses
├── repository    # Spring Data JPA Repositories with custom JPQL queries
├── security      # CustomUserDetailsService and Security utilities
├── service       # Business Logic Layer (DuplicateDetection, Question, Paper, Analytics)
└── util          # DataInitializer with 45+ realistic questions & audit logs
```

---

## 3. Technology Stack

- **Backend Platform:** Java 17 (Microsoft OpenJDK 17)
- **Framework:** Spring Boot 3.2.4 (Spring MVC, Spring Data JPA, Spring Security)
- **Database Engine:** MySQL 8.0+ (with persistent embedded mode enabled by default for zero-config evaluation)
- **Build System:** Apache Maven 3.9+
- **Frontend / UI:** Thymeleaf, HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons
- **Data Visualizations:** Chart.js 4.4
- **Vector Mathematics:** Apache Commons Math3 (L2 normalization, Vector dot products, Cosine angle computation)

---

## 4. AI / NLP Semantic Similarity Methodology

The AI similarity module computes genuine mathematical similarity between question texts without mocked values or static placeholders.

### Step-by-Step Processing Pipeline:
1. **Text Normalization:**
   Lowercases input, removes special characters and excess whitespace.
2. **Domain-Aware Stopword Filtering & Canonical Stemming:**
   Filters non-discriminative academic stopwords (e.g. *what, is, explain, define, describe*) while preserving core technical domain entities (*TCP, IP, OS, Deadlock, B-Tree, ACID, Normalization*).
3. **N-Gram Term Extraction:**
   Extracts both unigrams and bigrams (e.g., `congestion_control`, `operating_system`, `normal_form`). Bigrams receive elevated weights (1.35×) to preserve domain-specific phrases.
4. **Vector Embedding Generation:**
   Constructs sublinear Term Frequency vectors ($tf = 1 + \ln(\text{count})$) and applies Euclidean $L_2$ vector normalization:
   $$\|\mathbf{v}\|_2 = \sqrt{\sum_{i=1}^{n} v_i^2} = 1.0$$
5. **Cosine Similarity Computation:**
   Calculates the normalized dot product between the query vector $\mathbf{u}$ and existing question vectors $\mathbf{v}$:
   $$\cos(\theta) = \sum_{t \in (\mathbf{u} \cap \mathbf{v})} u_t \cdot v_t$$
   Combined with a Jaccard token overlap harmonic factor for short-query stability.
6. **Configurable Threshold Decision Rules:**
   - **Similarity $\ge 85.0\%$**: **HIGHLY SIMILAR / DUPLICATE** (Red alert badge)
   - **Similarity $70.0\% - 84.9\%$**: **SIMILAR / REVIEW REQUIRED** (Orange warning badge)
   - **Similarity $< 70.0\%$**: **UNIQUE QUESTION** (Green success badge)

---

## 5. Demo Credentials

The application seeds a default demo faculty account during startup:

| Field | Demo Credential |
|---|---|
| **URL** | `http://localhost:8080/login` |
| **Email** | `faculty@college.edu` |
| **Password** | `Admin@123` |
| **Faculty ID**| `FAC-CSE-101` |
| **Department** | Computer Science and Engineering |

*(A convenient "Auto-Fill" button is provided directly on the login screen for rapid testing).*

---

## 6. How to Run the Application

### Prerequisites:
- Java 17 or later installed (`java -version`)
- Maven 3.9+ installed (`mvn -version`)

### Quick Start:
```bash
# 1. Navigate to the project root
cd C:\Users\divya\.gemini\antigravity-ide\scratch\duplicate-question-detection

# 2. Build the application and run unit tests
mvn clean compile test

# 3. Start the Spring Boot Web Application
mvn spring-boot:run
```

Once started, open your web browser and navigate to:
```
http://localhost:8080
```

---

## 7. Database Setup & MySQL Configuration

### Default Out-of-the-Box Mode (Dev Profile)
By default, the application runs on an embedded persistent file database (`./data/questionbank`) operating in **MySQL compatibility mode**. It requires no separate database daemon to be installed, allowing immediate execution and testing.

### Production MySQL Mode
To connect to an active MySQL server:
1. Ensure MySQL is running on port `3306`.
2. Create the database and tables using the provided script:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/seed_data.sql
   ```
3. Run the Spring Boot application with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
   Or set the environment variables:
   - `MYSQL_URL=jdbc:mysql://localhost:3306/question_detection_db`
   - `MYSQL_USER=root`
   - `MYSQL_PASSWORD=your_password`

---

## 8. Key Screens and Modules

1. **Faculty Login (`/login`)**:
   Clean academic portal authentication with password visibility toggle and demo auto-fill.
2. **Faculty Dashboard (`/dashboard`)**:
   KPI cards (Total Questions, Duplicates Detected, Unique Questions, Papers Built, Top Question), Chart.js breakdowns (Unit distribution, Bloom's levels, Difficulty, Duplicate status), and real-time audit logs.
3. **Question Bank (`/questions`)**:
   Advanced combined multi-attribute filtering (Subject, Unit I–V, Course Outcome CO1–CO5, Bloom's level, Difficulty, Marks, Question Type), full-text search, pagination, and modal dialogs.
4. **Add Question with Real-Time AI Detection (`/questions/add`)**:
   Interactive question entry where typing activates asynchronous AI duplicate scanning. Shows similarity score gauge, progress bar, matched questions with metadata, and faculty decision controls (*Edit Question*, *Save Anyway*).
5. **AI Duplicate Detection Sandbox (`/ai/detect`)**:
   Visual flow explanation of the NLP pipeline, pre-loaded test cases for viva demonstrations, and token-level feature breakdown.
6. **Question Paper Builder (`/papers/builder`)**:
   Question picker with live balance analytics (Total marks, Unit distribution, CO % mapping, Bloom's taxonomy balance, and balance advisory warnings).
7. **Printable Question Paper (`/papers/print/{id}`)**:
   Print-ready university examination layout with college header, instructions, course outcome tags, and marks breakdown.
8. **Reports & Analytics (`/reports`)**:
   Comprehensive institutional reports including frequently asked questions, underutilized questions, duplicate statistics, and unit-wise distribution charts.
9. **Faculty Profile (`/profile`)**:
   Faculty details management and secure BCrypt password updating.

---

## 9. REST API Documentation

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/questions` | Query questions with multi-attribute filtering and pagination |
| `GET` | `/api/questions/{id}` | Retrieve question details by ID |
| `POST` | `/api/questions` | Add a new question to the repository |
| `PUT` | `/api/questions/{id}` | Update an existing question |
| `DELETE` | `/api/questions/{id}` | Delete a question and its embedding |
| `POST` | `/api/questions/check-similarity` | Analyze duplicate risk for a question text |
| `GET` | `/api/questions/{id}/similar` | Fetch top similar questions for an existing question |
| `GET` | `/api/question-papers` | List all generated question papers |
| `POST` | `/api/question-papers` | Create a new question paper with question joins |
| `POST` | `/api/question-papers/compute-distribution` | Calculate live Unit, CO, and Bloom distributions |
| `GET` | `/api/reports/duplicates` | Retrieve duplicate detection audit metrics |
| `GET` | `/api/reports/frequent` | Retrieve highest usage questions |
| `GET` | `/api/reports/unit-wise` | Retrieve unit and cognitive distributions |
| `GET` | `/api/reports/usage` | Retrieve question usage statistics |
| `GET` | `/api/faculty/profile` | Retrieve authenticated faculty profile |
| `PUT` | `/api/faculty/profile` | Update faculty profile |

---

## 10. Sample Demonstration Scenarios (For College Viva)

### Scenario A: High Similarity Duplicate Detection (TCP Congestion)
1. Navigate to **Add Question** or **AI Duplicate Detection**.
2. Select Subject: `Computer Networks`.
3. Enter Question:
   > *"Describe how congestion control is handled in TCP."*
4. **Result:** AI Engine detects match with Question #1 (*"Explain the working principle of TCP congestion control."*).
5. **Output:** Similarity score **> 85%**, **HIGHLY SIMILAR / DUPLICATE** (Red alert), recommending Question #1.

### Scenario B: Review Required Detection (Deadlock Conditions)
1. Enter Question:
   > *"Explain the four conditions necessary for system resource deadlocks."*
2. **Result:** AI Engine detects match with Question #5 (*"What are the necessary conditions for a deadlock to occur in an operating system?"*).
3. **Output:** Similarity score **~81%**, **SIMILAR / REVIEW REQUIRED** (Orange warning).

### Scenario C: Unique Question Detection
1. Enter Question:
   > *"Explain quantum computing qubit superposition and entanglement gates."*
2. **Result:** Similarity score **< 20%**, **UNIQUE QUESTION** (Green success), allows immediate addition.

---

## 11. Future Enhancements
- Integration of ONNX Runtime native transformer bindings for multi-lingual sentence embeddings.
- Automatic Blooms Taxonomy level classifier using fine-tuned transformer text classification.
- AI-driven question generation and automated rubric scoring.
