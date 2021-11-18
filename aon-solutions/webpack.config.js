const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');

module.exports = {
    entry: {
      app:'./src/main/webapp/index.js',
      documental:'./src/main/webapp/modules/documental/aon-documental.js'
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
      }
};