<?php

ini_set("gd.jpeg_ignore_warning",1);
require("mergeimg.php");

$imgurl = $_POST['imgurl'];
$dst_x=$_POST['x'];
$dst_y=$_POST['y'];
$src_w=$_POST['width'];
$src_h=$_POST['height'];
$tw=$_POST['tw'];
$th=$_POST['th'];

//Get the file
$content = file_get_contents($imgurl);

//Store in the filesystem.
$fp = fopen("map_part.jpg", "w");
fwrite($fp, $content);
fclose($fp);

mergeimg("final_map.jpg","map_part.jpg",$dst_x,$dst_y,$src_w,$src_h,$tw,$th);


echo "success";