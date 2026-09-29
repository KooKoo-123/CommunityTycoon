// 테마 데이터에서 글·작성자·관련 댓글을 선택합니다. sky.js와 forest.js 다음에 읽습니다.
(function() {
    var npc = window.Npc;
    function pick(list) { return list[Math.floor(Math.random() * list.length)]; }
    function themeData(theme) { return npc.themes[theme] || npc.themes.sky; }
    npc.selectPost = function(theme) { return pick(themeData(theme).posts); };
    npc.selectName = function(theme) { return pick(themeData(theme).names); };
    npc.selectReply = function(theme, post) {
        if (theme === 'forest' && post.reply) return post.reply;
        var text = post.title + ' ' + post.content;
        var candidates = [];
        themeData(theme).comments.forEach(function(rule) {
            if (rule.keywords.some(function(keyword) { return text.includes(keyword); })) {
                candidates = candidates.concat(rule.replies);
            }
        });
        if (!candidates.length) candidates = ['이야기 나눠 주셔서 고마워요. 다음 이야기도 기다릴게요.'];
        return pick(candidates);
    };
})();
