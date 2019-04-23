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

  var query = DashboardService.violationResource.query({userId:$rootScope.globals.currentUser.userId === 151 ? 29 : $rootScope.globals.currentUser.userId});

   query.$promise.then(function(res){
    $scope.scanHistory = res;
    $scope.curPolicyStatus = $scope.scanHistory[$scope.scanHistory.length - 1];

    var ruleMap = {};
    var vRulemap = {};

    $scope.curStatus = {
      pTotal: $scope.curPolicyStatus.total_polices,
      pNumOfpass: $scope.curPolicyStatus.total_polices - $scope.curPolicyStatus.number_policy_violation,
      rTotal: $scope.curPolicyStatus.total_rules,
      rNumOfpass: $scope.curPolicyStatus.total_rules - $scope.curPolicyStatus.number_rules_violation
    };
    var pPassRate = $scope.curStatus.pNumOfpass/$scope.curStatus.pTotal;
    var rPassRate = $scope.curStatus.rNumOfpass/$scope.curStatus.rTotal;
    $scope.curStatus.pPassRate = $rootScope.decimalToPercent(pPassRate, 2);
    $scope.curStatus.rPassRate = $rootScope.decimalToPercent(rPassRate, 2);

    $scope.passCheck = ($scope.curStatus.pNumOfpass ===  $scope.curStatus.pTotal) ? true : false;

    var allRate = getPassRate($scope.scanHistory.length, $scope.scanHistory);
    var dailyRate = getPassRate(5, $scope.scanHistory);

    $scope.overall = {
      pPassRate: allRate[0],
      rPassRate: allRate[1]
    };

    $scope.daily = {
      pPassRate: dailyRate[0],
      rPassRate: dailyRate[1]
    };
  }).then(function(){
    //pie chart
    setTimeout(function(){
      if ($('.easy-pie-chart').length > 0) {
        $('.easy-pie-chart').easyPieChart({
          onStep: function(from, to, percent) {
            this.el.children[0].innerHTML = Math.floor(percent*100)/100 + '%';
          },
        });
      }
    }, 300);
  }).then(function(){
    //trend chart
    var policyPassRate = [];
    var rulePassRate = [];

    $scope.scanHistory.forEach(function(scan){
      var pPassRate = 1-scan.number_policy_violation/scan.total_polices;
      var rPassRate = 1-scan.number_rules_violation/scan.total_rules;
      policyPassRate.push([
        Date.parse(scan.created_at),
        $rootScope.decimalToPercent(pPassRate > 0 ? pPassRate : 0, 2)
      ]);

      rulePassRate.push([
        Date.parse(scan.created_at),
        $rootScope.decimalToPercent(rPassRate > 0 ? rPassRate : 0, 2)
      ]);
    });

    Highcharts.setOptions({
      global: {
        timezone: 'America/Los_Angeles',
        useUTC: false
      }
    });

    Highcharts.chart('policyTrendChart', {
      chart: {
        zoomType: 'x'
      },
      title: {
        text: 'Policy Passing Rate Overall Trend'
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
        data: policyPassRate.sort(function(a, b){return a[0] - b[0]})
      }]
    });

    Highcharts.chart('ruleTrendChart', {
      chart: {
        zoomType: 'x'
      },
      title: {
        text: 'Rule Passing Rate Overall Trend'
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
        data: rulePassRate.sort(function(a, b){return a[0] - b[0]})
      }]
    });
  }).catch(function(errList){
  })

  function getPassRate(counts, historyList) {
    var len = historyList.length - 1;
    var pPassRate = 0;
    var rPassRate = 0;
    for (var i = len; i > len - counts; i--) {
      var pNumOfpass = historyList[i].total_polices - historyList[i].number_policy_violation;
      var rNumOfpass = historyList[i].total_rules - historyList[i].number_rules_violation;
      pPassRate += (pNumOfpass > 0 ? pNumOfpass : 0)/historyList[i].total_polices;
      rPassRate += (rNumOfpass > 0 ? rNumOfpass : 0)/historyList[i].total_rules;
    }

    return [$rootScope.decimalToPercent(pPassRate/counts, 2), $rootScope.decimalToPercent(rPassRate/counts, 2)];
  }
}

function PolicyListController($rootScope, $scope, PolicyService) {
  $scope.showAddNew = false;
  $scope.validation = true;
  $scope.policyName = "";
  $('[data-toggle="tooltip"]').tooltip();
  getPolicyList();
  
  $scope.addNewPolicy = function() {
    $scope.validation = validateSubmission();

    if ($scope.validation){
      //fire api
      var params = {
        user_id: $rootScope.globals.currentUser.userId,
        policy_name: $scope.policyName
      };
      var added = PolicyService.postResource.save({policy:params});

      added.$promise.then(function(res){
        $scope.policyName = "";
        $scope.successMessage = "New policy added successfully!";
        $("#successMess").css("display", "block").fadeOut(5000);
        getPolicyList();
      }).catch(function(err){
        $scope.errorMessage = "Interal Error";
        $("#errorMess").css("display", "block").fadeOut(15000);
      });
    }
  }

  function validateSubmission() {
    if ($scope.policyName === "") {
      $scope.alertMessage = "Please add a policy name!";
      return false;
    }

    return true;
  }

  function getPolicyList() {
    var query = PolicyService.getResource.query({userId:$rootScope.globals.currentUser.userId === 151 ? 29 : $rootScope.globals.currentUser.userId});

    query.$promise.then(function(res) {
      $scope.historicalData = res;
      angular.element(document).ready(function() {
        var dTable = $('#buildsDataTable');
        dTable.DataTable({"order": [[ 0, "desc" ]]});
      });
    });
  }
}

function PolicyDetailController($rootScope, $scope, $timeout, $q, PolicyService, RuleService) {
  $scope.policyId = $rootScope.$stateParams.policyId;
  $scope.showAddNew = false;
  $scope.validation = true;
  $scope.ruleInfo = {
    name: "",
    content: ""
  };
  $('[data-toggle="tooltip"]').tooltip();
  getPolicyDetail();

  $scope.addNewRule = function() {
    $scope.validation = validateSubmission();

    if ($scope.validation){
      //fire api
      var params = {
        policy_id: $scope.policyId,
        rule_name: $scope.ruleInfo.name,
        rule_content: $scope.ruleInfo.content
      };
      var added = RuleService.postResource.save({rule:params});

      added.$promise.then(function(res){
        $scope.ruleInfo = {
          name: "",
          content: ""
        };
        $scope.successMessage = "New rule added successfully!";
        $("#successMess").css("display", "block").fadeOut(5000);
        getPolicyDetail();
      }).catch(function(err){
        $scope.errorMessage = "Interal Error";
        $("#errorMess").css("display", "block").fadeOut(15000);
      });
    }
  }

  function validateSubmission() {
    if ($scope.ruleInfo.name === "" || $scope.ruleInfo.content === "") {
      $scope.alertMessage = "Please complete all required fields!";
      return false;
    }

    return true;
  }

  function getPolicyDetail() {
    var query1 = PolicyService.getPolicyResource.get({policyId: $scope.policyId}); 
    var query2 = RuleService.getRulesResource.query({policyId: $scope.policyId});

    $q.all([query1.$promise, query2.$promise])
    .then(function(resList){
      $scope.policyInfo = resList[0];
      $scope.ruleList = resList[1];

      $scope.policyData = {
        numOfpass: $scope.ruleList.length,
        total: $scope.ruleList.length,
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
  }
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
  var query = ItemService.getItemsResource.query({userId:$rootScope.globals.currentUser.userId === 151 ? 29 : $rootScope.globals.currentUser.userId});

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
      dTable.DataTable({"order": [[ 0, "desc" ]]});
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
