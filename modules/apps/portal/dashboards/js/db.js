var ues = ues || {};
(function () {

    var plugins = {};

    Handlebars.registerHelper('dump', function (o) {
        return JSON.stringify(o);
    });

    Handlebars.registerHelper('include', function (contexts) {
        var i, type,
            length = contexts ? contexts.length : 0,
            html = '',
            renderData = function (data) {
                var template,
                    context = typeof data.context === 'function' ? data.context() : data.context;
                if (data.partial) {
                    console.log('Rendering template ' + data.partial);
                    template = '<div id="' + context._.id + '">' + Handlebars.compile($('#' + data.partial).html()) + '</div>';
                } else {
                    console.log('No template, serializing data');
                    template = JSON.stringify;
                }
                return template(context);
            };
        console.log('Including : ' + JSON.stringify(contexts));
        if (length == 0) {
            return html;
        }
        type = typeof contexts;
        if (contexts instanceof Array) {
            for (i = 0; i < length; i++) {
                html += renderData(contexts[i]);
            }
        } else if (contexts instanceof String || type === 'string' ||
            contexts instanceof Number || type === 'number' ||
            contexts instanceof Boolean || type === 'boolean') {
            html = contexts.toString();
        } else {
            html = renderData(contexts);
        }
        return new Handlebars.SafeString(html);
    });

    ues.render = function (o) {
        var template = Handlebars.compile($('#' + o.partial).html());
        var html = template(o.context);
        //TODO: differentiate server side and client side rendering
        $('#content').find('> div').html(html);
    };

    ues.plugin = function (plugin, fn) {
        plugins[plugin] = {
            fn: fn
        };
    };

    ues.hbs = function (tmpl, data) {
        var template = Handlebars.compile($('#' + tmpl).html());
        return template(data);
    };

    ues.render(page);

}());