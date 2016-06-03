<?php

$value = $_POST["value"];

if ($value == "open") {
    //$write = new XMLWriter();
    
    unlink("map.kml");
    unlink("map_db.kml");
    unlink("data.osm");
	unlink("data.osm.wkt");
    
    $xml = simplexml_load_file("sample_kml.kml");
    $xml->asXML("map.kml");
    
    $dom = dom_import_simplexml($xml)->ownerDocument;
    $dom->formatOutput = true;
    $dom->save("notmap.kml");
} else {
    $start = $_POST["start"];
    $end = $_POST["end"];
    $xml       = simplexml_load_file("map.kml");
    $folder    = $xml->Document->addChild("Folder");
    $name      = $folder->addChild("name", "Directions from  xxx to yyy");
    //$name->innerXML("Directions from  xxx to yyy");
    $placemark = $folder->addChild("Placemark");
    $placemark->addchild("name", "Directions from  xxx to yyy");
    $placemark->addChild("ExtendedData");
    
	//$multigeometry=$placemark->addchild("Multigeometry");
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
    
    
    //$coord->innerXMl($value);
    $xml->asXML("map.kml");
    $dom = dom_import_simplexml($xml)->ownerDocument;
    $dom->formatOutput = true;
    $dom->save("notmap.kml");
    //echo $dom->saveXML();
    /*$dom                     = new DOMDocument('1.0');
    $dom->preserveWhiteSpace = false;
    $dom->formatOutput       = true;
    $dom->loadXML($xml->asXML());
    $dom->saveXML("map.kml");*/
}

//$mf=file_put_contents("map.kml",$value.PHP_EOL,FILE_APPEND

echo "success";