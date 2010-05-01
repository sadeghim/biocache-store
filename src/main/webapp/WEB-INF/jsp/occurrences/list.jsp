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
        <script type="text/javascript">
            $(document).ready(function() {
                var facetLinksSize = $("ul#subnavlist li").size();
                if (facetLinksSize == 0) {
                    // Hide an empty facet link list
                    $("#facetBar > h4").hide();
                    $("#facetBar #navlist").hide();
                }
            });
        </script>
    </head>
    <body>
        <h1>Occurrence Search Results</h1>
        <c:if test="${not empty query}">
            <div id="facetBar">
                <h4>Refine your results:</h4>
                <ul id="navlist">
                    <c:if test="${not empty query}"><c:set var="queryParam">q=<c:out value="${query}&title=${title}" escapeXml="true"/></c:set></c:if>
                    <c:forEach var="facetResult" items="${searchResults.facetResults}">
                        <c:if test="${!fn:containsIgnoreCase(facetQuery, facetResult.fieldResult[0].label)}">
                            <li><span class="FieldName"><fmt:message key="facet.${facetResult.fieldName}"/></span></li>
                            <ul id="subnavlist">
                                <c:forEach var="fieldResult" items="${facetResult.fieldResult}">
                                    <%-- test to see if the current facet search is also a listed facet link --%>
                                    <%--<c:if test="${!fn:containsIgnoreCase(facetQuery, fieldResult.label)}">--%>
                                    <li id="subactive">
                                        <a href="?fq=${facetResult.fieldName}:${fieldResult.label}&${queryParam}">
                                            <fmt:message key="${facetResult.fieldName}.${fieldResult.label}"/> (${fieldResult.count})</a>
                                    </li>
                                    <%--</c:if>--%>
                                </c:forEach>
                            </ul>
                        </c:if>
                    </c:forEach>
                </ul>
                <br/>
                <c:if test="${not empty facetQuery}">
                    <div id="removeFacet">
                        <h4>Displaying subset of results, restricted to: <span id="facetName">
                                <fmt:message key="${fn:substringAfter(facetQuery, ':')}"/></span></h4>
                        <p>&bull; <a href="?<c:out value="${queryParam}"/>">Return to full result list</a></p>
                    </div>
                </c:if>
            </div>
            <div id="results" >
                <h3>Search results for <a href="">${queryJsEscaped}</a> - ${searchResults.totalRecords} results found</h3>
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
                        <c:forEach var="occurrence" items="${searchResults.occurrences}">
                            <tr>
                                <td><a href="../${occurrence}">${occurrence.taxonName}</a></td>
                                <td>${occurrence.dataResource}</td>
                                <td>${occurrence.basisOfRecord}</td>
                                <td>${occurrence.year}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </body>
</html>