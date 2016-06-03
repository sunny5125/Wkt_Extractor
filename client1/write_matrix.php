<?php

$time = $_POST["time"];

if ($time)  {
    $file=fopen("Algo123/muletrajectory/Input/input.txt","w");
    fwrite($file,$time);
    fclose($file);
}

//$mf=file_put_contents("map.kml",$value.PHP_EOL,FILE_APPEND

echo $time;
echo "success writing matrix";