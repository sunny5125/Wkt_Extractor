<?php

/*$output=shell_exec("python addedge.py map.osm.wkt map_db.osm.wkt");
echo "<pre>$output</pre>";
echo "<br>";*/

$output = shell_exec('python --version 2>&1');
echo "<pre>$output</pre>";

$output=shell_exec("python addedge.py map.osm.wkt map_db.osm.wkt 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("cd Algo123 && echo 120 | 1click.bat 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("python parsepdm.py wifi_idb_mapping120.0 output120.0 9 \"Algo123/muletrajectory/Output/\" 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

/*$output = shell_exec("dir");
echo "<pre>$output</pre>";*/