$(function () {

    var dashboard = ues.global.dashboard;

    var saveDashboard = ues.dashboards.save;

    var sharedRoleHbs = Handlebars.compile($("#shared-role-hbs").html());

    //TODO: fix the url
    var engine = new Bloodhound({
        name: 'roles',
        limit: 10,
        prefetch: {
            url: 'https://localhost:9443/designer/apis/roles?q=%QUERY',
            filter: function (roles) {
                console.log(roles);
                return $.map(roles, function (role) {
                    return {name: role};
                });
            },
            ttl: 60
        },
        datumTokenizer: function (d) {
            return d.name.split(/[\s\/]+/) || [];
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace
    });

    engine.initialize();

    var viewer = function (el, role) {
        var permissions = dashboard.permissions;
        var viewers = permissions.viewers;
        viewers.push(role);
        saveDashboard();
        $('.ues-settings .ues-shared-view').append(sharedRoleHbs(role));
        el.typeahead('val', '');
    };

    var editor = function (el, role) {
        var permissions = dashboard.permissions;
        var editors = permissions.editors;
        editors.push(role);
        saveDashboard();
        $('.ues-settings .ues-shared-edit').append(sharedRoleHbs(role));
        el.typeahead('val', '');
    };

    //TODO: handle autocompletion and check clearing
    $('#ues-share-view').typeahead(null, {
        name: 'roles',
        displayKey: 'name',
        source: engine.ttAdapter()
    }).on('typeahead:selected', function (e, role, roles) {
        viewer($(this), role.name);
    }).on('typeahead:autocomplete', function (e, role) {
        viewer($(this), role.name);
    });

    $('#ues-share-edit').typeahead(null, {
        name: 'roles',
        displayKey: 'name',
        source: engine.ttAdapter()
    }).on('typeahead:selected', function (e, role, roles) {
        editor($(this), role.name);
    }).on('typeahead:autocomplete', function (e, role) {
        editor($(this), role.name);
    });

    $('#settings').find('.ues-shared-edit').on('click', '.remove-button', function () {
        var el = $(this).closest('.ues-shared-role');
        var role = el.data('role');
        var permissions = dashboard.permissions;
        var editors = permissions.editors;
        editors.splice(editors.indexOf(role), 1);
        saveDashboard();
        el.remove();
    }).end().find('.ues-shared-view').on('click', '.remove-button', function () {
        var el = $(this).closest('.ues-shared-role');
        var role = el.data('role');
        var permissions = dashboard.permissions;
        var viewers = permissions.viewers;
        viewers.splice(viewers.indexOf(role), 1);
        saveDashboard();
        el.remove();
    });
});