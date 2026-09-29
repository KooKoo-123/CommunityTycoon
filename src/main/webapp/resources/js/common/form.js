// 로그인·가입 등 공통 폼의 중복 제출과 삭제 확인을 처리합니다.
document.querySelectorAll('form').forEach(function(form) {
    form.addEventListener('submit', function(event) {
        var gamePage = !!document.getElementById('game-state');
        if (form.classList.contains('delete-form') && !window.confirm(
            form.dataset.confirm || (gamePage ? '삭제할까요?' : '삭제할까요? 삭제 후에는 되돌릴 수 없습니다.'))) {
            event.preventDefault();
            return;
        }
        if (gamePage) return;
        if (form.dataset.submitting) {
            event.preventDefault();
            return;
        }
        form.dataset.submitting = 'true';
        var button = form.querySelector('button[type=submit],button:not([type])');
        if (button) {
            button.dataset.submitDisabled = 'true';
            button.disabled = true;
            button.style.opacity = '.6';
        }

    });
});
window.addEventListener('pageshow', function() {
    document.querySelectorAll('form').forEach(function(form) {
        delete form.dataset.submitting;
        form.querySelectorAll('button[data-submit-disabled]').forEach(function(button) {
            button.disabled = false;
            button.style.opacity = '';
            delete button.dataset.submitDisabled;
        });
    });
});
