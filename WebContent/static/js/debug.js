

(function($) {
    var apis = 'query logout loginstatus'.split(' ');
    $(apis).each(function(i, e) {
        $('<option/>').attr('value', e)
                      .text(e)
                      .appendTo('#action');
    });
    $('#submit').click(function() {
        var action = $('#action option:selected').val();
        var v = eval('({' + $('#values').val() + '})');
        $.getJSON('/' + action, v, function(d) {
            $('#result').text(JSON.stringify(d, null, '    '));
        });
    });
})(jQuery);
