<%--
    Document   : list
    Created on : Apr 21, 2010, 9:36:39 AM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="pageName" content="species"/>
        <title>Occurrence Search Results</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-ui-1.8.custom.min.js"></script>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bie-theme/jquery-ui-1.8.custom.css" charset="utf-8">
        <script type="text/javascript">
            $(document).ready(function() {
                var facetLinksSize = $("ul#subnavlist li").size();
                if (facetLinksSize == 0) {
                    // Hide an empty facet link list
                    $("#facetBar > h4").hide();
                    $("#facetBar #navlist").hide();
                }

                var icons = {
			header: "ui-icon-circle-arrow-e",
			headerSelected: "ui-icon-circle-arrow-s"
		};
		$("#accordion").accordion({
			icons: icons,
                        autoHeight: false
		});
		$("#toggle").button().toggle(function() {
			$("#accordion").accordion("option", "icons", false);
		}, function() {
			$("#accordion").accordion("option", "icons", icons);
		});
            });
        </script>
    </head>
    <body>
        <h1>Occurrence Search Results</h1>
        <c:if test="${not empty query}">
            
            <div id="searchResults" >
                <h3>Search results for <a href="">${queryJsEscaped}</a> - ${searchResult.totalRecords} results found</h3>
                <table class="solrResults">
                    <thead>
                        <tr>
                            <th>Scientific Name</th>
                            <th>Dataset</th>
                            <th>Type</th>
                            <th>Year</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="occurrence" items="${searchResult.occurrences}">
                            <tr>
                                <td><a href="${occurrence.id}">${occurrence.taxonName}</a></td>
                                <td>${occurrence.dataResource}</td>
                                <td>${occurrence.basisOfRecord}</td>
                                <td>${occurrence.year}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <div id="facets">
                <h4>Refine your results:</h4>
                <div id="accordion">
                    <c:if test="${not empty query}"><c:set var="queryParam">q=<c:out value="${query}&title=${title}" escapeXml="true"/></c:set></c:if>
                    <c:forEach var="facetResult" items="${searchResult.facetResults}">
                        <c:if test="${!fn:containsIgnoreCase(facetQuery, facetResult.fieldResult[0].label)}">
                            <h3><a href="#"><span class="FieldName"><fmt:message key="facet.${facetResult.fieldName}"/> [${fn:length(facetResult.fieldResult)}]</span></a></h3>
                            <div id="subnavlist"><ul>
                                <c:forEach var="fieldResult" items="${facetResult.fieldResult}">
                                    <%-- test to see if the current facet search is also a listed facet link --%>
                                    <%--<c:if test="${!fn:containsIgnoreCase(facetQuery, fieldResult.label)}">--%>
                                    <li><a href="?fq=${facetResult.fieldName}:${fieldResult.label}&${queryParam}">
                                            <fmt:message key="${fieldResult.label}"/> (${fieldResult.count})</a>
                                    </li>
                                    <%--</c:if>--%>
                                </c:forEach>
                            </ul></div>
                        </c:if>
                    </c:forEach>
                </div>
                <br/>
                <c:if test="${not empty facetQuery}">
                    <div id="removeFacet">
                        <h4>Displaying subset of results, restricted to: <span id="facetName">
                                <fmt:message key="${fn:substringAfter(facetQuery, ':')}"/></span></h4>
                        <p>&bull; <a href="?<c:out value="${queryParam}"/>">Return to full result list</a></p>
                    </div>
                </c:if>
            </div>
        </c:if>
    </body>
</html>