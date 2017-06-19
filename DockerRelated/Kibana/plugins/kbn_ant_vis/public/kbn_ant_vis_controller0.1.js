import uiModules from 'ui/modules';

const module = uiModules.get('kibana/kbn_ant_vis', ['kibana']);

import d3 from 'd3';
import _ from 'lodash';
import $ from 'jquery';

module.controller('KbnAntVisController', function ($scope, $element, $rootScope, Private, $interval) {

	let svgRoot = $element[0];
	let div;
	let svg;
	let rects = [];
	
	let _mapRect = function(rect) {
		return {
			y: ((rect.y-1)*$scope.vis.params.cellSize),
			x: ((rect.x-1)*$scope.vis.params.cellSize),
		}
	}
	
	let _addRect = function(rect) {
		let cordinates = _mapRect(rect);
		let id = rect.id;
		if(rects[id] == undefined){
			rects[id] = rect
			rects[id].cordinates = cordinates;
		}
	}
	
	let _draw = function( ) {
		for(var i = 0; i<rects.length;i++){
			if(rects[i] != undefined){
				let rect = rects[i];
				if(rect.svg == undefined){
					rect.svg = svg.append("rect")
					.attr("width", $scope.vis.params.cellSize)
					.attr("height", $scope.vis.params.cellSize)
					.attr("id", i)
					.attr("stroke-width", 0)
					.style("fill", "#757575");
				}
				rect.svg
				.attr("x", rect.cordinates.x)
				.attr("y", rect.cordinates.y)
			}
		}
	}
	
	let _buildVis = function( ) {
		
		div = d3.select(svgRoot);
		
		svg = div.append('svg')
			.attr('width', '100%')
			.attr('height', '100%')
			.append('g')
			.call(d3.behavior.zoom().scaleExtent([-18, 18])
				.on('zoom', zoom))
			.append('g');
	
		if (svg != undefined ){
			svg.append("rect")
			.attr("x", 0)
			.attr("y", 0)
			.attr("width", $scope.vis.params.cellSize*$scope.vis.params.gridSize)
			.attr("height", $scope.vis.params.cellSize*$scope.vis.params.gridSize)
			.style("fill", "#F7F7F7");
		}
	}

	function zoom( ) {
		if ($scope.vis.params.enableZoom) {
			if (d3.event !== null) {
				if (d3.event.translate !== undefined && d3.event.scale !== undefined) {
					svg.attr('transform', 'translate(' + d3.event.translate + ')scale(' + d3.event.scale + ')');
				}
			}
		}
	}

	$scope.$watch('esResponse', function (resp) {
		if (resp) {
			let x = 0;
			let y = 0;
			let id = "";
			var scope = $scope;			
			d3.select(svgRoot).selectAll('svg').remove();
			//clear rects :(
			rects = [];
			_buildVis( );
			_.map(resp.aggregations, function (rootElement) {
				if (rootElement !== null) {
					let buckets = rootElement.buckets;
					if (buckets.hasOwnProperty('movement') && buckets.movement.hasOwnProperty('3')) {
						_.map(buckets.movement[3].buckets, function (ant) {
							id = ant.key + "";
							if (ant.hasOwnProperty('4') && ant[4].buckets.length > 0 ) {
								let xBucket = ant[4].buckets[0];						
								x = parseInt(xBucket.key);
								if (xBucket.hasOwnProperty('5') && xBucket[5].buckets.length > 0) {
									let yBucket = xBucket[5].buckets[0];
									y = parseInt(yBucket.key);
									//if (yBucket.hasOwnProperty('1')) {
									//	timestamp = yBucket[1].value;
										//add ants
									_addRect({id:id,x:x,y:y});
									//}									
								}
							}
							
						});
						_draw( );
					}
				}
			});
		}
	});
});
