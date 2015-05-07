var log = new Log();

var dir = '/store/';

var assetsDir = function (type) {
    return dir + type + '/';
};

var findOne = function (type, id) {
    var parent = assetsDir(type);
    var file = new File(parent + id);
    if (!file.isExists()) {
        return null;
    }
    file = new File(file.getPath() + '/' + type + '.json');
    if (!file.isExists()) {
        return null;
    }
    file.open('r');
    var asset = JSON.parse(file.readAll());
    file.close();
    return asset;
};

var find = function (type, query, start, count) {
    var parent = new File(assetsDir(type));
    var assetz = parent.listFiles();
    var assets = [];
    assetz.forEach(function (asset) {
        if (!asset.isDirectory()) {
            return;
        }
        asset = new File(asset.getPath() + '/' + type + '.json');
        asset.open('r');
        assets.push(JSON.parse(asset.readAll()));
        asset.close();
    });
    return assets;
};

var create = function (type, asset) {
    var parent = new File(assetsDir(type));
    var file = new File(asset.id, parent);
    file.mkdir();
    file = new File(type + '.json', file);
    file.open('w');
    file.write(JSON.stringify(asset));
    file.close();
};

var update = function (asset) {

};

var remove = function (id) {

};