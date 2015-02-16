var ues = ues || {};
var store = {};

(function () {
    var store = (ues.store = {});

    store.asset = function (id, cb) {
        $.get('widgets.jag?name=' + id, function (data) {
            cb(false, data);
        }, 'json');
    };

    store.assets = function (paging, cb) {
        $.get('widgets.jag?start=' + paging.start + '&count=' + paging.count, function (data) {
            cb(false, data);
        }, 'json');
    };
}());