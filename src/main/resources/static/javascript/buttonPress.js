document.addEventListener('DOMContentLoaded', function() {
    const button = document.querySelector('.submit-button');

    button.addEventListener('mousedown', function () {
        button.classList.toggle('concave');
    });

    button.addEventListener('mouseup', function () {
        button.classList.remove('concave');
    });

    button.addEventListener('mouseleave', function () {
        button.classList.remove('concave');
    });
});