(function () {

    if (!ues.plugins) {
        throw 'ues.plugins cannot be found. Please include ues-plugins.js.';
    }

    var renderLayout = function (layout, done) {
        $.get(layout.url, function (data) {
            done(false, $(data));
        }, 'html');
    };

    var renderWidget = function (container, block, done) {
        var plugin = ues.plugins[block.content.type];
        if (!plugin) {
            return console.warn('ues dashboard plugin for ' + block.content.type + ' cannot be found');
        }
        var sandbox = $('<div id="' + block.id + '" class="ues-widget"></div>');
        sandbox.appendTo(container);
        plugin.create(sandbox, block, ues.hub, done);
    };

    var wirings;

    var publishForClient = ues.hub.publishForClient;
    ues.hub.publishForClient = function (container, topic, data) {
        console.log('publishing data container:%s, topic:%s, data:%j', container.getClientID(), topic, data);
        var clientId = container.getClientID();
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
        document.title = document.title + ' | ' + (page.title || dashboard.title);
    };

    var renderPage = function (element, dashboard, page, done) {
        setDocumentTitle(dashboard, page);
        wirings = wires(page);
        var container;
        var area;
        var layout = $(page.layout.content);
        var content = page.content;
        for (area in content) {
            if (content.hasOwnProperty(area)) {
                container = $('#' + area, layout);
                content[area].forEach(function (options) {
                    renderWidget(container, options);
                });
            }
        }
        element.html(layout);
        if (!done) {
            return;
        }
        done();
    };

    var renderDashboard = function (element, dashboard, name, done) {
        var page = dashboard.pages[name];
        if (!page) {
            throw 'Requested page : ' + name + ' cannot be found';
        }
        renderPage(element, dashboard, page, done);
    };

    ues.widgets = {
        render: renderWidget
    };

    ues.dashboards = {
        render: renderDashboard
    };

}());