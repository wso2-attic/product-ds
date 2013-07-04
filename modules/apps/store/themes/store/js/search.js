$(function () {
    History.Adapter.bind(window, 'statechange', function () {
        var state = History.getState();
        if (state.data.id === 'sort-assets') {
            renderAssets(state.data.context);
        }
    });

    var search = function () {
        var url = caramel.url('/assets/' + store.asset.type + '/?query=' + $('#search').val());
        caramel.data({
            title: null,
            header: ['sort-assets'],
            body: ['assets', 'pagination']
        }, {
            url: url,
            success: function (data, status, xhr) {
                History.pushState({
                    id: 'sort-assets',
                    context: data
                }, document.title, url);
            },
            error: function (xhr, status, error) {
                theme.loaded($('#assets-container').parent(), '<p>Error while retrieving data.</p>');
            }
        });
        theme.loading($('#assets-container').parent());
    };

    $('#search').keypress(function (e) {
        if (e.keyCode === 13) {
            search();
        }
    });

    $('#search-button').click(function () {
        search();
        return false;
    });
});