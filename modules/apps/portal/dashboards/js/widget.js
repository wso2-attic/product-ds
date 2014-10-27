var ues = ues || {};

(function() {
    var designer = (ues.designer = {});

    designer.widget = function(id, cb) {
        $.get('widgets.jag?id=' + id, function(data) {
            cb(false, data);
        }, 'json');
    };

    designer.widgets = function(paging, cb) {
        $.get('widgets.jag?start=' + paging.start + '&count=' + paging.count, function(data) {
            cb(false, data);
        }, 'json');
    };
}());
