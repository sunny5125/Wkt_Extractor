var apiKey = 'AIzaSyCqi5sXaHEQeNutMf2EpOb87mVyiWW5ndY';
var apikeys = ['AIzaSyCqi5sXaHEQeNutMf2EpOb87mVyiWW5ndY', 'AIzaSyAAe-scFiDk6a1tjn2LuGhbr1-rQU4XXw0', 'AIzaSyB-CAwY9h4-BcUJFc0L9vkd-XVXs8GSC_Y', 'AIzaSyAr1Emq9B_NURCjfAlEGQSyw1juILocuUM'];
var apikeyindex = 0;
var apikeylength = 4;
var l = [2];
var ln = [2];
var map;
var point;
var lat;
var lng;
var lat_new, lng_new;
var latlng;
var distance = 6;
var coord;
var coordinate = [];
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var reqCount = 0;
//var kmlpoints = [];

var stopDirections = false;

var routeboxer = new RouteBoxer();

addPathToFile(true);

var bounds;

var initMapCenter = new google.maps.LatLng(23.5491169, 87.2909367);

var onekmlng = 0.01;

function nextMakeGrid(sw, ne) {
    var new_sw = sw;
    var lng = sw.lng() + onekmlng;
    lng = lng > original_ne.lng() ? original_ne.lng() : lng;
    var new_ne = new google.maps.LatLng(ne.lat(), lng);
    recMakeGrid(new_sw, new_ne);
}

var stichedges = [];

var rec_topleft;
var recInfoWindow

function createRecInfoWindow(topleft) {

    recInfoWindow = new google.maps.InfoWindow({
        content: "Hello",
        disableAutoPan: true
    });
    // Set the info window's content and position.
    //recInfoWindow.setContent();
    recInfoWindow.setPosition(topleft);
    recInfoWindow.open(map);
}

function showRecInfoWindow(sw, ne) {
    var topleft = new google.maps.LatLng(ne.lat(), sw.lng());
    var width = calcDistanceInMeters(topleft.lat(), topleft.lng(), ne.lat(), ne.lng()),
        height = calcDistanceInMeters(topleft.lat(), topleft.lng(), sw.lat(), sw.lng());
    width = width / 1000;
    height = height / 1000;
    console.log(width.toFixed(2) + " " + height.toFixed(2));
    var contentString = '<b>Rectangle Area</b><br><br>' +
        'Width: ' + width.toFixed(2) + 'km<br>' +
        'Height: ' + height.toFixed(2) + 'km';

    recInfoWindow.setContent(contentString);
    recInfoWindow.setPosition(topleft);
}

var can_draw_rectangle = false,
    drawing_rectangle = false,
    rectangle_complete = false;

var original_sw, original_ne,
    curr_sw, curr_ne;

function recMakeGrid(sw, ne) {
    curr_sw = sw;
    curr_ne = ne;

    makeGrid(sw, ne);
    var rectangle = new google.maps.Rectangle({
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        map: map,
        bounds: new google.maps.LatLngBounds(sw, ne)
    });
}

function calcDistanceMatrix() {
    avg_speed = prompt("Average Speed (Km/hr)?");
    if (avg_speed == null)
        return false;

    avg_speed = parseInt(avg_speed);
    time_matrix = [];
    for (var i = 0; i < dbmarkers.length; i++) {
        time_matrix.push(Array(dbmarkers.length));
        time_matrix[i][i] = 0;
    }
    console.log(time_matrix);
    distmatrix(0, 1);
    return true;
}

var avg_speed;
var time_matrix = [];

function write_distmatrix() {
    var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("POST", "write_matrix.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    console.log(time_matrix);
    var time = [];
    time.push(dbmarkers.length);
    time.push("");
    for (var i = 0; i < time_matrix.length; i++) {
        var row = time_matrix[i].join(" ");
        time.push(row);
    }

    var params = "time=" + encodeURIComponent(time.join("\n"));

    console.log(params);
    xmlhttp.send(params);
}

function distmatrix(i, j) {
    if (j >= dbmarkers.length) {
        i++;
        if (i >= dbmarkers.length - 1) {
            write_distmatrix();
            return;
        }
        j = i + 1;
    }
    var request = {
        origin: dbmarkers[i].getPosition(),
        destination: dbmarkers[j].getPosition(),
        provideRouteAlternatives: false,
        travelMode: google.maps.TravelMode.WALKING
    };
    setLastRequestTime();
    directionsService.route(request, function (response, status) {
        if (status == google.maps.DirectionsStatus.OK) {

            var dirRoute = response.routes[0];
            var distanceinm = dirRoute.legs[0].distance.value;
            var distanceInKm = distanceinm / 1000;
            var duration = parseFloat(dirRoute.legs[0].duration.value) / 60;

            var value = Math.floor(parseFloat(duration));
            time_matrix[i][j] = time_matrix[j][i] = parseInt((distanceInKm / avg_speed) * 60);
            console.log((i + 1) + " " + (j + 1) + " " + distanceinm + " m");
            console.log((i + 1) + " " + (j + 1) + " " + duration + " mins");
            setTimeout(function () {
                distmatrix(i, j + 1);
            }, getTimeout());
        } else
            setTimeout(function () {
                distmatrix(i, j);
            }, getTimeout());
    });
}

var dbmarkers = [];
var dbcircles = [];
var pixels = [];

function addDB(marker) {

    calcMaxBoundaries(marker.getPosition());

    dbmarkers.push(marker);
    marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_spin&chld=0.9|0|FFFF42|11|b|db" + (dbmarkers.length));

    var populationOptions = {
        strokeColor: '#000000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#FFDD00',
        fillOpacity: 0.4,
        map: map,
        center: marker.getPosition(),
        radius: 100
    };
    // Add the circle for this city to the map.
    var circle = new google.maps.Circle(populationOptions);
    dbcircles.push(circle);

    /*
    $.ajax({
        url: "https://roads.googleapis.com/v1/snapToRoads" + "?interpolate=true" + "&key=" + apiKey + "&path=" + coord,
        success: function (data) {
            if (!jQuery.isEmptyObject(data)) {
                var lat_snapped = data.snappedPoints[0].location.latitude,
                    lng_snapped = data.snappedPoints[0].location.longitude,
                    latlng = new google.maps.LatLng(lat_snapped, lng_snapped);
                var marker = new google.maps.Marker({
                    position: latlng,
                    map: map,
                    icon: "http://chart.apis.google.com/chart?chst=d_map_spin&chld=0.9|0|FFFF42|11|b|db" + (dbmarkers.length + 1)
                });
                //coordinate.push(latlng);
                //pixels.push(latlngToPoint(map, latlng, map.getZoom()));
                //dbmarkers.push(marker);
            }
        },
        async: true
    });*/
}

function writedbs() {
    var data = [];

    var sw = new google.maps.LatLng(mapMinLat, mapMinLon);
    data.push(sw.lng() + "," + sw.lat() + ",1.0");
    for (var i = 0, n = dbmarkers.length; i < n; i++) {
        var latlng = dbmarkers[i].getPosition();
        var line = latlng.lng() + "," + latlng.lat() + ",1.0";
        data.push(line);
    }

    //console.log(data);
    //return;

    var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("POST", "writedb.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var params = "data=" + encodeURIComponent(data.join(" "));
    //var params = "data=" + encodeURIComponent(line);
    params += "&start=" + data[0];
    params += "&end=" + data[1];
    xmlhttp.send(params);
}

var imageZoomLevel;

function startMapImageRequestFull() {
    var sw = new google.maps.LatLng(mapMinLat, mapMinLon),
        ne = new google.maps.LatLng(mapMaxLat, mapMaxLon),
        pixelCoordinatesw = latlngToPoint(map, sw, map.getZoom()),
        pixelCoordinatene = latlngToPoint(map, ne, map.getZoom()),
        width = Math.floor(pixelCoordinatene.x) - Math.floor(pixelCoordinatesw.x),
        height = Math.floor(pixelCoordinatesw.y) - Math.floor(pixelCoordinatene.y);
    var offsetx = pixelCoordinatesw.x,
        offsety = pixelCoordinatene.y;

    imageZoomLevel = map.getZoom();

    recSaveImageReq(offsetx, offsety, 0, 0, width, height);

    writedbs();

    //alert("The Map has been Extracted");
    console.log("The Map has been Extracted");
    console.log("REQUEST COUNT " + reqCount);

    sendBackKML();
}

function sendBackKML() {
    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("POST", "send_back_kml.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var params = "minlat=" + mapMinLat;
    params += "&maxlat=" + mapMaxLat;
    params += "&minlng=" + mapMinLon;
    params += "&maxlng=" + mapMaxLon;
    params += "&row=" + row;
    params += "&col=" + col;
    
    params+="&brow="+JSON.stringify(bottomrow);
    params+="&rcol="+JSON.stringify(rightcol);

    /*
    for (var i = 0; i < bottomrow.length; i++) {
        params += "&brow[]=" + (bottomrow[i] == null ? "null" : bottomrow[i]);
    }
    for (var i = 0; rightcol.length; i++) {
        params += "&rcol[]=" + (rightcol[i] == null ? "null" : rightcol[i]);
    }*/

    console.log(params);
    xmlhttp.send(params);
}

function startMapImageRequest(rectangle) {
    var bounds = rectangle.getBounds(),
        sw = bounds.getSouthWest(),
        ne = bounds.getNorthEast(),
        pixelCoordinatesw = latlngToPoint(map, sw, map.getZoom()),
        pixelCoordinatene = latlngToPoint(map, ne, map.getZoom()),
        width = Math.floor(pixelCoordinatene.x) - Math.floor(pixelCoordinatesw.x),
        height = Math.floor(pixelCoordinatesw.y) - Math.floor(pixelCoordinatene.y);
    var offsetx = pixelCoordinatesw.x,
        offsety = pixelCoordinatene.y;

    recSaveImageReq(offsetx, offsety, 0, 0, width, height);
}

function recSaveImageReq(offsetx, offsety, x, y, tw, th) {

    if (x >= tw) {
        x = 0;
        y += 640;
        if (y >= th) {
            return;
        }
    }
    var width = tw - x > 640 ? 640 : tw - x,
        height = th - y > 640 ? 640 : Number(th - y);

    var pointne = {
            x: offsetx + x + width,
            y: offsety + y
        },
        pointsw = {
            x: offsetx + x,
            y: offsety + y + height
        },
        ne = pointToLatLng(map, pointne, map.getZoom()),
        sw = pointToLatLng(map, pointsw, map.getZoom());

    var centerlat = (sw.lat() + ne.lat()) / 2,
        centerlng = (sw.lng() + ne.lng()) / 2,
        imgurl = "https://maps.googleapis.com/maps/api/staticmap?center=" + centerlat + "," + centerlng + "&zoom=" + map.getZoom() + "&size=" + width + "x" + height + "&maptype=roadmap" + "&format=jpg";

    console.log(imgurl);

    //snapimg(offsetx, offsety, x + width, y, tw, th);

    // sending url to php file to save image as jpg
    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);

            recSaveImageReq(offsetx, offsety, x + width, y, tw, th);
        }
    }
    xmlhttp.open("POST", "saveimage.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var params = "imgurl=" + encodeURIComponent(imgurl);
    params += "&tw=" + tw;
    params += "&th=" + th;
    params += "&width=" + width;
    params += "&height=" + height;
    params += "&x=" + x;
    params += "&y=" + y;
    xmlhttp.send(params);
}
var latlngToPoint = function (map, latlng, z) {
    var normalizedPoint = map.getProjection().fromLatLngToPoint(latlng); // returns x,y normalized to 0~255
    var scale = Math.pow(2, z);
    var pixelCoordinate = new google.maps.Point(normalizedPoint.x * scale, normalizedPoint.y * scale);
    return pixelCoordinate;
};
var pointToLatLng = function (map, point, z) {
    var scale = Math.pow(2, z);
    var normalizedPoint = new google.maps.Point(point.x / scale, point.y / scale);
    var latlng = map.getProjection().fromPointToLatLng(normalizedPoint);
    return latlng;
};

function sendCoord(sw, ne) {
    //var maxlat, minlat;
    //var maxlon, minlon;
    var lat;
    var lng;

    l[0] = Number(sw.lat());
    l[1] = Number(ne.lat());
    ln[0] = Number(sw.lng());
    ln[1] = Number(ne.lng());

    // for latitude 
    if (l[0] > l[1]) {
        maxlat = l[0];
        minlat = l[1];
    } else {
        maxlat = l[1];
        minlat = l[0];
    }

    //for longitude
    if (ln[0] > ln[1]) {
        maxlon = ln[0];
        minlon = ln[1];
    } else {
        maxlon = ln[1];
        minlon = ln[0];
    }

    bounds = new google.maps.LatLngBounds(sw, ne);

    /*mapMinLat = minlat;
    mapMinLon = minlon;
    mapMaxLat = maxlat;
    mapMaxLon = maxlon;*/

    /*for (lat = minlat, lng = minlon; lat < maxlat; lat += 0.0001) {
        runSnapToRoad(lat, lng);
    }
    for (lat = maxlat, lng = minlon; lng < maxlon; lng += 0.0001) {
        runSnapToRoad(lat, lng);
    }
    for (lat = maxlat, lng = maxlon; lat > minlat; lat -= 0.0001) {
        runSnapToRoad(lat, lng);
    }
    for (lat = minlat, lng = maxlon; lng > minlon; lng -= 0.0001) {
        runSnapToRoad(lat, lng);
    }*/
    //recRunSnapToRoad(minlat, minlon, "left");
}

var maxlat, minlat, maxlon, minlon,
    dinterval = 0.0001;

var mapMinLat = null,
    mapMaxLat = null,
    mapMinLon = null,
    mapMaxLon = null;

function recRunSnapToRoad(lat, lng, side) {
    var coord = new google.maps.LatLng(lat, lng);
    coord = coord.toUrlValue();

    $.ajax({
        url: "https://roads.googleapis.com/v1/snapToRoads" + "?interpolate=true" + "&key=" + apikeys[apikeyindex] + "&path=" + coord,
        success: function (data) {
            if (!jQuery.isEmptyObject(data))
                processSnapToRoadResponse(data, lat, lng);
            switch (side) {
            case "left":
                lat += dinterval;
                if (lat >= maxlat) {
                    side = "top";
                    lat = maxlat;
                    lng = minlon;
                }
                break;
            case "top":
                lng += dinterval;
                if (lng >= maxlon) {
                    side = "right";
                    lat = maxlat;
                    lng = maxlon;
                }
                break;
            case "right":
                lat -= dinterval;
                if (lat <= minlat) {
                    side = "bottom";
                    lat = minlat;
                    lng = maxlon;
                }
                break;
            case "bottom":
                lng -= dinterval;
                if (lng <= minlon) {
                    side = "stop";
                    removeClustering();
                    console.log("BORDER SNAPPED: " + coordinate.length);
                    //var r = confirm("Do you want to add directions?");
                    //if (r == true) {
                    addDirection();
                    //floodFilling();
                    //}
                }
                break;
            }

            if (side != "stop")
                recRunSnapToRoad(lat, lng, side);
        },
        complete: function (e) {
            // too many request
            if (e.status == 429) {
                apikeyindex = (apikeyindex + 1) % apikeylength;
                if (side != "stop")
                    recRunSnapToRoad(lat, lng, side);
            }
        },
        async: true
    });
}

function runSnapToRoad(lat1, lng1) {

    //alert("in runSnapto Road");
    var coord = new google.maps.LatLng(lat1, lng1);
    coord = coord.toUrlValue();
    //console.log("coord"+coord)

    //console.log(lat1+","+lng1);
    $.ajax({
        url: "https://roads.googleapis.com/v1/snapToRoads" + "?interpolate=true" + "&key=" + apikeys[apikeyindex] + "&path=" + coord,
        success: function (data) {
            if (!jQuery.isEmptyObject(data))
                processSnapToRoadResponse(data, lat1, lng1);
        },
        complete: function (e) {
            // too many request
            if (e.status == 429) {
                apikeyindex = (apikeyindex + 1) % apikeylength;
                processSnapToRoadResponse(data, lat1, lng1);
            }
        },
        async: true
    });

    /**$.get('https://roads.googleapis.com/v1/snapToRoads', {
            interpolate: true,
            key: apiKey,
            path: coord
        }, function(data) {
            processSnapToRoadResponse(data);
       });
    **/
}

function processSnapToRoadResponse(data, lat1, lng1) {
    console.log(data);
    lat_new = data.snappedPoints[0].location.latitude;
    lng_new = data.snappedPoints[0].location.longitude;
    console.log("on road" + lat_new + "," + lng_new);
    lat_new = Number(lat_new);
    lng_new = Number(lng_new);
    calDistance(lat1, lng1);
}

function calDistance(lat1, lng1) {
    latlng = new google.maps.LatLng(lat1, lng1);

    //alert("in calDiatance");
    var R = 6371000; // Radius of the earth in metres

    var dLat = deg2rad(lat_new - lat1); // deg2rad below
    var dLon = deg2rad(lng_new - lng1);
    //console.log("lat "+(lat_new - lat1));
    //console.log("lng "+(lng_new - lng1));
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat_new)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = Number(R * c); // Distance in metres

    if (d < 6) {
        //alert("alert kyun nhi aya");
        //console.log(latlng);
        coordinate.push(latlng);
        console.log("coordinate=" + coordinate);

        /*var marker = new google.maps.Marker({
            position: latlng,
            map: map,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                fillColor: "blue",
                fillOpacity: 0.8,
                scale: 1.5,
                strokeColor: "blue"
            },
            animation: google.maps.Animation.DROP,
            title: 'Snapped to road',
            visible: true
        });*/
        var marker = placeColorMarker(latlng, "red");
        markers[latlng] = marker;
    }

    //console.log("distance"+d);
}
var markers = {};

function deg2rad(deg) {
    return deg * (Math.PI / 180)
}

function calcDistanceInMeters(lat1, lng1, lat2, lng2) {
    //alert("in calDiatance");
    var R = 6371000; // Radius of the earth in metres

    var dLat = deg2rad(lat2 - lat1); // deg2rad below
    var dLon = deg2rad(lng2 - lng1);
    //console.log("lat "+(lat_new - lat1));
    //console.log("lng "+(lng_new - lng1));
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = Number(R * c); // Distance in metres
    return d;
}

function addDirection() {
    var count = 0;

    rec(0, 1);

    /*for(var x=0;x<coordinate.length;x++)
            {
	           for(var y=x+1;y<coordinate.length;y++)
	           {
		          reqDirection(coordinate[x],coordinate[y]);
	           }
           }*/
}

function removeClustering() {
    for (var i = coordinate.length - 1; i >= 1; i--) {
        var d = calcDistanceInMeters(coordinate[i - 1].lat(), coordinate[i - 1].lng(), coordinate[i].lat(), coordinate[i].lng());
        console.log(d);
        if (d < 11.2) {
            console.log("removed");
            markers[coordinate[i]].setMap(null);
            coordinate.splice(coordinate.indexOf(coordinate[i]), 1);
        }
    }
}

var lastRequestTime;

function rec(x, y) {
    if (y >= coordinate.length) {
        x++;
        if (x >= coordinate.length - 1) {
            console.log("DIRECTIONS COMPLETE");
            startMapImageRequestFull();
            //startSimulation();
            //floodFilling();
            return;
        }
        y = x + 1;
    }
    reqDirection(coordinate[x], coordinate[y], x, y);
    /*setTimeout(function () {                
    }, 700);*/
}

function reqDirection(source, dest, x, y) {
    setLastRequestTime();
    var request = {
        origin: source,
        destination: dest,
        provideRouteAlternatives: true,
        travelMode: google.maps.TravelMode.WALKING
    };
    directionsService.route(request, function (response, status) {
        if (stopDirections) {
            startMapImageRequestFull();
            return;
        }

        console.log(status);
        if (status == google.maps.DirectionsStatus.OK) {
            //console.log(response);
            //count++;
            console.log(response.routes.length);
            var routesLength = response.routes.length;
            /*for (var i = 0, len = ; i < len; i++) {
            }*/
            processRoutes(0, response, x, y);
        } else {
            setTimeout(function () {
                reqDirection(source, dest, x, y);
            }, getTimeout());
        }
    });
}

var group_colors = ['black', 'blue', 'red', 'green'];

function startSimulation() {

    var latency = prompt("Enter Latency");
    if (latency == null)
        return;

    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
            /*var groups_array=JSON.parse(xmlhttp.responseText);
            console.log(groups_array);
            for(var i=0;i<groups_array.length;i++) {
                var group=groups_array[i];
                for(var j=0;j<group.length;j++) {
                    console.log(j+" "+i);
                    dbcircles[group[j]].setOptions({
                        fillColor: group_colors[i]
                    });
                }
            }*/
        }
    }
    xmlhttp.open("POST", "simulate.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    var centerlat = (mapMinLat + mapMaxLat) / 2;
    console.log("centerlat: " + centerlat);

    var params = "map_zoom=" + imageZoomLevel;
    params += "&centerlat=" + encodeURIComponent(centerlat);
    params += "&latency=" + latency;

    console.log(params);

    xmlhttp.send(params);
}

//var latVisited = [],
//    lngVisited = [];
var allboxes = [];

function timeDifferenceinms(time1, time2) {
    var difference = time1 - time2;
    return difference;
}

var dirTimeoutInterval = 800,
    boxsize = 0.001;

function processRoutes(index, response, x, y) {
    if (index >= response.routes.length) {
        console.log("routes end " + x + " " + y);
        setTimeout(function () {
            rec(x, y + 1);
        }, getTimeout());

        return;
    }
    console.log("routeindex " + index);

    var kmlpoints = [],
        kmlroute = [],
        overlappedlatlngs = [];
    var directionsDisplay = new google.maps.DirectionsRenderer({
        map: map,
        directions: response,
        preserveViewport: true,
        routeIndex: index,
        suppressMarkers: true,
        suppressPolylines: true
    });

    var overlapped = false,
        lastOverlappedPoint = null,
        kmllatlngs = [];
    var dirroute = response.routes[index].overview_path;
    for (var j = 0, n = dirroute.length; j < n; j++) {
        var latlng = dirroute[j];
        overlapped = checkAllboxContains(latlng);

        if (overlapped) {
            overlappedlatlngs.push(latlng);

            kmllatlngs.push(latlng);
            kmlroute.push(latlng.lng() + ',' + latlng.lat() + ',1.0'); // pushing end point of route
            if (overlappedlatlngs.length == 1 && kmlroute.length > 1) {
                kmlpoints.push(kmlroute);
                if (kmllatlngs.length == 1)
                    placeColorMarker(kmllatlngs[0], "blue")
                else if (kmllatlngs.length > 1)
                    drawPolyline(kmllatlngs, "blue");
            }
            kmllatlngs = [latlng];
            kmlroute = [latlng.lng() + ',' + latlng.lat() + ',1.0']; // potential start point of route
        } else {

            calcMaxBoundaries(latlng);

            if (overlappedlatlngs.length == 1) {
                placeColorMarker(overlappedlatlngs[0], "red");
            } else if (overlappedlatlngs.length > 1) {
                drawPolyline(overlappedlatlngs, "red");
            }

            overlappedlatlngs = [];

            kmllatlngs.push(latlng);
            kmlroute.push(latlng.lng() + ',' + latlng.lat() + ',1.0');
        }
        //latlngs.push(latlng);
        //kmlpoints.push(latlng.lng() + ',' + latlng.lat() + ',1.0');
    }
    if (!overlapped && kmlroute.length > 1) {
        kmlpoints.push(kmlroute);
        drawPolyline(kmllatlngs, "blue");
    }

    if (kmlpoints.length > 0) {
        var boxes = routeboxer.box(dirroute, boxsize);
        addToAllBoxes(boxes);
        //if (!overlapped) {
        console.log("paths to file " + kmlpoints.length);
        addPathToFile(false, kmlpoints, index, response, x, y, 0);
    } else {
        /*for (var i = 0, n = latlngs.length; i < n; i++) {
            placeColorMarker(latlngs[i], "orange");
        }*/
        //drawPolyline(latlngs);
        processRoutes(index + 1, response, x, y);
    }
}

function calcMaxBoundaries(latlng) {
    if (mapMaxLat == null) {
        mapMinLat = latlng.lat();
        mapMinLon = latlng.lng();
        mapMaxLat = latlng.lat();
        mapMaxLon = latlng.lng();
    } else {
        var lat = latlng.lat(),
            lng = latlng.lng();

        if (lat < mapMinLat)
            mapMinLat = lat;
        else if (lat > mapMaxLat)
            mapMaxLat = lat;

        if (lng < mapMinLon)
            mapMinLon = lng;
        else if (lng > mapMaxLon)
            mapMaxLon = lng;
    }
}

function addToAllBoxes(boxes) {
    for (var b = 0; b < boxes.length; b++) {
        allboxes.push(boxes[b]);
        //drawBoxes(boxes);
    }
}

function checkAllboxContains(latlng) {
    for (var b = 0, bl = allboxes.length; b < bl; b++) {
        if (allboxes[b].contains(latlng)) {
            return true;
        }
    }
    return false;
}

function drawPolyline(latlngs, color) {
    var flightPath = new google.maps.Polyline({
        path: latlngs,
        geodesic: true,
        strokeColor: color,
        strokeOpacity: 1.0,
        strokeWeight: 2
    });

    flightPath.setMap(map);
}

function addPathToFile(open, kmlpoints, index, response, x, y, kmlindex) {
    if (!open && kmlindex >= kmlpoints.length) {
        processRoutes(index + 1, response, x, y);
        return;
    }
    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
            if (open == false) {
                addPathToFile(open, kmlpoints, index, response, x, y, kmlindex + 1);
            }
        }
    }
    xmlhttp.open("POST", "write.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    var params;
    if (open) {
        params = "value=open";
    } else {
        var kmlroute = kmlpoints[kmlindex];
        params = "value=" + kmlroute.join(" ");
        params += "&start=" + kmlroute[0];
        params += "&end=" + kmlroute[kmlroute.length - 1];
    }
    xmlhttp.send(params);
}

function drawBoxes(boxes) {
    var boxpolys = new Array(boxes.length);
    for (var i = 0; i < boxes.length; i++) {
        boxpolys[i] = new google.maps.Rectangle({
            bounds: boxes[i],
            fillOpacity: 0,
            strokeOpacity: 1.0,
            strokeColor: '#000000',
            strokeWeight: 1,
            map: map
        });
    }
}

var q = [];
//var point_interval = 0.00025;
var point_interval = 0.001;
var regionrows, regioncols;

function region(sw, ne) {
    this.sw = sw;
    this.ne = ne;
    this.i = null;
    this.j = null;
    this.points = [];
    this.bounds = new google.maps.LatLngBounds(sw, ne);
    this.marker = null;
    this.contains = function (latlng) {
        return (this.bounds.contains(latlng));
    }
    this.push = function (latlng) {
        this.points.push(latlng);
    }
    this.hasPoint = function () {
        return this.points.length > 0;
    }
    this.setMarker = function (marker, i, j) {
        this.marker = marker;
        this.i = i;
        this.j = j;
    }
    this.traversed = false;
}

var regions = [],
    gridSnappedPoints = [],
    gridrows, gridcols;
var stitchPoints = [],
    stitchEdges = [];

function makeGrid(sw, ne) {

    sendCoord(sw, ne);

    var grid = [];

    if (gridSnappedPoints.length > 0) {
        //console.log(gridSnappedPoints);
        var hadpoints = stitchPoints.length > 0 ? true : false;
        for (var k = 0; k < gridSnappedPoints.length; k++) {
            stitchPoints.push(null);
            for (var l = gridSnappedPoints[k].length - 1; l >= 0; l--) {
                if (gridSnappedPoints[k][l] != null) {
                    if (hadpoints && stitchPoints[k] != null)
                        stitchEdges.push(new edge(stitchPoints[k], gridSnappedPoints[k][l]));
                    else
                        stitchPoints[k] = gridSnappedPoints[k][l];
                    break;
                }
            }
            //gridSnappedPoints[k].splice(0, gridSnappedPoints[k].length - 1)
        }
        //stitchPoints = gridSnappedPoints;
        //console.log(gridSnappedPoints);
    }

    regions = [];
    gridSnappedPoints = [];

    var i;
    //for (i = minlat + point_interval; i <= maxlat; i += point_interval) {
    for (i = minlat; i <= maxlat; i += point_interval) {
        var gridrow = [],
            regionrow = [],
            gridSnappedRow = [];

        var j;
        //for (j = minlon + point_interval; j <= maxlon; j += point_interval) {
        for (j = minlon; j <= maxlon; j += point_interval) {
            var latlng = new google.maps.LatLng(i, j);
            gridrow.push(latlng);

            var swgrid = new google.maps.LatLng(latlng.lat() - point_interval, latlng.lng() - point_interval);
            regionrow.push(new region(swgrid, latlng));

            gridSnappedRow.push(null);

            placeColorMarker(latlng, "blue");
        }
        grid.push(gridrow);
        gridSnappedPoints.push(gridSnappedRow);
        if (j != maxlon) {
            var ne = new google.maps.LatLng(i, maxlon),
                sw = new google.maps.LatLng(i - point_interval, j - point_interval);
            regionrow.push(new region(sw, ne));
        }
        regions.push(regionrow);
    }
    if (i != maxlat) {
        var regionrow = [];
        for (var j = minlon + point_interval; j <= maxlon; j += point_interval) {
            var ne = new google.maps.LatLng(maxlat, j),
                sw = new google.maps.LatLng(i - point_interval, j - point_interval);
            regionrow.push(new region(sw, ne));
        }
        if (j != maxlon) {
            var ne = new google.maps.LatLng(maxlat, maxlon),
                sw = new google.maps.LatLng(maxlat - point_interval, j - point_interval);
            regionrow.push(new region(sw, ne));
        }
        regions.push(regionrow);
    }

    console.log(grid);
    gridrows = grid.length;
    gridcols = (gridrows == 0 ? 0 : grid[0].length);

    regionrows = regions.length;
    regioncols = (regionrow == 0 ? 0 : regions[0].length);
    //console.log(rows+" "+cols);

    snapGrid(grid, 0, 0, gridrows, gridcols);

}

var innerSnappedCoordinates = [];
var optimizedVertexList = [];

function snapGrid(grid, i, j, rows, cols) {
    if (j >= cols) {
        j = 0;
        i++;
        if (i >= rows) {
            floodFilling();
            //recRunSnapToRoad(minlat, minlon, "left");
            return;
        }
    }
    console.log(i + " " + j);
    var coord = grid[i][j],
        coordUrlValue = coord.toUrlValue();
    $.ajax({
        url: "https://roads.googleapis.com/v1/snapToRoads" + "?interpolate=false" + "&key=" + apikeys[apikeyindex] + "&path=" + coordUrlValue,
        success: function (data) {
            if (!jQuery.isEmptyObject(data)) {
                var snapped_lat = data.snappedPoints[0].location.latitude;
                var snapped_lng = data.snappedPoints[0].location.longitude;
                var latlng = new google.maps.LatLng(snapped_lat, snapped_lng);
                gridSnappedPoints[i][j] = latlng;
                placeColorMarker(latlng, "orange");

                /*
                if (bounds.contains(latlng)) {
                    innerSnappedCoordinates.push(latlng);
                    gridSnappedPoints[i][j] = latlng;

                    var marker = placeColorMarker(latlng, "orange");

                    var ri = Math.floor((snapped_lat - grid[0][0].lat()) / point_interval) + 1,
                        rj = Math.floor((snapped_lng - grid[0][0].lng()) / point_interval) + 1;

                    /*if (regions[ri][rj].marker == null) {
                        regions[ri][rj].setMarker(marker, ri, rj);
                        optimizedVertexList.push(regions[ri][rj]);
                    } else {
                        marker.setMap(null);
                    }//

                    console.log(ri + " " + rj);
                    marker.setTitle(ri + " " + rj);
                }*/
            }
            j++;
            snapGrid(grid, i, j, rows, cols);
        },
        complete: function (e) {
            // too many request
            if (e.status == 429) {
                apikeyindex = (apikeyindex + 1) % apikeylength;
                snapGrid(grid, i, j, rows, cols);
            }
        },
        async: true
    });
}

function placeColorMarker(latlng, color) {
    var marker = new google.maps.Marker({
        position: latlng,
        map: map,
        icon: {
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: color,
            fillOpacity: 0.8,
            scale: 1.5,
            strokeColor: color
        },
        animation: google.maps.Animation.DROP,
        visible: true
    });
    return marker;
}

function getRoadSnapPoint(latlng) {
    coord = latlng.toUrlValue();
    $.ajax({
        url: "https://roads.googleapis.com/v1/snapToRoads" + "?interpolate=true" + "&key=" + apiKey + "&path=" + coord,
        success: function (data) {
            if (!jQuery.isEmptyObject(data)) {
                //processSnapToRoadResponse(data, lat1, lng1);
                lat_snapped = data.snappedPoints[0].location.latitude;
                lng_snapped = data.snappedPoints[0].location.longitude;
                console.log("SNAPPED on road" + lat_new + "," + lng_new);
            } else {
                // TO DO TO DO
            }
        },
        async: true
    });
}

function areAllTraverse(vertexList) {
    for (var i = 0, n = vertexList.length; i < n; i++) {
        if (!vertexList[i].traversed) {
            console.log(vertexList[i].i + " " + vertexList[i].j);
            return vertexList[i];
        }
    }
    return null;
}

var bottomrow = null,
    rightcol = null;

function floodFilling() {
    /*var root = areAllTraverse(optimizedVertexList);
    while (root != null) {
        bfs(root);
        root = areAllTraverse(optimizedVertexList);
    }*/

    /*var coords=[];
    for(var i=0;i<optimizedVertexList.length;i++) {
        coords.push(optimizedVertexList[i].marker.getPosition());
    }*/

    //coordinate = innerSnappedCoordinates;
    //addDirection();

    //recFlooding(0, 0);

    console.log(gridSnappedPoints);

    edges = [];
    //row wise
    for (var r = 0; r < gridrows; r++) {
        var source = null,
            c = 0;
        do {
            source = gridSnappedPoints[r][c++];

            //selecting bottom row
            if (bottomrow == null) {
                bottomrow = [source];
                /*for (var i = 0; i < gridcols; i++)
                    bottomrow.push(gridSnappedPoints[r][i]);
                */
            }
        } while (source == null && c < gridcols);

        for (; c < gridcols; c++) {
            var dest = gridSnappedPoints[r][c];
            if (dest != null) {
                newedge = new edge(source, dest)
                edges.push(newedge);
                source = dest;
            }
        }

    }

    //column wise
    for (var c = 0; c < gridcols; c++) {
        var source = null,
            r = 0;
        do {
            source = gridSnappedPoints[r++][c];

            //selecting right column
            rightcol = [source];
            /*for (var i = 0; i < gridrows; i++)
                rightcol.push(gridSnappedPoints[i][c]);
                */
        } while (source == null && r < gridrows);

        for (; r < gridrows; r++) {
            var dest = gridSnappedPoints[r][c];
            if (dest != null) {
                edges.push(new edge(source, dest));
                source = dest;
            }
        }
    }

    console.log("EDGES COUNT: " + edges.length);

    reqEdgesDirection(0, edges);
}

function reqEdgesDirection(edgeindex) {
    if (edgeindex >= edges.length) {

        /*if (curr_ne.lng() >= original_ne.lng())
            return;

        var new_sw = new google.maps.LatLng(curr_sw.lat(), curr_sw.lng() + onekmlng);
        nextMakeGrid(new_sw, curr_ne);*/

        startMapImageRequestFull();
        return;
    }

    console.log("edgeindex " + edgeindex);
    setLastRequestTime();
    var request = {
        origin: edges[edgeindex].from,
        destination: edges[edgeindex].to,
        provideRouteAlternatives: true,
        travelMode: google.maps.TravelMode.WALKING
    };
    directionsService.route(request, function (response, status) {
        if (stopDirections) {
            startMapImageRequestFull();
            return;
        }

        console.log(status);
        if (status == google.maps.DirectionsStatus.OK) {
            reqCount++;
            console.log(response.routes.length);
            var routesLength = response.routes.length;
            processEdgeResponse(0, response, edgeindex);
        } else if (status == google.maps.DirectionsStatus.ZERO_RESULTS) {
            reqEdgesDirection(edgeindex + 1);
        } else {
            setTimeout(function () {
                reqEdgesDirection(edgeindex);
            }, getTimeout());
        }
    });
}

function setLastRequestTime() {
    lastRequestTime = (new Date()).getTime();
}

function getTimeout() {
    var timestamp = (new Date()).getTime(),
        timediff = timeDifferenceinms(lastRequestTime, timestamp),
        timeout = timediff > dirTimeoutInterval ? 0 : dirTimeoutInterval - timediff;
    return timeout;
}

function processEdgeResponse(index, response, edgeindex) {
    if (index >= response.routes.length) {
        setTimeout(function () {
            reqEdgesDirection(edgeindex + 1);
        }, getTimeout());
        return;
    }

    var kmlpoints = [],
        latlngs = [];
    var directionsDisplay = new google.maps.DirectionsRenderer({
        map: map,
        directions: response,
        preserveViewport: true,
        routeIndex: index,
        suppressMarkers: true,
        suppressPolylines: true
    });

    var overlapped = false,
        overlappedlatlngs = [],
        lastOverlappedPoint = null,
        kmllatlngs = [],
        kmlroute = [];
    var dirroute = response.routes[index].overview_path;
    for (var j = 0, n = dirroute.length; j < n; j++) {
        var latlng = dirroute[j];
        overlapped = checkAllboxContains(latlng);

        if (overlapped) {
            overlappedlatlngs.push(latlng);

            kmllatlngs.push(latlng);
            kmlroute.push(latlng.lng() + ',' + latlng.lat() + ',1.0'); // pushing end point of route
            if (overlappedlatlngs.length == 1 && kmlroute.length > 1) {
                kmlpoints.push(kmlroute);
                if (kmllatlngs.length == 1)
                    placeColorMarker(kmllatlngs[0], "blue")
                else if (kmllatlngs.length > 1)
                    drawPolyline(kmllatlngs, "blue");
            }
            kmllatlngs = [latlng];
            kmlroute = [latlng.lng() + ',' + latlng.lat() + ',1.0']; // potential start point of route
        } else {

            calcMaxBoundaries(latlng);

            if (overlappedlatlngs.length == 1) {
                placeColorMarker(overlappedlatlngs[0], "red");
            } else if (overlappedlatlngs.length > 1) {
                drawPolyline(overlappedlatlngs, "red");
            }

            overlappedlatlngs = [];

            kmllatlngs.push(latlng);
            kmlroute.push(latlng.lng() + ',' + latlng.lat() + ',1.0');
        }
        //latlngs.push(latlng);
        //kmlpoints.push(latlng.lng() + ',' + latlng.lat() + ',1.0');
    }
    if (!overlapped && kmlroute.length > 1) {
        kmlpoints.push(kmlroute);
        drawPolyline(kmllatlngs, "blue");
    }

    if (kmlpoints.length > 0) {
        var boxes = routeboxer.box(dirroute, boxsize);
        addToAllBoxes(boxes);
        //if (!overlapped) {
        console.log("paths to file " + kmlpoints.length);
        //addPathToFile(false, kmlpoints, index, response, x, y, 0);
        addEdgeToFile(false, kmlpoints, index, response, edgeindex, 0);
    } else {
        /*for (var i = 0, n = latlngs.length; i < n; i++) {
            placeColorMarker(latlngs[i], "orange");
        }*/
        //drawPolyline(latlngs);
        //processRoutes(index + 1, response, x, y);
        processEdgeResponse(index + 1, response, edgeindex);
    }
}

function addEdgeToFile(open, kmlpoints, index, response, edgeindex, kmlindex) {

    if (!open && kmlindex >= kmlpoints.length) {
        processEdgeResponse(index + 1, response, edgeindex);
        return;
    }
    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
            if (open == false) {
                addEdgeToFile(open, kmlpoints, index, response, edgeindex, kmlindex + 1);
            }
        }
    }
    xmlhttp.open("POST", "write.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    var params;
    if (open) {
        params = "value=open";
    } else {
        var kmlroute = kmlpoints[kmlindex];
        params = "value=" + kmlroute.join(" ");
        params += "&start=" + kmlroute[0];
        params += "&end=" + kmlroute[kmlroute.length - 1];
    }
    xmlhttp.send(params);
}

function edge(from, to) {
    this.from = from;
    this.to = to;
}
var edges = [];

function bfs(root) {
    var lq = [];
    lq.push(root);
    root.traversed = true;
    while (lq.length > 0) {
        var tempreg = lq[0];
        tempreg.marker.setIcon({
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: "black",
            fillOpacity: 0.8,
            scale: 1.5,
            strokeColor: "black"
        });
        lq.splice(0, 1);
        var i = tempreg.i,
            j = tempreg.j;
        if (i + 1 >= regionrows || j + 1 >= regioncols) {
            //To Do- nothing!!!!     
        } else {
            if (regions[i + 1][j].marker != null && !regions[i + 1][j].traversed) {
                lq.push(regions[i + 1][j]);
                regions[i + 1][j].traversed = true;
                edges.push(new edge(tempreg.marker.getPosition(), regions[i + 1][j].marker.getPosition()));
                //addEdge(tempreg,regions[i+1][j]);
            } else if (regions[i][j + 1].marker != null && !regions[i][j + 1].traversed) {
                lq.push(regions[i][j + 1]);
                regions[i][j + 1].traversed = true;
                edges.push(new edge(tempreg.marker.getPosition(), regions[i][j + 1].marker.getPosition()));
                //addEdge(tempreg,regions[i][j+1]);
            } else if (regions[i + 1][j + 1].marker != null && !regions[i + 1][j + 1].traversed) {
                lq.push(regions[i + 1][j + 1]);
                regions[i + 1][j + 1].traversed = true;
                edges.push(new edge(tempreg.marker.getPosition(), regions[i + 1][j + 1].marker.getPosition()))
                    //addEdge(tempreg,regions[i+1][j+1]);
            }
        }
    }

    console.log("sesh hoche");
}

function addEdge(start, end) {
    //to-do 
}

function startServer(sw, ne) {
    var xmlhttp = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("POST", "server.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var params = "sw_lat=" + sw.lat();
    params += "&sw_lng=" + sw.lng();
    params += "&ne_lat=" + ne.lat();
    params += "&ne_lng=" + ne.lng();

    console.log(params);
    xmlhttp.send(params);
}