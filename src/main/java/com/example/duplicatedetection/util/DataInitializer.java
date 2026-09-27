package com.example.duplicatedetection.util;

import com.example.duplicatedetection.ai.SemanticVectorEngine;
import com.example.duplicatedetection.entity.*;
import com.example.duplicatedetection.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final FacultyRepository facultyRepository;
    private final QuestionRepository questionRepository;
    private final QuestionEmbeddingRepository embeddingRepository;
    private final SimilarityCheckRepository similarityCheckRepository;
    private final QuestionPaperRepository paperRepository;
    private final QuestionPaperQuestionRepository paperQuestionRepository;
    private final QuestionUsageRepository usageRepository;
    private final PasswordEncoder passwordEncoder;
    private final SemanticVectorEngine vectorEngine;
    private final ObjectMapper objectMapper;

    @Value("${app.demo.faculty.email:faculty@eec.srmrmp.edu.in}")
    private String demoEmail;

    @Value("${app.demo.faculty.password:Admin@123}")
    private String demoPassword;

    @Value("${app.demo.faculty.department:Artificial Intelligence and Data Science}")
    private String demoDepartment;

    @Value("${app.demo.faculty.name:Dr. Sarah Jenkins}")
    private String demoName;

    @Value("${app.demo.faculty.facultyId:FAC-AIDS-101}")
    private String demoFacultyId;

    public DataInitializer(FacultyRepository facultyRepository,
                           QuestionRepository questionRepository,
                           QuestionEmbeddingRepository embeddingRepository,
                           SimilarityCheckRepository similarityCheckRepository,
                           QuestionPaperRepository paperRepository,
                           QuestionPaperQuestionRepository paperQuestionRepository,
                           QuestionUsageRepository usageRepository,
                           PasswordEncoder passwordEncoder,
                           SemanticVectorEngine vectorEngine,
                           ObjectMapper objectMapper) {
        this.facultyRepository = facultyRepository;
        this.questionRepository = questionRepository;
        this.embeddingRepository = embeddingRepository;
        this.similarityCheckRepository = similarityCheckRepository;
        this.paperRepository = paperRepository;
        this.paperQuestionRepository = paperQuestionRepository;
        this.usageRepository = usageRepository;
        this.passwordEncoder = passwordEncoder;
        this.vectorEngine = vectorEngine;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Faculty faculty = initFaculty();

        List<String> distinctSubjects = questionRepository.findDistinctSubjects();
        boolean needsReseed = questionRepository.count() == 0 ||
                distinctSubjects.contains("Operating Systems") ||
                distinctSubjects.contains("Database Management Systems") ||
                !distinctSubjects.contains("Machine Learning Techniques") ||
                !distinctSubjects.contains("Discrete Mathematics");

        if (needsReseed) {
            usageRepository.deleteAll();
            paperQuestionRepository.deleteAll();
            paperRepository.deleteAll();
            similarityCheckRepository.deleteAll();
            embeddingRepository.deleteAll();
            questionRepository.deleteAll();

            List<Question> questions = seedQuestions(faculty);
            seedEmbeddings(questions);
            seedQuestionPapers(faculty, questions);
            seedSimilarityChecks(faculty, questions);
        }
    }

    private Faculty initFaculty() {
        Optional<Faculty> existing = facultyRepository.findByEmail(demoEmail);
        if (existing.isPresent()) {
            Faculty f = existing.get();
            f.setDepartment(demoDepartment);
            f.setName(demoName);
            return facultyRepository.save(f);
        }

        // Migrate any previous faculty (e.g. from older default email) to the new email & department
        List<Faculty> allFaculty = facultyRepository.findAll();
        if (!allFaculty.isEmpty()) {
            Faculty f = allFaculty.get(0);
            f.setEmail(demoEmail);
            f.setDepartment(demoDepartment);
            f.setName(demoName);
            f.setFacultyId(demoFacultyId);
            f.setPassword(passwordEncoder.encode(demoPassword));
            return facultyRepository.save(f);
        }

        Faculty demo = new Faculty();
        demo.setFacultyId(demoFacultyId);
        demo.setName(demoName);
        demo.setEmail(demoEmail);
        demo.setPassword(passwordEncoder.encode(demoPassword));
        demo.setDepartment(demoDepartment);
        demo.setRole("ROLE_FACULTY");
        demo.setEnabled(true);

        return facultyRepository.save(demo);
    }

    private List<Question> seedQuestions(Faculty faculty) {
        List<Question> list = new ArrayList<>();

        // =========================================================================
        // 1. COMPUTER NETWORKS (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #1
        list.add(new Question("Explain the working principle of TCP congestion control.",
                "Computer Networks", "Unit III", "CO3", "Understand", 10, "Medium", "Descriptive", faculty));
        // Intentional duplicate pair #1
        list.add(new Question("Describe how congestion control is handled in TCP.",
                "Computer Networks", "Unit III", "CO3", "Understand", 10, "Medium", "Descriptive", faculty));

        list.add(new Question("Explain Dijkstra's shortest path routing algorithm with an example.",
                "Computer Networks", "Unit IV", "CO4", "Apply", 13, "Hard", "Problem", faculty));
        list.add(new Question("Compare the OSI reference model with the TCP/IP protocol suite across all layers.",
                "Computer Networks", "Unit I", "CO1", "Analyze", 8, "Medium", "Descriptive", faculty));
        list.add(new Question("Explain the mechanism of sliding window protocol with Go-Back-N and Selective Repeat ARQ.",
                "Computer Networks", "Unit II", "CO2", "Apply", 10, "Hard", "Descriptive", faculty));
        list.add(new Question("Explain the three-way handshake connection establishment and teardown process in TCP.",
                "Computer Networks", "Unit III", "CO3", "Analyze", 8, "Medium", "Descriptive", faculty));
        list.add(new Question("What is subnetting? Calculate the subnet mask and host range for IP address 192.168.10.0/26.",
                "Computer Networks", "Unit III", "CO3", "Apply", 6, "Medium", "Problem", faculty));
        list.add(new Question("Describe the architecture and message exchange flow of the Domain Name System (DNS).",
                "Computer Networks", "Unit V", "CO5", "Understand", 7, "Easy", "Descriptive", faculty));

        // =========================================================================
        // 2. DISCRETE MATHEMATICS (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #2
        list.add(new Question("Define an equivalence relation and determine whether a relation is reflexive, symmetric, and transitive.",
                "Discrete Mathematics", "Unit I", "CO1", "Understand", 8, "Medium", "Descriptive", faculty));
        // Intentional duplicate pair #2
        list.add(new Question("Explain the conditions for an equivalence relation with examples of reflexivity, symmetry, and transitivity.",
                "Discrete Mathematics", "Unit I", "CO1", "Understand", 8, "Medium", "Descriptive", faculty));

        list.add(new Question("State and prove the Pigeonhole Principle with an application in counting distinct pairs.",
                "Discrete Mathematics", "Unit II", "CO2", "Apply", 6, "Medium", "Problem", faculty));
        list.add(new Question("Solve the linear recurrence relation a_n = 5a_{n-1} - 6a_{n-2} with initial conditions a_0 = 1 and a_1 = 4.",
                "Discrete Mathematics", "Unit II", "CO2", "Apply", 10, "Hard", "Problem", faculty));
        list.add(new Question("Explain Euler and Hamiltonian paths and circuits in connected undirected graphs with diagrams.",
                "Discrete Mathematics", "Unit IV", "CO4", "Analyze", 10, "Medium", "Descriptive", faculty));
        list.add(new Question("Prove by mathematical induction that 1 + 2 + ... + n = n(n+1)/2 for all positive integers n.",
                "Discrete Mathematics", "Unit I", "CO1", "Apply", 8, "Easy", "Problem", faculty));
        list.add(new Question("Construct the truth table for (P -> Q) ^ (Q -> R) -> (P -> R) and determine whether it is a tautology.",
                "Discrete Mathematics", "Unit I", "CO1", "Analyze", 7, "Easy", "Problem", faculty));
        list.add(new Question("Explain Dijkstra's algorithm for finding the minimal spanning tree versus Prim's and Kruskal's methods.",
                "Discrete Mathematics", "Unit V", "CO5", "Evaluate", 12, "Hard", "Descriptive", faculty));

        // =========================================================================
        // 3. ADVANCED DATA STRUCTURES AND ALGORITHMS (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #3
        list.add(new Question("Explain the insertion and balancing operations in an AVL tree with rotation examples.",
                "Advanced Data Structures and Algorithms", "Unit II", "CO2", "Apply", 10, "Hard", "Descriptive", faculty));
        // Intentional duplicate pair #3
        list.add(new Question("Describe how balance factors and tree rotations maintain height balance in AVL trees during insertion.",
                "Advanced Data Structures and Algorithms", "Unit II", "CO2", "Apply", 10, "Hard", "Descriptive", faculty));

        list.add(new Question("Explain the 0/1 Knapsack problem using dynamic programming and analyze its time and space complexity.",
                "Advanced Data Structures and Algorithms", "Unit III", "CO3", "Analyze", 12, "Hard", "Problem", faculty));
        list.add(new Question("Describe the structural properties and node insertion rules of Red-Black Trees.",
                "Advanced Data Structures and Algorithms", "Unit II", "CO2", "Understand", 8, "Medium", "Descriptive", faculty));
        list.add(new Question("Explain the Bellman-Ford algorithm for single-source shortest paths and how it detects negative weight cycles.",
                "Advanced Data Structures and Algorithms", "Unit IV", "CO4", "Apply", 10, "Hard", "Problem", faculty));
        list.add(new Question("Describe the design and prefix search operations of a Trie data structure with a diagram.",
                "Advanced Data Structures and Algorithms", "Unit I", "CO1", "Understand", 7, "Medium", "Descriptive", faculty));
        list.add(new Question("Compare B-Trees and B+ Trees in terms of record storage, search efficiency, and range queries.",
                "Advanced Data Structures and Algorithms", "Unit V", "CO5", "Analyze", 9, "Medium", "Descriptive", faculty));
        list.add(new Question("Define asymptotic notations Big-O, Big-Omega, and Big-Theta with mathematical definitions and graphical interpretations.",
                "Advanced Data Structures and Algorithms", "Unit I", "CO1", "Remember", 6, "Easy", "Short Answer", faculty));

        // =========================================================================
        // 4. EMBEDDED SYSTEM DESIGN (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #4
        list.add(new Question("Compare CISC and RISC architectures with respect to embedded microcontroller design and execution speed.",
                "Embedded System Design", "Unit I", "CO1", "Analyze", 8, "Medium", "Descriptive", faculty));
        // Intentional duplicate pair #4
        list.add(new Question("Explain the differences between RISC and CISC processors in embedded systems design and instruction sets.",
                "Embedded System Design", "Unit I", "CO1", "Analyze", 8, "Medium", "Descriptive", faculty));

        list.add(new Question("Explain the working and protocol timing of I2C and SPI serial communication buses in embedded hardware.",
                "Embedded System Design", "Unit III", "CO3", "Analyze", 10, "Medium", "Descriptive", faculty));
        list.add(new Question("Describe Interrupt Service Routine (ISR) latency, interrupt priorities, and context switching in ARM microcontrollers.",
                "Embedded System Design", "Unit II", "CO2", "Understand", 8, "Hard", "Descriptive", faculty));
        list.add(new Question("Explain Pulse Width Modulation (PWM) generation and how it is used for DC motor speed control.",
                "Embedded System Design", "Unit II", "CO2", "Apply", 8, "Medium", "Problem", faculty));
        list.add(new Question("Compare Rate Monotonic Scheduling (RMS) and Earliest Deadline First (EDF) in Real-Time Operating Systems (RTOS).",
                "Embedded System Design", "Unit IV", "CO4", "Evaluate", 10, "Hard", "Descriptive", faculty));
        list.add(new Question("Describe the purpose and fail-safe operation of a Watchdog Timer in mission-critical embedded systems.",
                "Embedded System Design", "Unit V", "CO5", "Understand", 6, "Easy", "Short Answer", faculty));
        list.add(new Question("Explain memory architectures for embedded systems: SRAM, DRAM, Flash, and EEPROM with power and speed trade-offs.",
                "Embedded System Design", "Unit I", "CO1", "Remember", 7, "Easy", "Descriptive", faculty));

        // =========================================================================
        // 5. MACHINE LEARNING TECHNIQUES (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #5
        list.add(new Question("Explain the difference between supervised and unsupervised learning algorithms with real-world examples.",
                "Machine Learning Techniques", "Unit I", "CO1", "Understand", 8, "Easy", "Descriptive", faculty));
        // Intentional duplicate pair #5
        list.add(new Question("Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases.",
                "Machine Learning Techniques", "Unit I", "CO1", "Understand", 8, "Easy", "Descriptive", faculty));

        list.add(new Question("Explain the Bias-Variance tradeoff in machine learning and discuss how L1 (Lasso) and L2 (Ridge) regularization prevent overfitting.",
                "Machine Learning Techniques", "Unit II", "CO2", "Analyze", 10, "Hard", "Descriptive", faculty));
        list.add(new Question("Describe Gradient Descent optimization algorithms: Batch Gradient Descent, Stochastic Gradient Descent (SGD), and Adam optimizer.",
                "Machine Learning Techniques", "Unit II", "CO2", "Understand", 10, "Medium", "Descriptive", faculty));
        list.add(new Question("Explain the K-Means clustering algorithm step-by-step and describe the Elbow method for choosing the optimal number of clusters K.",
                "Machine Learning Techniques", "Unit III", "CO3", "Apply", 8, "Medium", "Problem", faculty));
        list.add(new Question("Illustrate the working of Support Vector Machines (SVM), the maximum margin hyperplane, and the kernel trick for non-linear classification.",
                "Machine Learning Techniques", "Unit III", "CO3", "Analyze", 12, "Hard", "Descriptive", faculty));
        list.add(new Question("Explain how Decision Trees calculate Information Gain and Gini Impurity, and how Random Forest ensembles reduce variance.",
                "Machine Learning Techniques", "Unit IV", "CO4", "Evaluate", 10, "Medium", "Descriptive", faculty));
        list.add(new Question("Define precision, recall, F1-score, and ROC-AUC curve with confusion matrix formulas for imbalanced classification evaluation.",
                "Machine Learning Techniques", "Unit V", "CO5", "Analyze", 8, "Medium", "Short Answer", faculty));

        // =========================================================================
        // 6. OBJECT ORIENTED PROGRAMMING USING JAVA (8 questions, 1 intentional duplicate pair)
        // =========================================================================
        // Intentional duplicate reference #6
        list.add(new Question("Explain runtime polymorphism and dynamic method dispatch in Java with code examples.",
                "Object Oriented Programming using Java", "Unit II", "CO2", "Apply", 8, "Medium", "Descriptive", faculty));
        // Intentional duplicate pair #6
        list.add(new Question("Describe method overriding and dynamic method dispatch and how runtime polymorphism is achieved in Java.",
                "Object Oriented Programming using Java", "Unit II", "CO2", "Apply", 8, "Medium", "Descriptive", faculty));

        list.add(new Question("Explain the four core principles of Object-Oriented Programming: Encapsulation, Inheritance, Polymorphism, and Abstraction.",
                "Object Oriented Programming using Java", "Unit I", "CO1", "Understand", 10, "Easy", "Descriptive", faculty));
        list.add(new Question("Describe the Java Exception handling hierarchy and the difference between checked and unchecked exceptions with try-catch-finally.",
                "Object Oriented Programming using Java", "Unit III", "CO3", "Understand", 8, "Medium", "Descriptive", faculty));
        list.add(new Question("Compare ArrayList, LinkedList, and HashMap in the Java Collections Framework regarding internal implementation and performance.",
                "Object Oriented Programming using Java", "Unit IV", "CO4", "Analyze", 10, "Medium", "Descriptive", faculty));
        list.add(new Question("Explain multithreading in Java: Thread lifecycle, thread synchronization using synchronized blocks, and inter-thread communication.",
                "Object Oriented Programming using Java", "Unit III", "CO3", "Analyze", 10, "Hard", "Problem", faculty));
        list.add(new Question("Explain the differences between Abstract Classes and Interfaces in Java, including default and static methods introduced in Java 8.",
                "Object Oriented Programming using Java", "Unit II", "CO2", "Evaluate", 8, "Medium", "Descriptive", faculty));
        list.add(new Question("What are Java Generics? Explain how bounded type parameters and wildcards ensure compile-time type safety.",
                "Object Oriented Programming using Java", "Unit V", "CO5", "Understand", 7, "Easy", "Short Answer", faculty));

        return questionRepository.saveAll(list);
    }

    private void seedEmbeddings(List<Question> questions) {
        List<QuestionEmbedding> embeddings = new ArrayList<>();
        for (Question q : questions) {
            try {
                Map<String, Double> vector = vectorEngine.generateEmbeddingVector(q.getQuestionText());
                String json = objectMapper.writeValueAsString(vector);
                embeddings.add(new QuestionEmbedding(q, json, vectorEngine.getEngineName()));
            } catch (Exception e) {
                // Ignore serialization error during seeding
            }
        }
        embeddingRepository.saveAll(embeddings);
    }

    private void seedQuestionPapers(Faculty faculty, List<Question> questions) {
        // Create Sample Paper 1: Computer Networks Midterm Exam
        QuestionPaper paper1 = new QuestionPaper(
                "Mid-Term Examination: Computer Networks",
                "Computer Networks",
                "CS-501-MID",
                "2024-2025",
                "Semester V",
                50,
                "Answer all questions in Part A (5x2=10 marks) and any four questions from Part B (4x10=40 marks).",
                faculty
        );
        QuestionPaper savedPaper1 = paperRepository.save(paper1);

        List<Question> cnQuestions = questions.stream()
                .filter(q -> "Computer Networks".equals(q.getSubject()))
                .limit(6)
                .toList();

        int num = 1;
        for (Question q : cnQuestions) {
            q.setUsageCount(q.getUsageCount() + 1);
            questionRepository.save(q);

            QuestionPaperQuestion qpq = new QuestionPaperQuestion(savedPaper1, q, num++, num <= 3 ? "Part A" : "Part B", q.getMarks());
            paperQuestionRepository.save(qpq);
            usageRepository.save(new QuestionUsage(q, savedPaper1));
        }

        // Create Sample Paper 2: Machine Learning Techniques Final Assessment
        QuestionPaper paper2 = new QuestionPaper(
                "End-Semester Examination: Machine Learning Techniques",
                "Machine Learning Techniques",
                "AI-601-END",
                "2024-2025",
                "Semester VI",
                60,
                "Part A carries 20 marks. Part B carries 40 marks. Scientific calculators are permitted.",
                faculty
        );
        QuestionPaper savedPaper2 = paperRepository.save(paper2);

        List<Question> mlQuestions = questions.stream()
                .filter(q -> "Machine Learning Techniques".equals(q.getSubject()))
                .limit(6)
                .toList();

        num = 1;
        for (Question q : mlQuestions) {
            q.setUsageCount(q.getUsageCount() + 1);
            questionRepository.save(q);

            QuestionPaperQuestion qpq = new QuestionPaperQuestion(savedPaper2, q, num++, num <= 2 ? "Part A" : "Part B", q.getMarks());
            paperQuestionRepository.save(qpq);
            usageRepository.save(new QuestionUsage(q, savedPaper2));
        }
    }

    private void seedSimilarityChecks(Faculty faculty, List<Question> questions) {
        Question q1 = questions.get(0); // TCP congestion (Computer Networks)
        Question q2 = questions.stream()
                .filter(q -> "Machine Learning Techniques".equals(q.getSubject()))
                .findFirst().orElse(questions.get(1));

        // Check 1: High Similarity detection
        SimilarityCheck check1 = new SimilarityCheck(
                "Describe how congestion control is handled in TCP.",
                q1,
                q1.getQuestionText(),
                91.4,
                "HIGHLY_SIMILAR",
                "High semantic similarity detected (91.4%). Both questions discuss TCP congestion control mechanisms.",
                faculty
        );

        // Check 2: Moderate Similarity detection
        SimilarityCheck check2 = new SimilarityCheck(
                "Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases.",
                q2,
                q2.getQuestionText(),
                86.8,
                "HIGHLY_SIMILAR",
                "Semantic similarity (86.8%) detected with supervised vs unsupervised learning fundamentals.",
                faculty
        );

        // Check 3: Unique question detection
        SimilarityCheck check3 = new SimilarityCheck(
                "Explain quantum computing qubit superposition and entanglement gates in cryptographic key distribution.",
                null,
                null,
                14.5,
                "UNIQUE",
                "The question appears to be unique. No significant semantic overlap detected.",
                faculty
        );

        similarityCheckRepository.saveAll(Arrays.asList(check1, check2, check3));
    }
}
