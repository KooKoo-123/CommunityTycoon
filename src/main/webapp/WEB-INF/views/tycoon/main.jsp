<%-- 게임 대문과 게시판 목록입니다. 서버 자료를 처음 표시한 뒤 js/tycoon/ui.js가 바뀐 목록과 HUD를 갱신합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play">
    <%@ include file="../common/play-start.jspf" %>
    <%-- 두 영역을 미리 만들어 놓고 구매 순간 hidden 속성만 바꿉니다. --%>
    <main id="hello-site" class="hello-site" ${site.hasBoard ? 'hidden' : ''}>
        <h1>Hello world!</h1>
        <p>The time on the server is <c:out value="${serverTime}" />.</p>
    </main>
    <main id="board-panel" class="live-board container py-4" ${not site.hasBoard ? 'hidden' : ''}>
        <header class="live-board-heading d-flex justify-content-between align-items-center pb-3 mb-3">
            <div><h1>게시판</h1><p>우리 동네 이웃들의 작은 이야기</p></div>
            <a class="site-button primary btn btn-sm" href="<c:url value='/board/write'/>">글쓰기</a>
        </header>
        <p id="comment-locked" ${site.hasComments ? 'hidden' : ''} class="small text-muted">댓글을 구매하면 글에 답글이 달리기 시작해요.</p>
        <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
        <form id="board-search" data-module-panel="search" ${not site.hasSearch ? 'hidden' : ''} class="row g-2 mb-3" method="get" action="<c:url value='/tycoon'/>">
            <div class="col-sm"><label class="visually-hidden" for="search-title">제목 검색</label><input class="form-control" id="search-title" name="title" placeholder="제목 검색" value="<c:out value='${pageRequest.title}'/>"></div>
            <div class="col-sm"><label class="visually-hidden" for="search-writer">작성자 검색</label><input class="form-control" id="search-writer" name="writer" placeholder="작성자 검색" value="<c:out value='${pageRequest.writer}'/>"></div>
            <div class="col-auto"><button class="btn btn-primary" type="submit">검색</button> <a class="btn btn-outline-secondary" href="<c:url value='/tycoon'/>">초기화</a></div>
        </form>
        <p id="new-post-notice" class="small" hidden>새 이야기가 도착했어요. <a href="<c:url value='/tycoon'/>">목록 새로 보기</a></p>
        <div class="table-responsive">
            <table class="live-board-table table table-hover align-middle">
                <thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>작성일</th></tr></thead>
                <tbody id="post-list">
                    <c:forEach items="${posts}" var="post">
                        <tr>
                            <td>${post.displayNo}</td>
                            <td><a href="<c:url value='/board/${post.boardId}'/>" <c:if test="${not empty post.firstImageUrl}">data-preview-url="<c:url value='${post.firstImageUrl}'/>"</c:if>><c:out value="${post.title}" /></a></td>
                            <td><c:if test="${post.authorType == 'PLAYER' and not empty post.authorProfileImageId}"><img class="profile-image me-2" src="<c:url value='/files/${post.authorProfileImageId}'/>" alt="작성 당시 프로필"></c:if><c:out value="${post.authorNickname}" /> <span class="writer-label">${post.authorType == 'PLAYER' ? '운영자' : 'NPC'}</span></td>
                            <td>DAY ${post.createdDay}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <nav id="board-pages" data-module-panel="paging" ${not site.hasPaging ? 'hidden' : ''} class="d-flex justify-content-center gap-2 my-3" aria-label="게시글 페이지">
            <c:if test="${pagination.prev}"><c:url var="prevUrl" value="/tycoon"><c:param name="page" value="${pagination.start - 1}"/><c:param name="title" value="${pageRequest.title}"/><c:param name="writer" value="${pageRequest.writer}"/></c:url><a class="btn btn-sm" href="${prevUrl}">이전</a></c:if>
            <c:if test="${not empty pagination}"><c:forEach begin="${pagination.start}" end="${pagination.end}" var="pageNo">
                <c:url var="pageUrl" value="/tycoon"><c:param name="page" value="${pageNo}"/><c:param name="title" value="${pageRequest.title}"/><c:param name="writer" value="${pageRequest.writer}"/></c:url>
                <a class="btn btn-sm ${pageNo == pagination.page ? 'btn-primary' : 'btn-outline-secondary'}" href="${pageUrl}" ${pageNo == pagination.page ? 'aria-current="page"' : ''}>${pageNo}</a>
            </c:forEach></c:if>
            <c:if test="${pagination.next}"><c:url var="nextUrl" value="/tycoon"><c:param name="page" value="${pagination.end + 1}"/><c:param name="title" value="${pageRequest.title}"/><c:param name="writer" value="${pageRequest.writer}"/></c:url><a class="btn btn-sm" href="${nextUrl}">다음</a></c:if>
        </nav>
        <p id="empty-posts" ${not empty posts ? 'hidden' : ''} class="text-center text-muted">아직 글이 없어요. 잠시 기다리면 이웃들이 찾아옵니다.</p>
    </main>
    <%@ include file="../common/play-end.jspf" %>
</body>
</html>
