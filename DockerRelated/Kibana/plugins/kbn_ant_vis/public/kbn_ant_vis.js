import 'ui/agg_table';
import 'ui/agg_table/agg_table_group';

import 'plugins/kbn_ant_vis/kbn_ant_vis.less';
import 'plugins/kbn_ant_vis/kbn_ant_vis_controller';
import TemplateVisTypeTemplateVisTypeProvider from 'ui/template_vis_type/template_vis_type';
import VisSchemasProvider from 'ui/vis/schemas';
import kbnAntVisTemplate from 'plugins/kbn_ant_vis/kbn_ant_vis.html';

require('ui/registry/vis_types').register(KbnAntVisProvider);

function KbnAntVisProvider(Private) {
  const TemplateVisType = Private(TemplateVisTypeTemplateVisTypeProvider);
  const Schemas = Private(VisSchemasProvider);

  return new TemplateVisType({
    name: 'kbn_ant',
    title: 'Ant Visualization',
    icon: 'fa-space-shuttle',
    description: 'Ant Visualization',
    template: require('plugins/kbn_ant_vis/kbn_ant_vis.html'),
    params: {
      defaults: {
        showMetricsAtAllLevels: false,
		cellSize: 5,
		gridSize: 100,
		enableZoom: false
      },
      editor: require('plugins/kbn_ant_vis/kbn_ant_vis_params.html')
    },
    hierarchicalData: function (vis) {
      return Boolean(vis.params.showPartialRows || vis.params.showMetricsAtAllLevels);
    },
    schemas: new Schemas([
      {
        group: 'metrics',
        name: 'metric',
        title: 'Timestamp',
		field: 'timestamp',
        min: 1,
        max: 1,
		aggFilter: ['max']
      },
      {
        group: 'buckets',
        name: 'movement',
		label: 'movement',
        title: 'Movement',
        min: 1,
        max: 1,
        aggFilter: ['filters']
      },
      {
        group: 'buckets',
        name: 'ants',
        title: 'ID',
        min: 1,
        max: 1,
        aggFilter: ['terms']
      },
      {
        group: 'buckets',
        name: 'split',
        title: 'X',
        min: 1,
        max: 1,
        aggFilter: ['terms']
      },
      {
        group: 'buckets',
        name: 'split',
        title: 'Y',
        min: 1,
        max: 1,
        aggFilter: ['terms']
      }
    ]),
    requiresSearch: true
  });
}

export default KbnAntVisProvider;
