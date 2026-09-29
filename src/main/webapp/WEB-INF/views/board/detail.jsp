<%-- 게시글 본문·작성 당시 프로필·첨부 미리보기·댓글을 표시합니다. c:out은 내용을 HTML로 실행하지 않고 출력합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play"><%@ include file="../common/play-start.jspf" %><main class="editor-wrap container py-4"><a class="back-link d-inline-block mb-3"
            href="<c:url value='/board'/>">← 모든 이야기</a>
        <article class="panel post-detail card p-4"><span
                class="author-tag ${post.authorType == 'PLAYER' ? 'player' : ''} badge">${post.authorType == 'PLAYER' ? '운영자의
                이야기' : 'NPC의 이야기'}</span>
            <h1>
                <c:out value="${post.title}" />
            </h1>
            <div class="post-meta d-flex flex-wrap align-items-center gap-3 small my-3"><c:choose><c:when test="${post.authorType == 'PLAYER' and not empty post.authorProfileImageId}"><img class="profile-image" src="<c:url value='/files/${post.authorProfileImageId}'/>" alt="작성 당시 프로필"></c:when><c:otherwise><span class="profile-image-fallback">${post.authorType == 'PLAYER' ? 'M' : 'N'}</span></c:otherwise></c:choose><b>
                    <c:out value="${post.authorNickname}" />
                </b><span>DAY ${post.createdDay}</span></div>
            <div class="post-images">
                <c:forEach items="${attachments}" var="file">
                    <c:if test="${file.image}"><img class="post-attachment-image" src="<c:url value='/files/${file.uploadId}'/>" alt="<c:out value='${file.originalName}'/>" loading="lazy"></c:if>
                </c:forEach>
            </div>
            <div class="post-body"><c:out value="${post.content}" /></div>
            <c:if test="${not empty attachments}">
                <section class="attachment-box" aria-label="첨부파일">
                    <h2>첨부파일 <span>${attachments.size()}개</span></h2>
                    <ul class="attachment-list">
                        <c:forEach items="${attachments}" var="file">
                            <li><span><c:out value="${file.originalName}"/></span>
                                <a class="btn btn-sm" data-download="true" href="<c:url value='/files/${file.uploadId}?download=true'/>">다운로드</a>
                            </li>
                        </c:forEach>
                    </ul>
                </section>
            </c:if>
            <div class="post-actions d-flex justify-content-between align-items-center mt-4 pt-3 border-top"><a class="btn btn-outline-light" href="<c:url value='/board'/>">목록으로</a>
                <c:if test="${post.authorType == 'PLAYER'}">
                    <div><a class="btn btn-outline-light" href="<c:url value='/board/${post.boardId}/edit'/>">수정</a>
                        <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                        <form class="inline-form delete-form d-inline-block" action="<c:url value='/board/${post.boardId}/delete'/>"
                            method="post"><button class="btn btn-danger-soft">삭제</button></form>
                    </div>
                </c:if>
            </div>
        </article>
        <section id="comments" class="comment-section card mt-4 p-4">
            <h2>댓글 <c:if test="${site.hasComments}"><span>${comments.size()}</span></c:if>
            </h2>
            <div id="comment-content-panel" ${not site.hasComments ? 'hidden' : ''}>
                    <c:forEach items="${comments}" var="comment">
                        <article class="comment-item py-3 border-bottom">
                            <header><c:choose><c:when test="${not empty comment.authorProfileImageId}"><img class="profile-image me-2" src="<c:url value='/files/${comment.authorProfileImageId}'/>" alt="작성 당시 프로필"></c:when><c:otherwise><span class="profile-image-fallback">${comment.authorType == 'NPC' ? 'N' : 'M'}</span></c:otherwise></c:choose><strong>
                                    <c:out value="${comment.displayName}" />
                                </strong></header>
                            <p class="comment-content"><c:out value="${comment.content}" /></p>
                            <c:if test="${comment.authorType == 'PLAYER'}">
                                <div class="comment-actions d-flex align-items-start gap-3">
                                    <details>
                                        <summary>수정</summary>
                                        <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                                        <form action="<c:url value='/board/${post.boardId}/comments/${comment.commentId}/edit'/>"
                                            method="post">
                                            <label class="form-label" for="comment-edit-${comment.commentId}">댓글 수정</label>
                                            <textarea class="form-control" id="comment-edit-${comment.commentId}" name="content" required
                                                maxlength="1000" rows="3"><c:out value="${comment.content}"/></textarea>
                                            <button class="site-button primary btn btn-sm" type="submit">수정 저장</button>
                                        </form>
                                    </details>
                                    <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                                    <form class="delete-form"
                                        action="<c:url value='/board/${post.boardId}/comments/${comment.commentId}/delete'/>"
                                        method="post"><button class="site-button btn btn-sm" type="submit">댓글 삭제</button></form>
                                </div>
                            </c:if>
                        </article>
                    </c:forEach>
                    <c:if test="${empty comments}">
                        <p class="comment-empty">아직 댓글이 없어요. 첫 이야기를 건네 보세요.</p>
                    </c:if>
                    <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                    <form class="comment-form d-grid gap-2 mt-4" action="<c:url value='/board/${post.boardId}/comments'/>" method="post">
                        <label class="form-label" for="comment-content">댓글 남기기</label>
                        <textarea class="form-control" id="comment-content" name="content" required maxlength="1000" rows="3"
                            placeholder="어떤 생각이 드셨나요? (최대 1,000자)"></textarea>
                        <button class="site-button primary btn btn-sm" type="submit">댓글 등록</button>
                    </form>
                </div>
                <div id="comment-locked" ${site.hasComments ? 'hidden' : ''}>
                    <p class="comment-empty">아직 댓글 기능이 잠겨 있어요. 상단 [기능 구매]에서 댓글을 구매해 주세요.</p>
                </div>
        </section>
    </main><%@ include file="../common/play-end.jspf" %></body>
</html>
