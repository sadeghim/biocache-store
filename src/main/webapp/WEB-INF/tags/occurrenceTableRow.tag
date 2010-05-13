<%@ include file="/common/taglibs.jsp" %><%@
    attribute name="fieldName" required="true" type="java.lang.String" %><%@
    attribute name="fieldNameIsMsgCode" required="false" type="java.lang.Boolean" %><%@
    attribute name="link" required="false" type="java.lang.String" %>
<c:set var="bodyText"><jsp:doBody/></c:set>
<c:if test="${not empty bodyText}">
    <tr>
        <td class="label">
            <c:choose>
                <c:when test="${fieldNameIsMsgCode}"><fmt:message key="${fieldName}"/></c:when>
                <c:otherwise>${fieldName}</c:otherwise>
            </c:choose>
        </td>
        <td class="value">
            <c:if test="${not empty link}"><a href="${link}"></c:if>${bodyText}<c:if test="${not empty link}"></a></c:if>
        </td>
    </tr>
</c:if>