(function () {

    var events = ues.events;

    var plugin = (ues.plugins['widget'] = {});

    plugin.prepare = function (sandbox, hub) {

    };

    plugin.create = function (sandbox, options, hub) {
        var html = '<h2>' + options.name + '</h2>';
        html += '<button class="send btn btn-primary" type="button">Send</button>';
        sandbox.html(html);
        var id = options.id;
        var container = new OpenAjax.hub.InlineContainer(ues.hub, id, {
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
                container: container
            }
        });

        var Hub = function (client, options) {
            this.client = client;
            this.options = options;
        };

        Hub.prototype.on = function (event, done) {
            var hub = this;
            var listeners = hub.options.listen;
            var listener = listeners[event];
            if (!listener) {
                return console.warn('event %s has not defined under listener section, hence ignoring', event);
            }
            var notifiers = listener.on;
            if (!notifiers) {
                return;
            }
            notifiers.forEach(function (notifier) {
                var channel = notifier.from + '.' + notifier.event;
                hub.client.subscribe(channel, done);
                console.log('subscribed for channel:%s by %s', channel, hub.options.id);
            });
        };

        Hub.prototype.emit = function (event, data) {
            var channel = this.options.id + '.' + event;
            console.log('publishing event:%s, data:%s by notifier:%s on channel:%s', event, data, this.options.id, channel);
            this.client.publish(channel, data);
        };

        client.connect(function (client, success, error) {
            var hub = new Hub(client, options);
            sandbox.on('click', '.send', function () {
                hub.emit('user-country', 'LK');
                hub.emit('client-country', 'US');
                //hub.emit('country-code', {});
            });
            hub.on('state', function (state) {
                console.log(state);
            });
        });

    };

    plugin.update = function (sandbox, options, events, hub) {

    };

    plugin.destroy = function (sandbox, hub) {
        $(sandbox).remove('iframe');
    };

}());