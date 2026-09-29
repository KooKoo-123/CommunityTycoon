<%-- 예외 처리기가 Model에 넣은 오류 안내를 표시하는 공통 화면입니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="common/head.jspf" %></head>
<body>
    <main class="error-page panel card container p-4 mt-5"><span class="eyebrow">LET'S TAKE A SMALL PAUSE</span>
        <h1>잠깐만요, 운영자님.</h1>
        <p>
            <c:out value="${message}" />
        </p>
        <c:choose>
            <c:when test="${loginRequired}">
                <a class="btn btn-primary" href="<c:url value='/member/login?expired=1'/>">다시 로그인하기 →</a>
            </c:when>
            <c:otherwise>
                <a class="btn btn-primary" href="<c:url value='/'/>">내 사이트로 돌아가기 →</a>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
