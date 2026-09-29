// 목록의 첫 이미지 미리보기. 이벤트 위임으로 NPC 활동 후 새로 그린 행에도 적용합니다.
(function() {
    var list = document.getElementById('post-list');
    if (!list) return;
    var popup = document.createElement('div');
    popup.className = 'post-image-preview'; popup.hidden = true;
    popup.setAttribute('aria-hidden', 'true');
    var image = document.createElement('img'); image.alt = '첫 번째 첨부 이미지';
    popup.appendChild(image); document.body.appendChild(popup);
    var active = null;
    function hide() { popup.hidden = true; active = null; image.removeAttribute('src'); }
    function position(x, y) {
        popup.style.left = Math.max(8, Math.min(x + 16, window.innerWidth - popup.offsetWidth - 8)) + 'px';
        popup.style.top = Math.max(8, Math.min(y + 16, window.innerHeight - popup.offsetHeight - 8)) + 'px';
    }
    function show(event) {
        var link = event.target.closest('[data-preview-url]');
        if (!link || link === active) return;
        active = link; popup.hidden = false; image.src = link.dataset.previewUrl;
        var box = link.getBoundingClientRect();
        position(event.clientX == null ? box.left : event.clientX, event.clientY == null ? box.bottom : event.clientY);
    }
    list.addEventListener('mouseover', show);
    list.addEventListener('focusin', show);
    list.addEventListener('mousemove', function(event) { if (active) position(event.clientX, event.clientY); });
    list.addEventListener('mouseout', function(event) { if (active && !active.contains(event.relatedTarget)) hide(); });
    list.addEventListener('focusout', hide);
    image.addEventListener('error', hide);
    window.addEventListener('scroll', hide, true);
    window.addEventListener('resize', hide);
    document.addEventListener('keydown', function(event) { if (event.key === 'Escape') hide(); });
    new MutationObserver(hide).observe(list, {childList: true});
})();
