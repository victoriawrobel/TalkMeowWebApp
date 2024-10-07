document.addEventListener('DOMContentLoaded', function() {
    // Fetch all avatars initially
    fetch('/image/avatars')
        .then(response => response.text())
        .then(html => {
            document.getElementById('avatarList').innerHTML = html;
            attachAvatarClickHandlers();
        });

    document.querySelectorAll('select').forEach(select => {
    select.addEventListener('change', function() {
        const furColor = document.getElementById('furColor').value;
        const eyeColor = document.getElementById('eyeColor').value;
        const pattern = document.getElementById('pattern').value;
        const breed = document.getElementById('breed').value;
        const age = document.getElementById('age').value;

        // AJAX call to filter avatars
        fetch(`/image/avatars?furColor=${furColor}&eyeColor=${eyeColor}&pattern=${pattern}&breed=${breed}&age=${age}`)
        .then(response => response.text())
        .then(html => {
                document.getElementById('avatarList').innerHTML = html;
                attachAvatarClickHandlers();
            });
        });
    });
});

function attachAvatarClickHandlers() {
    const avatarItems = document.querySelectorAll('.avatar-item');

    avatarItems.forEach(item => {
        const img = item.querySelector('.avatar-image');
        const radio = item.querySelector('input[type="radio"]');

        img.addEventListener('click', function() {
            // Deselect all avatars
            document.querySelectorAll('.avatar-image').forEach(img => img.classList.remove('selected'));

            // Select the clicked avatar
            img.classList.add('selected');
            radio.checked = true;
        });
    });
}
