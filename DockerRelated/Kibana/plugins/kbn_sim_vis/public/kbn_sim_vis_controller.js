import uiModules from 'ui/modules';
import dateMath from '@elastic/datemath';
import moment from 'moment';

const module = uiModules.get('kibana/kbn_sim_vis', ['kibana']);

import $ from 'jquery';

module.controller('KbnSimVisController', function ($scope, $rootScope) {
	let startMoment = null;
	$rootScope.$antInterval = typeof $rootScope.$antInterval == 'undefined' ? null : $rootScope.$antInterval;
	let pause = false;
	
	
	let _buildSimulation = function( ) {	
		$rootScope.$$timefilter.refreshInterval.display == "Off" && $scope.stopSimulation( );
		$rootScope.$antInterval == null ? $("#playSim").show( ) : $("#playSim").hide( );
		$rootScope.$antInterval != null ? $("#pauseSim").show( ) : $("#pauseSim").hide( );		
	}
	
	let _interruptSimulation = function( ){
		if ($rootScope.$antInterval  != null){			
			$("#playSim").show( );
			$("#pauseSim").hide( );
			clearInterval($rootScope.$antInterval );
			$rootScope.$antInterval = null;
			//pause kbn refresh
			$rootScope.$$timefilter.refreshInterval.pause = true;
		}
	}
	
	$scope.mFactor = 5;
	
	$scope.startSimulation = function() {
		if ($rootScope.$antInterval == null){
			$("#playSim").hide( );
			$("#pauseSim").show( );
			//refresh interval
			$rootScope.$$timefilter.refreshInterval.value = parseInt($scope.mFactor)*100;
			$rootScope.$$timefilter.refreshInterval.display = parseInt($scope.mFactor)*100 + " ms";
			$rootScope.$$timefilter.refreshInterval.pause = false;
			
			//set new start moment 
			$rootScope.$$timefilter.time.to = dateMath.parse($rootScope.$$timefilter.time.to);
			
			//new Simulation
			if( startMoment != null && !pause) {
				$rootScope.$$timefilter.time.from = startMoment;
				$rootScope.$$timefilter.time.to = startMoment;
			}	
		 
			//select interval
			$rootScope.$antInterval = setInterval(function () {	
				$("#playSim").hide( );
				$("#pauseSim").show( );				
				$rootScope.$$timefilter.time.mode = "absolute";
				$rootScope.$$timefilter.time.to = dateMath.parse($rootScope.$$timefilter.time.to).add(parseInt($scope.mFactor)*100,'ms');
				// check if to > as now
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

	$scope.$watch($rootScope.$$timefilter.refreshInterval.pause, function ( ) {	
		//pause on kbn ui element -> interrupts simulation
		$rootScope.$$timefilter.refreshInterval.pause == true && _interruptSimulation( );
	});
	$scope.$watch('esResponse', function (resp) {	
		if (resp && $rootScope.$antInterval == null && resp.hasOwnProperty('aggregations') && resp.aggregations.hasOwnProperty('1') ) {
			_buildSimulation( );
			startMoment = moment(resp.aggregations[1].value);
		}
	});
});
