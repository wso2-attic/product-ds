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

    //TODO: handle autocompletion and check clearing
    $('#ues-share-view').typeahead(null, {
        name: 'roles',
        displayKey: 'name',
        source: engine.ttAdapter()
    }).on('typeahead:selected', function (e, role, roles) {
        var permissions = dashboard.permissions;
        permissions.viewers.push(role);
        saveDashboard();
        $('.ues-settings .ues-shared-view').append(sharedRoleHbs(role));
        $(this).val('');
    });

    $('#ues-share-edit').typeahead(null, {
        name: 'roles',
        displayKey: 'name',
        source: engine.ttAdapter()
    }).on('typeahead:selected', function (e, role, roles) {
        var permissions = dashboard.permissions;
        permissions.editors.push(role);
        saveDashboard();
        $('.ues-settings .ues-shared-edit').append(sharedRoleHbs(role));
        $(this).val('');
    });
});