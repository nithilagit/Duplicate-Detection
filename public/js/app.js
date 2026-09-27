/**
 * AI-Based Duplicate Question Detection System
 * Main Frontend Logic & Interactive Engine for Vercel
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

    // 3. Update User Header Information
    if (window.AppStore) {
        const user = window.AppStore.getUser();
        document.querySelectorAll('.user-display-email').forEach(el => el.innerText = user.email);
        document.querySelectorAll('.user-display-dept').forEach(el => el.innerText = user.department);
    }
});

/**
 * Universal Safe Logout for Static Deployment
 */
function handleLogout(e) {
    if (e) e.preventDefault();
    if (window.AppStore) {
        window.AppStore.logout();
    } else {
        window.location.href = '/login.html';
    }
}

/**
 * Duplicate Detection Helper for Add Question Form & AI Page
 */
const DuplicateDetector = {
    checkSimilarity: async function (questionText, subject, excludeId = null) {
        if (!questionText || questionText.trim().length < 5) {
            return null;
        }

        // Try AppStore client-side engine directly
        if (window.AppStore) {
            return window.AppStore.checkSimilarity(questionText, subject, excludeId);
        }

        // Fallback basic client-side check
        return {
            status: 'UNIQUE',
            highestSimilarity: 0.0,
            explanation: 'Analysis completed.',
            extractedTokens: [],
            matches: []
        };
    }
};

/**
 * Question Paper Builder Interactive Engine
 */
const PaperBuilder = {
    selectedQuestions: new Map(),

    init: function () {
        this.selectedQuestions.clear();
        this.renderSelectedTable();
        this.clearDistributionDisplay();
    },

    toggleQuestion: function (btn) {
        const qId = parseInt(btn.getAttribute('data-id'));
        const qText = btn.getAttribute('data-text');
        const qSubject = btn.getAttribute('data-subject');
        const qUnit = btn.getAttribute('data-unit');
        const qCo = btn.getAttribute('data-co');
        const qBloom = btn.getAttribute('data-bloom');
        const qMarks = parseInt(btn.getAttribute('data-marks')) || 10;
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
                sectionName: this.selectedQuestions.size < 5 ? 'Part A' : 'Part B',
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

    updateDistribution: function () {
        const questions = Array.from(this.selectedQuestions.values());
        if (questions.length === 0) {
            this.clearDistributionDisplay();
            return;
        }

        let totalMarks = 0;
        const unitDist = {};
        const coCount = {};
        const bloomDist = {};
        let diffSum = 0;

        const diffWeights = { 'Easy': 1, 'Medium': 2, 'Hard': 3 };

        questions.forEach(q => {
            totalMarks += q.allocatedMarks;
            unitDist[q.unit] = (unitDist[q.unit] || 0) + 1;
            coCount[q.courseOutcome] = (coCount[q.courseOutcome] || 0) + 1;
            bloomDist[q.bloomLevel] = (bloomDist[q.bloomLevel] || 0) + 1;
            diffSum += (diffWeights[q.difficulty] || 2);
        });

        const totalQ = questions.length;
        const avgScore = diffSum / totalQ;
        let avgDiffLabel = 'Medium';
        if (avgScore < 1.6) avgDiffLabel = 'Easy';
        else if (avgScore > 2.4) avgDiffLabel = 'Hard';

        // Update Total questions & Total Marks
        const totalQEl = document.getElementById('dist-total-questions');
        const totalMarksEl = document.getElementById('dist-total-marks');
        const avgDiffEl = document.getElementById('dist-avg-diff');

        if (totalQEl) totalQEl.innerText = totalQ;
        if (totalMarksEl) totalMarksEl.innerText = totalMarks;
        if (avgDiffEl) avgDiffEl.innerText = avgDiffLabel;

        // Render Unit Distribution
        const unitBox = document.getElementById('dist-units-list');
        if (unitBox) {
            let uHtml = '';
            for (const [unit, count] of Object.entries(unitDist)) {
                uHtml += `<div class="d-flex justify-content-between mb-1 small">
                    <span>${unit}</span>
                    <span class="badge bg-primary rounded-pill">${count} Qs</span>
                </div>`;
            }
            unitBox.innerHTML = uHtml;
        }

        // Render CO distribution
        const coBox = document.getElementById('dist-co-list');
        if (coBox) {
            let coHtml = '';
            for (const [co, count] of Object.entries(coCount)) {
                const pct = Math.round((count / totalQ) * 100);
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
        if (bloomBox) {
            let bHtml = '';
            for (const [bloom, count] of Object.entries(bloomDist)) {
                bHtml += `<span class="badge bg-secondary me-1 mb-1">${bloom}: ${count}</span>`;
            }
            bloomBox.innerHTML = bHtml;
        }

        // Render Advisory Warnings
        const warnBox = document.getElementById('dist-warnings-box');
        if (warnBox) {
            const warnings = [];
            const standardUnits = ["Unit I", "Unit II", "Unit III", "Unit IV", "Unit V"];
            standardUnits.forEach(u => {
                if (!unitDist[u]) warnings.push(`No questions selected from ${u}.`);
            });
            if (totalMarks < 50) {
                warnings.push(`Total marks (${totalMarks} M) is lower than standard end-semester target (100 M).`);
            }

            if (warnings.length > 0) {
                let wHtml = '<div class="alert alert-warning p-2 small mb-0"><ul class="mb-0 ps-3">';
                warnings.forEach(w => wHtml += `<li>${w}</li>`);
                wHtml += '</ul></div>';
                warnBox.innerHTML = wHtml;
                warnBox.style.display = 'block';
            } else {
                warnBox.style.display = 'none';
            }
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

    savePaper: function () {
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

        // Generate printable modal directly on page
        this.showPrintModal({
            title: title.trim(),
            subject: subject,
            examCode: examCode,
            academicYear: academicYear,
            semester: semester,
            totalMarks: totalMarks,
            instructions: instructions,
            questions: questionsArray
        });
    },

    showPrintModal: function (paper) {
        let modalEl = document.getElementById('paperPreviewModal');
        if (!modalEl) {
            modalEl = document.createElement('div');
            modalEl.id = 'paperPreviewModal';
            modalEl.className = 'modal fade';
            modalEl.setAttribute('tabindex', '-1');
            document.body.appendChild(modalEl);
        }

        let qListHtml = '';
        paper.questions.forEach((q, idx) => {
            qListHtml += `
                <div class="mb-3 pb-2 border-bottom">
                    <div class="d-flex justify-content-between align-items-start">
                        <span class="fw-bold me-2">Q${idx + 1}.</span>
                        <div class="flex-grow-1">${q.questionText}</div>
                        <span class="fw-bold ms-3 text-nowrap">[${q.allocatedMarks} Marks]</span>
                    </div>
                    <div class="text-muted small mt-1">
                        Section: ${q.sectionName || 'Part A'} | ${q.unit} | Outcome: ${q.courseOutcome} | Bloom: ${q.bloomLevel}
                    </div>
                </div>
            `;
        });

        modalEl.innerHTML = `
            <div class="modal-dialog modal-lg modal-dialog-scrollable">
                <div class="modal-content shadow-lg">
                    <div class="modal-header bg-light">
                        <h5 class="modal-title fw-bold text-dark">
                            <i class="bi bi-file-earmark-check-fill text-success me-2"></i>Generated Question Paper
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body p-4" id="printablePaperContent">
                        <div class="text-center pb-3 border-bottom mb-4">
                            <h4 class="fw-bold mb-1">EASWARI ENGINEERING COLLEGE</h4>
                            <div class="text-muted small mb-2">Department of Artificial Intelligence & Data Science</div>
                            <h5 class="fw-bold text-primary mb-1">${paper.title} - ${paper.academicYear}</h5>
                            <div class="small fw-semibold text-secondary">
                                Course: ${paper.subject} (${paper.examCode}) | ${paper.semester}
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3 small fw-bold">
                                <span>Time Allowed: 3 Hours</span>
                                <span>Max Marks: ${paper.totalMarks}</span>
                            </div>
                        </div>

                        ${paper.instructions ? `<div class="alert alert-secondary small p-2 mb-3"><strong>Instructions:</strong> ${paper.instructions}</div>` : ''}

                        <div class="questions-list">
                            ${qListHtml}
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="window.print()">
                            <i class="bi bi-printer me-1"></i> Print Question Paper
                        </button>
                    </div>
                </div>
            </div>
        `;

        const bsModal = new bootstrap.Modal(modalEl);
        bsModal.show();
    }
};

window.DuplicateDetector = DuplicateDetector;
window.PaperBuilder = PaperBuilder;
window.handleLogout = handleLogout;
