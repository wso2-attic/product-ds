$(function () {
    //TODO: cleanup this

    var dashboardUrl = 'dashboards';

    var randomId = function () {
        return Math.random().toString(36).slice(2);
    };

    var cache = {
        layout: [],
        gadget: []
    };

    var current = 'landing';

    var pager = function (name, page) {
        return page ? (dashboard.pages[name] = page) : dashboard.pages[name];
    };

    var find = function (type, id) {
        var i;
        var item;
        var items = cache[type];
        var length = items.length;
        for (i = 0; i < length; i++) {
            item = items[i];
            if (item.id === id) {
                return item;
            }
        }
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

    var designerHbs = Handlebars.compile($("#designer-hbs").html());
    var optionsHbs = Handlebars.compile($("#options-hbs").html());
    var widgetsHbs = Handlebars.compile($("#widgets-hbs").html());
    var widgetHbs = Handlebars.compile($("#widget-hbs").html());
    var layoutsHbs = Handlebars.compile($("#layouts-hbs").html());
    var layoutHbs = Handlebars.compile($("#layout-hbs").html());

    var userPrefs = function (widget, metadata) {
        var pref;
        var opts = {};
        var prefs = metadata.userPrefs;
        for (pref in prefs) {
            if (prefs.hasOwnProperty(pref)) {
                pref = prefs[pref];
                opts[pref.name] = {
                    type: pref.dataType,
                    title: pref.displayName,
                    value: pref.defaultValue,
                    options: pref.orderedEnumValues,
                    required: pref.required
                };
            }
        }
        widget.plugin = opts;
    };

    var options = function (id, widget) {
        console.log(widget.options);
        $('#middle').find('.designer .options').html(optionsHbs({
            options: widget.options,
            plugin: widget.plugin
        }));
    };

    var layout = function (data) {
        $('#middle').find('.designer').find('.content').html(layoutHbs(data))
            .find('.ues-widget-box').droppable({
                //activeClass: 'ui-state-default',
                hoverClass: 'ui-state-hover',
                //accept: ':not(.ui-sortable-helper)',
                drop: function (event, ui) {
                    //$(this).find('.placeholder').remove();
                    var id = ui.helper.data('id');
                    var widget = find('gadget', id);
                    var droppable = $(this);
                    var area = droppable.attr('id');
                    var page = pager(current);
                    var content = page.content;
                    content = content[area] || (content[area] = []);
                    content.push(widget);
                    ues.store.gadget(id, function (err, data) {
                        var id = randomId();
                        var el = $(widgetHbs({
                            id: id
                        }));
                        droppable.html(el);
                        ues.gadget($('#' + id), data.data.url, null, null, function (err, metadata) {
                            userPrefs(widget, metadata);
                            el.on('click', '.widget-toolbar .options-handle', function () {
                                options(id, widget);
                            });
                            options(id, widget);
                        });
                    });
                }
            });
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

    ues.store.gadgets({
        start: 0,
        count: 20
    }, function (err, data) {
        cache.gadget = data;
        $('#middle')
            .find('.widgets .content').html(widgetsHbs(data))
            .on('click', '.thumbnails .add-button', function () {

            });
    });

    ues.store.layouts({
        start: 0,
        count: 20
    }, function (err, data) {
        cache.layout = data;
        $('#middle')
            .find('.designer .content').html(layoutsHbs(data))
            .on('click', '.thumbnails .add', function () {
                var id = $(this).data('id');
                var ly = find('layout', id);
                pager(current, {
                    title: 'My Dashboard',
                    layout: ly,
                    content: {}
                });
                $.get(ly.url, function (data) {
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