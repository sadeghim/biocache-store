<%-- 
    Document   : sighting
    Created on : Aug 6, 2010, 5:19:21 PM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="googleKey" scope="request"><ala:propertyLoader bundle="biocache" property="googleKey"/></c:set>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="UTF-8" >
        <title>Contribute a Sighting</title>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/date.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.timePicker.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/timePicker.css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.datePicker.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/datePicker.css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.tooltip.min.js"></script>
        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/jquery.tooltip.css" />
        <script type="text/javascript" src="http://www.google.com/jsapi?key=${googleKey}"></script>
        <style type="text/css" >
            #mapCanvas {
                width: 315px;
                height: 315px;
            }
        </style>
        <script type="text/javascript">
            // Load Google maps via AJAX API
            google.load("maps", "3", {other_params:"sensor=false"});

            var geocoder, zoom, map, marker;

//            google.setOnLoadCallback(function() {
//                geocoder = new google.maps.Geocoder();
//            });

            function geocodePosition(pos) {
                geocoder.geocode({
                    latLng: pos
                }, function(responses) {
                    if (responses && responses.length > 0) {
                        updateMarkerAddress(responses[0].formatted_address);
                    } else {
                        updateMarkerAddress('Cannot determine address at this location.');
                    }
                });
            }

            function updateMarkerStatus(str) {
                document.getElementById('markerStatus').innerHTML = str;
            }

            function updateMarkerPosition(latLng) {
                document.getElementById('info').innerHTML = [
                    latLng.lat(),
                    latLng.lng()
                ].join(', ');
                var rnd = 100000000;
                var lat = Math.round(latLng.lat() * rnd) / rnd; // round to 8 decimal places
                var lng = Math.round(latLng.lng() * rnd) / rnd; // round to 8 decimal places
                $('#markerLatitude').html(lat);
                $('#sightingLatitude').val(lat);
                $('#markerLongitude').html(lng);
                $('#sightingLongitude').val(lng);
            }

            function updateMarkerAddress(str) {
                document.getElementById('markerAddress').innerHTML = str;
                $('#sightingLocation').val(str);
            }

            function initialize() {
                var lat = $('input#sightingLatitude').val();
                var lng = $('input#sightingLongitude').val();
                var latLng = new google.maps.LatLng(lat, lng);
                map = new google.maps.Map(document.getElementById('mapCanvas'), {
                    zoom: zoom,
                    center: latLng,
                    mapTypeId: google.maps.MapTypeId.HYBRID
                });
                marker = new google.maps.Marker({
                    position: latLng,
                    title: 'Sighting',
                    map: map,
                    draggable: true
                });

                // Update current position info.
                updateMarkerPosition(latLng);
                geocodePosition(latLng);

                // Add dragging event listeners.
                google.maps.event.addListener(marker, 'dragstart', function() {
                    updateMarkerAddress('Dragging...');
                });

                google.maps.event.addListener(marker, 'drag', function() {
                    updateMarkerStatus('Dragging...');
                    updateMarkerPosition(marker.getPosition());
                });

                google.maps.event.addListener(marker, 'dragend', function() {
                    updateMarkerStatus('Drag ended');
                    geocodePosition(marker.getPosition());
                });
            }

            // Onload handler to fire off the app.
            //google.maps.event.addDomListener(window, 'load', initialize);
            /**
             * Try to get a lat/long using HTML5 geoloation API
             */
            function attemptGeolocation() {
                // HTML5 GeoLocation
                if (navigator && navigator.geolocation) {
                    //alert("trying to get coords with navigator.geolocation...");  
                    function getPostion(position) {  
                        //alert('coords: '+position.coords.latitude+','+position.coords.longitude);
                        $('#mapCanvas').html('');
                        $('#sightingLatitude').val(position.coords.latitude);
                        $('#sightingLongitude').val(position.coords.longitude);
                        zoom = 15;
                        //codeAddress(true);
                        initialize();
                    }
                    function positionDeclined() {
                        //alert('geolocation request declined or errored');
                        $('#mapCanvas').html('');
                        zoom = 3;
                        initialize();
                    }

                    $('#mapCanvas').html('Waiting for confirmation to use your current location (see browser message at top of window').css('color','red').css('font-size','14px');
                    navigator.geolocation.getCurrentPosition(getPostion, positionDeclined);
                } else if (google.loader && google.loader.ClientLocation) {
                    // Google AJAX API fallback GeoLocation
                    //alert("getting coords using google geolocation");
                    $('#latitude').val(google.loader.ClientLocation.latitude);
                    $('#longitude').val(google.loader.ClientLocation.longitude);
                    zoom = 11;
                   // codeAddress(true);
                   initialize();
                } else {
                    //alert("Client geolocation failed");
                    //codeAddress();
                    zoom = 10;
                    initialize();
                }
            }

            /**
             * Reverse geocode coordinates via Google Maps API
             */
            function codeAddress() {
                var address = $('input#address').val();

                if (geocoder && address) {
                    //geocoder.getLocations(address, addAddressToPage);
                    geocoder.geocode( { 'address': address}, function(results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            //map.setCenter(results[0].geometry.location);
                            //var marker = new google.maps.Marker({
                            //    map: map,
                            //   position: results[0].geometry.location
                            //});
                            var lat = results[0].geometry.location.lat();
                            var lon = results[0].geometry.location.lng();
                            var locationStr = results[0].formatted_address;
                            $('input#sightingLocation').val(locationStr); // hidden form element
                            $('#markerAddress').val(locationStr); // visible span
                            $('input#sightingLatitude').val(lat); // hidden form element
                            $('#markerLatitude').val(lat); // visible span
                            $('input#sightingLongitude').val(lon); // hidden form element
                            $('#markerLongitude').val(lon); // visible span
                            initialize();
                        } else {
                            alert("Geocode was not successful for the following reason: " + status);
                        }
                    });
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
                    //if ($('input#location').val() == "") {
                    //    $('input#address').val(locationStr);
                    //}
                    $('input#sightingLocation').val(locationStr); // hidden form element
                    $('#markerAddress').val(locationStr); // visible span
                    $('input#sightingLatitude').val(lat); // hidden form element
                    $('#markerLatitude').val(lat); // visible span
                    $('input#sightingLongitude').val(lon); // hidden form element
                    $('#markerLongitude').val(lon); // visible span
                    loadMap();
                }
            }

            /**
             * Document onLoad event using JQuery
             */
            $(document).ready(function() {
                // geocoding
                geocoder = new google.maps.Geocoder();
                attemptGeolocation();

                // populate date if a new entry
                var sightingDate = "${param.date}";
                if (sightingDate.length == 0) {
                    $('#sightingDate').datePicker({startDate:'01/01/1996'}).val(new Date().asString()).trigger('change');
                }

                // populate time if a new entry
                var sightingTime = "${param.time}";
                if (sightingTime.length == 0) {
                    var date = new Date();
                    var hour = date.getHours();
                    if (hour < 10) hour = "0" + hour;
                    var minutes = date.getMinutes();
                    if (minutes < 10) minutes = "0" + minutes;
                    $('#sightingTime').val(hour+":"+minutes);
                }

                $("#sightingTime").timePicker({
                    //startTime: new Date(),
                    //leftOffset: 85,
                    step: 15
                });
                
                // trigger Google geolocation search on search button
                $('#locationSearch').click(function (e) {
                     e.preventDefault(); // ignore the href text - used for data
                     codeAddress();
                });

                // catch the enter key and trigger location search
                $(window).keydown(function(e){
                     if (e.which == 13) {
                         //alert('Enter key pressed');
                         e.preventDefault(); // ignore the href text - used for data
                         codeAddress();
                         return false;
                     }
                     
                });

                // set hieght of inner div
                var h = $('#locationBlock').height();
                $('#location').height(h-20); // $('#locationBlock').height

                $('.locationInput').attr('title', 'Use the map controls to set the location');
                $('.locationInput').tooltip();
            });
        </script>
    </head>
    <body>
        <div id="header">
            <div id="breadcrumb">
                <a href="http://test.ala.org.au">Home</a>
                <a href="http://test.ala.org.au/explore">Contribute</a>
                Contribute a Sighting
            </div>
            <h1>Contribute a Sighting</h1>
        </div>
        
            <c:choose>
                <c:when test="${!empty pageContext.request.remoteUser}"><%-- User is logged in --%>
                    <c:if test="${not empty taxonConcept}">
                        <form name="sighting" id="sighting" action="" method="POST">
                            <div id="column-one">
                                <div style="float: left; padding-right: 15px" id="images" class="section">
                                    <img src="${taxonConcept.imageThumbnailUrl}" height="85px" alt="species thumbnail"/>
                                </div>
                                <div style="margin-left: 115px" class="section">
                                    <h2><a href="http://bie.ala.org.au/species/${param['guid']}"><alatag:formatSciName name="${taxonConcept.scientificName}" rankId="${taxonConcept.rankId}"/>
                                        (${taxonConcept.commonName})</a>
                                        <input type="hidden" name="guid" id="sightingGuid" value="${param.guid}"/>
                                    </h2>
                                    <fieldset id="sightingInfo">
                                        <p><label for="date">Date</label>
                                            <input type="text" id="sightingDate" name="date" size="20" value="${param.date}"/>
                                            <span>(DD-MM-YYYY)</span>
                                        </p>
                                        <p><label for="time">Time</label>
                                            <input type="text" id="sightingTime" name="time" size="20" value="${param.time}"/>
                                            <span>(HH:MM)</span>
                                        </p>
                                        <p><label for="number">Number observed</label>
                                            <select id="sightingNumber" name="number">
                                                <c:forEach var="option" begin="1" end="100" step="1">
                                                    <option<c:if test="${option == param.number}"> selected</c:if>>${option}</option>
                                                </c:forEach>
                                            </select>
                                        </p>
                                        <p><label for="location">Location</label>
                                            <span id="markerAddress" class="locationInput"></span>
                                            <input type="hidden" id="sightingLocation" class="locationInput" name="location" size="20" value=""/>
                                        </p>
                                            <p><label for="latitude">Latitude</label>
                                            <span id="markerLatitude" class="locationInput"></span>
                                            <input type="hidden" id="sightingLatitude" class="locationInput" name="latitude" size="20" value="-27"/>
                                        </p>
                                        <p><label for="longitude">Longitude</label>
                                            <span id="markerLongitude" class="locationInput"></span>
                                            <input type="hidden" id="sightingLongitude" class="locationInput" name="longitude" size="20" value="133"/>
                                        </p>
                                        <p><label for="coordinateUncertainty">Coordinate Uncertainty</label>
                                            <select id="sightingCoordinateUncertainty" name="coordinateUncertainty">
                                                <option>1</option>
                                                <option>5</option>
                                                <option>10</option>
                                                <option>20</option>
                                                <option selected>50</option>
                                                <option>100</option>
                                                <option>500</option>
                                                <option>1000</option>
                                            </select>
                                            <span>(in metres)</span>
                                        </p>
                                        <p><label for="notes">Notes</label>
                                            <textarea id="sightingNotes" name="notes" cols="30" rows="5">${param.notes}</textarea>
                                            <span id="notes">(E.g. weather conditions, observed behaviour, <i>etc.</i>)</span>
                                        </p>
                                        <p><label for=""></label>
                                            <input type="submit" name="action" id="sightingSubmit" value="Next >"/>
                                        </p>
                                    </fieldset>
                                </div>
                            </div>
                            <div id="column-two">
                                <div class="section">
                                    <div id="sightingAddress">
                                        <label for="address">Enter the location or address of sighting: </label>
                                        <input name="address" id="address" size="40" value="${address}"/>
                                        <input id="locationSearch" type="button" value="Search"/>
<!--                                        <input type="hidden" name="location" id="location" value="${location}"/>-->
                                    </div>
                                    <div id="mapCanvas"></div>
                                    <div style="font-size: 90%"><b>Hint:</b> click and drag the marker pin to fine-tune the location coordinates</div>
                                    <div style="display: none">
                                        <div id="markerAddress"></div>
                                        <div id="markerStatus" style="display: none"></div>
                                        <div id="info"></div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="section">${error}</div>
                    </c:if>
                </c:when>
                <c:otherwise><%-- User is NOT logged in --%>
                    <jsp:include page="loginMsg.jsp"/>
                </c:otherwise>
            </c:choose>
    </body>
</html>