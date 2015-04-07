$(function () {
    //TODO: cleanup this

    var dashboardUrl = ues.utils.relativePrefix() + 'dashboards';

    var dashboard;

    var page;

    var storeCache = {};

    var layoutsListHbs = Handlebars.compile($("#layouts-list-hbs").html());

    var layoutHbs = Handlebars.compile($("#layout-hbs").html());

    var widgetsListHbs = Handlebars.compile($("#widgets-list-hbs").html());

    var widgetToolbarHbs = Handlebars.compile($("#widget-toolbar-hbs").html());

    var widgetOptionsHbs = Handlebars.compile($("#widget-options-hbs").html());

    var designerHbs = Handlebars.compile($("#designer-hbs").html());

    var randomId = function () {
        return Math.random().toString(36).slice(2);
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

    var findWidget = function (id) {
        var i;
        var length;
        var area;
        var widget;
        var widgets;
        var content = page.content;
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

    var updateWidgetOptions = function (id, opts) {
        var o;
        var opt;
        var block = findWidget(id);
        var options = block.content.options;
        for (opt in opts) {
            if (opts.hasOwnProperty(opt)) {
                o = options[opt];
                o.value = opts[opt];
            }
        }
    };

    var removeWidget = function (id) {
        var container = $('#' + id);
        var area = container.closest('.ues-widget-box').attr('id');
        var widget = findWidget(id);
        var content = page.content;
        area = content[area];
        var index = area.indexOf(widget);
        area.splice(index, 1);
        container.remove();
    };

    var saveDashboard = function (dashboard) {
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

    var initWidgetToolbar = function () {
        var designer = $('#middle').find('.ues-designer');
        designer.on('click', '.ues-widget .ues-toolbar .ues-options-handle', function () {
            var id = $(this).closest('.ues-widget').attr('id');
            renderWidgetOptions(id);
        });
        designer.on('click', '.ues-widget .ues-toolbar .ues-trash-handle', function () {
            var id = $(this).closest('.ues-widget').attr('id');
            removeWidget(id);
        });
    };

    var renderWidgetToolbar = function (id) {
        $('#' + id).prepend($(widgetToolbarHbs()))
    };

    var renderWidget = function (container, wid) {
        var id = randomId();
        //TODO: remove hardcoded gadget
        var asset = findStoreCache('gadget', wid);
        var area = container.attr('id');
        var content = page.content;
        content = content[area] || (content[area] = []);
        var widget = {
            id: id,
            content: asset
        };
        content.push(widget);
        ues.widgets.render(container, widget, function (err, block) {
            renderWidgetToolbar(id);
            renderWidgetOptions(id);
        });
    };

    var renderWidgetOptions = function (id) {
        var opts = {};
        var widget = findWidget(id);
        $('#middle').find('.ues-designer .ues-options').html(widgetOptionsHbs({
            id: widget.id,
            options: widget.content.options
        })).find('.ues-sandbox').on('click', '.ues-save', function () {
            var thiz = $(this);
            var id = thiz.data('id');
            var sandbox = thiz.closest('.ues-sandbox');
            $('input', sandbox).each(function () {
                var el = $(this);
                opts[el.attr('name')] = el.val();
            });
            $('select', sandbox).each(function () {
                var el = $(this);
                opts[el.attr('name')] = el.val();
            });
            updateWidgetOptions(id, opts);
        });
    };

    var loadWidgets = function (start, count) {
        ues.store.gadgets({
            start: start,
            count: count
        }, function (err, data) {
            storeCache.gadget = data;
            $('#middle').find('.ues-widgets .ues-content').html(widgetsListHbs(data));
        });
    };

    var initWidgets = function () {
        $('.ues-widgets').on('mouseenter', '.thumbnail .ues-drag-handle', function () {
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
        }).on('mouseleave', '.thumbnail .ues-drag-handle', function () {
            $(this).draggable('destroy');
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

    var listenLayout = function () {
        $('#middle').find('.ues-designer')
            .children('.ues-toolbar').find('.ues-save').on('click', function () {
                saveDashboard(dashboard);
            }).end().end()
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

    var layoutContainer = function () {
        return $('#middle').find('.ues-designer').html(layoutHbs()).find('.ues-layout');
    };

    var createPage = function (id) {
        var layout = findStoreCache('layout', id);
        $.get(layout.url, function (data) {
            var name = 'landing';
            var title = 'My Dashboard';
            layout.content = data;
            page = {
                title: title,
                layout: layout,
                content: {}
            };
            dashboard.landing = name;
            dashboard.pages[name] = page;
            var container = layoutContainer();
            ues.dashboards.render(container, dashboard, name, function () {
                listenLayout();
            });
        }, 'html');
    };

    var initExisting = function (landing) {
        page = dashboard.pages[landing];
        if (!page) {
            throw 'specified page : ' + landing + ' cannot be found';
        }
        var container = layoutContainer();
        ues.dashboards.render(container, dashboard, landing, function () {
            $('#middle').find('.ues-designer .ues-widget').each(function () {
                var id = $(this).attr('id');
                renderWidgetToolbar(id);
            });
            listenLayout();
        });
    };

    var initFresh = function () {
        ues.store.layouts({
            start: 0,
            count: 20
        }, function (err, data) {
            storeCache.layout = data;
            $('#middle')
                .find('.ues-designer .ues-content').html(layoutsListHbs(data))
                .on('click', '.thumbnails .ues-add', function () {
                    createPage($(this).data('id'));
                });
        });
    };

    var initDashboard = function (db, page) {
        if (db) {
            dashboard = db;
            initExisting(page || db.landing);
            return;
        }
        dashboard = {
            id: randomId(),
            pages: {}
        };
        initFresh();
    };

    initTabs();
    initWidgetToolbar();
    initWidgets();
    loadWidgets(0, 20);
    initDashboard(ues.global.dashboard, ues.global.page);

    //TODO: uncomment this
    /*$('.designer .content').on('mouseenter', '.widget .widget-toolbar', function () {
     $('.tools', $(this)).show();
     }).on('mouseleave', '.widget .widget-toolbar', function () {
     $('.tools', $(this)).hide();
     });*/

});