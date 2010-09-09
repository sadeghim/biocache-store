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
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.query.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-ui-1.8.custom.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.simplemodal.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/openlayers/OpenLayers.js"></script>
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
            var map, vectorLayer, selectControl, selectFeature, loadingControl;

            /* Openlayers map */
            function loadMap() {
                map = new OpenLayers.Map('pointsMap',{controls: []});
                //add controls
                map.addControl(new OpenLayers.Control.Attribution());
                var panel = new OpenLayers.Control.Panel();
                // control to display the "loading" graphic
                loadingControl = new OpenLayers.Control.Button({displayClass: "loadingVector", trigger: dummyFunction});
                panel.addControls([loadingControl]);
                map.addControl(panel);
                // hide the loading graphic
                loadingControl.moveTo(new OpenLayers.Pixel(-1000, -1000));
                baseLayer = new OpenLayers.Layer.WMS( "OpenLayers WMS",
                "http://labs.metacarta.com/wms/vmap0",
                {layers: 'basic'} );
                map.addLayer(baseLayer);
                map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);
                // reload vector layer on zoom event
                map.events.register('zoomend', map, function (e) {
                    loadVectorLayer();
                });

                loadVectorLayer(); // load data via GeoJSON

            }

            function dummyFunction() {
                alert('button');
            }

            /**
             * Load occurrence points/clusters via GeoJSON request
             */
            function loadVectorLayer() {
                if (vectorLayer != null) {
                    vectorLayer.destroy();
                }

                var myStyles = new OpenLayers.StyleMap({
                    "default": new OpenLayers.Style({
                        fillColor: "${'${color}'}",//"#ffcc66",
                        //fillColor: "#D75A25",
                        //strokeColor: "${'${color}'}",
                        fillOpacity: 0.7,
                        graphicZIndex: 1,
                        strokeWidth: 0
                    })
                });
                
                var legend = '<table id="cellCountsLegend" class="show-70"><tr><td style="background-color:#333; color:white; text-align:right;">Record counts:&nbsp;</td><td style="width:60px;background-color:#ffff00;">1&ndash;9</td><td style="width:60px;background-color:#ffcc00;">10&ndash;49</td><td style="width:60px;background-color:#ff9900;">50&ndash;99</td><td style="width:60px;background-color:#ff6600;">100&ndash;249</td><td style="width:60px;background-color:#ff3300;">250&ndash;499</td><td style="width:60px;background-color:#cc0000;">500+</td></tr></table>';

                vectorLayer  = new OpenLayers.Layer.Vector("Occurrences", {
                    styleMap: myStyles,
                    attribution: legend,
                    //strategies: [new OpenLayers.Strategy.BBOX()], // new OpenLayers.Strategy.Fixed(),new OpenLayers.Strategy.BBOX()
                    protocol: new OpenLayers.Protocol.HTTP({
                        format: new OpenLayers.Format.GeoJSON()
                    })
                });

                map.addLayer(vectorLayer);
                
                // trigger lading of GeoJSON
                reloadData();
            }

            /* load features via ajax call */
            function reloadData() {
                // show loading graphic
                loadingControl.moveTo(new OpenLayers.Pixel(270, 220));
                // url vars
                var geoJsonUrl = "${pageContext.request.contextPath}/geojson/cells"; //+"&zoom=4&callback=?";
                var zoomLevel = map.getZoom();
                var params = {
                    q: "${query}",
                <c:forEach items="${facetQuery}" var="fq">fq: "${fq}",</c:forEach>
                    zoom: zoomLevel,
                    type: "${type}"
                };
                // JQuery GET
                $.get(geoJsonUrl, params, dataRequestHandler);
            }

            /* handler for loading features */
            function dataRequestHandler(data) {
                // clear existing
                vectorLayer.destroyFeatures();
                // parse returned json
                var features = new OpenLayers.Format.GeoJSON().read(data);
                // add features to map
                vectorLayer.addFeatures(features);
                // hide the "loading" graphic
                loadingControl.moveTo(new OpenLayers.Pixel(-1000, -1000));
                
                // add select control
                if (selectControl != null) {
                    map.removeControl(selectControl);
                    selectControl.destroy();
                    selectControl = null;
                }

                selectControl = new OpenLayers.Control.SelectFeature(vectorLayer, {
                    //hover: true,
                    onSelect: onFeatureSelect,
                    onUnselect: onFeatureUnselect
                });

                map.addControl(selectControl);
                selectControl.activate();
            }


            function onPopupClose(evt) {
                selectControl.unselect(selectedFeature);
            }

            function onFeatureSelect(feature) {
                selectedFeature = feature;
                popup = new OpenLayers.Popup.FramedCloud("cellPopup", feature.geometry.getBounds().getCenterLonLat(),
                    null, "<div style='font-size:12px; color: #222;'>Number of records: " + feature.attributes.count, // +
                    //"<br /><a href=''>View records in this area</a> " + feature.geometry.getBounds().toBBOX() + "</div>",
                    null, true, onPopupClose);
                feature.popup = popup;
                map.addPopup(popup);
            }

            function onFeatureUnselect(feature) {
                map.removePopup(feature.popup);
                feature.popup.destroy();
                feature.popup = null;
            }

            function destroyMap() {
                if (map != null) {
                    map.destroy();
                    $("#pointsMap").html('');
                }
            }

    function checkRegexp(o,regexp,n) {

        if ( o.val().length>0 &&!( regexp.test( o.val() ) ) ) {
            o.addClass('ui-state-error');
            alert(n);
            return false;
        } else {
            return true;
        }

}

            // Jquery Document.onLoad equivalent
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
                    collapsible: true,
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

                $("#searchButtons > button").button();
                $("#searchButtons > button#download").click(function() {
                    //var downloadUrl = "${pageContext.request.contextPath}/occurrences/download?q=${query}&fq=${fn:join(facetQuery, '&fq=')}&type=${type}";
                    
                    $("#dialog-confirm").dialog('open');
                    //alert("URL is "+downloadUrl);
                    //if (confirm("Continue with download?\rClick 'OK' to download or 'cancel' to abort.")) {
                    //    window.location.replace(downloadUrl);
                   // }
                });

                 // Configure Dialog box for Download button (JQuery UI)
                    $("#dialog-confirm").dialog({
                        resizable: true,
                        modal: true,
                        autoOpen: false,
                        width: 375,
                        buttons: {
                            'Download File': function() {
                                var email = $("#email");
                                email.removeClass('ui-state-error');
                                //Only allow empty or valid email addresses
                                // From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
                                if(checkRegexp(email, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i,"Invalid email format. (eg. name@host.com)")){

                                    var reason = $("#reason").val();
                                    if(typeof reason == "undefined")
                                        reason="";
                                    var downloadUrl = "${pageContext.request.contextPath}/occurrences/download?q=${query}&fq=${fn:join(facetQuery, '&fq=')}&type=${type}&email="+email.val()+"&reason="+encodeURIComponent(reason)+"&file="+$("#filename").val();

                                    window.location.replace(downloadUrl);
                                    $(this).dialog('close');
                                }
                            },
                            Cancel: function() {
                                $(this).dialog('close');
                                $("#email").removeClass('ui-state-error');
                            }
                        }
                    });

                $('button#showMap').click(function (e) {
                    window.location.replace("#searchResults");
                    $("#pointsMap").show();
                    loadMap();
                    $('#pointsMap').modal();

                });

                // more/fewer search option links
                $("#refineMore a").click(function(e) {
                    e.preventDefault();
                    $("#accordion").slideDown();
                    $("#refineLess").show('slow');
                    $("#refineMore").hide('slow');
                });
                $("#refineLess a").click(function(e) {
                    e.preventDefault();
                    $("#accordion").slideUp();
                    $("#refineLess").hide('slow');
                    $("#refineMore").show('slow');
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
                // add the triggered param
                if (paramName != null && paramValue != null) {
                    if (paramName == 'sort') {
                        paramList.push(paramName + "=" +paramValue);
                    } else if (paramName == 'dir' && !(sort == null || sort == 'score')) {
                        paramList.push(paramName + "=" +paramValue);
                    }
                }

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
                var fqList = $.query.get('fq');
                $("#searchForm").submit();
                window.location.replace(url);
            }
        </script>
    </head>
    <body>
        <div id="header">
            <div id="breadcrumb">
                <a href="http://test.ala.org.au">Home</a>
                <a href="http://test.ala.org.au/explore">Explore</a>
                Occurrence Records Search
            </div>
            <h1>Occurrence Search Results</h1>
        </div>
        <div id="column-one" class="full-width">
            <div class="section">
                <c:if test="${not empty searchResult && searchResult.totalRecords > 0}">
                    <fmt:formatNumber value="${searchResult.totalRecords}" pattern="#,###,###" var="totalHits"/>
                    <div id="searchResults">
                        <fmt:formatNumber var="currentPage" value="${(searchResult.startIndex / searchResult.pageSize) + 1}" pattern="0"/>
                        <div class="solrResults">
                            <div id="searchHeader">
                                <div id="searchButtons">
                                    <c:if test="${!fn:contains(entityQuery, 'km of point')}"><%-- Don't display buttons on searchByArea version of page --%>
                                        <button id="download" title="Download all ${totalHits} results as XLS (tab-delimited) file">Download</button>
                                        <button id="showMap" title="Display a small map showing points for records">View as Map</button>
                                    </c:if>
                                </div>
                                    <div id="dialog-confirm" title="Download Occurrences" >
                                        <!--p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Download the result list.</p-->
                                        <p>Please provide the following optional details before downloading:</p>
                                        <form id="downloadForm">
                                            <fieldset>
                                                <p><label for="email">Email</label>
                                                <input type="text" name="email" id="email" value="${pageContext.request.remoteUser}" size="30"  /></p>
                                                <p><label for="filename">File Name</label>
                                                <input type="text" name="filename" id="filename" value="data" size="30"  /></p>
                                                <p><label for="reason" style="vertical-align: top">Download Reason</label>
                                                <textarea name="reason" rows="5" cols="30" id="downloadReason"  ></textarea></p>
                                            </fieldset>
                                        </form>
                                    </div>
                                <div id="searchTerms">
                                    <div class="queryTermBox">
                                        <c:choose>
                                            <c:when test="${not empty entityQuery}">
                                                ${entityQuery}
                                            </c:when>
                                            <c:otherwise>
                                                Search: <a href="?q=${queryJsEscaped}">${queryJsEscaped}</a><a name="searchResults">&nbsp;</a>
                                            </c:otherwise>
                                        </c:choose>
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
                                <div id="resultsStats">
                                    Returned <fmt:formatNumber value="${searchResult.totalRecords}" pattern="#,###,###"/> results
                                    <%--Page ${currentPage} of <fmt:formatNumber value="${lastPage}" pattern="#,###,###"/>--%>
                                </div>
                                <div id="searchControls">
                                    sort by
                                    <select id="sort" name="sort">
                                        <option value="score" <c:if test="${param.sort eq 'score'}">selected</c:if>>best match</option>
                                        <option value="taxon_name" <c:if test="${param.sort eq 'taxon_name'}">selected</c:if>>scientific name</option>
                                        <option value="common_name" <c:if test="${param.sort eq 'common_name'}">selected</c:if>>common name</option>
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
                            </div>
                            <table id="occurrenceResults">
                                <thead>
                                    <tr>
                                        <th>Scientific Name</th>
                                        <th>Common Name</th>
                                        <th>Dataset</th>
                                        <th>Record Type</th>
                                        <th>Record Date</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="occurrence" items="${searchResult.occurrences}">
                                        <tr>
                                            <td id="col1"><a href="${occurrence.id}" class="occurrenceLink"><alatag:formatSciName rankId="${occurrence.rankId}" name="${occurrence.taxonName}"/></a></td>
                                            <td id="col5">${occurrence.commonName}</td>
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
                        <div id="searchTypes" style="display:none">
                            <ul>
                                <li><a href="#">Site Pages</a></li>
                                <li><a href="/bie-webapp/species/search?q=${param['q']}">Species</a></li>
                                <li><a href="#">Regions</a></li>
                                <li class="active"><strike>Occurrence Records</strike></li>
                                <li><a href="#">Institutions</a></li>
                                <li><a href="#">Collections</a></li>
                                <li><a href="#">Data Providers</a></li>
                                <li><a href="#">Data Sets</a></li>
                            </ul>
                        </div>
                        <div id="refineMore" style="display:none;"><a href="#">More Search Options</a></div>
                        <div id="accordion"  style="display:block;">
                            <c:if test="${not empty query}">
                                <c:set var="queryParam">q=<c:out value="${param['q']}" escapeXml="true"/><c:if
                                        test="${not empty param.fq}">&fq=${fn:join(paramValues.fq, "&fq=")}</c:if></c:set>
                            </c:if>
                            <c:forEach var="facetResult" items="${searchResult.facetResults}">
                                <c:if test="${!fn:containsIgnoreCase(facetQuery, facetResult.fieldResult[0].label)}">
                                    <h3><a href="#"><span class="FieldName"><fmt:message key="facet.${facetResult.fieldName}"/></span></a></h3>
                                    <div id="subnavlist">
                                        <ul>
                                            <c:set var="lastElement" value="${facetResult.fieldResult[fn:length(facetResult.fieldResult)-1]}"/>
                                            <c:if test="${lastElement.label eq 'before' && lastElement.count > 0}">
                                                <li><c:set var="firstYear" value="${fn:substring(facetResult.fieldResult[0].label, 0, 4)}"/>
                                                    <a href="?${queryParam}&fq=${facetResult.fieldName}:[* TO ${facetResult.fieldResult[0].label}]">Before ${firstYear}</a>
                                                    (<fmt:formatNumber value="${lastElement.count}" pattern="#,###,###"/>)
                                                </li>
                                            </c:if>
                                            <c:forEach var="fieldResult" items="${facetResult.fieldResult}" varStatus="vs">
                                                <c:if test="${fieldResult.count > 0}">
                                                     <c:set var="dateRangeTo"><c:choose><c:when test="${vs.last || facetResult.fieldResult[vs.count].label=='before'}">*</c:when><c:otherwise>${facetResult.fieldResult[vs.count].label}</c:otherwise></c:choose></c:set>
                                                    <c:choose>
                                                        <c:when test="${fn:containsIgnoreCase(facetResult.fieldName, 'occurrence_date') && fn:endsWith(fieldResult.label, 'Z')}">
                                                            <li><c:set var="startYear" value="${fn:substring(fieldResult.label, 0, 4)}"/>
                                                                <a href="?${queryParam}&fq=${facetResult.fieldName}:[${fieldResult.label} TO ${dateRangeTo}]">${startYear} - ${startYear + 10}</a>
                                                                (<fmt:formatNumber value="${fieldResult.count}" pattern="#,###,###"/>)</li>
                                                        </c:when>
                                                        <c:when test="${fn:endsWith(fieldResult.label, 'before')}"><%-- skip, otherwise gets inserted at bottom, not top of list --%></c:when>
                                                        <c:otherwise>
                                                            <li><a href="?${queryParam}&fq=${facetResult.fieldName}:${fieldResult.label}"><fmt:message key="${not empty fieldResult.label ? fieldResult.label : 'unknown'}"/></a>
                                                                (<fmt:formatNumber value="${fieldResult.count}" pattern="#,###,###"/>)</li>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                        <div id="refineLess" style="display:none;"><a href="#">Fewer Search Options</a></div>
                        <br/>
                        <div id="pointsMap"></div>
                        <div id="busyIcon" style="display:none;"><img src="${pageContext.request.contextPath}/static/css/images/wait.gif" alt="busy/spinning icon" /></div>
                    </div>
                </c:if>
                <c:if test="${empty searchResult || searchResult.totalRecords == 0}">
                    <br/>
                    <p>Your search
                        <c:if test="${not empty entityQuery}">
                            ${entityQuery}
                        </c:if>
            	- <b>${queryJsEscaped}</b> 
            	- did not match any documents.            
                    </p>
                </c:if>
            </div>
        </div>
    </body>
</html>