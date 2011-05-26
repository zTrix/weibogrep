
(function() {
    API.status().success(function(e) {
        if (e.logged_in) {
            window.location = '/grep.html';
        } else {
            window.location = '/login.html';
        }
    });
})();
