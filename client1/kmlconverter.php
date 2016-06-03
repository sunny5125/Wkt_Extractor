<?php 
if(!empty($_POST['data']))
{
$data = $_POST['data'];
echo "inside php";

$file = fopen("data.osm",'a');//creates new file
fwrite($file,$data);
fclose($file);
}
?>