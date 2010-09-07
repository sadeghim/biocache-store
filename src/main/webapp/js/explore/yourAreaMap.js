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
//  var contextPath = "${pageContext.request.contextPath}";

var map, selectControl, selectFeature, marker, circle, markerInfowindow, lastInfoWindow;
var points = [];
var infoWindows = [];
var geocoder;

//var proj900913 = new OpenLayers.Projection("EPSG:900913");
//var proj4326 = new OpenLayers.Projection("EPSG:4326");

// pointer fn
function initialize() {
    loadMap();
}
/**
 * Google map API v3
 */
function loadMap() {
    var latLng = new google.maps.LatLng($('#latitude').val(), $('#longitude').val());
    map = new google.maps.Map(document.getElementById('mapCanvas'), {
        zoom: zoom,
        center: latLng,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
        },
        navigationControl: true,
        navigationControlOptions: {
            style: google.maps.NavigationControlStyle.SMALL // DEFAULT
        },
        mapTypeId: google.maps.MapTypeId.HYBRID
    });
    marker = new google.maps.Marker({
        position: latLng,
        title: 'Sighting Location',
        map: map,
        draggable: true
    });

    markerInfowindow = new google.maps.InfoWindow({
        content: '<div class="infoWindow">marker address</div>' // gets updated by geocodePosition()
    });
    
    google.maps.event.addListener(marker, 'click', function(event) {
            if (lastInfoWindow) lastInfoWindow.close();
            markerInfowindow.setPosition(event.latLng);
            markerInfowindow.open(map, marker);
            lastInfoWindow = markerInfowindow;
    });

    // Add a Circle overlay to the map.
    var radius = parseInt($('select#radius').val()) * 1000;
    circle = new google.maps.Circle({
        map: map,
        radius: radius,
        strokeWeight: 1,
        strokeColor: 'white',
        strokeOpacity: 0.5,
        fillColor: '#222', // '#2C48A6'
        fillOpacity: 0.2,
        zIndex: -10
    });
    // bind circle to marker
    circle.bindTo('center', marker, 'position');

    // Update current position info.
    //updateMarkerPosition(latLng);
    geocodePosition(latLng);

    // Add dragging event listeners.
    google.maps.event.addListener(marker, 'dragstart', function() {
        updateMarkerAddress('Dragging...');
    });

    google.maps.event.addListener(marker, 'drag', function() {
        updateMarkerAddress('Dragging...');
        //updateMarkerPosition(marker.getPosition());
    });

    google.maps.event.addListener(marker, 'dragend', function() {
        updateMarkerAddress('Drag ended');
        updateMarkerPosition(marker.getPosition());
        geocodePosition(marker.getPosition());
        LoadTaxaGroupCounts();
        map.setCenter(marker.getPosition());
    });
    
    google.maps.event.addListener(map, 'zoom_changed', function() {
        //loadRecordsLayer();
    });
    
    if (!points || points.length == 0) {
        //$('#taxa-level-0 tbody td:first').click(); // click on "all species" group
        loadRecordsLayer();
    }
}

/**
 * Google geocode function
 */
function geocodePosition(pos) {
    geocoder.geocode({
        latLng: pos
    }, function(responses) {
        if (responses && responses.length > 0) {
            //console.log("geocoded position", responses[0]);
            var address = responses[0].formatted_address;
            updateMarkerAddress(address);
            // update the info window for marker icon
            var content = '<div class="infoWindow"><b>Your Location:</b><br/>'+address+'</div>';
            markerInfowindow.setContent(content);
        } else {
            updateMarkerAddress('Cannot determine address at this location.');
        }
    });
}

/**
 * Update the "address" hidden input and display span
 */
 function updateMarkerAddress(str) {
    $('#markerAddress').empty().html(str);
    $('#location').val(str);
    $('#dialog-confirm code').html(str); // download popup text
}

/**
 * Update the lat & lon hidden input elements
 */
function updateMarkerPosition(latLng) {
    $('#latitude').val(latLng.lat());
    $('#longitude').val(latLng.lng());
    // Update URL hash for back button, etc
    location.hash = latLng.lat() + "|" + latLng.lng() + "|" + zoom;
    $('#dialog-confirm #rad').html(radius);
}

/**
 * Load (reload) geoJSON data into vector layer
 */
function loadRecordsLayer(retry) {
    if (!map && !retry) {
        // in case AJAX calls this function before map has initialised
        setTimeout(function() {if (!points || points.length == 0) {loadRecordsLayer(true);console.log("retrying...");}}, 2000);
        return;
    } else if (!map) {
        //console.log('retry failed');
        return;
    }
  
    // URL for GeoJSON web service
    var geoJsonUrl = contextPath + "/geojson/radius-points";
    var zoom = (map && map.getZoom()) ? map.getZoom() : 12;
    // request params for ajax geojson call
    var params = {
        "taxa": taxa,
        "rank": rank,
        "lat": $('#latitude').val(),
        "long": $('#longitude').val(),
        "radius": $('#radius').val(),
        "zoom": zoom
    };
    //console.log('About to call $.get', map);
    // JQuery AJAX call
    $.get(geoJsonUrl, params, loadNewGeoJsonData);
}

/**
 * Callback for geoJSON ajax call
 */
function loadNewGeoJsonData(data) {
    // clear vector featers and popups
    if (points && points.length > 0) {
        $.each(points, function (i, p) {
            p.setMap(null); // remove from map
        });
        points = [];
    } else {
        points = [];
    }

    if (infoWindows && infoWindows.length > 0) {
        $.each(infoWindows, function (i, n) {
            n.close(); // close any open popups
        });
        infoWindows = [];
    } else {
        infoWindows = [];
    }

    $.each(data.features, function (i, n) {
        var latLng1 = new google.maps.LatLng(n.geometry.coordinates[1], n.geometry.coordinates[0]);
        var iconUrl = contextPath+"/static/images/circle-"+n.properties.color.replace('#','')+".png";
        var markerImage = new google.maps.MarkerImage(iconUrl,
            new google.maps.Size(9, 9),
            new google.maps.Point(0,0),
            new google.maps.Point(4, 5)
        );
        points[i] = new google.maps.Marker({
            map: map,
            position: latLng1,
            title: n.properties.count+" occurrences",
            icon: markerImage
        });

        var solrQuery;
        if (taxa.indexOf("|") > 0) {
            var parts = taxa.split("|");
            var newParts = [];
            for (j in parts) {
                newParts.push(rank+":"+parts[j]);
            }
            solrQuery = newParts.join(" OR ");
        } else {
            solrQuery = rank+':'+taxa;
        }

        var content = '<div class="infoWindow">Number of records: '+n.properties.count+'<br/>'+
                '<a href="'+ contextPath +'/occurrences/searchByArea?q='+solrQuery+'|'+
                n.geometry.coordinates[1]+'|'+n.geometry.coordinates[0]+'|0.05">View list of records</a></div>';
        infoWindows[i] = new google.maps.InfoWindow({
            content: content,
            maxWidth: 200,
            disableAutoPan: false
        });
        google.maps.event.addListener(points[i], 'click', function(event) {
            if (lastInfoWindow) lastInfoWindow.close(); // close any previously opened infoWindow
            infoWindows[i].setPosition(event.latLng);
            infoWindows[i].open(map, points[i]);
            lastInfoWindow = infoWindows[i]; // keep reference to current infoWindow
        });
    });
    
}

/**
 * Try to get a lat/long using HTML5 geoloation API
 */
function attemptGeolocation() {
    // HTML5 GeoLocation
    if (navigator && navigator.geolocation) {
        //console.log("trying to get coords with navigator.geolocation...");  
        function getMyPostion(position) {  
            //alert('coords: '+position.coords.latitude+','+position.coords.longitude);
            //console.log('geolocation request accepted');
            $('#mapCanvas').empty();
            updateMarkerPosition(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
            initialize();
        }
        
        function positionWasDeclined() {
            //console.log('geolocation request declined or errored');
            $('#mapCanvas').empty();
            //zoom = 12;
            initialize();
        }
        // Add message to browser - FF needs this as it is not easy to see
        var msg = 'Waiting for confirmation to use your current location (see browser message at top of window)'+
            '<br/><a href="#" onClick="loadMap(); return false;">Click here to load map</a>';
        $('#mapCanvas').html(msg).css('color','red').css('font-size','14px');
        navigator.geolocation.getCurrentPosition(getMyPostion, positionWasDeclined);
        //console.log("line after navigator.geolocation.getCurrentPosition...");  
        // Neither functions gets called for some reason, so I've added a delay to initalize map anyway
        setTimeout(function() {if (!map) positionWasDeclined();}, 9000);
    } else if (google.loader && google.loader.ClientLocation) {
        // Google AJAX API fallback GeoLocation
        //alert("getting coords using google geolocation");
        updateMarkerPosition(new google.maps.LatLng(google.loader.ClientLocation.latitude, google.loader.ClientLocation.longitude));
        initialize();
    } else {
        //alert("Client geolocation failed");
        //codeAddress();
        zoom = 12;
        initialize();
    }
}

/**
 * Reverse geocode coordinates via Google Maps API
 */
function codeAddress(reverseGeocode) {
    var address = $('input#address').val();

    if (geocoder && address) {
        //geocoder.getLocations(address, addAddressToPage);
        geocoder.geocode( {'address': address, region: 'AU'}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                // geocode was successful
                updateMarkerAddress(results[0].formatted_address);
                updateMarkerPosition(results[0].geometry.location);
                // reload map pin, etc
                initialize();
                loadRecordsLayer();
                LoadTaxaGroupCounts();
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
    } else {
        initialize();
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
        updateMarkerAddress(locationStr);
        updateMarkerPosition(new google.maps.LatLng(lat, lon));
    }
}

