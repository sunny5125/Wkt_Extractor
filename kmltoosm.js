var output_strings = [];
var feature;
var write_feature;
var feature_strings = [];
var data = new FormData();
var xhr = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");

function kmltoosm(file) {
    feature = new OpenLayers.Format.KML();

    if (file) {
        var reader = new FileReader();
        reader.onload = function (evnt) {
            var output = evnt.target.result;
            var readText = reader.result;
            feature_strings = feature.read(readText); // reading the input here
            writeOSM(feature_strings);

        }; //end onload()
        reader.readAsText(file);

    } else {
        alert("FILE NOT FOUND");
    }
}

function writeOSM(feature) {
    write_feature = new OpenLayers.Format.OSM(); // writing into osm format here
    output_strings = write_feature.write(feature);
    data.append("data", output_strings);
    xhr.open('POST', 'kmlconverter.php', true);
    xhr.send(data);
}