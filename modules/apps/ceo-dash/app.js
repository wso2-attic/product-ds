var carbon = require('carbon');
var conf = carbon.server.loadConfig('carbon.xml');
var offset = conf.*::['Ports'].*::['Offset'].text();
var hostName = conf.*::['HostName'].text().toString();

if (hostName === null || hostName === '') {
    hostName = 'localhost';
}

var httpPort = 9763 + parseInt(offset, 10);
var httpsPort = 9443 + parseInt(offset, 10);

var process = require('process');
process.setProperty('server.host', hostName);
process.setProperty('http.port', httpPort.toString());
process.setProperty('https.port', httpsPort.toString());

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

