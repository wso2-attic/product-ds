//TODO: enable timeout animations for map and sticky

var sticky_relocate, showSticky, updateGadgets, filterPath, scrollableElement, cc, from, to, responsivegadget, scale = [];

$(document).ready(function () {

    /*----------------function definitions---------------------*/

    sticky_relocate = function () {
        try {
            var window_top = $(window).scrollTop();
            var div_top = $('#sticky-anchor').offset().top;
            if (window_top > div_top)
                $('#sticky').addClass('stick')
            else
                $('#sticky').removeClass('stick');
        } catch (e) {

        }
    };
    showSticky = function () {
        //$('#preloader').show();
        $('#map-tip').hide();
        $('#sticky').animate({
            opacity: 1
        }, "slow");
        setTimeout(function () {
            var targetOffset = $('#sticky').offset().top - 65;
            $(scrollElem).animate({
                scrollTop: targetOffset
            }, 1000, function () {

                $('#gadgets').animate({
                    opacity: 1
                }, 1000);
            });
            $('#alert-start').hide();

        }, 600);
    };
    updateGadgets = function (selectedList) {

        from = from || '200601';
        to = to || '201001';

        selectedList = selectedList || 0;

        var api;


        var count;

        var senders;

        var channel;

        var list;

        $('.total-counts').html('');

        var lists = [
            {
                channel: 'org.wso2.arc',
                list: 'architecture'
            },
            {
                channel: 'org.wso2.carbon-commits',
                list: 'commits'
            },
            {
                channel: 'org.wso2.jira',
                list: 'jira'
            },
            {
                channel: 'org.wso2.carbon-dev',
                list: 'carbon-dev'
            }
        ];

        /*
         {
         channel : 'org.wso2-dev',
         list : 'dev'
         }
         */

        // get list counts

        $.each(lists, function (index, value) {

            $.getJSON('api/wso2.jag?list=' + value.list + '&from=' + from + '&to=' + to + '&action=totalPosts', function (data) {

                count = data.count;
                $('.total-counts').append('<div data-listId="' + index + '" class="span2 txt-center count-' + value.list + '"></div>');
                $('.count-' + value.list).html('<h1>' + count + '</h1><h4>' + value.list + '</h4>');
            });
        });

        $.each(lists, function (index, value) {
            var obj;
            var channel = lists[index].channel;
            var list = lists[index].list;

            $.ajax({
                url: 'api/wso2.jag?list=' + list + '&from=' + from + '&to=' + to,
                dataType: 'json',
                //async : false,
                success: function (data) {
                    obj = data;

                    $.ajax({
                        url: 'api/wso2.jag?list=' + list + '&from=' + from + '&to=' + to + '&action=sender',
                        dataType: 'json',
                        //async : false,
                        success: function (data) {

                            obj['senders'] = data.msg;
                            UESContainer.inlineClient.publish(channel, obj);
                        }
                    });

                }
            });

        });

    };
    filterPath = function (string) {
        return string.replace(/^\//, '').replace(/(index|default).[a-zA-Z]{3,4}$/, '').replace(/\/$/, '');
    };
    var locationPath = filterPath(location.pathname);

    scrollableElement = function (els) {
        for (var i = 0, argLength = arguments.length; i < argLength; i++) {
            var el = arguments[i], $scrollElement = $(el);
            if ($scrollElement.scrollTop() > 0) {
                return el;
            } else {
                $scrollElement.scrollTop(1);
                var isScrollable = $scrollElement.scrollTop() > 0;
                $scrollElement.scrollTop(0);
                if (isScrollable) {
                    return el;
                }
            }
        }
        return [];
    };
    var scrollElem = scrollableElement('html', 'body');

    // responsive gadgets
    $.gridster = {
        positions: [],
        full_width: false
    };

    responsivegadget = function () {

        var idx = 1;
        var sizey = 0;

        if ($('.span12').width() <= 724 && $.gridster.full_width === false) {// when scaled down
            $.gridster.positions.length = 0;
            // clear previous positions
            $.gridster.full_width = true;

            $('#sticky').css('top', 0);

            $('.gridster > ul').height('auto');
            $('.gridster > ul >li').each(function () {
                var row = $(this).attr('data-row');
                var col = $(this).attr('data-col');
                var prevSizey = parseInt($(this).prev().attr('data-sizey'));

                (prevSizey) ? sizey += prevSizey : 0;

                $.gridster.positions.push({
                    "row": row,
                    "col": col
                });
                $(this).removeClass('gs_w');
                $(this).attr({
                    'data-col': '1',
                    'data-row': sizey + 1
                });
                idx++;
            });

        } else if ($('.span12').width() > 724 && $.gridster.full_width === true) {// when scaled up
            $.gridster.full_width = false;
            $('#sticky').css('top', 66);

            $('.gridster > ul >li').each(function () {
                $(this).attr({
                    'data-col': $.gridster.positions[idx - 1].col,
                    'data-row': $.gridster.positions[idx - 1].row
                });
                $(this).addClass('gs_w');
                idx++;
            });

        }
    };
    /*----------------function calls---------------------*/

    if (!localStorage.firstVisit) {
        $('#introModal').modal({
            keyboard: true,
            backdrop: 'static'
        });
        localStorage.firstVisit = true;
    }

    $(window).scroll(sticky_relocate);

    var month = [];
    month[0] = "Jan";
    month[1] = "Feb";
    month[2] = "Mar";
    month[3] = "Apr";
    month[4] = "May";
    month[5] = "Jun";
    month[6] = "Jul";
    month[7] = "Aug";
    month[8] = "Sep";
    month[9] = "Oct";
    month[10] = "Nov";
    month[11] = "Dec";

    for (var y = 2005; y <= 2012; y++) {

        scale.push(y);

    }


    $("#Slider1").slider({
        from: 0,
		to: monthDiff(new Date(2005, 01),new Date()),
		step: 1,
		dimension: '',
		scale: getScale(),
		limits: false,
		calculate: function (value) {
		    var year = Math.floor(value / 12);
		    return year + 2005 + ' ' + month[value % 12];
		},
		skin: 'round_plastic',
		callback: function (value) {

		    var split = value.split(';');

		    from = (2005 + Math.floor(split[0] / 12));
		    to = (2005 + Math.floor(split[1] / 12));

		    var m1 = 1 + (split[0] % 12);
		    from += m1 < 10 ? "0" + m1 : m1;

		    var m2 = 1 + (split[1] % 12);
		    to += m2 < 10 ? "0" + m2 : m2;

            updateGadgets();

            $("#Slider1").slider('value', split[0], split[1]);

        }
    });

    $("#Slider1").slider('value', 12, 84);
	from = from || "200601"; 
		to = to || "201201";

    /*	$(window).bind('resize', responsivegadget);
     $(document).bind('ready', responsivegadget);
     */
    setTimeout(updateGadgets, 3000);

});
var getScale = function(){
	var scale = [];
	var d1 = 2005, d2 = new Date().getFullYear(); 
	for(var y = d1; y <= d2; y++){ scale.push(y) }
	return scale;
};
var monthDiff = function(d1, d2) {
    var months;
	d1 = new Date(d1.getFullYear(), 01)
    months = (d2.getFullYear() - d1.getFullYear()) * 12;
    //months -= d1.getMonth() + 1;
    //months += d2.getMonth();
    return months <= 0 ? 0 : months;
}

