(function () {

    osapi.container.GadgetHolder.IFRAME_ID_PREFIX_ = '';

    var gadgets = {};

    var subscribeForClient = ues.hub.subscribeForClient;

    ues.hub.subscribeForClient = function (container, topic, conSubId) {
        var clientId = container.getClientID();
        var options = gadgets[clientId];
        if (!options) {
            return subscribeForClient.apply(ues.hub, [container, topic, conSubId]);
        }
        var channel = options.id + '.' + topic;
        console.log('subscribing container:%s topic:%s, channel:%s by %s', clientId, topic, channel);
        return subscribeForClient.apply(ues.hub, [container, channel, conSubId]);
    };

    var plugin = (ues.plugins['gadget'] = {});

    plugin.prepare = function (sandbox, hub) {

    };

    plugin.create = function (sandbox, options, hub) {
        gadgets[options.id] = options;
        ues.gadget(sandbox, options.content.data.url);
    };

    plugin.update = function (sandbox, options, events, hub) {

    };

    plugin.destroy = function (sandbox, hub) {
        $(sandbox).remove('iframe');
    };

}());