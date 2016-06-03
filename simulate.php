<?php

ini_set('max_execution_time', 0);

//ob_start();

$map_zoom=$_POST['map_zoom'];
$centerlat=$_POST['centerlat'];
$latency=$_POST['latency'];

//$centerlat=round((double)$centerlat,5);
//$diff=$centerlat-(double)0.38425;
//echo $map_zoom." round ".$centerlat." diff ".$diff;

$scalefactor_16=cos(deg2rad(abs($centerlat-0.3842485249971568)))*2.4;
$zoom=pow(2,16-$map_zoom)*$scalefactor_16;

//echo $scalefactor_16." ~ ".$zoom;

$lines = file("ONE_GUI\src\settings.txt");
$lines[157]="GUI.UnderlayImage.scale = ".round($zoom,2)."\n";
file_put_contents('ONE_GUI\src\settings.txt', $lines);

$lines = file("ONE_GUI\src\sample_settings.txt");
$lines[208]="GUI.UnderlayImage.scale = ".round($zoom,2)."\n";
file_put_contents('ONE_GUI\src\sample_settings.txt', $lines);

//========= CONVERTING KML TO OSM ==============

$output=shell_exec("python ogr2osm.py map.kml -f 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("python ogr2osm.py map_db.kml -f 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

//=========== PARSING AND REMOVING REDUNDANT NODES ==========
$output=shell_exec("python parseosm.py map.osm map.osm 2>&1");
echo "<pre>$output</pre>";

//=========== CONVERTING OSM TO WKT =================
$output=shell_exec("echo y | java -jar osm2wkt.jar map.osm");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("echo y | java -jar osm2wkt.jar map_db.osm");
echo "<pre>$output</pre>";
echo "<br>";

//=========== ADDING DB EDGES =======================
$output=shell_exec("python addedge.py map.osm.wkt map_db.osm.wkt 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("cd Algo123 && echo $latency | 1click.bat 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

$output=shell_exec("python parsepdm.py wifi_idb_mapping$latency.0 output$latency.0 9 \"Algo123/muletrajectory/Output/\" 2>&1");
echo "<pre>$output</pre>";
echo "<br>";

//=========== COPYING WKTS TO DATA\ ==========
$output=shell_exec("copy /y data.osm.wkt ONE_GUI\src\data");
echo "<pre>$output</pre>";
echo "<br>";
$output=shell_exec("copy /y data.osm.wkt ONE_GUI\src\Eone_1.4.1\data");
echo "<pre>$output</pre>";
echo "<br>";

//============ COPYING MAP IMAGE TO DATA\ ===========
$output=shell_exec("copy /y final_map.jpg ONE_GUI\src\data");
echo "<pre>$output</pre>";
echo "<br>";
$output=shell_exec("copy /y final_map.jpg ONE_GUI\src\Eone_1.4.1\data");
echo "<pre>$output</pre>";
echo "<br>";

//============= COPYING SETTINGS FILE ==================
$output=shell_exec("copy /y gui_input.txt ONE_GUI\src");
echo "<pre>$output</pre>";
echo "<br>";

/*ob_clean();

$lines = file("gui_input.txt");
$dbs=$lines[1];
$wifis=$lines[2+$dbs];

$first_wifi=$dbs*2;
$cluster_wifi=$lines[2+$dbs+1];
$cluster_wifi=explode(",",$cluster_wifi);
$groups = array();
$dbindex=0;
foreach ($cluster_wifi as $wifi) {
    $group_index=intval($wifi)-$first_wifi;
    if(isset($groups[$group_index]))
        array_push($groups[$group_index],$dbindex);
    else
        $groups[$group_index]=array($dbindex);
    $dbindex++;
    //field.groupCenters.get(groupindex).add(field.dbhosts.get(j));
}

echo json_encode($groups);*/

//============ LET IT RIP AAAAAAAAAAA==============

$output = shell_exec("cd ONE_GUI\src && java Start settings.txt 2>&1");
echo "<pre>$output</pre>";