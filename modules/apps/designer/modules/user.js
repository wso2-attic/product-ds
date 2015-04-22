var current = function () {
    return session.get('user');
};

var login = function (username, password) {
    var carbon = require('carbon');
    var server = new carbon.server.Server();
    if(!server.authenticate(username, password)) {
        return false;
    }
    var user = carbon.server.tenantUser(username);
    session.put('user', user);
    return true;
};

var logout = function () {
    session.remove('user');
};

var authorized = function (perm, action) {
    return true;
};