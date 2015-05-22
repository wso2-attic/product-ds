(function () {
    var prefix = ues.utils.relativePrefix();

    var domain = ues.global.domain;

    ues.plugins.uris['store'] = function (uri) {
        return prefix + 'store/' + domain + '/' + uri;
    };
}());