// 로그인 폼의 고양이 연출만 담당합니다. common/form.js 다음에 읽습니다.
(function() {
    var form = document.getElementById('login-form');
    var mascot = document.getElementById('login-mascot');
    if (!form || !mascot) return;
    form.addEventListener('submit', function(event) {
        if (event.defaultPrevented) return;
        event.preventDefault();
        mascot.src = mascot.dataset.activeSrc;
        mascot.classList.add('is-meowing');
        mascot.alt = 'MEOW! 인사하는 고양이';
        window.setTimeout(function() { HTMLFormElement.prototype.submit.call(form); }, 450);
    });
    window.addEventListener('pageshow', function() {
        mascot.src = mascot.dataset.idleSrc;
        mascot.classList.remove('is-meowing');
        mascot.alt = '커뮤니티 타이쿤 고양이 로고';
    });
})();
