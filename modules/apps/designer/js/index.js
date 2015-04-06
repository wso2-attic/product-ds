$(function () {
    //TODO: cleanup this

    var dashboardUrl = window.location.pathname.match(/.*(\/dashboards\/).+/ig) ? '../dashboards' : 'dashboards';

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

    var findWidgetDef = function (type, id) {
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

    var findBlock = function (id) {
        var page = pager(current);
        var content = page.content;
        var i;
        var length;
        var area;
        var widget;
        var widgets;
        for (area in content) {
            if (content.hasOwnProperty(area)) {
                widgets = content[area];
                length = widgets.length;
                for (i = 0; i < length; i++) {
                    widget = widgets[i];
                    if (widget.id === id) {
                        return widget;
                    }
                }
            }
        }
    };

    var save = function () {
        $.ajax({
            url: dashboardUrl,
            method: 'POST',
            data: JSON.stringify(dashboard),
            contentType: 'application/json'
        }).success(function (data) {
            console.log('dashboard saved successfully');
        }).error(function () {
            console.log('error saving dashboard');
        });
    };

    var update = function (id, opts) {
        var block = findBlock(id);
        var options = block.widget.options;
        var o;
        var opt;
        for (opt in opts) {
            if (opts.hasOwnProperty(opt)) {
                o = options[opt];
                o.value = opts[opt];
            }
        }
        console.log(pager(current));
    };

    var designerHbs = Handlebars.compile($("#designer-hbs").html());
    var optionsHbs = Handlebars.compile($("#options-hbs").html());
    var widgetsHbs = Handlebars.compile($("#widgets-hbs").html());
    var widgetHbs = Handlebars.compile($("#widget-hbs").html());
    var layoutsHbs = Handlebars.compile($("#layouts-hbs").html());
    var layoutHbs = Handlebars.compile($("#layout-hbs").html());

    //TODO: handle plugin options in an extensible manner
    var userPrefs = function (widget, metadata) {
        var pref;
        var opts = widget.options || (widget.options = {});
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
    };

    var options = function (id, widget) {
        console.log(widget.options);
        var opts = {};
        $('#middle').find('.designer .options').html(optionsHbs({
            id: id,
            options: widget.options
        })).find('.sandbox').on('click', '.save', function () {
            var thiz = $(this);
            var id = thiz.data('id');
            var sandbox = thiz.closest('.sandbox');
            $('input', sandbox).each(function () {
                var el = $(this);
                opts[el.attr('name')] = el.val();
            });
            $('select', sandbox).each(function () {
                var el = $(this);
                opts[el.attr('name')] = el.val();
            });
            update(id, opts);
        });
    };

    var layout = function (data) {
        $('#middle').find('.designer').html(layoutHbs(data))
            .find('.toolbar .save').on('click', function () {
                save();
            }).end()
            .find('.ues-widget-box').droppable({
                //activeClass: 'ui-state-default',
                hoverClass: 'ui-state-hover',
                //accept: ':not(.ui-sortable-helper)',
                drop: function (event, ui) {
                    //$(this).find('.placeholder').remove();
                    var id = randomId();
                    var wid = ui.helper.data('id');
                    var widget = findWidgetDef('gadget', wid);
                    var droppable = $(this);
                    var area = droppable.attr('id');
                    var page = pager(current);
                    var content = page.content;
                    content = content[area] || (content[area] = []);
                    content.push({
                        id: id,
                        widget: widget
                    });
                    ues.store.gadget(wid, function (err, data) {
                        var el = $(widgetHbs({
                            id: id
                        }));
                        droppable.html(el);
                        ues.gadgets.render($('#' + id), data.data.url, null, null, function (err, metadata) {
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

    var layouter = function () {
        var page = pager(current);
        if (page && page.layout) {
            layout(page.layout.content);
            return;
        }
        ues.store.layouts({
            start: 0,
            count: 20
        }, function (err, data) {
            cache.layout = data;
            $('#middle')
                .find('.designer .content').html(layoutsHbs(data))
                .on('click', '.thumbnails .add', function () {
                    var id = $(this).data('id');
                    var ly = findWidgetDef('layout', id);
                    $.get(ly.url, function (data) {
                        ly.content = data;
                        pager(current, {
                            title: 'My Dashboard',
                            layout: ly,
                            content: {}
                        });
                        layout(data);
                    }, 'html');
                });
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

    layouter();

    /*var source = function (html) {
     return '<div class="container">' + html + '</div>';
     };*/

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