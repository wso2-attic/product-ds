var caramel = require('caramel');
require('modules/app.js');
var climate = require('climate.js').config();
var sites = require('/sites/site.json');
var rxtPath = '/sites/', deployer = require('/modules/deployer.js'), 
context = caramel.configs().context, 
base = climate.server.http + context + rxtPath, 
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
			url : sites.sites[i].attributes.overview_url,
			thumbnail : sites.sites[i].attributes.images_thumbnail,
			banner : sites.sites[i].attributes.images_banner,
			status : sites.sites[i].attributes.overview_status
		});
	}

	log.info("Climate site deployed");
};
populateSites();
