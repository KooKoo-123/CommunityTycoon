<%-- 닉네임·테마·프로필과 사진 보관함 화면입니다. 구매 상태에 따라 기능을 표시하고 각 폼을 서버에 제출합니다. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head><%@ include file="../common/head.jspf" %></head>
<body class="tycoon-play">
    <%@ include file="../common/play-start.jspf" %>
    <main class="container py-4 profile-page">
        <a href="<c:url value='/tycoon'/>" class="back-link">← 내 커뮤니티</a>
        <header class="my-4"><h1>운영자의 작은 방</h1><p>나를 소개하고, 우리 동네 분위기를 꾸며 보세요.</p>
        </header>
        <section id="mypage-locked" class="card p-4" ${site.hasMypage ? 'hidden' : ''}>
            <h2>🔒 아직 문이 잠겨 있어요</h2><p>상단 [기능 구매]에서 마이페이지를 구매하면 내 방이 열립니다.</p>
        </section>
        <div data-module-panel="mypage" ${not site.hasMypage ? 'hidden' : ''}>
            <c:if test="${not empty notice}"><p class="alert alert-success"><c:out value="${notice}"/></p></c:if>
            <div class="row g-4">
                <section class="col-lg-7"><div class="card p-4 h-100">
                    <h2>내 프로필</h2>
                    <div class="d-flex gap-3 align-items-center my-3">
                        <c:choose><c:when test="${not empty site.userProfileImageUploadId}"><img class="profile-portrait" src="<c:url value='/files/${site.userProfileImageUploadId}'/>" alt="내 프로필"></c:when>
                        <c:otherwise><img class="profile-portrait" src="<c:url value='/resources/images/cat.png'/>" alt="기본 고양이"></c:otherwise></c:choose>
                        <div><strong><c:out value="${site.userNickname}"/>님</strong><p class="small mb-0"><c:out value="${currentMember.loginId}"/> · ${currentMember.role == 'ADMIN' ? '관리자 계정' : '사용자 계정'}</p></div>
                    </div>
                    <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                    <form action="<c:url value='/member/profile'/>" method="post" class="d-grid gap-2">
                        <label for="profile-nickname" class="form-label">닉네임</label>
                        <input id="profile-nickname" class="form-control" name="nickname" maxlength="30" required value="<c:out value='${site.userNickname}'/>">
                        <label for="profile-theme" class="form-label mt-2">테마</label>
                        <select id="profile-theme" class="form-select" name="theme">
                            <option value="sky" ${site.userTheme == 'sky' ? 'selected' : ''}>하늘빛 마을 · 기본</option>
                            <option id="forest-theme-option" value="forest" ${site.userTheme == 'forest' ? 'selected' : ''} ${not site.hasTheme ? 'disabled' : ''}>위스터리아 빌리지 (Wisteria Village - 등나무 마을) · 구매 테마</option>
                        </select>
                        <button type="submit" class="btn btn-primary mt-2">프로필 변경</button>
                    </form>
                    <hr>
                    <p id="profile-image-locked" ${site.hasProfileImage ? 'hidden' : ''} class="small text-muted">사진을 바꾸려면 프로필 사진 모듈을 구매하세요.</p>
                    <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                    <form data-module-panel="profileImage" ${not site.hasProfileImage ? 'hidden' : ''} action="<c:url value='/files/profile-image'/>" method="post" enctype="multipart/form-data" class="d-grid gap-2">
                        <label class="form-label" for="profile-photo">새 프로필 사진</label>
                        <input id="profile-photo" type="file" name="file" class="form-control" accept="image/png,image/jpeg,image/gif" required>
                        <small>PNG·JPG·GIF / 5 MB 이하. 이전 사진은 그대로 보관해요.</small>
                        <button type="submit" class="btn btn-outline-secondary">사진 업로드 및 변경</button>
                    </form>
                    <hr>
                    <h3 class="h6">프로필 사진 보관함</h3>
                    <p id="profile-image-storage-locked" ${site.hasProfileImageStorage ? 'hidden' : ''} class="small text-muted">프로필 보관함 모듈을 구매하면 예전 사진을 다시 선택할 수 있어요.</p>
                    <div data-module-panel="profileImageStorage" ${not site.hasProfileImageStorage ? 'hidden' : ''} class="profile-image-storage-list d-flex gap-2 overflow-auto py-2">
                        <c:forEach items="${profileImageStorageList}" var="photo">
                            <div class="position-relative flex-shrink-0">
                            <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                            <form action="<c:url value='/files/profile-image/select'/>" method="post">
                                <input type="hidden" name="profileImageStorageId" value="${photo.profileImageStorageId}">
                                <button class="profile-image-storage-choice ${site.userProfileImageId == photo.profileImageStorageId ? 'is-current' : ''}" type="submit" aria-label="이 사진으로 변경" ${site.userProfileImageId == photo.profileImageStorageId ? 'disabled' : ''}>
                                    <img src="<c:url value='/files/${photo.uploadId}'/>" alt="보관된 프로필 사진" loading="lazy">
                                    <span>${site.userProfileImageId == photo.profileImageStorageId ? '현재 사진' : '선택'}</span>
                                </button>
                            </form>
                            <%-- [입력 처리] action은 받을 서버 주소, method는 요청 방식입니다. name 값으로 Java 매개변수와 연결됩니다. --%>
                            <form action="<c:url value='/files/profile-image/delete'/>" method="post" class="delete-form" data-confirm="보관함에서 삭제하시겠습니까?">
                                <input type="hidden" name="profileImageStorageId" value="${photo.profileImageStorageId}">
                                <button type="submit" class="remove-button" aria-label="보관함에서 사진 삭제">×</button>
                            </form>
                            </div>
                        </c:forEach>
                        <c:if test="${empty profileImageStorageList}"><p class="small">새 사진을 올리면 여기에 보관됩니다.</p></c:if>
                    </div>
                </div></section>
                <section class="col-lg-5"><a class="card p-4 manager-card h-100 text-decoration-none" href="<c:url value='/member/manager'/>">
                    <h2>커뮤니티 관리자</h2>
                    <img class="manager-portrait my-4" src="<c:url value='/resources/images/cat.png'/>" alt="관리자냥">
                    <h3>관리자냥</h3><p class="manager-status">💤 자는 중</p>
                    <p class="small">평화로운 커뮤니티를 지켜보는 중입니다.<br>프로필을 눌러 관리자를 만나 보세요.</p><span>상세 프로필 보기 →</span>
                </a></section>
            </div>
        </div>
    </main>
    <%@ include file="../common/play-end.jspf" %>
</body>
</html>
