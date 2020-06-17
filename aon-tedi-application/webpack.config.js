// Import path for resolving file paths
var path = require('path');

module.exports = {
  // Specify the entry point for our app.
  entry: [
    path.join(__dirname, 'lib/lambda.js')
  ],
  mode : "development",
  target: 'node',
  // Specify the output file containing our bundled code
  output: {
    path: __dirname,
    filename: 'index.js',
    libraryTarget: 'commonjs'
  },
  module: {
  }
}