var current;
var login;
var logout;
var authorized;
var roles;

(function () {
    var log = new Log();

    current = function () {
        return session.get('user');
    };

    login = function (username, password) {
        var carbon = require('carbon');
        var server = new carbon.server.Server();
        if (!server.authenticate(username, password)) {
            return false;
        }
        var user = carbon.server.tenantUser(username);
        var um = new carbon.user.UserManager(server);
        user.roles = um.getRoleListOfUser(user.username);
        session.put('user', user);
        return true;
    };

    logout = function () {
        session.remove('user');
    };

    authorized = function (perm, action) {
        return true;
    };

    roles = function () {
        var carbon = require('carbon');
        var server = new carbon.server.Server();
        var um = new carbon.user.UserManager(server);
        return um.allRoles();
    };

}());