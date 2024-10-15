function loadScript(url, callback) {
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onload = callback;
    document.head.appendChild(script);
}

function getCurrentUserAndSetClass() {
    fetch('/user/currentUser')
        .then(response => response.text())
        .then(username => {
            const currentUserElement = document.getElementById('current-user-username');
            if (currentUserElement) {
                currentUserElement.classList.add(username);
            }
        })
        .catch(error => console.error('Error fetching current user:', error));
}

loadScript('https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js', function() {
    loadScript('https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js', function() {
        getCurrentUserAndSetClass();

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
            const otherUserElement = document.getElementById('other-username');
            if (otherUserElement && otherUserElement.className === message) {
                console.log("Message matches other-username, no notification displayed.");
                return;
            }

            const popup = document.createElement('div');
            popup.className = 'message-popup';
            popup.innerText = 'New message from: ' + message;

            document.body.appendChild(popup);

            setTimeout(() => {
                popup.remove();
            }, 3000);

            console.log("Received message: ", message);
        }
    });
});