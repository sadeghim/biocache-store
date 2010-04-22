<%-- 
    Document   : show
    Created on : Apr 21, 2010, 9:36:39 AM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Occurrence Record ${id}</title>
    </head>
    <body>
        <h1>Occurrence Details: ${occurrenceRecord.institutionCode} ${occurrenceRecord.collectionCode} ${occurrenceRecord.catalogueNumber}</h1>

        <div id="occurrenceDrillDown" class="occurrenceContainer">
            <c:if test="${not empty occurrenceRecord && !rawOnly}">
                <jsp:include page="actions.jsp"/>
            </c:if>
            <c:set var="noIssues" value="${!rawOnly && occurrenceRecord.taxonomicIssue==0 && occurrenceRecord.geospatialIssue==0 && occurrenceRecord.otherIssue==0}"/>
            <c:if test="${!noIssues}">
                <div id="warnings" class="topContainer">
                    <h4><spring:message code="taxonconcept.drilldown.warnings.title"/></h4>
                    <p>
                    <c:if test="${not empty occurrenceRecord}">
                        <gbiftag:formatGeospatialIssues issuesBit="${occurrenceRecord.geospatialIssue}" messageSource="${messageSource}" locale="${request.getLocale}"/>
                        <c:if test="${not empty geospatialIssueText}">
                            <spring:message code="geospatial.issues"/>: ${geospatialIssueText}<br/>
                        </c:if>
                        <gbiftag:formatOtherIssues issuesBit="${occurrenceRecord.otherIssue}" messageSource="${messageSource}" locale="${request.getLocale}"/>
                        <c:if test="${not empty otherIssueText}">
                            <spring:message code="other.issues"/>: ${otherIssueText}<br/>
                        </c:if>
                        <gbiftag:formatTaxonomicIssues issuesBit="${occurrenceRecord.taxonomicIssue}" messageSource="${messageSource}" locale="${request.getLocale}"/>
                        <c:if test="${not empty taxonomicIssueText}">
                            <spring:message code="taxonomic.issues"/>: ${taxonomicIssueText}<br/>
                        </c:if>
                    </c:if>
                    <c:if test="${empty occurrenceRecord || rawOnly}">
                        <spring:message code="occurrence.record.parse.unavailable"/><br/>
                    </c:if>
                    </p>
                </div>
            </c:if>

            <div id="occurrenceDataset"<c:if test="${noIssues}"> class="topContainer"</c:if>>
                <h4 class="dataset"><spring:message code="occurrence.record.dataset.legend"/></h4>
                <jsp:include page="dataset.jsp"/>
            </div>

            <div id="occurrenceTaxonomy">
                <h4 class="taxonomy"><spring:message code="occurrence.record.taxonomy.legend"/></h4>
                <jsp:include page="taxonomy.jsp"/>
            </div>

            <div id="occurrenceGeospatial">
                <h4 class="geospatial"><spring:message code="occurrence.record.geospatial.legend"/></h4>
                <jsp:include page="geospatial.jsp"/>
            </div>

            <jsp:include page="annotateDataset.jsp"/>
            <jsp:include page="annotateTaxonomy.jsp"/>
            <jsp:include page="annotateGeospatial.jsp"/>
            <jsp:include page="annotateReply.jsp"/>
            <%--<tiles:insert page="annotateComment.jsp"/>--%>

            <c:if test="${not empty rawOccurrenceRecord}">
                <p class="lastModified">
                <spring:message code="occurrence.record.created" text="First indexed"/>: <fmt:formatDate value="${rawOccurrenceRecord.created}"/>
                </p>
                <p class="lastModified">
                <spring:message code="occurrence.record.modified" text="Last indexed"/>: <fmt:formatDate value="${rawOccurrenceRecord.modified}"/>
                </p>
            </c:if>
        </div>

    </body>
</html>
