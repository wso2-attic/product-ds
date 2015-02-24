(function () {

    if (!ues.plugins) {
        throw 'ues.plugins cannot be found. Please include ues-plugins.js.';
    }

    var Hub = function (client) {
        this.client = client;
    };

    Hub.prototype.on = function () {

    };

    Hub.prototype.once = function () {

    };

    Hub.prototype.emit = function () {

    };

    var renderLayout = function (layout, done) {
        $.get(layout.url, function (data) {
            done(false, $(data));
        }, 'html');
    };

    var renderBlock = function (container, block) {
        var plugin = ues.plugins[block.type];
        if (!plugin) {
            return console.warn('ues dashboard plugin for ' + block.type + ' cannot be found');
        }
        plugin.create(container, block, ues.hub);
    };

    var subscriptions = {};

    /*setTimeout(function () {
     var container = ues.hub.getContainer('__gadget_gadget-2');
     container.sendToClient('org.wso2.ues.samples.ch', {
     msg: 'A message from g1',
     id: 123456
     }, 0);
     }, 10000);*/

    var wires = function(page) {
            
    };

    ues.page = function (element, page, done) {
        renderLayout(page.layout, function (err, layout) {
            var container;
            var area;
            var content = page.content;
            for (area in content) {
                if (content.hasOwnProperty(area)) {
                    container = $('#' + area, layout);
                    content[area].forEach(function (options) {
                        var sandbox = $('<div id="' + options.id + '" class="ues-widget"></div>');
                        sandbox.appendTo(container);
                        renderBlock(sandbox, options);
                    });
                }
            }
            element.html(layout);
            done();
        });
    };
}());