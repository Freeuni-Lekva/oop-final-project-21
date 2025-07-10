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
    getProgress() {
        return ((this.currentQuestion + 1) / this.totalQuestions) * 100;
    }
}

const QuizValidator = {
};

const QuizUI = {
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
        
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }
};

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
}

const QuizEventHandlers = {
    beforeUnloadHandler: null
};

document.addEventListener('DOMContentLoaded', function() {
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