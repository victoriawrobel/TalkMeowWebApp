const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to public messages
    stompClient.subscribe('/messages', function(messageOutput) {
        showMessageOutput(JSON.parse(messageOutput.body));
    });

    // Subscribe to private messages
    stompClient.subscribe('/user/specific', function(messageOutput) {
        showMessageOutput(JSON.parse(messageOutput.body));
    });
});

function showMessageOutput(message) {
    // Display the message to the user
    console.log("Received message: ", message);
    // Add your code to display the message in the UI
}