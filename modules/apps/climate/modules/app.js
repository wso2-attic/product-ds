var caramel = require('caramel');


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


caramel.configs({
    context: '/portal',
    negotiation: true,
    themer: function () {
        return 'portal';
    }
});

var configs = require('climate.js').config();
var portal = require('portal.js');

configs.login = portal.login;
configs.logout = portal.logout;
configs.register = portal.register;

var server = require('server.js');
server.init(configs);

var user = require('user.js');
user.init(configs);

portal.init(configs);
