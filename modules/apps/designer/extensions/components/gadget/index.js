(function () {

    var gadgetPrefix = (osapi.container.GadgetHolder.IFRAME_ID_PREFIX_ = 'sandbox-');

    var containerPrefix = 'gadget-';

    var gadgets = {};

    var subscribeForClient = ues.hub.subscribeForClient;

    var containerId = function (id) {
        return containerPrefix + id;
    };

    var gadgetId = function (id) {
        return gadgetPrefix + containerPrefix + id;
    };

    ues.hub.subscribeForClient = function (container, topic, conSubId) {
        var clientId = container.getClientID();
        var data = gadgets[clientId];
        if (!data) {
            return subscribeForClient.apply(ues.hub, [container, topic, conSubId]);
        }
        var component = data.component;
        var channel = component.id + '.' + topic;
        console.log('subscribing container:%s topic:%s, channel:%s by %s', clientId, topic, channel);
        return subscribeForClient.apply(ues.hub, [container, channel, conSubId]);
    };

    var plugin = (ues.components['gadget'] = {});

    var createPanel = function () {
        var html =
            '<div class="panel panel-default">' +
            '<div class="panel-heading">' +
            '<h3 class="panel-title"></h3>' +
            '</div>' +
            '<div class="panel-body"></div>' +
            '</div>';
        return $(html);
    };

    plugin.create = function (sandbox, component, hub, done) {
        var content = component.content;
        ues.gadgets.preload(content.data.url, function (err, metadata) {
            var pref;
            var opts = content.options || (content.options = {});
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
            var cid = containerId(component.id);
            var gid = gadgetId(component.id);
            var panel = createPanel();
            var container = $('<div id="' + cid + '" class="ues-component-box-gadget"></div>');
            panel.find('.panel-title').html(content.title);
            container.appendTo(panel.find('.panel-body'));
            panel.appendTo(sandbox);
            var site = ues.gadgets.render(container, content.data.url);
            gadgets[gid] = {
                component: component,
                site: site
            };
            done(false, component);
        });
    };

    plugin.update = function (sandbox, component, hub, done) {

    };

    plugin.destroy = function (sandbox, component, hub, done) {
        var gid = gadgetId(component.id);
        var data = gadgets[gid];
        var site = data.site;
        ues.gadgets.remove(site.getId());
        done(false);
    };

}());