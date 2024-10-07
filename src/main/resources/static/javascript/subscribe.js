function loadScript(url, callback) {
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onload = callback;
    document.head.appendChild(script);
}

loadScript('https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js', function() {
    loadScript('https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js', function() {
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            const currentUser = document.getElementById('current-user-username').className;

            if (currentUser) {
                stompClient.subscribe('/user/' + currentUser + '/new-message', function (messageOutput) {
                    showMessageOutput(messageOutput.body);
                });
            }
        });

        function showMessageOutput(message) {
            const currentUserElement = document.getElementById('current-user-username');
            currentUserElement.textContent = "received a message: " + message;
            console.log("Received message: ", message);
        }
    });
});