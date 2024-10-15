document.addEventListener("DOMContentLoaded", function () {
    const searchBarInput = document.getElementById("search-bar-input");
    const searchResults = document.getElementById("search-results");
    const searchToggle = document.getElementById("search-toggle");
    const searchBar = document.getElementById("search-bar");
    const navbar = document.querySelector('.navbar'); // Get the navbar element

    function adjustSearchBarSize() {
        if (window.innerWidth <= 768) {
            const navbarHeight = navbar.offsetHeight;
            const navbarWidth = navbar.offsetWidth;

            searchBar.style.width = `${navbarWidth}px`;
            searchBar.style.height = `${navbarHeight}px`;
        } else {
            searchBar.style.width = '';
            searchBar.style.height = '';
        }
    }

    adjustSearchBarSize();
    window.addEventListener('resize', adjustSearchBarSize);

    if (searchToggle && searchBar) {
        searchToggle.addEventListener("click", function () {
            searchBar.classList.toggle("active");

            if (searchBar.classList.contains("active")) {
                searchBarInput.focus();
            }

            adjustSearchBarSize();
        });
    }

    if (searchBarInput) {
        searchBarInput.addEventListener("blur", function () {
            setTimeout(() => {
                searchBar.classList.remove("active");
                searchBarInput.value = "";
                searchResults.innerHTML = "";
            }, 200);
        });

        searchBarInput.addEventListener("input", function () {
            let username = this.value;

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
                searchResults.innerHTML = "";
            }
        });

        if (searchResults) {
            searchResults.addEventListener("mousedown", function (event) {
                event.preventDefault();
            });
        }
    }
});
