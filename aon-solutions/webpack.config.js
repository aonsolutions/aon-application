const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    entry: './src/main/webapp/index.js',
    output: {
        filename: 'app.min.js',
        path: path.resolve(__dirname, 'src/main/webapp/dist')
    },
    plugins: [new MiniCssExtractPlugin({
        filename: 'styles.min.css'
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