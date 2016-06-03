<?php

set_time_limit(0);

$port=12001;

$pid=shell_exec("netstat -aon|grep :$port|tr -s [:space:] \" \"|cut -d\" \" -f6|tr -d \"\n\" 2>&1");
echo $pid;
//$output=shell_exec("kill -f $pid 2>&1");
$output=shell_exec("Taskkill /PID $pid /F 2>&1");
echo $output;
if(!empty($pid)) {
    
}

echo "stopped server successfully";