var update = function (ctx, asset) {
    var url = asset.url;
    if (!url.match('/(http://)|(https://)/i')) {
        asset.url = ctx.store + url;
    }
    url = asset.thumbnail;
    if (!url.match('/(http://)|(https://)/i')) {
        asset.thumbnail = ctx.store + url;
    }
};