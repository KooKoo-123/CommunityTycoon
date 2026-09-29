<%-- 관리자냥 소개 화면입니다. 게임 상태와 공통 HUD는 다른 게임 화면과 공유합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play">
    <%@ include file="../common/play-start.jspf" %>
    <main class="container py-5 profile-page">
        <a href="<c:url value='/member/mypage'/>">← 마이페이지</a>
        <article class="card p-4 p-lg-5 mt-3 manager-detail">
            <div class="row align-items-center g-4">
                <div class="col-md-4 text-center"><img class="manager-portrait" src="<c:url value='/resources/images/cat.png'/>" alt="관리자냥"><p class="manager-status mt-3">☕ 휴식 중</p></div>
                <div class="col-md-8"><div class="small text-muted">FRONTEND SECURITY LEAD</div><h1>🐾 관리자냥</h1><p>수석 앱 보안 엔지니어</p>
                    <dl class="manager-facts">
                        <dt>직함</dt><dd>커뮤니티 관리자 및 수석 앱 보안 엔지니어 (Frontend Security Lead)</dd>
                        <dt>특기</dt><dd>프록시(Proxy) 하나로 객체 감시하기</dd>
                        <dt>약점</dt><dd>프록시 외에는 다른 보안을 잘 모름. 서버 보안은 백엔드에게 전적으로 떠넘김.</dd>
                        <dt>성향</dt><dd>평소에는 꿀 빠는 줄 알았으나, 유저가 콘솔창을 건드리는 순간 누구보다 빠르게 경악하는 프로 직장인.</dd>
                    </dl>
                    <p class="manager-quote">“잠깐 졸고 있었을 뿐이다냥. 골드는 보고 있다냥!”</p>
                </div>
            </div>
        </article>
    </main>
    <%@ include file="../common/play-end.jspf" %>
</body>
</html>
