$(function () {
    //TODO: cleanup this

    var dashboardUrl = 'dashboards';

    var randomId = function () {
        return Math.random().toString(36).slice(2);
    };

    var save = function () {
        $.ajax({
            url: dashboardUrl + '/' + page.id,
            method: 'POST',
            data: JSON.stringify(page),
            contentType: 'application/json'
        }).success(function (data) {
            console.log('dashboard saved successfully');
        }).error(function () {
            console.log('error saving dashboard');
        });
    };

    var designer = Handlebars.compile($("#designer-hbs").html());
    var widgets = Handlebars.compile($("#thumbs-hbs").html());
    var options = Handlebars.compile($("#options-hbs").html());
    var layouts = Handlebars.compile($("#layouts-hbs").html());
    var widget = Handlebars.compile($("#widget-hbs").html());

    var layout = function (data) {
        $('#middle').find('.designer').find('.content').html(data);
    };

    /**
     * Tab initialization
     */
    $('#left')
        .find('.nav-tabs a')
        .click(function (e) {
            e.preventDefault();
            var el = $(this);
            el.tab('show');
        });

    $('#right').find('.properties-handle').click(function () {
        $('#right').find('.navbar').toggle();
    });

    $('.widgets').on('mouseenter', '.thumbnail .drag-handle', function () {
        $(this).draggable({
            cancel: false,
            appendTo: 'body',
            helper: 'clone',
            start: function (event, ui) {
                console.log('dragging');
                $('#left').find('a[href="#designer"]').tab('show');
            },
            stop: function () {
                //$('#left a[href="#widgets"]').tab('show');
            }
        });
    }).on('mouseleave', '.thumbnail .drag-handle', function () {
        $(this).draggable('destroy');
    });

    $('.ues-widget-box').droppable({
        //activeClass: 'ui-state-default',
        hoverClass: 'ui-state-hover',
        //accept: ':not(.ui-sortable-helper)',
        drop: function (event, ui) {
            //$(this).find('.placeholder').remove();
            var id = ui.helper.data('id');
            var droppable = $(this);
            ues.store.asset(id, function (err, data) {
                var id = Math.random().toString(36).slice(2);
                droppable.html('<div id=' + id + '></div>');
                UESContainer.renderGadget(id, data.attributes.url);
            });
        }
    });

    ues.store.gadgets({
        start: 0,
        count: 20
    }, function (err, data) {
        $('#middle')
            .find('.widgets .content').html(widgets(data))
            .on('click', '.thumbnails .add-button', function () {

            });
    });

    ues.store.layouts({
        start: 0,
        count: 20
    }, function (err, data) {
        $('#middle')
            .find('.designer .content').html(layouts(data))
            .on('click', '.thumbnails .add', function () {
                var url = $(this).data('url');
                $.get(url, function (data) {
                    $('#middle').find('.designer .content').html(layout(data));
                }, 'html');
            });
    });

    var source = function (html) {
        return '<div class="container">' + html + '</div>';
    };

    /*$('.preview').click(function () {
     var html = $('.sandbox .container-fluid').html();
     console.log(html);
     $('#sandbox').contents()
     .find('body')
     .html(source(html));
     */
    /*$('.container').hide();
     $('#sandbox').contents()
     .find('body')
     .html('<div class="container"><div class="row"><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div></div></div>');
     */
    /*
     });*/

    //TODO: uncomment this
    /*$('.designer .content').on('mouseenter', '.widget .widget-toolbar', function () {
     $('.tools', $(this)).show();
     }).on('mouseleave', '.widget .widget-toolbar', function () {
     $('.tools', $(this)).hide();
     });*/

});