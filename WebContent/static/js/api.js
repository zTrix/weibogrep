

(function($, name) {
    var api = window[name] = {};
    var get_method = 'oauth_url status logout grep user_info'.split(' ');

    $(get_method).each(function(i, e) {
        api[e] = function(param) {
            return $.getJSON('/api/' + e, param);
        }
    });
})(jQuery, 'API');

