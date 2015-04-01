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

    /*    var sub1 = OpenAjax.hub.IframeContainer.prototype.subscribe;
     OpenAjax.hub.IframeContainer.prototype.subscribe = function() {
     var sid = sub1.apply(this, Array.prototype.slice.call(arguments));
     console.log('----------ruchira---------------' + sid);
     return sid;
     };

     var sub2 = OpenAjax.hub.InlineContainer.prototype.subscribe;
     OpenAjax.hub.InlineContainer.prototype.subscribe = function() {
     var sid = sub2.apply(this, Array.prototype.slice.call(arguments));
     console.log('----------ruchira---------------' + sid);
     return sid;
     };*/

    //opensocial container for the DOM
    var container = new osapi.container.Container(params);

    //Gadget renderer
    var gadget = function (sandbox, url, prefs, params, done) {
        var options = {};
        options[osapi.container.RenderParam.WIDTH] = '100%';
        options[osapi.container.RenderParam.VIEW] = 'home';
        extend(options, params);
        sandbox = (sandbox instanceof jQuery) ? sandbox : $(sandbox);
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
            var fn = configs(ues.configs, ['hub', 'subscribe']);
            return fn ? fn(topic, container) : true;
        },
        onUnsubscribe: function (topic, container) {
            var fn = configs(ues.configs, ['hub', 'unsubscribe']);
            return fn ? fn(topic, container) : true;
        },
        onPublish: function (topic, data, from, to) {
            /*var clientId = to.getClientID();
             var sub = subscriptions[clientId];
             var container = ues.hub.getContainer(clientId);
             container.sendToClient(topic, data, sub.conSubId);*/
            var fn = configs(ues.configs, ['hub', 'publish']);
            return fn ? fn(topic, data, from, to) : true;
        }
    });

    var inlineContainer = new OpenAjax.hub.InlineContainer(hub, 'ues', {
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
    /*
     var Hub = function (client) {

     };

     Hub.prototype.on = function () {

     };

     Hub.prototype.once = function () {

     };

     Hub.prototype.off = function () {

     };*/

    ues.hub = hub;
    ues.container = container;
    ues.client = client;
    ues.gadget = gadget;
    ues.plugins = {};
}());