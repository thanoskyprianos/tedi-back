// event name : message
const io = require('socket.io')(4200)

// When a client connects a user connected gets displayed
io.on('connection', (socket) => {
    console.log('You are connected to messages');

    // When a message is received, it logs the message in the console
    socket.on('message', (message) => {
        console.log(message);
    });

});

// node .
// => listening on http://localhost:4200


/* Changes to make in front-end (index.)html -> messages.html file
 * point to the socket.io ex) <script src="https://cdn.socket.io/socket.io-3.0.0.js"></script>
 * <script defer src="app.js"></script>
 */