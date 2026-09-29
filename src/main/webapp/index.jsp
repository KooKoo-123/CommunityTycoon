<%-- [화면 역할] 최초 진입용 JSP입니다. 실제 첫 화면의 이동 처리는 컨트롤러와 함께 확인하세요. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- index.jsp 직접 접근도 HomeController와 동일한 진입 흐름을 사용합니다. --%>
<c:redirect url="/index" />
