document.addEventListener("DOMContentLoaded", function () {
    const searchBarInput = document.getElementById("search-bar-input");
    const searchResults = document.getElementById("search-results");
    const searchToggle = document.getElementById("search-toggle");
    const searchBar = document.getElementById("search-bar");
    const navbar = document.querySelector('.navbar'); // Get the navbar element

    // Function to adjust search bar size in small screen mode
    function adjustSearchBarSize() {
        if (window.innerWidth <= 768) {
            const navbarHeight = navbar.offsetHeight; // Get the height of the navbar
            const navbarWidth = navbar.offsetWidth; // Get the width of the navbar

            // Set the search bar's height and width to match the navbar
            searchBar.style.width = `${navbarWidth}px`;
            searchBar.style.height = `${navbarHeight}px`;
        } else {
            // Reset styles for larger screens
            searchBar.style.width = '';
            searchBar.style.height = '';
        }
    }

    // Call adjustSearchBarSize initially and on window resize
    adjustSearchBarSize();
    window.addEventListener('resize', adjustSearchBarSize);

    // Toggle search bar visibility on smaller screens
    if (searchToggle && searchBar) {
        searchToggle.addEventListener("click", function () {
            searchBar.classList.toggle("active");

            // Automatically focus on the input when search bar is toggled open
            if (searchBar.classList.contains("active")) {
                searchBarInput.focus();
            }

            // Adjust search bar size when toggled
            adjustSearchBarSize();
        });
    }

    // Hide the search bar when the input loses focus on small screens
    if (searchBarInput) {
        searchBarInput.addEventListener("blur", function () {
            setTimeout(() => {
                // Only hide if the search bar contains the 'active' class
                searchBar.classList.remove("active");
            }, 200); // Delay to allow interactions with results
        });

        // Listen for input changes in the search bar
        searchBarInput.addEventListener("input", function () {
            let username = this.value;

            // If the input is not empty, perform the search
            if (username.length > 0) {
                fetch('/user/search?username=' + encodeURIComponent(username))
                    .then(response => response.text())
                    .then(html => {
                        searchResults.innerHTML = html;
                    })
                    .catch(error => {
                        console.error("Error fetching search results:", error);
                    });
            } else {
                // Clear the search results if the search input is empty
                searchResults.innerHTML = "";
            }
        });

        // Prevent hiding the search bar when clicking on the search results
        if (searchResults) {
            searchResults.addEventListener("mousedown", function (event) {
                event.preventDefault(); // Prevents the blur event on the input
            });
        }
    }
});
