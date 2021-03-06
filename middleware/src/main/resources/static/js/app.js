var scd = angular.module(
    'scd', 
    ['ui.router', 
    'RestApiServices', 
    'Authentication', 
    'ngCookies',
    'ScdDirectives',
    'ScdFilters']);


scd.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', 
	function($stateProvider, $urlRouterProvider, $locationProvider) {
	$locationProvider.hashPrefix('');

    $urlRouterProvider.otherwise('/home/dashboard');
    $urlRouterProvider.when('/home', '/home/dashboard');

    $stateProvider
    	.state('login', {
        	url: '/login',
            templateUrl: 'partials/login.html',
            controller: LoginController
        })

        .state('register', {
            url: '/register',
            templateUrl: 'partials/register.html',
            controller: RegisterController
        })

        .state('link_accounts', {
            url: '/link_accounts',
            templateUrl: 'partials/link_accounts.html',
            controller: LinkAccountsController
        })

        
        .state('home', {
            url: '/home',
            templateUrl: 'partials/home.html',
            controller: HomeController
        })

        .state('home.dashboard', {
            url: '/dashboard',
            templateUrl: 'partials/home-dashboard.html',
            controller: DashboardController
        })

        .state('home.policy_list', {
            url: '/policy_list',
            templateUrl: 'partials/home-policy_list.html',
            controller: PolicyListController
        })
        .state('home.policy_list.policy_detail', {
            url: '/policy_detail?policyId',
            templateUrl: 'partials/home-policy_list-policy_detail.html',
            controller: PolicyDetailController
        })
        .state('home.policy_list.policy_detail.rule_detail', {
            url: '/rule_detail?ruleId',
            templateUrl: 'partials/home-policy_list-policy_detail-rule_detail.html',
            controller: RuleDetailController
        })

        .state('home.item_list', {
            url: '/item_list',
            templateUrl: 'partials/home-item_list.html',
            controller: ItemListController
        })

        .state('home.management', {
            url: '/management',
            templateUrl: 'partials/home-management.html',
            controller: ManagementController
        })
        
        .state('home.setting', {
            url: '/setting',
            templateUrl: 'partials/home-setting.html',
            controller: SettingController
        })
}])

.run(['$rootScope', '$state', '$stateParams', '$location', '$cookies', '$http',
	function ($rootScope, $state, $stateParams, $location, $cookies, $http) {
		$rootScope.$state = $state;
    	$rootScope.$stateParams = $stateParams;
    	
		// keep user logged in after page refresh
    	$rootScope.globals = $cookies.getObject('globals') || {};
    	if ($rootScope.globals.currentUser) {
        	$http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
    	}

    	$rootScope.$on('$locationChangeStart', function (event, next, current) {
        	// redirect to login page if not logged in and trying to access a restricted page
        	if ($location.path() !== '/login' && $location.path() !== '/register' && $location.path() !== '/link_accounts') {
                try {
                    var authdata = $rootScope.globals.currentUser.authdata;
                    if (authdata === undefined)
                        $location.path('/link_accounts');
                }
                catch (error) {
                    $location.path('/login');
                }
        	}

            if ($location.path() === '/link_accounts' && !$rootScope.globals.currentUser) {
                $location.path('/login');
            }
    	});

        //global functions
        $rootScope.scrollTo = function(divId) {
            var elmnt = document.getElementById(divId);
            elmnt.scrollIntoView();
        }

        $rootScope.decimalToPercent = function(target, roundDown) {
            return Math.floor(target * 100 * Math.pow(10, roundDown))/Math.pow(10, roundDown);
        }

        $rootScope.addMinutes =  function (dt, minutes) {
            return new Date(dt.getTime() + minutes*60000);
        }
	}]);
