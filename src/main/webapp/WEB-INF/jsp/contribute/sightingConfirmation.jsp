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
        <title>Contribute a Sighting - Confirmation</title>
    </head>
    <body>
        <div id="header">
            <div id="breadcrumb">
                <a href="http://test.ala.org.au">Home</a>
                <a href="http://test.ala.org.au/explore">Contribute</a>
                Contribute a Sighting
            </div>
            <h1>Contribute a Sighting - Confirmation</h1>
        </div>
        
            <c:choose>
                <c:when test="${!empty pageContext.request.remoteUser}"><%-- User is logged in --%>
                    <c:if test="${not empty taxonConcept}">
                        <form name="sighting" id="sighting" action="" method="POST">
                            <div id="column-one">
                                <div style="float: left; padding-right: 15px" id="images" class="section">
                                    <img src="${taxonConcept.imageThumbnailUrl}" height="85px" alt="species thumbnail"/>
                                </div>
                                <div style="margin-left: 115px" class="section">
                                    <h2><a href="http://bie.ala.org.au/species/${param.guid}"><alatag:formatSciName name="${taxonConcept.scientificName}" rankId="${taxonConcept.rankId}"/>
                                        (${taxonConcept.commonName})</a>
                                        <input type="hidden" name="guid" id="sightingGuid" value="${param.guid}"/>
                                    </h2>
                                    <fieldset id="sightingInfo">
                                        <p><label for="date">Date</label>
                                            <span>${param.date}</span>
                                            <input type="hidden" name="date" value="${param.date}"/>
                                        </p>
                                        <p><label for="time">Time</label>
                                            <span>${param.time}</span>
                                            <input type="hidden" name="time" value="${param.time}"/>
                                        </p>
                                        <p><label for="number">Number observed</label>
                                            <span>${param.number}</span>
                                            <input type="hidden" name="number" value="${param.number}"/>
                                        </p>
                                        <p><label for="location">Location</label>
                                            <span>${param.location}</span>
                                            <input type="hidden" name="location" value="${param.location}"/>
                                        </p>
                                            <p><label for="latitude">Latitude</label>
                                            <span>${param.latitude}</span>
                                            <input type="hidden" name="latitude" value="${param.latitude}"/>
                                        </p>
                                        <p><label for="longitude">Longitude</label>
                                            <span>${param.longitude}</span>
                                            <input type="hidden" name="longitude" value="${param.longitude}"/>
                                        </p>
                                        <p><label for="coordinateUncertainty">Coordinate Uncertainty</label>
                                            <span>${param.coordinateUncertainty}</span>
                                            <input type="hidden" name="coordinateUncertainty" value="${param.coordinateUncertainty}"/>
                                        </p>
                                        <p><label for="notes">Notes</label>
                                            <span>${param.notes}</span>
                                            <input type="hidden" name="notes" value="${param.notes}"/>
                                        </p>
                                        <p class="red">Are these values correct? Click "back" to go back and edit or "finish" to confirm.</p>
                                        <p><label for=""><input type="submit" name="action" id="sightingBack" value="< Back"/></label>
                                            <input type="submit" name="action" id="sightingSubmit" value="Finish"/>
                                        </p>
                                    </fieldset>
                                </div>
                            </div>
                            
                        </form>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="section">${error}</div>
                    </c:if>
                </c:when>
                <c:otherwise><%-- User is NOT logged in --%>
                    <c:set var="queryString" value="${pageContext.request.queryString}"/>
                    <c:choose>
                        <c:when test="${empty queryString}">
                            <c:set var="requestUrl" value="${pageContext.request.requestURL}"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="requestUrl" value="${pageContext.request.requestURL}?${fn:replace(queryString, '+', '%2B')}"/>
                        </c:otherwise>
                    </c:choose>
                    <div style="border-top: 1px solid #DDD; margin-top: 10px">&nbsp;</div>
                    <div>You are not logged in. <ala:loginLogoutLink returnUrlPath="${requestUrl}"/></div>
                </c:otherwise>
            </c:choose>
    </body>
</html>