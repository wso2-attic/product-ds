$(function () {


    var page = {
        content: {}
    };

    page.title = 'My Dashboard';


    var savePageJson = function () {
        $.ajax({
            url: 'registry.jag',
            type: 'POST',
            data: JSON.stringify(page),
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            success: function () {
            },
            statusCode: {
                200: function () {
                    alert('Saved Successfully!!!!');
                }
            }
        });
    };


    var exitPreview = function () {

        $('#preview-content').removeClass('dashboard-preview');

        $('#home-toolbar').show();
        $('#dashboard-label').hide();
        $('#preview-mode-tools').hide();
        $('#designer-mode-tools').show();
    };

    var showPreview = function () {

        $('#preview-content').addClass('dashboard-preview');

        $('#dashboard-label').show();
        $('#preview-mode-tools').show();
        $('#designer-mode-tools').hide();
        $('#home-toolbar').hide();
    };


    var updatePageJson = function (listenerJson, theListener, selectedValue, listenerJsonRef) {
        var length = listenerJson[theListener].on.length;
        var event;
        for (var i = 0; i < length; i++) {
            if (listenerJson[theListener].on[i].from == selectedValue) {
                event = listenerJson[theListener].on[i].event;
                break;
            }
        }

        var on = listenerJsonRef[theListener].on || (listenerJsonRef[theListener].on = []);
        var onLength = on.length;

        var found = false;
        for (var j = 0; j < onLength; j++) {
            if (on[j].from == selectedValue) {
                found = true;
                break;
            }
        }

        if (!found) {
            on.push({
                "event": event,
                "from": selectedValue
            });
        }
    };

    var updateListenerJson = function (listenerJson, notifyingGadgetArray, listeners) {
        var length = notifyingGadgetArray.length;
        for (var i = 0; i < length; i++) {
            listenerJson[listeners].on.push({
                "event": notifyingGadgetArray[i].notifier,
                "from": notifyingGadgetArray[i].id
            });
        }
    };

    var getNotifyingGadget = function (type, notifyingGadgetArray, containers, i, notifiers) {
        if (page.content[containers][i].notify.hasOwnProperty(notifiers)) {
            if (type == page.content[containers][i].notify[notifiers].type) {
                var gadgetJson = $.extend(true, {}, page.content[containers][i]);
                gadgetJson.notifier = notifiers;
                notifyingGadgetArray.push(gadgetJson);
            }
        }
    };

    var findNotifiers = function (type, notifyingGadgetArray, containers, i) {
        for (var notifiers in page.content[containers][i].notify) {
            if (page.content[containers][i].notify.hasOwnProperty(notifiers)) {
                getNotifyingGadget(type, notifyingGadgetArray, containers, i, notifiers);
            }
        }
    };

    var findInContainerArray = function (type, notifyingGadgetArray, containers) {
        var length = page.content[containers].length;
        for (var i = 0; i < length; i++) {
            findNotifiers(type, notifyingGadgetArray, containers, i);
        }
    };

    var getNotifiers = function (type) {
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
    };


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


    var designer = Handlebars.compile($('#designer-hbs').html());
    var widgets = Handlebars.compile($('#thumbs-hbs').html());
    var options = Handlebars.compile($('#options-hbs').html());
    var layouts = Handlebars.compile($('#layouts-hbs').html());

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

        $('#dashboard-label').hide();
        $('#preview-mode-tools').hide();

        //when layout select button is clicked
        //todo jquery on instead of click
        $('.btn-primary-layout').click(function () {

            var btnId = $(this).data('id');
            var layoutJson;
            var length = data.length;

            for (var i = 0; i < length; i++) {
                var name = data[i].name;
                var theLayout = data[i];
                if (name == btnId) {
                    layoutJson = theLayout;
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
                            var arrayLength = content.length;

                            //deep copy
                            //jQuery = $
                            var listenerJson = $.extend(true, {}, data.listen);
                            var listenerJsonRef = page.content[targetId][arrayLength - 1].listen;

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
                                var listenerJsonRef;
                                for (var containers in page.content) {
                                    var found = false;
                                    if (page.content.hasOwnProperty(containers)) {
                                        if (!page.content[containers][0]) {
                                            continue;
                                        }
                                        var length = page.content[containers].length;
                                        for (var i = 0; i < length; i++) {
                                            if (page.content[containers][i].id == divId) {
                                                listenerJson = $.extend(true, {}, page.content[containers][i].listen);
                                                listenerJsonRef = page.content[containers][i].listen;
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (found) {
                                            break;
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

                                $('.form-control').change(function () {
                                    var selectedValue = this.options[this.selectedIndex].value;
                                    //noinspection JSPotentiallyInvalidUsageOfThis
                                    var theListener = this.id;
                                    updatePageJson(listenerJson, theListener, selectedValue, listenerJsonRef);
                                });

                            });

                            $('.form-control').change(function () {
                                var selectedValue = this.options[this.selectedIndex].value;
                                var theListener = this.id;
                                updatePageJson(listenerJson, theListener, selectedValue, listenerJsonRef);
                            });
                        });
                    }
                });


                $('.btn-primary-save').click(function () {
                    savePageJson();
                });


                $('.btn-primary-preview').click(function () {
                    showPreview();

                    $('.btn-primary-exitPreview').click(function () {
                        exitPreview();
                    });

                    $('.btn-primary-saveAsJag').click(function () {
                        alert('Saved');
                    });

                });

            }, 'text');
        });
    });


    $('.widgets').on('mouseenter', '.thumbnail .drag-handle', function () {
        $(this).draggable({
            cancel: false,
            appendTo: 'body',
            helper: 'clone',
            start: function () {
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