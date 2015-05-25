(function () {

    var domain = ues.global.anonDomain || ues.global.domain;

    var assetsUrl = ues.utils.relativePrefix() + 'assets';

    var store = (ues.store = {});

    store.asset = function (type, id, cb) {
        $.get(assetsUrl + '/' + id + '?domain=' + domain + '&type=' + type, function (data) {
            cb(false, data);
        }, 'json');
    };

    store.assets = function (type, paging, cb) {
        $.get(assetsUrl + '?domain=' + domain + '&start=' + paging.start + '&count=' + paging.count + '&type=' + type, function (data) {
            cb(false, data);
        }, 'json');
    };
}());