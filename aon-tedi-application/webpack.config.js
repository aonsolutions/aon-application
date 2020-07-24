
// Import path for resolving file paths
var path = require('path');

const serverConfig = {
  // Specify the entry point for our app.
  entry: [
    path.join(__dirname, 'lib/lambda.js')
  ],
  mode : "development",  target: 'node',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'lib.node.js'
  }
  //…
};

const clientConfig = {
  node: {
    fs: "empty",
    tls: "empty",
    net: "empty",
    child_process: "empty"
  },
  // Specify the entry point for our app.
  entry: [
    path.join(__dirname, 'lib/browser.js')
  ],
  mode : "development",  
  target: 'web', // <=== can be omitted as default is 'web'
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'tedi.js',
    library: 'tedi',
  }
  //…
};

module.exports = [clientConfig]