$(function() {
	function drawGadgets() {

		applyGridster();

		$.get('apis/ues/layout/', {}, function(result) {
			if (result) {

				var userWidgets = result.widgets;
				var defaultWidgets = layout.serialize();

				$.each(userWidgets, function(i, w) {

					if (w.wid > newWid) {

						newWid = w.wid;
					}
					//find w in defaultWidgets, if found copy attributes to _widget
					if (isWidgetFound(w, defaultWidgets)) {

						//update coords in default grid
						$('.layout_block[data-wid="' + w.wid + '"]').attr({
							'data-col' : w.x,
							'data-row' : w.y,
							'data-url' : w.url,
							'data-title' : w.title,
							'data-prefs' : w.prefs
						});

					} else {
						//add user widget to grid
						layout.add_widget(widgetTemplate2({
							wid : w.wid,
							url : w.url,
							prefs : w.prefs
						}), w.width, w.height, w.x, w.y);
					}
				});

				$.each(defaultWidgets, function(i, w) {
					// skip static widgets
					if (w.y == 1) {
						return true;
					}

					if (w.wid > newWid) {
						newWid = w.wid;
					}

					// remove widgets in default grid but not found in user widgets
					if (!isWidgetFound(w, userWidgets)) {

						var removeWidget = $('.layouts_grid').find('.layout_block[data-wid="' + w.wid + '"]');
						layout.remove_widget($(removeWidget));
					}
				});

				$('#dashboardName').find('span').text(result.title);

			}

			var widgets = $('.layouts_grid').find('.layout_block');

			$.each(widgets, function(i, widget) {
				var $w = $(widget);
				var wid = $w.attr('data-wid');
				if (wid > newWid) {
					newWid = wid;
				}

				var url = $w.attr('data-url');
				var title = $w.attr('data-title');
				var prefs = JSON.parse($w.attr('data-prefs').replace(/'/g, '"'));
				var gadgetArea = $w.find('.add-gadget-item');
				if (url != '') {
					$w.find('.designer-placeholder').remove();
					$w.find('.btn-add-gadget').remove();
					insertGadget($w, url, {
						prefs : prefs
					}, title);
				}

			});

		}).error(function(error) {
			console.log(error);
		});

		setGridOffsetTop();

	}

}); 