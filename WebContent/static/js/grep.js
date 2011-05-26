// grep.js, for grep.html

(function($) {
    API.status().success(function(e) {
        if (!e.logged_in) {
            window.location = '/login.html';
        }
    });

    var form = $('form[action=query]');
    $('input[type=submit]', form).submit(function() {
        alert(1);
    });
})(jQuery);
