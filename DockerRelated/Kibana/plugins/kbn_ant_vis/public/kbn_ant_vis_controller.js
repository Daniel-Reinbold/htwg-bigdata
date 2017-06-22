import uiModules from 'ui/modules';

const module = uiModules.get('kibana/kbn_ant_vis', ['kibana']);

import d3 from 'd3';
import _ from 'lodash';
import $ from 'jquery';

module.controller('KbnAntVisController', function ($scope, $element, $rootScope) {
	let svgRoot = $element[0];
	let svg;
	let rects = [];
	
	let _mapRect = function(rect) {
		return {
			y: ((rect.y)*$scope.vis.params.cellSize),
			x: ((rect.x)*$scope.vis.params.cellSize),
		}
	}
	
	let _addRect = function(rect) {
		rect.coordinates = _mapRect(rect);
		rects.push(rect);
	}
	
	let _draw = function() {
		rects.forEach(function(rect){
			svg.append('svg:rect')
					.attr('width', $scope.vis.params.cellSize)
					.attr('height', $scope.vis.params.cellSize)
					.attr('stroke-width', 0)
					.attr('fill-opacity', $scope.vis.params.fillOpacity * rect.count)
					.style('fill', $scope.vis.params.fillColor)
					.attr('x', rect.coordinates.x)
					.attr('y', rect.coordinates.y)
				.append("svg:title").text(rect.title)
					.attr('x', rect.coordinates.x)
					.attr('y', rect.coordinates.y);
		});
	}
	let _buildVis = function() {	
		//adding background layer
		svg.append('svg:rect')
			.attr('x', 0)
			.attr('y', 0)
			.attr('width', $scope.vis.params.cellSize*$scope.vis.params.gridSize)
			.attr('height', $scope.vis.params.cellSize*$scope.vis.params.gridSize)
			.style('fill', $scope.vis.params.bgColor);
	}
	let _initializeVis = function() {
		let div = d3.select(svgRoot);				
		svg = div.append('svg:svg')
					.attr('width', '100%')
					.attr('height', '100%')
				.append('svg:g')
				.call(d3.behavior.zoom().scaleExtent([-2, 8])
					.on('zoom', zoom));		
	}

	let _getCellInfo = function(ant){
		
	}
	function zoom() {
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
			if (svg == null) _initializeVis();
			d3.select(svgRoot).selectAll('rect').remove();
			rects = [];	
			_buildVis();
			let yid = $scope.vis.aggs.bySchemaName['yaxis'][0].id;
			let xid = $scope.vis.aggs.bySchemaName['xaxis'][0].id;
			let movementid = $scope.vis.aggs.bySchemaName['movement'][0].id
			let metricsid = $scope.vis.aggs.bySchemaName['metric'][0].id
			let rootElement = resp.aggregations;
			if (rootElement != null && rootElement.hasOwnProperty(movementid) && rootElement[movementid].hasOwnProperty('buckets')) {
				let buckets = rootElement[movementid].buckets;
				if ($scope.vis.aggs.bySchemaName['metric'][0]._opts.type == 'max'){
					let antsid = $scope.vis.aggs.bySchemaName['ants'][0].id
					if (buckets.hasOwnProperty('movement') && buckets.movement.hasOwnProperty(antsid)) {
						_.map(buckets.movement[antsid].buckets, function (ant) {							
							if (ant.hasOwnProperty(xid) && ant[xid].buckets.length > 0 ) {
								let xBucket = ant[xid].buckets[0];
								if (xBucket.hasOwnProperty(yid) && xBucket[yid].buckets.length > 0) {
									let yBucket = xBucket[yid].buckets[0];
									_addRect({title:ant.key + "",x:parseInt(xBucket.key),y:parseInt(yBucket.key),count:parseInt(yBucket.doc_count)});								
								}
							}						
						});
					}
				} else if ($scope.vis.aggs.bySchemaName['metric'][0]._opts.type == 'count') {
					if ( buckets.hasOwnProperty('movement') && buckets.movement.hasOwnProperty(xid) ) {
							_.map(buckets.movement[xid].buckets, function (xBucket) {
							if (xBucket.hasOwnProperty(yid) && xBucket[yid].hasOwnProperty('buckets') ){
								_.map(xBucket[yid].buckets, function (yBucket){
									_addRect({title:yBucket.doc_count + "",x:parseInt(xBucket.key),y:parseInt(yBucket.key),count:parseInt(yBucket.doc_count)});
								});
							}
						});
					}
				}
			}
			_draw();				
		}
	});
});
