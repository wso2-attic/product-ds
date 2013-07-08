$(function () {
    var comments = {
            interval: 5 * 1000,
            commentsUrl: caramel.context + '/apis/comments?asset=' + $('#assetp-tabs').data('aid') + '&page=',
            pagingUrl: '/apis/comments/paging?page=',
            partial: '/themes/store/partials/comments.hbs'
        },
        paging = {
            current: 1,
            partial: '/themes/store/partials/pagination.hbs'
        };

    var el = $('.user-rating'),
        rating = el.data('rating');
    $($('input', el)[rating - 1]).attr('checked', 'checked');

    $('.auto-submit-star').rating({
        callback: function (value, link) {		
		if(value==undefined){
			value=0;
		}
        	$('.rate-num-assert').html('('+value+')');
            caramel.post('/apis/rate', {
                asset: $('#assetp-tabs').data('aid'),
                value: value || 0
            }, function (data) {

            });
        }
    });

    $('#tab-reviews').on('click', '.pagination a', function (e) {
        e.preventDefault();
        var page,
            thiz = $(this),
            str = thiz.text(),
            url = thiz.attr('href');
        if (url === '#') {
            return;
        }
        if (str.indexOf('Next') !== -1) {
            page = paging.current + 1;
        } else if (str.indexOf('Prev') !== -1) {
            page = paging.current - 1
        } else {
            page = parseInt(str, 10);
        }
        loadReviews(url, page);
    });

    var loadReviews = function (url, page) {
        var el = $('#tab-reviews').find('.content');
        comments.updated = new Date().getTime();
        async.parallel({
            comments: function (callback) {
                $.get(url, function (data) {
                    callback(null, data);
                }, 'json');
            },
            paging: function (callback) {
                caramel.post(comments.pagingUrl + page, {
                    asset: $('#assetp-tabs').data('aid')
                }, function (data) {
                    paging.current = data.current;
                    callback(null, data);
                }, 'json');
            }
        }, function (error, result) {
            if (error) {
                theme.loaded(el, '<p>Error while retrieving data.</p>')
            } else {
                async.parallel({
                    comments: function (callback) {
                        var i, comment,
                            comments = result.comments,
                            length = comments.length;
                        for(i = 0; i < length; i++) {
                            comment = comments[i];
                            comment.created.time = moment(comment.created.time).format('LL');
                        }
                        caramel.render('comments', {
                            user: store.user,
                            comments: comments
                        }, callback);
                    },
                    paging: function (callback) {
                        caramel.render('pagination', result.paging, callback);
                    }
                }, function (err, result) {
                    theme.loaded(el, result.comments);
                    el.append(result.paging);
                });
            }
            $('#assetp-tabs').tab();
        });
        theme.loading(el);
    };

    $('#assetp-tabs').on('click', 'a[href="#tab-reviews"]',function (e) {
        var thiz = $(this),
            current = new Date().getTime();
        e.preventDefault();
        if (!comments.interval || (current - (comments.updated || 0) < comments.interval)) {
            thiz.tab('show');
            return;
        }
        loadReviews(comments.commentsUrl + paging.current, paging.current);
        thiz.tab('show');
    }).on('click', 'a[href="#tab-properties"]', function (e) {

        });

    $('#tab-review-box').find('.btn-primary').live('click', function (e) {
        if (!$("#form-review").valid()) return;
        caramel.post('/apis/comment', {
            asset: $('#assetp-tabs').data('aid'),
            content: $('#tab-review-box').find('.content').val()
        }, function (data) {
            loadReviews(comments.commentsUrl + paging.current, paging.current);
        }, 'json');
    });

    $('#btn-add-gadget').click(function () {
	var elem = $(this);
	if(store.user){
	    isAssertTrue(elem.data('aid'),elem.data('type'));
		}else{
		   asset.process(elem.data('type'), elem.data('aid'), location.href);
			}
    });

    $("a[data-toggle='tooltip']").tooltip();

    $('.embed-snippet').hide();

    $('.btn-embed').click(function() {
    	$('.embed-snippet').toggle(400);
    	return false;
    });
	
	$('#tab-review-box').live('focus', function(e){
	if($('#comment-content').hasClass('user-review')) {
	$(".btn-review").removeClass("btn-primary");
	$(".btn-review").addClass("disabled");
	$('.error-text').show();
	return false;
	}
    });

    /*    $('#btn-copy-gadget-code').click(function(){
     var script = $('#modal-add-gadget code').html().trim();
     localStorage['gadget-code'] = script;
     $(this).fadeOut("fast", function(){
     $(this).attr('id', 'btn-open-editor').text('Open Editor').fadeIn("fast", function(){
     $('.copy-status').html('Code copied to clipboard').delay(1000).fadeIn();
     });

     })

     })
     $('#btn-open-editor').live('click', function(){
     location.href = '/portal/dashboard.jag';
     })*/
    /*
     $(document).scroll(function(){
     var h = $(this).scrollTop();
     if(h>19){
     $('.asset-description-header').addClass('asset-description-header-scroll');
     } else {
     $('.asset-description-header').removeClass('asset-description-header-scroll');
     }
     })*/
});
