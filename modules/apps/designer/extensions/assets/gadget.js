var update = function (ctx, asset) {
    var url = asset.data.url;
    if (!url.match('/(http://)|(https://)/i')) {
        asset.data.url = ctx.store + url;
    }
    url = asset.thumbnail;
    if (!url.match('/(http://)|(https://)/i')) {
        asset.thumbnail = ctx.store + url;
    }
};