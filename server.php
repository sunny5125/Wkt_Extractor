<?php

$host = "localhost";
$port = 12001;

set_time_limit(0);

/*
$pid=shell_exec("netstat -aon|grep :$port|tr -s [:space:] ' '|cut -d' ' -f6|tr -d '\n'");
if($pid!='\n') {
    $output=shell_exec("kill -f $pid");
}*/

$sw_lat=$_POST['sw_lat'];
$sw_lng=$_POST['sw_lng'];
$ne_lat=$_POST['ne_lat'];
$ne_lng=$_POST['ne_lng'];
$zoom=$_POST['zoom'];
$rows=$_POST['rows'];
$cols=$_POST['cols'];

/*
$ne_lat=23.55549010202876;
$ne_lng=87.29006767272949;
$sw_lat=23.55045456785048;
$sw_lng=87.28260040283203;
*/

function server_thread($sw,$ne) {
    
    global $host;
    global $port;
    global $zoom;
    global $rows;
    global $cols;
    
    //===============GRID CONFIG===================
    //$rows=1;
    //$cols=1;
    $noc=$rows*$cols;
    
    $grid=array();
    for($i=0;$i<$rows;$i++) {
        $row=array_fill(0,$cols,"");
        array_push($grid,$row);
    }
        

    $socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create socket\n");

    $result = socket_bind($socket, $host, $port) or die("Could not bind to socket\n");

    $result = socket_listen($socket, $noc*2) or die("Could not set up socket listener\n");
    
    //socket_set_nonblock($socket);

    $clients=array();
    for ($i=0;$i<$noc;$i++) {
        $spawn = socket_accept($socket) or die("Could not accept incoming connection\n");
        
        $addressc="";
        $portc="";
        socket_getpeername($spawn,$addressc,$portc);
        //echo "\naccepted $addressc:$portc";
        
        /*$input = socket_read($spawn, 1024) or die("Could not read input\n");
        $output = strrev($input) . "\n";
        socket_write($spawn, $output, strlen ($output)) or die("Could not write output\n");
        //socket_close($spawn);*/
        
        array_push($clients,$spawn);
    }
    
    //$nw=array($ne[0],$sw[1]);
    /*SENDING COORDINATES*/
    $lng_interval=abs($sw[1]-$ne[1])/$cols;
    $lat_interval=abs($sw[0]-$ne[0])/$rows;
    
    //$lng_sw=$sw[1];
    $lat_sw=$ne[0]-$lat_interval;
    $lng_sw=$sw[1];
    
    $lat_ne=$ne[0];
    $lng_ne=$sw[1]+$lng_interval;
    
    $cid=0;
    for($r=0;$r<$rows;$r++) {
        for($c=0;$c<$cols;$c++) {
            $sw=array($lat_sw + ($r*$lat_interval), $lng_sw + ($c*$lng_interval));
            $ne=array($lat_ne + ($r*$lat_interval), $lng_ne + ($c*$lng_interval));
            
            $coordinates=array('sw'=>$sw,
                               'ne'=>$ne,
                               'row'=>$r,
                               'col'=>$c,
                               'zoom'=>$zoom);
            
            $client=$clients[$cid];
            $cid+=1;
            $message=json_encode($coordinates);
            socket_write($client,$message,strlen($message)) or die ("could not send coordinates");
        }
    }
    
    /*foreach ($clients as $client) {
        
        //$sw=array($sw[0],$lng_sw);
        $sw=array($lat_sw,$lng_sw);
        $ne=array($lat_ne,$lng_ne);
        //$ne=array($ne[0],$lng_ne);
        
        $lng_sw+=$lng_interval;
        $lng_ne+=$lng_interval;
        
        $coordinates=array('sw'=>$sw,'ne'=>$ne);
        
        $message=json_encode($coordinates);
        socket_write($client,$message,strlen($message)) or die ("could not send coordinates");
    }*/
    
    $lat_min=1000;
    $lat_max=1000;
    $lng_min=1000;
    $lng_max=1000;
    
    $edges=array();
    
    $folders="";
    /*ACCEPT CLIENTS AGAIN*/
    $clients=array();
    for ($i=0;$i<$noc;$i++) {
        $client = socket_accept($socket) or die("Could not accept incoming connection\n");
        $addressc="";
        $portc="";
        socket_getpeername($client,$addressc,$portc);
        //echo "\naccepted $addressc:$portc";
        
        array_push($clients,$client);
        
        $message="";
        while($line=socket_read($client,1024))
            $message.=$line;
        $array=json_decode($message,true);
        $folders.=$array["folders"];
        
        $minlat=$array["minlat"];
        $maxlat=$array["maxlat"];
        $minlng=$array["minlng"];
        $maxlng=$array["maxlng"];
        
        $row=$array["row"];
        $col=$array["col"];
        $grid[$row][$col]=array("brow"=>$array["brow"],
                                "rcol"=>$array["rcol"]);
        
        // for latitude
        if($lat_min==1000) {
            $lat_min=$minlat;
            $lat_max=$maxlat;
            $lng_min=$minlng;
            $lng_max=$maxlng;
        }
        else {
            if($minlat<$lat_min)
                $lat_min=$minlat;
            if($maxlat>$lat_max)
                $lat_max=$maxlat;
            if($minlng<$lng_min)
                $lng_min=$minlng;
            if($maxlng>$lng_max)
                $lng_max=$maxlng;
        }
    }
    
    
    /* GETTING BACK KML 
    foreach ($clients as $client) {
    }*/
    
    //connecting rows
    for($c=0;$c<$cols;$c++) {
        for($r=1;$r<$rows;$r++) {
            $rcol1=$grid[$r-1][$c]["rcol"];
            $rcol2=$grid[$r][$c]["rcol"];
            
            if($rcol1!=null && $rcol2!=null)            
                array_push($edges, array("from"=>$rcol1[0], "to"=>$rcol2[0]));
        }
    }
    //connecting columns
    for($r=0;$r<$rows;$r++) {
        for($c=1;$c<$cols;$c++) {
            $brow1=$grid[$r][$c-1]["brow"];
            $brow2=$grid[$r][$c]["brow"];
            
            if($brow1!=null && $brow2!=null)
                array_push($edges, array("from"=>$brow1[0], "to"=>$brow2[0]));
        }
    }
    
    $folders="<?xml version='1.0' encoding='UTF-8'?> 
<kml xmlns='http://earth.google.com/kml/2.1' xmlns:trails='http://www.google.com/kml/trails/1.0'>
<Document>".$folders."</Document>
</kml>";
    
    unlink("map.kml");
    file_put_contents("map.kml", $folders);    
    
    /*    
    $xml = simplexml_load_file("sample_kml_client.kml");
    $xml->addChild("Document",$folders);
    $xml->asXML("map_client.kml");
    */    
    //file_put_contents("from_client.kml", $lines);
    
    $array=["minlat"=>$lat_min,
            "maxlat"=>$lat_max,
            "minlng"=>$lng_min,
            "maxlng"=>$lng_max,
           "edges"=>$edges];
    //echo "SERVER CLOSED";
    echo json_encode($array);
    socket_close($socket);
}

$sw=array($sw_lat, $sw_lng);
$ne=array($ne_lat, $ne_lng);
server_thread($sw,$ne);

?>