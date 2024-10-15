document.addEventListener('DOMContentLoaded', function() {
    fetchAvatars();

    document.querySelectorAll('select').forEach(select => {
        select.addEventListener('change', function() {
            const filters = {
                furColor: document.getElementById('furColor').value,
                eyeColor: document.getElementById('eyeColor').value,
                pattern: document.getElementById('pattern').value,
                breed: document.getElementById('breed').value,
                age: document.getElementById('age').value
            };

            fetchAvatars(filters);
        });
    });
});

function fetchAvatars(filters = {}) {
    const queryString = Object.keys(filters)
        .map(key => filters[key] ? `${key}=${encodeURIComponent(filters[key])}` : '')
        .filter(Boolean)
        .join('&');

    const url = queryString ? `/image/avatars?${queryString}` : '/image/avatars';

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch avatars');
            }
            return response.text();
        })
        .then(html => {
            document.getElementById('avatarList').outerHTML = html;
            attachAvatarClickHandlers();
        })
        .catch(error => {
            console.error('Error fetching avatars:', error);
        });
}

function attachAvatarClickHandlers() {
    const avatarItems = document.querySelectorAll('.avatar-item');

    avatarItems.forEach(item => {
        const img = item.querySelector('.avatar-image');
        const radio = item.querySelector('input[type="radio"]');

        img.addEventListener('click', function() {
            document.querySelectorAll('.avatar-image').forEach(img => img.classList.remove('selected'));

            img.classList.add('selected');
            radio.checked = true;
        });
    });
}
