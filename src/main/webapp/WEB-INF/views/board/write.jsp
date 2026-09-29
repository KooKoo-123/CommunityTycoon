<%-- 새 글 작성 폼입니다. multipart/form-data는 텍스트와 파일을 함께 전송할 때 사용하는 형식입니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play"><%@ include file="../common/play-start.jspf" %><main class="editor-wrap container py-4"><a class="back-link d-inline-block mb-3"
            href="<c:url value='/board'/>">← 게시판</a>
        <div class="eyebrow">LEAVE A LITTLE STORY</div>
        <h1>새로운 이야기</h1>
        <p class="muted text-muted">당신의 한마디가 이 공간의 첫 분위기를 만듭니다.</p>
        <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
        <form class="panel editor-form stack-form card p-4 d-grid gap-2" method="post" enctype="multipart/form-data" action="<c:url value='/board'/>">
            <label class="form-label" for="title">제목</label><input class="form-control title-input" id="title" name="title" required
                maxlength="100" placeholder="어떤 이야기를 나누고 싶나요?" value="<c:out value='${post.title}'/>">
            <label class="form-label" for="content">내용</label><textarea id="content" class="form-control" name="content" required
                maxlength="10000" rows="11"
                placeholder="커뮤니티에 전하고 싶은 이야기를 자유롭게 적어 주세요."><c:out value="${post.content}"/></textarea>
            <div data-module-panel="attachment" ${not site.hasAttachment ? 'hidden' : ''}>
                <label class="form-label" for="post-files">첨부파일 (최대 5개, 각 10 MB)</label>
                <input class="form-control" type="file" name="files" id="post-files" multiple>
                <p class="small text-muted mt-2">X로 제외한 뒤 글을 저장하면 반영됩니다. 수정 취소 시 기존 파일은 유지됩니다.</p>
                <div id="new-file-previews" class="d-flex flex-wrap gap-2"></div>
            </div>
            <div class="form-actions d-flex justify-content-end gap-2 mt-3"><a class="btn btn-outline-light" href="<c:url value='/board'/>">취소</a><button
                    class="btn btn-primary">이야기 등록하기 →</button></div>
        </form>
    </main><%@ include file="../common/play-end.jspf" %></body>
</html>

