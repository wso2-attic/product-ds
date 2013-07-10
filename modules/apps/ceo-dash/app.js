var configs = require('/config.js').config();


var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var sso = function (options) {
    var path = '/_system/config/repository/identity/SAMLSSO/' + options.issuer64,
        server = require('/modules/server.js'),
        registry = server.systemRegistry();
    registry.put(path, {
        properties: {'Issuer': options.issuer, 'SAMLSSOAssertionConsumerURL': options.consumerUrl, 'doSignAssertions': options.doSign, 'doSingleLogout': options.singleLogout, 'useFullyQualifiedUsername': options.useFQUsername}
    });
};

var addSSOConfig = function () {
    sso({'issuer': 'ceo-dash',
        'consumerUrl': configs.ssoConfiguration.appAcs,
        'doSign': 'true',
        'singleLogout': 'true',
        'useFQUsername': 'true',
        'issuer64': 'Y2VvLWRhc2g'});

 };

addSSOConfig();

