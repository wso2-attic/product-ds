(function () {

    var gadgetPrefix = (osapi.container.GadgetHolder.IFRAME_ID_PREFIX_ = 'ues-');

    var containerPrefix = 'gadget-';

    var gadgets = {};

    var subscribeForClient = ues.hub.subscribeForClient;

    var containerId = function (id) {
        return containerPrefix + id;
    };

    var gadgetId = function (id) {
        return gadgetPrefix + id;
    };

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

    plugin.create = function (sandbox, widget, hub, done) {
        ues.gadgets.preload(widget.content.data.url, function (err, metadata) {
            var pref;
            var opts = widget.content.options || (widget.content.options = {});
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
            var cid = containerId(widget.id);
            var gid = gadgetId(widget.id);
            var container = $('<div id="' + cid + '" class="ues-gadget-box"></div>').appendTo(sandbox);
            gadgets[gid] = widget;
            ues.gadgets.render(container, widget.content.data.url);
            done(false, widget);
        });
    };

    plugin.update = function (sandbox, widget, hub, done) {

    };

    plugin.destroy = function (sandbox, options, hub, done) {
        $(sandbox).remove('iframe');
    };

}());