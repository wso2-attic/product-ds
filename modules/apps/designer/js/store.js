var ues = ues || {};
var store = {};

(function () {
    var store = (ues.store = {});

    store.gadget = function (id, cb) {
        $.get('assets/' + id + '?type=gadget', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.gadgets = function (paging, cb) {
        $.get('assets?start=' + paging.start + '&count=' + paging.count + '&type=gadget', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.layout = function (id, cb) {
        $.get('assets/' + id + '?type=layout', function (data) {
            cb(false, data);
        }, 'json');
    };

    store.layouts = function (paging, cb) {
        $.get('assets?start=' + paging.start + '&count=' + paging.count + '&type=layout', function (data) {
            cb(false, data);
        }, 'json');
    };


}());