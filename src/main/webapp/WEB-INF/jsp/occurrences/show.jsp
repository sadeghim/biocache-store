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
        <meta name="geo.position" content="${occurrence.latitude};${occurrence.longitude}"/>
        <title>Occurrence Record: ${id}</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/annotation.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.form.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.url.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.cookie.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.simplemodal.js"></script>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/basic.css" charset="utf-8">
        <script>
            // Set some global variables to be used in imported JS files
            occurrenceHtmlUrl = "http://${pageContext.request.serverName}${pageContext.request.contextPath}/occurrences/${occurrence.id}";
            occurrenceJsonUrl = "${pageContext.request.contextPath}/occurrences/${occurrence.id}.json";
            annotationJsonUrl = "${pageContext.request.contextPath}/annotation/retrieveAllAnnotationsForOccurrenceRecord?url="+occurrenceHtmlUrl;
            annotationReplyJsonUrl = "${pageContext.request.contextPath}/annotation/retrieveReplyAnnotationsForAnnotation";
            //saveAnnotationUrl = "${pageContext.request.contextPath}/annotation/saveAnnotation";
            //getAnnotationsUrl =  "<gbif:propertyLoader bundle='portal' property='hostName'/>/danno/annotea/";
        </script>
         <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/occurrenceSpecial.css" charset="utf-8">
    </head>
    <body>
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
                        title:"Occurrence Location"
                    });
                });
            </script>
            <div id="occurrenceMap"></div>
        </c:if>
        <c:if test="${not empty occurrence}">
            <c:set var="bieWebappContext" scope="request"><ala:propertyLoader bundle="biocache" property="bieWebappContext"/></c:set>
            <!--
            <h1>Occurrence Details: ${occurrence.institutionCode} ${occurrence.collectionCode} ${occurrence.catalogueNumber}</h1>
            -->
            <h1>Occurrence Details: ${occurrence.id}</h1>
            <div id="debug"></div>
            <!-- add the occurrence issue descriptions if necessary -->
            <c:set var="noIssues" value="${occurrence.taxonomicIssue==0 && occurrence.geospatialIssue==0 && occurrence.otherIssue==0}"/>
                <c:if test="${!noIssues}">
                    <div id="warnings" class="topContainer">
                        <h4>Warnings</h4>
                        <alatag:formatGeospatialIssues issuesBit="${occurrence.geospatialIssue}"/>
                        <c:if test="${not empty geospatialIssueText}">
				Geospatial issues: ${geospatialIssueText}<br/>
			</c:if>
                        <alatag:formatOtherIssues issuesBit="${occurrence.otherIssue}" />
                        <c:if test="${not empty otherIssueText}">
                            Miscellaneous issues: ${otherIssueText}<br/>
                        </c:if>
			<alatag:formatTaxonomicIssues issuesBit="${occurrence.taxonomicIssue}"/>
			<c:if test="${not empty taxonomicIssueText}">
                            Taxonomic issues: ${taxonomicIssueText}<br/>
			</c:if>
                    </div>
                </c:if>
            <div id="occurrenceDataset" class="occurrenceSection">
                <h2>Dataset</h2>
                <table class="occurrenceTable" id="datasetTable">
                    <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="dataProvider" fieldName="Data Provider">${occurrence.dataProvider}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="dataResource" fieldName="Data Set">${occurrence.dataResource}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="institutionCode" fieldName="Institution Code">${occurrence.institutionCode}
                        <c:if test="${not empty institutionCodeLsid && not empty institutionCodeName}">
                            (<a href="${pageContext.request.contextPath}/institutions/${occurrence.institutionCodeLsid}">${occurrence.institutionCodeName}</a>)
                        </c:if>
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="collectionCode" fieldName="Collection Code">${occurrence.collectionCode}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="catalogueNumber" fieldName="Catalogue Number">${occurrence.catalogueNumber}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="basisOfRecord" fieldName="Basis of Record">${occurrence.basisOfRecord}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="occurrenceDate" fieldName="Record Date"><fmt:formatDate value="${occurrence.occurrenceDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="${occurrence.identifierType}" fieldName="${occurrence.identifierType}" fieldNameIsMsgCode="true">${occurrence.identifierValue}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="identifierName" fieldName="Identifier">${occurrence.identifierName}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="identifierDate" fieldName="Identified Date"><fmt:formatDate value="${occurrence.identifierDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="collector" fieldName="Collector">${occurrence.collector}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="typeStatus" fieldName="Type Status">${occurrence.typeStatus}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="taxonomicIssue" fieldName="taxonomic Issue"><c:if test="${occurrence.taxonomicIssue != 0}">${occurrence.taxonomicIssue}</c:if></alatag:occurrenceTableRow>
                </table>
            </div>
            <div id="occurrenceTaxonomy" class="occurrenceSection">
                <h2>Taxonomy</h2>
                <table class="occurrenceTable"  id="taxonomyTable">
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Scientific Name">
                        <c:choose>
                            <c:when test="${!(fn:containsIgnoreCase(occurrence.taxonName, occurrence.rawTaxonName) || fn:containsIgnoreCase(occurrence.author, occurrence.rawAuthor))}">
                                <alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.rawTaxonName}"/> ${occurrence.rawAuthor}
                                (interpreted as <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/${bieWebappContext}/species/${occurrence.taxonConceptLsid}">
                                    <alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/> ${occurrence.author}</a>)
                            </c:when>
                            <c:otherwise>
                            	<c:if test="${not empty occurrence.taxonConceptLsid}">
                                  <a href="${bieWebappContext}/species/${occurrence.taxonConceptLsid}">
                                </c:if>
                                	<alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/> 
                                	${occurrence.author}
                               	<c:if test="${not empty occurrence.taxonConceptLsid}">
                               	  </a>
                               	</c:if>
                            </c:otherwise>
                        </c:choose>
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Taxon Rank">
                        <span style="text-transform: capitalize;">${occurrence.rank}</span>
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Common Name">
                         ${occurrence.commonName}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Kingdom" path="${bieWebappContext}/species/" guid="${occurrence.kingdomLsid}">
                        ${occurrence.kingdom}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Phylum" path="${bieWebappContext}/species/"  guid="${occurrence.phylumLsid}">
                        ${occurrence.phylum}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Class" path="${bieWebappContext}/species/"  guid="${occurrence.classLsid}">
                        ${occurrence.clazz}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Order" path="${bieWebappContext}/species/"  guid="${occurrence.orderLsid}">
                        ${occurrence.order}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Family" path="${bieWebappContext}/species/"  guid="${occurrence.familyLsid}">
                        ${occurrence.family}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Genus" path="${bieWebappContext}/species/"  guid="${occurrence.genusLsid}">
                        ${occurrence.genus}
                    </alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="" fieldName="Species" path="${bieWebappContext}/species/"  guid="${occurrence.speciesLsid}">
                        ${occurrence.species}
                    </alatag:occurrenceTableRow>
                </table>
            </div>
            <div id="occurrenceGeospatial" class="occurrenceSection">
                <h2>Geospatial</h2>
                <table class="occurrenceTable"  id="geospatialTable">
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Country"><c:if test="${not empty occurrence.countryCode}"><fmt:message key="country.${occurrence.countryCode}"/></c:if></alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="State/Province">${occurrence.state}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Biogeographic Region">${occurrence.biogeographicRegion}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Place">${occurrence.place}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Latitude">${occurrence.latitude}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Longitude">${occurrence.longitude}</alatag:occurrenceTableRow>
                    <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="" fieldName="Coordinate Precision">${occurrence.coordinatePrecision}</alatag:occurrenceTableRow>
                </table>
            </div>
            <div id="occurrenceMetadata">
                First indexed: <fmt:formatDate value="${occurrence.createdDate}" pattern="yyyy-MM-dd"/>
                <br/>
                Last indexed:  <fmt:formatDate value="${occurrence.modifiedDate}" pattern="yyyy-MM-dd"/>
            </div>

            <jsp:include page="annotateDataset.jsp"/>
            <jsp:include page="annotateTaxonomy.jsp"/>
            <jsp:include page="annotateGeospatial.jsp"/>
            <jsp:include page="annotateReply.jsp"/>
        </c:if>
        <c:if test="${empty occurrence}">
            <p>The requested record was not found</p>
        </c:if>
    </body>
</html>
