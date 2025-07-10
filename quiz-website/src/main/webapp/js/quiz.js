/**
 * Quiz JavaScript functionality
 * Handles quiz taking, navigation, timers, and form validation
 */

class QuizTimer {
    constructor(timeInSeconds, onUpdate, onExpire) {
        this.timeLeft = timeInSeconds;
        this.onUpdate = onUpdate;
        this.onExpire = onExpire;
        this.interval = null;
        this.isRunning = false;
    }
    
    start() {
        if (this.isRunning) return;
        
        this.isRunning = true;
        this.interval = setInterval(() => {
            this.timeLeft--;
            
            if (this.onUpdate) {
                this.onUpdate(this.timeLeft);
            }
            
            if (this.timeLeft <= 0) {
                this.stop();
                if (this.onExpire) {
                    this.onExpire();
                }
            }
        }, 1000);
    }
    
    stop() {
        if (this.interval) {
            clearInterval(this.interval);
            this.interval = null;
            this.isRunning = false;
        }
    }
    
    pause() {
        this.stop();
    }
    
    resume() {
        if (!this.isRunning && this.timeLeft > 0) {
            this.start();
        }
    }
    
    getTimeLeft() {
        return this.timeLeft;
    }
    
    formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    }
}

class QuizNavigation {
    constructor(totalQuestions, currentQuestion = 0) {
        this.totalQuestions = totalQuestions;
        this.currentQuestion = currentQuestion;
        this.answers = new Array(totalQuestions).fill(null);
    }
    
    canGoNext() {
        return this.currentQuestion < this.totalQuestions - 1;
    }
    
    canGoPrevious() {
        return this.currentQuestion > 0;
    }
    
    next() {
        if (this.canGoNext()) {
            this.currentQuestion++;
            return true;
        }
        return false;
    }
    
    previous() {
        if (this.canGoPrevious()) {
            this.currentQuestion--;
            return true;
        }
        return false;
    }
    
    goToQuestion(questionIndex) {
        if (questionIndex >= 0 && questionIndex < this.totalQuestions) {
            this.currentQuestion = questionIndex;
            return true;
        }
        return false;
    }
    
    getCurrentQuestion() {
        return this.currentQuestion;
    }
    
    getTotalQuestions() {
        return this.totalQuestions;
    }
    
    getProgress() {
        return ((this.currentQuestion + 1) / this.totalQuestions) * 100;
    }
    
    setAnswer(questionIndex, answer) {
        if (questionIndex >= 0 && questionIndex < this.totalQuestions) {
            this.answers[questionIndex] = answer;
        }
    }
    
    getAnswer(questionIndex) {
        return this.answers[questionIndex];
    }
    
    getAnsweredCount() {
        return this.answers.filter(answer => answer !== null && answer !== '').length;
    }
    
    getUnansweredCount() {
        return this.totalQuestions - this.getAnsweredCount();
    }
}

const QuizValidator = {
    validateQuizForm: function(formData) {
        const errors = [];
        
        if (!formData.title || formData.title.trim().length < 3) {
            errors.push('Quiz title must be at least 3 characters long');
        }
        
        if (!formData.description || formData.description.trim().length < 10) {
            errors.push('Quiz description must be at least 10 characters long');
        }
        
        if (formData.timeLimit && (formData.timeLimit < 0 || formData.timeLimit > 1440)) {
            errors.push('Time limit must be between 0 and 1440 minutes');
        }
        
        return errors;
    },
    
    validateTextQuestion: function(formData) {
        const errors = [];
        
        if (!formData.questionTitle || formData.questionTitle.trim().length < 3) {
            errors.push('Question title must be at least 3 characters long');
        }
        
        if (!formData.questionText || formData.questionText.trim().length < 5) {
            errors.push('Question text must be at least 5 characters long');
        }
        
        return errors;
    },
    
    validateMultipleChoiceQuestion: function(formData) {
        const errors = [];
        
        if (!formData.questionTitle || formData.questionTitle.trim().length < 3) {
            errors.push('Question title must be at least 3 characters long');
        }
        
        if (!formData.questionText || formData.questionText.trim().length < 5) {
            errors.push('Question text must be at least 5 characters long');
        }
        
        const options = formData.options || [];
        const validOptions = options.filter(opt => opt && opt.trim().length > 0);
        
        if (validOptions.length < 2) {
            errors.push('At least 2 options are required');
        }
        
        if (formData.correctAnswer === null || formData.correctAnswer === undefined) {
            errors.push('Please select the correct answer');
        }
        
        return errors;
    },
    
    validateImageQuestion: function(formData) {
        const errors = [];
        
        if (!formData.questionTitle || formData.questionTitle.trim().length < 3) {
            errors.push('Question title must be at least 3 characters long');
        }
        
        if (!formData.questionText || formData.questionText.trim().length < 5) {
            errors.push('Question text must be at least 5 characters long');
        }
        
        if (!formData.imageFile && !formData.imageUrl) {
            errors.push('Please provide an image file or URL');
        }
        
        return errors;
    }
};

// Quiz UI Helper Functions
const QuizUI = {
    showLoadingState: function(element, text = 'Loading...') {
        if (element) {
            element.disabled = true;
            element.dataset.originalText = element.textContent;
            element.textContent = text;
        }
    },
    
    hideLoadingState: function(element) {
        if (element && element.dataset.originalText) {
            element.disabled = false;
            element.textContent = element.dataset.originalText;
            delete element.dataset.originalText;
        }
    },
    
    updateProgressBar: function(percentage) {
        const progressBar = document.querySelector('.quiz-progress-fill');
        if (progressBar) {
            progressBar.style.width = percentage + '%';
        }
    },
    
    showNotification: function(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `quiz-notification quiz-notification-${type}`;
        notification.textContent = message;
        
        // Style the notification
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: bold;
            z-index: 9999;
            animation: slideIn 0.3s ease-out;
        `;
        
        switch (type) {
            case 'success':
                notification.style.backgroundColor = '#28a745';
                break;
            case 'error':
                notification.style.backgroundColor = '#dc3545';
                break;
            case 'warning':
                notification.style.backgroundColor = '#ffc107';
                notification.style.color = '#000';
                break;
            default:
                notification.style.backgroundColor = '#17a2b8';
        }
        
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    },
    
    confirmAction: function(message, onConfirm, onCancel) {
        if (confirm(message)) {
            if (onConfirm) onConfirm();
        } else {
            if (onCancel) onCancel();
        }
    },
    
    formatTime: function(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    }
};

// Quiz Auto-save functionality
class QuizAutoSave {
    constructor(saveInterval = 30000) { // 30 seconds
        this.saveInterval = saveInterval;
        this.interval = null;
        this.pendingChanges = false;
        this.lastSaveTime = null;
    }
    
    start(saveFunction) {
        if (this.interval) {
            clearInterval(this.interval);
        }
        
        this.interval = setInterval(() => {
            if (this.pendingChanges && saveFunction) {
                saveFunction();
                this.pendingChanges = false;
                this.lastSaveTime = new Date();
            }
        }, this.saveInterval);
    }
    
    stop() {
        if (this.interval) {
            clearInterval(this.interval);
            this.interval = null;
        }
    }
    
    markChanged() {
        this.pendingChanges = true;
    }
    
    getLastSaveTime() {
        return this.lastSaveTime;
    }
}

// Quiz Event Handlers
const QuizEventHandlers = {
    handleFormSubmit: function(formElement, validationFunction) {
        formElement.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const data = Object.fromEntries(formData.entries());
            
            if (validationFunction) {
                const errors = validationFunction(data);
                if (errors.length > 0) {
                    QuizUI.showNotification(errors.join('\n'), 'error');
                    return;
                }
            }
            
            // Submit the form
            this.submit();
        });
    },
    
    beforeUnloadHandler: null, // Store reference to the handler
    
    handleBeforeUnload: function(hasUnsavedChanges = true) {
        // Remove existing handler first
        this.removeBeforeUnload();
        
        if (hasUnsavedChanges) {
            this.beforeUnloadHandler = function(e) {
                e.preventDefault();
                e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
            };
            window.addEventListener('beforeunload', this.beforeUnloadHandler);
        }
    },
    
    removeBeforeUnload: function() {
        if (this.beforeUnloadHandler) {
            window.removeEventListener('beforeunload', this.beforeUnloadHandler);
            this.beforeUnloadHandler = null;
        }
    },
    
    handleQuestionNavigation: function(navigation) {
        const nextBtn = document.querySelector('.quiz-nav-next');
        const prevBtn = document.querySelector('.quiz-nav-prev');
        
        if (nextBtn) {
            nextBtn.addEventListener('click', function(e) {
                e.preventDefault();
                if (navigation.next()) {
                    // Update UI
                    QuizUI.updateProgressBar(navigation.getProgress());
                }
            });
        }
        
        if (prevBtn) {
            prevBtn.addEventListener('click', function(e) {
                e.preventDefault();
                if (navigation.previous()) {
                    // Update UI
                    QuizUI.updateProgressBar(navigation.getProgress());
                }
            });
        }
    }
};

// Initialize quiz functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Add CSS animations
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
        
        .quiz-loading {
            opacity: 0.7;
            pointer-events: none;
        }
        
        .quiz-fade-in {
            animation: fadeIn 0.3s ease-out;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
    `;
    document.head.appendChild(style);

    document.querySelectorAll('.quiz-btn, .quiz-card, .quiz-option').forEach(element => {
        element.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        
        element.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
});

window.QuizTimer = QuizTimer;
window.QuizNavigation = QuizNavigation;
window.QuizValidator = QuizValidator;
window.QuizUI = QuizUI;
window.QuizAutoSave = QuizAutoSave;
window.QuizEventHandlers = QuizEventHandlers; 