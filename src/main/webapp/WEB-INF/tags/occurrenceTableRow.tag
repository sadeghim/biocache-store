<%@ include file="/common/taglibs.jsp" %>
<%@ attribute name="fieldName" required="true" type="java.lang.String" %>
<c:set var="bodyText"><jsp:doBody/></c:set>
<c:if test="${not empty bodyText}">
    <tr>
        <td class="label">${fieldName}</td>
        <td class="value">${bodyText}</td>
    </tr>
</c:if>