(function () {
    var container = $('.ues-widget-box').first();
    var url = 'http://localhost:9763/dashboards/widgets/usa-map/index.xml';
    CommonContainer.renderGadget(container, url);
}());