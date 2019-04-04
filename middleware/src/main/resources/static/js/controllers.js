'use strict';

/* Controllers */

function HomeController($rootScope, $scope, $location, AuthenticationService) {
  $scope.currentUser = $rootScope.globals.currentUser;

  // Sidebar Toggle
  $('.sidebar-toggle').on('click', function(e) {
      $('.app').toggleClass('is-collapsed');
      e.preventDefault();
  });

  $scope.logout = function() {
      AuthenticationService.ClearCredentials();
      $location.path('/login');
  }
}

function LoginController($rootScope, $scope, $location, UserService, AuthenticationService) {
  AuthenticationService.ClearCredentials();

  $scope.login = function() {
    AuthenticationService.SetCredentials($scope.user.email, $scope.user.password);
    var userLogin = UserService.loginResource.login({username:$scope.user.email, password:$scope.user.password});

    userLogin.$promise.then(function(res){
      $location.path('/home/dashboard');
      if (res.error) {
        $scope.errorMessage = res.error;
        $("#errorMess").css("display", "block").fadeOut(10000);
      }
      else if (res.success) {
        AuthenticationService.SetCredentials($scope.user.email, $scope.user.password);
        $location.path('/home/dashboard');
      }
    });
  }
}

function RegisterController($rootScope, $scope, $location, RegisterService, AuthenticationService) {
  AuthenticationService.ClearCredentials();
  $scope.alertMessage = "";
  $scope.successMessage = "Registered successfully! Please go to your email and click on the verification link!";
  $scope.validation = true;
  $scope.userInfo = {
    userName: "",
    email: "",
    password: "",
    repassword: ""
  }

  $scope.register = function() {
    $scope.validation = validateRegistration();

    if ($scope.validation) {
      var userInfo = {
        username: $scope.userInfo.userName,
        email: $scope.userInfo.email,
        password: $scope.userInfo.password,
      }
      var registered = RegisterService.save({
        username: $scope.userInfo.userName, 
        email: $scope.userInfo.email,
        password: $scope.userInfo.password
      });

      registered.$promise.then(function(res) {
        if (res.error){
          $scope.errorMessage = res.error;
          $("#errorMess").css("display", "block").fadeOut(15000);
        }
        else if (res.success) {
          $("#successMess").css("display", "block").fadeOut(15000);
        }
      })
    }
  }

  function validateRegistration() {
    if ($scope.userInfo.userName === "" || 
        $scope.userInfo.email === "" ||
        $scope.userInfo.password === "" ||
        $scope.userInfo.repassword === "") {
      $scope.alertMessage = "Please fill out complete information!";
      return false;
    }

    if ($scope.userInfo.password !== $scope.userInfo.repassword) {
      $scope.alertMessage = "Re-enter password does not match password!";
      return false;
    }

    return true;
  }
}

function DashboardController($rootScope, $scope) {
  //Progress chart
  $('[data-toggle="tooltip"]').tooltip();
  $scope.passCheck = true;

  /*
  var query = HistoryService.get();

  query.$promise
  .then(function(res) {
    $scope.hisInfoList = res.data.hist_info_list;

    var currentBuild = $scope.hisInfoList[$scope.hisInfoList.length - 1];

    $scope.passCheck = currentBuild.pass_check;
  
    $scope.curBuildData = {
      buildId: currentBuild.build_id,
      buildDate: currentBuild.created_on,
      numOfpass: currentBuild.total_hosts - currentBuild.total_failed_hosts,
      total: currentBuild.total_hosts,
      hostPassRate: $rootScope.decimalToPercent(currentBuild.host_pass_rate, 2),
      newHostNum: currentBuild.new_hosts,
      newHostRate: $rootScope.decimalToPercent(currentBuild.new_host_rate, 2)
    };

    $scope.overallData = {
      buildsPassRate: $rootScope.decimalToPercent(res.data.overall_builds_pass_rate, 2),
      hostsPassRate: $rootScope.decimalToPercent(res.data.overall_hosts_pass_rate, 2),
      newHostsRate: $rootScope.decimalToPercent(res.data.overall_new_hosts_rate, 2)
    }

    $scope.monthlyData = {
      buildsPassRate: $rootScope.decimalToPercent(res.data.monthly_builds_pass_rate, 2),
      hostsPassRate: $rootScope.decimalToPercent(res.data.monthly_hosts_pass_rate, 2),
      newHostsRate: $rootScope.decimalToPercent(res.data.monthly_new_hosts_rate, 2)
    }
  })
  .then(function(){
    //Pie Chart
    setTimeout(function(){
      if ($('.easy-pie-chart').length > 0) {
        $('.easy-pie-chart').easyPieChart({
          onStep: function(from, to, percent) {
            this.el.children[0].innerHTML = Math.floor(percent*100)/100 + '%';
          },
        });
      }
    }, 300);
  })
  .then(function() {
    //Trend Chart
    var hisPassRate = [];

    $scope.hisInfoList.forEach(function(build){
      hisPassRate.push([
        Date.parse(build.created_on),
        build.host_pass_rate * 100
      ]);
    });

    Highcharts.setOptions({
      global: {
        timezone: 'America/Los_Angeles',
        useUTC: false
      }
    });

    Highcharts.chart('trendChart', {
      chart: {
        zoomType: 'x'
      },
      title: {
        text: 'Passing Rate Overall Trend'
      },
      subtitle: {
        text: document.ontouchstart === undefined ?
                  'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
      },
      xAxis: {
        type: 'datetime'
      },
      yAxis: {
        title: {
          text: 'Passing rate %'
        }
      },
      legend: {
        enabled: false
      },
      plotOptions: {
        area: {
            fillColor: {
                linearGradient: {
                    x1: 0,
                    y1: 0,
                    x2: 0,
                    y2: 1
                },
                stops: [
                    [0, Highcharts.getOptions().colors[0]],
                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                ]
            },
            marker: {
                radius: 2
            },
            lineWidth: 1,
            states: {
                hover: {
                    lineWidth: 1
                }
            },
            threshold: null
        }
      },

      series: [{
        type: 'area',
        name: 'Passing Rate',
        data: hisPassRate
      }]
    });
  });
  */

  $('.easy-pie-chart').easyPieChart({
    onStep: function(from, to, percent) {
      this.el.children[0].innerHTML = Math.floor(percent*100)/100 + '%';
    },
  });

  //Trend Chart

    var hisPassRate = [
      [
          1167609600000,
          0.9537
      ],
      [
          1167696000000,
          0.9537
      ],
      [
          1167782400000,
          0.9559
      ],
      [
          1167868800000,
          0.9631
      ],
      [
          1167955200000,
          0.9644
      ],
      [
          1168214400000,
          0.969
      ],
      [
          1168300800000,
          0.9683
      ],
      [
          1168387200000,
          0.97
      ],
      [
          1168473600000,
          0.9703
      ],
      [
          1168560000000,
          0.9757
      ],
      [
          1168819200000,
          0.9728
      ],
      [
          1168905600000,
          0.9721
      ],
      [
          1168992000000,
          0.9748
      ],
      [
          1169078400000,
          0.974
      ],
      [
          1169164800000,
          0.9718
      ],
      [
          1169424000000,
          0.9731
      ],
      [
          1169510400000,
          0.967
      ],
      [
          1169596800000,
          0.969
      ],
      [
          1169683200000,
          0.9706
      ],
      [
		1169769600000,
		0.9752
	],
	[
		1170028800000,
		0.974
	],
	[
		1170115200000,
		0.971
	],
	[
		1170201600000,
		0.9721
	],
	[
		1170288000000,
		0.9681
	],
	[
		1170374400000,
		0.9681
	],
	[
		1170633600000,
		0.9738
	],
	[
		1170720000000,
		0.972
	],
	[
		1170806400000,
		0.9701
	],
	[
		1170892800000,
		0.9699
	],
	[
		1170979200000,
		0.9689
	],
	[
		1171238400000,
		0.9719
	],
	[
		1171324800000,
		0.968
	],
	[
		1171411200000,
		0.9645
	],
	[
		1171497600000,
		0.9613
	],
	[
		1171584000000,
		0.9624
	],
	[
		1171843200000,
		0.9616
	],
	[
		1171929600000,
		0.9608
	],
	[
		1172016000000,
		0.9608
	],
	[
		1172102400000,
		0.9631
	],
	[
		1172188800000,
		0.9615
	],
	[
		1172448000000,
		0.96
	],
	[
		1172534400000,
		0.956
	],
	[
		1172620800000,
		0.957
	],
	[
		1172707200000,
		0.9562
	],
	[
		1172793600000,
		0.9598
	],
	[
		1173052800000,
		0.9645
	],
	[
		1173139200000,
		0.9635
	],
	[
		1173225600000,
		0.9614
	],
	[
		1173312000000,
		0.9604
	],
	[
		1173398400000,
		0.9603
	],
	[
		1173657600000,
		0.9602
	],
	[
		1173744000000,
		0.9566
	],
	[
		1173830400000,
		0.9587
	],
	[
		1173916800000,
		0.9562
	],
	[
		1174003200000,
		0.9506
	],
	[
		1174262400000,
		0.9518
	],
	[
		1174348800000,
		0.9522
	],
	[
		1174435200000,
		0.9524
	]
    ];
/*
    $scope.hisInfoList.forEach(function(build){
      hisPassRate.push([
        Date.parse(build.created_on),
        build.host_pass_rate * 100
      ]);
    });*/

    Highcharts.setOptions({
      global: {
        timezone: 'America/Los_Angeles',
        useUTC: false
      }
    });

    Highcharts.chart('trendChart', {
      chart: {
        zoomType: 'x'
      },
      title: {
        text: 'Passing Rate Overall Trend'
      },
      subtitle: {
        text: document.ontouchstart === undefined ?
                  'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
      },
      xAxis: {
        type: 'datetime'
      },
      yAxis: {
        title: {
          text: 'Passing rate %'
        }
      },
      legend: {
        enabled: false
      },
      plotOptions: {
        area: {
            fillColor: {
                linearGradient: {
                    x1: 0,
                    y1: 0,
                    x2: 0,
                    y2: 1
                },
                stops: [
                    [0, Highcharts.getOptions().colors[0]],
                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                ]
            },
            marker: {
                radius: 2
            },
            lineWidth: 1,
            states: {
                hover: {
                    lineWidth: 1
                }
            },
            threshold: null
        }
      },

      series: [{
        type: 'area',
        name: 'Passing Rate',
        data: hisPassRate
      }]
    });
}

function PolicyListController($scope) {
    /*
  var query = HistoryService.get();

  query.$promise.then(function(res) {
    angular.element(document).ready(function() {
      var dTable = $('#buildsDataTable');
      dTable.DataTable({"order": [[ 0, "desc" ]]});
    });

    $scope.historicalData = res.data.hist_info_list;
  });*/

    angular.element(document).ready(function() {
      var dTable = $('#buildsDataTable');
      dTable.DataTable();
    });
    $scope.historicalData = [
			{
				'build_id': 1,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 2,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 3,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 4,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 5,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 6,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 7,
				'pass_check': false,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 8,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 9,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 10,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 11,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			},
            {
				'build_id': 12,
				'pass_check': true,
				'created_on': '11-08-2018 5:39PM',
				'updated_on': '11-08-2018 5:39PM'
			}
		]
}

function PolicyDetailController($rootScope, $scope, $timeout) {
  $scope.buildId = $rootScope.$stateParams.policyId;
  $scope.passCheck = true;

  /*
  var query = BuildService.get({buildId: $scope.buildId});

  query.$promise
  .then(function(res) {
    if (res.error) {
      alert(res.error);
      window.history.back();
    }

    var currentBuild = res.data;
    $scope.hostsRegData = [];
    $scope.passCheck = currentBuild.pass_check;

    $scope.buildData = {
      buildId: currentBuild.build_id,
      buildDate: currentBuild.created_on,
      numOfpass: currentBuild.total_hosts - currentBuild.total_failed_hosts,
      total: currentBuild.total_hosts,
      dplVersion: currentBuild.dpl_version,
      dceVersion: currentBuild.dce_version,
      actVersion: currentBuild.act_version,
      hostPassRate: $rootScope.decimalToPercent(currentBuild.host_pass_rate, 2),
      newHostNum: currentBuild.new_hosts,
      newHostRate: $rootScope.decimalToPercent(currentBuild.new_host_rate, 2)
    };

    for (var key in currentBuild.regression_detail) {
      // skip loop if the property is from prototype
      if (!currentBuild.regression_detail.hasOwnProperty(key)) {continue;}

      var hostRegressionInfo = currentBuild.regression_detail[key];

      var infoForDisplay = {
        hostId: hostRegressionInfo.host_id,
        ip: hostRegressionInfo.ip,
        matchRes: hostRegressionInfo.host_detail_match
      }

      $scope.hostsRegData.push(infoForDisplay);
    }
  })
  .then(function() {
    angular.element(document).ready(function() {  
      var dTable = $('#hostsDataTable');
      dTable.DataTable();  
    });
  });*/

  $scope.alertSetting = {
    socialAccount: {
      facebook: true,
      twitter: false,
      instagram: false
    },
    alertMethod: {
      email: true,
      text: false,
      phone: false
    }
  }

  $scope.buildData = {
      buildId: $scope.buildId,
      buildDate: '11-08-2018 5:39PM',
      numOfpass: 5,
      total: 5,
      hostPassRate: 100,
      newHostNum: 1,
      newHostRate: 20
    };

  $scope.hostsRegData = [
      {
        hostId: 1,
        ip: 'Rule Content 1',
        matchRes: true
      },
      {
        hostId: 2,
        ip: 'Rule Content 2',
        matchRes: true
      },
      {
        hostId: 3,
        ip: 'Rule Content 3',
        matchRes: true
      },
      {
        hostId: 4,
        ip: 'Rule Content 4',
        matchRes: true
      },
      {
        hostId: 5,
        ip: 'Rule Content 5',
        matchRes: true
      },
  ];

  angular.element(document).ready(function() {
  var dTable = $('#hostsDataTable');
  dTable.DataTable();
});

  $('[data-toggle="tooltip"]').tooltip();

  $scope.$on('$viewContentLoaded', function(event)
  { 
    $timeout(function() {
      $rootScope.scrollTo('buildDetial');
      if ($scope.jumpToHostDetial)
        $rootScope.scrollTo('hostDetial');
    }, 0);
  });

  $scope.$on('jumpToHostDetial', function(event)
  { 
    $scope.jumpToHostDetial = true;
  });
}

function RuleDetailController($rootScope, $scope, $timeout, $q) {
  $scope.buildId = $rootScope.$stateParams.policyId;
  $scope.hostId = $rootScope.$stateParams.ruleId;
  $scope.showHis = false;
   /*
  var query1 = HostService.get({buildId: $scope.buildId, hostId: $scope.hostId});
  var query2 = HostHistoryService.get({hostId: $scope.hostId});
  var query3 = HostInfoService.get({buildId: $scope.buildId, hostId: $scope.hostId});

  query3.$promise.then(function(res) {
    if (res.error) {
      alert(res.error);
      window.history.back();
    }
    $scope.hostInfo = res.data;
  });


  $q.all([query1.$promise, query2.$promise])
  .then(function(resList){
    if (resList[0].error) {
      alert(resList[0].error);
      window.history.back();
    }
    $scope.hostData = resList[0].data;
    $scope.hostHistData = resList[1].data;
  })
  .then(function(){
    angular.element(document).ready(function() {  
      var dTable = $('#hostDataTable');  
      dTable.DataTable();
    });

    angular.element(document).ready(function() {  
      var dTable = $('#hostHistDataTable');
      dTable.DataTable();
    });
  });*/

  $scope.hostData = {
      build_date: '11-09-2018 5:28PM',
      host_detail_match: true
  };

  $scope.showHistory = function() {
    $scope.showHis = !$scope.showHis;
  };

  $scope.$on('$viewContentLoaded', function(event)
  { 
    $timeout(function() {
      $rootScope.scrollTo('hostDetial');
      $rootScope.$broadcast('jumpToHostDetial');
    }, 0);
  });
}

function AlertListController($rootScope, $scope) {
  //getSampleList();
  $scope.showHostDetail = false;
  $scope.hostSample = {};
  $scope.alertMessage = "";
  $scope.successMessage = "Alert updated successfully!";
  $scope.validation = true;
  $('[data-toggle="tooltip"]').tooltip();
  
  $scope.inspectHost = function(sample) {
    $scope.showHostDetail = true;
    
    $scope.hostSample = {
      alertId: sample.alertId,
      name: sample.name,
      comment: sample.comment
    };
    setTimeout(function(){$rootScope.scrollTo('hostDetial');}, 300);
  };
  angular.element(document).ready(function() {
    var dTable = $('#samplesDataTable');
    dTable.DataTable();
  });

  $scope.sampleList = [
    {
    'alertId': 1,
    'name': 'Alert 1',
    'comment': 'Alert for purpose 1',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 2,
    'name': 'Alert 2',
    'comment': 'Alert for purpose 2',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 3,
    'name': 'Alert 3',
    'comment': 'Alert for purpose 3',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 3,
    'name': 'Alert 3',
    'comment': 'Alert for purpose 3',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 4,
    'name': 'Alert 4',
    'comment': 'Alert for purpose 4',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 5,
    'name': 'Alert 5',
    'comment': 'Alert for purpose 5',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 6,
    'name': 'Alert 6',
    'comment': 'Alert for purpose 6',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 7,
    'name': 'Alert 7',
    'comment': 'Alert for purpose 7',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 8,
    'name': 'Alert 8',
    'comment': 'Alert for purpose 8',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 9,
    'name': 'Alert 9',
    'comment': 'Alert for purpose 9',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
      {
    'alertId': 10,
    'name': 'Alert 10',
    'comment': 'Alert for purpose 10',
    'createdOn': '11-07-2018 3:45 PM',
    'updatedOn': '11-07-2018 5:45 PM',
    },
  ];
/*
  $scope.updateSample = function() {
    $scope.validation = validateSubmission();

    if ($scope.validation) {
      // turn read in text file from string to array of string
      var doc = angular.copy($scope.hostSample.infoDocTextarea);
      var docArray = doc.split("\n");
      if (docArray[docArray.length - 1] === "")
        docArray.pop();

      var hsample = angular.copy($scope.hostSample);
      hsample.hostRawInfo = docArray;
      //update api call
      var updated = SampleService.update(
        {hostId:$scope.hostSample.hostId},
        {host:hsample});

      updated.$promise.then(function(res) {
        //refresh sample list to display the updated content
        if (res.error){
          $scope.errorMessage = res.error;
          $("#errorMess").css("display", "block").fadeOut(15000);
        }
        else {
          $scope.hostSample.infoDocTextarea = res.data;
          getSampleList();
          $("#successMess").css("display", "block").fadeOut(5000);
        }
      });
      
      if ($scope.hostSample.infoDoc) {
        delete $scope.hostSample.infoDoc;
      }
      $("#hostInfoInput").val("")
    }
  }

  function validateSubmission() {
    if ($scope.hostSample.os === "" || 
        $scope.hostSample.prim === "" ||
        $scope.hostSample.vendor === "" ||
        $scope.hostSample.infoDocTextarea === "") {
      $scope.alertMessage = "Please add complete information for a sample host!";
      return false;
    }

    if ($scope.hostSample.infoDoc) {
      var fileContent = angular.copy($scope.hostSample.infoDoc);
      fileContent = fileContent.replace(/\n/g, "");
      fileContent = fileContent.replace(/\s/g, "");

      if (fileContent === "") {
        delete $scope.hostSample.infoDoc
        $("#hostInfoInput").val("")
        $scope.alertMessage = "Empty host info document is not allowed!";
        return false;
      }
    }

    return true
  }

  function getSampleList() {
    angular.element(document).ready(function() {  
      $('#samplesDataTable').DataTable().destroy();
    });
    var query = SampleListService.get();

    query.$promise
    .then(function(res){
      $scope.sampleList = res.data;
    })
    .then(function(){
      angular.element(document).ready(function() {  
        var dTable = $('#samplesDataTable');
        dTable.DataTable();  
      });
    })
  }

  $scope.$watch('hostSample.infoDoc', function() {
    if ($scope.hostSample.infoDoc)
      $scope.hostSample.infoDocTextarea = $scope.hostSample.infoDoc;
  });
  */
}

function ManagementController($rootScope, $scope) {
  $scope.hostSample = {os:"", prim:"", vendor:"", infoDocTextarea:"", autoFormat: true, comment: null};
  $scope.alertMessage = "";
  $scope.successMessage = "New sample host added successfully!";
  $scope.triggerSuccessMessage = "Manual trigger FRT cycle has been forced successfully!";
  $scope.validation = true;
  $('[data-toggle="tooltip"]').tooltip();

  //getPrevTrigger();
    
  $scope.triggerCycle = function() {
    $scope.disableUntil = $rootScope.addMinutes(new Date(), 1440);
    $scope.disableTrigger = true;
    $scope.timeIsUp = false;
    $("#triggerSuccessMess").css("display", "block").fadeOut(5000);

    $scope.cycleCompleteMessage = "Manual trigger content scan has been triggered successfully!";
    $('#alertModal').modal('show');

    /*
    var triggered = ManagementService.trigger({user: $rootScope.globals.currentUser.email});
    triggered.$promise.then(function(res){
      if (res.success) {
        $scope.cycleCompleteMessage = "Manual trigger FRT cycle has been completed successfully!";
        $('#alertModal').modal('show');
      }
      else if (res.error) {
        $scope.cycleErrorMessage = res.error;
        $('#alertModal').modal('show');
      }
    });*/
  };

  $scope.addNewSample = function() {
    $scope.validation = validateSubmission();

    if ($scope.validation) {
      // turn read in text file from string to array of string
      var doc = angular.copy($scope.hostSample.infoDocTextarea);
      var docArray = doc.split("\n");
      if (docArray[docArray.length - 1] === "")
        docArray.pop();

      var hsample = angular.copy($scope.hostSample);
      hsample.hostRawInfo = docArray;
      //add api call
      var added = SampleService.save({host:hsample});

      added.$promise.then(function(res) {
        //refresh sample list to display the updated content
        if (res.error){
          $scope.errorMessage = res.error;
          $("#errorMess").css("display", "block").fadeOut(15000);
        }
        else if (res.success) {
          $scope.hostSample.os = "";
          $scope.hostSample.prim = "";
          $scope.hostSample.vendor = "";
          $scope.hostSample.infoDocTextarea = "";
          $scope.hostSample.comment = "";
          $("#successMess").css("display", "block").fadeOut(5000);
        }
      });

      if ($scope.hostSample.infoDoc) {
        delete $scope.hostSample.infoDoc;
      }
      $("#hostInfoInput").val("")
    }
  }

  function validateSubmission() {
    if ($scope.hostSample.os === "" || 
        $scope.hostSample.prim === "" ||
        $scope.hostSample.vendor === "" ||
        $scope.hostSample.infoDocTextarea === "") {
      $scope.alertMessage = "Please add complete information for a sample host!";
      return false;
    }

    if ($scope.hostSample.infoDoc) {
      var fileContent = angular.copy($scope.hostSample.infoDoc);
      fileContent = fileContent.replace(/\n/g, "");
      fileContent = fileContent.replace(/\s/g, "");

      if (fileContent === "") {
        delete $scope.hostSample.infoDoc
        $("#hostInfoInput").val("")
        $scope.alertMessage = "Empty host info document is not allowed!";
        return false;
      }
    }

    return true
  }

  function getPrevTrigger() {
    var prevTrigger = ManagementService.get();

    prevTrigger.$promise.then(function(res){
      if (res.not_found) {}
      else if (res.prev_trigger_time) {
        var prevTriTime = new Date(res.prev_trigger_time)
        var disUntil = $rootScope.addMinutes(prevTriTime, 30);

        if (disUntil > new Date()) {
          $scope.disableUntil = angular.copy(disUntil.toString());
          $scope.disableTrigger = true;
          $scope.timeIsUp = false;
        }
        else {
          $scope.disableTrigger = false;
          $scope.timeIsUp = true;
        }
      }
    })
  }

  $scope.$watch('timeIsUp', function() {
    if ($scope.timeIsUp) {
      $scope.disableTrigger = false;
    }
  });

  $scope.$watch('hostSample.infoDoc', function() {
    if ($scope.hostSample.infoDoc)
      $scope.hostSample.infoDocTextarea = $scope.hostSample.infoDoc;
  });
}

function SettingController($rootScope, $scope) {
  $scope.passwordInfo = 
  {
    curPass:"", 
    newPass: "", 
    reNewPass: "",
    email: $rootScope.globals.currentUser.email
  };
  $scope.alertMessage = "";
  $scope.successMessage = "Password changed successfully!";
  $scope.validation = true;

  $scope.changePassword = function() {
    $scope.validation = validateSubmission();

    if ($scope.validation) {
      var updated = UserService.changePasswordResource.update({passwordInfo: $scope.passwordInfo});

      updated.$promise.then(function(res){
        if (res.error) {
          $scope.errorMessage = res.error;
          $("#errorMess").css("display", "block").fadeOut(10000);
        }
        else if (res.success) {
          $scope.passwordInfo = {curPass:"", newPass: "", reNewPass: "", email: $rootScope.globals.currentUser.email};
          $("#successMess").css("display", "block").fadeOut(5000);
        }
      })
    }
  }

  function validateSubmission() {
    if ($scope.passwordInfo.curPass === "" || 
        $scope.passwordInfo.newPass === "" ||
        $scope.passwordInfo.reNewPass === "") {
      $scope.alertMessage = "Please fill out complete information!";
      return false;
    }
    if ($scope.passwordInfo.newPass !== $scope.passwordInfo.reNewPass) {
      $scope.alertMessage = "Re-enter new password does not match new password!";
      return false;
    }

    if ($scope.passwordInfo.newPass === $scope.passwordInfo.curPass) {
      $scope.alertMessage = "New password cannot be the same with current password!";
      return false;
    }
    return true
  }
}
