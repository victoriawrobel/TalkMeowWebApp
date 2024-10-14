document.addEventListener('DOMContentLoaded', function() {
    const changeAvatarButton = document.getElementById('avatar-change-button');
    const avatarSelectionContainer = document.getElementById('avatar-selection-container');
    const avatarChangeForm = document.getElementById('profile-avatar-change-submit');

    changeAvatarButton.addEventListener('click', function(event) {
        event.preventDefault();
        if (avatarSelectionContainer.style.display === 'none' || avatarSelectionContainer.style.display === '') {
            avatarSelectionContainer.style.display = 'block';
        } else {
            avatarSelectionContainer.style.display = 'none';
            avatarChangeForm.submit();
        }
    });

});