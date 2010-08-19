/* 
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 * 
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 * 
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 */

// Note there are some global variables that are set by the calling page (which has access to
// the ${pageContet} object, which are required by this file.:
//
//  var lon = ${longitude};
//  var lat = ${latitude};
//  var radius = ${radius};
//  var contextPath = "${pageContext.request.contextPath}";

var map, selectControl, vectorLayer, selectFeature, markerLayer, circleLayer;
var geocoder;
var proj900913 = new OpenLayers.Projection("EPSG:900913");
var proj4326 = new OpenLayers.Projection("EPSG:4326");

/**
 * Openlayers map
 */
function loadMap() {
    // create OpenLayers map object
    map = new OpenLayers.Map('yourMap',{maxResolution: 2468,controls: []});
    //add controls - restrict mouse wheel chaos
    map.addControl(new OpenLayers.Control.Navigation({zoomWheelEnabled:false}));
    map.addControl(new OpenLayers.Control.ZoomPanel({displayClass: "olControlZoomPanel olZoomPanel"}));
    //map.addControl(new OpenLayers.Control.PanPanel());
    map.addControl(new OpenLayers.Control.LayerSwitcher({ascending: false}));
    //map.addControl(new OpenLayers.Control.OverviewMap());
    
    // create Google base layers
    var gmap = new OpenLayers.Layer.Google(
        "Google Streets",
        {'sphericalMercator': true, maxExtent: new OpenLayers.Bounds(11548635,-5889094,18604187,-597430)}
    );
    
    var gsat = new OpenLayers.Layer.Google(
        "Google Satellite",
        {'sphericalMercator': true, type: G_SATELLITE_MAP, maxExtent: new OpenLayers.Bounds(11548635,-5889094,18604187,-597430), numZoomLevels: 22}
    );
    
    var ghyb = new OpenLayers.Layer.Google(
        "Google Hybrid",
        {'sphericalMercator': true, maxExtent: new OpenLayers.Bounds(11548635,-5889094,18604187,-597430), type: G_HYBRID_MAP}
    );

    map.addLayers([ghyb, gsat, gmap ]);

    var point = new OpenLayers.LonLat(lon, lat);
    map.setCenter(point.transform(proj4326, map.getProjectionObject()), zoom);
    
    // marker pin (Google-style)
    markerLayer = new OpenLayers.Layer.Vector("Pin");
    var pinPoint = new OpenLayers.Geometry.Point(lon, lat);
    var feature = new OpenLayers.Feature.Vector(
        pinPoint.transform(proj4326, map.getProjectionObject()),
        {title:'Your location' },
        {   externalGraphic: contextPath +'/static/css/images/marker.png',
            graphicHeight: 28,
            graphicWidth: 18,
            graphicYOffset: -24,
            graphicZIndex: 750,
            rendererOptions: {zIndexing: true}
        }
    );

    markerLayer.addFeatures(feature);
    map.addLayer(markerLayer);
    markerLayer.setZIndex(750);
//    
//    // Add "pin" marker
//    var markers = new OpenLayers.Layer.Markers( "Markers" );
//    map.addLayer(markers);
//    var size = new OpenLayers.Size(18,28);
//    var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
//    var icon = new OpenLayers.Icon(contextPath +'/static/css/images/marker.png',size,offset);
//    markers.addMarker(new OpenLayers.Marker(new OpenLayers.Geometry.Point(lon, lat).transform(proj4326, map.getProjectionObject()),icon));

    // load circle showing area included in search
    drawCircleRadius();
    // create the vector Layer
    cerateVectorLayer();
    // load occurrences data onto map
    loadRecordsLayer();
    // reload dynamic layers on zoom event
    map.events.register('zoomend', map, function (e) {
        drawCircleRadius();
        loadGeoJsonData();
    });
}

/**
 * Create vector Layer for GeoJSON data display
 */
function cerateVectorLayer() {
    // remove existing data if present
    if (vectorLayer != null) {
        // Remove any active popups (otherwise they are stuck on screen)
        for (pop in map.popups) {
            map.removePopup(map.popups[pop]);
        }
        vectorLayer.destroyFeatures();
        vectorLayer.destroy();
        //vectorLayer = null;
    }

    // configuring the styling of the vetor layer
    var myStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            pointRadius: 4,
            fillColor: '${color}',
            strokeColor: '${color}',
            fillOpacity: 0.7,
            //graphicZIndex: '${color}',
            strokeWidth: 0
        })
    });

    // projection options
    var GeoJSON_options = {
        ignoreExtraDims: true,
        internalProjection: map.baseLayer.projection,
        externalProjection: proj4326
    };

    // create vector layer for occurrence points
    vectorLayer = new OpenLayers.Layer.Vector("Occurrences", {
        projection: map.baseLayer.projection,
        styleMap: myStyles,
        protocol: new OpenLayers.Protocol.HTTP({
            format: new OpenLayers.Format.GeoJSON(GeoJSON_options)
        })
    });

    map.addLayer(vectorLayer);
}
/**
 * Load (reload) geoJSON data into vector layer
 */
function loadRecordsLayer() {
    // clear vector featers and popups
    vectorLayer.destroyFeatures();
    for (pop in map.popups) {
        map.removePopup(map.popups[pop]);
    }
    // URL for GeoJSON web service
    var geoJsonUrl = contextPath + "/geojson/radius-points"; //+"&zoom=4&callback=?";
    // request params for ajax geojson call
    var params = {
        "taxa": taxa,
        "rank": rank,
        "lat": lat,
        "long":  lon,
        "radius": radius
    };
    // JQuery AJAX call
    $.get(geoJsonUrl, params, loadNewGeoJsonData);
}

/**
 * Callback for geoJSON ajax call
 */
function loadNewGeoJsonData(data) {
    // URL for GeoJSON web service
    var url = contextPath + "/geojson/radius-points"; //+"&zoom=4&callback=?";
    // request params for ajax geojson call
    var params = {
        "taxa": taxa,
        "rank": rank,
        "lat": lat,
        "long":  lon,
        "radius": radius
    };

    var proj_options = {
        internalProjection: map.baseLayer.projection,
        externalProjection: proj4326
    }
    var features = new OpenLayers.Format.GeoJSON(proj_options).read(data);

    vectorLayer.addFeatures(features);

    loadSelectControl();
}

/**
 * Register select event on occurrence points
 */
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
    selectControl.activate();  // errors on map re-size/zoom change so commented-out for now
}

/**
 * vectorLayer feature onSelect function
 */
function onFeatureSelect(feature) {
    selectedFeature = feature;
    var featureCentre = feature.geometry.getCentroid().transform(map.getProjectionObject(), proj4326);
    popup = new OpenLayers.Popup.FramedCloud(feature.attributes.type, feature.geometry.getBounds().getCenterLonLat(),
        null, "<div style='font-size:12px'>Number of records: " + feature.attributes.count +
        "<br /><a href='"+ contextPath +"/occurrences/searchByArea?q="+rank+":"+taxa+"|"+featureCentre.y+"|"+featureCentre.x+"|0.05'>View list of records</a></div>",
        null, true, onPopupClose);
    feature.popup = popup;
    map.addPopup(popup);
}

/**
 * vectorLayer feature onUnselect function
 */
function onFeatureUnselect(feature) {
    map.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
}

function onPopupClose(evt) {
    selectControl.unselect(selectedFeature);
}

function destroyMap() {
    if (map != null) {
        //alert("destroying map");
        map.destroy();
        $("#pointsMap").html('');
    }
}

/**
 * Draw a circle representing the area included in occurrence records search
 */
function drawCircleRadius() {
    if (circleLayer != null) {
        circleLayer.destroy();
        circleLayer = null;
    }

    circleLayer = new OpenLayers.Layer.Vector("Cirlce", {projection: map.getProjectionObject()});
    var point = new OpenLayers.Geometry.Point(lon, lat);
    //alert('proj = '+map.getProjectionObject());
    var DOTS_PER_UNIT = OpenLayers.INCHES_PER_UNIT.km * OpenLayers.DOTS_PER_INCH;

    var rad = radius * DOTS_PER_UNIT / map.getScale();
    // add fudge factor for spherical mercapter projection (Google maps)
    // function was determined using http://www.xuru.org/rt/NLR.asp
    // works well for middle latitidues but is abit out for Hobart and Darwin
    if (map.getProjectionObject() == "EPSG:900913") {
        // (Math.pow(1.005940831, Math.abs(lat))
        // (0.3534329364 * Math.log(Math.abs(lat))
        // (Math.pow(Math.abs(lat), 0.55373737645)
        rad = rad * (Math.pow(1.005940831, Math.abs(lat)));
    }
    var style_green = {
        fillColor: "lightBlue",
        fillOpacity: 0.1,
        strokeColor: "lightBlue",
        strokeOpacity: 0.8,
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

/**
 * Try to get a lat/long using HTML5 geoloation API
 */
function attemptGeolocation() {
    //alert("trying html5 geolocation...");  
    // HTML5 GeoLocation
    if (navigator && navigator.geolocation) {
        //alert("trying to get coords with navigator.geolocation...");  
        function getPostion(position) {  
            $('#yourMap').html(''); // clear message
            //alert('coords: '+position.coords.latitude+','+position.coords.longitude);
            $('#latitude').val(position.coords.latitude);
            $('#longitude').val(position.coords.longitude);
            codeAddress(true);
        }

        function positionDeclined() {
            //alert('geolocation request declined or errored');
            $('#yourMap').html(''); // clear message
            codeAddress();
        }

        $('#yourMap').html('Waiting for confirmation to use your current location (see browser message at top of window').css('color','red').css('font-size','16px');
        navigator.geolocation.getCurrentPosition(getPostion, positionDeclined);

    } else if (google.loader && google.loader.ClientLocation) {
        // Google AJAX API fallback GeoLocation
        //alert("getting coords using google geolocation");
        $('#latitude').val(google.loader.ClientLocation.latitude);
        $('#longitude').val(google.loader.ClientLocation.longitude);
        codeAddress(true);
    } else {
        //alert("Client geolocation failed");
        codeAddress();
    }
}

/**
 * Reverse geocode coordinates via Google Maps API
 */
function codeAddress(reverseGeocode) {
    var address = $('input#address').val();
    var lng = $('input#longitude').val();
    var lat = $('input#latitude').val();

    if (geocoder) {
        if ((reverseGeocode || !address) && lng && lat) {
            //alert("geocoding using latLon");
            var latLon = new GLatLng(lat,lng);
            geocoder.getLocations(latLon, addAddressToPage);
        }
        else if (address) {
            geocoder.getLocations(address, addAddressToPage);
        }
    } else {
        loadMap();
    }
}

/**
 * Geocode location via Google Maps API
 */
function addAddressToPage(response) {
    //map.clearOverlays();
    if (!response || response.Status.code != 200) {
        alert("Sorry, we were unable to geocode that address");
    } else {
        var location = response.Placemark[0];
        var lat = location.Point.coordinates[1]
        var lon = location.Point.coordinates[0];
        var locationStr = response.Placemark[0].address;
        if ($('input#location').val() == "") {
            $('input#address').val(locationStr);
        }
        $('input#location').val(locationStr);
        $('input#latitude').val(lat);
        $('input#longitude').val(lon);
        $('form#searchForm').submit();
    }
}

