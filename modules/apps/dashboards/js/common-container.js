var CommonContainer;

(function () {

    var getPrefs = function (site, url, done) {
        console.log(Array.prototype.slice.call(arguments));
        done({
            username: 'ruchira.wageesha@gmail.com'
        });
    };

    var setPrefs = function (site, url, prefs) {
        console.log(Array.prototype.slice.call(arguments));
    };

    var params = {};
    params[osapi.container.ContainerConfig.RENDER_DEBUG] = true;
    params[osapi.container.ContainerConfig.GET_PREFERENCES] = getPrefs;
    params[osapi.container.ContainerConfig.SET_PREFERENCES] = setPrefs;
    CommonContainer = new osapi.container.Container(params);

    CommonContainer.renderGadget = function (container, url, prefs, params, done) {
        params = params || {};
        params[osapi.container.RenderParam.WIDTH] = '100%';
        params[osapi.container.RenderParam.VIEW] = 'home';
        container.each(function () {
            var container = $(this);
            params[osapi.container.RenderParam.HEIGHT] = container.height();
            var site = CommonContainer.newGadgetSite(container[0]);
            CommonContainer.navigateGadget(site, url, prefs, params, function (metadata) {
                if (metadata.error) {
                    done ? done(metadata.error) : console.log(metadata.error);
                    return;
                }
                if (done) {
                    done();
                }
            });
        });
    };
}());