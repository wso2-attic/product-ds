var db;

(function () {
    var plugins = {};
    db = function (plugin, fn) {
        if(arguments.length === 0) {
            //render dashboard
        }
        plugins[plugin] = {
            fn: fn
        };
    };

}());
