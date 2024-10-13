document.addEventListener('DOMContentLoaded', function() {
    // Fetch and display avatars initially
    fetchAvatars();

    // Add change event listeners to all the select elements for filtering
    document.querySelectorAll('select').forEach(select => {
        select.addEventListener('change', function() {
            const filters = {
                furColor: document.getElementById('furColor').value,
                eyeColor: document.getElementById('eyeColor').value,
                pattern: document.getElementById('pattern').value,
                breed: document.getElementById('breed').value,
                age: document.getElementById('age').value
            };

            // Call function to fetch avatars based on filters
            fetchAvatars(filters);
        });
    });
});

/**
 * Fetches avatars from the server, applies filtering if filters are provided,
 * and updates the avatar list with the new avatars.
 * @param {Object} filters - An object containing filters for avatar search.
 */
function fetchAvatars(filters = {}) {
    // Build the query string for filters
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
            attachAvatarClickHandlers(); // Re-attach the event listeners
        })
        .catch(error => {
            console.error('Error fetching avatars:', error);
        });
}

/**
 * Attaches click handlers to avatar images for selection.
 * Called every time avatars are updated via AJAX.
 */
function attachAvatarClickHandlers() {
    const avatarItems = document.querySelectorAll('.avatar-item');

    avatarItems.forEach(item => {
        const img = item.querySelector('.avatar-image');
        const radio = item.querySelector('input[type="radio"]');

        img.addEventListener('click', function() {
            // Deselect all avatars by removing the selected class from all images
            document.querySelectorAll('.avatar-image').forEach(img => img.classList.remove('selected'));

            // Select the clicked avatar and check the corresponding radio button
            img.classList.add('selected');
            radio.checked = true;
        });
    });
}
