<%-- 기존 글 수정 폼입니다. attachments.js가 새 파일 미리보기와 삭제 대상 번호(removeIds)를 관리합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play"><%@ include file="../common/play-start.jspf" %><main class="editor-wrap container py-4"><a class="back-link d-inline-block mb-3"
            href="<c:url value='/board'/>">← 게시판</a>
        <div class="eyebrow">LEAVE A LITTLE STORY</div>
        <h1>이야기 다듬기</h1>
        <p class="muted text-muted">이야기의 제목과 내용을 수정해 주세요.</p>
        <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
        <form class="panel editor-form stack-form card p-4 d-grid gap-2" method="post" enctype="multipart/form-data" action="<c:url value='/board/${post.boardId}/edit'/>">
            <label class="form-label" for="title">제목</label><input class="form-control title-input" id="title" name="title" required
                maxlength="100" placeholder="어떤 이야기를 나누고 싶나요?" value="<c:out value='${post.title}'/>">
            <label class="form-label" for="content">내용</label><textarea id="content" class="form-control" name="content" required
                maxlength="10000" rows="11"
                placeholder="커뮤니티에 전하고 싶은 이야기를 자유롭게 적어 주세요."><c:out value="${post.content}"/></textarea>
            <div data-module-panel="attachment" ${not site.hasAttachment ? 'hidden' : ''} class="d-flex flex-wrap gap-2">
                <c:forEach items="${attachments}" var="file">
                    <div class="attachment-thumb" data-existing-upload="${file.uploadId}">
                        <c:if test="${file.image}"><img src="<c:url value='/files/${file.uploadId}'/>" alt="<c:out value='${file.originalName}'/>"></c:if>
                        <span><c:out value="${file.originalName}"/></span>
                        <button class="remove-button" type="button" data-remove-upload="${file.uploadId}" aria-label="첨부파일 제외">×</button>
                    </div>
                </c:forEach>
            </div>
            <div data-module-panel="attachment" ${not site.hasAttachment ? 'hidden' : ''}>
                <label class="form-label" for="post-files">첨부파일 (최대 5개, 각 10 MB)</label>
                <input class="form-control" type="file" name="files" id="post-files" multiple>
                <p class="small text-muted mt-2">X로 제외한 뒤 글을 저장하면 반영됩니다. 수정 취소 시 기존 파일은 유지됩니다.</p>
                <div id="new-file-previews" class="d-flex flex-wrap gap-2"></div>
            </div>
            <div class="form-actions d-flex justify-content-end gap-2 mt-3"><a class="btn btn-outline-light" href="<c:url value='/board'/>">취소</a><button
                    class="btn btn-primary">수정 내용 저장 →</button></div>
        </form>
    </main><%@ include file="../common/play-end.jspf" %></body>
</html>

