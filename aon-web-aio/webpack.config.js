const path                      = require('path');
const MiniCssExtractPlugin      = require('mini-css-extract-plugin');
const CssMinimizerPlugin        = require('css-minimizer-webpack-plugin');
const HtmlWebpackPlugin         = require('html-webpack-plugin');
const CopyWebpackPlugin         = require('copy-webpack-plugin');
const RemoveEmptyScriptsPlugin  = require('webpack-remove-empty-scripts');
const { WebpackManifestPlugin } = require('webpack-manifest-plugin');

module.exports = {
//  devtool: 'source-map',
//  stats: {
//    errorDetails: true
//  },
  entry: {
    // app         : './src/main/webapp/index.js',                       // Para usar en el BETA
    aio         : './src/main/webapp/aio.js',
    appSass     : './src/main/webapp/index.js',                       // Para usar en el NEW
    sass        : './src/main/webapp/assets_sass/styles/main.scss',   // Para usar en el NEW
    sassIframe  : './src/main/webapp/assets_sass/styles/iframe.scss', // Para usar en el NEW - IFRAME
    paturpat    : './src/main/webapp/paturpat.js'
  },
  output: {
    // Hash solo para appSass.js y sass.css
    filename: (pathData) => {
      return pathData.chunk.name === 'appSass' ? '[name].[contenthash].min.js' : '[name].min.js';
    },
    path  : path.resolve(__dirname, 'src/main/webapp/dist'),
    clean : true
  },
  plugins: [
    new RemoveEmptyScriptsPlugin(),
    new MiniCssExtractPlugin({
      filename: (pathData) => {
        return pathData.chunk.name === 'sass' || pathData.chunk.name === 'sassIframe'
          ? '[name].[contenthash].min.css'
          : '[name].min.css';
      }
    }),
    new WebpackManifestPlugin({
      fileName  : 'manifest.json', // Acceder desde JS directamente
      publicPath: ''
    }),
    new HtmlWebpackPlugin({
      template: './src/main/webapp/templates/app',
      filename: '../app',
      chunks  : ['appSass', 'sass'],  // Solo incluye estos
      inject  : false
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: path.resolve(__dirname, 'src/main/webapp/assets_sass/favicons'),
          to: 'favicons'
        },
        {
          from: path.resolve(__dirname, 'src/main/webapp/assets_sass/images/logos'),
          to: 'logos'
        }
      ]
    })
  ],
  module: {
    rules: [
      {
        test: /\.css$/i,
        use : [MiniCssExtractPlugin.loader, 'css-loader']
//        devtool
//        use: [
//          MiniCssExtractPlugin.loader,
//          { loader: 'css-loader', options: { sourceMap: true } }
//        ]
      },
      {
        test: /\.scss$/i,  // Regla para archivos .scss
        use : [
          MiniCssExtractPlugin.loader,  // Extrae el CSS en un archivo separado
          'css-loader',  // Procesa el CSS
          'sass-loader'  // Compila Sass a CSS
        ]
//        devtool
//        use: [
//          MiniCssExtractPlugin.loader,
//          { loader: 'css-loader', options: { sourceMap: true } },
//          { loader: 'sass-loader', options: { sourceMap: true } }
//        ]
      }
    ]
  },
  optimization: {
    minimizer: [
      // new CssMinimizerPlugin(), '...'
    ]
  },
  resolve: {
    alias: {
      // aoncss: path.resolve(__dirname, 'src/main/webapp/css/aon.css'),
      //aonsolutions: path.resolve(__dirname, '../aon-solutions/src/main/webapp/'),
      aonparent: path.resolve(__dirname, 'src/main/webapp/modules/aon-parent.js'),
      aio: path.resolve(__dirname, 'src/main/webapp/')
    }
  }
};
