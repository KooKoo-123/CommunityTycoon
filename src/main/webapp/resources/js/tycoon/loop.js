// NPC·이탈·게임 시간 타이머. 대사 선택은 npc/select.js, 통신은 api.js에 맡깁니다.
(function() {
    var game = window.Tycoon;
    if (!game) return;
    var npcTimer, departureTimer, clockTimer;
    var clockStartedAt = Date.now();
    var clockBaseSeconds = game.stats.playSeconds;
    var clockBaseDay = game.stats.day;

    game.npcDelay = function() {
        var min = 4000, max = 10000;
        if (game.stats.traffic >= 30) { min = 2000; max = 4000; }
        else if (game.stats.traffic >= 10) { min = 3000; max = 7000; }
        return min + Math.floor(Math.random() * (max - min + 1));
    };
    game.scheduleNpc = function() {
        clearTimeout(npcTimer);
        if (!game.modules.board || game.failed || game.leaving) return;
        npcTimer = setTimeout(npcAction, game.npcDelay());
    };
    async function npcAction() {
        if (game.failed || game.leaving) return;
        if (game.busy) { game.scheduleNpc(); return; }
        var post = window.Npc.selectPost(game.theme);
        var npc = {title: post.title, content: post.content, author: post.author || window.Npc.selectName(game.theme)};
        if (game.modules.comment && Math.random() < 0.5) {
            npc.reply = window.Npc.selectReply(game.theme, post);
            npc.replyAuthor = post.replyAuthor || window.Npc.selectName(game.theme);
            if (game.theme === 'forest') npc.replyBack = post.answer;
        }
        var postReward = 10;
        var commentReward = (npc.reply ? 3 : 0) + (npc.replyBack ? 3 : 0);
        game.stats.gold = Math.min(9000000000000, game.stats.gold + postReward + commentReward);
        game.stats.traffic = Math.min(1000000, game.stats.traffic + 1);
        game.render();
        try {
            var result = await game.send('npc', game.formData(npc), false);
            await game.refreshBoard();
            game.message(npc.author + '의 새 글 +' + postReward + ' G · 이용자 +1' +
                (npc.replyBack ? ' · 댓글 2개 +' + commentReward + ' G' : (npc.reply ? ' · 댓글 +' + commentReward + ' G' : '')));
            game.scheduleNpc();
        } catch (error) { }
    }
    async function departure() {
        if (game.failed || game.leaving) return;
        try {
            if (!game.busy) {
                if (game.stats.traffic > 0) {
                    game.stats.traffic--;
                    await game.send('save', game.formData(), false);
                    game.scheduleNpc();
                } else {
                    await game.state();
                }
            }
        } catch (error) { game.failed = true; game.stop(); game.message(error.message); }
        if (!game.failed && !game.leaving) departureTimer = setTimeout(departure, 15000);
    }
    // 브라우저가 타이머를 잠시 늦춰도 마지막 1초만 세는 대신 실제 차이를 따라잡습니다.
    game.resetClock = function() {
        clockStartedAt = Date.now();
        clockBaseSeconds = game.stats.playSeconds;
        clockBaseDay = game.stats.day;
    };
    function gameClock() {
        if (game.failed || game.leaving) return;
        if (!game.busy) {
            var secondsSincePageOpened = Math.floor((Date.now() - clockStartedAt) / 1000);
            var totalSeconds = clockBaseSeconds + secondsSincePageOpened;
            var addedDays = Math.floor(totalSeconds / 600);
            var changed = addedDays > 0;
            game.stats.playSeconds = totalSeconds % 600;
            game.stats.day = Math.min(1000000, clockBaseDay + addedDays);
            game.render();
            if (changed) {
                clockBaseDay = game.stats.day;
                clockBaseSeconds = game.stats.playSeconds;
                clockStartedAt += addedDays * 600000;
                game.save().catch(function() {});
            }
        }
        clockTimer = setTimeout(gameClock, 1000);
    }
    game.stop = function() {
        clearTimeout(npcTimer);
        clearTimeout(departureTimer);
        clearTimeout(clockTimer);
    };
    game.start = function() {
        game.stop();
        game.resetClock();
        game.scheduleNpc();
        departureTimer = setTimeout(departure, 15000);
        clockTimer = setTimeout(gameClock, 1000);
    };
})();
