import 'ui/agg_table';
import 'ui/agg_table/agg_table_group';

import 'plugins/kbn_sim_vis/kbn_sim_vis.less';
import 'plugins/kbn_sim_vis/kbn_sim_vis_controller';
import TemplateVisTypeTemplateVisTypeProvider from 'ui/template_vis_type/template_vis_type';
import VisSchemasProvider from 'ui/vis/schemas';
import kbnSimVisTemplate from 'plugins/kbn_sim_vis/kbn_sim_vis.html';

require('ui/registry/vis_types').register(KbnSimVisProvider);

function KbnSimVisProvider(Private) {
  const TemplateVisType = Private(TemplateVisTypeTemplateVisTypeProvider);
  const Schemas = Private(VisSchemasProvider);

  return new TemplateVisType({
    name: 'kbn_sim',
    title: 'Ant Simulation',
    icon: 'fa-play',
    description: 'Ant Simulation',
    template: require('plugins/kbn_sim_vis/kbn_sim_vis.html'),
    params: {
      defaults: {
        showMetricsAtAllLevels: false,
		cellSize: 5,
		gridSize: 100,
		enableZoom: false
      },
      editor: require('plugins/kbn_sim_vis/kbn_sim_vis_params.html')
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
		aggFilter: ['min']
      }
    ]),
    requiresSearch: true
  });
}

export default KbnSimVisProvider;
