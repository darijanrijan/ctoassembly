app.directive('highlightChanges', function() {
	return {
		scope : {
			val: '='
		},
		link: function(scope, elm, attrs, ctrl) {
			scope.$watch('val', function() {
				if (startAnimating) {
					var bgColor = $(elm).css('backgroundColor');
					$(elm).text(scope.value);
					$(elm).fadeOut(1, function() {
						$(elm).css('backgroundColor', 'orange');
					});
					$(elm).fadeIn(100, function() {
						$(elm).css('backgroundColor', bgColor);
					});
				}
			}, true);
		}
	};
});

app.directive('waitingForAngular', ['$timeout', function(timer) {
	return {
		restrict : 'C',
		link : function(scope, elem, attrs) {
			var hideSpinner = function() {
				//once Angular is started, set opacitiy back to 1:
				//it would be nicer if I used display:none, show() and hide() but CodeMirror is not created when display:none. God knows why
				$('.spinner').hide();
				$('.CodeMirror').css({opacity: 1});
				$('.buttons').css({opacity: 1});
				$('.ccode-title').css({opacity: 1});
				//var spinner = elem.find('.spinner');
				//var content = elem.find('.ccode-content');
			};
			//so that it gets executed after the UI has been done
			timer(hideSpinner, 500);
		}
	};
}]);