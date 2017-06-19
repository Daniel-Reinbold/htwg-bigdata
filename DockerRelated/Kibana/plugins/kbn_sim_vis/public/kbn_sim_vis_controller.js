import uiModules from 'ui/modules';
import dateMath from '@elastic/datemath';
import moment from 'moment';

const module = uiModules.get('kibana/kbn_sim_vis', ['kibana']);

import $ from 'jquery';

module.controller('KbnSimVisController', function ($scope, $element, $rootScope, Private, $interval) {
	let startMoment = null;
	let antInterval = null;
	let pause = false;
	
	
	let _buildSimulation = function( ) {	
		$rootScope.$$timefilter.refreshInterval.display == "Off" && $scope.stopSimulation( );
		antInterval == null ? $("#playSim").show( ) : $("#playSim").hide( );
		antInterval != null ? $("#pauseSim").show( ) : $("#pauseSim").hide( );		
	}
	
	let _interruptSimulation = function( ){
		if (antInterval != null){			
			$("#playSim").show( );
			$("#pauseSim").hide( );
			clearInterval(antInterval);
			antInterval=null;
			//pause kbn refresh
			$rootScope.$$timefilter.refreshInterval.pause = true;
		}
	}
	
	$scope.mFactor = 5;
	
	$scope.startSimulation = function() {
		if (antInterval == null){
			$("#playSim").hide( );
			$("#pauseSim").show( );
			//refresh interval
			$rootScope.$$timefilter.refreshInterval.value = parseInt($scope.mFactor)*100;
			$rootScope.$$timefilter.refreshInterval.display = parseInt($scope.mFactor)*100 + " ms";
			$rootScope.$$timefilter.refreshInterval.pause = false;
			
			//set new start moment 
			$rootScope.$$timefilter.time.to = dateMath.parse($rootScope.$$timefilter.time.to);
				
			if( startMoment != null && !pause) {
				$rootScope.$$timefilter.time.from = startMoment;
				$rootScope.$$timefilter.time.to = startMoment;
			}	
		 
			//select interval
			antInterval = setInterval(function () {	
				$rootScope.$$timefilter.time.mode = "absolute";
				$rootScope.$$timefilter.time.to = dateMath.parse($rootScope.$$timefilter.time.to).add(parseInt($scope.mFactor)*100,'ms');
				if( $rootScope.$$timefilter.time.to.valueOf() >= moment().valueOf() ){
					$rootScope.$$timefilter.time.mode = "relative";
					$rootScope.$$timefilter.time.to = "now"
					//interrupt Simulation					
					_interruptSimulation( );
					//kbn interval takes new values
					$rootScope.$$timefilter.refreshInterval.pause = false;
					//cancel ant pause
					pause = false;
				}
				console.log("antInterval@work");
			}, $scope.mFactor * 100 );
				
		}
	}
	
	$scope.pauseSimulation = function() {
		_interruptSimulation( );	
		pause = true;	
	}
	$scope.stopSimulation = function() {
		_interruptSimulation( );
		pause = false;
	}


	$scope.$watch('esResponse', function (resp) {	
		if (resp && antInterval == null && resp.hasOwnProperty('aggregations') && resp.aggregations.hasOwnProperty('1') ) {
			_buildSimulation( );
			startMoment = moment(resp.aggregations[1].value);
		}
	});
});
