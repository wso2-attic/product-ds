var layout, dummy_gadget_block = 50, block_params = {
	max_width : 6,
	max_height : 6
}, MARGINS_RATIO = 0.1, COLS = block_params.max_width;

var onShowAssetLoad, tmpGadgetInfo, isQueryRan = false, isSelectionChanged = false;

var flow_data = {};

(function($) {

	var extensions = {
		resize_widget_dimensions : function(options) {
			if (options.widget_margins) {
				this.options.widget_margins = options.widget_margins;
			}

			if (options.widget_base_dimensions) {
				this.options.widget_base_dimensions = options.widget_base_dimensions;
			}

			this.min_widget_width = (this.options.widget_margins[0] * 2) + this.options.widget_base_dimensions[0];
			this.min_widget_height = (this.options.widget_margins[1] * 2) + this.options.widget_base_dimensions[1];

			var serializedGrid = this.serialize();
			this.$widgets.each($.proxy(function(i, widget) {
				var $widget = $(widget);
				var data = serializedGrid[i];
				this.resize_widget($widget, data.sizex, data.sizey);
			}, this));

			this.generate_grid_and_stylesheet();
			this.get_widgets_from_DOM();
			this.set_dom_grid_height();
			return false;
		}
	};
	$.extend($.Gridster, extensions);
})(jQuery);

$.validator.addMethod("alphanumeric", function(value, element) {
        return this.optional(element) || /^[a-z0-9\-]+$/i.test(value);
    }, "Must be alphanumeric.");


$(function() {

	var $STORE_MODAL = $('#modal-add-gadget');
	var $LAYOUTS_GRID = $('#layouts_grid');
	var newDimensions = calculateNewDimensions();

	var widgetId = 1;

	layout = $('.layouts_grid ul').gridster({
		widget_base_dimensions : newDimensions[0],
		widget_margins : newDimensions[1],

		serialize_params : function($w, wgd) {
			var gadgetInfo = $($w.get(0)).data('gadgetInfo');
			var wclass = ($(wgd.el[0]).attr('class').indexOf('static') != -1) ? 'static' : '';
			var gadgetId = $w.find(".add-gadget-item > div").attr('id');
			var gadgetRenderInfo = UESContainer.getGadgetInfo(gadgetId);
			var prefs = gadgetRenderInfo && gadgetRenderInfo.opt.prefs || {};

			return {
				wid : widgetId++,
				x : wgd.col,
				y : wgd.row,
				title : $w.find('input').val(),
				width : wgd.size_x,
				height : wgd.size_y,
				prefs : JSON.stringify(prefs).replace(/"/g, "'"),
				wclass : wclass,
				url : gadgetInfo && gadgetInfo.attributes.overview_url_temp
			};

		},
		max_cols : 6,
		max_size_x : 6
	}).data('gridster');

	setTimeout(function() {
		drawGrid(newDimensions[0][0]);
	}, 2000);

	function calculateNewDimensions() {
		var containerWidth = $('#layouts_grid').innerWidth();
		var newMargin = containerWidth * MARGINS_RATIO / (COLS * 2);
		var newSize = containerWidth * (1 - MARGINS_RATIO) / COLS;
		return [[newSize, newSize], [newMargin, newMargin]];
	}

	var timeOut;

	function resize() {
		var newDimensions = calculateNewDimensions();

		//window.setTimeout(function() {
		layout.resize_widget_dimensions({
			widget_base_dimensions : newDimensions[0],
			widget_margins : newDimensions[1]
		});
		//}, 2000);

		drawGrid(newDimensions[0][0]);

		var iframes = $('iframes');
		$.each(iframes, function(i, w) {
			//refreshGadget(i);
		});

		clearTimeout(timeOut);
		timeOut = setTimeout(setGridOffsetTop, 500);

	}

	function drawGrid(blockSize) {
		var h = $LAYOUTS_GRID.innerWidth() / blockSize;
		var v = $LAYOUTS_GRID.innerHeight() / blockSize;

		$('#grid-guides').html('').hide();

		for (var i = 0; i < v; i++) {
			for (var j = 0; j < h; j++) {

				var plus = '<i class="designer-guides-plus" data-row="' + (i + 1) + '" data-col="' + (j + 1) + '"></i>';
				$('#grid-guides').append(plus).fadeIn("slow");
			}
		}
	}

	var itemTmp = Handlebars.compile($('#item-template').html());

	$('#dummy-gadget').resizable({
		grid : dummy_gadget_block,
		containment : "#dummy-gadget-container",
		stop : function(event, ui) {
			var h = Math.round($(this).height()) / dummy_gadget_block;
			var w = Math.round($(this).width()) / dummy_gadget_block;
			var display = w + "x" + h;
			$(this).find('#dummy-size').html(display).attr({
				'data-w' : w,
				'data-h' : h
			});
		}
	});

	var registerEventsToWidget = function(widget) {
		var addGadgetBtn = $(widget).find('.btn-add-gadget');
		addGadgetBtn.click(onGadgetSelectButton);
	};

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~start gadget-gen ui~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	//Select Datasource window

	function onGadgetSelectButton() {
		lastClickedGadgetButton = $(this);
		$('#modal-add-gadget-wizard').modal('show');
	}


	$('#modal-add-gadget-wizard').on('hidden', function() {
		flow_data = {};
		$("#wizard-add-gadget").steps('reset');
		$('#wizard-add-gadget > .steps > ul > li.done').removeClass('done').addClass('disabled');
		$('.wizard-dsType').removeClass('active');
		$('#wizard-dsTypeSel').val('');

		$('#gadgetArea-preview').html($("#gadgetPreviewPlaceholder").html());

		var cWindow = $('#store-gadget-div').find('iframe').get(0).contentWindow;
		cWindow.deselectGadget();

		$('#wizard-add-gadget-btn-prev').addClass('disabled');

	});

	$("#wizard-add-gadget").steps({
		headerTag : "h3",
		bodyTag : "section",
		transitionEffect : "fade",
		onStepChanging : function(event, currentIndex, newIndex) {

			if (newIndex == 4) {
				$('#wizard-add-gadget-btn-next').hide();
				$('#wizard-add-gadget-btn-finish').show();
			} else {
				$('#wizard-add-gadget-btn-next').show();
				$('#wizard-add-gadget-btn-finish').hide();
			}

			switch (currentIndex) {
				case 0:
					var dsType = $('#wizard-dsTypeSel').val();
					flow_data.dataSource = dsType;
					break;

				case 1:
					var conSettings = {};
					$('#wizard-add-gadget-p-1').find('.control-group').each(function() {
						conSettings[$(this).find('label').html()] = $(this).find('input').val();
					});

					flow_data.conSettings = conSettings;
					break;

				case 2:
					if (!isQueryRan) {
						var queryData = {};
						$('#wizard-add-gadget-p-2').find('.control-group').each(function() {
							queryData[$(this).find('label').html()] = $(this).find('input').val() || $(this).find('textarea').val();
						});

						flow_data.appName = $('#inp-dashboard').val();
						flow_data.queryData = queryData;
						getQueryData();
					}
					break;

			}

			switch (newIndex) {
				case 1:

					var nextWindowData = {
						createConnection : metadata.dataSourcesDescriptions[flow_data.dataSource]
					};

					var source = $("#create-new-connection").html().replace(/\[\[/g, '{{').replace(/\]\]/g, '}}');
					var template = Handlebars.compile(source);
					$('#wizard-add-gadget-p-1').html(template(nextWindowData));
					break;

				case 2:
					if (!flow_data.queryData) {
						var window3Data = metadata.datasourceWindow_3[flow_data.dataSource];
						if (window3Data) {
							var nextWindowData = {
								sqlEditor : window3Data
							};

							var source = $("#sql-query-editor").html().replace(/\[\[/g, '{{').replace(/\]\]/g, '}}');
							var template = Handlebars.compile(source);
							$('#wizard-add-gadget-p-2').html(template(nextWindowData));

							$('.inp-query').change(function() {
								isQueryRan = false;
							});

							//  $('#modal-create-new-connection').modal('hide');
							//$('#modal-sql-query-editor').modal('show');
						} else {
							// $('#modal-create-new-connection').modal('hide');
							// $STORE_MODAL.modal('show');
							//alert("Select gadget");
						}
					}

					break;

				case 4:
					if (isSelectionChanged || isQueryRan) {
						getDataFormat();
					}
					break;

			}

			return (currentIndex > newIndex) ? true : $('#form-wizard').valid({
				rules : {
					chartTitle : {
						alphanumeric : true
					}
				}
			});
		}
	});

	//Next Click of 4th window (Gadget store window)
	onShowAssetLoad = function() {
		var cWindow = $('#store-gadget-div').find('iframe').get(0).contentWindow;
		if (cWindow.addListener) {
			cWindow.addListener(function(gadgetInfo) {
				tmpGadgetInfo = gadgetInfo;
				isSelectionChanged = true;

			});
		}
	};

	var getQueryData = function() {
		caramel.ajax({
			type : 'POST',
			url : "apis/gadgetGen?action=queryDbAll",
			data : JSON.stringify(flow_data),
			success : function(result) {
				flow_data.column_headers = result.tableHeaders;
			},
			contentType : 'application/json',
			dataType : 'json'
		});
		isQueryRan = true;
	}
	var getDataFormat = function() {
		isSelectionChanged = false;
		isQueryRan = false;
		caramel.ajax({
			type : 'POST',
			url : tmpGadgetInfo.attributes.overview_dataformat,
			success : generateDataMapping,
			contentType : 'application/json',
			dataType : 'json'
		});

	}
	var generateDataMapping = function(tableData) {
		var divCont = populateMappingRow(tableData.dataColumns, flow_data.column_headers, true);
		$('#modal-data-mapping').html(divCont);

		flow_data.dataColumns = tableData.dataColumns;

		var nextWindowData = {
			gadget_type : tmpGadgetInfo.attributes.overview_name,
			dataLabels : tableData.dataLabels
		};

		var source = $("#data-mapping-extension").html().replace(/\[\[/g, '{{').replace(/\]\]/g, '}}');
		var template = Handlebars.compile(source);
		$('#modal-data-mapping-extension-space').html(template(nextWindowData));

		$('#mapping-add-series-btn').bind('click', addSeriesBtnClick);
		//To add more series

		$('#btn-preview-gadget').bind('click', function(e) {
			e.preventDefault();
			$('.gadget-preview-loader').fadeIn("fast");
			processFieldMapping('preview');
		});

	}
	var newlabelID = 0;

	var populateMappingRow = function(dataColumns, columnHeaders, isFirst) {
		var columns = [];
		if (!isFirst && dataColumns.length > 1) {
			var cloneDataColumns = dataColumns.slice(1);
			columns = cloneDataColumns;
		} else {
			columns = dataColumns;
		}
		var nextWindowData = {
			id : newlabelID++,
			dataColumns : columns,
			columnHeaders : columnHeaders
		};
		var source = $("#data-mapping").html().replace(/\[\[/g, '{{').replace(/\]\]/g, '}}');
		var template = Handlebars.compile(source);
		return template(nextWindowData);

	}
	var addSeriesBtnClick = function(e) {
		e.preventDefault();
		var divCont = populateMappingRow(flow_data.dataColumns, flow_data.column_headers, false);
		$('#modal-data-mapping').append(divCont);
	}
	var processFieldMapping = function(mode) {
		var mappingData = [];
		var url = 'apis/gadgetGen?action=createJag';

		url = (mode == 'preview') ? url + '&mode=preview' : url;

		$('#modal-data-mapping').children().each(function() {
			var series = {}
			if (mappingData[0]) {
				series = jQuery.extend(true, {}, mappingData[0]);
			}
			var firstdiv = $(this).children().first();
			series[firstdiv.children()[0].innerHTML] = firstdiv.find('input').val();
			$(this).children().next().each(function() {
				series[$(this).children()[0].innerHTML] = $(this).find(":selected").text();
			});
			mappingData.push(series);
		});
		var dashboard = $('#inp-dashboard').val();
		flow_data.chartTitle = $('#chart-title-input').val();
		flow_data.refreshSequence = $('#refresh-sequence-input').val();

		var labelData = {};
		$('#data-labels').find('.control-group').each(function() {
			labelData[$(this).find('label').html()] = $(this).find('input').val();
		});

		flow_data.mappingData = mappingData;
		flow_data.dataLabels = labelData;
		flow_data.chartLocation = tmpGadgetInfo.attributes.overview_location;
		flow_data.chartType = tmpGadgetInfo.attributes.overview_name;
		flow_data.chartOptions = tmpGadgetInfo.attributes.overview_chartoptions;

		caramel.ajax({
			type : 'POST',
			url : url,
			data : JSON.stringify(flow_data),
			success : insertGadgetToTarget(mode),
			contentType : 'application/json',
			dataType : 'json'
		});

	}
	var insertGadgetToTarget = function(mode) {

		return function(data) {
			var modPrefs = {};
			var prefs = {};
			prefs.dataSource = data.jagPath;
			prefs.updateGraph = flow_data.refreshSequence;
			modPrefs.prefs = prefs;

			var gadgetLi;

			//$('#modal-data-mapper').modal('hide');

			if (mode == 'preview') {
				gadgetLi = $('#gadget-preview');
				gadgetLi.data('gadgetInfo', tmpGadgetInfo);
				insertGadgetPreview(gadgetLi, data.gadgetLocation + tmpGadgetInfo.attributes.overview_url, modPrefs);
			} else {
				tmpGadgetInfo.attributes.overview_url_temp = data.gadgetLocation + tmpGadgetInfo.attributes.overview_url
				gadgetLi = lastClickedGadgetButton.parents('li');
				gadgetLi.data('gadgetInfo', tmpGadgetInfo);
				insertGadget(gadgetLi, tmpGadgetInfo.attributes.overview_url_temp, modPrefs, flow_data.chartTitle);
				var placeholder = lastClickedGadgetButton.siblings('.designer-placeholder');
				lastClickedGadgetButton.remove();
				placeholder.remove();
				deleteTempFiles();
			}
		}
	}
	var deleteTempFiles = function() {
		tmpGadgetInfo = {};
		caramel.ajax({
			type : 'POST',
			url : 'apis/gadgetGen?action=deleteTemp',
			data : flow_data.appName,
			success : function() {
				$('#modal-add-gadget-wizard').modal('hide');
				flow_data = {};
			},
			contentType : 'application/json',
			dataType : 'json'
		});
	}

	$('#wizard-add-gadget-btn-prev').click(function() {
		$('a[href=#previous]').click();
	});
	$('#wizard-add-gadget-btn-next').click(function() {
		$('a[href=#next]').click();
	});
	$('#wizard-add-gadget-btn-finish').click(function() {
		processFieldMapping('dashboard');
	});

	$('#wizard-add-gadget-btn-prev, #wizard-add-gadget-btn-next').bind('click', function() {
		var cssClass = $('.actions > ul > li').eq(0).attr('class');
		$('#wizard-add-gadget-btn-prev').removeClass().addClass('btn btn-primary btn-large ' + cssClass);

	})

	$('body').on('click', '.wizard-dsType', function(e) {
		e.preventDefault();
		$('.wizard-dsType').removeClass('active');
		$(this).toggleClass('active');
		$('#wizard-dsTypeSel').val($(this).attr('data-dsType'));
	});

	$('body').on('click', '.btn-validateCon', function(e) {
		e.preventDefault();
		var conSettings = {};
		$('#wizard-add-gadget-p-1').find('.control-group').each(function() {
			conSettings[$(this).find('label').html()] = $(this).find('input').val();
		});

		flow_data.conSettings = conSettings;

		caramel.ajax({
			type : 'POST',
			url : "apis/gadgetGen?action=validateCon",
			data : JSON.stringify(flow_data),
			success : function(result) {
				alert(result.message);
			},
			contentType : 'application/json',
			dataType : 'json'
		});
	});

	$('body').on('click', '.btn-execQuery', function(e) {
		e.preventDefault();
		var queryData = {};
		$('#wizard-add-gadget-p-2').find('.control-group').each(function() {
			queryData[$(this).find('label').html()] = $(this).find('input').val() || $(this).find('textarea').val();
		});

		flow_data.appName = $('#inp-dashboard').val();
		flow_data.queryData = queryData;

		isSelectionChanged = true;

		caramel.ajax({
			type : 'POST',
			url : "apis/gadgetGen?action=queryDbAll",
			data : JSON.stringify(flow_data),
			success : renderDatasetTable,
			contentType : 'application/json',
			dataType : 'json'
		});

		isQueryRan = true;

	});

	var renderDatasetTable = function(result) {
		isQueryRan = true;
		$('#wizard-add-gadget-p-2 .well').animate({
			'margin-top' : 0
		});

		flow_data.column_headers = result.tableHeaders;
		var source = $("#sql-query-table").html().replace(/\[\[/g, '{{').replace(/\]\]/g, '}}');
		var template = Handlebars.compile(source);
		$('#sql-editor-dataset').html(template(result));
	}
	//------------------------------------------------------------------------end of gadget-gen ui -------------------------------

	var eventRegistered = false;
	// is an event is resisted to show-asset gadget to get the selected gadget.

	function drawGadgets() {

		var mode = $('#inp-view-mode').val(), layoutFormat, template, layoutType = $('#inp-layout').val();
		layoutFormat = (mode == 'view' || mode == '') ? $('.layout_block:not(.static)') : getLayoutFormat(layoutType);

		for (var i = 0; i < layoutFormat.length; i++) {
			template = itemTmp();
			var itemLayout = layoutFormat[i];
			var widget = layout.add_widget(template, itemLayout.width, itemLayout.height, itemLayout.x, itemLayout.y);
			registerEventsToWidget(widget);
		}
		setGridOffsetTop();
	}

	var lastClickedGadgetButton = null;

	$STORE_MODAL.on('hidden', function() {
	});

	UESContainer.renderGadget('store-gadget-div', portalGadgets.store);

	//id to be use in dynamically added gadgets.
	var id = 1;

	function insertGadget(parentEl, url, pref, title) {
		id++;
		var gadgetDiv = parentEl.find('.add-gadget-item');
		var idStr = 'gadgetArea-d' + id;
		gadgetDiv.html('<div id="' + idStr + '">');
		UESContainer.renderGadget(idStr, url, pref || {}, function(gadgetInfo) {
			if (gadgetInfo.meta.modulePrefs) {
				//parentEl.find('.grid_header').append('<input class="gadget-title-txt" value="' + gadgetInfo.meta.modulePrefs.title + '">');
				parentEl.find('.grid_header').append('<input class="gadget-title-txt" value="' + title + '">');
				parentEl.find('.show-widget-pref').show();
			}
		});
		//deleteTempFiles();
	}

	function insertGadgetPreview(parentEl, url, pref) {

		var idStr = 'gadgetArea-preview';
		if ($('#' + idStr).length) {
			UESContainer.removeGadget(idStr);
		}
		parentEl.html('<div id="' + idStr + '">');
		UESContainer.renderGadget(idStr, url, pref || {}, function(gadgetInfo) {
			$('.gadget-preview-loader').fadeOut("fast");
			if (gadgetInfo.meta.modulePrefs) {
				//parentEl.find('.grid_header').append('<input class="gadget-title-txt" value="' + gadgetInfo.meta.modulePrefs.title + '">');
				//parentEl.find('.show-widget-pref').show();
			}
		});
	}

	function refreshGadget(iframe) {
		var parentDiv = iframe.parents('div');

		iframe.ready(function() {
			iframe.height(parentDiv.parents('li').height() - 90);
		});

		iframe.get(0) && iframe.get(0).contentDocument.location.reload(true);
	}

	function getLayoutFormat(layoutType) {
		var layoutFormat;
		switch (layoutType) {
			case 'rows':
				layoutFormat = [{
					"x" : 1,
					"y" : 2,
					"width" : 6,
					"height" : 2
				}, {
					"x" : 1,
					"y" : 4,
					"width" : 6,
					"height" : 2
				}, {
					"x" : 1,
					"y" : 6,
					"width" : 6,
					"height" : 2
				}];
				break;
			case 'columns':
				layoutFormat = [{
					"x" : 1,
					"y" : 2,
					"width" : 2,
					"height" : 6
				}, {
					"x" : 3,
					"y" : 2,
					"width" : 2,
					"height" : 6
				}, {
					"x" : 5,
					"y" : 2,
					"width" : 2,
					"height" : 6
				}];
				break;

			case 'composite':

				layoutFormat = [{
					"x" : 1,
					"y" : 2,
					"width" : 2,
					"height" : 4
				}, {
					"x" : 3,
					"y" : 2,
					"width" : 4,
					"height" : 1
				}, {
					"x" : 3,
					"y" : 3,
					"width" : 4,
					"height" : 3
				}];
				break;
			default:
			case 'grid':

				layoutFormat = [{
					"x" : 1,
					"y" : 2,
					"width" : 2,
					"height" : 2
				}, {
					"x" : 3,
					"y" : 2,
					"width" : 2,
					"height" : 2
				}, {
					"x" : 5,
					"y" : 2,
					"width" : 2,
					"height" : 2
				}, {
					"x" : 1,
					"y" : 4,
					"width" : 2,
					"height" : 2
				}, {
					"x" : 3,
					"y" : 4,
					"width" : 2,
					"height" : 2
				}, {
					"x" : 5,
					"y" : 4,
					"width" : 2,
					"height" : 2
				}];
				break;

		}
		return layoutFormat;
	}


	$('#btn-add-dummy-gadget').click(function(e) {
		e.preventDefault();
		var $dummy = $('#dummy-size');
		var w = Number($dummy.attr('data-w'));
		var h = Number($dummy.attr('data-h'));
		var widget = layout.add_widget(itemTmp(), w, h, 1, 2);
		registerEventsToWidget(widget);
		$('.dropdown.open .dropdown-toggle').dropdown('toggle');
	});

	$('#btn-preview-dash').click(function() {
		if ($(this).data('tooltip') == 'hide') {
			var dashboard = $('#inp-dashboard').val();
			var win = window.open('/' + dashboard, '_blank');
			win.focus();
		}
	});

	$("#btn-exit-view").click(function() {
		$('.sub-navbar-designer-view').slideUp("fast", function() {
			changeMode('design');
		});
	});

	$('.close-widget').live('click', function(e) {
		e.preventDefault();
		var widget = $(this).closest('.gs_w');
		layout.remove_widget($(widget));
		$(widget).remove();
		$('.gs_w').show();
	});

	var formArrayToPref = function(a) {
		var o = {};
		$.each(a, function() {
			if (o[this.name] !== undefined) {
				if (!o[this.name].push) {
					o[this.name] = [o[this.name]];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
	};

	$('.show-widget-pref').live('click', function(e) {
		e.preventDefault();
		var $this = $(this);
		var widget = $this.closest('.gs_w');
		var id = widget.find(".add-gadget-item > div").attr('id');
		var info = UESContainer.getGadgetInfo(id);
		if (info) {
			var prefCont = widget.find('.gadget-pref-cont');

			var hidePref = function() {
				prefCont.empty();
				prefCont.hide();
				widget.find('.grid_header_controls').removeClass('grid_header_controls-show');
				$this.attr('data-collapse', true);
			};

			var savePref = function(e) {
				e.preventDefault();
				var newPref = formArrayToPref(prefCont.find('form').serializeArray());
				UESContainer.redrawGadget(id, {
					prefs : newPref
				});
				hidePref();
			};

			if ($this.attr('data-collapse') == 'false') {
				hidePref();
				return;
			}

			var prefInfo = info.meta.userPrefs;
			var currentPref = info.opt.prefs || {};
			var html = '<form>';

			for (prefName in prefInfo) {
				var pref = prefInfo[prefName];
				var prefId = 'gadget-pref-' + id + '-' + prefName;
				html += '<label  for="' + prefId + '">' + pref.displayName + '</label>';
				html += '<input name="' + prefName + '" type="text" id="' + prefId + '" value="' + (currentPref[prefName] || pref.defaultValue ) + '">';
			}
			html += '<br><button class="btn btn-cancel-pref">Cancel</button>';
			html += '<button class="btn btn-primary btn-save-pref">Save</button>';
			html += '</form>';
			prefCont.html(html);
			prefCont.find('.btn-cancel-pref').on('click', function(e) {
				e.preventDefault();
				hidePref();
			});
			prefCont.find('.btn-save-pref').on('click', savePref);
			prefCont.show();
			widget.find('.grid_header_controls').addClass('grid_header_controls-show');
			$this.attr('data-collapse', false);
		}
	});

	$('.expand-widget').live('click', function(e) {
		e.preventDefault();
		var widget = $(this).closest('.gs_w');
		widget.addClass('maximized-view');
		var widgetEl = widget.get(0);
		$('.gs_w').each(function(i, el) {
			if (el != widgetEl) {
				$(el).hide();
			}
		});
		UESContainer.maximizeGadget(widget.find(".add-gadget-item > div").attr('id'));
	});

	$('.shrink-widget').live('click', function(e) {
		e.preventDefault();
		var widget = $(this).closest('.gs_w');
		widget.removeClass('maximized-view');
		$('.gs_w').show();
		UESContainer.restoreGadget(widget.find(".add-gadget-item > div").attr('id'));
	});

	function changeMode(mode) {
		if (mode == 'view') {
			var title = $('#inp-designer-title').val();
			$('#dashboardName').text(title).fadeIn();
			$('.sub-navbar-designer-view').fadeIn();
			layout.disable();
			$('#grid-guides').fadeOut("slow");
			$('.close-widget').hide();
			$('.layout_block .btn-add-gadget').hide();
			$('.layout_block').addClass('layout_block_view');

		} else if (mode == 'design') {
			$('#dashboardName').hide();
			$('.sub-navbar-designer').fadeIn();
			layout.enable();
			$('#grid-guides').fadeIn("slow");
			$('.close-widget').show();
			$('.layout_block .btn-add-gadget').show();
			$('.layout_block').removeClass('layout_block_view');
		}
	}

	function checkMode() {
		/*
		 if (window.location.hash) {
		 var hash = window.location.hash.substr(1);
		 changeMode(hash);
		 }
		 */
		var mode = $('#inp-view-mode').val();
		changeMode(mode);
	}

	// Hides the 3 static gridster widgets placed at the top.
	// placing static widgets was a fix for gridster responsive bug
	// https://github.com/ducksboard/gridster.js/pull/77
	function setGridOffsetTop() {
		var sizey = parseInt($('.static').height());

		$('.layouts_grid').animate({
			'margin-top' : "-" + (sizey - 80) + "px"
		});

	}


	$('#btn-save').click(function(e) {
		var dashboard = $('#inp-dashboard').val();
		var title = $('#inp-designer-title').val();
		var data = {
			title : title,
			widgets : layout.serialize()
		};

		var icon = $(this).find('i');
		icon.removeClass().addClass('icon-spinner icon-spin');

		$.post('apis/dashboard/' + dashboard, {
			layout : JSON.stringify(data)
		}).done(function(response) {
			setTimeout(function() {
				icon.removeClass().addClass('icon-save');
				$("#btn-preview-dash").removeClass('disabled').tooltip('destroy').data('tooltip', 'hide');
			}, 6000);
		}).fail(function(xhr, textStatus, errorThrown) {
			icon.removeClass().addClass('icon-save');
			// Session unavailable
			if (xhr.status == 401) {
				showAlert('Session timed out. Please login again.', 'alert-error', '.alert-bar');
			} else {
				showAlert('Error occured while saving dashboard. Please retry or re-login.', 'alert-error', '.alert-bar');
			}
		});
	});

	drawGadgets();

	$(window).bind('resize', resize);
	$(window).bind('load', checkMode);

});

