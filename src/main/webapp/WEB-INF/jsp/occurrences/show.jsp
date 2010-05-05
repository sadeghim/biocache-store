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
<%@ taglib tagdir="/WEB-INF/tags" prefix="ala" %>
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
                <ala:occurrenceTableRow fieldName="Data Provider">${occurrence.dataProvider}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Data Set">${occurrence.dataResource}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Institution Code">${occurrence.institutionCode}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Collection Code">${occurrence.collectionCode}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Catalogue Number">${occurrence.catalogueNumber}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Basis of Record">${occurrence.basisOfRecord}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="${occurrence.identifierType}" fieldNameIsMsgCode="true">${occurrence.identifierValue}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Date Collected">${occurrence.occurrenceDate}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Type Status">${occurrence.typeStatus}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="taxonomic Issue"><c:if test="${occurrence.taxonomicIssue != 0}">${occurrence.taxonomicIssue}</c:if></ala:occurrenceTableRow>
            </table>
        </div>
        <div id="occurrenceTaxonomy" class="occurrenceSection">
            <h2>Taxonomy</h2>
            <table class="occurrenceTable">
                <ala:occurrenceTableRow fieldName="Scientific Name">
                    <i>${occurrence.rawTaxonName}</i> ${occurrence.rawAuthor}
                    <c:if test="!fn:containsIgnoreCase(occurrence.taxonName, occurrence.rawTaxonName)">
                        (interpreted as <i>${occurrence.taxonName}</i> ${occurrence.author})
                    </c:if>
                </ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Kingdom">${occurrence.kingdom}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Family">${occurrence.family}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Genus">${occurrence.genus}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Species">${occurrence.species}</ala:occurrenceTableRow>
            </table>
        </div>
        <div id="occurrenceGeospatial" class="occurrenceSection">
            <h2>Geospatial</h2>
            <table class="occurrenceTable">
                <ala:occurrenceTableRow fieldName="Country">${occurrence.countryCode}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="State/Province">${occurrence.state}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Biogeographic Region">${occurrence.biogeographicRegion}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Places">${occurrence.place}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Latitude">${occurrence.latitude}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Longitude">${occurrence.longitude}</ala:occurrenceTableRow>
                <ala:occurrenceTableRow fieldName="Coordinate Precision">${occurrence.coordinatePrecision}</ala:occurrenceTableRow>
            </table>
        </div>

    </body>
</html>
