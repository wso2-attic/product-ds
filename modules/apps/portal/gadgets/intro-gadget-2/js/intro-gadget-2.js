//chart object
var intro2 = intro2 || null;

gadgets.HubSettings.onConnect = function() {
	gadgets.Hub.subscribe("org.uec.geo.intro2", callbackGadget2);

};

function callback1(topic, data, subscriberData) {
	document.getElementById("output").innerHTML = "message : " + gadgets.util.escapeString(data + "") + "<br/>" + "received at: " + (new Date()).toString();
}

function callbackGadget2(topic, obj, subscriberData) {

	var data = [];

	for (i in obj) {
		data.push([parseInt(obj[i].date), parseInt(obj[i].value)]);
	}
	data.reverse();

	$.plot("#placeholder", [data], {
		series : {
			lines : {
				show : true
			},
			points : {
				show : true
			},
			color: '#009E90'
		},

		grid : {
			hoverable : true,
			clickable : true
		},

		yaxis : {
			show : true,
			tickFormatter : function suffixFormatter(val, axis) {
				return val / 1000000;
			}
		},
		xaxis : {
			show : true,
			ticks : 4,
			tickDecimals : 0
		}
	}, colors = ['red', 'orange', 'green', 'blue', 'purple']);

	$("#placeholder").bind("plothover", function(event, pos, item) {
		if (item) {
			if (previousPoint != item.dataIndex) {

				previousPoint = item.dataIndex;

				$("#tooltip").remove();
				var x = item.datapoint[0], y = item.datapoint[1];

				showTooltip(item.pageX, item.pageY, x + " , " + y);
			}
		} else {
			$("#tooltip").remove();
			previousPoint = null;
		}
	});

	function showTooltip(x, y, contents) {
		$("<div id='tooltip'>" + contents + "</div>").css({
			top : y + 5,
			left : x + 5
		}).appendTo("body").fadeIn(200);
	}

}

