<%-- 
    Document   : show
    Created on : Apr 21, 2010, 9:36:39 AM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Occurrence Record: ${id}</title>
    </head>
    <body onload="initialize()">
        <h1>Occurrence Details: ${occurrence.institutionCode} ${occurrence.collectionCode} ${occurrence.catalogueNumber}</h1>
        <c:if test="${not empty occurrence.latitude && not empty occurrence.longitude}">
            <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
            <script type="text/javascript">
                $(document).ready(function() {
                    var latlng = new google.maps.LatLng(${occurrence.latitude}, ${occurrence.longitude});
                    var myOptions = {
                        zoom: 4,
                        center: latlng,
                        scaleControl: true,
                        mapTypeId: google.maps.MapTypeId.ROADMAP
                    };

                    var map = new google.maps.Map(document.getElementById("occurrenceMap"), myOptions);

                    var marker = new google.maps.Marker({
                        position: latlng,
                        map: map,
                        title:"Occurence Location"
                    });
                });
            </script>
            <div id="occurrenceMap"></div>
        </c:if>
        <div id="occurrenceDataset" class="occurrenceSection">
            <h2>Datset</h2>
            <table class="occurrenceTable">
                <alatag:occurrenceTableRow fieldName="Data Provider">${occurrence.dataProvider}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Data Set">${occurrence.dataResource}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Institution Code">${occurrence.institutionCode}
                    <c:if test="${not empty institutionCodeLsid && not empty institutionCodeName}">
                        (<a href="${pageContext.request.contextPath}/institutions/${occurrence.institutionCodeLsid}">${occurrence.institutionCodeName}</a>)
                    </c:if>
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Collection Code">${occurrence.collectionCode}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Catalogue Number">${occurrence.catalogueNumber}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Basis of Record">${occurrence.basisOfRecord}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Record Date"><fmt:formatDate value="${occurrence.occurrenceDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="${occurrence.identifierType}" fieldNameIsMsgCode="true">${occurrence.identifierValue}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Identifier">${occurrence.identifierName}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Identified Date"><fmt:formatDate value="${occurrence.identifierDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Collector">${occurrence.collector}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Type Status">${occurrence.typeStatus}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="taxonomic Issue"><c:if test="${occurrence.taxonomicIssue != 0}">${occurrence.taxonomicIssue}</c:if></alatag:occurrenceTableRow>
            </table>
        </div>
        <div id="occurrenceTaxonomy" class="occurrenceSection">
            <h2>Taxonomy</h2>
            <table class="occurrenceTable">
                <alatag:occurrenceTableRow fieldName="Scientific Name">
                    <c:choose>
                        <c:when test="${!(fn:containsIgnoreCase(occurrence.taxonName, occurrence.rawTaxonName) || fn:containsIgnoreCase(occurrence.author, occurrence.rawAuthor))}">
                            <alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.rawTaxonName}"/> ${occurrence.rawAuthor}
                            (interpreted as <a href="${pageContext.request.contextPath}/species/${occurrence.taxonConceptLsid}">
                                <alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/> ${occurrence.author}</a>)
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/species/${occurrence.taxonConceptLsid}"><alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/> ${occurrence.author}</a>
                        </c:otherwise>
                    </c:choose>
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Taxon Rank">
                    <span style="text-transform: capitalize;">${occurrence.rank}</span>
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Kingdom" link="${pageContext.request.contextPath}/species/${occurrence.kingdomLsid}">
                    ${occurrence.kingdom}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Phylum" link="${pageContext.request.contextPath}/species/${occurrence.phylumLsid}">
                    ${occurrence.phylum}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Class" link="${pageContext.request.contextPath}/species/${occurrence.classLsid}">
                    ${occurrence.clazz}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Order" link="${pageContext.request.contextPath}/species/${occurrence.orderLsid}">
                    ${occurrence.order}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Family" link="${pageContext.request.contextPath}/species/${occurrence.familyLsid}">
                    ${occurrence.family}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Genus" link="${pageContext.request.contextPath}/species/${occurrence.genusLsid}">
                    ${occurrence.genus}
                </alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Species" link="${pageContext.request.contextPath}/species/${occurrence.speciesLsid}">
                    ${occurrence.species}
                </alatag:occurrenceTableRow>
            </table>
        </div>
        <div id="occurrenceGeospatial" class="occurrenceSection">
            <h2>Geospatial</h2>
            <table class="occurrenceTable">
                <alatag:occurrenceTableRow fieldName="Country"><c:if test="${not empty occurrence.countryCode}"><fmt:message key="country.${occurrence.countryCode}"/></c:if></alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="State/Province">${occurrence.state}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Biogeographic Region">${occurrence.biogeographicRegion}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Place">${occurrence.place}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Latitude">${occurrence.latitude}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Longitude">${occurrence.longitude}</alatag:occurrenceTableRow>
                <alatag:occurrenceTableRow fieldName="Coordinate Precision">${occurrence.coordinatePrecision}</alatag:occurrenceTableRow>
            </table>
        </div>
        <div id="occurrenceMetadata">
            First indexed: <fmt:formatDate value="${occurrence.createdDate}" pattern="yyyy-MM-dd"/>
            <br/>
            Last indexed:  <fmt:formatDate value="${occurrence.modifiedDate}" pattern="yyyy-MM-dd"/>
        </div>
    </body>
</html>
