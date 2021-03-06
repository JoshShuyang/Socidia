'use strict';

angular.module('RestApiServices', ['ngResource'])
	.factory('UserService', function($resource) {
		return {
			loginResource: $resource('/login', {}, {login: {method: 'POST'}}),
			changePasswordResource: $resource('/api/user', {}, {update: {method: 'PUT'}})
		}
	})
	.factory('RegisterService', function($resource) {
		return $resource('/middleware/signup');
	})
	.factory('LinkService', function($resource) {
		return {
			linkResource: $resource('/middleware/createFacebookAuthorization'),
			getResource: $resource('/middleware/getSocialAccount')
		}
	})
	.factory('DashboardService', function($resource) {
		var hostname = window.location.hostname;
		hostname = 'https://' +  hostname + ':8888';
		return {
			violationResource: $resource(hostname+'/:userId/userinsideinfo')
		}
	})
	.factory('PolicyService', function($resource) {
		var hostname = window.location.hostname;
		hostname = 'https://' +  hostname + ':8888';
		return {
			getResource: $resource(hostname+'/:userId/policy'),
			getPolicyResource: $resource(hostname+'/policy/:policyId'),
			postResource: $resource(hostname+'/policy'),
			putResource: $resource(hostname+'/policy/:policyId')
		}
	})
	.factory('RuleService', function($resource) {
		var hostname = window.location.hostname;
		hostname = 'https://' +  hostname + ':8888';
		return {
			getRulesResource: $resource(hostname+'/policy/:policyId/rules'),
			getRuleDetailResource: $resource(hostname+'/policy_rule/:ruleId'),
			postResource: $resource(hostname+'/policy_rule')
		}
	})
	.factory('ItemService', function($resource) {
		var hostname = window.location.hostname;
		hostname = 'https://' +  hostname + ':8888';
		return {
			getItemsResource: $resource(hostname+'/:userId/item'),
			getItemDetailResource: $resource(hostname+'/item/:itemId'),
			getItemViolateRulesResource: $resource(hostname+'/item_violate_rule/:itemId')
		}
	})
	.factory('AuthorService', function($resource) {
		var hostname = window.location.hostname;
		hostname = 'https://' +  hostname + ':8888';
		return $resource(hostname+'/author/:authorId');
	});


angular.module('Authentication', [])
	.factory('AuthenticationService',
		['Base64', '$http', '$cookies', '$rootScope', '$timeout',
		function (Base64, $http, $cookies, $rootScope, $timeout) {
			var service = {};

			service.SetCredentials = function (email, user) {
				var username = email.substring(0, email.indexOf("@"));

				$rootScope.globals = {
					currentUser: {
						email: email,
						username: user.username || username,
						userId: user.id
					}
				};

				$cookies.putObject('globals', $rootScope.globals);
			};

			service.SetCredentialsLvl2 = function (email, userId) {
				var authdata = Base64.encode(email + ':' + userId);

				$http.defaults.headers.common['Authorization'] = 'Basic ' + authdata; // jshint ignore:line
				$rootScope.globals.currentUser.authdata = authdata;
				$cookies.putObject('globals', $rootScope.globals);
			}

			service.ClearCredentials = function () {
				$rootScope.globals = {};
				$cookies.remove('globals');
				$cookies.remove('JSESSIONID');
				$http.defaults.headers.common.Authorization = 'Basic ';
			};

			return service;
		}])

	.factory('Base64', function () {
		/* jshint ignore:start */

		var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

		return {
			encode: function (input) {
				var output = "";
				var chr1, chr2, chr3 = "";
				var enc1, enc2, enc3, enc4 = "";
				var i = 0;

				do {
					chr1 = input.charCodeAt(i++);
					chr2 = input.charCodeAt(i++);
					chr3 = input.charCodeAt(i++);

					enc1 = chr1 >> 2;
					enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
					enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
					enc4 = chr3 & 63;

					if (isNaN(chr2)) {
						enc3 = enc4 = 64;
					} else if (isNaN(chr3)) {
						enc4 = 64;
					}

					output = output +
						keyStr.charAt(enc1) +
						keyStr.charAt(enc2) +
						keyStr.charAt(enc3) +
						keyStr.charAt(enc4);
					chr1 = chr2 = chr3 = "";
					enc1 = enc2 = enc3 = enc4 = "";
				} while (i < input.length);

				return output;
			},

			decode: function (input) {
				var output = "";
				var chr1, chr2, chr3 = "";
				var enc1, enc2, enc3, enc4 = "";
				var i = 0;

				// remove all characters that are not A-Z, a-z, 0-9, +, /, or =
				var base64test = /[^A-Za-z0-9\+\/\=]/g;
				if (base64test.exec(input)) {
					window.alert("There were invalid base64 characters in the input text.\n" +
						"Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
						"Expect errors in decoding.");
				}
				input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

				do {
					enc1 = keyStr.indexOf(input.charAt(i++));
					enc2 = keyStr.indexOf(input.charAt(i++));
					enc3 = keyStr.indexOf(input.charAt(i++));
					enc4 = keyStr.indexOf(input.charAt(i++));

					chr1 = (enc1 << 2) | (enc2 >> 4);
					chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
					chr3 = ((enc3 & 3) << 6) | enc4;

					output = output + String.fromCharCode(chr1);

					if (enc3 != 64) {
						output = output + String.fromCharCode(chr2);
					}
					if (enc4 != 64) {
						output = output + String.fromCharCode(chr3);
					}

					chr1 = chr2 = chr3 = "";
					enc1 = enc2 = enc3 = enc4 = "";

				} while (i < input.length);

				return output;
			}
		};

		/* jshint ignore:end */
	});
