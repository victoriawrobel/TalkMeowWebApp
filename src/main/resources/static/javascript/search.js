// Listen for input changes in the search bar
document.getElementById("search-bar").addEventListener("input", function () {
    let username = this.value;

    // If the input is not empty, perform the search
    if (username.length > 0) {
        fetch('/user/search?username=' + username)
            .then(response => response.text())
            .then(html => {
                document.getElementById("search-results").innerHTML = html;
            });
    } else {
        // Clear the search results if the search input is empty
        document.getElementById("search-results").innerHTML = "";
    }
});