    <%--
        Document   : show
        Created on : Apr 21, 2010, 9:36:39 AM
        Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
    --%>
    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ include file="/common/taglibs.jsp" %>
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
    <c:set var="googleKey" scope="request"><ala:propertyLoader bundle="biocache" property="googleKey"/></c:set>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Explore Your Area | Atlas of Living Australia</title>
            <script type="text/javascript" src="http://www.google.com/jsapi?key=${googleKey}"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-ui-1.8.4.custom.min.js"></script>
            <link type="text/css" rel="stylesheet" href="${initParam.centralServer}/wp-content/themes/ala/css/biocache-theme/jquery-ui-1.8.custom.css" charset="utf-8">
            <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.ba-hashchange.min.js"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/explore/yourAreaMap.js"></script>
            <script type="text/javascript">
                // Global variables for Openlayers
                var contextPath = "${pageContext.request.contextPath}";
                var zoom = ${zoom};
                var radius = ${radius};

                //make the taxa and rank global variable so that they can be used in the download
                var taxa = [];
                taxa[0] ="*";
                var rank ="*";

                var zoomForRadius = {
                    1000: 14,
                    5000: 12,
                    10000: 11
                };

                var radiusForZoom = {
                    11: 10,
                    12: 5,
                    14: 1
                };
                
                // Load Google maps via AJAX API
                google.load("maps", "3", {other_params:"sensor=false"});

                // all mapping JS code moved to /js/explore/yourAreaMap.js

                /**
                 * Process the JSON data from an Species list AJAX request (species in area)
                 */
                function processSpeciesJsonData(data, appendResults) {
                    // clear right list unless we're paging
                    if (!appendResults) {
                        //$('#loadMoreSpecies').detach();
                        $('#rightList tbody').empty();
                    }
                    // process JSON data
                    if (data.speciesCount > 0) {
                        var lastRow = $('#rightList tbody tr').length;
                        var linkTitle = "display on map";
                        var infoTitle = "view species page";
                        var recsTitle = "view list of records";
                        // iterate over list of species from search
                        for (i=0;i<data.species.length;i++) {
                            // create new table row
                            var count = i + lastRow;
                            // add count
                            var tr = '<tr><td>'+(count+1)+'.</td>';
                            // add scientific name
                            tr = tr + '<td class="sciName"><a id="taxon_name" class="taxonBrowse2" title="'+linkTitle+'" href="'+
                                data.species[i].name+'"><i>'+data.species[i].name+'</i></a>';
                            // add common name
                            if (data.species[i].commonName) {
                                tr = tr + ' ('+data.species[i].commonName+')';
                            }
                            // add links to species page and ocurrence search (inside hidden div)
                            var speciesInfo = '<div class="speciesInfo">';
                            if (data.species[i].guid) {
                                speciesInfo = speciesInfo + '<a title="'+infoTitle+'" href="${speciesPageUrl}'+data.species[i].guid+
                                    '"><img src="${pageContext.request.contextPath}/static/css/images/page_white_go.png" alt="species page icon" style="margin-bottom:-3px;"/>'+
                                    ' species profile</a> | ';
                            }
                            speciesInfo = speciesInfo + '<a href="${pageContext.request.contextPath}/occurrences/searchByArea?q=taxon_name:'+data.species[i].name+
                                    '|'+$('input#latitude').val()+'|'+$('input#longitude').val()+'|'+$('select#radius').val()+'" title="'+
                                    recsTitle+'"><img src="${pageContext.request.contextPath}/static/css/images/database_go.png" '+
                                    'alt="search list icon" style="margin-bottom:-3px;"/> list of records</a></div>';
                            tr = tr + speciesInfo;
                            // add number of records
                            tr = tr + '</td><td class="rightCounts">'+data.species[i].count+' </td></tr>';
                            // write list item to page
                            $('#rightList tbody').append(tr);
                            //if (console) console.log("tr = "+tr);
                        }

                        if (data.species.length == 50) {
                            // add load more link
                            var newStart = $('#rightList tbody tr').length;
                            $('#rightList tbody').append('<tr id="loadMoreSpecies"><td>&nbsp;</td><td colspan="2"><a href="'+newStart+
                                '">Show more species</a></td></tr>');
                        }
                        
                    } else if (appendResults) {
                        // do nothing
                    } else {
                        // no spceies were found (either via paging or clicking on taxon group
                        var text = '<tr><td></td><td colspan="2">[no species found]</td></tr>';
                        $('#rightList tbody').append(text);
                    }

                    // Register clicks for the list of species links so that map changes
                    $('#rightList tbody tr').click(function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        var taxon = $(this).find('a.taxonBrowse2').attr('href');
                        rank = $(this).find('a.taxonBrowse2').attr('id');
                        taxa = []; // array of taxa
                        taxa = (taxon.indexOf("|") > 0) ? taxon.split("|") : taxon;
                        //$(this).unbind('click'); // activate links inside this row
                        $('#rightList tbody tr').removeClass("activeRow2"); // un-highlight previous current taxon
                        // remove previous species info row
                        $('#rightList tbody tr#info').detach(); 
                        var info = $(this).find('.speciesInfo').html();
                        // copy contents of species into a new (tmp) row
                        if (info)
                            $(this).after('<tr id="info"><td><td>'+info+'<td></td></tr>');
                        // hide previous selected spceies info box
                        $(this).addClass("activeRow2"); // highloght current taxon
                        // show the links for current selected species
                        //console.log('species link -> loadRecordsLayer()');
                        loadRecordsLayer();
                    });

                    // Register onClick for "load more species" link
                    $('#loadMoreSpecies a').click(
                        function(e) {
                            e.preventDefault(); // ignore the href text - used for data
                            var taxon = $('#taxa-level-0 tr.activeRow').find('a.taxonBrowse').attr('href');
                            rank = $('#taxa-level-0 tr.activeRow').find('a.taxonBrowse').attr('id');
                            taxa = []; // array of taxa
                            taxa = (taxon.indexOf("|") > 0) ? taxon.split("|") : taxon;
                            var start = $(this).attr('href');
                            // AJAX...
                            var uri = "${pageContext.request.contextPath}/explore/species.json";
                            var params = "?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa="+taxa+"&rank="+rank+"&start="+start;
                            //$('#taxaDiv').html('[loading...]');
                            $('#loadMoreSpecies').detach(); // delete it
                            $.getJSON(uri + params, function(data) {
                                // process JSON data from request
                                processSpeciesJsonData(data, true);
                            });
                        }
                    );
                    
                    // add hover effect to table cell with scientific names
                    $('#rightList tbody tr').hover(
                        function() {
                            $(this).addClass('hoverCell');
                        },
                        function() {
                            $(this).removeClass('hoverCell');
                        }
                    );
                }

                /**
                 * For each taxa group get species counts via AJAX call
                 */
                function LoadTaxaGroupCounts() {
                    $('a.taxonBrowse').each(function(index) {
                        var countsUrl = "${pageContext.request.contextPath}/explore/taxaGroupCount";
                        var element = $(this); // for use inside ajax callback
                        var params = {
                            "group": $(this).attr('title'),
                            "latitude": $('#latitude').val(),
                            "longitude": $('#longitude').val(),
                            "radius": $('#radius').val()
                        }
                        $.get(countsUrl, params, function(count) {
                            $(element).parent('td').siblings(':last-child').html(count);
                        });
                    });
                    // reload the all species right list
                    $('#taxa-level-0 tbody td:first').click();
                }

                /**
                 * Document onLoad event using JQuery
                 */
                $(document).ready(function() {
                    // re-call (skin) JS function to tweak with search input
                    greyInitialValues();
                    // instantiate GClientGeocoder
                    //geocoder = new GClientGeocoder(); //new google.maps.Geocoder();
                    geocoder = new google.maps.Geocoder();

                    // onMouseOver event on Group items
                    $('#taxa-level-0 tbody tr').hover(
                        function() {
                            $(this).addClass('hoverRow');
                        },
                        function() {
                            $(this).removeClass('hoverRow');
                        }
                    );
                    // add title attribute for tooltip
                    $('#taxa-level-0 tbody tr').attr('title', 'display on map');
                    // catch the link on the taxon groups table
                    $('#taxa-level-0 tbody tr').click(function(e) {
                        e.preventDefault(); // ignore the href text - used for data
                        taxa = $(this).find('a.taxonBrowse').attr('href'); // $(this+' a.taxonBrowse').attr('href');
                        //console.log("taxon: "+taxon);
                        rank = $(this).find('a.taxonBrowse').attr('id');
                        //taxa = []; // array of taxa
                        //taxa = (taxon.indexOf("|") > 0) ? taxon.split("|") : taxon;
                        $('#taxa-level-0 tr').removeClass("activeRow"); 
                        $(this).addClass("activeRow"); 
                        $('#taxa-level-1 tbody tr').addClass("activeRow"); 
                        // load records layer on map
                        //console.log('about to run: loadRecordsLayer()');
                        if (map) loadRecordsLayer();
                        // AJAX...
                        var uri = "${pageContext.request.contextPath}/explore/species.json";
                        var params = {
                            latitude: $('#latitude').val(),
                            longitude: $('#longitude').val(),
                            radius: $('#radius').val(),
                            taxa: taxa,
                            rank: rank
                        };
                        //var params = "?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa="+taxa+"&rank="+rank;
                        $('#taxaDiv').html('[loading...]');
                        $.getJSON(uri, params, function(data, status) {
                            // process JSON data from request
                            if (status == "success") processSpeciesJsonData(data);
                        });
                    });

                    // By default action on page load - show the all species group (simulate a click)
                    //$('#taxa-level-0 tbody td:first').click();

                    // register click event on "Search" button"
                    $('input#locationSearch').click(
                        function(e) {
                            e.preventDefault(); // ignore the href text - used for data
                            codeAddress();
                        }
                    );

                    // Register onChange event on radius drop-down - will re-submit form
                    $('select#radius').change(
                        function(e) {
                            radius = parseInt($(this).val());
                            var radiusInMetres = radius * 1000;
                            circle.setRadius(radiusInMetres);
                            zoom = zoomForRadius[radiusInMetres];
                            map.setZoom((zoom)?zoom:12);
                            updateMarkerPosition(marker.getPosition()); // so bookmarks is updated
                            //loadRecordsLayer();
                            LoadTaxaGroupCounts();
                        }
                    );

                    // Dynamically set height of #taxaDiv (to match containing div height)
                    var tableHeight = $('#taxa-level-0').height();
                    //$('#rightList table').height(tableHeight+2);
                    $('.tableContainer').height(tableHeight+8);
                    var tbodyHeight = $('#taxa-level-0 tbody').height();
                    $('#rightList tbody').height(tbodyHeight);
                    
                    // register click event on download button
                    $("button#download").click(
                        function(e){
                            e.preventDefault();
                            // trigger dialog box
                            $("#dialog-confirm").dialog('open');
                        }
                    );

                    // Configure Dialog box for Download button (JQuery UI)
                    $("#dialog-confirm").dialog({
                        resizable: false,
                        modal: true,
                        autoOpen: false,
                        buttons: {
                            'Download File': function() {
                                var downloadUrl ="${pageContext.request.contextPath}/explore/download?latitude=${latitude}&longitude=${longitude}&radius=${radius}&taxa=*&rank=*";
                                window.location.replace(downloadUrl);
                                $(this).dialog('close');
                            },
                            Cancel: function() {
                                $(this).dialog('close');
                            }
                        }
                    });

                    // trigger ajax to load counts for taxa groups (left column)
                    LoadTaxaGroupCounts();

                    // Handle back button and saved URLs
                    // hash coding: #lat|lng|zoom
                    var url = escape(window.location.hash.replace( /^#/, '')); // escape used to prevent injection attacks

                    if (url) {
                        var hashParts = url.split("%7C"); // note escaped version of |
                        //console.log("url hash = ", url, coords);
                        if (hashParts.length == 3) {
                            zoom = parseInt(hashParts[2]); // set global var
                            radius = radiusForZoom[zoom];  // set global var
                            $('select#radius').val(radius); // update drop-down widget
                            updateMarkerPosition(new google.maps.LatLng(hashParts[0], hashParts[1]));
                            loadMap();
                        } else {
                            attemptGeolocation();
                        }
                    } else {
                        //console.log("url not set, geolocating...");
                        attemptGeolocation();
                    }
                    
                }); // End: $(document).ready() function
            </script>
        </head>
        <body>
            <div id="header">
                <div id="breadcrumb">
                    <a href="${initParam.centralServer}">Home</a>
                    <a href="${initParam.centralServer}/explore">Explore</a>
                    Your Area
                </div>
                <h1>Explore Your Area</h1>
            </div>
            <div id="column-one" class="full-width">
                <div class="section">
                    <div>
                        <div id="mapOuter" style="width: 400px; height: 450px; float:right;">
                            <div id="mapCanvas" style="width: 400px; height: 430px;"></div>
                            <div style="font-size:11px;width:400px;color: black;" class="show-80">
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
                            <form name="searchForm" id="searchForm" action="" method="GET">
                                <div id="locationInput">
                                    <h2>Enter your location or address</h2>
                                    <div id="searchHints">E.g. a street address, place name, postcode or coordinates (as lat, lon)</div>
                                    <input name="address" id="address" size="50" value="${address}"/>
                                    <input id="locationSearch" type="submit" value="Search"/>
                                    <input type="hidden" name="latitude" id="latitude" value="${latitude}"/>
                                    <input type="hidden" name="longitude" id="longitude" value="${longitude}"/>
                                    <input type="hidden" name="location" id="location" value="${location}"/>
                                </div>
                                <div id="locationInfo">
                                    <c:if test="${true || not empty location}">
                                        <p>Showing records for: <span id="markerAddress">${location}</span></p>
                                    </c:if>
                                    <button id="download" title="Download a list of all species (tab-delimited file)">Download</button>
                                    <div id="dialog-confirm" title="Continue with download?" style="display: none">
                                        <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>You are about to
                                            download a list of species found within a <span id="rad"></span> km radius of <code>${location}</code>.<br/>
                                            Format: tab-delimited text file (called data.xls)</p>
                                    </div>
                                    <p>Display records in a
                                        <select id="radius" name="radius">
                                            <option value="1" <c:if test="${radius eq '1.0'}">selected</c:if>>1</option>
                                            <option value="5" <c:if test="${radius eq '5.0'}">selected</c:if>>5</option>
                                            <option value="10" <c:if test="${radius eq '10.0'}">selected</c:if>>10</option>
                                        </select> km radius <!--<input type="submit" value="Reload"/>-->
                                </div>
                                <div id="taxaBox">
                                    <div id="rightList" class="tableContainer">
                                        <table>
                                            <thead class="fixedHeader">
                                                <tr>
                                                    <th>&nbsp;</th>
                                                    <th>Species</th>
                                                    <th>Records</th>
                                                </tr>
                                            </thead>
                                            <tbody class="scrollContent">
                                            </tbody>
                                        </table>
                                    </div>
                                    <div id="leftList">
                                        <table id="taxa-level-0">
                                            <thead>
                                                <tr>
                                                    <th>Group</th>
                                                    <th>Count</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="tg" items="${taxaGroups}">
                                                    <c:set var="indent">
                                                        <c:choose>
                                                            <c:when test="${tg.parentGroup == null}"></c:when>
                                                            <c:when test="${tg.parentGroup == 'ALL_LIFE'}">indent</c:when>
                                                            <c:otherwise>indent2</c:otherwise>
                                                        </c:choose>
                                                    </c:set>
                                                    <tr>
                                                        <td class="${indent}"><a href="${fn:join(tg.taxa, "|")}" id="${tg.rank}" title="${tg.label}" class="taxonBrowse">${tg.label}</a>
                                                        <td></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </body>
    </html>
