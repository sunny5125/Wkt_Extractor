<?php

function mergeimg($destfn,$srcfn,$dst_x,$dst_y,$src_w,$src_h,$tw,$th){
    
    $src=imagecreatefromjpeg("map_part.jpg");
    
    $dest='';
    if($dst_x==0 && $dst_y==0)
        $dest=imagecreatetruecolor($tw,$th);
    else
        $dest=imagecreatefromjpeg($destfn);
    
    imagecopy($dest,$src,$dst_x,$dst_y,0,0,$src_w,$src_h);

    header("Content-Type: image/jpeg");
    imagejpeg($dest,$destfn);

    imagedestroy($dest);
    imagedestroy($src);
}