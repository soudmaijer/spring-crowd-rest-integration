angular.module('sample', ['ngSanitize', 'ui.bootstrap']);

function SampleController($scope, $http) {

    $scope.alerts = [];

    $scope.login = function (loginForm) {
        $http.post('/api/login', $scope.loginForm)
            .success(function (data, status, headers, config) {
                $scope.alerts.push({type: 'success', msg: data});
            }).error(function (data, status, headers, config) {
                $scope.alerts.push({type: 'danger',msg: data});
            });
    }
    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };
}