(function () {

    var gadgets = {};

    /*var configs = ues.configs || (ues.configs = {});
    var hub = configs.hub || (configs.hub = {});
    hub.publish = function (topic, data, from, to) {
        var clientId = from.getClientID();
        //clientId = 'pujw6jwm3t';
        console.log('topic:%s, data:%s, from:%s to:%s', topic, data, clientId, to.getClientID());
        var wire = wires[clientId];
        if (!wire) {
            return false;
        }
        var listeners = wire[topic];
        if (!listeners) {
            return false;
        }
        listeners.forEach(function (listener) {
            var container = ues.hub.getContainer(listener);
            var subscription = subscriptions[listener];
            var sub;
            if (!subscription || !(sub = subscription[topic])) {
                console.error('Subscription cannot be found for the wiring event: %s, from:%s to:%s',
                    topic, clientId, listener);
                return;
            }
            container.sendToClient(topic, data, sub.conSubId);
        });
        return true;
    };*/


    var subscribeForClient = ues.hub.subscribeForClient;

    ues.hub.subscribeForClient = function (container, topic, conSubId) {
        var clientId = container.getClientID();
        var options = gadgets[clientId];
        if (!options) {
            return subscribeForClient.apply(ues.hub, [container, topic, conSubId]);
        }
        var listeners = options.listen;
        var listener = listeners[topic];
        if (!listener) {
            return console.warn('event %s has not defined under listener section, hence ignoring', topic);
        }
        var notifiers = listener.on;
        if (!notifiers) {
            return;
        }
        var notifier = notifiers[0];
        var channel = notifier.from + '.' + notifier.event;

        var hubSubId = subscribeForClient.apply(ues.hub, [container, channel, conSubId]);
        console.log('subscription request from:%s, topic:%s, conSubId:%s, hubSubId:%s',
            clientId, topic, conSubId, hubSubId);
        console.log('subscribed for channel:%s by %s', channel, options.id);
        return hubSubId;
    };

    var plugin = (ues.plugins['gadget'] = {});

    plugin.prepare = function (sandbox, hub) {

    };

    plugin.create = function (sandbox, options, hub) {
        gadgets['__gadget_' + options.id] = options;
        ues.gadget(sandbox, options.data.url);
    };

    plugin.update = function (sandbox, options, events, hub) {

    };

    plugin.destroy = function (sandbox, hub) {
        $(sandbox).remove('iframe');
    };

}());