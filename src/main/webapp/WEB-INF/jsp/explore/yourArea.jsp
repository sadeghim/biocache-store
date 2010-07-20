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
            <title>Explore Your Area</title>
            <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensorfalse&amp;key=${googleKey}" type="text/javascript"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/openlayers/OpenLayers.js"></script>
            <script type="text/javascript">
                /* Openlayers vars - ${param['radius']} */
                var lon = ${longitude};
                var lat = ${latitude};
                //make the taxa and rank global variable so that they can be used in the download
                var taxa =[]
                taxa[0] ="*";
                var rank ="*";
                var zoom = ${zoom};
                var map, vectorLayer, selectControl, selectFeature, markerLayer, circleLayer;
                var geocoder;
                var proj900913 = new OpenLayers.Projection("EPSG:900913");
                var proj4326 = new OpenLayers.Projection("EPSG:4326");
                /* Openlayers map */
                function loadMap() {
                    map = new OpenLayers.Map('yourMap',{
                        //projection: proj900913,
                        numZoomLevels: 20,
                        controls: []});
                    //add controls
                    map.addControl(new OpenLayers.Control.Navigation({zoomWheelEnabled: false}));
                    map.addControl(new OpenLayers.Control.PanZoomBar({zoomWorldIcon: false}));
                    map.addControl(new OpenLayers.Control.Attribution());
                    //map.addControl(new OpenLayers.Control.ScaleLine());
                    map.addControl(new OpenLayers.Control.LayerSwitcher({'ascending':false}));
                    //map.addControl(new OpenLayers.Control.OverviewMap());
                    var baseLayer = new OpenLayers.Layer.WMS(
                        "OpenLayers WMS",
                        "http://labs.metacarta.com/wms/vmap0",// "http://labs.metacarta.com/wms-c/Basic.py?"
                        {layers: 'basic'}, // {layers: 'satellite'},
                        {wrapDateLine: true}
                    );
                    var blueMarbleLayer = new OpenLayers.Layer.WMS(
                        "Satellite",
                        "http://labs.metacarta.com/wms-c/Basic.py?",
                        {layers: 'satellite'},
                        {wrapDateLine: true}
                    );
                    var satelliteLayer = new OpenLayers.Layer.Google(
                        "Google Hybrid Map" ,
                        {type: G_HYBRID_MAP, 'sphericalMercator': false}
                    );

                    map.addLayers([satelliteLayer,baseLayer]); //  blueMarbleLayer
                    //                var gphy = new OpenLayers.Layer.Google(
                    //                    "Google Physical",
                    //                    {type: G_PHYSICAL_MAP,'sphericalMercator': true}
                    //                );
                    //                var gmap = new OpenLayers.Layer.Google(
                    //                    "Google Streets", // the default
                    //                    {numZoomLevels: 20,'sphericalMercator': true}
                    //                );
                    //                map.addLayers([satellite, gphy, gmap]);

                    var point = new OpenLayers.LonLat(lon, lat);
                    map.setCenter(point.transform(proj4326, map.getProjectionObject()), zoom);
                    //map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);
                    // reload vector layer on zoom event
                    map.events.register('zoomend', map, function (e) {
                        drawCircleRadius();
                        loadRecordsLayer();
                        loadSelectControl();
                    });

                    // marker pin (Google-style)
                    markerLayer = new OpenLayers.Layer.Vector("Pin");
                    var pinPoint = new OpenLayers.Geometry.Point(lon, lat);
                    var feature = new OpenLayers.Feature.Vector(
                    pinPoint.transform(proj4326, map.getProjectionObject()),
                    {title:'Your location' },
                    {externalGraphic: '${pageContext.request.contextPath}/static/css/images/marker.png', graphicHeight: 28, graphicWidth: 18, graphicYOffset: -24 , graphicZIndex: 1000, rendererOptions: {zIndexing: true}}
                );

                    markerLayer.addFeatures(feature);
                    map.addLayer(markerLayer);
                    markerLayer.setZIndex(1000);

                    // circle showing area included in search

                    drawCircleRadius();
                    //loadVectorLayer();
                    loadSelectControl();
                }

                function loadRecordsLayer(taxa, rank) {
                    if (vectorLayer != null) {
                        vectorLayer.destroy();
                        vectorLayer = null;
                    }

                    var myStyles = new OpenLayers.StyleMap({
                        "default": new OpenLayers.Style({
                            pointRadius: 5, //"${'${count}'}", // sized according to count attribute
                            fillColor: "${'${color}'}",//"#ffcc66",
                            //fillColor: "#D75A25",
                            strokeColor: "${'${color}'}",
                            fillOpacity: 0.6,
                            graphicZIndex: "${'${count}'}",
                            strokeWidth: 0
                        })
                    });

                    var geoJsonUrl = "${pageContext.request.contextPath}/geojson/radius-points"; //+"&zoom=4&callback=?";
                    //var zoomLevel = map.getZoom();

                    var params = {
                        //q: "${query}",
                        //zoom: zoomLevel
                        "taxa": taxa,
                        "rank": rank,
                        "lat": ${latitude},
                        "long":  ${longitude},
                        "radius": ${radius}
                    };

                    var proj_options = {
                        'internalProjection': map.baseLayer.projection,
                        'externalProjection': proj4326
                    };
                    
                    //var legend = '<table id="cellCountsLegend"><tr><td style="background-color:#333; color:white; text-align:right;">Records:&nbsp;</td><td style="width:50px;background-color:#ffff00;">1&ndash;9</td><td style="width:50px;background-color:#ffcc00;">10&ndash;49</td><td style="width:50px;background-color:#ff9900;">50&ndash;99</td><td style="width:50px;background-color:#ff6600;">100&ndash;249</td><td style="width:50px;background-color:#ff3300;">250&ndash;499</td><td style="width:50px;background-color:#cc0000;">500+</td></tr></table>';

                    vectorLayer = new OpenLayers.Layer.Vector("Occurrences", {
                        projection: proj4326,
                        styleMap: myStyles,
                        rendererOptions: {zIndexing: true},
                        //attribution: legend,
                        strategies: [new OpenLayers.Strategy.Fixed()], // new OpenLayers.Strategy.Fixed(),new OpenLayers.Strategy.BBOX()
                        protocol: new OpenLayers.Protocol.HTTP({
                            url: geoJsonUrl,
                            params: params,
                            format: new OpenLayers.Format.GeoJSON(proj_options)
                        })
                    });

                    map.addLayer(vectorLayer);
                    vectorLayer.refresh();
                    markerLayer.setZIndex(1000); // so pin icon isn't covered with points
                }

                function onPopupClose(evt) {
                    selectControl.unselect(selectedFeature);
                }

                function onFeatureSelect(feature) {
                    selectedFeature = feature;
                    popup = new OpenLayers.Popup.FramedCloud("chicken", feature.geometry.getBounds().getCenterLonLat(),
                    null, "<div style='font-size:.8em'>Records in area: " + feature.attributes.count, // +
                    //"<br /><a href=''>View records in this area</a> " + feature.geometry.getBounds() + "</div>",
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
                        //alert("destroying map");
                        map.destroy();
                        $("#pointsMap").html('');
                    }
                }

                function loadSelectControl() {
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
                    //selectControl.activate();  // errors on map re-size/zoom change so commented-out for now
                }

                function drawCircleRadius() {
                    if (circleLayer != null) {
                        circleLayer.destroy();
                        circleLayer = null;
                    }

                    circleLayer = new OpenLayers.Layer.Vector("Cirlce", {projection: proj4326});
                    var point = new OpenLayers.Geometry.Point(lon, lat);
                    var DOTS_PER_UNIT = OpenLayers.INCHES_PER_UNIT.km * OpenLayers.DOTS_PER_INCH;
                    var rad = ${radius} * DOTS_PER_UNIT / map.getScale();
                    var style_green = {
                        fillColor: "lightBlue",
                        fillOpacity: 0.5,
                        strokeColor: "lightBlue",
                        strokeOpacity: 1,
                        strokeWidth: 1,
                        //graphicZIndex: 10,
                        pointRadius: rad
                        //pointerEvents: "visiblePainted"
                    };
                    var pointFeature = new OpenLayers.Feature.Vector(point.transform(proj4326, map.getProjectionObject()),{},style_green);
                    //pointFeature.transform(proj4326, map.getProjectionObject());
                    circleLayer.addFeatures([pointFeature]);
                    map.addLayer(circleLayer);
                    //circleLayer.setZIndex(10);
                }

                function addAddressToPage(response) {
                    //map.clearOverlays();
                    if (!response || response.Status.code != 200) {
                        alert("Sorry, we were unable to geocode that address");
                    } else {
                        var location = response.Placemark[0];
                        var lat = location.Point.coordinates[1]
                        var lon = location.Point.coordinates[0];
                        var locationStr = response.Placemark[0].address;
                        $('input#location').val(locationStr);
                        $('input#latitude').val(lat);
                        $('input#longitude').val(lon);
                        $('form#searchForm').submit();
                    }
                }

                function codeAddress(coordinates) {
                    var address = $('input#address').val();
                    var lat = $('input#longitude').val();
                    var lon =$('input#latitude').val();

                    if (geocoder) {
                        if (coordinates && lat && lon) {
                            var latLon = new GLatLng(lon,lat);
                            geocoder.getLocations(latLon, addAddressToPage);
                        }
                        else if (address) {
                            geocoder.getLocations(address, addAddressToPage);
                        }
                    }
                }

                $(document).ready(function() {
                    // re-call function to tweak with search input
                    greyInitialValues();
                    // load OpenLayers map
                    loadMap();
                    geocoder = new GClientGeocoder(); //new google.maps.Geocoder();
                    geocoder.setBaseCountryCode("AU");
                    // catch the link on the taxon groups table
                    $('.taxonBrowse').click(function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        var taxon = $(this).attr('href');
                        rank = $(this).attr('id');
                        taxa = []; // array of taxa
                        if (taxon.contains("|")) {
                            taxa = taxon.split("|");
                        } else {
                            taxa[0] = taxon;
                        }
                        //Internet Explorer for Windows versions up to and including 7 don’t support the value inherit.(http://reference.sitepoint.com/css/background-color)
                        $('.taxonBrowse').parent().parent().css('background-color','white');
                        $(this).parent().parent().css('background-color','#E8EACE');
                        $('#taxa-level-1 tbody tr').css('background-color','#E8EACE');
                        // load records layer on map
                        loadRecordsLayer(taxa, rank);
                        // AJAX...
                        var uri = "${pageContext.request.contextPath}/explore/species.json";
                        var params = "?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa="+taxa+"&rank="+rank;
                        $('#taxa-level-1').html('[loading...]');
                        $.getJSON(uri + params, function(data) {
                            //alert(data.rank + "|" + data.taxa)
                            //$('#taxa-level-1 tbody:last').html('<tr></tr>');
                            if (data.speciesCount > 0) {
                                //$('#taxa-level-1 tbody:last tr:last').html('<td>'+data.species[0].name+' ('+data.species[0].count+')</td>');
                                $('#taxa-level-1').html('<div id="taxaDiv"><ol></ol></div>');
                                for (i=0;i<data.species.length;i++) {
                                    //$('#taxa-level-1 tr:last').after('<tr><td>'+data.species[i].name+' ('+data.species[i].count+')</td></tr>');
                                    if(data.species[i].guid===null){
                                        $('#taxa-level-1 #taxaDiv ol').append('<li><span><i>'+data.species[i].name+'</i> ('+
                                            data.species[i].count+' records)</span></li>');
                                    }
                                    else if(data.species[i].commonName === null){
                                        $('#taxa-level-1 #taxaDiv ol').append('<li><span><a href="${speciesPageUrl}'+
                                            data.species[i].guid+'"><i>'+data.species[i].name+'</i></a> ('+
                                            data.species[i].count+' records)</span></li>');

                                    }
                                    else{
                                        $('#taxa-level-1 #taxaDiv ol').append('<li><span><a href="${speciesPageUrl}'+
                                            data.species[i].guid+'"><i>'+data.species[i].name+'</i></a> - '+data.species[i].commonName+' ('+
                                            data.species[i].count+' records)</span></li>');
                                    }
                                }
                            } else {
                                $('#taxa-level-1 tbody:last tr:last').html('<td>[no species found]</td>');
                            }
                            $('#taxa-level-1 tbody td').css('background-color','#E8EACE');
                        });
                    }
                );

                // By default show the all records group
                $('#taxa-level-0 tbody td:first a.taxonBrowse').click();

                $("button#download").click(
                    function(e){
                        e.preventDefault();
                        var downloadUrl ="${pageContext.request.contextPath}/explore/download?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa="+taxa+"&rank=" + rank;
                        //alert("URL is " + downloadUrl);
                        if (confirm("Continue with download?\rClick 'OK' to download or 'cancel' to abort.")) {
                            window.location.replace(downloadUrl);
                        }

                    }
                );

                $('input#locationSearch').click(
                    function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        codeAddress();
                    }
                );

                $('input#coordSearch').click(
                    function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        $().val("");
                        codeAddress(true);
                    }
                );
                });
            </script>
        </head>
        <body>
            <div id="breadcrumb">
                <a href="http://test.ala.org.au">Home</a>
                <a href="http://test.ala.org.au/explore">Explore</a>
                Your Area
            </div>
            <div id="decoratorBody">
                <h2>Explore Your Area</h2>
                <div id="mapOuter" style="width: 400px; height: 450px; float:right;">
                    <div id="yourMap"></div>
                    <div style="font-size:11px;width:400px;">
                        <table id="cellCountsLegend">
                            <tr>
                                <td style="background-color:#333; color:white; text-align:right;">Records:&nbsp;</td>
                                <td style="width:60px;background-color:#ffff00;">1&ndash;9</td>
                                <td style="width:60px;background-color:#ffcc00;">10&ndash;49</td>
                                <td style="width:60px;background-color:#ff9900;">50&ndash;99</td>
                                <td style="width:60px;background-color:#ff6600;">100&ndash;249</td>
                                <td style="width:60px;background-color:#ff3300;">250&ndash;499</td>
                                <td style="width:60px;background-color:#cc0000;">500+</td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="left-col">
                    <form name="searchForm" id="searchForm" action="" method="GET" autocomplete="off">
                        <div id="locationInput">
                            <h5>Enter your location or address:</h5>
                            <input name="address" id="address" size="50" value="${address}"/>
                            <input id="locationSearch" type="submit" value="Search"/>
                            <input type="hidden" name="latitude" id="latitude" value="${latitude}"/>
                            <input type="hidden" name="longitude" id="longitude" value="${longitude}"/>
                            <input type="hidden" name="location" id="location" value="${location}"/>
                        </div>
                        <div id="locationInfo">
                            <c:if test="${not empty location}">
                                <p>Showing records for: <b>${location}</b></p>
                            </c:if>
                            <p>Display records in a
                                <select id="radius" name="radius">
                                    <option value="5" <c:if test="${radius eq '5'}">selected</c:if>>5</option>
                                    <option value="10" <c:if test="${radius eq '10'}">selected</c:if>>10</option>
                                    <option value="50" <c:if test="${radius eq '50'}">selected</c:if>>50</option>
                                </select> km radius <input type="submit" value="Reload"/>
                                
                        </div>
                <!--        <p>Results - <fmt:formatNumber value="${allLifeCounts}" pattern="#,###,###"/> records found</p>-->
                        <div id="taxaBox">
                            <div id="rightList">
                                <button id="download" title="Download displayed species as XLS (tab-delimited) file">Download</button>
                                <div id="thead">Names</div>
                                <div id="taxa-level-1">
                                </div>
<!--                                <table id="taxa-level-1" style="width:100%;">
                                    <thead><tr><th style="padding-left: 15px;">Names</th></tr></thead>
                                    <tbody><tr></tr></tbody>
                                </table>-->
                            </div>
                            <div id="leftList">
                                <table id="taxa-level-0">
                                    <tr>
                                        <th>Group</th>
                                        <th>Records</th>
                                        <th>Species</th>
                                    </tr>
                                    <tr>
                                        <td><a href="*" id="*" class="taxonBrowse">All Records</a>
                                        <!-- <td><a href="../occurrences/search?q=location.search&lat=${latitude}&lon=${longitude}&rad=${radius}" title="See all ${allLifeCounts} records">${allLifeCounts}</a></td> -->
                                        <td>${allLifeCounts}</td>
                                        <td>${fn:length(allLife)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Animalia" id="kingdom" class="taxonBrowse">Animals</a>
                                        <td>${animalsCount}</td>
                                        <td>${fn:length(animals)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Mammalia" id="class" class="taxonBrowse">Mammals</a></td>
                                        <td>${mammalsCount}</td>
                                        <td>${fn:length(mammals)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Aves" id="class" class="taxonBrowse">Birds</a></td>
                                        <td>${birdsCount}</td>
                                        <td>${fn:length(birds)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Reptilia" id="class" class="taxonBrowse">Reptiles</a></td>
                                        <td>${reptilesCount}</td>
                                        <td>${fn:length(reptiles)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Amphibia" id="class" class="taxonBrowse">Amphibians</a></td>
                                        <td>${frogsCount}</td>
                                        <td>${fn:length(frogs)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Agnatha|Chondrichthyes|Osteichthyes" id="class" class="taxonBrowse">Fish</a></td>
                                        <td>${fishCount}</td>
                                        <td>${fn:length(fish)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent2"><a href="Insecta" id="class" class="taxonBrowse">Insects</a></td>
                                        <td>${insectsCount}</td>
                                        <td>${fn:length(insects)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Plantae" id="kingdom" class="taxonBrowse">Plants</a></td>
                                        <td>${plantsCount}</td>
                                        <td>${fn:length(plants)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Fungi" id="kingdom" class="taxonBrowse">Fungi</a></td>
                                        <td>${fungiCount}</td>
                                        <td>${fn:length(fungi)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Chromista" id="kingdom" class="taxonBrowse">Chromista</a></td>
                                        <td>${chromistaCount}</td>
                                        <td>${fn:length(chromista)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Protozoa" id="kingdom" class="taxonBrowse">Protozoa</a></td>
                                        <td>${protozoaCount}</td>
                                        <td>${fn:length(protozoa)}</td>
                                    </tr>
                                    <tr>
                                        <td class="indent"><a href="Bacteria" id="kingdom" class="taxonBrowse">Bacteria</a></td>
                                        <td>${bacteriaCount}</td>
                                        <td>${fn:length(bacteria)}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </body>
    </html>
