<?php

include_once("host.php");

$minlat=$_POST["minlat"];
$maxlat=$_POST["maxlat"];
$minlng=$_POST["minlng"];
$maxlng=$_POST["maxlng"];

$row=$_POST['row'];
$col=$_POST['col'];
$brow=json_decode($_POST['brow']);
$rcol=json_decode($_POST['rcol']);

global $host;
global $port;

set_time_limit(0);

$socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create socket\n");

$result = socket_connect($socket, $host, $port) or die("Could not connect to server\n");

//$message="AWESOME";
//$lines=file_get_contents("map.kml");

$xml = simplexml_load_file("map.kml");

$folders="";
foreach ($xml->Document->children() as $child) {
    $folders .= $child->asXML();
}

$array=["minlat"=>$minlat,
        "maxlat"=>$maxlat,
        "minlng"=>$minlng,
        "maxlng"=>$maxlng,
        "folders"=>$folders,
        "row"=>$row,
        "col"=>$col,
        "brow"=>$brow,
        "rcol"=>$rcol];

$message=json_encode($array);

$result = socket_write ($socket, $message, strlen($message)) or die("Could not write to server\n");

//socket_close($socket);

echo "$minlat $maxlat $minlng $maxlng";
//exit();
?>