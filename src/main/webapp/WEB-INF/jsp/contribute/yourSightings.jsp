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
                google.load("maps", "3", {other_params:"sensor=false"});

                $(document).ready(function() {
                    var latlng = new google.maps.LatLng(${points[0].coordinates[1]}, ${points[1].coordinates[0]});
                    var myOptions = {
                        zoom: 15,
                        center: latlng,
                        scaleControl: true,
                        mapTypeControlOptions: {
                            mapTypeIds: [google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.HYBRID, google.maps.MapTypeId.TERRAIN ]
                        },
                        mapTypeId: google.maps.MapTypeId.ROADMAP
                    };

                    var map = new google.maps.Map(document.getElementById("mapCanvas"), myOptions);
                    
                    <c:forEach var="point" items="${points}" varStatus="status">
                        var latlng_${status.count} = new google.maps.LatLng(${point.coordinates[1]}, ${point.coordinates[0]});
                        var marker_${status.count} = new google.maps.Marker({
                            position: latlng_${status.count},
                            map: map,
                            title:"Occurrence ${point.occurrenceUid}"
                        });
                        var contentString_${status.count} = "<div>Record Id: ${point.occurrenceUid}</div>" +
                            "<div>Species LSID: ${point.taxonConceptGuid}</div>";
                        var infowindow_${status.count} = new google.maps.InfoWindow({
                            content: contentString_${status.count}
                        });
                        google.maps.event.addListener(marker_${status.count}, 'click', function() {
                            infowindow_${status.count}.open(map, marker_${status.count});
                        });
                    </c:forEach>
                    
                });
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
                    <div id="column-one">
                        <div class="section">
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
                    </div>
                    <div id="column-two">
                        <div class="section">
                            <div id="mapCanvas" style="height: 315px; width: 315px;"></div>
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