//TODO: enable timeout animations for map and sticky

var sticky_relocate, showSticky, updateGadgets, filterPath, scrollableElement, cc, range, responsivegadget;

$(document).ready(function () {

    /*----------------function definitions---------------------*/

    var initMap = function () {

        var br = new google.maps.LatLng(30.411944, -91.185556);
        var mapOptions = {
            zoom: 2,
            center: br,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        }

        var map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);

        var ctaLayer = new google.maps.KmlLayer('http://www.gelib.com/wp-content/uploads/2009/world-borders_nl.kml', {
            preserveViewport: true
        });
        ctaLayer.setMap(map);

        google.maps.event.addListener(ctaLayer, 'click', function (kmlEvent) {
            var text = kmlEvent.featureData.snippet;
            cc = text.match(/[A-Z]{3}/).toString();
            range = '1992:2008';
		$(".climate-gadget-container").show();
            showSticky();
            updateGadgets();
		$(".climate-gadget-title").show()
        });

    }();

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
    updateGadgets = function () {

        var api;
        var obj;

        var indicators = [
            {
                channel: 'org.uec.geo.data',
                indicator: 'EN.ATM.CO2E.PC'
            },
            {
                channel: 'org.uec.geo.agri',
                indicator: 'AG.LND.AGRI.ZS'
            },
            {
                channel: 'org.uec.geo.elec',
                indicator: 'EG.USE.ELEC.KH.PC'
            },
            {
                channel: 'org.uec.geo.energy',
                indicator: 'EG.USE.PCAP.KG.OE'
            },
            {
                channel: 'org.uec.geo.green',
                indicator: 'EN.ATM.GHGO.KT.CE'
            }
        ];

        for (var i = 0; i < indicators.length; i++) {

            api = 'http://api.worldbank.org/countries/' + cc + '/indicators/' + indicators[i].indicator + '?format=jsonP&date=' + range + '&prefix=?';

            $.getJSON(api, (function (i) {
                return function (data) {
                    obj = data[1];
                    console.log("Channel:" + indicators[i].channel);
                    console.log(obj);
                    UESContainer.inlineClient.publish(indicators[i].channel, obj);
                };
            })(i));
        }
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

    $(window).scroll(sticky_relocate);

    $("#Slider1").slider({
        from: 1990,
        to: 2010,
        heterogeneity: ['50/2000'],
        step: 1,
        format: {
            format: '0000',
            locale: 'us'
        },
        skin: 'round_plastic',
        callback: function (value) {
            if (typeof cc == 'undefined') {
                alert("Please select a country first");
            } else {
                range = value.replace(/;/, ':');
                console.log("range: " + range);
                updateGadgets();

            }

        }
    });

    $("#Slider1").slider('prc', 10, 90);

    //$(window).bind('resize', adjustLayout);
    //$(document).bind('ready', adjustLayout);

    //$(window).bind('resize', responsivegadget);
    //$(document).bind('ready', responsivegadget);

});

