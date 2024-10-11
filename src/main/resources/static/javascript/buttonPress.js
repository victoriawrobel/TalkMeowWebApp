document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.submit-button');

    buttons.forEach(button => {
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
});