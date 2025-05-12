const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: {
    app     : './src/main/webapp/index.js',                     // Para usar en el BETA
    aio     : './src/main/webapp/aio.js',
    appSass : './src/main/webapp/index.js',                     // Para usar en el NEW
    sass    : './src/main/webapp/assets_sass/styles/main.scss'  // Para usar en el NEW
  },
  output: {
    // Hash solo para appSass.js y sass.css
    filename: (pathData) => {
      return pathData.chunk.name === 'appSass' ? '[name].[contenthash].min.js' : '[name].min.js';
    },
    path: path.resolve(__dirname, 'src/main/webapp/dist'),
    clean: true
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: (pathData) => {
        return pathData.chunk.name === 'sass' ? '[name].[contenthash].min.css' : '[name].min.css';
      }
    }),
    new HtmlWebpackPlugin({
      template: './src/main/webapp/new',
      filename: '../new',
      chunks: ['appSass', 'sass'],  // Solo incluye estos
      inject: 'body'
    })
  ],
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader']
      },
      {
        test: /\.scss$/i,  // Regla para archivos .scss
        use: [
          MiniCssExtractPlugin.loader,  // Extrae el CSS en un archivo separado
          'css-loader',  // Procesa el CSS
          'sass-loader'  // Compila Sass a CSS
        ]
      }
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
      //aonsolutions: path.resolve(__dirname, '../aon-solutions/src/main/webapp/'),
      aonparent: path.resolve(__dirname, 'src/main/webapp/modules/aon-parent.js'),
      aio: path.resolve(__dirname, 'src/main/webapp/')
    }
  }
};
