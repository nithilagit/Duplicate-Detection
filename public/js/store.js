/**
 * AI-Based Duplicate Question Detection System
 * Centralized Client-Side Data Store & Business Logic Engine
 */

const SEED_QUESTIONS = [
    {
        id: 1,
        questionText: "Explain the working principle of TCP congestion control with AIMD (Additive Increase Multiplicative Decrease) and slow start mechanisms.",
        subject: "Computer Networks",
        unit: "Unit III",
        courseOutcome: "CO3",
        bloomLevel: "Understand",
        marks: 10,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 2,
        createdAt: "Sep 27, 2026 10:15"
    },
    {
        id: 2,
        questionText: "Describe how congestion control is handled in TCP including slow start, congestion avoidance, and fast retransmit phases.",
        subject: "Computer Networks",
        unit: "Unit III",
        courseOutcome: "CO3",
        bloomLevel: "Understand",
        marks: 10,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 1,
        createdAt: "Sep 27, 2026 10:20"
    },
    {
        id: 3,
        questionText: "Explain Dijkstra's shortest path routing algorithm with an example network topology graph and compute routing table for a given source node.",
        subject: "Computer Networks",
        unit: "Unit IV",
        courseOutcome: "CO4",
        bloomLevel: "Apply",
        marks: 13,
        difficulty: "Hard",
        questionType: "Problem",
        usageCount: 3,
        createdAt: "Sep 27, 2026 11:00"
    },
    {
        id: 4,
        questionText: "Differentiate between IPv4 and IPv6 header formats and discuss the transition strategies including dual-stack, tunneling, and header translation.",
        subject: "Computer Networks",
        unit: "Unit IV",
        courseOutcome: "CO4",
        bloomLevel: "Analyze",
        marks: 8,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 0,
        createdAt: "Sep 27, 2026 11:30"
    },
    {
        id: 5,
        questionText: "Define an equivalence relation and determine whether a given relation R on set A is reflexive, symmetric, and transitive with a detailed proof.",
        subject: "Discrete Mathematics",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Understand",
        marks: 8,
        difficulty: "Easy",
        questionType: "Problem",
        usageCount: 2,
        createdAt: "Sep 27, 2026 12:00"
    },
    {
        id: 6,
        questionText: "Explain the conditions for an equivalence relation with examples of reflexivity, symmetry, and transitivity on integers modulo n.",
        subject: "Discrete Mathematics",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Understand",
        marks: 8,
        difficulty: "Easy",
        questionType: "Descriptive",
        usageCount: 1,
        createdAt: "Sep 27, 2026 12:15"
    },
    {
        id: 7,
        questionText: "State and prove the Pigeonhole Principle. Apply the generalized pigeonhole principle to solve a problem with 35 students taking 5 different elective courses.",
        subject: "Discrete Mathematics",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Apply",
        marks: 10,
        difficulty: "Medium",
        questionType: "Problem",
        usageCount: 1,
        createdAt: "Sep 27, 2026 12:30"
    },
    {
        id: 8,
        questionText: "Explain the insertion and balancing operations in an AVL tree with rotation examples (LL, RR, LR, and RL rotations).",
        subject: "Advanced Data Structures and Algorithms",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Apply",
        marks: 10,
        difficulty: "Hard",
        questionType: "Descriptive",
        usageCount: 2,
        createdAt: "Sep 27, 2026 13:00"
    },
    {
        id: 9,
        questionText: "Describe how balance factors and tree rotations maintain height balance in AVL trees during node insertion and deletion operations.",
        subject: "Advanced Data Structures and Algorithms",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Apply",
        marks: 10,
        difficulty: "Hard",
        questionType: "Descriptive",
        usageCount: 0,
        createdAt: "Sep 27, 2026 13:15"
    },
    {
        id: 10,
        questionText: "Explain the implementation and amortized time complexity analysis of Fibonacci Heaps for Decrease-Key and Delete-Min operations.",
        subject: "Advanced Data Structures and Algorithms",
        unit: "Unit III",
        courseOutcome: "CO3",
        bloomLevel: "Analyze",
        marks: 12,
        difficulty: "Hard",
        questionType: "Descriptive",
        usageCount: 1,
        createdAt: "Sep 27, 2026 13:45"
    },
    {
        id: 11,
        questionText: "Compare CISC and RISC architectures with respect to embedded microcontroller design, pipelining, and instruction execution speed.",
        subject: "Embedded System Design",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Analyze",
        marks: 8,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 2,
        createdAt: "Sep 27, 2026 14:00"
    },
    {
        id: 12,
        questionText: "Explain the differences between RISC and CISC processors in embedded systems design and instruction sets with typical ARM vs x86 examples.",
        subject: "Embedded System Design",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Analyze",
        marks: 8,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 1,
        createdAt: "Sep 27, 2026 14:15"
    },
    {
        id: 13,
        questionText: "Discuss the architecture of ARM Cortex-M microcontrollers and explain the operation of the Nested Vectored Interrupt Controller (NVIC).",
        subject: "Embedded System Design",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Understand",
        marks: 10,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 0,
        createdAt: "Sep 27, 2026 14:30"
    },
    {
        id: 14,
        questionText: "Explain the difference between supervised and unsupervised learning algorithms with real-world examples and mathematical objective functions.",
        subject: "Machine Learning Techniques",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Understand",
        marks: 8,
        difficulty: "Easy",
        questionType: "Descriptive",
        usageCount: 3,
        createdAt: "Sep 27, 2026 15:00"
    },
    {
        id: 15,
        questionText: "Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases and dataset considerations.",
        subject: "Machine Learning Techniques",
        unit: "Unit I",
        courseOutcome: "CO1",
        bloomLevel: "Understand",
        marks: 8,
        difficulty: "Easy",
        questionType: "Descriptive",
        usageCount: 2,
        createdAt: "Sep 27, 2026 15:15"
    },
    {
        id: 16,
        questionText: "Derive the backpropagation algorithm for a multi-layer perceptron neural network using the chain rule of calculus and gradient descent optimization.",
        subject: "Machine Learning Techniques",
        unit: "Unit III",
        courseOutcome: "CO3",
        bloomLevel: "Analyze",
        marks: 14,
        difficulty: "Hard",
        questionType: "Problem",
        usageCount: 1,
        createdAt: "Sep 27, 2026 15:45"
    },
    {
        id: 17,
        questionText: "Explain runtime polymorphism and dynamic method dispatch in Java with code examples demonstrating interface and abstract class implementations.",
        subject: "Object Oriented Programming using Java",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Apply",
        marks: 8,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 2,
        createdAt: "Sep 27, 2026 16:00"
    },
    {
        id: 18,
        questionText: "Describe method overriding and dynamic method dispatch and how runtime polymorphism is achieved in Java virtual machine execution.",
        subject: "Object Oriented Programming using Java",
        unit: "Unit II",
        courseOutcome: "CO2",
        bloomLevel: "Apply",
        marks: 8,
        difficulty: "Medium",
        questionType: "Descriptive",
        usageCount: 1,
        createdAt: "Sep 27, 2026 16:15"
    },
    {
        id: 19,
        questionText: "What are Java Generics? Explain how bounded type parameters and wildcards ensure compile-time type safety.",
        subject: "Object Oriented Programming using Java",
        unit: "Unit V",
        courseOutcome: "CO5",
        bloomLevel: "Understand",
        marks: 7,
        difficulty: "Easy",
        questionType: "Short Answer",
        usageCount: 0,
        createdAt: "Sep 27, 2026 16:30"
    }
];

const AppStore = {
    STORAGE_KEY: 'ai_question_bank_v2',
    USER_KEY: 'ai_auth_user',
    PAPERS_KEY: 'ai_saved_papers',

    init: function () {
        if (!localStorage.getItem(this.STORAGE_KEY)) {
            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(SEED_QUESTIONS));
        }
        if (!localStorage.getItem(this.USER_KEY)) {
            localStorage.setItem(this.USER_KEY, JSON.stringify({
                name: "Dr. Sarah Jenkins",
                email: "faculty@eec.srmrmp.edu.in",
                department: "Artificial Intelligence and Data Science",
                role: "FACULTY"
            }));
        }
    },

    getAllQuestions: function () {
        this.init();
        try {
            const data = localStorage.getItem(this.STORAGE_KEY);
            return data ? JSON.parse(data) : SEED_QUESTIONS;
        } catch (e) {
            return SEED_QUESTIONS;
        }
    },

    saveAllQuestions: function (questions) {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(questions));
    },

    getQuestionById: function (id) {
        const questions = this.getAllQuestions();
        return questions.find(q => q.id === parseInt(id)) || null;
    },

    addQuestion: function (q) {
        const questions = this.getAllQuestions();
        const maxId = questions.reduce((max, item) => Math.max(max, item.id || 0), 0);
        const newQuestion = {
            id: maxId + 1,
            questionText: q.questionText.trim(),
            subject: q.subject,
            unit: q.unit || 'Unit I',
            courseOutcome: q.courseOutcome || 'CO1',
            bloomLevel: q.bloomLevel || 'Understand',
            marks: parseInt(q.marks) || 10,
            difficulty: q.difficulty || 'Medium',
            questionType: q.questionType || 'Descriptive',
            usageCount: 0,
            createdAt: new Date().toLocaleString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            })
        };
        questions.unshift(newQuestion);
        this.saveAllQuestions(questions);
        return newQuestion;
    },

    deleteQuestion: function (id) {
        let questions = this.getAllQuestions();
        questions = questions.filter(q => q.id !== parseInt(id));
        this.saveAllQuestions(questions);
    },

    getFilteredQuestions: function (filter = {}) {
        let list = this.getAllQuestions();

        if (filter.subject) {
            list = list.filter(q => q.subject === filter.subject);
        }
        if (filter.unit) {
            list = list.filter(q => q.unit === filter.unit);
        }
        if (filter.co) {
            list = list.filter(q => q.courseOutcome === filter.co);
        }
        if (filter.bloom) {
            list = list.filter(q => q.bloomLevel === filter.bloom);
        }
        if (filter.difficulty) {
            list = list.filter(q => q.difficulty === filter.difficulty);
        }
        if (filter.search && filter.search.trim()) {
            const s = filter.search.trim().toLowerCase();
            list = list.filter(q => 
                q.questionText.toLowerCase().includes(s) ||
                q.subject.toLowerCase().includes(s) ||
                (q.unit && q.unit.toLowerCase().includes(s))
            );
        }

        return list;
    },

    getStats: function () {
        const questions = this.getAllQuestions();
        const unitData = {};
        const bloomData = {};
        const diffData = {};
        const subjectData = {};

        questions.forEach(q => {
            unitData[q.unit] = (unitData[q.unit] || 0) + 1;
            bloomData[q.bloomLevel] = (bloomData[q.bloomLevel] || 0) + 1;
            diffData[q.difficulty] = (diffData[q.difficulty] || 0) + 1;
            subjectData[q.subject] = (subjectData[q.subject] || 0) + 1;
        });

        return {
            totalQuestions: questions.length,
            unitData: unitData,
            bloomData: bloomData,
            diffData: diffData,
            subjectData: subjectData
        };
    },

    // Tokenization and Stopword Removal for Vector Semantic Analysis
    tokenize: function (text) {
        if (!text) return [];
        const stopwords = new Set([
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
            "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being",
            "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't",
            "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during",
            "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here",
            "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i",
            "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it",
            "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my",
            "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or",
            "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same",
            "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
            "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
            "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll",
            "they're", "they've", "this", "those", "through", "to", "too", "under", "until",
            "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've",
            "were", "weren't", "what", "what's", "when", "when's", "where", "where's",
            "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't",
            "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your",
            "yours", "yourself", "yourselves", "explain", "describe", "define", "discuss",
            "state", "briefly", "detail", "write", "give", "illustrate"
        ]);

        return text.toLowerCase()
            .replace(/[^a-z0-9\s]/g, ' ')
            .split(/\s+/)
            .filter(w => w.length > 2 && !stopwords.has(w));
    },

    getTermFrequency: function (tokens) {
        const tf = {};
        tokens.forEach(t => tf[t] = (tf[t] || 0) + 1);
        return tf;
    },

    cosineSimilarity: function (tf1, tf2) {
        let dotProduct = 0;
        let mag1 = 0;
        let mag2 = 0;

        for (let term in tf1) {
            if (tf2[term]) {
                dotProduct += tf1[term] * tf2[term];
            }
            mag1 += tf1[term] * tf1[term];
        }

        for (let term in tf2) {
            mag2 += tf2[term] * tf2[term];
        }

        if (mag1 === 0 || mag2 === 0) return 0;
        return dotProduct / (Math.sqrt(mag1) * Math.sqrt(mag2));
    },

    checkSimilarity: function (text, subject, excludeId = null) {
        if (!text || text.trim().length < 5) return null;

        const inputTokens = this.tokenize(text);
        const inputTf = this.getTermFrequency(inputTokens);

        const allQuestions = this.getAllQuestions();
        const candidatePool = subject ? allQuestions.filter(q => q.subject === subject) : allQuestions;

        const matches = [];

        candidatePool.forEach(q => {
            if (excludeId && q.id === parseInt(excludeId)) return;

            const qTokens = this.tokenize(q.questionText);
            const qTf = this.getTermFrequency(qTokens);
            const similarity = this.cosineSimilarity(inputTf, qTf);
            const percentage = Math.round(similarity * 1000) / 10;

            if (percentage > 10) {
                matches.push({
                    questionId: q.id,
                    questionText: q.questionText,
                    subject: q.subject,
                    unit: q.unit,
                    courseOutcome: q.courseOutcome,
                    bloomLevel: q.bloomLevel,
                    marks: q.marks,
                    similarityScore: percentage,
                    matchClassification: percentage >= 85 ? 'HIGHLY_SIMILAR' : (percentage >= 70 ? 'SIMILAR' : 'UNIQUE')
                });
            }
        });

        matches.sort((a, b) => b.similarityScore - a.similarityScore);
        const highestSimilarity = matches.length > 0 ? matches[0].similarityScore : 0.0;

        let status = 'UNIQUE';
        let explanation = 'The question appears to be unique. No significant semantic overlap detected with existing questions in the bank.';

        if (highestSimilarity >= 85.0) {
            status = 'HIGHLY_SIMILAR';
            explanation = `High semantic similarity detected (${highestSimilarity}%). This question strongly resembles an existing question in ${matches[0].subject}. Recommendation: Reject or modify before saving.`;
        } else if (highestSimilarity >= 70.0) {
            status = 'SIMILAR';
            explanation = `Moderate semantic overlap detected (${highestSimilarity}%). Review recommended to ensure distinct assessment criteria.`;
        }

        return {
            status: status,
            highestSimilarity: highestSimilarity,
            explanation: explanation,
            extractedTokens: inputTokens.slice(0, 15),
            matches: matches
        };
    },

    getUser: function () {
        this.init();
        try {
            return JSON.parse(localStorage.getItem(this.USER_KEY));
        } catch (e) {
            return {
                name: "Dr. Sarah Jenkins",
                email: "faculty@eec.srmrmp.edu.in",
                department: "Artificial Intelligence and Data Science"
            };
        }
    },

    setUser: function (u) {
        localStorage.setItem(this.USER_KEY, JSON.stringify(u));
    },

    logout: function () {
        localStorage.removeItem('session_active');
        window.location.href = '/login.html';
    }
};

// Initialize store immediately on script load
AppStore.init();
window.AppStore = AppStore;
