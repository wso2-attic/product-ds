var caramel = require('caramel');

caramel.configs({
    context: '/store',
    cache: true,
    negotiation: true,
    themer: function () {
        /*var meta = caramel.meta();
        if(meta.request.getRequestURI().indexOf('gadget') != -1) {
            return 'modern';
        }*/
        return 'store';
    }/*,
    languagesDir: '/i18n',
    language: function() {
        return 'si';
    }*/
});

var configs = require('/store.js').config();
var server = require('/modules/server.js');

server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var store = require('/modules/store.js');
store.init({

});