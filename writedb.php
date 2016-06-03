<?php

$value = $_POST["data"];

if ($value == "open") {
    //$write = new XMLWriter();
    //unlink("map_db.kml");
    //unlink("data.osm");
	//unlink("data.osm.wkt");
} else {
    $start = $_POST["start"];
    $end = $_POST["end"];
    $xml = simplexml_load_file("sample_kml.kml");
    //$xml = simplexml_load_file("map_db.kml");
    //$xml = simplexml_load_file("map.kml");
    $folder = $xml->Document->addChild("Folder");
    $name = $folder->addChild("name", "Directions from  xxx to yyy");
    //$name->innerXML("Directions from  xxx to yyy");
    $placemark = $folder->addChild("Placemark");
    $placemark->addchild("name", "Directions from  xxx to yyy");
    //$placemark->addChild("ExtendedData");
    
	$line = $placemark->addChild("LineString");
    $line->addChild("tessellate", "1"); //->innerXML("1");
    $line->addChild("coordinates", $value);
    
    
    $placemark = $folder->addChild("Placemark");
    $placemark->addchild("name", "xxx");
    $placemark->addChild("Description","xxx");
    $placemark->addChild("ExtendedData",'');
    $point=$placemark->addChild("Point");
    $point->addChild("coordinates",$start);
    
    $placemark = $folder->addChild("Placemark");
    $placemark->addchild("name", "yyy");
    $placemark->addChild("Description","yyyy");
    $placemark->addChild("ExtendedData");
    $point=$placemark->addChild("Point");
    $point->addChild("coordinates",$end);
    
    $xml->asXML("map_db.kml");
}

echo "success db points";