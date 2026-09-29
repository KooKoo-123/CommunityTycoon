<%-- 회원가입 입력 폼입니다. 입력 name은 JoinRequestDTO 필드에 연결되고 최종 검사는 서버가 수행합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <%@ include file="../common/head.jspf" %>
</head>
<body class="game-title-screen">
    <main class="title-screen-content">
        <header class="game-logo">
            <img class="mascot-logo" src="<c:url value='/resources/images/cat.png'/>" alt="커뮤니티 타이쿤 고양이 로고">
            <h1>커뮤니티<br><span>타이쿤</span></h1>
            <div>COMMUNITY TYCOON</div>
        </header>
        <section class="title-menu" aria-label="회원가입">
            <h2>우리 동네의 첫 운영자가 되어 주세요</h2>
            <p class="title-description">이름을 정하고, 나만의 작은 인터넷 세상을 열어 보세요.</p>
            <c:if test="${not empty message}">
                <div class="notice error alert">
                    <c:out value="${message}" />
                </div>
            </c:if>
            <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
            <form class="stack-form d-grid gap-2" action="<c:url value='/member/join'/>" method="post">
                <label class="form-label" for="loginId">아이디 <small>영문·숫자·밑줄 4~30자</small></label><input id="loginId" class="form-control"
                    name="loginId" required pattern="[a-zA-Z0-9_]{4,30}" maxlength="30" autocomplete="username"
                    value="<c:out value='${loginId}'/>">
                <label class="form-label" for="password">비밀번호 <small>8~20자</small></label>
                <input id="password" class="form-control" type="password" name="password" required minlength="8" maxlength="20" autocomplete="new-password">
                <label class="form-label" for="nickname">운영자 닉네임</label>
                <input id="nickname" class="form-control" name="nickname" required maxlength="30" value="<c:out value='${nickname}'/>" placeholder="커뮤니티에 표시될 닉네임">
                <button class="btn btn-primary wide w-100">회원가입</button>
            </form>
            <div class="auth-bottom">이미 운영 중인가요? <a href="<c:url value='/member/login'/>">로그인</a></div>
        </section>
    </main>
</body>
</html>

