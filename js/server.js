var net = require('net');

var server = net.createServer(function(socket) {
	//socket.write('Echo server\r\n');
    
    socket.on('data', function(data) {
        
        console.log('DATA ' + socket.remoteAddress + ': ' + data);
        // Write the data back to the socket, the client will receive it as data from the server
        //socket.write('You said "' + data + '"');
        
    });
    
    // Add a 'close' event handler to this instance of socket
    socket.on('close', function(data) {
        console.log('CLOSED: ' + socket.remoteAddress +' '+ socket.remotePort);
    });
    
    socket.write('Echo server\r\n');
    
	//socket.pipe(socket);
});

server.listen(1337, '127.0.0.1');