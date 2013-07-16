var caramel = require('caramel');
require('/app.js');
var portal = require('/config.js').config();
var sites = {
    "sites": [
        {
            "name": "ClimateApp",
            "tags": "report,climate",
            "rate": 4,
            "attributes": {
                "overview_status": "CREATED",
                "overview_name": "ClimateApp",
                "overview_version": "1.0.0",
                "overview_url": "/climate/",
                "overview_provider": "admin",
                "overview_description": "Climate Change is a microsite that visualize the statistics of various geological locations on climate change. " +
"User can choose the country and the preferred time period to analyze the displayed data. This microsite can be taken as a sample when creating sites in UES",
                "images_thumbnail": "/climate/thumbnail.jpg",
                "images_banner": "/climate/banner.jpg"
            }
        }
    ]
};
var rxtPath = '/sites/', deployer = require('/modules/deployer.js'), 
context = caramel.configs().context, 
base = portal.server.http + context + rxtPath, 
log = new Log('climate.site.deployer');

var populateSites = function() {
	var slength = sites.sites.length;
	for( i = 0; i < slength; i++) {
		var name = sites.sites[i].name;

		var path = base + name + '/';
		deployer.site({
			name : sites.sites[i].name,
			tags : sites.sites[i].tags.split(','),
			rate: sites.sites[i].rate,
			provider : sites.sites[i].attributes.overview_provider,
			version : sites.sites[i].attributes.overview_version,
            description : sites.sites[i].attributes.overview_description,
			url : portal.server.http + sites.sites[i].attributes.overview_url,
			thumbnail : portal.server.http + sites.sites[i].attributes.images_thumbnail,
			banner : portal.server.http + sites.sites[i].attributes.images_banner,
			status : sites.sites[i].attributes.overview_status
		});
	}

	log.info("Default climate sites deployed");
};
populateSites();
