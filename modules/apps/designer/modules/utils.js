var log = new Log();
var relativePrefix = function (path) {
    var parts = path.split('/');
    var prefix = '';
    var i;
    var count = parts.length - 3;
    for (i = 0; i < count; i++) {
        prefix += '../';
    }
    return prefix;
};