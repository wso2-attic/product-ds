var ues = ues || {};

(function () {
    //search overridden configs through ues.configs object
    var configs = function (configs, args) {
        var find = function (o, args) {
            var pop = args.shift();
            if (typeof o !== 'object' || !o[pop]) {
                return null;
            }
            if (!args.length) {
                return o[pop];
            }
            return find(o[pop], args);
        };
        return find(configs, args);
    };

    var extend = function (options, extended) {
        var name;
        for (name in extended) {
            if (extended.hasOwnProperty(name)) {
                options[name] = extended[name];
            }
        }
    };

    var merge = function (options) {
        var args = Array.prototype.slice.call(arguments, 1);
        var extended = configs(ues.configs, args);
        if (!extended) {
            return options;
        }
        return extended ? extend(options, extended) : options;
    };

    var params = {};
    params[osapi.container.ContainerConfig.RENDER_DEBUG] = true;
    merge(params, 'container');

    console.log(params);

    //opensocial container for the DOM
    var container = new osapi.container.Container(params);

    //Gadget renderer
    var gadget = function (sandbox, url, prefs, params, done) {
        var options = {};
        options[osapi.container.RenderParam.WIDTH] = '100%';
        options[osapi.container.RenderParam.VIEW] = 'home';
        extend(options, params);
        sandbox.each(function () {
            var sandbox = $(this);
            options[osapi.container.RenderParam.HEIGHT] = sandbox.height();
            var site = container.newGadgetSite(sandbox[0]);
            container.navigateGadget(site, url, prefs, options, function (metadata) {
                if (metadata.error) {
                    done ? done(metadata.error) : console.log(metadata.error);
                    return;
                }
                if (done) {
                    done();
                }
            });
        });
    };

    //Initializing OpenAjax ManagedHub
    var hub = new OpenAjax.hub.ManagedHub({
        onSubscribe: function (topic, container) {
            console.log(container.getClientID() + ' subscribes to this topic \'' + topic + '\'');
            return true;
        },
        onUnsubscribe: function (topic, container) {
            console.log(container.getClientID() + ' unsubscribes from tthis topic \'' + topic + '\'');
            return true;
        },
        onPublish: function (topic, data, from, to) {
            console.log(from.getClientID() + ' publishes \'' + data + '\' to topic \'' + topic + '\' subscribed by ' + to.getClientID());
            return true;
        }
    });

    var inlineContainer = new OpenAjax.hub.InlineContainer(hub, 'ues',
        {
            Container: {
                onSecurityAlert: function (source, alertType) {
                    //Handle client-side security alerts
                },
                onConnect: function (container) {
                    //Called when client connects
                },
                onDisconnect: function (container) {
                    //Called when client disconnects
                }
            }
        }
    );

    var client = new OpenAjax.hub.InlineHubClient({
        HubClient: {
            onSecurityAlert: function (source, alertType) {
            }
        },
        InlineHubClient: {
            container: inlineContainer
        }
    });

    // Linking ManagedHub with opensocial pubsub2
    gadgets.pubsub2router.init({
        hub: hub
    });

    ues.hub = hub;
    ues.container = container;
    ues.client = client;
    ues.gadget = gadget;
}());