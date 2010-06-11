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
            var map, vectorLayer, selectControl, selectFeature, markerLayer;

            /* Openlayers map */
            function loadMap() {
                map = new OpenLayers.Map('map',{numZoomLevels: 16,controls: []});
                //add controls
                map.addControl(new OpenLayers.Control.Navigation({zoomWheelEnabled: false}));
                //map.addControl(new OpenLayers.Control.MousePosition());
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
                    loadVectorLayer();
                });

                markerLayer = new OpenLayers.Layer.Vector("Pin");
                var feature = new OpenLayers.Feature.Vector(
                    new OpenLayers.Geometry.Point(lon, lat),
                    {title:'Your location'},
                    {externalGraphic: 'http://geocoder.ca/marker.png', graphicHeight: 38, graphicWidth: 24, graphicZIndex: 1000, rendererOptions: {zIndexing: true}});
                markerLayer.addFeatures(feature);
                
                map.addLayer(markerLayer);
                markerLayer.setZIndex(1000);

                //map.addLayers([baseLayer,vectorLayer]);
                loadVectorLayer();
            }

            function loadVectorLayer() {
                if (vectorLayer != null) {
                    vectorLayer.destroy();
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
                var zoomLevel = map.getZoom();
                var params = {
                    //q: "${query}",
                    //zoom: zoomLevel
                    "lat": ${latitude},
                    "long":  ${longitude},
                    "radius": ${radius}
                };

                var legend = '<table id="cellCountsLegend"><tr><td style="background-color:#333; color:white; text-align:right;">Record counts:&nbsp;</td><td style="width:60px;background-color:#ffff00;">1&ndash;9</td><td style="width:60px;background-color:#ffcc00;">10&ndash;49</td><td style="width:60px;background-color:#ff9900;">50&ndash;99</td><td style="width:60px;background-color:#ff6600;">100&ndash;249</td><td style="width:60px;background-color:#ff3300;">250&ndash;499</td><td style="width:60px;background-color:#cc0000;">500+</td></tr></table>';

                vectorLayer  = new OpenLayers.Layer.Vector("Occurrences", {
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

            $(document).ready(function() {
                loadMap();
            });
        </script>
    </head>
    <body>
        <h1>Explore Your Area</h1>
        <p>Your Location is: latitude = ${latitude}; longitude = ${longitude}</p>
        <h3>Results for ${location}</h3>
        <p>Show records in a
            <select id="radius" name="radius">
                <option value="10" <c:if test="${radius eq '10'}">selected</c:if>>10</option>
                <option value="50" <c:if test="${radius eq '50'}">selected</c:if>>50</option>
                <option value="100" <c:if test="${radius eq '100'}">selected</c:if>>100</option>
            </select> km radius</p>
        <div id="map" style="width: 500px; height: 400px;"></div>
        <h3>Summary of Species Groups</h3>
        <p></p>
    </body>
</html>
