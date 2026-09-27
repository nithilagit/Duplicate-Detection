/**
 * AI-Based Duplicate Question Detection System
 * Main Frontend Logic
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Password Visibility Toggle
    const togglePasswordButtons = document.querySelectorAll('.toggle-password-btn');
    togglePasswordButtons.forEach(btn => {
        btn.addEventListener('click', function () {
            const inputTarget = document.querySelector(this.getAttribute('data-target'));
            if (inputTarget) {
                if (inputTarget.type === 'password') {
                    inputTarget.type = 'text';
                    this.innerHTML = '<i class="bi bi-eye-slash"></i>';
                } else {
                    inputTarget.type = 'password';
                    this.innerHTML = '<i class="bi bi-eye"></i>';
                }
            }
        });
    });

    // 2. Initialize Bootstrap Tooltips if Bootstrap is available
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
});

/**
 * Duplicate Detection Helper for Add Question Form & AI Page
 */
const DuplicateDetector = {
    knowledgeBase: [
        { id: 1, subject: "Computer Networks", unit: "Unit III", courseOutcome: "CO3", bloomLevel: "Understand", marks: 10, questionText: "Explain the working principle of TCP congestion control." },
        { id: 2, subject: "Computer Networks", unit: "Unit III", courseOutcome: "CO3", bloomLevel: "Understand", marks: 10, questionText: "Describe how congestion control is handled in TCP." },
        { id: 3, subject: "Computer Networks", unit: "Unit IV", courseOutcome: "CO4", bloomLevel: "Apply", marks: 13, questionText: "Explain Dijkstra's shortest path routing algorithm with an example." },
        { id: 5, subject: "Discrete Mathematics", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Understand", marks: 8, questionText: "Define an equivalence relation and determine whether a relation is reflexive, symmetric, and transitive." },
        { id: 6, subject: "Discrete Mathematics", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Understand", marks: 8, questionText: "Explain the conditions for an equivalence relation with examples of reflexivity, symmetry, and transitivity." },
        { id: 7, subject: "Advanced Data Structures and Algorithms", unit: "Unit II", courseOutcome: "CO2", bloomLevel: "Apply", marks: 10, questionText: "Explain the insertion and balancing operations in an AVL tree with rotation examples." },
        { id: 8, subject: "Advanced Data Structures and Algorithms", unit: "Unit II", courseOutcome: "CO2", bloomLevel: "Apply", marks: 10, questionText: "Describe how balance factors and tree rotations maintain height balance in AVL trees during insertion." },
        { id: 9, subject: "Embedded System Design", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Analyze", marks: 8, questionText: "Compare CISC and RISC architectures with respect to embedded microcontroller design and execution speed." },
        { id: 10, subject: "Embedded System Design", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Analyze", marks: 8, questionText: "Explain the differences between RISC and CISC processors in embedded systems design and instruction sets." },
        { id: 11, subject: "Machine Learning Techniques", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Understand", marks: 8, questionText: "Explain the difference between supervised and unsupervised learning algorithms with real-world examples." },
        { id: 12, subject: "Machine Learning Techniques", unit: "Unit I", courseOutcome: "CO1", bloomLevel: "Understand", marks: 8, questionText: "Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases." },
        { id: 13, subject: "Object Oriented Programming using Java", unit: "Unit II", courseOutcome: "CO2", bloomLevel: "Apply", marks: 8, questionText: "Explain runtime polymorphism and dynamic method dispatch in Java with code examples." },
        { id: 14, subject: "Object Oriented Programming using Java", unit: "Unit II", courseOutcome: "CO2", bloomLevel: "Apply", marks: 8, questionText: "Describe method overriding and dynamic method dispatch and how runtime polymorphism is achieved in Java." }
    ],

    checkSimilarity: async function (questionText, subject, excludeId = null) {
        if (!questionText || questionText.trim().length < 5) {
            return null;
        }

        try {
            const response = await fetch('/api/questions/check-similarity', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    questionText: questionText.trim(),
                    subject: subject || '',
                    excludeQuestionId: excludeId
                })
            });

            if (response.ok) {
                const json = await response.json();
                if (json && json.data) return json.data;
            }
        } catch (err) {
            console.log('Falling back to local client-side semantic vector engine');
        }

        return this.clientSideCheck(questionText, subject);
    },

    tokenize: function (text) {
        return text.toLowerCase()
            .replace(/[^a-z0-9\s]/g, ' ')
            .split(/\s+/)
            .filter(w => w.length > 2 && !["the","and","for","with","are","this","that","what","how","explain","describe","state","define"].includes(w));
    },

    getVector: function (tokens) {
        const map = {};
        tokens.forEach(t => map[t] = (map[t] || 0) + 1);
        return map;
    },

    cosineSimilarity: function (v1, v2) {
        let dot = 0, mag1 = 0, mag2 = 0;
        for (let k in v1) {
            if (v2[k]) dot += v1[k] * v2[k];
            mag1 += v1[k] * v1[k];
        }
        for (let k in v2) {
            mag2 += v2[k] * v2[k];
        }
        if (mag1 === 0 || mag2 === 0) return 0;
        return dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    },

    clientSideCheck: function (text, subject) {
        const inputTokens = this.tokenize(text);
        const inputVector = this.getVector(inputTokens);

        const pool = subject ? this.knowledgeBase.filter(q => q.subject === subject) : this.knowledgeBase;
        const matches = [];

        pool.forEach(q => {
            const qTokens = this.tokenize(q.questionText);
            const qVector = this.getVector(qTokens);
            const sim = this.cosineSimilarity(inputVector, qVector);
            const score = Math.round(sim * 1000) / 10;
            if (score > 15) {
                matches.push({
                    questionId: q.id,
                    questionText: q.questionText,
                    subject: q.subject,
                    unit: q.unit,
                    courseOutcome: q.courseOutcome,
                    bloomLevel: q.bloomLevel,
                    marks: q.marks,
                    similarityScore: score,
                    matchClassification: score >= 85 ? 'HIGHLY_SIMILAR' : (score >= 70 ? 'SIMILAR' : 'UNIQUE')
                });
            }
        });

        matches.sort((a, b) => b.similarityScore - a.similarityScore);
        const topScore = matches.length > 0 ? matches[0].similarityScore : 0.0;
        let status = 'UNIQUE';
        let explanation = 'The question appears to be unique. No significant semantic overlap detected with existing questions in the bank.';

        if (topScore >= 85.0) {
            status = 'HIGHLY_SIMILAR';
            explanation = `High semantic similarity detected (${topScore}%). This question strongly resembles an existing question in ${matches[0].subject}. Recommendation: Reject or modify.`;
        } else if (topScore >= 70.0) {
            status = 'SIMILAR';
            explanation = `Moderate semantic overlap detected (${topScore}%). Review required before approval to ensure differentiation.`;
        }

        return {
            status: status,
            highestSimilarity: topScore,
            explanation: explanation,
            extractedTokens: inputTokens,
            matches: matches.slice(0, 3)
        };
    }
};


/**
 * Question Paper Builder Interactive State Manager
 */
const PaperBuilder = {
    selectedQuestions: new Map(),

    init: function () {
        this.renderSelectedTable();
        this.updateDistribution();
    },

    toggleQuestion: function (btn) {
        const qId = parseInt(btn.getAttribute('data-id'));
        const qText = btn.getAttribute('data-text');
        const qSubject = btn.getAttribute('data-subject');
        const qUnit = btn.getAttribute('data-unit');
        const qCo = btn.getAttribute('data-co');
        const qBloom = btn.getAttribute('data-bloom');
        const qMarks = parseInt(btn.getAttribute('data-marks') || 10);
        const qDiff = btn.getAttribute('data-difficulty');

        if (this.selectedQuestions.has(qId)) {
            this.selectedQuestions.delete(qId);
            btn.classList.remove('btn-success');
            btn.classList.add('btn-outline-primary');
            btn.innerHTML = '<i class="bi bi-plus-circle"></i> Add';
        } else {
            this.selectedQuestions.set(qId, {
                questionId: qId,
                questionNumber: this.selectedQuestions.size + 1,
                sectionName: this.selectedQuestions.size < 4 ? 'Part A' : 'Part B',
                allocatedMarks: qMarks,
                questionText: qText,
                subject: qSubject,
                unit: qUnit,
                courseOutcome: qCo,
                bloomLevel: qBloom,
                difficulty: qDiff
            });
            btn.classList.remove('btn-outline-primary');
            btn.classList.add('btn-success');
            btn.innerHTML = '<i class="bi bi-check-circle"></i> Added';
        }

        this.renderSelectedTable();
        this.updateDistribution();
    },

    removeQuestion: function (qId) {
        this.selectedQuestions.delete(qId);
        const cardBtn = document.querySelector(`.btn-select-q[data-id="${qId}"]`);
        if (cardBtn) {
            cardBtn.classList.remove('btn-success');
            cardBtn.classList.add('btn-outline-primary');
            cardBtn.innerHTML = '<i class="bi bi-plus-circle"></i> Add';
        }
        this.renderSelectedTable();
        this.updateDistribution();
    },

    renderSelectedTable: function () {
        const tbody = document.getElementById('selected-questions-tbody');
        const emptyAlert = document.getElementById('empty-selection-alert');
        const countBadge = document.getElementById('selected-count-badge');

        if (!tbody) return;

        if (countBadge) {
            countBadge.innerText = this.selectedQuestions.size;
        }

        if (this.selectedQuestions.size === 0) {
            tbody.innerHTML = '';
            if (emptyAlert) emptyAlert.style.display = 'block';
            return;
        }

        if (emptyAlert) emptyAlert.style.display = 'none';

        let html = '';
        let num = 1;
        this.selectedQuestions.forEach((item, qId) => {
            item.questionNumber = num;
            html += `
                <tr>
                    <td class="text-center font-monospace fw-bold">${num}</td>
                    <td>
                        <div class="fw-semibold text-truncate" style="max-width: 320px;" title="${item.questionText}">
                            ${item.questionText}
                        </div>
                        <small class="text-muted">${item.unit} | ${item.courseOutcome} | ${item.bloomLevel}</small>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" style="width: 80px;" 
                               value="${item.sectionName}" 
                               onchange="PaperBuilder.updateSection(${qId}, this.value)">
                    </td>
                    <td>
                        <input type="number" class="form-control form-control-sm" style="width: 70px;" 
                               min="1" max="30" value="${item.allocatedMarks}" 
                               onchange="PaperBuilder.updateMarks(${qId}, this.value)">
                    </td>
                    <td class="text-end">
                        <button type="button" class="btn btn-outline-danger btn-sm" onclick="PaperBuilder.removeQuestion(${qId})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
            num++;
        });

        tbody.innerHTML = html;
    },

    updateMarks: function (qId, newMarks) {
        if (this.selectedQuestions.has(qId)) {
            const item = this.selectedQuestions.get(qId);
            item.allocatedMarks = parseInt(newMarks) || 1;
            this.updateDistribution();
        }
    },

    updateSection: function (qId, section) {
        if (this.selectedQuestions.has(qId)) {
            const item = this.selectedQuestions.get(qId);
            item.sectionName = section || 'Part A';
        }
    },

    updateDistribution: async function () {
        const qIds = Array.from(this.selectedQuestions.keys());
        if (qIds.length === 0) {
            this.clearDistributionDisplay();
            return;
        }

        try {
            const response = await fetch('/api/question-papers/compute-distribution', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(qIds)
            });

            if (!response.ok) return;

            const res = await response.json();
            const dist = res.data;

            // Update Total questions & Total Marks
            const totalQEl = document.getElementById('dist-total-questions');
            const totalMarksEl = document.getElementById('dist-total-marks');
            const avgDiffEl = document.getElementById('dist-avg-diff');

            if (totalQEl) totalQEl.innerText = dist.totalQuestions;
            if (totalMarksEl) {
                // Calculate from actual allocated marks
                let sum = 0;
                this.selectedQuestions.forEach(item => sum += (item.allocatedMarks || 0));
                totalMarksEl.innerText = sum;
            }
            if (avgDiffEl) avgDiffEl.innerText = dist.averageDifficulty;

            // Render Unit Distribution
            const unitBox = document.getElementById('dist-units-list');
            if (unitBox && dist.unitDistribution) {
                let uHtml = '';
                for (const [unit, count] of Object.entries(dist.unitDistribution)) {
                    uHtml += `<div class="d-flex justify-content-between mb-1 small">
                        <span>${unit}</span>
                        <span class="badge bg-primary rounded-pill">${count} Qs</span>
                    </div>`;
                }
                unitBox.innerHTML = uHtml;
            }

            // Render CO distribution
            const coBox = document.getElementById('dist-co-list');
            if (coBox && dist.coPercentages) {
                let coHtml = '';
                for (const [co, pct] of Object.entries(dist.coPercentages)) {
                    coHtml += `
                        <div class="mb-2">
                            <div class="d-flex justify-content-between small mb-1">
                                <span>${co}</span>
                                <span class="fw-bold">${pct}%</span>
                            </div>
                            <div class="progress" style="height: 6px;">
                                <div class="progress-bar bg-info" style="width: ${pct}%"></div>
                            </div>
                        </div>
                    `;
                }
                coBox.innerHTML = coHtml;
            }

            // Render Bloom distribution
            const bloomBox = document.getElementById('dist-bloom-list');
            if (bloomBox && dist.bloomDistribution) {
                let bHtml = '';
                for (const [bloom, count] of Object.entries(dist.bloomDistribution)) {
                    bHtml += `<span class="badge bg-secondary me-1 mb-1">${bloom}: ${count}</span>`;
                }
                bloomBox.innerHTML = bHtml;
            }

            // Render Advisory Warnings
            const warnBox = document.getElementById('dist-warnings-box');
            if (warnBox) {
                if (dist.advisoryWarnings && dist.advisoryWarnings.length > 0) {
                    let wHtml = '<div class="alert alert-warning p-2 small mb-0"><ul class="mb-0 ps-3">';
                    dist.advisoryWarnings.forEach(w => wHtml += `<li>${w}</li>`);
                    wHtml += '</ul></div>';
                    warnBox.innerHTML = wHtml;
                    warnBox.style.display = 'block';
                } else {
                    warnBox.style.display = 'none';
                }
            }

        } catch (e) {
            console.error('Error computing distribution:', e);
        }
    },

    clearDistributionDisplay: function () {
        const totalQEl = document.getElementById('dist-total-questions');
        const totalMarksEl = document.getElementById('dist-total-marks');
        const avgDiffEl = document.getElementById('dist-avg-diff');
        const unitBox = document.getElementById('dist-units-list');
        const coBox = document.getElementById('dist-co-list');
        const bloomBox = document.getElementById('dist-bloom-list');
        const warnBox = document.getElementById('dist-warnings-box');

        if (totalQEl) totalQEl.innerText = '0';
        if (totalMarksEl) totalMarksEl.innerText = '0';
        if (avgDiffEl) avgDiffEl.innerText = 'N/A';
        if (unitBox) unitBox.innerHTML = '<span class="text-muted small">No questions selected</span>';
        if (coBox) coBox.innerHTML = '<span class="text-muted small">No questions selected</span>';
        if (bloomBox) bloomBox.innerHTML = '<span class="text-muted small">No questions selected</span>';
        if (warnBox) warnBox.style.display = 'none';
    },

    savePaper: async function () {
        const title = document.getElementById('paper-title').value;
        const subject = document.getElementById('paper-subject').value;
        const examCode = document.getElementById('paper-code').value;
        const academicYear = document.getElementById('paper-year').value;
        const semester = document.getElementById('paper-semester').value;
        const instructions = document.getElementById('paper-instructions').value;

        if (!title || !subject || !academicYear || !semester) {
            alert('Please fill in all required paper metadata fields (Title, Subject, Academic Year, Semester).');
            return;
        }

        if (this.selectedQuestions.size === 0) {
            alert('Please select at least one question for the question paper.');
            return;
        }

        const questionsArray = Array.from(this.selectedQuestions.values());
        let totalMarks = 0;
        questionsArray.forEach(q => totalMarks += q.allocatedMarks);

        const payload = {
            title: title.trim(),
            subject: subject,
            examCode: examCode,
            academicYear: academicYear,
            semester: semester,
            totalMarks: totalMarks,
            instructions: instructions,
            questions: questionsArray
        };

        try {
            const btn = document.getElementById('btn-save-paper');
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Generating...';
            }

            const response = await fetch('/api/question-papers', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error('Failed to save paper');
            }

            const res = await response.json();
            window.location.href = `/papers/view/${res.data.id}`;
        } catch (e) {
            alert('Error generating question paper: ' + e.message);
            const btn = document.getElementById('btn-save-paper');
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-check-lg me-1"></i> Generate Question Paper';
            }
        }
    }
};
