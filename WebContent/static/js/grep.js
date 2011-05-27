// grep.js, for grep.html
var greptype_people = "people";

var user;
var rs_items;
var form = $('form#grep_box');
var rs_timeout=5000;
var rs_count;
var requery_timeout=10000;
var requery_handler;
var requery_timestamp;
var last_query;
var do_requery = 1;
var last_qtype;

function requery() {
    if (do_requery == 0)  return;
    if (last_query == "") return;
    API.grep({
        q: last_query,
        type : last_qtype,
        newerthan: requery_timestamp
    }).success(function(e) {
       if (e.err < 0) return;
       requery_timestamp = e.newerthan;
       if (e.items.length > 0) {
            rs_count = rs_count + e.items.length;
            $('#rs_status').html("Found new results!");
            $.tmpl(e.type, e.items).prependTo('#result');
            setTimeout(function(){
            $('#rs_status').html('Found ' + rs_count + ' results');}, rs_timeout);
    $('.rs_item').mouseenter(function(){
        $(this).addClass("rs_item_mouseenter");
    });
    $('.rs_item').mouseleave(function(){
        $(this).removeClass("rs_item_mouseenter");
    });
       }
    });
    requery_handler = setTimeout(requery, requery_timeout);
}

(function($) {
    API.status().success(function(e) {
        if (!e.logged_in) {
            window.location = '/login.html';
        }
    });

    $.get('/tmpl/timeline_item.html').success(function(html) {
        $.template('timeline', html);
    });
    $.get('/tmpl/friend_item.html').success(function(html) {
        $.template('friend', html);
    });

    form.submit(function(e) {
        e.preventDefault();
        var query = $('input[type=text]', form).val();
        last_query = query;
        clearTimeout(requery_handler);
        console.log(query);
        $('#rs_status').hide();
        $('#result').html("");
        if (query == "") return;
        last_qtype = $('#stype_selector').val();
        $('#rs_status').html('Loading...');
        $('#rs_status').show();
        API.grep({
            q: query,
            type : last_qtype
        }).success(function(e) {
            if (e.err < 0) {
                $('#rs_status').html("Please try relogin...");
                $('#rs_status').show();
                return;
            }
            rs_items=e;
            requery_timestamp=e.newerthan;
            rs_count = e.items.length;
            if (e.items.length <= 0) {
                $('#rs_status').html('No result found!');    
                $('#rs_status').fadeOut(rs_timeout);
            } else {
                $('#result').html($.tmpl(e.type, e.items, {
                    getDate: function(m) {
                        var d = new Date(m), now=$.now(), delta = now - d;
                        if (delta<3600000) {
                            return Math.round(delta/60000)+'分钟前';
                        } else if (delta<86400000) {
                            return Math.round(delta/3600000)+'小时前';
                        } else if (delta<259200000) {      //three days
                            return Math.round(delta/86400000)+'天前';
                        }
                        return d.getFullYear() + "年" + (d.getMonth()+1) + "月" + d.getDate() + "日";
                    }
                }));
                $('#rs_status').html('Found ' + rs_count + ' results');
            $('.rs_item').mouseenter(function(){
                $(this).addClass("rs_item_mouseenter");
            });
            $('.rs_item').mouseleave(function(){
                $(this).removeClass("rs_item_mouseenter");
            });
            }
            requery_handler = setTimeout(requery, requery_timeout);
        }).error(function() {
            console.log('api service failed');
            $('#rs_status').html('Connecting error...');
            $('#rs_status').fadeOut(rs_timeout);
        });
    });

    $('#logout').click(function() {
        API.logout().success(function() {
            window.location = "/login.html";
        });
    });


    $('#rs_status').hide();

    API.user_info().success(function(e) {
        user = e;
        var ua = $('.header .user');
        ua.text(user.name);
        ua.attr('href', 'http://weibo.com/' + user.id);
        ua.attr('target', '_blank');
        var indexed = $('.doccount #indexed_num');
        indexed.html(user.grep_indexing_num > 0 ? user.grep_indexing_num : 0);
    });
})(jQuery);

