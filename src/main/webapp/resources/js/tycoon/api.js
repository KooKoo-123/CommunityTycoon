// 서버 통신: 요청 데이터 포장 → fetch → 응답 검사. state.js 다음에 읽습니다.
(function() {
    var game = window.Tycoon;
    if (!game) return;
    game.formData = function(npc) {
        var data = new URLSearchParams();
        Object.keys(game.stats).forEach(function(name) { data.set(name, game.stats[name]); });
        Object.keys(game.modules).forEach(function(name) { data.set(name, game.modules[name]); });
        if (npc) Object.keys(npc).forEach(function(name) {
            if (npc[name] != null) data.set(name, npc[name]);
        });
        return data;
    };
    game.checkResponse = function(response) {
        if (response.status === 401) {
            game.failed = true;
            game.stop();
            location.assign(game.root + '/member/login?expired=1');
            throw new Error('다른 곳에서 로그인했거나 세션이 만료되었습니다.');
        }
        if (!response.ok) throw new Error('요청을 처리하지 못했어요. 연결과 구매 조건을 확인해 주세요.');
        return response;
    };

    game.send = function(path, data, json) {
        game.busy = true;
        game.render();
        game.pending = fetch(game.root + '/tycoon/' + path, {method: 'POST', body: data})
            .then(game.checkResponse)
            .then(function(response) { return json ? response.json() : response.text(); })
            .then(function(result) {
                if (json) game.apply(result);
                else if (!/^\d+(,\d+)?$/.test(result)) throw new Error('서버 응답을 확인해 주세요.');
                return result;
            }).catch(function(error) {
                game.failed = true;
                game.stop();
                game.message(error.message + ' 저장되지 않은 요청을 자동으로 반복하지 않습니다.');
                throw error;
            }).finally(function() { game.busy = false; game.render(); });
        return game.pending;
    };
    game.save = async function() {
        if (game.busy || game.failed) return;
        await game.send('save', game.formData(), false);
        game.message('DAY ' + game.stats.day + ' · 게임 저장 완료');
    };
    game.action = function(action, module) {
        var data = game.formData();
        data.set('action', action);
        data.set('module', module || '');
        return game.send('action', data, true);
    };
    game.state = async function() {
        var response = await fetch(game.root + '/tycoon/state', {cache: 'no-store'});
        game.checkResponse(response);
        return response.json();
    };
    game.posts = async function(params) {
        var response = await fetch(game.root + '/tycoon/posts?' + params, {cache: 'no-store'});
        game.checkResponse(response);
        return response.json();
    };
    // 페이지 종료 시 최선의 저장 시도입니다. 전달 성공을 보장하지는 않습니다.
    game.saveOnExit = function() {
        navigator.sendBeacon(game.root + '/tycoon/save', game.formData());
    };
})();
