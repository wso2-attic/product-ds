(function () {
    var container = $('.gadget');
    var url = 'http://localhost:9763/dashboards/widgets/usa-map/usa.xml';
    var site = CommonContainer.newGadgetSite(container[0]);
    var params = {};
    params[osapi.container.RenderParam.WIDTH] = '100%';
    params[osapi.container.RenderParam.VIEW] = 'home';
    params[osapi.container.RenderParam.HEIGHT] = container.height();
    CommonContainer.navigateGadget(site, url, {}, params, function (metadata) {
        if (metadata.error) {
            gadgets.error('There was an error rendering ' + url);
            return;
        }
    });
}());