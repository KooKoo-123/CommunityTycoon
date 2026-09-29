// 관리자냥: 반응 말풍선·사료·관리자 보너스·골드 조작 감지 연출.
(function() {
    var game = window.Tycoon;
    if (!game) return;
    var toastTimer;
    game.reaction = function(text) {
        clearTimeout(toastTimer);
        document.getElementById('manager-toast-text').textContent = text;
        document.getElementById('manager-toast').hidden = false;
        toastTimer = setTimeout(function() { document.getElementById('manager-toast').hidden = true; }, 5000);
    };
    document.getElementById('feed-manager').addEventListener('click', async function() {
        if (game.busy || game.failed || game.leaving || game.stats.gold <= 10) return;
        try {
            await game.action('feed');
            game.reaction('관리자냥이 행복해합니다! -10G');
        } catch (error) { }
    });
    async function bonus() {
        if (!game.admin || game.busy || game.failed || game.leaving) return;
        try { await game.action('bonus'); game.message('관리자 보너스 +1,000 G · 저장 완료'); }
        catch (error) { }
    }
    document.getElementById('gold-box').addEventListener('click', bonus);
    document.getElementById('gold-box').addEventListener('keydown', function(event) {
        if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); bonus(); }
    });

    // 발표용 재미 연출입니다. 서버 보안을 대신하지는 않습니다.
    var waiting = false;
    var requestedGold = null;
    Object.defineProperty(window, 'playerStats', {value: new Proxy(game.stats, {
        set: function(target, name, value) {
            if (name !== 'gold') { target[name] = value; return true; }
            var amount = Number(value);
            amount = Number.isFinite(amount) ? Math.min(9000000000000, Math.abs(Math.trunc(amount))) : 0;
            requestedGold = amount % 10;
            target.gold = requestedGold;
            game.render();
            game.reaction('유저가 가진 돈만큼 관리자냥이 사료를 먹습니다! -' + (amount - requestedGold).toLocaleString() + ' G');
            saveRemainder();
            return true;
        }
    })});
    async function saveRemainder() {
        if (waiting || game.failed) return;
        waiting = true;
        try {
            await game.pending;
            while (requestedGold !== null) {
                game.stats.gold = requestedGold;
                requestedGold = null;
                await game.action('tamper');
            }
            game.message('관리자냥이 사료를 다 먹었어요. 남은 ' + game.stats.gold + ' G를 저장했습니다.');
        } catch (error) { game.message('골드 저장에 실패했습니다. 연결을 확인해 주세요.'); }
        finally { waiting = false; }
    }
})();
