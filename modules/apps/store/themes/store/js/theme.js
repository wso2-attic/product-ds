var theme = (function () {
    var loading, loaded,
        loaderClass = 'loading';

    loading = function (el) {
        var loader;
        //el.children().hide();
        loader = $('.' + loaderClass, el);
        if (loader.length === 0) {
            loader = el.prepend('<div class="overlay loading"><img src="' + caramel.context + '/themes/store/img/preloader-40x40.gif"></div>');
        }
        loader.show();
    };

    loaded = function (el, data) {
        var children;
        $('.' + loaderClass, el).hide();
        children = el.children(':not(.' + loaderClass + ')');
        if (!data) {
            children.show();
            return;
        }
        children.remove();
        el.append(data);
    };

    return {
        loading: loading,
        loaded: loaded
    };
})();


$(function(){
	$(window).bind('resize', adjustStoreRight);
	
	adjustStoreRight();
})

var adjustStoreRight = function(){
	
	var docWidth = $(window).width();
	
	if(docWidth < 1200){
		$('.store-left').removeClass('span9').addClass('span12');
		$('.store-right').removeClass('span3').addClass('span12');
		
		$('.store-right > .row > .span3').removeClass('span3').addClass('span12');
		$('.store-right').height('auto');
		$('.asset-description-header > .row > .span9').removeClass('span9').addClass('span12');
	} else {
		$('.store-left').removeClass('span12').addClass('span9');
		$('.store-right').removeClass('span12').addClass('span3');
		$('.store-right > .row > .span12').removeClass('span12').addClass('span3');
		$('.asset-description-header > .row > .span12').removeClass('span12').addClass('span9');
		
		setTimeout(function(){ 
		($('.store-right').height() < $('.store-left').height()) &&  $('.store-right').height($('.store-left').height() + 15);
		}, 200);
	}
	
	
	
}
$(function(){
	//$(".btn-popover").popover();
	var isVisible = false;
	var clickedAway = false;

	$('.btn-popover').popover({
		html: false,
		trigger: 'manual'
	    }).click(function(e) {
		$(this).popover('show');
		clickedAway = false
		isVisible = true
		e.preventDefault()
		    $('.popover').bind('click',function() {
		        clickedAway = false
		        //alert('popover has been clicked!');
		    });
	    });

	$(document).click(function(e) {
	  if(isVisible && clickedAway)
	  {
	    $('.btn-popover').popover('hide')
	    isVisible = clickedAway = false
	  }
	  else
	  {
	    clickedAway = true
	  }
	});

});

