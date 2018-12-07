'use strict';

angular.module('ScdDirectives', [])
	.directive("fileread", [function () {
    return {
      scope: {
        fileread: "="
      },
      link: function (scope, element, attributes) {
        element.bind("change", function (changeEvent) {
          var reader = new FileReader();
          reader.onload = function (loadEvent) {
            scope.$apply(function () {
              scope.fileread = loadEvent.target.result;
            });
          }
          reader.readAsText(changeEvent.target.files[0]);
        });
      }
    }
  }])
  .directive('countDownDirective', [
    'Util',
    '$interval',
    function (Util, $interval) {
      return {
        scope: 
        { 
          countdownto: "=",
          timeisup: "="       
        },
        link: function (scope, element) {
          scope.$watch('countdownto', function(){
            var future;
            future = new Date(scope.countdownto);

            if (future instanceof Date && !isNaN(future)) {
              var timer = $interval(function () {
                var diff;
                diff = Math.floor((future.getTime() - new Date().getTime()) / 1000);

                if (diff === 0) {
                  scope.timeisup = true;
                  $interval.cancel(timer);
                }

                return diff <= 0 ? element.text("") : element.text(Util.dhms(diff));
              }, 1000);
            }
          });
        }
      };
    }
  ]).factory('Util', [function () {
    return {
      dhms: function (t) {
        var days, hours, minutes, seconds;
        days = Math.floor(t / 86400);
        t -= days * 86400;
        hours = Math.floor(t / 3600) % 24;
        t -= hours * 3600;
        minutes = Math.floor(t / 60) % 60;
        t -= minutes * 60;
        seconds = t % 60;
        var textArray = []
        if (days > 0) {
          textArray.push(days + 'd');
        }
        if (hours > 0) {
          textArray.push(hours + 'h');
        }
        if (minutes > 0) {
          textArray.push(minutes + 'm');
        }
        textArray.push(seconds + 's');

        return textArray.join(' ');
      }
    };
  }]);
