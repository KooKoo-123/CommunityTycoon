<%-- 아이디·비밀번호를 서버로 제출합니다. common/form.js가 중복 제출을 막고 member/login.js가 고양이 연출을 처리합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <%@ include file="../common/head.jspf" %>
    <link rel="preload" as="image" href="<c:url value='/resources/images/meow_cat.png'/>">
</head>
<body class="game-title-screen">
    <main class="title-screen-content">
        <header class="game-logo">
            <div class="login-mascot-frame">
                <img id="login-mascot" data-idle-src="<c:url value='/resources/images/cat.png'/>" data-active-src="<c:url value='/resources/images/meow_cat.png'/>" class="mascot-logo" src="<c:url value='/resources/images/cat.png'/>" alt="커뮤니티 타이쿤 고양이 로고">
            </div>
            <h1>커뮤니티<br><span>타이쿤</span></h1>
            <div>COMMUNITY TYCOON</div>
        </header>
        <section class="title-menu" aria-label="로그인">
            <h2>돌아오신 걸 환영해요!</h2>
            <c:if test="${param.expired == '1'}"><p class="notice error">다른 곳에서 로그인했거나 세션이 만료되었습니다. 다시 로그인해 주세요.</p></c:if>
            <p class="title-description">작은 사이트에서 시작하는, 북적북적 커뮤니티 생활.</p>
            <c:if test="${not empty message}">
                <div class="notice error alert">
                    <c:out value="${message}" />
                </div>
            </c:if>
            <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
            <form id="login-form" action="<c:url value='/member/login'/>" method="post" class="stack-form d-grid gap-2">
                <label class="form-label" for="loginId">아이디</label>
                <input class="form-control" id="loginId" name="loginId" value="<c:out value='${loginId}'/>" required
                    maxlength="30" autocomplete="username" placeholder="운영자 아이디">
                <label class="form-label" for="password">비밀번호</label>
                <input class="form-control" id="password" type="password" name="password" required maxlength="20"
                    autocomplete="current-password" placeholder="비밀번호">
                <button class="btn btn-primary wide w-100" type="submit">로그인</button>
            </form>
            <div class="auth-bottom">아직 사이트가 없나요? <a href="<c:url value='/member/join'/>">회원가입</a></div>
        </section>
    </main>
</body>
</html>
