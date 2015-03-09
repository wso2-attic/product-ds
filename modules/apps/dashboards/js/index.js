$(function () {


    var page = {
        content: {}
    };

    page.title = 'My Dashboard';

    /**
     * Tab initialization
     */
    $('#left')
        .find('.nav-tabs a')
        .click(function (e) {
            e.preventDefault();
            var el = $(this);
            el.tab('show');
        });

    $('#right').find('.properties-handle').click(function () {
        $('#right').find('.navbar').toggle();
    });


    var designer = Handlebars.compile($("#designer-hbs").html());
    var widgets = Handlebars.compile($("#thumbs-hbs").html());
    var options = Handlebars.compile($("#options-hbs").html());
    var layouts = Handlebars.compile($("#layouts-hbs").html());

    var startValue = 0;
    var noOfValues = 20;

    ues.store.gadgets({
        start: startValue,
        count: noOfValues
    }, function (err, data) {
        $('#middle')
            .find('.widgets .content').html(widgets(data)).end()
            .find('.thumbnails').on('click', '.add-button', function () {
            });
    });


    ues.store.layouts({
        start: 0,
        count: 20
    }, function (err, data) {

        $('#middle')
            .find('.designer .content').html(layouts(data));

        //when layout select button is clicked
        //todo jquery on instead of click
        $('.btn-primary-layout').click(function () {

            var btnId = $(this).data('id');
            var layoutJson;
            var length = data.length;

            for (var i = 0; i < length; i++) {
                var name = data[i].name;
                if (name == btnId) {
                    layoutJson = data[i];
                    break;
                }
            }

            //add layout to page.json
            page.layout = layoutJson;

            $.get(layoutJson.url, function (data) {
                $('#middle')
                    .find('.designer .content').html(data);

                $('.ues-widget-box').droppable({
                    //activeClass: 'ui-state-default',
                    hoverClass: 'ui-state-hover',
                    //accept: ':not(.ui-sortable-helper)',
                    drop: function (event, ui) {

                        //$(this).find('.placeholder').remove();
                        var id = ui.helper.data('id');

                        //get the container id
                        var targetId = $(event.target).attr('id');

                        var droppable = $(this);
                        ues.store.gadget(id, function (err, data) {
                            var id = Math.random().toString(36).slice(2);
                            droppable.html('<div id=' + id + ' class="widget"></div>');
                            ues.gadget($('#' + id), data.data.url);

                            data.id = id;
                            var content = page.content[targetId] || (page.content[targetId] = []);
                            content.push(data);

                            //deep copy
                            //jQuery = $
                            var listenerJson = $.extend(true, {}, data.listen);

                            for (var listeners in listenerJson) {
                                if (listenerJson.hasOwnProperty(listeners)) {
                                    listenerJson[listeners].on = [];

                                    var type = listenerJson[listeners].type;
                                    var notifyingGadgetArray = getNotifiers(type);

                                    if (notifyingGadgetArray) {
                                        updateListenerJson(listenerJson, notifyingGadgetArray, listeners);
                                    }
                                }
                            }

                            //todo printing jsons only for testing
                            console.log(listenerJson);
                            console.log(page);

                            var optionContent = $('#middle').find('.designer .optionContent');
                            optionContent.html(options(listenerJson));

                            $('.widget').click(function () {
                                var divId = this.id;
                                var listenerJson;
                                for (var containers in page.content) {
                                    if (page.content.hasOwnProperty(containers)) {
                                        if (!page.content[containers][0]) {
                                            continue;
                                        }
                                        var length = page.content[containers].length;
                                        for (var i = 0; i < length; i++) {
                                            if (page.content[containers][i].id == divId) {
                                                listenerJson = $.extend(true, {}, page.content[containers][i].listen);
                                            }
                                        }
                                    }
                                }

                                if (listenerJson) {
                                    for (var listeners in listenerJson) {
                                        if (listenerJson.hasOwnProperty(listeners)) {
                                            listenerJson[listeners].on = [];

                                            var type = listenerJson[listeners].type;
                                            var notifyingGadgetArray = getNotifiers(type);

                                            if (notifyingGadgetArray) {
                                                updateListenerJson(listenerJson, notifyingGadgetArray, listeners);
                                            }
                                        }
                                    }
                                }
                                optionContent.html(options(listenerJson));
                            });
                        });
                    }
                });

            }, 'text');
        });
    });


    function updateListenerJson(listenerJson, notifyingGadgetArray, listeners) {
        var length = notifyingGadgetArray.length;
        for (var i = 0; i < length; i++) {
            listenerJson[listeners].on.push({
                "event": notifyingGadgetArray[i].notifier,
                "from": notifyingGadgetArray[i].id,
                "name": notifyingGadgetArray[i].name
            });
        }
    }

    function getNotifyingGadget(type, notifyingGadgetArray, containers, i, notifiers) {
        if (page.content[containers][i].notify.hasOwnProperty(notifiers)) {
            if (type == page.content[containers][i].notify[notifiers].type) {
                var gadgetJson = $.extend(true, {}, page.content[containers][i]);
                gadgetJson.notifier = notifiers;
                notifyingGadgetArray.push(gadgetJson);
            }
        }
    }

    function findNotifiers(type, notifyingGadgetArray, containers, i) {
        for (var notifiers in page.content[containers][i].notify) {
            if (page.content[containers][i].notify.hasOwnProperty(notifiers)) {
                getNotifyingGadget(type, notifyingGadgetArray, containers, i, notifiers);
            }
        }
    }

    function findInContainerArray(type, notifyingGadgetArray, containers) {
        var length = page.content[containers].length;
        for (var i = 0; i < length; i++) {
            findNotifiers(type, notifyingGadgetArray, containers, i);
        }
    }

    function getNotifiers(type) {
        var notifyingGadgetArray = [];
        for (var containers in page.content) {
            if (page.content.hasOwnProperty(containers)) {
                if (!page.content[containers][0]) {
                    continue;
                }
                findInContainerArray(type, notifyingGadgetArray, containers);
            }
        }
        return notifyingGadgetArray;
    }


    $('.widgets').on('mouseenter', '.thumbnail .drag-handle', function () {
        $(this).draggable({
            cancel: false,
            appendTo: 'body',
            helper: 'clone',
            start: function (event, ui) {
                console.log('dragging');
                $('#left').find('a[href="#designer"]').tab('show');
            },
            stop: function () {
                //$('#left a[href="#widgets"]').tab('show');
            }
        });
    }).on('mouseleave', '.thumbnail .drag-handle', function () {
        $(this).draggable('destroy');
    });


    $('#sandbox').load(function () {
        $(this).contents()
            .find('body')
            .html('<div class="container"><div class="row"><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-9 ues-widget-box"></div></div></div>');
    });

    $('.preview-toggle').click(function () {
        $('#sandbox').toggle();
    });

    var source = function (html) {
        return '<div class="container">' + html + '</div>';
    };

    $('.preview').click(function () {
        var html = $('.sandbox .container-fluid').html();
        console.log(html);
        $('#sandbox').contents()
            .find('body')
            .html(source(html));
        /*$('.container').hide();
         $('#sandbox').contents()
         .find('body')
         .html('<div class="container"><div class="row"><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div><div class="col-lg-3 ues-widget-box"></div></div></div>');
         */
    });

});