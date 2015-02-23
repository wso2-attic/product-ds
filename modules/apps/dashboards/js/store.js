var ues = ues || {};
var store = {};

(function () {
    var store = (ues.store = {});

    store.gadget = function (id, cb) {
        $.get('assets.jag?name=' + id + '&type=gadget', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.gadgets = function (paging, cb) {
        $.get('assets.jag?start=' + paging.start + '&count=' + paging.count + '&type=gadget', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.layout = function (id, cb) {
        $.get('assets.jag?name=' + id + '&type=layout', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.layouts = function (paging, cb) {
        $.get('assets.jag?start=' + paging.start + '&count=' + paging.count + '&type=layout', function (data) {
            cb(false, data);
        }, 'json');
    };


}());