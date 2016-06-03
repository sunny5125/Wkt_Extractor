<?php

include_once("host.php");

global $host;
global $port;

set_time_limit(0);

$socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create socket\n");

$result = socket_connect($socket, $host, $port) or die("Could not connect toserver\n");

//$message="AWESOME";
//socket_write($socket, $message, strlen($message)) or die("Could not send data to server\n");

$result = socket_read ($socket, 1024) or die("Could not read server response\n");
//echo "Reply From Server  :".$result;
$coordinates = json_decode($result,true);
//echo var_export($coordinates,true);
$sw=$coordinates['sw'];
$ne=$coordinates['ne'];
$zoom=$coordinates['zoom'];

$row=$coordinates['row'];
$col=$coordinates['col'];
//var_dump($sw);
//var_dump($ne);

socket_close($socket);
//exit();
?>

    <html>

    <head>
        <title>Resource Allocation Tool</title>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
        <meta charset="utf-8">
        <style>
            html,
            body,
            #map-canvas {
                height: 100%;
                margin: 0px;
                padding: 0px
            }
            
            #panel {
                position: absolute;
                top: 5px;
                left: 50%;
                margin-left: -180px;
                z-index: 5;
                background-color: #fff;
                padding: 5px;
                border: 1px solid #999;
            }
            
            #bar {
                width: 240px;
                background-color: rgba(255, 255, 255, 0.75);
                margin: 8px;
                padding: 4px;
                border-radius: 4px;
            }
            
            #autoc {
                width: 100%;
                box-sizing: border-box;
            }
            
            #control input {
                //display: block;
                margin: 15px 10px;
                padding: 5px 10px;
                background: rgba(0, 0, 0, 0.7);
                color: white;
                //outline: 0 !important;
                border: 0;
                border-radius: 2px;
            }
        </style>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
        <!--
    <script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js"></script>
    <script src="http://www.openstreetmap.org/openlayers/OpenStreetMap.js"></script>-->
        <script src="../kmltoosm.js"></script>
        <script src="https://maps.googleapis.com/maps/api/js?&signed_in=true&libraries=drawing,places"></script>
        <script type="text/javascript" src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/routeboxer/src/RouteBoxer.js"></script>
        <script src="map_script.js"></script>
        <script>
            
            var row=<?php echo $row ?>;
            var col=<?php echo $col ?>;
            var mapZoom=<?php echo $zoom ?>;
            
            function initialize() {
                var sw = new google.maps.LatLng(<?php echo $sw[0] ?>, <?php echo $sw[1]?>);
                var ne = new google.maps.LatLng(<?php echo $ne[0] ?>, <?php echo $ne[1]?>);

                var lat_center = (sw.lat() + ne.lat()) / 2,
                    lng_center = (sw.lng() + ne.lng()) / 2;

                initMapCenter = new google.maps.LatLng(lat_center, lng_center);

                var mapOptions = {
                    center: initMapCenter,
                    zoom: mapZoom,
                    scaleControl: true
                };

                var map_canvas = document.getElementById('map-canvas');
                map = new google.maps.Map(map_canvas, mapOptions);

                var overlay = new google.maps.OverlayView();
                overlay.draw = function () {};
                overlay.setMap(map);

                map.controls[google.maps.ControlPosition.RIGHT_TOP].push(
                    document.getElementById('bar'));
                var autocomplete = new google.maps.places.Autocomplete(
                    document.getElementById('autoc'));
                autocomplete.bindTo('bounds', map);
                autocomplete.addListener('place_changed', function () {
                    var place = autocomplete.getPlace();
                    if (place.geometry.viewport) {
                        map.fitBounds(place.geometry.viewport);
                    } else {
                        map.setCenter(place.geometry.location);
                        map.setZoom(15);
                    }
                });

                var drawingManager = new google.maps.drawing.DrawingManager({
                    drawingMode: google.maps.drawing.OverlayType.MARKER,
                    drawingControl: true,
                    drawingControlOptions: {
                        position: google.maps.ControlPosition.TOP_CENTER,
                        drawingModes: [google.maps.drawing.OverlayType.RECTANGLE]
                    },
                    rectangleOptions: {
                        fillColor: '#0000FF',
                        fillOpacity: 0.1,
                        strokeWeight: 2,
                        clickable: false,
                        editable: true
                    }
                });

                var control = document.getElementById('control');
                control.style.display = 'block';
                map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(control);

                var btn_ss = document.getElementById("btn-ss");
                btn_ss.onclick = startSimulation;

                var btn_sd = document.getElementById("btn-sd");
                btn_sd.onclick = function () {
                    stopDirections = true;
                };

                var btn_sp = document.getElementById("btn-sp");
                btn_sp.onclick = function () {
                    var filechooser = document.getElementById("file");
                    filechooser.onchange = function (event) {
                        if (event.target.files && event.target.files[0]) {
                            //kmltoosm(event.target.files[0]);
                            var reader = new FileReader();
                            reader.onload = function () {
                                var dataURL = reader.result;
                                console.log(reader.result);
                            };
                            reader.readAsText(event.target.files[0]);
                        }
                    };
                    filechooser.click();
                }

                var rectangle = new google.maps.Rectangle({
                    fillColor: '#0000FF',
                    fillOpacity: 0.1,
                    strokeWeight: 2,
                    map: map,
                    bounds: new google.maps.LatLngBounds(sw, ne)
                });

                //sendCoord(sw, ne);
                //calcMaxBoundaries(sw);
                //calcMaxBoundaries(ne);
                makeGrid(sw, ne);
            }

            google.maps.event.addDomListener(window, 'load', initialize);
        </script>
    </head>

    <body>
        <div id="map-canvas"></div>
        <div id="bar">
            <p class="auto">
                <input type="text" id="autoc" />
            </p>
        </div>
        <div id="control">
            <!--<input id="btn-db" type="button" value="Write DB"/>-->
            <input id="btn-dr" type="button" value="Draw Rectangle">
            <input id="btn-direction" type="button" value="Extract WKT">
            <input id="btn-sd" type="button" value="Stop Directions">
            <!--<input id="btn-kmltoosm" type="button" value="KML to OSM">-->
            <input id="btn-ss" type="button" value="Start Simulation">
            <input id="btn-sp" type="button" value="Shelter Points File">
            <input id="file" type="file" style="visibility:hidden;display:none">
            <!--<input id="btn-dm" type="button" value="Distance Matrix">-->


        </div>
    </body>

    </html>