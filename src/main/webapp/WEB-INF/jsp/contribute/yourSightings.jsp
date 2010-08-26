<%-- 
    Document   : sighting
    Created on : Aug 6, 2010, 5:19:21 PM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="googleKey" scope="request"><ala:propertyLoader bundle="biocache" property="googleKey"/></c:set>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="UTF-8" >
        <title>Your Sightings</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/date.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.timePicker.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/timePicker.css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.datePicker.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/datePicker.css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.tooltip.min.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/jquery.tooltip.css" />
        <script type="text/javascript" src="http://www.google.com/jsapi?key=${googleKey}"></script>
        <script type="text/javascript">
            <c:if test="${!empty pageContext.request.remoteUser}"><%-- User is logged in --%>
            
            </c:if>
        </script>
    </head>
    <body>
        <div id="header">
            <div id="breadcrumb">
                <a href="http://test.ala.org.au">Home</a>
                <a href="http://test.ala.org.au/explore">Contribute</a>
                Your Sightings
            </div>
            <h1>Your Sightings</h1>
        </div>
        
        <c:choose>
            <c:when test="${!empty pageContext.request.remoteUser}"><%-- User is logged in --%>
                <c:if test="${not empty taxonConceptMap}">
                    <div id="column-one" class="section">
                        <c:forEach var="tc" items="${taxonConceptMap}">
                            <img src="${tc.imageThumbnailUrl}" alt="species image thumbnail" style="display: block; float: left; margin-right: 10px;"/>
                            <div style="padding: 5px;">
                                <a href="http://bie.ala.org.au/species/${tc.guid}"><alatag:formatSciName name="${tc.scientificName}" rankId="${tc.rankId}"/> (${tc.commonName})</a>
                                <br/>
                                Records: ${tc.count}
                            </div>
                            <div style="clear: both; height:5px;"></div>
                        </c:forEach>
                    </div>
                    <div id="column-two">
                        <div class="section">
                            <div id="mapCanvas"></div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="section">${error}</div>
                </c:if>
            </c:when>
            <c:otherwise><%-- User is NOT logged in --%>
                <jsp:include page="loginMsg.jsp"/>
            </c:otherwise>
        </c:choose>
    </body>
</html>