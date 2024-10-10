document.addEventListener('DOMContentLoaded', function() {
    const button = document.querySelector('.submit-button');

    button.addEventListener('click', function () {
        button.classList.toggle('concave');
    });
});