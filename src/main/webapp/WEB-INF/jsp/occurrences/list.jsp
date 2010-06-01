<%--
    Document   : list
    Created on : Apr 21, 2010, 9:36:39 AM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="pageName" content="species"/>
        <title>Occurrence Search Results</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.query.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-ui-1.8.custom.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.simplemodal.js"></script>
        <script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js"></script>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bie-theme/jquery-ui-1.8.custom.css" charset="utf-8">
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/basic.css" charset="utf-8">
        <!--[if lt IE 7]>
        <link type='text/css' href='${pageContext.request.contextPath}/static/css/basic_ie.css' rel='stylesheet' media='screen' />
        <![endif]-->
        <script type="text/javascript">
            /* Openlayers vars */
            var lon = 133;
            var lat = -27;
            var zoom = 4;
            var map, layer, myStyles, style;

            /* Openlayers map */
            function loadMap() {
                map = new OpenLayers.Map('pointsMap');
                layer = new OpenLayers.Layer.WMS( "OpenLayers WMS",
                        "http://labs.metacarta.com/wms/vmap0",
                        {layers: 'basic'} );
                map.addLayer(layer);
                map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);

                myStyles = new OpenLayers.StyleMap({
                    "default": new OpenLayers.Style({
                        pointRadius: 5, //"${'${count}'}", // sized according to count attribute
                        fillColor: "${'${color}'}",//"#ffcc66",
                        strokeColor: "${'${color}'}",
                        fillOpacity: 0.8,
                        strokeWidth: 1,
                        graphicZIndex: 1
                    })
                });

                style = new OpenLayers.Style();

                var ruleLow = new OpenLayers.Rule({
                  filter: new OpenLayers.Filter.Comparison({
                      type: OpenLayers.Filter.Comparison.LESS_THAN,
                      property: "count",
                      value: 10
                  }),
                  symbolizer: {pointRadius: 5, fillColor: "#ffcc66",
                               fillOpacity: 0.5, strokeColor: "#ff99#33"}
                });

                var ruleHigh = new OpenLayers.Rule({
                  filter: new OpenLayers.Filter.Comparison({
                      type: OpenLayers.Filter.Comparison.GREATER_THAN_OR_EQUAL_TO,
                      property: "count",
                      value: 10
                  }),
                  symbolizer: {pointRadius: 5, fillColor: "blue",
                               fillOpacity: 0.4, strokeColor: "blue"}
                });

                var elseRule = new OpenLayers.Rule({
                    elseFilter: true,
                    symbolizer: {strokeColor: "#0000FF" }
                });

                style.addRules([ruleLow, ruleHigh, elseRule]);

                var fqs = "&<c:if test="${not empty facetQuery}">fq=${fn:join(facetQuery, '&fq=')}</c:if>";

                $.getJSON("http://localhost:8888/biocache-webapp/occurrences/json/points.geojson?q=${query}"+fqs+"&zoom=4&callback=?", function(data){
                    //alert("getJson success... "+data.type);
                    var geojson_format = new OpenLayers.Format.GeoJSON();
                    var vector_layer = new OpenLayers.Layer.Vector("GeoJSON",{ styleMap: myStyles }); // new OpenLayers.StyleMap( { "default": style })
                    vector_layer.addFeatures(geojson_format.read(data));
                    map.addLayer(vector_layer);
                });
            }

            function destroyMap() {
                if (map != null) {
                    //alert("destroying map");
                    map.destroy();
                    $("#pointsMap").html('');
                }
            }

            $(document).ready(function() {
                var facetLinksSize = $("ul#subnavlist li").size();
                
                if (facetLinksSize == 0) {
                    // Hide an empty facet link list
                    $("#facetBar > h4").hide();
                    $("#facetBar #navlist").hide();
                }
                /* Accordion widget */
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

                $("select#sort").change(function() {
                    var val = $("option:selected", this).val();
                    reloadWithParam('sort',val);
                });
                $("select#dir").change(function() {
                    var val = $("option:selected", this).val();
                    reloadWithParam('dir',val);
                });

                $("#searchHeader > button").button();
                $("#searchHeader > button#download").click(function() {
                    var downloadUrl = "${pageContext.request.contextPath}/occurrences/download?q=${query}&fq=${fn:join(facetQuery, '&fq=')}";
                    //alert("URL is "+downloadUrl);
                    if (confirm("Continue with download?\rClick 'OK' to download or 'cancel' to abort.")) {
                        window.location.replace(downloadUrl);
                    }
                });

                $('button#showMap').click(function (e) {
                    $("#pointsMap").show();
                    loadMap();
                    $('#pointsMap').modal();
                    
                });

           });

            /**
             * Catch sort drop-down and build GET URL manually
             */
            function reloadWithParam(paramName, paramValue) {
                var paramList = [];
                var q = $.getQueryParam('q'); //$.query.get('q')[0];
                var fqList = $.getQueryParam('fq'); //$.query.get('fq');
                var sort = $.getQueryParam('sort');
                var dir = $.getQueryParam('dir');
                // add query param
                if (q != null) {
                    paramList.push("q=" + q);
                }
                // add filter query param
                if (fqList != null) {
                    paramList.push("fq=" + fqList.join("&fq="));
                }
                // add sort param if already set
                if (paramName != 'sort' && sort != null) {
                    paramList.push('sort' + "=" + sort);
                }
                // add dir param if already set
//                if (paramName != 'dir' && dir != null) {
//                    paramList.push('dir' + "=" + dir);
//                }
                //alert("sort = "+sort+" | paramName = "+paramName);
                // add the triggered param
                if (paramName != null && paramValue != null) {
                    if (paramName == 'sort') {
                        paramList.push(paramName + "=" +paramValue);
                    } else if (paramName == 'dir' && !(sort == null || sort == 'score')) {
                        paramList.push(paramName + "=" +paramValue);
                    }
                }
                
                //alert("params = "+paramList.join("&"));
                //alert("url = "+window.location.pathname);
                window.location.replace(window.location.pathname + '?' + paramList.join('&'));
            }

            function removeFacet(facet) {
                var q = $.getQueryParam('q'); //$.query.get('q')[0];
                var fqList = $.getQueryParam('fq'); //$.query.get('fq');
                var paramList = [];
                if (q != null) {
                    paramList.push("q=" + q);
                }
                //alert("this.facet = "+facet+"; fqList = "+fqList.join('|'));
                
                if (fqList instanceof Array) {
                    //alert("fqList is an array");
                    for (var i in fqList) {
                        //alert("i == "+i+"| fq = "+fqList[i]);
                        if (decodeURI(fqList[i]) == facet) {
                            //alert("removing fq: "+fqList[i]);
                            fqList.splice(fqList.indexOf(fqList[i]),1);
                        }
                    }
                } else {
                    //alert("fqList is NOT an array");
                    if (decodeURI(fqList) == facet) {
                        fqList = null;
                    }
                }
                //alert("(post) fqList = "+fqList.join('|'));
                if (fqList != null) {
                    paramList.push("fq=" + fqList.join("&fq="));
                }
                
                window.location.replace(window.location.pathname + '?' + paramList.join('&'));
            }

            // jQuery getQueryParam Plugin 1.0.0 (20100429)
            // By John Terenzio | http://plugins.jquery.com/project/getqueryparam | MIT License
            // Adapted by Nick dos Remedios to handle multiple params with same name - return a list
            (function ($) {
                // jQuery method, this will work like PHP's $_GET[]
                $.getQueryParam = function (param) {
                    // get the pairs of params fist
                    var pairs = location.search.substring(1).split('&');
                    var values = [];
                    // now iterate each pair
                    for (var i = 0; i < pairs.length; i++) {
                        var params = pairs[i].split('=');
                        if (params[0] == param) {
                            // if the param doesn't have a value, like ?photos&videos, then return an empty srting
                            //return params[1] || '';
                            values.push(params[1]);
                        }
                    }
                    
                    if (values.length > 0) {
                        return values;
                    } else {
                        //otherwise return undefined to signify that the param does not exist
                        return undefined;
                    }
                    
                };
            })(jQuery);

            function changeSort(el) {
                //var params = document.location.search.substr(1);
                //var fqList = $.getQueryParam("fq");
                //alert("fq param = "+fqList.join("|"));
                //alert("debug: "+$(this).val());
                var fqList = $.query.get('fq');
                $("#searchForm").submit();
                window.location.replace(url);
            }
        </script>
    </head>
    <body>
        <c:if test="${not empty searchResult && searchResult.totalRecords > 0}">
            <fmt:formatNumber value="${searchResult.totalRecords}" pattern="#,###,###" var="totalHits"/>
            <div id="searchHeader" >
                <button id="download" title="Download all ${totalHits} results as XLS (tab-delimited) file">Download</button>
                <button id="showMap" title="Display a small map showing points for records">View as Map</button>
                <h1>Occurrence Search Results</h1>
            </div>
            <div id="searchResults">
                <fmt:formatNumber var="currentPage" value="${(searchResult.startIndex / searchResult.pageSize) + 1}" pattern="0"/>
                <div id="searchTerms">
                    <div class="queryTermBox">
                        Search: <a href="?q=${queryJsEscaped}">${queryJsEscaped}</a><a name="searchResults">&nbsp;</a>
                    </div>
                    <c:forEach var="filter" items="${paramValues.fq}">
                        <c:set var="fqField" value="${fn:substringBefore(filter, ':')}"/>
                        <c:set var="fqValue" value="${fn:substringAfter(filter, ':')}"/>
                        <c:if test="${not empty fqValue}">
                            <div class="facetTermBox">
                                <!-- <b class="facetTermDivider ui-icon ui-icon-triangle-1-e">&nbsp;</b>-->
                                <span class="facetFieldName"><fmt:message key="facet.${fqField}"/>:</span>
                                ${fn:replace(fqValue,'-01-01T12:00:00Z','')} <a href="#" onClick="removeFacet('${filter}'); return false;" class="facetCloseLink ui-icon ui-icon-closethick" title="Remove this restriction">&nbsp;</a>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <div class="solrResults">
                    <div id="sortWidget">
                        <div id="resultsStats">
                            Page ${currentPage} of <fmt:formatNumber value="${lastPage}" pattern="#,###,###"/> (<fmt:formatNumber value="${searchResult.totalRecords}" pattern="#,###,###"/> results)
                        </div>
                        sort by
                        <select id="sort" name="sort">
                            <option value="score" <c:if test="${param.sort eq 'score'}">selected</c:if>>best match</option>
                            <option value="taxon_name" <c:if test="${param.sort eq 'taxon_name'}">selected</c:if>>scientific name</option>
                            <!--                            <option value="rank">rank</option>-->
                            <option value="occurrence_date" <c:if test="${param.sort eq 'occurrence_date'}">selected</c:if>>record date</option>
                            <option value="record_type" <c:if test="${param.sort eq 'record_type'}">selected</c:if>>record type</option>
                        </select>
                        sort order
                        <select id="dir" name="dir">
                            <option value="asc" <c:if test="${param.dir eq 'asc'}">selected</c:if>>normal</option>
                            <option value="desc" <c:if test="${param.dir eq 'desc'}">selected</c:if>>reverse</option>
                        </select>
                    </div>
                    <table>
                        <thead>
                            <tr>
                                <th>Scientific Name</th>
                                <th>Dataset</th>
                                <th>Record Type</th>
                                <th>Record Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="occurrence" items="${searchResult.occurrences}">
                                <tr>
                                    <td id="col1"><a href="${occurrence.id}" class="occurrenceLink"><alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/></a></td>
                                    <td id="col2">${occurrence.dataResource}</td>
                                    <td id="col3">${occurrence.basisOfRecord}</td>
                                    <td id="col4"><fmt:formatDate value="${occurrence.occurrenceDate}" pattern="yyyy-MM-dd"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div id="searchNavBar">
                        <alatag:searchNavigationLinks totalRecords="${searchResult.totalRecords}" startIndex="${searchResult.startIndex}"
                             lastPage="${lastPage}" pageSize="${searchResult.pageSize}"/>
                    </div>
                </div>
            </div>
            <div id="facets">
                <h4>Refine search:</h4>
                <div id="accordion">
                    <c:if test="${not empty query}">
                        <c:set var="queryParam">q=<c:out value="${query}" escapeXml="true"/><c:if test="${not empty param.fq}">&fq=${fn:join(paramValues.fq, "&fq=")}</c:if></c:set>
                    </c:if>
                    <c:forEach var="facetResult" items="${searchResult.facetResults}">
                        <c:if test="${!fn:containsIgnoreCase(facetQuery, facetResult.fieldResult[0].label)}">
                            <h3><a href="#"><span class="FieldName"><fmt:message key="facet.${facetResult.fieldName}"/></span></a></h3>
                            <div id="subnavlist">
                                <ul>
                                    <c:set var="lastElement" value="${facetResult.fieldResult[fn:length(facetResult.fieldResult)-1]}"/>
                                    <c:if test="${lastElement.label eq 'before'}">
                                        <li><c:set var="firstYear" value="${fn:substring(facetResult.fieldResult[0].label, 0, 4)}"/>
                                            <a href="?${queryParam}&fq=${facetResult.fieldName}:[* TO ${facetResult.fieldResult[0].label}]">Before ${firstYear}</a>
                                            (<fmt:formatNumber value="${lastElement.count}" pattern="#,###,###"/>)
                                        </li>
                                    </c:if>
                                    <c:forEach var="fieldResult" items="${facetResult.fieldResult}" varStatus="vs">
                                        <c:set var="dateRangeTo"><c:choose><c:when test="${vs.last}">*</c:when><c:otherwise>${facetResult.fieldResult[vs.count].label}</c:otherwise></c:choose></c:set>
                                        <c:choose>
                                            <c:when test="${fn:containsIgnoreCase(facetResult.fieldName, 'occurrence_date') && fn:endsWith(fieldResult.label, 'Z')}">
                                                <li><c:set var="startYear" value="${fn:substring(fieldResult.label, 0, 4)}"/>
                                                    <a href="?${queryParam}&fq=${facetResult.fieldName}:[${fieldResult.label} TO ${dateRangeTo}]">${startYear} - ${startYear + 10}</a>
                                                    (<fmt:formatNumber value="${fieldResult.count}" pattern="#,###,###"/>)</li>
                                            </c:when>
                                            <c:when test="${fn:endsWith(fieldResult.label, 'before')}"><%-- skip --%></c:when>
                                            <c:otherwise>
                                                <li><a href="?${queryParam}&fq=${facetResult.fieldName}:${fieldResult.label}"><fmt:message key="${fieldResult.label}"/></a>
                                                (<fmt:formatNumber value="${fieldResult.count}" pattern="#,###,###"/>)</li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <br/><!-- TODO: redo the "remove current facet" UI -->
                <c:if test="${false && not empty facetQuery}">
                    <div id="removeFacet">
                        <h4>Displaying subset of results, restricted to: <ul id="facetName">
                            <c:forEach var="filter" items="${paramValues.fq}">
                                <c:set var="fqField" value="${fn:substringBefore(filter, ':')}"/>
                                <c:set var="fqValue" value="${fn:substringAfter(filter, ':')}"/>
                                <li><span style="color:#666666;">${fqField}:</span>${fqValue}</li>
                            </c:forEach></ul></h4>
                        <p>&rarr; <a href="?q=${query}">Return to full result list</a></p>
                    </div>
                </c:if>
            </div>
            <!-- totalRecords = ${totalRecords} || lastPage = ${lastPage} -->
            <div id="pointsMap">
            </div>
        </c:if>
        <c:if test="${empty searchResult || searchResult.totalRecords == 0}">
            <h1>Occurrence Search Results</h1>
            <p>Your search - <b>${queryJsEscaped}</b> - did not match any documents.</p>
        </c:if>
    </body>
</html>