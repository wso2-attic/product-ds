var CommonContainer;

(function () {
    var params = {};
    params[osapi.container.ContainerConfig.RENDER_DEBUG] = true;
    CommonContainer = new osapi.container.Container(params);

    CommonContainer.renderGadget = function (container, url, prefs, params, done) {
        params = params || {};
        params[osapi.container.RenderParam.WIDTH] = '100%';
        params[osapi.container.RenderParam.VIEW] = 'home';
        params[osapi.container.RenderParam.HEIGHT] = container.height();
        var site = CommonContainer.newGadgetSite(container[0]);
        CommonContainer.navigateGadget(site, url, prefs, params, function (metadata) {
            if (metadata.error) {
                return done(metadata.error);
            }
        });
    };
}());