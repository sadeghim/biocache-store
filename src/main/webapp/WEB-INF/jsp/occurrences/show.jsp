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
        <title>Occurrence Record: ${id} | Atlas of Living Australia</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/annotation.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.form.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.url.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.cookie.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.simplemodal.js"></script>
        <link type="text/css" rel="stylesheet" href="${initParam.centralServer}/wp-content/themes/ala/css/basic.css" charset="utf-8">
        <link type="text/css" rel="stylesheet" href="${initParam.centralServer}/wp-content/themes/ala/css/occurrenceSpecial.css" charset="utf-8">
         <script>
            // Set some global variables to be used in imported JS files
            occurrenceHtmlUrl = "http://${pageContext.request.serverName}${pageContext.request.contextPath}/occurrences/${occurrence.id}";
            occurrenceJsonUrl = "${pageContext.request.contextPath}/occurrences/${occurrence.id}.json";
            annotationJsonUrl = "${pageContext.request.contextPath}/annotation/retrieveAllAnnotationsForOccurrenceRecord?url="+occurrenceHtmlUrl;
            annotationReplyJsonUrl = "${pageContext.request.contextPath}/annotation/retrieveReplyAnnotationsForAnnotation";
            //saveAnnotationUrl = "${pageContext.request.contextPath}/annotation/saveAnnotation";
            //getAnnotationsUrl =  "<gbif:propertyLoader bundle='portal' property='hostName'/>/danno/annotea/";
        </script>
    </head>
    <body>
        <div id="header">
            <div id="breadcrumb">
                <a href="${initParam.centralServer}">Home</a>
                <a href="${initParam.centralServer}/explore">Explore</a>
                Occurrence Record - ${occurrence.id}
            </div>
            <h1>Occurrence Details: ${occurrence.id}</h1>
        </div>
        <div id="column-one" class="full-width">
            <div class="section">
                <c:if test="${not empty occurrence.latitude && not empty occurrence.longitude}">
                    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
                    <script type="text/javascript">
                        $(document).ready(function() {
                            var latlng = new google.maps.LatLng(${occurrence.latitude}, ${occurrence.longitude});
                            var myOptions = {
                                zoom: 4,
                                center: latlng,
                                scaleControl: true,
                                mapTypeControlOptions: {
                                    mapTypeIds: [google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.HYBRID, google.maps.MapTypeId.TERRAIN ]
                                },
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
                <c:if test="${not empty collectionLogo}">
                <div id="logoRecord">
                	<img src="${collectionLogo}" />
                </div>
                </c:if>                
				<c:if test="${not empty images}">
                <div id="imageRecords">
                   	<c:forEach items="${images}" var="imageRecord">
                   		<div class="imageRecord">
                   		<c:choose>
                   			<c:when test="${not empty imageRecord.htmlForDisplay}">
                   				<c:if test="${fn:startsWith(imageRecord.htmlForDisplay, 'http://')}">
                   					<a href="${imageRecord.url}"><img src="${imageRecord.htmlForDisplay}"/></a>
                   					<p>
                   						${imageRecord.description}
                   					</p>
                   				</c:if>	
                   			</c:when>
                   			<c:otherwise>
		             			<c:forEach var="url" items="${fn:split(imageRecord.url, ',')}">
		             				<c:set var="url" value="${fn:trim(url)}"/>
	                   				<a href="${url}"><img src="${url}"/></a>
	                   			</c:forEach>	
                   			</c:otherwise>
                   		</c:choose>
                   		</div>
                    </c:forEach>
                </div>
				</c:if>
                <c:if test="${not empty occurrence}">
                    <c:set var="bieWebappContext" scope="request"><ala:propertyLoader bundle="biocache" property="bieWebappContext"/></c:set>
                    <c:set var="collectionsWebappContext" scope="request"><ala:propertyLoader bundle="biocache" property="collectionsWebappContext"/></c:set>                    
                    <!--
                    <h1>Occurrence Details: ${occurrence.institutionCode} ${occurrence.collectionCode} ${occurrence.catalogueNumber}</h1>
                    -->
<!--                    <h1>Occurrence Details: ${occurrence.id}</h1>-->
                    <div id="debug"></div>
                    <!-- add the occurrence issue descriptions if necessary -->
                    <c:set var="noIssues" value="${!((empty occurrence.taxonomicIssue || occurrence.taxonomicIssue==0)  && (empty occurrence.geospatialIssue || occurrence.geospatialIssue==0) && (empty occurrence.otherIssue || occurrence.otherIssue==0))}"/>
                    <c:if test="${noIssues}">
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
                            <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="dataProvider" fieldName="Data Provider">
                            	<c:choose>
									<c:when test="${occurrence.dataProviderUid != null && not empty occurrence.dataProviderUid}">
		                            	<a href="${collectionsWebappContext}/public/show/${occurrence.dataProviderUid}">
		                            		${occurrence.dataProvider}
		                            	</a>
		                            </c:when>
		                            <c:otherwise>
		                            	${occurrence.dataProvider}
		                            </c:otherwise>
	                            </c:choose>
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="dataResource" fieldName="Data Set">
                            	<c:choose>
	                            	<c:when test="${occurrence.dataResourceUid != null && not empty occurrence.dataResourceUid}">
		                            	<a href="${collectionsWebappContext}/public/show/${occurrence.dataResourceUid}">
		                            		${occurrence.dataResource}
		                            	</a>
	                            	</c:when>
	                            	<c:otherwise>
	                            		${occurrence.dataResource}
	                            	</c:otherwise>
								</c:choose>                            	
                            </alatag:occurrenceTableRow>
                            
                           	<c:choose>
                           		<c:when test="${occurrence.institutionCodeUid != null && not empty occurrence.institutionCodeUid}">
	                           		<alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="institutionCode" fieldName="Institution">
		                            	<a href="${collectionsWebappContext}/public/show/${occurrence.institutionCodeUid}">
		                            		${collectionInstitution}(Institution Code: ${occurrence.institutionCode})
		                            	</a>
	                            	</alatag:occurrenceTableRow>
                            	</c:when>                            	
								<c:otherwise>
									<alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="institutionCode" fieldName="Institution Code">
	                            		${occurrence.institutionCode}
	                            	</alatag:occurrenceTableRow>
                            	</c:otherwise>  
								<%--
	                           	${occurrence.institutionCode}
	                               <c:if test="${not empty institutionCodeLsid && not empty institutionCodeName}">
	                                   (<a href="${pageContext.request.contextPath}/institutions/${occurrence.institutionCodeLsid}">${occurrence.institutionCodeName}</a>)
	                               </c:if>
	                            --%>                            	                          	
                           	</c:choose>                           
                           	 <c:choose>
                            	 <c:when test="${occurrence.collectionCodeUid != null && not empty occurrence.collectionCodeUid}">
                            	 	<alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="collectionCode" fieldName="Collection">
		                            	<a href="${collectionsWebappContext}/public/show/${occurrence.collectionCodeUid}">
		                            		${collectionName} (Collection Code: ${occurrence.collectionCode})
		                            	</a>
	                            	</alatag:occurrenceTableRow>
                            	</c:when>
								<c:otherwise>
									<alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="collectionCode" fieldName="Collection Code">
	                            		${occurrence.collectionCode}
                            		</alatag:occurrenceTableRow>
                            	</c:otherwise>
                           	</c:choose>                            	                            
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="catalogueNumber" fieldName="Catalogue Number">${occurrence.catalogueNumber}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="basisOfRecord" fieldName="Basis of Record">${occurrence.basisOfRecord}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="occurrenceDate" fieldName="Record Date"><fmt:formatDate value="${occurrence.occurrenceDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="${occurrence.identifierType}" fieldName="${occurrence.identifierType}" fieldNameIsMsgCode="true">${occurrence.identifierValue}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="identifierName" fieldName="Identifier">${occurrence.identifierName}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="identifierDate" fieldName="Identified Date"><fmt:formatDate value="${occurrence.identifierDate}" pattern="yyyy-MM-dd"/></alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="collectorName" fieldName="Collector/observer">${occurrence.collector}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="dataset" fieldCode="typeStatus" fieldName="Type Status">${occurrence.typeStatus}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="dataset" fieldCode="taxonomicIssue" fieldName="taxonomic Issue"><c:if test="${occurrence.taxonomicIssue != 0}">${occurrence.taxonomicIssue}</c:if></alatag:occurrenceTableRow>
                        </table>
                    </div>
                    <div id="occurrenceTaxonomy" class="occurrenceSection">
                        <h2>Taxonomy</h2>
                        <table class="occurrenceTable"  id="taxonomyTable">
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="scientificName" fieldName="Scientific Name">
                                <c:choose>
                                    <c:when test="${!(fn:containsIgnoreCase(occurrence.taxonName, occurrence.rawTaxonName) || fn:containsIgnoreCase(occurrence.author, occurrence.rawAuthor))}">
                                        <alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.rawTaxonName}"/> ${occurrence.rawAuthor}
                                        (interpreted as <a href="${bieWebappContext}/species/${occurrence.taxonConceptLsid}">
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
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="taxonRank" fieldName="Taxon Rank">
                                <span style="text-transform: capitalize;">${occurrence.rank}</span>
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="taxonomy" fieldCode="commonName" fieldName="Common Name">
                                 ${occurrence.commonName}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="kingdom" fieldName="Kingdom" path="${bieWebappContext}/species/" guid="${occurrence.kingdomLsid}">
                                ${occurrence.kingdom}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="phylum" fieldName="Phylum" path="${bieWebappContext}/species/"  guid="${occurrence.phylumLsid}">
                                ${occurrence.phylum}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="class" fieldName="Class" path="${bieWebappContext}/species/"  guid="${occurrence.classLsid}">
                                ${occurrence.clazz}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="order" fieldName="Order" path="${bieWebappContext}/species/"  guid="${occurrence.orderLsid}">
                                ${occurrence.order}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="family" fieldName="Family" path="${bieWebappContext}/species/"  guid="${occurrence.familyLsid}">
                                ${occurrence.family}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="genus" fieldName="Genus" path="${bieWebappContext}/species/"  guid="${occurrence.genusLsid}">
                                ${occurrence.genus}
                            </alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="taxonomy" fieldCode="species" fieldName="Species" path="${bieWebappContext}/species/"  guid="${occurrence.speciesLsid}">
                                ${occurrence.species}
                            </alatag:occurrenceTableRow>
                        </table>
                    </div>
                    <div id="occurrenceGeospatial" class="occurrenceSection">
                        <h2>Geospatial</h2>
                        <table class="occurrenceTable"  id="geospatialTable">
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="country" fieldName="Country"><c:if test="${not empty occurrence.countryCode}"><fmt:message key="country.${occurrence.countryCode}"/></c:if></alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="state" fieldName="State/Province">${occurrence.state}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="biogeographicRegion" fieldName="Biogeographic Region">${occurrence.biogeographicRegion}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="place" fieldName="Place">${occurrence.place}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="locality" fieldName="Locality">${rawOccurrence.locality}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="latitude" fieldName="Latitude">${rawOccurrence.latitude}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="true" section="geospatial" fieldCode="longitude" fieldName="Longitude">${rawOccurrence.longitude}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="coordinatePrecision" fieldName="Coordinate Precision (metres)">
                            	<c:if test="${not empty occurrence.latitude || not empty occurrence.longitude}">
                            		${not empty occurrence.coordinatePrecision ? occurrence.coordinatePrecision : 'Unknown'}
                            	</c:if>
                            </alatag:occurrenceTableRow>
                            <c:if test="${not empty rawOccurrence.generalisedInMetres}">
                            	<alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="generalisedInMetres" fieldName="Coordinates generalised">
                            		Due to sensitivity concerns, the coordinates of this record have been generalised to ${rawOccurrence.generalisedInMetres} metres.
                            	</alatag:occurrenceTableRow>
                            </c:if>
                            <alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="geodeticDatum" fieldName="Geodetic datum">${rawOccurrence.geodeticDatum}</alatag:occurrenceTableRow>
                            <alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="occurrenceRemarks" fieldName="Notes">${rawOccurrence.occurrenceRemarks}</alatag:occurrenceTableRow>
                            <c:if test="${not empty rawOccurrence.individualCount && rawOccurrence.individualCount>0}">
                            	<alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="individualCount" fieldName="Individual count">${rawOccurrence.individualCount}</alatag:occurrenceTableRow>
                            </c:if>
                            <alatag:occurrenceTableRow annotate="false" section="geospatial" fieldCode="citation" fieldName="Citation">${rawOccurrence.citation}</alatag:occurrenceTableRow>

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
            </div>
        </div>
    </body>
</html>
