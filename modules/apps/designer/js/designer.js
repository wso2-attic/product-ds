$(function () {
    //TODO: cleanup this

    var dashboardUrl = window.location.pathname.match(/.*(\/dashboards\/).+/ig) ? '../dashboards' : 'dashboards';

    var currentPage = 'landing';

    var storeCache = {
        layout: [],
        gadget: []
    };

    var layoutsListHbs = Handlebars.compile($("#layouts-list-hbs").html());

    var layoutHbs = Handlebars.compile($("#layout-hbs").html());

    var widgetsListHbs = Handlebars.compile($("#widgets-list-hbs").html());

    var widgetHbs = Handlebars.compile($("#widget-hbs").html());

    var widgetOptionsHbs = Handlebars.compile($("#widget-options-hbs").html());

    var designerHbs = Handlebars.compile($("#designer-hbs").html());

    var randomId = function () {
        return Math.random().toString(36).slice(2);
    };

    var findPage = function (name) {
        return dashboard.pages[name];
    };

    var createPage = function (name, page) {
        return dashboard.pages[name] = page;
    };

    var findStoreCache = function (type, id) {
        var i;
        var item;
        var items = storeCache[type];
        var length = items.length;
        for (i = 0; i < length; i++) {
            item = items[i];
            if (item.id === id) {
                return item;
            }
        }
    };

    var findPageWidget = function (id) {
        var page = findPage(currentPage);
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

    var updatePageOptions = function (id, opts) {
        var block = findPageWidget(id);
        var options = block.widget.options;
        var o;
        var opt;
        for (opt in opts) {
            if (opts.hasOwnProperty(opt)) {
                o = options[opt];
                o.value = opts[opt];
            }
        }
        console.log(findPage(currentPage));
    };

    var saveDashboard = function () {
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

    //TODO: handle plugin options in an extensible manner
    var mergeUserPrefs = function (widget, metadata) {
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

    var renderWidget = function (container, id) {
        var instanceId = randomId();
        var widget = findStoreCache('gadget', id);
        var area = container.attr('id');
        var page = findPage(currentPage);
        var content = page.content;
        content = content[area] || (content[area] = []);
        content.push({
            id: instanceId,
            widget: widget
        });
        ues.store.gadget(id, function (err, data) {
            var el = $(widgetHbs({
                id: instanceId
            }));
            container.html(el);
            ues.gadget($('#' + instanceId), data.data.url, null, null, function (err, metadata) {
                mergeUserPrefs(widget, metadata);
                el.on('click', '.widget-toolbar .options-handle', function () {
                    renderWidgetOptions(instanceId, widget);
                });
                renderWidgetOptions(instanceId, widget);
            });
        });
    };

    var renderWidgetOptions = function (id, widget) {
        console.log(widget.options);
        var opts = {};
        $('#middle').find('.designer .options').html(widgetOptionsHbs({
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
            updatePageOptions(id, opts);
        });
    };

    var renderLayout = function (data) {
        $('#middle').find('.designer').html(layoutHbs(data))
            .find('.toolbar .save').on('click', function () {
                saveDashboard();
            }).end()
            .find('.ues-widget-box').droppable({
                //activeClass: 'ui-state-default',
                hoverClass: 'ui-state-hover',
                //accept: ':not(.ui-sortable-helper)',
                drop: function (event, ui) {
                    //$(this).find('.placeholder').remove();
                    renderWidget($(this), ui.helper.data('id'));
                }
            });
    };

    var loadWidgets = function () {
        ues.store.gadgets({
            start: 0,
            count: 20
        }, function (err, data) {
            storeCache.gadget = data;
            $('#middle')
                .find('.widgets .content').html(widgetsListHbs(data))
                .on('click', '.thumbnails .add-button', function () {

                });
        });
    };

    var initWidgetEvents = function () {
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
    };

    var initLayout = function () {
        var page = findPage(currentPage);
        if (page && page.layout) {
            renderLayout(page.layout.content);
            return;
        }
        ues.store.layouts({
            start: 0,
            count: 20
        }, function (err, data) {
            storeCache.layout = data;
            $('#middle')
                .find('.designer .content').html(layoutsListHbs(data))
                .on('click', '.thumbnails .add', function () {
                    var id = $(this).data('id');
                    var ly = findStoreCache('layout', id);
                    $.get(ly.url, function (data) {
                        ly.content = data;
                        createPage(currentPage, {
                            title: 'My Dashboard',
                            layout: ly,
                            content: {}
                        });
                        renderLayout(data);
                    }, 'html');
                });
        });
    };

    var initTabs = function () {
        $('#left')
            .find('.nav-tabs a')
            .click(function (e) {
                e.preventDefault();
                var el = $(this);
                el.tab('show');
            });
    };

    initTabs();
    initLayout();
    initWidgetEvents();
    loadWidgets();

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