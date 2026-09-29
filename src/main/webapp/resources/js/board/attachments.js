// 작성/수정 화면의 파일 선택과 미리보기만 담당합니다.
(function() {
    var input = document.getElementById('post-files');
    if (!input) return;
    var form = input.form;
    var preview = document.getElementById('new-file-previews');
    var selected = [];
    var urls = [];
    function render() {
        urls.forEach(function(url) { URL.revokeObjectURL(url); });
        urls = [];
        preview.replaceChildren();
        // FileList를 직접 수정할 수 없어 DataTransfer로 남길 파일 목록을 새로 구성합니다.
        var transfer = new DataTransfer();
        selected.forEach(function(file, index) {
            transfer.items.add(file);
            var box = document.createElement('div');
            box.className = 'attachment-thumb';
            if (['image/png', 'image/jpeg', 'image/gif'].includes(file.type)) {
                var image = document.createElement('img');
                var url = URL.createObjectURL(file);
                urls.push(url);
                image.src = url;
                image.alt = file.name;
                box.appendChild(image);
            }
            var name = document.createElement('span');
            name.textContent = file.name;
            box.appendChild(name);
            var button = document.createElement('button');
            button.type = 'button';
            button.className = 'remove-button';
            button.textContent = '×';
            button.setAttribute('aria-label', file.name + ' 선택 취소');
            button.addEventListener('click', function() { selected.splice(index, 1); render(); });
            box.appendChild(button);
            preview.appendChild(box);
        });
        input.files = transfer.files;
    }
    input.addEventListener('change', function() {
        var incoming = Array.from(input.files);
        var existing = document.querySelectorAll('[data-existing-upload]').length;
        if (existing + selected.length + incoming.length > 5 || incoming.some(function(file) { return file.size > 10 * 1024 * 1024; })) {
            alert('첨부파일은 최대 5개, 각 10 MB까지 가능합니다.');
        } else selected = selected.concat(incoming);
        render();
    });
    // 기존 첨부의 X는 removeIds 숨김 입력을 추가합니다. 실제 DB/S3 처리는 수정 폼을 저장할 때 실행됩니다.
    document.querySelectorAll('[data-remove-upload]').forEach(function(button) {
        button.addEventListener('click', function() {
            var removed = document.createElement('input');
            removed.type = 'hidden';
            removed.name = 'removeIds';
            removed.value = button.dataset.removeUpload;
            form.appendChild(removed);
            button.closest('[data-existing-upload]').remove();
        });
    });
})();
