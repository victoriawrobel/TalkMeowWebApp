document.addEventListener('DOMContentLoaded', function() {
    fetch('/user/currentUser/avatar')
        .then(response => response.text())
        .then(userId => {
            const userAvatar = document.getElementById('user-avatar');
            if (userAvatar) {
                userAvatar.src = `/image/avatar/${userId}`;
            }
        })
        .catch(error => console.error('Error fetching user ID:', error));
});