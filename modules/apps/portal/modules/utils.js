var log = new Log();

var relativePrefix = function (path) {
    var parts = path.split('/');
    var prefix = '';
    var i;
    var count = parts.length - 3;
    for (i = 0; i < count; i++) {
        prefix += '../';
    }
    return prefix;
};

var tenantedPrefix = function (prefix, domain) {
    if (!domain) {
        return prefix;
    }
    var configs = require('/configs/designer.json');
    var carbon = require('carbon');
    if (domain === carbon.server.superTenant.domain) {
        return prefix;
    }
    return prefix + configs.tenantPrefix.replace(/^\//, '') + '/' + domain + '/';
};

var sandbox = function (context, fn) {
    var carbon = require('carbon');
    var options = {};

    if (context.anonDomain) {
        options.domain = context.anonDomain;
        if (context.anonDomain === context.domain) {
            options.username = context.username;
        }
    } else {
        if (context.domain) {
            options.username = context.username;
            options.domain = context.domain;
        } else {
            options.domain = carbon.server.tenantDomain();
        }
    }
    options.tenantId = carbon.server.tenantId(options);
    carbon.server.sandbox(options, fn);
};

var allowed = function (roles, allowed) {
    var hasRole = function (role, roles) {
        var i;
        var length = roles.length;
        for (i = 0; i < length; i++) {
            if (roles[i] == role) {
                return true;
            }
        }
        return false;
    };
    var i;
    var length = allowed.length;
    for (i = 0; i < length; i++) {
        if (hasRole(allowed[i], roles)) {
            return true;
        }
    }
    return false;
};

var context = function (user, domain) {
    var ctx = {
        anonDomain: domain
    };
    if (user) {
        ctx.username = user.username;
        ctx.domain = String(user.domain);
    }
    return ctx;
};

var tenantExists = function (domain) {
    var carbon = require('carbon');
    var tenantId = carbon.server.tenantId({
        domain: domain
    });
    log.info(tenantId);
    return tenantId !== -1;
};

var currentContext = function () {
    var PrivilegedCarbonContext = Packages.org.wso2.carbon.context.PrivilegedCarbonContext;
    var context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
    var username = context.getUsername();
    return {
        username: username,
        domain: context.getTenantDomain(),
        tenantId: context.getTenantId()
    };
};