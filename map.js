var map;
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
//var clickedlocation = new Array(2);
var clickedlocation = [];
var index = 0;
var kmlpoints = [];
addPathToFile(true);

var rectangle;

function initialize() {
    var mapOptions = {
        zoom: 10,
        center: new google.maps.LatLng(23.456, 87.456)
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
        mapOptions);

    var control = document.getElementById('control');
    control.style.display = 'block';
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(control);

    var btn = document.getElementById("btn-direction");
    btn.onclick = addDirection;

    var btn_kmltoosm = document.getElementById("btn-kmltoosm");
    btn_kmltoosm.onclick = function () {
        var filechooser = document.getElementById("file");
        filechooser.onchange = function (event) {
            if (event.target.files && event.target.files[0]) {
                kmltoosm(event.target.files[0]);
            }
        };
        filechooser.click();
    }

    google.maps.event.addListener(map, 'click', function (event) {
        placeMarker(event.latLng);
    });

    /*var bounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(boundary_lat, boundary_long),
        new google.maps.LatLng(boundary_lat2, boundary_long2)
    );

    rectangle = new google.maps.Rectangle({
        bounds: bounds,
        editable: true,
        draggable: true
    });

    rectangle.setMap(map);*/

}

function addDirection() {
    // directionsDisplay = new google.maps.DirectionsRenderer();

    /*var lat1 = document.getElementById("lat1").value,
        lng1 = document.getElementById("lng1").value,
        lat2 = document.getElementById("lat2").value,
        lng2 = document.getElementById("lng2").value;*/
    // directionsDisplay.setMap(map);

  for(var x=0;x<clickedlocation.length;x++)
  {
	  for(var y=x+1;y<clickedlocation.length;y++)
	  {
	  
   var request = {
        origin: clickedlocation[x],
        destination: clickedlocation[y],
        provideRouteAlternatives: true,
        travelMode: google.maps.TravelMode.WALKING
    };
    directionsService.route(request, function (response, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            console.log(response);
            kmlpoints = [];
            console.log(response.routes.length);
            for (var i = 0, len = response.routes.length; i < len; i++) {
                new google.maps.DirectionsRenderer({
                    map: map,
                    directions: response,
                    preserveViewport: true,
                    routeIndex: i
                });

                console.log(i);

                //while(writing);



                var dirroute = response.routes[i];
                //var route = dirroute.overview_path;
                //alert(dirroute.overview_path[0]);
                for (var j = 0; j < dirroute.overview_path.length; j++) {
                    kmlpoints.push(dirroute.overview_path[j].lng() + ',' + dirroute.overview_path[j].lat() + ',0.0');
                }
            }

            //directionsDisplay.setDirections(response);
            addPathToFile(false);
        }
    });

	}
  }
}

var writing = false;

function addPathToFile(open) {
    /* if(writing)
         return;
     writing=true;*/

    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            console.log(xmlhttp.responseText);
            writing = false;
        }
    }
    xmlhttp.open("POST", "write.php", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    var params;
    if (open) {
        params = "value=open";
    } else {
        params = "value=" + kmlpoints.join(" ");
        params += "&start=" + kmlpoints[0];
        params += "&end=" + kmlpoints[kmlpoints.length - 1];
    }
    xmlhttp.send(params);
}

function placeMarker(location) {

    //clickedlocation[index] = location;
    //alert(clickedlocation[index]);
    //index = (index + 1) % 2;
	clickedlocation.push(location);
	console.log(clickedlocation);
    var marker = new google.maps.Marker({
        position: location,
        map: map
    });
}

google.maps.event.addDomListener(window, 'load', initialize);