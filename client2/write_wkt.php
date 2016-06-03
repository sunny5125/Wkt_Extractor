<?php

$data=$_POST['data'];

$file = fopen("map.wkt","w");
fwrite($file,$data);
fclose($file);

echo "success writing wkt";