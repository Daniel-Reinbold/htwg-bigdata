module.exports = function(kibana){
	return new kibana.Plugin({
		name: 'kbn_sim_vis',
		require: ['kibana'],
		uiExports: {
			visTypes: [
				'plugins/kbn_sim_vis/kbn_sim_vis'
				]
			}
	});
};
