//GadgetID
var curId = 0;

var host;

var testGadgets = [];

$(function() {

	//initializing the common container
	CommonContainer.init();
	host = resolveHost();
	// TODO: we need to integrate the REST api get the following gadgets, in milestone 2
	//testGadgets = [ host + '/portal/gadgets/wso2-architecture/wso2-architecture.xml'];
	testGadgets = [host + '/_system/governance/gadgetxmls/admin/wso2-carbon-commits/1.0.xml', host + '/_system/governance/gadgetxmls/admin/wso2-jira/1.0.xml', host + '/_system/governance/gadgetxmls/admin/wso2-carbon-dev/1.0.xml'];
	drawGadgets();
});

var drawGadgets = function() {
	CommonContainer.preloadGadgets(testGadgets, function(result) {
		for (var gadgetURL in result) {
			if (!result[gadgetURL].error) {
				buildGadget(result, gadgetURL);
				curId++;
			}
		}
	});
};

var gadgetTemplate = '<div id="gadget-site" class="portlet-content span12"><div class="gadget-title"></div></div>';

var buildGadget = function(result, gadgetURL) {

	result = result || {};
	var element = getNewGadgetElement(result, gadgetURL);

	$(element).data('gadgetSite', CommonContainer.renderGadget(gadgetURL, curId));

};

var getNewGadgetElement = function(result, gadgetURL) {
	result[gadgetURL] = result[gadgetURL] || {};

	var newGadgetSite = gadgetTemplate;
	newGadgetSite = newGadgetSite.replace(/(gadget-site)/g, '$1-' + curId);

	$(newGadgetSite).appendTo($('#gadgetArea-' + curId));

	var gadgetTitle = result[gadgetURL].modulePrefs.title;
	//var gadgetDesc = result[gadgetURL].modulePrefs.description;

	$('#gadgetArea-' + curId + ' .gadget-title').html('<h2>' + gadgetTitle + '</h2>');

	return $('#gadget-site-' + curId).get([0]);
}
var resolveHost = function() {
	//http://<domain>:<port>/
    return document.location.protocol + "//" + document.location.host + '/portal/gadget-proxy.jag?gadget=';
}

