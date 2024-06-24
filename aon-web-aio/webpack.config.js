const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');

module.exports = {
    entry: {
      app:'./src/main/webapp/index.js',
    },
    output: {
        filename: '[name].min.js',
        path: path.resolve(__dirname, 'src/main/webapp/dist')
    },
    plugins: [new MiniCssExtractPlugin({
        filename: '[name].min.css'
    })],
    module: {
      rules: [
        {
          test: /\.css$/i,
          use: [MiniCssExtractPlugin.loader, 'css-loader'],
        },
      ]
    },
    optimization: {
        minimizer: [
          new CssMinimizerPlugin(), '...'
        ]
	},
	resolve: {
    	alias: {
      		aoncss: path.resolve(__dirname, 'src/main/webapp/css/aon.css'),
      		aonsolutions: path.resolve(__dirname, '../aon-solutions/src/main/webapp/'),
          aonparent: path.resolve(__dirname, 'src/main/webapp/modules/aon-parent.js')
    	}
  	}
};
