(function () {
    var container = $('.ues-widget-box').first();
    var url = 'http://localhost:9763/dashboards/widgets/usa-map/index.xml';
    ues.gadget(container, url);
}());