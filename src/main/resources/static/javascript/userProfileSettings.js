function enableEditing() {
    document.getElementById('usernameInput').disabled = false;
    document.getElementById('submitButton').style.display = 'inline';
    document.getElementById('passwordEncrypted').style.display = 'none';
    document.getElementById('passwordChange').style.display = 'inline';
}

function validateForm() {
    const username = document.getElementById('usernameInput').value;
    if (username === '') {
        alert('Username cannot be empty');
        return false;
    }
    return true;
}
