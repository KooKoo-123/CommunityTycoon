// 화면 이동과 생명주기: 저장 후 이동·뒤로가기 복귀·게임 최초 시작. 게임 JS 중 마지막에 읽습니다.
(function() {
    var game = window.Tycoon;
    if (!game) return;
    async function beforeLeaving(action) {
        if (game.leaving || game.failed) return;
        game.leaving = true;
        game.stop();
        try {
            await game.pending;
            await game.send('save', game.formData(), false);
            action();
        } catch (error) { game.leaving = false; }
    }
    document.addEventListener('submit', function(event) {
        if (event.defaultPrevented) return;
        var form = event.target;
        event.preventDefault();
        beforeLeaving(function() { HTMLFormElement.prototype.submit.call(form); });
    });
    document.addEventListener('click', function(event) {
        var a = event.target.closest('a');
        if (!a || a.hasAttribute('data-download') || a.target === '_blank' || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
        if (a.origin !== location.origin || !a.getAttribute('href') || a.getAttribute('href').startsWith('#')) return;
        event.preventDefault();
        beforeLeaving(function() { location.href = a.href; });
    });
    window.addEventListener('pagehide', function() {
        game.stop();
        // 브라우저 강제 종료에서는 보장되지 않으므로 각 행동도 이미 DB에 저장합니다.
        if (!game.busy && !game.failed && !game.leaving) game.saveOnExit();
    });
    window.addEventListener('pageshow', async function(event) {
        if (!event.persisted) return;
        game.stop();
        try {
            game.apply(await game.state());
            game.leaving = false;
            game.failed = false;
            game.render();
            game.start();
        } catch (error) { game.message(error.message); }
    });
    game.render();
    game.start();
})();
