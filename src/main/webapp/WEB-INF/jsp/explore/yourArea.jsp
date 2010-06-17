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
        <script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js"></script>
        <script type="text/javascript">
            /* Openlayers vars - ${param['radius']} */
            var lon = ${longitude};
            var lat = ${latitude};
            var zoom = ${zoom};
            var map, vectorLayer, selectControl, selectFeature, markerLayer, circleLayer;

            /* Openlayers map */
            function loadMap() {
                map = new OpenLayers.Map('map',{numZoomLevels: 16,controls: []});
                //add controls
                map.addControl(new OpenLayers.Control.Navigation({zoomWheelEnabled: false}));
                map.addControl(new OpenLayers.Control.PanZoomBar({zoomWorldIcon: false}));
                map.addControl(new OpenLayers.Control.Attribution());
                map.addControl(new OpenLayers.Control.ScaleLine());
                map.addControl(new OpenLayers.Control.LayerSwitcher({'ascending':false}));
                map.addControl(new OpenLayers.Control.OverviewMap());
                baseLayer = new OpenLayers.Layer.WMS( "OpenLayers WMS",
                        "http://labs.metacarta.com/wms/vmap0",// "http://labs.metacarta.com/wms-c/Basic.py?"
                        {layers: 'basic'}, // {layers: 'satellite'},
                        {wrapDateLine: true} ); 
                map.addLayer(baseLayer);
                map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);
                // reload vector layer on zoom event
                map.events.register('zoomend', map, function (e) {
                    drawCircleRadius();
                    loadVectorLayer();
                    loadSelectControl();
                });
                
                // marker pin (Google-style)
                markerLayer = new OpenLayers.Layer.Vector("Pin");
                var feature = new OpenLayers.Feature.Vector(
                    new OpenLayers.Geometry.Point(lon, lat),
                    {title:'Your location'},
                    {externalGraphic: '${pageContext.request.contextPath}/static/css/images/marker.png', graphicHeight: 28, graphicWidth: 18, graphicYOffset: -14, graphicZIndex: 1000, rendererOptions: {zIndexing: true}});
                markerLayer.addFeatures(feature);
                
                map.addLayer(markerLayer);
                markerLayer.setZIndex(1000);
                
                // circle showing area included in search
                
                drawCircleRadius();
                loadVectorLayer();
                loadSelectControl();
            }

            function loadVectorLayer() {
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
                        fillOpacity: 0.7,
                        graphicZIndex: "${'${count}'}",
                        strokeWidth: 0
                    })
                });

                var geoJsonUrl = "${pageContext.request.contextPath}/geojson/radius-points"; //+"&zoom=4&callback=?";
                //var zoomLevel = map.getZoom();
                var params = {
                    //q: "${query}",
                    //zoom: zoomLevel
                    "lat": ${latitude},
                    "long":  ${longitude},
                    "radius": ${radius}
                };

                var legend = '<table id="cellCountsLegend"><tr><td style="background-color:#333; color:white; text-align:right;">Records:&nbsp;</td><td style="width:50px;background-color:#ffff00;">1&ndash;9</td><td style="width:50px;background-color:#ffcc00;">10&ndash;49</td><td style="width:50px;background-color:#ff9900;">50&ndash;99</td><td style="width:50px;background-color:#ff6600;">100&ndash;249</td><td style="width:50px;background-color:#ff3300;">250&ndash;499</td><td style="width:50px;background-color:#cc0000;">500+</td></tr></table>';

                vectorLayer = new OpenLayers.Layer.Vector("Occurrences", {
                    styleMap: myStyles,
                    rendererOptions: {zIndexing: true},
                    attribution: legend,
                    strategies: [new OpenLayers.Strategy.Fixed()], // new OpenLayers.Strategy.Fixed(),new OpenLayers.Strategy.BBOX()
                    protocol: new OpenLayers.Protocol.HTTP({
                        url: geoJsonUrl,
                        params: params,
                        format: new OpenLayers.Format.GeoJSON()
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

                circleLayer = new OpenLayers.Layer.Vector("Cirlce", {});
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
                var pointFeature = new OpenLayers.Feature.Vector(point,null,style_green);
                circleLayer.addFeatures([pointFeature]);
                map.addLayer(circleLayer);
                //circleLayer.setZIndex(10);
            }

            $(document).ready(function() {
                loadMap();
//                $("#treeView").treeview({
//			url: "/biocache-webapp/explore/species.json"
//		});
                $('.taxonBrowse').click(
                    function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        var taxon = $(this).attr('href');
                        var rank = $(this).attr('id');
                        $('.taxonBrowse').parent().parent().css('background-color','inherit');
                        $(this).parent().parent().css('background-color','#DDD');
                        $('#taxa-level-1 tbody tr').css('background-color','#DDD');
                        // AJAX...
                        var uri = "/biocache-webapp/explore/species.json";
                        var params = "?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa="+taxon+"&rank="+rank;
                        $('#taxa-level-1 tbody:last').html('<tr><td>[loading...]</td></tr>');
                        $.getJSON(uri + params, function(data) {
                            //alert(data.rank + "|" + data.taxa)
                            $('#taxa-level-1 tbody:last').html('<tr></tr>');
                            if (data.speciesCount > 0) {
                                //$('#taxa-level-1 tbody:last tr:last').html('<td>'+data.species[0].name+' ('+data.species[0].count+')</td>');
                                $('#taxa-level-1 tbody:last tr:last').html('<td rowspan="12"><div id="taxaDiv"><ol></ol></div></td>');
                                for (i=0;i<data.species.length;i++) {
                                    //$('#taxa-level-1 tr:last').after('<tr><td>'+data.species[i].name+' ('+data.species[i].count+')</td></tr>');
                                    $('#taxa-level-1 #taxaDiv ol').append('<li><a href="${speciesPageUrl}'+
                                        data.species[i].guid+'"><i>'+data.species[i].name+'</i></a> ('+
                                        data.species[i].count+' records)</li>');
                                }
                            } else {
                                $('#taxa-level-1 tbody:last tr:last').html('<td>[no species found]</td>');
                            }
                            $('#taxa-level-1 tbody td').css('background-color','#DDD');
                        });
                    }
                );

                $('#taxa-level-0 tbody td:first a.taxonBrowse').click();
            });
        </script>
    </head>
    <body>
        <h1>Explore Your Area</h1>
        <div id="map" style="width: 400px; height: 400px;float:right;"></div>
        <form name="searchForm" id="searchForm" action="" method="GET" autocomplete="off">
        <p>Your Location is:
            latitude <input name="latitude" id="latitude" <c:if test="${not empty latitude}">value="<c:out value="${latitude}" />"</c:if> type="text" size="8"/>
            longitude <input name="longitude" id="longitude" <c:if test="${not empty longitude}">value="<c:out value="${longitude}" />"</c:if> type="text" size="8"/></p>
        <p>Show records in a
            <select id="radius" name="radius">
                <option value="5" <c:if test="${radius eq '5'}">selected</c:if>>5</option>
                <option value="10" <c:if test="${radius eq '10'}">selected</c:if>>10</option>
                <option value="50" <c:if test="${radius eq '50'}">selected</c:if>>50</option>
            </select> km radius <input type="submit" value="Reload"/></p>
        <h3>Results for ${location} (<fmt:formatNumber value="${allLifeCounts}" pattern="#,###,###"/> records found)</h3>

        <div id="taxaBox">
            <div id="rightList" style="float:right; width:290px; margin-right: 10px;">
                <table id="taxa-level-1" style="width:100%;">
                    <thead><tr><th>&nbsp;</th></tr></thead>
                    <tbody><tr></tr></tbody>
                </table>
            </div>
            <div id="leftList">
                <table id="taxa-level-0" style="width:100%;">
                    <tr>
                        <th>Group</th>
                        <th>Records</th>
                        <th>Species</th>
                    </tr>
                    <tr>
                        <td><a href="*" id="*" class="taxonBrowse">All Life</a>
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
                        <td class="indent2"><a href="Reptilia" id="class" class="taxonBrowse">Fish</a></td>
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
    </body>
</html>
