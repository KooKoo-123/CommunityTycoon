// 게임 상태 준비와 서버 상태 반영. 공통 head.jspf에서 가장 먼저 읽습니다.
(function() {
    var element = document.getElementById('game-state');
    if (!element) return;
    var game = window.Tycoon = {
        root: element.dataset.root,
        theme: document.documentElement.dataset.theme || 'sky',
        admin: element.dataset.admin === 'true',
        busy: false, leaving: false, failed: false,
        pending: Promise.resolve(),
        stats: {}, modules: {},
        render: function() {}, message: function() {}, reaction: function() {}, stop: function() {}
    };
    ['gold', 'traffic', 'day', 'playSeconds'].forEach(function(name) {
        game.stats[name] = Number(element.dataset[name]);
    });
    ['board', 'comment', 'mypage', 'profileImage', 'attachment', 'paging', 'search', 'theme', 'profileImageStorage'].forEach(function(name) {
        game.modules[name] = element.dataset[name] === 'true';
    });

    // 서버의 GameData 값을 JS 객체로 복사하고 HUD를 갱신합니다. comment는 서버에서 hasComments라는 이름입니다.
    game.apply = function(site) {
        Object.keys(game.stats).forEach(function(name) { game.stats[name] = Number(site[name]); });
        Object.keys(game.modules).forEach(function(name) {
            var key = name === 'comment' ? 'hasComments' : 'has' + name[0].toUpperCase() + name.slice(1);
            game.modules[name] = site[key] === true;
        });
        if (game.resetClock) game.resetClock();
        game.render();
    };
    window.tycoonModules = game.modules;
})();
