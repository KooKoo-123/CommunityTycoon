// 게임 화면: HUD·게시판 렌더링·구매 버튼·구매 창. 시작/이동은 navigation.js에서 담당합니다.
(function() {
    var game = window.Tycoon;
    if (!game) return;
    function show(id, visible) {
        var element = document.getElementById(id);
        if (element) element.hidden = !visible;
    }
    game.message = function(text) { document.getElementById('game-message').textContent = text; };
    game.render = function() {
        document.getElementById('hud-gold').textContent = game.stats.gold.toLocaleString() + ' G';
        document.getElementById('hud-traffic').textContent = game.stats.traffic.toLocaleString() + ' 명';
        document.getElementById('hud-day').textContent = 'DAY ' + game.stats.day;
        show('hello-site', !game.modules.board);
        show('board-panel', game.modules.board);
        show('comment-locked', !game.modules.comment);
        show('comment-content-panel', game.modules.comment);
        show('mypage-locked', !game.modules.mypage);
        show('profile-image-locked', !game.modules.profileImage);
        show('profile-image-storage-locked', !game.modules.profileImageStorage);
        document.querySelectorAll('[data-module-panel]').forEach(function(panel) {
            panel.hidden = !game.modules[panel.dataset.modulePanel];
        });
        document.querySelectorAll('[data-module]').forEach(function(button) {
            var name = button.dataset.module;
            var needsBoard = ['comment', 'attachment', 'paging', 'search'].includes(name);
            var needsProfileImage = name === 'profileImageStorage' && (!game.modules.mypage || !game.modules.profileImage);
            var adminReset = game.admin && game.modules[name];
            button.disabled = game.busy || game.failed || (!adminReset && (game.modules[name] || needsProfileImage ||
                (needsBoard && !game.modules.board) || (name === 'profileImage' && !game.modules.mypage) ||
                game.stats.gold < Number(button.dataset.price)));
            button.textContent = button.dataset.label + (game.modules[name] ? ' 구매 완료' : ' 구매 · ' + button.dataset.price + ' G');
            if (adminReset) button.title = '관리자 시연: 다시 누르면 미구매 상태로 돌아갑니다.';
            else button.removeAttribute('title');
        });
        var theme = document.getElementById('forest-theme-option');
        if (theme) theme.disabled = !game.modules.theme;
        document.getElementById('save-game').disabled = game.busy || game.failed;
        document.getElementById('feed-manager').disabled = game.busy || game.failed || game.stats.gold <= 10;
        document.getElementById('module-count').textContent = Object.values(game.modules).filter(Boolean).length + ' / 9';
        document.querySelectorAll('.manager-status').forEach(function(status) {
            status.textContent = Math.floor(Date.now() / 30000) % 2 ? '☕ 휴식 중' : '💤 자는 중';
        });
    };

    game.addPost = function(post) {
        var list = document.getElementById('post-list');
        if (!list) return;
        var row = document.createElement('tr');
        [post.displayNo, post.title, post.authorNickname, 'DAY ' + post.createdDay].forEach(function(value, index) {
            var cell = document.createElement('td');
            if (index === 1) {
                var link = document.createElement('a');
                link.href = game.root + '/board/' + post.boardId;
                link.textContent = value;
                if (post.firstImageUrl) link.dataset.previewUrl = game.root + post.firstImageUrl;
                cell.appendChild(link);
            } else if (index === 2) {
                if (post.authorType === 'PLAYER' && post.authorProfileImageId) {
                    var image = document.createElement('img');
                    image.className = 'profile-image me-2';
                    image.alt = '작성 당시 프로필';
                    image.src = game.root + '/files/' + post.authorProfileImageId;
                    cell.appendChild(image);
                }
                cell.appendChild(document.createTextNode(value + ' '));
                var badge = document.createElement('span');
                badge.className = 'writer-label';
                badge.textContent = post.authorType === 'PLAYER' ? '운영자' : 'NPC';
                cell.appendChild(badge);
            } else cell.textContent = value;
            row.appendChild(cell);
        });
        list.prepend(row);
        show('empty-posts', false);
    };
    game.refreshBoard = async function() {
        var list = document.getElementById('post-list');
        if (!list || !game.modules.board) return;
        var params = new URLSearchParams(location.search);
        var result = await game.posts(params);
        list.replaceChildren();
        result.dtoList.slice().reverse().forEach(game.addPost);
        show('empty-posts', !result.dtoList.length);
        var pages = document.getElementById('board-pages');
        pages.replaceChildren();
        function link(page, label) {
            var a = document.createElement('a');
            params.set('page', page);
            a.href = game.root + '/tycoon?' + params;
            a.textContent = label;
            a.className = 'btn btn-sm ' + (page === result.page ? 'btn-primary' : 'btn-outline-secondary');
            if (page === result.page) a.setAttribute('aria-current', 'page');
            pages.appendChild(a);
        }
        if (result.prev) link(result.start - 1, '이전');
        for (var page = result.start; page <= result.end; page++) link(page, page);
        if (result.next) link(result.end + 1, '다음');
    };

    document.getElementById('save-game').addEventListener('click', function() { game.save().catch(function() {}); });
    document.querySelectorAll('[data-module]').forEach(function(button) {
        button.addEventListener('click', async function() {
            if (game.busy || game.failed || game.leaving || button.disabled) return;
            var wasPurchased = game.modules[button.dataset.module];
            try {
                await game.action('buy', button.dataset.module);
                game.message(wasPurchased
                    ? button.dataset.label + '을(를) 미구매 상태로 바꿨습니다.'
                    : button.dataset.label + ' 구매 및 저장 완료!');
                if (game.modules.board) await game.refreshBoard();
                game.scheduleNpc();
            } catch (error) { }
        });
    });
var buildDialog = document.getElementById('build-dialog');
var openBuildButton = document.getElementById('open-build-panel');
var closeBuildButton = document.getElementById('close-build-panel');

if (buildDialog && openBuildButton && closeBuildButton) {
    openBuildButton.addEventListener('click', function() {
        buildDialog.showModal();
        openBuildButton.setAttribute('aria-expanded', 'true');
    });
    closeBuildButton.addEventListener('click', function() {
        buildDialog.close();
    });
    buildDialog.addEventListener('close', function() {
        openBuildButton.setAttribute('aria-expanded', 'false');
        openBuildButton.focus();
    });
    buildDialog.addEventListener('click', function(event) {
        if (event.target !== buildDialog) return;
        var box = buildDialog.getBoundingClientRect();
        if (event.clientX < box.left || event.clientX > box.right ||
            event.clientY < box.top || event.clientY > box.bottom) {
            buildDialog.close();
        }
    });
}
})();
