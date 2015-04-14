(function () {

    if (!ues.plugins) {
        throw 'ues.plugins cannot be found. Please include ues-plugins.js.';
    }

    var findPlugin = function (type) {
        var plugin = ues.plugins[type];
        if (!plugin) {
            throw 'ues dashboard plugin for ' + type + ' cannot be found';
        }
        return plugin;
    };

    var createWidget = function (container, widget, done) {
        var type = widget.content.type;
        var plugin = findPlugin(type);
        var sandbox = $('<div id="' + widget.id + '" class="ues-widget"></div>');
        sandbox.appendTo(container);
        plugin.create(sandbox, widget, ues.hub, done);
    };

    var destroyWidget = function (widget, done) {
        var plugin = findPlugin(widget.content.type);
        var container = $('#' + widget.id);
        plugin.destroy(container, widget, ues.hub, done);
    };

    var widgetId = function (clientId) {
        return clientId.split('-').pop();
    };

    var wirings;

    var publishForClient = ues.hub.publishForClient;
    ues.hub.publishForClient = function (container, topic, data) {
        console.log('publishing data container:%s, topic:%s, data:%j', container.getClientID(), topic, data);
        var clientId = widgetId(container.getClientID());
        var channels = wirings[clientId + '.' + topic];
        if (!channels) {
            return;
        }
        channels.forEach(function (channel) {
            publishForClient.apply(ues.hub, [container, channel, data]);
        });
    };

    var wires = function (page) {
        var content = page.content;
        var area;
        var blocks;
        var wirez = {};

        var wire = function (wirez, id, listeners) {
            var event;
            var listener;
            for (event in listeners) {
                if (listeners.hasOwnProperty(event)) {
                    listener = listeners[event];
                    if (!listener.on) {
                        continue;
                    }
                    listener.on.forEach(function (notifier) {
                        var channel = notifier.from + '.' + notifier.event;
                        var wire = wirez[channel] || (wirez[channel] = []);
                        wire.push(id + '.' + event);
                    });
                }
            }
        };

        for (area in content) {
            if (content.hasOwnProperty(area)) {
                blocks = content[area];
                blocks.forEach(function (block) {
                    var listeners = block.content.listen;
                    if (!listeners) {
                        return;
                    }
                    wire(wirez, block.id, listeners);
                });
            }
        }
        console.log(wirez);
        return wirez;
    };

    var setDocumentTitle = function (dashboard, page) {
        document.title = dashboard.title + ' | ' + page.title;
    };

    var renderPage = function (element, dashboard, page, done) {
        setDocumentTitle(dashboard, page);
        wirings = wires(page);
        var container;
        var area;
        var layout = $(page.layout.content);
        var content = page.content;
        element.html(layout);
        for (area in content) {
            if (content.hasOwnProperty(area)) {
                container = $('#' + area, layout);
                content[area].forEach(function (options) {
                    createWidget(container, options, function (err) {
                        if (err) {
                            console.error(err);
                        }
                    });
                });
            }
        }
        if (!done) {
            return;
        }
        done();
    };

    var findPage = function (dashboard, id) {
        var i;
        var page;
        var pages = dashboard.pages;
        var length = pages.length;
        for (i = 0; i < length; i++) {
            page = pages[i];
            if (page.id === id) {
                return page;
            }
        }
    };

    var renderDashboard = function (element, dashboard, name, done) {
        name = name || dashboard.landing;
        var page = findPage(dashboard, name);
        if (!page) {
            throw 'requested page : ' + name + ' cannot be found';
        }
        renderPage(element, dashboard, page, done);
    };

    var rewireDashboard = function (page) {
        wirings = wires(page);
    };

    ues.widgets = {
        create: createWidget,
        destroy: destroyWidget
    };

    ues.dashboards = {
        render: renderDashboard,
        rewire: rewireDashboard,
        findPage: findPage
    };

}());