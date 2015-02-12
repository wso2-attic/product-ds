$(function () {
    var options = {
        cols: 12,
        rows: 12
    };

    var gridHtml = function (options) {
        var i, j,
            html = '';
        for (i = 0; i < options.rows; i++) {
            for (j = 0; j < options.cols; j++) {
                html += '<li class="ui-state-default box" data-col="' + j + '" data-row="' + i + '"></li>';
            }
        }
        return html;
    };

    var grid = $('.grid').html(gridHtml(options));

    grid.selectable({
        selecting: function (event, ui) {
            console.log($(ui).data('col'));
        },
        stop: function (event, ui) {
            //console.log(event);
            //console.log($(ui).data('col'));
        }
    });

    parent.init();
});