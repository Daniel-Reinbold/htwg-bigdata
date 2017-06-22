module.exports = function(kibana){
	return new kibana.Plugin({
		name: 'kbn_ant_vis',
		require: ['kibana'],
		uiExports: {
			visTypes: [
				'plugins/kbn_ant_vis/kbn_ant_vis'
				]
			}
	});
};
