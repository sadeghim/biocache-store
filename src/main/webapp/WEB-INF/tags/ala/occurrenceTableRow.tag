<%@ include file="/common/taglibs.jsp" %>
<%@ attribute name="section" required="true" type="java.lang.String" %>
<%@ attribute name="fieldName" required="true" type="java.lang.String" %>
<%@ attribute name="messageCode" required="false" type="java.lang.String" %>
<%@ attribute name="annotate" required="true" type="java.lang.Boolean" %>
<%@ attribute name="matchedTaxa" required="false" type="org.gbif.portal.dto.taxonomy.BriefTaxonConceptDTO" %>
<%@ attribute name="headingName" required="false" type="java.lang.String" %>
<c:set var="annoIcon"><c:if test="${annotate}">${section}</c:if></c:set>
<c:set var="bodyText"><jsp:doBody/></c:set>
<c:if test="${not empty bodyText || not empty matchedTaxa}">
    <tr id="${fieldName}">
        <td class="label">
            <c:if test="${not empty messageCode}"><spring:message code="${messageCode}" text="${messageCode}"/></c:if>
        </td>
        <td class="annoText" name="${fieldName}"></td>
        <c:choose>
            <c:when test="${not empty headingName}">
                <th class="recordedValue"><jsp:doBody/></th>
                <th class="matchedValue">${headingName}</th>
            </c:when>
            <c:when test="${matchedTaxa!=null}">
                <td class="value recordedValue">
                    ${bodyText}
                    <div class="annoText"/>
                </td>
                <td class="matchedValue">
                    <alatag:linkGbifTaxonConcept concept='${matchedTaxa}' />
                </td>
            </c:when>
            <c:otherwise>
                <td class="value" colspan="2">
                    ${bodyText}
                    <div class="annoText"/>
                </td>
            </c:otherwise>
        </c:choose>
        <td class="${annoIcon}" name="${fieldName}"></td>
    </tr>
</c:if>