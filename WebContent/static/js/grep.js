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

    var user;

    API.user_info().success(function(e) {
        user = e;
        var ua = $('.header .user');
        ua.text(user.name);
        ua.attr('href', 'http://weibo.com/' + user.id);
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

    $('#logout').click(function() {
        API.logout().success(function() {
            window.location = "/login.html";
        });
    });
})(jQuery);

