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
    var userLogin = UserService.loginResource.login({username:$scope.user.email, password:$scope.user.password});

    userLogin.$promise.then(function(res){
      if (res.error) {
        $scope.errorMessage = res.error;
        $("#errorMess").css("display", "block").fadeOut(10000);
      }
      else {
        AuthenticationService.SetCredentials($scope.user.email, res.user);
        if (res.connection.length === 0) {
          $location.path('/link_accounts');
        }
        else {
          AuthenticationService.SetCredentialsLvl2($scope.user.email, res.user.id);
          $location.path('/dashboard');
        }
      }
    });
  }
}

function RegisterController($rootScope, $scope, $location, RegisterService, AuthenticationService) {
  AuthenticationService.ClearCredentials();
  $scope.alertMessage = "";
  $scope.errorMessage = "";
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

function LinkAccountsController($rootScope, $scope, $location, LinkService, AuthenticationService) {
  $scope.socialAccount = {
    facebook: false,
    twitter: false,
    instagram: false
  }

  $scope.linkSocialAccounts = function() {
    var linkSocialAccount = LinkService.linkResource.get();

    linkSocialAccount.$promise.then(function(res) {
      if (res.oauthURL) {
        window.open(res.oauthURL);
      }
    }).catch(function(error){
    })
  }

  setInterval(function(){
    var getSocialAccount = LinkService.getResource.query();

    getSocialAccount.$promise.then(function(res) {
      if (res.length > 0){
        var userInfo = res[0];
        AuthenticationService.SetCredentialsLvl2(userInfo.user.email, userInfo.user.id);
        $location.path('/home/dashboard');
      }
    }).catch(function(error){

    })
  }, 5000);
}

function DashboardController($rootScope, $scope, $q, DashboardService, PolicyService, RuleService, ItemService) {
  //Progress chart
  $('[data-toggle="tooltip"]').tooltip();
  $scope.passCheck = true;

  var query1 = DashboardService.violationResource.query({userId:29});
  var query2 = PolicyService.getResource.query({userId:29});

   $q.all([query1.$promise, query2.$promise])
  .then(function(resList){
    $scope.policyHistory = resList[0];
    $scope.totalPolicy = resList[1];
    $scope.curPolicyStatus = $scope.policyHistory[$scope.policyHistory.length - 1];

    var ruleMap = {};
    var vRulemap = {};
/*
    $scope.totalPolicy.forEach(function(ele){
      var query = RuleService.getRulesResource.query({policyId:ele.id});

      query.$promise.then(function(res){
        res.forEach(function(e){
          ruleMap[e.id] = true;
        });
      }).catch(function(err){})
    })

    var itemList = resList[2];

    itemList.forEach(function(ele){
      var query = ItemService.getItemViolateRulesResource.query({itemId:ele.id});

      query.$promise.then(function(res){
        res.forEach(function(ele){
          vRulemap[ele.rule_id] = true;
        });
      }).catch(function(err){})
    });
*/

    $scope.curStatus = {
      pTotal: $scope.totalPolicy.length,
      pNumOfpass: $scope.totalPolicy.length - $scope.curPolicyStatus.number_policy_violation,
      rTotal: 14,
      rNumOfpass: 10
    };
    var pPassRate = $scope.curStatus.pNumOfpass/$scope.curStatus.pTotal;
    var rPassRate = $scope.curStatus.rNumOfpass/$scope.curStatus.rTotal;
    $scope.curStatus.pPassRate = $rootScope.decimalToPercent(pPassRate, 2);
    $scope.curStatus.rPassRate = $rootScope.decimalToPercent(rPassRate, 2);

    $scope.passCheck = ($scope.curStatus.pNumOfpass ===  $scope.curStatus.pTotal) ? true : false;
  }).catch(function(errList){
  })

  $scope.overall = {
    pPassRate: 92.23,
    rPassRate: 84.10
  };

  $scope.daily = {
    pPassRate: 90.12,
    rPassRate: 86.34
  };

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
  setTimeout(function(){
      if ($('.easy-pie-chart').length > 0) {
        $('.easy-pie-chart').easyPieChart({
          onStep: function(from, to, percent) {
            this.el.children[0].innerHTML = Math.floor(percent*100)/100 + '%';
          },
        });
      }
    }, 300);

  //Trend Chart

    var hisPassRate = [
      [
          1167609600000,
          0.9123
      ],
      [
          1167696000000,
          0.9037
      ],
      [
          1167782400000,
          0.9159
      ],
      [
          1167868800000,
          0.8731
      ],
      [
          1167955200000,
          0.8944
      ],
      [
          1168214400000,
          0.913
      ],
      [
          1168300800000,
          0.9023
      ],
      [
          1168387200000,
          0.8423
      ],
      [
          1168473600000,
          0.842
      ],
      [
          1168560000000,
          0.8564
      ],
      [
          1168819200000,
          0.8843
      ],
      [
          1168905600000,
          0.853
      ],
      [
          1168992000000,
          0.8932
      ],
      [
          1169078400000,
          0.9123
      ],
      [
          1169164800000,
          0.9234
      ],
      [
          1169424000000,
          0.9345
      ],
      [
          1169510400000,
          0.9566
      ],
      [
          1169596800000,
          0.923
      ],
      [
          1169683200000,
          0.932
      ],
      [
    		1169769600000,
    		0.9123
    	],
    	[
    		1170028800000,
    		0.932
    	],
    	[
    		1170115200000,
    		0.9012
    	],
    	[
    		1170201600000,
    		0.9023
    	],
    	[
    		1170288000000,
    		0.9321
    	],
    	[
    		1170374400000,
    		0.9212
    	],
    	[
    		1170633600000,
    		0.9012
    	],
    	[
    		1170720000000,
    		0.8923
    	],
    	[
    		1170806400000,
    		0.9012
    	],
    	[
    		1170892800000,
    		0.9323
    	],
    	[
    		1170979200000,
    		0.9021
    	],
    	[
    		1171238400000,
    		0.9042
    	],
    	[
    		1171324800000,
    		0.932
    	],
    	[
    		1171411200000,
    		0.9231
    	],
    	[
    		1171497600000,
    		0.9021
    	],
    	[
    		1171584000000,
    		0.9123
    	],
    	[
    		1171843200000,
    		0.9321
    	],
    	[
    		1171929600000,
    		0.9092
    	],
    	[
    		1172016000000,
    		0.9126
    	],
    	[
    		1172102400000,
    		0.909
    	],
    	[
    		1172188800000,
    		0.921
    	],
    	[
    		1172448000000,
    		0.9023
    	],
    	[
    		1172534400000,
    		0.8903
    	],
    	[
    		1172620800000,
    		0.8921
    	],
    	[
    		1172707200000,
    		0.9132
    	],
    	[
    		1172793600000,
    		0.8849
    	],
    	[
    		1173052800000,
    		0.9403
    	],
    	[
    		1173139200000,
    		0.9232
    	],
    	[
    		1173225600000,
    		0.9123
    	],
    	[
    		1173312000000,
    		0.9021
    	],
    	[
    		1173398400000,
    		0.9043
    	],
    	[
    		1173657600000,
    		0.9501
    	],
    	[
    		1173744000000,
    		0.8923
    	],
    	[
    		1173830400000,
    		0.921
    	],
    	[
    		1173916800000,
    		0.9103
    	],
    	[
    		1174003200000,
    		0.929
    	],
    	[
    		1174262400000,
    		0.9421
    	],
    	[
    		1174348800000,
    		0.9343
    	],
    	[
    		1174435200000,
    		0.909
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

function PolicyListController($scope, PolicyService) {
  
  var query = PolicyService.getResource.query({userId:29});

  query.$promise.then(function(res) {
    $scope.historicalData = res;
    angular.element(document).ready(function() {
      var dTable = $('#buildsDataTable');
      dTable.DataTable({"order": [[ 0, "desc" ]]});
    });

  });
}

function PolicyDetailController($rootScope, $scope, $timeout, $q, PolicyService, RuleService) {
  $scope.policyId = $rootScope.$stateParams.policyId;

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

  var query1 = PolicyService.getPolicyResource.get({policyId: $scope.policyId}); 
  var query2 = RuleService.getRulesResource.query({policyId: $scope.policyId});

  $q.all([query1.$promise, query2.$promise])
  .then(function(resList){
    $scope.policyInfo = resList[0];
    $scope.ruleList = resList[1];

    $scope.policyData = {
      numOfpass: $scope.ruleLis.length,
      total: $scope.ruleLis.length,
      hostPassRate: 100
    };

    $scope.alertSetting = {
      socialAccount: {
        facebook: true,
        twitter: false,
        instagram: false
      },
      alertMethod: {
        email: $scope.policyInfo.notification_type === 0 ? true : false,
        text: $scope.policyInfo.notification_type === 1 ? true : false,
        phone: false
      }
    }
    
    angular.element(document).ready(function() {
    var dTable = $('#hostsDataTable');
    dTable.DataTable();
  });
  })
  .catch(function(errList){
  });

  $('[data-toggle="tooltip"]').tooltip();
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

function ItemListController($rootScope, $scope, ItemService, AuthorService) {
  $scope.itemList = [];
  var query = ItemService.getItemsResource.query({userId:29});

  query.$promise.then(function(res) {
    $scope.itemList = res;
    $scope.itemList.forEach(function(ele){
      var query1 = AuthorService.get({authorId:ele.author_id});

      query1.$promise.then(function(res1){
        ele.author_name = res1.author_name;
      }).catch(function(err){});
    });

    angular.element(document).ready(function() {
      var dTable = $('#ItemListDataTable');
      dTable.DataTable();
    });
  }).catch(function(err){});
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
