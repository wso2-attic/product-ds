var config;
(function () {
    config = function () {
        var log = new Log(),
            pinch = require('pinch.min.js').pinch,
           config = {
    "server": {
        "https": "%https.host%",
        "http": "%http.host%"
    },    
    "adminRole": "admin",
    "user": {
        "username": "admin",
        "password": "admin"
    },
    "userRoles": ["portal"]
   
},
            process = require('process'),
            localIP = process.getProperty('server.host'),
            httpPort = process.getProperty('http.port'),
            httpsPort = process.getProperty('https.port');

        pinch(config, /^/, function (path, key, value) {
            if ((typeof value === 'string') && value.indexOf('%https.host%') > -1) {
                return value.replace('%https.host%', 'https://' + localIP + ':' + httpsPort);
            } else if ((typeof value === 'string') && value.indexOf('%http.host%') > -1) {
                return value.replace('%http.host%', 'http://' + localIP + ':' + httpPort);
            }
            return  value;
        });
       // log.info(config);
        return config;
    };
})();