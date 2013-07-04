$(function(){
	

$('button[data-toggle=tooltip]').tooltip();

$(document).on('click', '#myasset-container .asset-remove-btn', function() {
caramel.get('/apis/remove', {
	    aid: $(this).attr('data-aid'),
	    type: $(this).attr('type')
            }, function (data) {
		location.reload();
            });
//console.log("removing : "+$(this).attr('data-aid')+" type :"+$(this).attr('type'));
});

$('.embed-snippet').hide();

$(document).on('click', '#myasset-container .btn-embed', function() {
    $(this).closest('.store-my-item').find('.embed-snippet').toggle(50);
    return false;
});

});