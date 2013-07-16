var caramel = require('caramel');
require('/app.js');
var portal = require('/config.js').config();
var sites = {
	"sites": [
		{
			"name": 'Markmail',
			"tags": "report,mail,markMail",
			"rate": 3,
			"attributes": {
				"overview_status": "CREATED",
				"overview_name": 'Markmail',
				"overview_version": '1.0.0',
				"overview_url": '/markmail/',
				"overview_provider": 'admin',
				"images_thumbnail": '/markmail/thumbnail.jpg',
				"images_banner": '/markmail/banner.jpg',
                "overview_description": "Markmail is a microsite that provides a glance at WSO2 Engineering. " +
"User can choose preferred time period to analyze the displayed data like, commit, architecture descussions etc. This microsite can be taken as a sample when creating sites in UES",

            }
		}
	]
};
var rxtPath = '/sites/', deployer = require('/modules/deployer.js'), 
context = caramel.configs().context, 
base = portal.server.http + context + rxtPath, 
log = new Log('markmail.site.deployer');

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

	log.info("Default markmail site deployed");
};
populateSites();
