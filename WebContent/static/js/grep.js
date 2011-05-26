// grep.js, for grep.html

(function($) {
    API.status().success(function(e) {
        if (!e.logged_in) {
            window.location = '/login.html';
        }
    });

    $.get('/tmpl/timeline_item.html').success(function(html) {
        $.template('timeline', html);
    });

    var form = $('form#grep_box');
    form.submit(function(e) {
        e.preventDefault();
        var query = $('input[type=text]', form).val();
        console.log(query);
        API.grep({
            q: query
        }).success(function(e) {
            $('ul.result').html($.tmpl('timeline', e.items));
        }).error(function() {
            console.log('api service failed');
        });
    });
})(jQuery);
