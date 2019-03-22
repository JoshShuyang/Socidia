'use strict';

/* Filters */

angular.module('ScdFilters', [])
	.filter('reverse', function() {
	  return function(items) {
	    if(typeof items === 'undefined') { return; }
	    return angular.isArray(items) ? 
	      items.slice().reverse() : // If it is an array, split and reverse it
	      (items + '').split('').reverse().join(''); // else make it a string (if it isn't already), and reverse it
	  };
	})

	.filter('utcToLocal', function ($filter) {
		return function (utcDateString, format) {
      if (!utcDateString) {return;}

      if (!format) {format = 'MM.dd.yy - hh:mm a';}
      
      var date = new Date(utcDateString);

      // convert and format date using the built in angularjs date filter
      return $filter('date')(date, format);
    };
  });