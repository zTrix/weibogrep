
(function($) {

    API.status().success(function(e) {
        if (e.logged_in) {
            window.location = '/grep.html';
        } 
    });

    API.oauth_url().success(function(r) {
        $('a.login').attr('href', r.oauth_url);
    });

})(jQuery);
