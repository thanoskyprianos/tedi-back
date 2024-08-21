// Establishment of websocket connection
const socket = io('ws://localhost:4200');

// Container for displaying messages
const messageContainer = document.getElementById('send-container')

// Event listener for incoming messages 
socket.on('message', textFromUser => {
    // When a message is received from the server the append messages function gets called
    // with text from the user
    appendMessages(textFromUser)
});


// Let the user send a message 
document.querySelector('button').onclick = () => {

    // Retrieve the users input
    const textFromUser = document.querySelector('input').value;
    
    // Send the value to the server, using the socket.emit
    socket.emit('message', textFromUser);

}

function appendMessages(message) 
{
    // div element gets created in order to display messages
    const messageElement = document.createElement('div')

    // Set the text content of the div to the message
    messageElement.innerText = message
    
    // Element gets appended to message container
    messageContainer.append(messageElement)
}