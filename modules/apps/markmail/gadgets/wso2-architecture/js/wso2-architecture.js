//chart object
var chartArc = chartArc || null;

gadgets.HubSettings.onConnect = function() {

	gadgets.Hub.subscribe("org.wso2.arc", callback);
};

function callback1(topic, data, subscriberData) {
	document.getElementById("output").innerHTML = "message : " + gadgets.util.escapeString(data + "") + "<br/>" + "received at: " + (new Date()).toString();
}

function callback(topic, obj, subscriberData) {

console.log("---------------------------------------------------------------");

	console.log(obj);
	
	var init = $('#chart-wso2').attr('data-init');

	var values = obj['msg'];
	var title = obj['title'];
	var senders = obj['senders'];
	
	
	console.log(values[0]['month']);
	console.log("data-init" + init);
	//console.log(senders);
	
	var myconfig = {
		baseUrl : '/markmail/js/dojo//'
	};

	var coords = [];
	var labels = [];

	for (var i in values) {
		labels.push({
			value : i,
			text : values[i].month.replace('Z','')
		});
	}
	console.log("Labels: ");
	console.log(labels);


	if (!init) {
		console.log("create");

		require(myconfig, ["dojox/charting/Chart", "dojox/charting/axis2d/Default", "dojox/charting/plot2d/Bars", "dojox/charting/plot2d/Markers", "dojox/charting/themes/Bahamation", "dojo/ready"], function(Chart, Default, Bars, Markers, Bahamation, ready) {
			ready(function() {
				chartArc = new Chart("chart-wso2");
				chartArc.addPlot("default", {
					type : Bars,
					markers : true,
					gap : 5,
					animate : {
						duration : 800
					}
				}).addAxis("x", {
					fixLower : "major",
					fixUpper : "major"
				}).addAxis("y", {
					vertical : true,
					 microTicks: false,
					labels : labels
				}).setTheme(Bahamation);

				console.log("Values length: " + values.length);
				for (i in values) {
					coords.push({
						x : i,
						y : parseInt(values[i]['count'])
					});
				}

				coords.reverse();
				chartArc.addSeries("Series A", coords);
				//var tip = new Tooltip(chartArc,"default");
				chartArc.render();

			});
		});

		$('#chart-wso2').attr('data-init', true);
	} else {
		console.log("update");
		console.log("Values length: " + values.length);

		for (i in values) {
			coords.push({
				x : i,
				y : parseInt(values[i]['count'])
			});
		}

		coords.reverse();
		console.log(coords);
		chartArc.addAxis("y", {
			vertical : true,
			labels : labels
		});
		chartArc.updateSeries("Series A", coords).render();

	}
	
	
	$('#senders ul').empty();
	for(var i in senders){
		$('#senders ul').append('<li><span class="span-left">' + senders[i].email.replace('+',' ') + ' </span><span class="span-right"> ' + senders[i].count + '</span></li>');
	}
	
	
	
}

